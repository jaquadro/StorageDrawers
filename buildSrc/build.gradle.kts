import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

// Force a Java 25-capable ASM here
configurations.all {
    resolutionStrategy {
        force(
            "org.ow2.asm:asm:9.9.1",
            "org.ow2.asm:asm-commons:9.9.1",
            "org.ow2.asm:asm-tree:9.9.1",
            "org.ow2.asm:asm-analysis:9.9.1",
            "org.ow2.asm:asm-util:9.9.1"
        )
    }
}

dependencies {
    gradleApi()
    implementation(group = "net.darkhax.curseforgegradle", name = "CurseForgeGradle", version = "1.1.26")
    implementation(group = "com.modrinth.minotaur", name = "Minotaur", version = "2.8.+")
}