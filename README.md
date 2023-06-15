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

StorageDrawers's buildscript comes from [GTCEu's 1.12.2 Buildscripts.](https://github.com/GregTechCEu/Buildscripts).

### Building and Developing

#### With IntelliJ IDEA
Simply clone this project, using the `Clone In IDEA` button at the top. Then, once background tasks to load gradle have been completed, select the `setupWorkspace` run configuration, and run it. 
Then, refresh your gradle. This will set up a development environment for you.

Once loaded in IntelliJ IDEA, you can use the other preset run configurations to:
- Build Storage Drawers
- Run Dev Client
- Run Dev Server
- Run Dev Obfuscated Client
- Run Dev Obfuscated Server

**Configuring Jabel**

With IntelliJ IDEA, you should also configure your project to give you proper warnings for using Jabel. 

Jabel is a tool which allows features found in higher versions of Java to be used in Java 8 programs, whilst still compiling into Java 8 Bytecode. However, this only supports language features not APIs. 

Thus, you must follow the steps listed [here](https://github.com/GregTechCEu/Buildscripts/blob/master/docs/jabel.md) to give proper warnings on what you can and can't use.

#### Without IntelliJ IDEA
If you don't have IntelliJ IDEA, simply clone the project with `git clone https://github.com/jaquadro/StorageDrawers`, go into the project folder in the command line, with `cd StorageDrawers`, and run `./gradlew setupDecompWorkspace` to setup a development environment. 

Then, you can use the following commands:
- `./gradlew build` - Builds Storage Drawers
- `./gradlew runClient` - Runs a Dev Client
- `./gradlew runServer` - Runs a Dev Server
- `./gradlew runObfClient` - Runs a Dev Obfuscated Client
- `./gradlew runObfServer` - Runs a Dev Obfuscated Server

#### Other useful tips
 - You do not need to worry about what java version you have set as your default. The buildscript will automatically download Zulu's Java 8. Simply do the steps above, and maybe change your editor's JDK to a version of Java 8, and you'll be ready to go!
 - In `gradle.properties`, you can edit the `debug_modid` variables at the bottom. This allows you to include those soft dependencies into your development clients and servers. Make sure to run the `setupWorkpace` run configuration or the commmand `./gradlew setupDecompWorkpace`, and refresh your gradle!

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
