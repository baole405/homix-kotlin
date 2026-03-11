package com.exe202.nova.ui.theme

import androidx.compose.ui.graphics.Color

// shadcn/ui Light Mode Tokens
val LightBackground = Color(0xFFFFFFFF)
val LightForeground = Color(0xFF09090B)
val LightCard = Color(0xFFFFFFFF)
val LightCardBorder = Color(0xFFE4E4E7)
val LightMuted = Color(0xFFF4F4F5)
val LightMutedForeground = Color(0xFF71717A)
val LightPrimary = Color(0xFF18181B)
val LightPrimaryForeground = Color(0xFFFAFAFA)
val LightSecondary = Color(0xFFF4F4F5)
val LightSecondaryForeground = Color(0xFF18181B)
val LightDestructive = Color(0xFFEF4444)
val LightBorder = Color(0xFFE4E4E7)
val LightInput = Color(0xFFE4E4E7)
val LightRing = Color(0xFF18181B)

// shadcn/ui Dark Mode Tokens
val DarkBackground = Color(0xFF09090B)
val DarkForeground = Color(0xFFFAFAFA)
val DarkCard = Color(0xFF09090B)
val DarkCardBorder = Color(0xFF27272A)
val DarkMuted = Color(0xFF27272A)
val DarkMutedForeground = Color(0xFFA1A1AA)
val DarkPrimary = Color(0xFFFAFAFA)
val DarkPrimaryForeground = Color(0xFF18181B)
val DarkSecondary = Color(0xFF27272A)
val DarkSecondaryForeground = Color(0xFFFAFAFA)
val DarkDestructive = Color(0xFFDC2626)
val DarkBorder = Color(0xFF27272A)
val DarkInput = Color(0xFF27272A)
val DarkRing = Color(0xFFD4D4D8)

// Manager Accent
val ManagerAccent = Color(0xFF16A34A)

// Status Colors — Light
val StatusPendingLightBg = Color(0xFFFEF9C3)
val StatusPendingLightText = Color(0xFF854D0E)
val StatusSuccessLightBg = Color(0xFFDCFCE7)
val StatusSuccessLightText = Color(0xFF166534)
val StatusErrorLightBg = Color(0xFFFEE2E2)
val StatusErrorLightText = Color(0xFF991B1B)
val StatusCancelledLightBg = Color(0xFFF4F4F5)
val StatusCancelledLightText = Color(0xFF71717A)

// Status Colors — Dark (15% alpha backgrounds)
val StatusPendingDarkBg = Color(0x26FACC15)
val StatusPendingDarkText = Color(0xFFFACC15)
val StatusSuccessDarkBg = Color(0x2622C55E)
val StatusSuccessDarkText = Color(0xFF22C55E)
val StatusErrorDarkBg = Color(0x26EF4444)
val StatusErrorDarkText = Color(0xFFEF4444)
val StatusCancelledDarkBg = Color(0xFF27272A)
val StatusCancelledDarkText = Color(0xFFA1A1AA)

// Legacy aliases (resolved at call-site via theme-aware helpers)
val StatusPending = Color(0xFFFACC15)
val StatusPaid = Color(0xFF22C55E)
val StatusOverdue = Color(0xFFEF4444)
val StatusCancelled = Color(0xFF71717A)
val StatusConfirmed = Color(0xFF22C55E)
val StatusRejected = Color(0xFFEF4444)
