package com.exe202.nova.data.mock

import com.exe202.nova.data.model.AnnouncementCategory
import com.exe202.nova.data.model.AnnouncementPriority
import com.exe202.nova.data.model.ApartmentStatus
import com.exe202.nova.data.model.AppRole
import com.exe202.nova.data.model.BBQSlot
import com.exe202.nova.data.model.BillStatus
import com.exe202.nova.data.model.BookingStatus
import com.exe202.nova.data.model.BookingStatsData
import com.exe202.nova.data.model.Customer
import com.exe202.nova.data.model.DashboardStats
import com.exe202.nova.data.model.FeeType
import com.exe202.nova.data.model.ManagerApartment
import com.exe202.nova.data.model.ManagerBill
import com.exe202.nova.data.model.ManagerBooking
import com.exe202.nova.data.model.Announcement
import com.exe202.nova.data.model.ParkingSlot
import com.exe202.nova.data.model.PoolSlot
import com.exe202.nova.data.model.RevenueData
import com.exe202.nova.data.model.ServiceType
import com.exe202.nova.data.model.SlotStatus
import com.exe202.nova.data.model.VehicleType

val MOCK_DASHBOARD_STATS = DashboardStats(
    totalApartments = 12,
    occupiedApartments = 9,
    pendingBills = 4,
    overdueBills = 2,
    pendingBookings = 3,
    pendingComplaints = 2
)

val MOCK_MANAGER_BOOKINGS = listOf(
    ManagerBooking(
        id = 1,
        serviceType = ServiceType.SWIMMING_POOL,
        slotNumber = null,
        date = "2026-03-10",
        endDate = null,
        startTime = "08:00",
        endTime = "10:00",
        status = BookingStatus.PENDING,
        notes = null,
        numberOfParticipants = 3,
        createdAt = "2026-03-08",
        residentName = "Nguyễn Văn An",
        apartmentUnit = "A101"
    ),
    ManagerBooking(
        id = 2,
        serviceType = ServiceType.BBQ,
        slotNumber = "BBQ-1",
        date = "2026-03-11",
        endDate = null,
        startTime = "17:00",
        endTime = "20:00",
        status = BookingStatus.PENDING,
        notes = "Sinh nhật gia đình",
        numberOfParticipants = 10,
        createdAt = "2026-03-08",
        residentName = "Trần Thị Bích",
        apartmentUnit = "A203"
    ),
    ManagerBooking(
        id = 3,
        serviceType = ServiceType.PARKING,
        slotNumber = "A3",
        date = "2026-03-09",
        endDate = "2026-04-09",
        startTime = "00:00",
        endTime = "23:59",
        status = BookingStatus.PENDING,
        notes = null,
        numberOfParticipants = null,
        createdAt = "2026-03-07",
        residentName = "Lê Minh Cường",
        apartmentUnit = "B301"
    ),
    ManagerBooking(
        id = 4,
        serviceType = ServiceType.SWIMMING_POOL,
        slotNumber = null,
        date = "2026-03-08",
        endDate = null,
        startTime = "09:00",
        endTime = "11:00",
        status = BookingStatus.CONFIRMED,
        notes = "Đã xác nhận",
        numberOfParticipants = 2,
        createdAt = "2026-03-06",
        residentName = "Phạm Thị Dung",
        apartmentUnit = "B402"
    ),
    ManagerBooking(
        id = 5,
        serviceType = ServiceType.BBQ,
        slotNumber = "BBQ-3",
        date = "2026-03-07",
        endDate = null,
        startTime = "18:00",
        endTime = "21:00",
        status = BookingStatus.CONFIRMED,
        notes = null,
        numberOfParticipants = 6,
        createdAt = "2026-03-05",
        residentName = "Hoàng Văn Em",
        apartmentUnit = "C501"
    ),
    ManagerBooking(
        id = 6,
        serviceType = ServiceType.PARKING,
        slotNumber = "B2",
        date = "2026-03-05",
        endDate = "2026-04-05",
        startTime = "00:00",
        endTime = "23:59",
        status = BookingStatus.REJECTED,
        notes = "Chỗ đã hết",
        numberOfParticipants = null,
        createdAt = "2026-03-04",
        residentName = "Vũ Thị Phương",
        apartmentUnit = "C602"
    ),
    ManagerBooking(
        id = 7,
        serviceType = ServiceType.SWIMMING_POOL,
        slotNumber = null,
        date = "2026-03-03",
        endDate = null,
        startTime = "14:00",
        endTime = "16:00",
        status = BookingStatus.CANCELLED,
        notes = "Cư dân hủy",
        numberOfParticipants = 1,
        createdAt = "2026-03-01",
        residentName = "Đặng Quốc Giang",
        apartmentUnit = "F04-701"
    )
)

