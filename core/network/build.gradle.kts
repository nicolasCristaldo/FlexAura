import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.google.secrets)
}

android {
    namespace = "com.flexaura.core.network"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        val localProperties = Properties().apply {
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                localPropertiesFile.inputStream().use { load(it) }
            }
        }

        val supabaseUrl = localProperties.getProperty("SUPABASE_URL")
            ?: System.getenv("SUPABASE_URL")
            ?: ""
        val supabaseAnonKey = localProperties.getProperty("SUPABASE_ANON_KEY")
            ?: System.getenv("SUPABASE_ANON_KEY")
            ?: ""

        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)

    // Hilt
    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)

    // Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)

    // Ktor
    implementation("io.ktor:ktor-client-android:3.0.1")
}