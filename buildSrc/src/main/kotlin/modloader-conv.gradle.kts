import com.texelsaurus.Properties
import com.texelsaurus.Versions
import org.gradle.jvm.tasks.Jar

plugins {
    id("java-conv")
    id("com.modrinth.minotaur")
    id("net.darkhax.curseforgegradle")
}

configurations {
    register("commonJava") {
        isCanBeResolved = true
    }
    register("commonResources") {
        isCanBeResolved = true
    }
}

dependencies {
    compileOnly(project(":common")) {
        capabilities {
            requireCapability("$group:${Properties.modid}")
        }
    }
    "commonJava"(project(path = ":common", configuration = "commonJava"))
    "commonResources"(project(path = ":common", configuration = "commonResources"))
}

tasks {
    named<JavaCompile>("compileJava").configure {
        dependsOn(configurations.getByName("commonJava"))
        source(configurations.getByName("commonJava"))
    }

    named<ProcessResources>("processResources").configure {
        dependsOn(configurations.getByName("commonResources"))
        from(configurations.getByName("commonResources"))
    }

    named<Javadoc>("javadoc").configure {
        dependsOn(configurations.getByName("commonJava"))
        source(configurations.getByName("commonJava"))
    }

    named<Jar>("sourcesJar") {
        dependsOn(configurations.getByName("commonJava"))
        dependsOn(configurations.getByName("commonResources"))
        from(configurations.getByName("commonJava"))
        from(configurations.getByName("commonResources"))
    }

}