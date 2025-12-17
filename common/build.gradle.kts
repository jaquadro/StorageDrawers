import com.texelsaurus.Versions
import com.texelsaurus.Properties

plugins {
    id("java-conv")
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
}

minecraft {
    version(Versions.minecraft)
    accessWideners(file("src/main/resources/${Properties.modid}.accesswidener"))
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.5")
    // This is just the eventbus implementation from neoforge, it does not require
    // the rest of neoforge.
    api("net.neoforged:bus:8.0.5")
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