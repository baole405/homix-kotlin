# Android Application for Apartment Management

This application allows residents and property managers to manage apartment-related operations including bills, facility bookings, maintenance requests, and building announcements. It utilizes a remote database accessed through a RESTful API.

Here's a breakdown of the functionalities in mobile app:

## 1. Authentication (Sign Up/Login): 10%

- **User Registration:** Enable users to sign up by creating an account with a full name, phone number, username, and password.
- **Login:** Allow existing users to log in using their registered credentials (email/username and password).
- **Google OAuth:** Support third-party login via Google OAuth 2.0 for a seamless authentication experience.
- **Password Security:** Ensure secure storage of user tokens using AES256-GCM encryption via Android's `EncryptedSharedPreferences` to prevent unauthorized access.
- **Role-Based Access:** Automatically route users to the appropriate interface (Resident or Manager) based on their assigned role after authentication.

## 2. Dashboard & Announcements: 15%

- **Data Fetching:** The app fetches dashboard data (upcoming bills, booking shortcuts, announcements, notifications) from the remote database via a RESTful API.
- **Resident Dashboard:** Display an overview including upcoming bills, quick-access booking shortcuts, recent announcements, and unread notifications.
- **Manager Dashboard:** Display key statistics cards (total apartments, occupied apartments, pending bills, overdue bills, pending bookings, pending complaints) along with pending booking requests and overdue bill alerts.
- **Announcements:** Show building-wide announcements with category (Maintenance, Event, Policy, Emergency, General) and priority (Normal, Important, Urgent) classification. Managers can create, pin, and manage announcements.

## 3. Bill Management: 15%

- **Bill List View:** Show a list of bills including bill title, amount, due date, period, and status (Pending, Paid, Overdue, Cancelled).
- **Sorting and Filtering:** Provide options for users to filter bills by status and view upcoming bills separately.
- **Bill Detail View:** Show all bill details including itemized fee breakdown, apartment information, payment history, and due dates.
- **Payment Integration:** Integrate with a payment gateway to generate payment links for online bill payments.
- **Mark as Paid:** Allow marking bills as paid with payment method selection (Bank Transfer, Cash, E-Wallet, Credit Card), transaction reference, and notes.

## 4. Facility Booking: 15%

- **Facility Overview:** Display available facilities including Parking, BBQ Area, and Swimming Pool with real-time availability status.
- **Booking Creation:** Allow residents to book facilities by selecting service type, date, time slot, and number of participants.
- **My Bookings:** Display a list of personal bookings with status tracking (Pending, Confirmed, Rejected, Cancelled).
- **Booking Management (Manager):** Allow managers to view all booking requests, approve or reject pending bookings, and manage facility slot inventory.
- **Booking Cancellation:** Allow residents to cancel their own bookings from the My Bookings screen.

## 5. Apartment & Resident Management: 10%

- **Apartment Information:** Display apartment details including unit number, floor, block, area, and monthly fee.
- **Resident Profile:** Show detailed resident information including personal details, assigned apartment, registered vehicles, and family members.
- **Customer Management (Manager):** Allow managers to list all residents, view individual customer profiles with apartment assignments.
- **Apartment Management (Manager):** Manage apartment units with occupancy status tracking (Occupied, Vacant, Maintenance).

## 6. Notification System: 15%

- **In-App Notifications:** Display all notifications with title, content, type, and read/unread status.
- **Mark as Read:** Allow users to mark individual notifications as read.
- **Real-Time Updates:** Fetch notifications from the API and display unread counts on the dashboard for immediate awareness.

## 7. Maintenance Requests: 10%

- **Submit Requests:** Allow residents to create maintenance requests with title, description, and location details.
- **Track Status:** Display maintenance request status (Pending, In Progress, Completed) for residents to monitor progress.
- **Request Management (Manager):** Allow managers to view and update maintenance request statuses.

## 8. Transaction History & Reports: 10%

- **Transaction History:** Display payment history organized by month, showing bill references, amounts, payment dates, and methods.
- **Fee Type Management (Manager):** Define and manage billing fee categories including name, unit price, measure unit, and recurrence settings.
- **Reports (Manager):** Provide analytics dashboards with revenue data, booking statistics, and activity summaries by configurable time periods.

---

## Technical Stack

### Frontend (Mobile Application)
- **Language:** Kotlin
- **Platform:** Android (API 26–36)
- **UI Framework:** Jetpack Compose with Material 3
- **Architecture:** MVVM (Model-View-ViewModel)
- **Dependency Injection:** Hilt (v2.51.1)
- **Networking:** Retrofit (v2.11.0) + OkHttp (v4.12.0)
- **Image Loading:** Coil (v2.6.0)
- **Navigation:** Compose Navigation with type-safe routes (kotlinx.serialization)
- **Local Storage:** EncryptedSharedPreferences, DataStore Preferences
- **Build Tool:** Gradle 8.13 (Kotlin DSL)

### Backend
- REST APIs providing endpoints for authentication, bills, bookings, apartments, notifications, statistics, transactions, and fee types.

### Database
- Remote database accessed through RESTful API endpoints.

### Authentication
- OAuth 2.0 with Google OAuth integration and Bearer token authorization.
