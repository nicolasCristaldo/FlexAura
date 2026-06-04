// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    // Ksp
    alias(libs.plugins.kotlin.ksp) apply false
    // Hilt
    alias(libs.plugins.dagger.hilt.android) apply false
    // Secrets
    alias(libs.plugins.google.secrets) apply false
    // Serialization
    alias(libs.plugins.kotlinx.serialization) apply false
    // Google services
    alias(libs.plugins.google.services) apply false
    // Firebase
    alias(libs.plugins.firebase.crashlytics) apply false
}