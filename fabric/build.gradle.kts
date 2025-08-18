import com.texelsaurus.Properties
import com.texelsaurus.Versions
import net.darkhax.curseforgegradle.TaskPublishCurseForge
import net.darkhax.curseforgegradle.Constants as CFG_Constants

plugins {
    id("modloader-conv")
    id("fabric-loom") version "1.7-SNAPSHOT"
    id("com.modrinth.minotaur")
}

dependencies {
    minecraft("com.mojang:minecraft:${Versions.minecraft}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${Versions.fabricLoader}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${Versions.fabric}")

    modImplementation("fuzs.forgeconfigapiport:forgeconfigapiport-fabric:21.1.4")

    modCompileOnlyApi("mezz.jei:jei-${Versions.minecraft}-fabric-api:19.21.2.313")
    modRuntimeOnly("mezz.jei:jei-${Versions.minecraft}-fabric:19.21.2.313")

    modImplementation("curse.maven:architectury-api-419699:5786326")
    modImplementation("curse.maven:ftb-library-fabric-438495:6807432")
    modImplementation("curse.maven:ftb-chunks-fabric-472657:6504892")
    modImplementation("curse.maven:ftb-teams-fabric-438497:6119436")
    // modImplementation("curse.maven:configured-457570:5873787")
}

loom {
    accessWidenerPath = file("src/main/resources/storagedrawers.fabric.accesswidener")
    mixin {
        defaultRefmapName.set("${Properties.modid}.refmap.json")
    }
    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("run")
        }
    }
}

tasks.create<TaskPublishCurseForge>("publishCurseForge") {
    dependsOn(tasks.remapJar)

    disableVersionDetection()
    apiToken = System.getenv("CURSEFORGE_API_KEY") ?: "debug_key"

    val mainFile = upload(Properties.curseProjectId, tasks.remapJar.get().archiveFile)
    mainFile.displayName = "${Properties.name}-${Versions.minecraft}-fabric-$version"
    mainFile.changelogType = "markdown"
    mainFile.changelog = File(rootDir, "CHANGELOG.last.md").readText()
    mainFile.releaseType = Properties.distRelease
    Properties.distGameVersions.split(',').forEach { v -> mainFile.addGameVersion(v) }
    mainFile.addModLoader("Fabric")
    mainFile.addRequirement("fabric-api")
    mainFile.addOptional("forge-config-api-port-fabric")
}

modrinth {
    token.set(System.getenv("MODRINTH_API_KEY") ?: "debug_key")
    projectId.set(Properties.modrinthProjectId)
    changelog.set(File(rootDir, "CHANGELOG.last.md").readText())
    versionName.set("${Properties.name}-${Versions.minecraft}-fabric-$version")
    versionNumber.set("${Versions.minecraft}-${Versions.mod}")
    versionType.set(Properties.distRelease)
    gameVersions.set(Properties.distGameVersions.split(','))
    uploadFile.set(tasks.remapJar.get())
    loaders.add("fabric")

    dependencies {
        required.project("fabric-api")
        optional.project("forge-config-api-port")
    }
}
tasks.modrinth.get().dependsOn(tasks.remapJar)