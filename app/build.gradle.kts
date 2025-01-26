plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    // Ajout de namespace au bon endroit
    namespace = "com.example.worldovertest"

    // Configuration du SDK
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.worldover"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("com.google.accompanist:accompanist-navigation-animation:0.34.0")
    implementation("androidx.compose.material:material-icons-extended:1.5.3")
    // Core Compose dependencies
    implementation("androidx.compose.ui:ui:1.5.3")
    implementation("androidx.compose.material:material:1.5.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.3")
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.androidx.material3.android)
    implementation(libs.firebase.database.ktx)
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.3")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.4")

    // Image loading with Coil
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("io.coil-kt:coil-svg:2.4.0")

    // Material Components
    implementation("com.google.android.material:material:1.9.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.3")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.3")
    implementation("androidx.navigation:navigation-compose:2.7.1")
    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
}
