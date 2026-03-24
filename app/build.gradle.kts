// Load .env file from project root
val envFile = rootProject.file(".env")
val envVars = mutableMapOf<String, String>()
if (envFile.exists()) {
    envFile.readLines().forEach { line ->
        val trimmed = line.trim()
        if (trimmed.isNotEmpty() && !trimmed.startsWith("#") && trimmed.contains("=")) {
            val (key, value) = trimmed.split("=", limit = 2)
            envVars[key.trim()] = value.trim()
        }
    }
}

// Load local.properties for machine-local secrets
val localPropertiesFile = rootProject.file("local.properties")
val localVars = mutableMapOf<String, String>()
if (localPropertiesFile.exists()) {
    localPropertiesFile.readLines().forEach { line ->
        val trimmed = line.trim()
        if (trimmed.isNotEmpty() && !trimmed.startsWith("#") && trimmed.contains("=")) {
            val (key, value) = trimmed.split("=", limit = 2)
            localVars[key.trim()] = value.trim()
        }
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)

    //google
    id("com.google.gms.google-services")
}

android {
    namespace = "com.exe202.nova"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.exe202.nova"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Use local server via emulator loopback; .env URL is for production/web
        val apiUrl = envVars["LOCAL_API_URL"] ?: "https://homix-a9d3h.ondigitalocean.app/api"
        buildConfigField("String", "API_BASE_URL", "\"${apiUrl}/\"")
        buildConfigField("String", "GOOGLE_CLIENT_ID", "\"${envVars["GOOGLE_CLIENT_ID"] ?: ""}\"")
        buildConfigField("String", "AUTH_BASE_URL", "\"${envVars["NEXT_PUBLIC_BETTER_AUTH_URL"] ?: "http://10.0.2.2:5000"}\"")
        val mapboxPublicKey = envVars["MAPBOX_PUBLIC_KEY"]
            ?: envVars["MAPBOX_ACCESS_TOKEN"]
            ?: localVars["MAPBOX_PUBLIC_KEY"]
            ?: localVars["MAPBOX_ACCESS_TOKEN"]
            ***REMOVED***
        val googleMapsUrl = envVars["MAP_DEFAULT_GOOGLE_MAPS_URL"]
            ?: "https://maps.google.com/?q=10.762622,106.660172"
        buildConfigField("String", "MAPBOX_PUBLIC_KEY", "\"${mapboxPublicKey}\"")
        buildConfigField("String", "MAP_DEFAULT_GOOGLE_MAPS_URL", "\"${googleMapsUrl}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    // Activity Compose
    implementation(libs.activity.compose)

    // Navigation
    implementation(libs.navigation.compose)

    // Lifecycle
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    // Retrofit + OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Security
    implementation(libs.security.crypto)

    // Image loading
    implementation(libs.coil.compose)

    // Splash Screen
    implementation(libs.splashscreen)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // DataStore
    implementation(libs.datastore.preferences)

    // Browser (Chrome Custom Tabs)
    implementation(libs.browser)

    implementation("com.mapbox.maps:android-ndk27:11.20.1")
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:34.11.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
}
