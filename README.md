StorageDrawers
==============

A mod adding compartmental storage for Minecraft Forge

For Players
-----------

StorageDrawers is recommended to be downloaded on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/storage-drawers). Make sure you select the newest 1.12 version.

You will also need Chameleon, in order to run StorageDrawers. You can download this from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/chameleon).


For Developers
--------------

#### Building and Developing

StorageDrawers is built using `gradle`. Follow these commands to get a development environment setup:

```
git clone https://github.com/jaquadro/StorageDrawers
cd StorageDrawers
./gradle setupDecompWorkspace
```
You can build the mod with `./gradle build`, whilst in the project folder. We recommend opening up the project in IDEA, and using the gradle window on your right to do this.

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
