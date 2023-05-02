StorageDrawers
==============

A mod adding compartmental storage for Minecraft Forge

For Players
-----------

StorageDrawers may be downloaded from any of the following sites:

- [Minecraft Forums](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2198533-storage-drawers-v1-10-7-v3-5-0-v4-0-0-updated-nov)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/storage-drawers)
- [Github Releases](https://github.com/jaquadro/StorageDrawers/releases)

For Developers
--------------

#### Building

StorageDrawers is built using `gradle`. To build StorageDrawers, the support library [Chameleon](https://github.com/jaquadro/Chameleon) must be checked out in an adjacent directory. These commands should be enough to get you started:

```
git clone https://github.com/jaquadro/Chameleon 
git clone https://github.com/jaquadro/StorageDrawers
cd StorageDrawers
./gradle build
```
For development, the `./gradle setupDecompWorkspace` command will setup the Dev Environment you need to develop and contribute to Storage Drawers.

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