val MOCK_MANAGER_BILLS = listOf(
    ManagerBill(
        id = "B001",
        title = "Tiền điện tháng 2/2026",
        amount = 850000.0,
        dueDate = "2026-03-15",
        period = "02/2026",
        status = BillStatus.PENDING,
        feeType = FeeType.ELECTRICITY,
        apartmentUnit = "A101",
        apartmentBlock = "A",
        residentName = "Nguyễn Văn An"
    ),
    ManagerBill(
        id = "B002",
        title = "Tiền nước tháng 2/2026",
        amount = 210000.0,
        dueDate = "2026-03-15",
        period = "02/2026",
        status = BillStatus.PENDING,
        feeType = FeeType.WATER,
        apartmentUnit = "A203",
        apartmentBlock = "A",
        residentName = "Trần Thị Bích"
    ),
    ManagerBill(
        id = "B003",
        title = "Phí quản lý tháng 2/2026",
        amount = 1200000.0,
        dueDate = "2026-02-28",
        period = "02/2026",
        status = BillStatus.OVERDUE,
        feeType = FeeType.MANAGEMENT,
        apartmentUnit = "B301",
        apartmentBlock = "B",
        residentName = "Lê Minh Cường"
    ),
    ManagerBill(
        id = "B004",
        title = "Tiền điện tháng 2/2026",
        amount = 720000.0,
        dueDate = "2026-02-28",
        period = "02/2026",
        status = BillStatus.OVERDUE,
        feeType = FeeType.ELECTRICITY,
        apartmentUnit = "B402",
        apartmentBlock = "B",
        residentName = "Phạm Thị Dung"
    ),
    ManagerBill(
        id = "B005",
        title = "Phí gửi xe tháng 2/2026",
        amount = 500000.0,
        dueDate = "2026-03-10",
        period = "02/2026",
        status = BillStatus.PENDING,
        feeType = FeeType.PARKING,
        apartmentUnit = "C501",
        apartmentBlock = "C",
        residentName = "Hoàng Văn Em"
    ),
    ManagerBill(
        id = "B006",
        title = "Phí internet tháng 2/2026",
        amount = 180000.0,
        dueDate = "2026-03-10",
        period = "02/2026",
        status = BillStatus.PENDING,
        feeType = FeeType.INTERNET,
        apartmentUnit = "C602",
        apartmentBlock = "C",
        residentName = "Vũ Thị Phương"
    ),
    ManagerBill(
        id = "B007",
        title = "Tiền điện tháng 1/2026",
        amount = 930000.0,
        dueDate = "2026-02-15",
        period = "01/2026",
        status = BillStatus.PAID,
        feeType = FeeType.ELECTRICITY,
        apartmentUnit = "F04-701",
        apartmentBlock = "F04",
        residentName = "Đặng Quốc Giang"
    ),
    ManagerBill(
        id = "B008",
        title = "Tiền nước tháng 1/2026",
        amount = 195000.0,
        dueDate = "2026-02-15",
        period = "01/2026",
        status = BillStatus.PAID,
        feeType = FeeType.WATER,
        apartmentUnit = "A101",
        apartmentBlock = "A",
        residentName = "Nguyễn Văn An"
    ),
    ManagerBill(
        id = "B009",
        title = "Phí dịch vụ tháng 2/2026",
        amount = 350000.0,
        dueDate = "2026-03-15",
        period = "02/2026",
        status = BillStatus.PENDING,
        feeType = FeeType.SERVICE,
        apartmentUnit = "A203",
        apartmentBlock = "A",
        residentName = "Trần Thị Bích"
    ),
    ManagerBill(
        id = "B010",
        title = "Phí quản lý tháng 1/2026",
        amount = 1200000.0,
        dueDate = "2026-01-31",
        period = "01/2026",
        status = BillStatus.PAID,
        feeType = FeeType.MANAGEMENT,
        apartmentUnit = "B301",
        apartmentBlock = "B",
        residentName = "Lê Minh Cường"
    ),
    ManagerBill(
        id = "B011",
        title = "Tiền điện tháng 2/2026",
        amount = 610000.0,
        dueDate = "2026-03-15",
        period = "02/2026",
        status = BillStatus.PENDING,
        feeType = FeeType.ELECTRICITY,
        apartmentUnit = "C501",
        apartmentBlock = "C",
        residentName = "Hoàng Văn Em"
    )
)

