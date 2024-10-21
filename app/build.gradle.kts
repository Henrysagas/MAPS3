plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.maps"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.maps"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx) // Core KTX
    implementation(libs.places) // Google Places API
    implementation(libs.play.services.maps) // Google Maps SDK
    implementation (libs.google.maps.services.v220)
    implementation(libs.android.maps.utils) // Google Maps Utilities (verifica que sea correcto)
    implementation(libs.androidx.lifecycle.runtime.ktx) // Lifecycle KTX
    implementation(libs.androidx.activity.compose) // Activity para Jetpack Compose
    implementation(platform(libs.androidx.compose.bom)) // BOM para Compose
    implementation(libs.androidx.ui) // Jetpack Compose UI
    implementation(libs.androidx.ui.graphics) // Jetpack Compose Graphics
    implementation(libs.androidx.ui.tooling.preview) // Preview para Compose
    implementation(libs.androidx.material3) // Material 3
    implementation(libs.androidx.appcompat) // AppCompat
    implementation(libs.androidx.constraintlayout) // ConstraintLayout


    testImplementation(libs.junit) // Testing con JUnit
    androidTestImplementation(libs.androidx.junit) // Android Testing
    androidTestImplementation(libs.androidx.espresso.core) // Espresso
    androidTestImplementation(platform(libs.androidx.compose.bom)) // BOM para testing de Compose
    androidTestImplementation(libs.androidx.ui.test.junit4) // Testing de UI para Compose
    debugImplementation(libs.androidx.ui.tooling) // Tooling para Compose
    debugImplementation(libs.androidx.ui.test.manifest) // Manifest para pruebas en modo debug

    implementation(libs.slf4j.api) // SLF4J API
    implementation(libs.jul.to.slf4j) // JUL a SLF4J
    runtimeOnly(libs.slf4j.android) // SLF4J para Android
    testRuntimeOnly(libs.slf4j.simple) // SLF4J Simple para testing
}