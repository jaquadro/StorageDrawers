import com.texelsaurus.Properties
import com.texelsaurus.Versions
import net.darkhax.curseforgegradle.Constants
import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("modloader-conv")
    id("net.neoforged.moddev") version ("0.1.74")
    id("com.modrinth.minotaur")
}

neoForge {
    version = Versions.neoForge
//  accessTransformers.add(file('src/main/resources/META-INF/accesstransformer.cfg'))
    runs {
        register("client") {
            client()
        }
        register("server") {
            server()
            programArgument("--nogui")
        }
    }

    mods {
        register(Properties.modid) {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
    // JEI
    runtimeOnly("mezz.jei:jei-1.21.1-neoforge:19.8.5.118")
    // JADE
    implementation("curse.maven:jade-324717:5591256")
}

tasks.create<TaskPublishCurseForge>("publishCurseForge") {
    dependsOn(tasks.jar)

    disableVersionDetection()
    apiToken = System.getenv("CURSEFORGE_API_KEY") ?: "debug_key"

    val mainFile = upload(Properties.curseProjectId, tasks.jar.get().archiveFile)
    mainFile.displayName = "${Properties.name}-${Versions.minecraft}-neoforge-$version"
    mainFile.changelogType = "markdown"
    mainFile.changelog = File(rootDir, "CHANGELOG.last.md").readText()
    mainFile.releaseType = Properties.distRelease
    Properties.distGameVersions.split(',').forEach { v -> mainFile.addGameVersion(v) }
    mainFile.addModLoader("NeoForge")
}

modrinth {
    token.set(System.getenv("MODRINTH_API_KEY") ?: "debug_key")
    projectId.set(Properties.modrinthProjectId)
    changelog.set(File(rootDir, "CHANGELOG.last.md").readText())
    versionName.set("${Properties.name}-${Versions.minecraft}-neoforge-$version")
    versionNumber.set("${Versions.minecraft}-${Versions.mod}")
    versionType.set(Properties.distRelease)
    gameVersions.set(Properties.distGameVersions.split(','))
    uploadFile.set(tasks.jar.get())
    loaders.add("neoforge")
}
tasks.modrinth.get().dependsOn(tasks.jar)
