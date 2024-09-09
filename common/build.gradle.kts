import com.texelsaurus.Versions

plugins {
    id("java-conv")
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
}

minecraft {
    version(Versions.minecraft)
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.5")
}

configurations {
    register("commonJava") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    register("commonResources") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
}

artifacts {
    add("commonJava", sourceSets.main.get().java.sourceDirectories.singleFile)
    add("commonResources", sourceSets.main.get().resources.sourceDirectories.singleFile)
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}