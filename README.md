StorageDrawers
==============

A mod adding compartmental storage for Minecraft Forge

For Players
-----------

StorageDrawers may be downloaded from any of the following sites:

- [Minecraft Forums](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2198533-storage-drawers-v1-10-7-v3-5-0-v4-0-0-updated-nov)
- [Curse.com](https://mods.curse.com/mc-mods/minecraft/223852-storage-drawers)
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
For development, the `./gradle idea` command will setup a multi-module project for IntelliJ with StorageDrawers and Chameleon

#### Maven

StorageDrawers builds and API are now available on a maven repo.  Add the following to your mod's build.gradle:
```
repositories {
    maven {
        name = "storagedrawers"
        url = "https://dl.bintray.com/jaquadro/dev/"
    }
}

dependencies {
    deobfCompile "com.jaquadro.minecraft.storagedrawers:StorageDrawers:<VERSION>:api"
    runtime "com.jaquadro.minecraft.storagedrawers:StorageDrawers:<VERSION>"
    runtime "com.jaquadro.minecraft.chameleon:Chameleon:<VERSION>"
}
```
You can [browse the repo](https://dl.bintray.com/jaquadro/dev/com/jaquadro/minecraft/) to see what versions are available.

Reporting Bugs
--------------

When reporting bugs, always include the version number of the mod.  If you're reporting a crash, include your client or server log depending on where the crash ocurred.
