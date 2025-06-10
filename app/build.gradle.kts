plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose) // Re-added: Kotlin Compose plugin
}

android {
    namespace = "com.example.jewishcalendarwidget"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.jewishcalendarwidget"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
        compose = true // Re-added: Enable Compose compiler features
    }
}

dependencies {
    // Core Android KTX and Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Compose UI dependencies (re-added)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview) // For Compose Previews
    implementation(libs.androidx.material3) // If using Material Design 3 for Compose

    // Standard Android View dependencies (kept)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)

    // Your Zmanim library
    implementation(libs.zmanim)
}

// Test dependencies (ensure this block is separate as before)
dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Compose UI testing dependencies (re-added if needed for Compose tests)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling) // For Compose tooling
    debugImplementation(libs.androidx.ui.test.manifest) // For Compose tooling/testing
}