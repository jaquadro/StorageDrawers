StorageDrawers
==============

A mod adding compartmental storage for Minecraft Forge

For Players
-----------

StorageDrawers is recommended to be downloaded on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/storage-drawers). Make sure you select the newest 1.12 version.

You will also need Chameleon, in order to run StorageDrawers. This can also be downloaded from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/chameleon).

There's also a discord community for Texel's mods: https://discord.gg/8WtpQfy

For Developers
--------------

StorageDrawers's buildscripts uses [CleanroomMC's TemplateDevEnv](https://github.com/CleanroomMC/TemplateDevEnv/tree/master) as a base.

### Building and Developing

#### With IntelliJ IDEA
Simply clone this project, and load it in IntelliJ IDEA. Then, select the `setupWorkspace` configuration, and run it. 
Then, refresh your gradle. This will set up a development environment for you.

Once loaded in IntelliJ IDEA, you can use the other preset run configurations to:
- Build Storage Drawers
- Run Dev Client
- Run Dev Server
- Run Dev Obfuscated Client
- Run Dev Obfuscated Server

#### Without IntelliJ IDEA
If you don't have IntelliJ IDEA, simply clone the project, and run `./gradle setupDecompWorkspace` to setup a development environment. 

Then, you can use the following commands:
- `./gradle build` - Builds Storage Drawers
- `./gradle runClient` - Runs a Dev Client
- `./gradle runServer` - Runs a Dev Server
- `./gradle runObfClient` - Runs a Dev Obfuscated Client
- `./gradle runObfServer` - Runs a Dev Obfuscated Server

### Maven

StorageDrawers builds and API are now accessed via [CurseMaven](https://www.cursemaven.com/).  Add the following to your mod's build.gradle:
```
repositories {
    maven {
        name = "Cursemaven"
        url "https://cursemaven.com"
    }
}

dependencies {
    deobfCompile "curse.maven:storage-drawers-223852:<storage_drawers_file-id>"
    deobfCompile "curse.maven:chameleon-230497:<chameleon_file_id>"
}
```
To find the file id, go to your desired version of [Storage Drawers](https://www.curseforge.com/minecraft/mc-mods/storage-drawers) and [Chameleon](https://www.curseforge.com/minecraft/mc-mods/chameleon), and click on the download page of the file. Once you are on the page, the file ID will be at the end of the URL.

The File ID of the newest Storage Drawers version is `2952606`, and the File ID of the newest Chameleon version is `2450900`.

Reporting Bugs
--------------

When reporting bugs, always include the version number of the mod.  If you're reporting a crash, include your client or server log depending on where the crash ocurred.
