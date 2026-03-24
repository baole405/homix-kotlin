package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.BBQSlot
import com.exe202.nova.data.model.Booking
import com.exe202.nova.data.model.CreateBookingRequest
import com.exe202.nova.data.model.ParkingSlot
import com.exe202.nova.data.model.PoolSlot
import com.exe202.nova.data.model.SlotStatus
import com.exe202.nova.data.model.VehicleType
import com.exe202.nova.data.repository.AppNotificationRepository
import com.exe202.nova.data.repository.BookingRepository
import com.exe202.nova.data.repository.FacilityRepository
import com.exe202.nova.util.SystemNotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class TimeSlot(val startTime: String, val endTime: String, val isBooked: Boolean = false)

data class PoolBookingState(
    val pool: PoolSlot? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val timeSlots: List<TimeSlot> = emptyList(),
    val selectedSlot: TimeSlot? = null,
    val participants: String = "",
    val notes: String = "",
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val error: String? = null
)

data class BbqBookingState(
    val slots: List<BBQSlot> = emptyList(),
    val expandedSlotId: String? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val startTime: String = "08:00",
    val endTime: String = "10:00",
    val participants: String = "",
    val notes: String = "",
    val bookedSlotIds: Set<String> = emptySet(),
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val error: String? = null
)

data class ParkingBookingState(
    val slots: List<ParkingSlot> = emptyList(),
    val isMonthly: Boolean = false,
    val selectedDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now().plusMonths(1),
    val vehicleFilter: VehicleType? = null,
    val selectedSlotIds: Set<String> = emptySet(),
    val bookedSlotIds: Set<String> = emptySet(),
    val notes: String = "",
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val error: String? = null
)

data class BookingUiState(
    val selectedTab: Int = 0,
    val pool: PoolBookingState = PoolBookingState(),
    val bbq: BbqBookingState = BbqBookingState(),
    val parking: ParkingBookingState = ParkingBookingState(),
    val bookedSlots: BookedSlotsState = BookedSlotsState(),
    val navigateToHistory: Boolean = false,
    val isLoading: Boolean = true
)