val MOCK_CUSTOMERS = listOf(
    Customer(id = "U001", name = "Nguyễn Văn An", email = "an.nguyen@email.com", phone = "0901234567", apartmentUnit = "A101", role = AppRole.RESIDENT),
    Customer(id = "U002", name = "Trần Thị Bích", email = "bich.tran@email.com", phone = "0912345678", apartmentUnit = "A203", role = AppRole.RESIDENT),
    Customer(id = "U003", name = "Lê Minh Cường", email = "cuong.le@email.com", phone = "0923456789", apartmentUnit = "B301", role = AppRole.RESIDENT),
    Customer(id = "U004", name = "Phạm Thị Dung", email = "dung.pham@email.com", phone = "0934567890", apartmentUnit = "B402", role = AppRole.RESIDENT),
    Customer(id = "U005", name = "Hoàng Văn Em", email = "em.hoang@email.com", phone = "0945678901", apartmentUnit = "C501", role = AppRole.RESIDENT),
    Customer(id = "U006", name = "Vũ Thị Phương", email = "phuong.vu@email.com", phone = "0956789012", apartmentUnit = "C602", role = AppRole.RESIDENT),
    Customer(id = "U007", name = "Đặng Quốc Giang", email = "giang.dang@email.com", phone = "0967890123", apartmentUnit = "F04-701", role = AppRole.RESIDENT),
    Customer(id = "U008", name = "Bùi Thị Hoa", email = "hoa.bui@email.com", phone = "0978901234", apartmentUnit = "A102", role = AppRole.RESIDENT),
    Customer(id = "U009", name = "Ngô Văn Ích", email = "ich.ngo@email.com", phone = "0989012345", apartmentUnit = "B303", role = AppRole.RESIDENT)
)

val MOCK_MANAGER_APARTMENTS = listOf(
    ManagerApartment(id = "AP001", unitNumber = "A101", floor = 1, block = "A", area = 65.0, status = ApartmentStatus.OCCUPIED, residentName = "Nguyễn Văn An", residentId = "U001", monthlyFee = 3500000.0),
    ManagerApartment(id = "AP002", unitNumber = "A102", floor = 1, block = "A", area = 65.0, status = ApartmentStatus.OCCUPIED, residentName = "Bùi Thị Hoa", residentId = "U008", monthlyFee = 3500000.0),
    ManagerApartment(id = "AP003", unitNumber = "A203", floor = 2, block = "A", area = 80.0, status = ApartmentStatus.OCCUPIED, residentName = "Trần Thị Bích", residentId = "U002", monthlyFee = 4200000.0),
    ManagerApartment(id = "AP004", unitNumber = "A204", floor = 2, block = "A", area = 80.0, status = ApartmentStatus.VACANT, residentName = null, residentId = null, monthlyFee = 4200000.0),
    ManagerApartment(id = "AP005", unitNumber = "B301", floor = 3, block = "B", area = 72.0, status = ApartmentStatus.OCCUPIED, residentName = "Lê Minh Cường", residentId = "U003", monthlyFee = 3800000.0),
    ManagerApartment(id = "AP006", unitNumber = "B303", floor = 3, block = "B", area = 72.0, status = ApartmentStatus.OCCUPIED, residentName = "Ngô Văn Ích", residentId = "U009", monthlyFee = 3800000.0),
    ManagerApartment(id = "AP007", unitNumber = "B402", floor = 4, block = "B", area = 90.0, status = ApartmentStatus.OCCUPIED, residentName = "Phạm Thị Dung", residentId = "U004", monthlyFee = 4800000.0),
    ManagerApartment(id = "AP008", unitNumber = "B404", floor = 4, block = "B", area = 90.0, status = ApartmentStatus.MAINTENANCE, residentName = null, residentId = null, monthlyFee = 4800000.0),
    ManagerApartment(id = "AP009", unitNumber = "C501", floor = 5, block = "C", area = 55.0, status = ApartmentStatus.OCCUPIED, residentName = "Hoàng Văn Em", residentId = "U005", monthlyFee = 3000000.0),
    ManagerApartment(id = "AP010", unitNumber = "C602", floor = 6, block = "C", area = 55.0, status = ApartmentStatus.OCCUPIED, residentName = "Vũ Thị Phương", residentId = "U006", monthlyFee = 3000000.0),
    ManagerApartment(id = "AP011", unitNumber = "C603", floor = 6, block = "C", area = 55.0, status = ApartmentStatus.VACANT, residentName = null, residentId = null, monthlyFee = 3000000.0),
    ManagerApartment(id = "AP012", unitNumber = "F04-701", floor = 7, block = "F04", area = 120.0, status = ApartmentStatus.OCCUPIED, residentName = "Đặng Quốc Giang", residentId = "U007", monthlyFee = 6500000.0)
)

