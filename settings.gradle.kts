pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "CineStreamTV"

include(":app-tv")
include(":core:core-domain")
include(":core:core-data")
include(":core:core-common")
include(":extension-engine")
include(":player")
