pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "FlexAura"
include(":app")
include(":core:designsystem")
include(":core:database")
include(":core:network")
include(":features:auth")
include(":data")
include(":features:home")
include(":features:profile")
include(":features:search")
include(":features:search")
include(":features:post")
include(":features:createpost")
include(":core:components")
