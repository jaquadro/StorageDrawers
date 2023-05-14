StorageDrawers
==============

A mod adding compartmental storage for Minecraft Forge

For Players
-----------

StorageDrawers is recommended to be downloaded on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/storage-drawers). Make sure you select the newest 1.12 version.

You will also need Chameleon, in order to run StorageDrawers. This can also be downloaded from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/chameleon).


For Developers
--------------

StorageDrawers's buildscripts use [CleanroomMC's TemplateDevEnv](https://github.com/CleanroomMC/TemplateDevEnv/tree/master) as a base.

#### Building and Developing

These commands should help you setup a development environment:

```
git clone https://github.com/jaquadro/StorageDrawers
cd StorageDrawers
./gradle setupDecompWorkspace
```

Once loaded in IntelliJ, you can use the preset run configurations to:
- Run Client
- Run Server
- Run Obfuscated Client
- Run Obfuscated Server
- Build Jars

Of course, you can do the same in command line. The commands for each, respectively, are:
- `./gradle runClient`
- `./gradle runServer`
- `./gradle runObfClient`
- `./gradle runObfServer`
- `./gradle build`

#### Maven

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
