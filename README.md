StorageDrawers
==============

A mod adding compartmental storage for Minecraft Forge

For Players
-----------

StorageDrawers may be downloaded from any of the following sites:

- [Minecraft Forums](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2198533-storage-drawers-v1-10-7-v3-5-0-v4-0-0-updated-nov)
- [CurseForge.com](https://www.curseforge.com/minecraft/mc-mods/storage-drawers)
- [Github Releases](https://github.com/jaquadro/StorageDrawers/releases)

For Developers
--------------

#### Building

StorageDrawers is built using `gradle`. These commands should be enough to get you started:

```
git clone https://github.com/jaquadro/StorageDrawers
cd StorageDrawers
./gradlew build
```
For development, the `./gradlew idea` command will setup a multi-module project for IntelliJ with StorageDrawers

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
}
```
An example version is `1.12-5.2.2`. You can [browse the repo](https://dl.bintray.com/jaquadro/dev/com/jaquadro/minecraft/) to see what versions are available.

Reporting Bugs
--------------

When reporting bugs, always include the version number of the mod.  If you're reporting a crash, include your client or server log depending on where the crash ocurred.
