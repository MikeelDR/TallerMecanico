pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // Esta línea asegura que se usen los repositorios del settings.gradle.kts
    repositories {
        google()  // Repositorio de Google
        mavenCentral()  // Repositorio de Maven Central
    }
}

rootProject.name = "TallerMecanico"
include(":app")  // Incluye tu módulo 'app'