import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}


// * DEFINE CONSTANTS
val profileId: String = gradleLocalProperties(rootDir).getProperty("ID_INFO_PROFILE_FIREBASE")
val urlMainPage: String = gradleLocalProperties(rootDir).getProperty("URL_MAIN_PAGE")

val storeFileProperty: String = gradleLocalProperties(rootDir).getProperty("STORE_FILE")
val storePasswordProperty: String = gradleLocalProperties(rootDir).getProperty("STORE_PASSWORD")
val keyAliasProperty: String = gradleLocalProperties(rootDir).getProperty("KEY_ALIAS")
val keyPasswordProperty: String = gradleLocalProperties(rootDir).getProperty("KEY_PASSWORD")


android {

    namespace = "com.nullpointer.nullsiteadmin"
    compileSdk = 34

    signingConfigs {
        create("release") {
            storeFile = file(storeFileProperty)
            storePassword = storePasswordProperty
            keyAlias = keyAliasProperty
            keyPassword = keyPasswordProperty
        }
    }

    defaultConfig {
        applicationId = "com.nullpointer.nullsiteadmin"
        minSdk = 21
        targetSdk = 34
        versionCode = 16
        versionName = "2.1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "ID_INFO_PROFILE_FIREBASE", profileId)
        buildConfigField("String", "URL_MAIN_PAGE", urlMainPage)

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }

        create("pre-release") {
            isDebuggable = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }

        create("profile") {
            isMinifyEnabled = true
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "proguard-rules-profile.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }


    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
                "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
            )
        }
    }

    applicationVariants.all {
        addJavaSourceFoldersToModel(
            File(buildDir, "generated/ksp/$name/kotlin")
        )
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.material:material:1.6.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-service:2.7.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.0")

    // * coil
    implementation("io.coil-kt:coil-compose:2.5.0")

    // *lottie compose
    implementation("com.airbnb.android:lottie-compose:6.3.0")

    // * timber
    implementation("com.orhanobut:logger:2.2.0")
    implementation("com.jakewharton.timber:timber:5.0.1")

    // * hilt
    val daggerHiltVersion = "2.50"
    implementation("com.google.dagger:hilt-android:$daggerHiltVersion")
    kapt("com.google.dagger:hilt-compiler:$daggerHiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    // ? hilt test
    testImplementation("com.google.dagger:hilt-android-testing:$daggerHiltVersion")
    androidTestImplementation("com.google.dagger:hilt-android-testing:$daggerHiltVersion")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:$daggerHiltVersion")

    // * room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    testImplementation("androidx.room:room-testing:$roomVersion")

    // * save state
    implementation("androidx.savedstate:savedstate-ktx:1.2.1")

    // * image compressor
    implementation("com.github.Shouheng88:compressor:1.6.0")

    // * splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // * shimmer effect
    implementation("com.valentinilk.shimmer:compose-shimmer:1.2.0")

    // * navigation
    val destinationsVersion = "1.10.0"
    implementation("io.github.raamcosta.compose-destinations:core:$destinationsVersion")
    ksp("io.github.raamcosta.compose-destinations:ksp:$destinationsVersion")

    // * data store
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // * Firebase
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.1.0"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // * gson
    implementation("com.google.code.gson:gson:2.10.1")


    implementation("androidx.biometric:biometric:1.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    implementation("com.vanniktech:android-image-cropper:4.5.0")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
}

kapt {
    correctErrorTypes = true
}
