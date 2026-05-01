// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google() // Repositorio para las dependencias de Android
        mavenCentral() // Repositorio central de Maven
    }
    dependencies {
        // Clase de servicios de Google para Firebase y otras funcionalidades
        classpath("com.google.gms:google-services:4.4.2") // Para Firebase
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0") // Kotlin Gradle Plugin
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.0") // Para Navigation Safe Args
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50") // Para Hilt
        // Otras dependencias necesarias a nivel de proyecto
    }
}

plugins {
    id("com.android.application") version "8.9.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

// Tarea personalizada de limpieza
tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory.asFile)  // Utiliza el nuevo método recomendado
}