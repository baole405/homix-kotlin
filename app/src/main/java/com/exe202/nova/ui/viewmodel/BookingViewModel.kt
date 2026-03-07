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
import com.exe202.nova.data.repository.BookingRepository
import com.exe202.nova.data.repository.FacilityRepository
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
    val isLoading: Boolean = true
)

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val facilityRepository: FacilityRepository,
    private val bookingRepository: BookingRepository
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
    }

    fun selectTab(index: Int) = _uiState.update { it.copy(selectedTab = index) }

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
        while (current.plusHours(2) <= close) {
            val next = current.plusHours(2)
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
                        date = state.selectedDate.toString(),
                        startTime = slot.startTime,
                        endTime = slot.endTime,
                        slotNumber = state.pool?.id,
                        slotNumbers = null,
                        endDate = null,
                        notes = state.notes.ifBlank { null },
                        price = state.pool?.pricePerHour?.times(2),
                        numberOfParticipants = state.participants.toIntOrNull()
                    )
                )
                _uiState.update { s -> s.copy(pool = s.pool.copy(isSubmitting = false, submitSuccess = true)) }
            } catch (e: Exception) {
                _uiState.update { s -> s.copy(pool = s.pool.copy(isSubmitting = false, error = e.message)) }
            }
        }
    }

    // --- BBQ ---
    fun expandBbqSlot(id: String?) = _uiState.update { s -> s.copy(bbq = s.bbq.copy(expandedSlotId = id)) }
    fun selectBbqDate(date: LocalDate) = _uiState.update { s -> s.copy(bbq = s.bbq.copy(selectedDate = date)) }
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
                        date = state.selectedDate.toString(),
                        startTime = state.startTime,
                        endTime = state.endTime,
                        slotNumber = slotId,
                        slotNumbers = null,
                        endDate = null,
                        notes = state.notes.ifBlank { null },
                        price = null,
                        numberOfParticipants = state.participants.toIntOrNull()
                    )
                )
                _uiState.update { s -> s.copy(bbq = s.bbq.copy(isSubmitting = false, submitSuccess = true, expandedSlotId = null)) }
            } catch (e: Exception) {
                _uiState.update { s -> s.copy(bbq = s.bbq.copy(isSubmitting = false, error = e.message)) }
            }
        }
    }

    // --- Parking ---
    fun toggleParkingMode(isMonthly: Boolean) = _uiState.update { s -> s.copy(parking = s.parking.copy(isMonthly = isMonthly, selectedSlotIds = emptySet())) }
    fun selectParkingDate(date: LocalDate) = _uiState.update { s -> s.copy(parking = s.parking.copy(selectedDate = date)) }
    fun selectParkingEndDate(date: LocalDate) = _uiState.update { s -> s.copy(parking = s.parking.copy(endDate = date)) }
    fun setParkingVehicleFilter(type: VehicleType?) = _uiState.update { s -> s.copy(parking = s.parking.copy(vehicleFilter = type, selectedSlotIds = emptySet())) }
    fun updateParkingNotes(v: String) = _uiState.update { s -> s.copy(parking = s.parking.copy(notes = v)) }

    fun toggleParkingSlot(id: String) {
        _uiState.update { s ->
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
                bookingRepository.createBooking(
                    CreateBookingRequest(
                        serviceType = "parking",
                        date = state.selectedDate.toString(),
                        startTime = "00:00",
                        endTime = "23:59",
                        slotNumber = null,
                        slotNumbers = state.selectedSlotIds.toList(),
                        endDate = if (state.isMonthly) state.endDate.toString() else null,
                        notes = state.notes.ifBlank { null },
                        price = null,
                        numberOfParticipants = null
                    )
                )
                _uiState.update { s -> s.copy(parking = s.parking.copy(isSubmitting = false, submitSuccess = true, selectedSlotIds = emptySet())) }
            } catch (e: Exception) {
                _uiState.update { s -> s.copy(parking = s.parking.copy(isSubmitting = false, error = e.message)) }
            }
        }
    }
}