val MOCK_ANNOUNCEMENTS = listOf(
    Announcement(
        id = "AN001",
        title = "Lịch bảo trì thang máy Block A",
        content = "Thang máy Block A sẽ được bảo trì vào ngày 15/03/2026. Cư dân vui lòng sử dụng thang bộ hoặc thang máy Block B trong thời gian này.",
        author = "Ban Quản Lý",
        category = AnnouncementCategory.MAINTENANCE,
        priority = AnnouncementPriority.IMPORTANT,
        createdAt = "2026-03-08",
        imageUrl = null,
        pinned = true
    ),
    Announcement(
        id = "AN002",
        title = "Hội nghị cư dân quý I/2026",
        content = "Kính mời toàn thể cư dân tham dự hội nghị cư dân quý I/2026 vào lúc 9:00 ngày 20/03/2026 tại hội trường tầng 1.",
        author = "Ban Quản Lý",
        category = AnnouncementCategory.EVENT,
        priority = AnnouncementPriority.NORMAL,
        createdAt = "2026-03-07",
        imageUrl = null,
        pinned = false
    ),
    Announcement(
        id = "AN003",
        title = "Quy định mới về giờ sử dụng hồ bơi",
        content = "Từ ngày 01/04/2026, giờ sử dụng hồ bơi điều chỉnh: 06:00-22:00 (thay vì 08:00-22:00 như trước). Phí giữ nguyên.",
        author = "Ban Quản Lý",
        category = AnnouncementCategory.POLICY,
        priority = AnnouncementPriority.NORMAL,
        createdAt = "2026-03-05",
        imageUrl = null,
        pinned = false
    ),
    Announcement(
        id = "AN004",
        title = "Cảnh báo: Mất điện khẩn cấp",
        content = "Do sự cố kỹ thuật, toàn tòa nhà sẽ mất điện từ 14:00-16:00 hôm nay. Máy phát điện dự phòng sẽ cấp điện cho thang máy và hành lang.",
        author = "Ban Quản Lý",
        category = AnnouncementCategory.EMERGENCY,
        priority = AnnouncementPriority.URGENT,
        createdAt = "2026-03-09",
        imageUrl = null,
        pinned = true
    ),
    Announcement(
        id = "AN005",
        title = "Thông báo nộp phí dịch vụ tháng 3",
        content = "Nhắc nhở cư dân nộp phí dịch vụ tháng 3/2026 trước ngày 15/03/2026 để tránh phát sinh phí trễ hạn.",
        author = "Ban Quản Lý",
        category = AnnouncementCategory.GENERAL,
        priority = AnnouncementPriority.NORMAL,
        createdAt = "2026-03-04",
        imageUrl = null,
        pinned = false
    )
)

