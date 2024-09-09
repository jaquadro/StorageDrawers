import com.texelsaurus.Properties
import com.texelsaurus.Versions
import org.gradle.jvm.tasks.Jar
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

plugins {
    base
    `java-library`
    idea
    `maven-publish`
}

base.archivesName.set("${Properties.name}-${project.name.lowercase()}-${Versions.minecraft}")
version = Versions.mod
group = Properties.group

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_21.majorVersion))
    withSourcesJar()
    withJavadocJar()
}

@Suppress("UnstableApiUsage")
repositories {
    mavenCentral()
    maven("https://maven.blamejared.com/") {
        name = "BlameJared"
        content {
            includeGroupAndSubgroups("com.blamejared")
            includeGroupAndSubgroups("mezz.jei")
            includeGroupAndSubgroups("com.faux")
            includeGroupAndSubgroups("org.openzen")
        }
    }
    maven("https://repo.spongepowered.org/repository/maven-public/") {
        name = "Sponge"
        content {
            includeGroupAndSubgroups("org.spongepowered")
        }
    }
    maven("https://maven.parchmentmc.org/") {
        name = "ParchmentMC"
        content {
            includeGroupAndSubgroups("org.parchmentmc")
        }
    }
    maven("https://cursemaven.com") {
        name = "CurseForge"
        content {
            includeGroupAndSubgroups("curse.maven")
        }
    }
}

setOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements").forEach { it: String ->
    configurations.getByName(it).outgoing {
        capability("$group:${base.archivesName.get()}:$version")
        capability("$group:${Properties.modid}-${project.name}-${Versions.minecraft}:$version")
        capability("$group:${Properties.modid}:$version")
    }
    publishing.publications {
        if (this is MavenPublication) {
            this.suppressPomMetadataWarningsFor(it)
        }
    }
}

tasks {
    named<JavaCompile>("compileJava").configure {
        options.encoding = StandardCharsets.UTF_8.toString()
        options.release.set(Versions.java.toInt())
    }
    named<Javadoc>("javadoc").configure {
        options {
            encoding = StandardCharsets.UTF_8.toString()
            // Javadoc defines this specifically as StandardJavadocDocletOptions
            // but only has a getter for MinimalJavadocOptions, but let's just make sure to be safe
            if (this is StandardJavadocDocletOptions) {
                addStringOption("Xdoclint:none", "-quiet")
            }
        }
    }
    named<ProcessResources>("processResources").configure {
        val properties = mapOf(
            "minecraft_version" to Versions.minecraft,
            "minecraft_version_range" to Versions.minecraftRange,
            "neo_version" to Versions.neoForge,
            "neo_version_range" to Versions.neoForgeVersionRange,
            "forge_version" to Versions.forge,
            "forge_version_range" to Versions.forgeVersionRange,
            "loader_version_range" to Versions.neoForgeLoaderRange,
            "fabric_version" to Versions.fabric,
            "fabric_loader" to Versions.fabricLoader,
            "java_version" to Versions.java,
            "mod_id" to Properties.modid,
            "mod_name" to Properties.name,
            "mod_license" to Properties.license,
            "mod_version" to Versions.mod,
            "mod_authors" to Properties.author,
            "mod_description" to Properties.description,
        )
        inputs.properties(properties)
        filesMatching(setOf("fabric.mod.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml", "pack.mcmeta")) {
            expand(properties)
        }
    }
    named<Jar>("jar").configure {
        from(project.rootProject.file("LICENSE"))
        manifest {
            attributes["Specification-Title"] = Properties.name
            attributes["Specification-Vendor"] = Properties.author
            attributes["Specification-Version"] = archiveVersion
            attributes["Implementation-Title"] = project.name
            attributes["Implementation-Version"] = archiveVersion
            attributes["Implementation-Vendor"] = Properties.author
            attributes["Implementation-Timestamp"] = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
            attributes["Timestamp"] = System.currentTimeMillis()
            attributes["Built-On-Java"] = "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})"
            attributes["Built-On-Minecraft"] = Versions.minecraft
        }
    }
}

@Suppress("UnstableApiUsage")
configurations {
    val library = register("library")
    val lor = register("localOnlyRuntime")
    getByName("implementation") {
        extendsFrom(library.get())
    }
    getByName("runtimeClasspath").extendsFrom(lor.get())
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components.getByName("java"))
        }
    }
    repositories {
        maven(System.getenv("local_maven_url") ?: "file://${project.projectDir}/repo")
    }
}

idea {
    module {
        excludeDirs.addAll(setOf(project.file("run"), project.file("runs"), project.file("run_server"), project.file("run_client"), project.file("run_game_test")))
    }
}