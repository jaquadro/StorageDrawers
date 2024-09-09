import com.texelsaurus.Properties
import com.texelsaurus.Versions
import net.darkhax.curseforgegradle.TaskPublishCurseForge
import net.darkhax.curseforgegradle.Constants as CFG_Constants

plugins {
    id("modloader-conv")
    id("net.minecraftforge.gradle") version ("[6.0.24,6.2)")
    id("org.spongepowered.mixin") version ("0.7-SNAPSHOT")
    id("com.modrinth.minotaur")
}

/*
mixin {
    config("${Properties.modid}.mixins.json")
}
*/

minecraft {
    mappings("official", Versions.minecraft)
    reobf = false
    runs {
        create("client") {
            taskName("Client")
            workingDirectory(project.file("run"))
            ideaModule("${rootProject.name}.${project.name}.main")
            //args("-mixin.config=${Properties.modid}.mixins.json")
            mods {
                create(Properties.modid) {
                    source(sourceSets.main.get())
                }
            }
        }
    }
}

dependencies {
    "minecraft"("net.minecraftforge:forge:${Versions.minecraft}-${Versions.forge}")
    annotationProcessor("org.spongepowered:mixin:0.8.5-SNAPSHOT:processor")
    implementation("net.sf.jopt-simple:jopt-simple:5.0.4") { version { strictly("5.0.4") } }

    // JEI
    runtimeOnly("mezz.jei:jei-1.21-forge:19.8.2.99")
}

sourceSets.configureEach {
    val dir = layout.buildDirectory.dir("sourcesSets/$this.name")
    this.output.setResourcesDir(dir)
    this.java.destinationDirectory.set(dir)
}

tasks.create<TaskPublishCurseForge>("publishCurseForge") {
    dependsOn(tasks.jar)

    disableVersionDetection()
    apiToken = System.getenv("CURSEFORGE_API_KEY") ?: "debug_key"

    val mainFile = upload(Properties.curseProjectId, tasks.jar.get().archiveFile)
    mainFile.displayName = "${Properties.name}-${Versions.minecraft}-forge-$version"
    mainFile.changelogType = "markdown"
    mainFile.changelog = File(rootDir, "CHANGELOG.last.md").readText()
    mainFile.releaseType = Properties.distRelease
    Properties.distGameVersions.split(',').forEach { v -> mainFile.addGameVersion(v) }
    mainFile.addModLoader("Forge")
}

modrinth {
    token.set(System.getenv("MODRINTH_API_KEY") ?: "debug_key")
    projectId.set(Properties.modrinthProjectId)
    changelog.set(File(rootDir, "CHANGELOG.last.md").readText())
    versionName.set("${Properties.name}-${Versions.minecraft}-forge-$version")
    versionNumber.set("${Versions.minecraft}-${Versions.mod}")
    versionType.set(Properties.distRelease)
    gameVersions.set(Properties.distGameVersions.split(',').toList())
    uploadFile.set(tasks.jar.get())
    loaders.add("forge")
}
tasks.modrinth.get().dependsOn(tasks.jar)