data class BookedSlotsState(
    val isLoading: Boolean = false,
    val showDialog: Boolean = false,
    val bookings: List<Booking> = emptyList(),
    val selectedDate: String = "",
    val serviceTypeLabel: String = "",
    val error: String? = null
)

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val facilityRepository: FacilityRepository,
    private val bookingRepository: BookingRepository,
    private val appNotificationRepository: AppNotificationRepository,
    private val systemNotificationHelper: SystemNotificationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState

    private val timeFmt = DateTimeFormatter.ofPattern("HH:mm")

    init {
        loadFacilities()
    }

    private fun loadFacilities() {
        val pools = facilityRepository.getPoolSlots()
        val bbq = facilityRepository.getBbqSlots()
        val parking = facilityRepository.getParkingSlots()
        _uiState.update {
            it.copy(
                isLoading = false,
                pool = it.pool.copy(pool = pools.firstOrNull(), timeSlots = generateTimeSlots(pools.firstOrNull())),
                bbq = it.bbq.copy(slots = bbq),
                parking = it.parking.copy(slots = parking)
            )
        }
        refreshBookedSlots()
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
        refreshBookedSlotsForTab(index)
    }

    fun loadBookedSlotsForSelectedTab() {
        val serviceType = serviceTypeForTab(_uiState.value.selectedTab)
        val date = selectedDateForTab(_uiState.value.selectedTab).toString()
        _uiState.update {
            it.copy(
                bookedSlots = it.bookedSlots.copy(
                    isLoading = true,
                    showDialog = true,
                    bookings = emptyList(),
                    selectedDate = date,
                    serviceTypeLabel = serviceTypeLabelForTab(it.selectedTab),
                    error = null
                )
            )
        }

        viewModelScope.launch {
            try {
                val bookings = bookingRepository.getBookingsByDate(date, serviceType)
                _uiState.update {
                    it.copy(bookedSlots = it.bookedSlots.copy(isLoading = false, bookings = bookings))
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(bookedSlots = it.bookedSlots.copy(isLoading = false, error = e.message))
                }
            }
        }
    }

    fun dismissBookedSlotsDialog() {
        _uiState.update { it.copy(bookedSlots = it.bookedSlots.copy(showDialog = false)) }
    }

    fun consumeNavigateToHistory() {
        _uiState.update {
            it.copy(
                navigateToHistory = false,
                pool = it.pool.copy(submitSuccess = false),
                bbq = it.bbq.copy(submitSuccess = false),
                parking = it.parking.copy(submitSuccess = false)
            )
        }
    }

    // --- Pool ---
    fun selectPoolDate(date: LocalDate) {
        _uiState.update { s -> s.copy(pool = s.pool.copy(selectedDate = date, selectedSlot = null)) }
        loadPoolBookings()
    }

    fun selectTimeSlot(slot: TimeSlot) {
        _uiState.update { s -> s.copy(pool = s.pool.copy(selectedSlot = slot)) }
    }

    fun updatePoolParticipants(v: String) = _uiState.update { s -> s.copy(pool = s.pool.copy(participants = v)) }
    fun updatePoolNotes(v: String) = _uiState.update { s -> s.copy(pool = s.pool.copy(notes = v)) }

    private fun loadPoolBookings() {
        val pool = _uiState.value.pool
        val date = pool.selectedDate.toString()
        viewModelScope.launch {
            try {
                val booked = bookingRepository.getBookingsByDate(date, "swimming_pool")
                val slots = generateTimeSlots(pool.pool, booked)
                _uiState.update { s -> s.copy(pool = s.pool.copy(timeSlots = slots)) }
            } catch (_: Exception) { }
        }
    }

    private fun generateTimeSlots(pool: PoolSlot?, bookedBookings: List<Booking> = emptyList()): List<TimeSlot> {
        if (pool == null) return emptyList()
        val open = LocalTime.parse(pool.openTime, timeFmt)
        val close = LocalTime.parse(pool.closeTime, timeFmt)
        val slots = mutableListOf<TimeSlot>()
        var current = open
        while (current.isBefore(close)) {
            val next = current.plusHours(2)
            if (next.isAfter(close) || next.isBefore(current)) break
            val startStr = current.format(timeFmt)
            val endStr = next.format(timeFmt)
            val isBooked = bookedBookings.any { b -> b.startTime == startStr }
            slots.add(TimeSlot(startStr, endStr, isBooked))
            current = next
        }
        return slots
    }

    fun submitPoolBooking() {
        val state = _uiState.value.pool
        val slot = state.selectedSlot ?: return
        viewModelScope.launch {
            _uiState.update { s -> s.copy(pool = s.pool.copy(isSubmitting = true, error = null)) }
            try {
                bookingRepository.createBooking(
                    CreateBookingRequest(
                        serviceType = "swimming_pool",
                        slotNumber = state.pool?.id,
                        date = state.selectedDate.toString(),
                        endDate = null,
                        startTime = slot.startTime,
                        endTime = slot.endTime,
                        notes = state.notes.ifBlank { null }
                    )
                )
                notifyBookingSuccess(
                    key = "booking_success_pool_${state.selectedDate}_${slot.startTime}",
                    title = "Dat cho thanh cong",
                    content = "Ban da dat be boi thanh cong. Hay mo app de xem chi tiet."
                )
                _uiState.update {
                    it.copy(
                        pool = it.pool.copy(isSubmitting = false, submitSuccess = true),
                        navigateToHistory = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { s -> s.copy(pool = s.pool.copy(isSubmitting = false, error = e.message)) }
            }
        }
    }

    // --- BBQ ---
    fun expandBbqSlot(id: String?) = _uiState.update { s -> s.copy(bbq = s.bbq.copy(expandedSlotId = id)) }
    fun selectBbqDate(date: LocalDate) {
        _uiState.update { s -> s.copy(bbq = s.bbq.copy(selectedDate = date)) }
        refreshBookedBbqSlots()
    }
    fun updateBbqStartTime(v: String) = _uiState.update { s -> s.copy(bbq = s.bbq.copy(startTime = v)) }
    fun updateBbqEndTime(v: String) = _uiState.update { s -> s.copy(bbq = s.bbq.copy(endTime = v)) }
    fun updateBbqParticipants(v: String) = _uiState.update { s -> s.copy(bbq = s.bbq.copy(participants = v)) }
    fun updateBbqNotes(v: String) = _uiState.update { s -> s.copy(bbq = s.bbq.copy(notes = v)) }

    fun submitBbqBooking() {
        val state = _uiState.value.bbq
        val slotId = state.expandedSlotId ?: return
        viewModelScope.launch {
            _uiState.update { s -> s.copy(bbq = s.bbq.copy(isSubmitting = true, error = null)) }
            try {
                bookingRepository.createBooking(
                    CreateBookingRequest(
                        serviceType = "bbq",
                        slotNumber = slotId,
                        date = state.selectedDate.toString(),
                        endDate = null,
                        startTime = state.startTime,
                        endTime = state.endTime,
                        notes = state.notes.ifBlank { null }
                    )
                )
                notifyBookingSuccess(
                    key = "booking_success_bbq_${state.selectedDate}_${slotId}_${state.startTime}",
                    title = "Dat cho thanh cong",
                    content = "Ban da dat khu BBQ thanh cong. Hay mo app de xem chi tiet."
                )
                _uiState.update {
                    it.copy(
                        bbq = it.bbq.copy(isSubmitting = false, submitSuccess = true, expandedSlotId = null),
                        navigateToHistory = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { s -> s.copy(bbq = s.bbq.copy(isSubmitting = false, error = e.message)) }
            }
        }
    }

    // --- Parking ---
    fun toggleParkingMode(isMonthly: Boolean) = _uiState.update { s -> s.copy(parking = s.parking.copy(isMonthly = isMonthly, selectedSlotIds = emptySet())) }
    fun selectParkingDate(date: LocalDate) {
        _uiState.update { s -> s.copy(parking = s.parking.copy(selectedDate = date)) }
        refreshBookedParkingSlots()
    }
    fun selectParkingEndDate(date: LocalDate) = _uiState.update { s -> s.copy(parking = s.parking.copy(endDate = date)) }
    fun setParkingVehicleFilter(type: VehicleType?) = _uiState.update { s -> s.copy(parking = s.parking.copy(vehicleFilter = type, selectedSlotIds = emptySet())) }
    fun updateParkingNotes(v: String) = _uiState.update { s -> s.copy(parking = s.parking.copy(notes = v)) }

    fun toggleParkingSlot(id: String) {
        _uiState.update { s ->
            if (id in s.parking.bookedSlotIds) return@update s
            val current = s.parking.selectedSlotIds.toMutableSet()
            if (id in current) current.remove(id) else current.add(id)
            s.copy(parking = s.parking.copy(selectedSlotIds = current))
        }
    }

    fun filteredParkingSlots(): List<ParkingSlot> {
        val state = _uiState.value.parking
        return state.slots.filter { slot ->
            slot.status != SlotStatus.MAINTENANCE &&
                (state.vehicleFilter == null || slot.type == state.vehicleFilter)
        }
    }

    fun submitParkingBooking() {
        val state = _uiState.value.parking
        if (state.selectedSlotIds.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { s -> s.copy(parking = s.parking.copy(isSubmitting = true, error = null)) }
            try {
                state.selectedSlotIds.forEach { slotId ->
                    bookingRepository.createBooking(
                        CreateBookingRequest(
                            serviceType = "parking",
                            slotNumber = slotId,
                            date = state.selectedDate.toString(),
                            endDate = if (state.isMonthly) state.endDate.toString() else null,
                            startTime = "00:00",
                            endTime = "23:59",
                            notes = state.notes.ifBlank { null }
                        )
                    )
                }
                notifyBookingSuccess(
                    key = "booking_success_parking_${state.selectedDate}_${state.selectedSlotIds.joinToString("-")}",
                    title = "Dat cho thanh cong",
                    content = "Ban da dat cho bai xe thanh cong. Hay mo app de xem chi tiet."
                )
                _uiState.update {
                    it.copy(
                        parking = it.parking.copy(
                            isSubmitting = false,
                            submitSuccess = true,
                            selectedSlotIds = emptySet()
                        ),
                        navigateToHistory = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { s -> s.copy(parking = s.parking.copy(isSubmitting = false, error = e.message)) }
            }
        }
    }

    private fun selectedDateForTab(tab: Int): LocalDate = when (tab) {
        0 -> _uiState.value.pool.selectedDate
        1 -> _uiState.value.bbq.selectedDate
        else -> _uiState.value.parking.selectedDate
    }

    private fun serviceTypeForTab(tab: Int): String = when (tab) {
        0 -> "swimming_pool"
        1 -> "bbq"
        else -> "parking"
    }

    private fun serviceTypeLabelForTab(tab: Int): String = when (tab) {
        0 -> "Bể bơi"
        1 -> "BBQ"
        else -> "Bãi xe"
    }

    private fun refreshBookedSlots() {
        refreshBookedBbqSlots()
        refreshBookedParkingSlots()
    }

    private fun refreshBookedSlotsForTab(tab: Int) {
        when (tab) {
            1 -> refreshBookedBbqSlots()
            2 -> refreshBookedParkingSlots()
        }
    }

    private fun refreshBookedBbqSlots() {
        val date = _uiState.value.bbq.selectedDate.toString()
        viewModelScope.launch {
            try {
                val booked = bookingRepository.getBookingsByDate(date, "bbq")
                val bookedIds = booked.mapNotNull { it.slotNumber }.toSet()
                _uiState.update {
                    it.copy(
                        bbq = it.bbq.copy(
                            bookedSlotIds = bookedIds,
                            expandedSlotId = it.bbq.expandedSlotId?.takeUnless { id -> id in bookedIds }
                        )
                    )
                }
            } catch (_: Exception) {
                // Ignore prefetch errors; form submit will still show backend errors.
            }
        }
    }

    private fun refreshBookedParkingSlots() {
        val date = _uiState.value.parking.selectedDate.toString()
        viewModelScope.launch {
            try {
                val booked = bookingRepository.getBookingsByDate(date, "parking")
                val bookedIds = booked.mapNotNull { it.slotNumber }.toSet()
                _uiState.update {
                    it.copy(
                        parking = it.parking.copy(
                            bookedSlotIds = bookedIds,
                            selectedSlotIds = it.parking.selectedSlotIds - bookedIds
                        )
                    )
                }
            } catch (_: Exception) {
                // Ignore prefetch errors; form submit will still show backend errors.
            }
        }
    }

    private fun notifyBookingSuccess(key: String, title: String, content: String) {
        appNotificationRepository.upsert(
            key = key,
            title = title,
            content = content,
            type = "booking"
        )
        systemNotificationHelper.show(
            key = key,
            title = title,
            content = content
        )
    }
}
