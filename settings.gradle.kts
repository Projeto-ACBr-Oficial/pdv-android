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

rootProject.name = "acbr-pdv"
include(":app")
include(":domain")
include(":data:common")
include(":data:mock")
include(":printer:sunmi")
include(":acbrlib:bal")
include(":acbrlib:pixcd")
include(":acbrlib:nfe")
include(":acbrlib:pdvtax")
