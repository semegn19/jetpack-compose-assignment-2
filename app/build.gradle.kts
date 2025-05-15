

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id ("kotlin-kapt")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
}

dependencies {

    // AndroidX Core & Lifecycle (from version catalog)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Jetpack Compose (using BOM - Bill of Materials - for consistent versions)
    implementation(platform(libs.androidx.compose.bom)) // Make sure composeBom version in libs.versions.toml is recent
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3) // This should be your primary Material 3
    implementation(libs.androidx.animation) // Version managed by BOM
    implementation(libs.androidx.material.icons.core) // Version managed by BOM
    implementation(libs.androidx.material.icons.extended) // Version managed by BOM

    // Navigation for Compose (update to a recent stable version if 2.9.0 is old)
    implementation(libs.androidx.navigation.compose) // Or latest stable

    // Accompanist (System UI Controller - check for newer versions or alternatives as Accompanist is not actively maintained for all APIs)
    implementation(libs.accompanist.systemuicontroller) // Or latest, consider alternatives if available

    // Networking - Retrofit & Moshi
    implementation(libs.retrofit) // Or latest stable (2.11.0 for example)
    implementation(libs.converter.moshi) // Or latest stable
    implementation(libs.logging.interceptor) // Or latest stable

    // Moshi for JSON Parsing (update to recent stable versions)
    implementation(libs.moshi) // Or latest stable
    implementation(libs.moshi.kotlin) // Or latest stable

    // Kotlin Standard Library & Coroutines (Match your Kotlin compiler version)
    implementation(libs.kotlin.stdlib) // Match your project's Kotlin version
    implementation(libs.kotlinx.coroutines.android) // Or latest stable (e.g., 1.8.1)
    implementation(libs.kotlin.metadata.jvm) // Match your project's Kotlin version

    // Room components (Version 2.7.1 is good for Kotlin 2.0)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt("androidx.room:room-compiler:2.7.1")

    // Hilt (Dependency Injection - use versions from libs.versions.toml for consistency)
    // Ensure 'hilt' version in libs.versions.toml is recent and compatible (e.g., 2.49+, ideally 2.51+)


    // Hilt support for ViewModel (This dependency might be older or not needed if hilt-navigation-compose covers it)
    // implementation ("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03") // Review if this is still needed with modern Hilt setup
    // kapt ("androidx.hilt:hilt-compiler:1.2.0") // This `androidx.hilt:hilt-compiler` is for AndroidX Hilt extensions, not the main Dagger Hilt compiler.
    // The main one is `com.google.dagger:hilt-compiler`

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom)) // For Compose testing libraries
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}