plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.foodrush"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.foodrush"
        minSdk = 31
        targetSdk = 36
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
    buildFeatures {
        compose = true
    }

    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }
}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.cloudinary:cloudinary-android:2.1.0")
    implementation(libs.androidx.room3.compiler)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.firebase.storage)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test:rules:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}