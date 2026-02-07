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
        maven("https://maplibre.org/maplibre-gl-native/android/maven")
    }
}

rootProject.name = "EventBuzz"

include(":app")
include(":core:common")
include(":core:ui")
include(":core:network")
include(":core:database")
include(":core:datastore")
include(":domain")
include(":feature:map")
include(":feature:list")
include(":feature:detail")
include(":feature:search")
include(":feature:auth")
include(":feature:profile")