val MOCK_PARKING_SLOTS = listOf(
    ParkingSlot(id = "A1", label = "A1", floor = "B1", type = VehicleType.CAR, status = SlotStatus.OCCUPIED, pricePerDay = 50000.0, pricePerMonth = 500000.0),
    ParkingSlot(id = "A2", label = "A2", floor = "B1", type = VehicleType.CAR, status = SlotStatus.OCCUPIED, pricePerDay = 50000.0, pricePerMonth = 500000.0),
    ParkingSlot(id = "A3", label = "A3", floor = "B1", type = VehicleType.CAR, status = SlotStatus.AVAILABLE, pricePerDay = 50000.0, pricePerMonth = 500000.0),
    ParkingSlot(id = "A4", label = "A4", floor = "B1", type = VehicleType.CAR, status = SlotStatus.AVAILABLE, pricePerDay = 50000.0, pricePerMonth = 500000.0),
    ParkingSlot(id = "A5", label = "A5", floor = "B1", type = VehicleType.CAR, status = SlotStatus.MAINTENANCE, pricePerDay = 50000.0, pricePerMonth = 500000.0),
    ParkingSlot(id = "B1", label = "B1", floor = "B1", type = VehicleType.MOTORBIKE, status = SlotStatus.OCCUPIED, pricePerDay = 15000.0, pricePerMonth = 150000.0),
    ParkingSlot(id = "B2", label = "B2", floor = "B1", type = VehicleType.MOTORBIKE, status = SlotStatus.OCCUPIED, pricePerDay = 15000.0, pricePerMonth = 150000.0),
    ParkingSlot(id = "B3", label = "B3", floor = "B1", type = VehicleType.MOTORBIKE, status = SlotStatus.AVAILABLE, pricePerDay = 15000.0, pricePerMonth = 150000.0),
    ParkingSlot(id = "P12", label = "P12", floor = "B2", type = VehicleType.CAR, status = SlotStatus.OCCUPIED, pricePerDay = 50000.0, pricePerMonth = 500000.0),
    ParkingSlot(id = "P05", label = "P05", floor = "B2", type = VehicleType.MOTORBIKE, status = SlotStatus.AVAILABLE, pricePerDay = 15000.0, pricePerMonth = 150000.0)
)

val MOCK_BBQ_SLOTS = listOf(
    BBQSlot(id = "BBQ-1", name = "Khu BBQ Sân Thượng 1", capacity = 15, pricePerHour = 200000.0, status = SlotStatus.AVAILABLE),
    BBQSlot(id = "BBQ-2", name = "Khu BBQ Sân Thượng 2", capacity = 20, pricePerHour = 250000.0, status = SlotStatus.AVAILABLE),
    BBQSlot(id = "BBQ-3", name = "Khu BBQ Sân Vườn A", capacity = 30, pricePerHour = 300000.0, status = SlotStatus.OCCUPIED),
    BBQSlot(id = "BBQ-4", name = "Khu BBQ Sân Vườn B", capacity = 25, pricePerHour = 280000.0, status = SlotStatus.AVAILABLE),
    BBQSlot(id = "BBQ-5", name = "Khu BBQ VIP", capacity = 10, pricePerHour = 500000.0, status = SlotStatus.MAINTENANCE)
)

val MOCK_POOL = PoolSlot(
    id = "POOL-01",
    name = "Hồ bơi Vô cực",
    location = "Sân thượng Block A",
    capacity = 30,
    pricePerHour = 50000.0,
    openTime = "08:00",
    closeTime = "22:00",
    maxDurationHours = 2,
    status = SlotStatus.AVAILABLE
)

val MOCK_REVENUE_DATA = listOf(
    RevenueData(month = "10/2025", amount = 42500000.0),
    RevenueData(month = "11/2025", amount = 45800000.0),
    RevenueData(month = "12/2025", amount = 51200000.0),
    RevenueData(month = "01/2026", amount = 48600000.0),
    RevenueData(month = "02/2026", amount = 46300000.0),
    RevenueData(month = "03/2026", amount = 38900000.0)
)

val MOCK_BOOKING_STATS = listOf(
    BookingStatsData(serviceType = ServiceType.SWIMMING_POOL, count = 24),
    BookingStatsData(serviceType = ServiceType.BBQ, count = 15),
    BookingStatsData(serviceType = ServiceType.PARKING, count = 31)
)
