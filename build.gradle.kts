import com.texelsaurus.Versions

plugins {
    `java-library`
    // Apply gradle-idea-ext at the root so IntelliJ uses this version
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.2"
}

version = Versions.mod