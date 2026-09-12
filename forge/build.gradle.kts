import com.texelsaurus.Properties
import com.texelsaurus.Versions
import net.darkhax.curseforgegradle.TaskPublishCurseForge
import net.minecraftforge.gradle.ForgeGradleExtension
import net.minecraftforge.gradle.MinecraftExtensionForProject
import net.minecraftforge.gradle.SlimeLauncherOptions
import org.gradle.api.NamedDomainObjectContainer

plugins {
    id("modloader-conv")
    id("net.minecraftforge.gradle") version ("[7.0.17,8)")
    id("net.minecraftforge.accesstransformers") version ("5.0.3")
    id("com.modrinth.minotaur")
}

val mcExt = extensions.getByName("minecraft") as MinecraftExtensionForProject
val fgExt = extensions.getByName("fg") as ForgeGradleExtension

mcExt.accessTransformer.from(file("src/main/resources/META-INF/accesstransformer.cfg"))

@Suppress("UNCHECKED_CAST")
val forgeRuns = mcExt.javaClass.getMethod("getRuns").apply { isAccessible = true }
    .invoke(mcExt) as NamedDomainObjectContainer<SlimeLauncherOptions>

val mainSourceSet = extensions.getByType(org.gradle.api.tasks.SourceSetContainer::class.java)
    .getByName("main")
forgeRuns.configureEach {
    workingDir.set(layout.projectDirectory.dir("run"))
    mods { create(Properties.modid).source(mainSourceSet) }
}
forgeRuns.register("client")
forgeRuns.register("server") { args("--nogui") }

sourceSets.configureEach {
    val dir = layout.buildDirectory.dir("sourceSets/$name")
    output.setResourcesDir(dir)
    java.destinationDirectory.set(dir)
}

repositories {
    mcExt.mavenizer(this)
    maven(fgExt.forgeMaven)
    maven(fgExt.minecraftLibsMaven)
    mavenCentral()
}

dependencies {
    "implementation"(mcExt.dependency("net.minecraftforge:forge:${Versions.minecraft}-${Versions.forge}").asProvider())
    "annotationProcessor"("net.minecraftforge:eventbus-validator:7.0.6")

    // RIP JEI for Forge
}

tasks.create<TaskPublishCurseForge>("publishCurseForge") {
    dependsOn(tasks.jar)

    disableVersionDetection()
    apiToken = System.getenv("CURSEFORGE_API_KEY") ?: "debug_key"

    val mainFile = upload(Properties.curseProjectId, tasks.jar.get().archiveFile)
    mainFile.displayName = "${Properties.name}-forge-$version"
    mainFile.changelogType = "markdown"
    mainFile.changelog = File(rootDir, "CHANGELOG.last.md").readText()
    mainFile.releaseType = Properties.distRelease
    Properties.distGameVersions.split(',').forEach { v -> mainFile.addGameVersion(v) }
    mainFile.addModLoader("Forge")
    mainFile.addEnvironment("Client", "Server")
}

modrinth {
    token.set(System.getenv("MODRINTH_API_KEY") ?: "debug_key")
    projectId.set(Properties.modrinthProjectId)
    changelog.set(File(rootDir, "CHANGELOG.last.md").readText())
    versionName.set("${Properties.name}-forge-$version")
    versionNumber.set("${Versions.mod}+forge")
    versionType.set(Properties.distRelease)
    gameVersions.set(Properties.distGameVersions.split(',').toList())
    uploadFile.set(tasks.jar.get())
    loaders.add("forge")
}
tasks.modrinth.get().dependsOn(tasks.jar)
