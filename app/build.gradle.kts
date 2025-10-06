plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.jewishcalendarwidget"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.jewishcalendarwidget"
        minSdk = 21
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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

    lint {
        // Disable only the targetSdk check since we're building for system partition, not Play Store
        disable += "ExpiredTargetSdkVersion"
        // Still check everything else for quality
        abortOnError = true
    }
}

dependencies {
    // Core Android KTX
    implementation(libs.androidx.core.ktx)

    // Standard Android View dependencies
    implementation(libs.androidx.appcompat)

    // Zmanim library for Hebrew calendar
    implementation(libs.zmanim)

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}