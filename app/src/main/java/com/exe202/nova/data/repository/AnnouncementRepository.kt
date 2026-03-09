package com.exe202.nova.data.repository

import com.exe202.nova.data.mock.MOCK_ANNOUNCEMENTS
import com.exe202.nova.data.model.Announcement
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnnouncementRepository @Inject constructor() {
    private val announcements = MOCK_ANNOUNCEMENTS.toMutableList()

    suspend fun getAllAnnouncements(): List<Announcement> = announcements.toList()

    suspend fun createAnnouncement(announcement: Announcement): Announcement {
        announcements.add(0, announcement)
        return announcement
    }

    suspend fun updateAnnouncement(announcement: Announcement): Announcement {
        val index = announcements.indexOfFirst { it.id == announcement.id }
        if (index == -1) throw Exception("Announcement not found")
        announcements[index] = announcement
        return announcement
    }

    suspend fun deleteAnnouncement(id: String) {
        announcements.removeAll { it.id == id }
    }
}
