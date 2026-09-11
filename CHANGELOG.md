[19.1.2]
- Fixed custom item stack amounts not being respected in all places
- Small efficiency improvement in network check (Thanks dberlin)
- Improved efficiency of item insertion to controller (Thanks dberlin)
- NEOFORGE: Fixed balance upgrade not taking effect through item interface
- NEOFORGE: Fixed item interface not working reliably
- FABRIC: Fixed balance upgrade not taking effect through item interface
- FABRIC: Added back WAILA (Jade) support (Thanks dberlin)
- FABRIC: Fixed custom drawer rendering under Sodium (Thanks BONNe)
- FABRIC: Fixed compatibility with Continuity mod (Thanks BONNe)

[19.1.1]
- Restored rendering of fill indicators

[19.1.0]
- Update to Minecraft 1.21.10

[19.0.1]
- FORGE: Fix compatibility with Forge 59.0.2+

[19.0.0]
- Updated to 1.21.9

[18.3.3]
- Added dropMode and dropStackLimit config entries
- FORGE: Let capability checks fall back to parent

[18.3.2]
- Fixed item dupe from integer overflow

[18.3.1]
- Balance upgrade works fully across controller networks
- Fixed additive and maxRange magnet upgrade config values
- Fixed double-click inserting equipped items

[18.3.0]
- Added bamboo as a default compacting rule
- Added config option to show quantify labels by default
- Added Hopper Upgrade
- Added Magnet upgrades
- Added Pause Key
- Fixed empty tag left on empty keyrings
- Added nl_nl translation (Jack McKalling)
- Updated translations from latest crowdin export

[18.2.0]
- Fixed swapping upgrade with stack count > 1 voiding extra items
- Fixed remote upgrade name in ru_ru and es_es (Jack McKalling)
- Improved consistency with claimed/protected chunks (e.g. via FTB Chunks)
- Fixed remote upgrades showing bound description when not bound
- Fixed admin key description indicating it's disabled
- Added FTB variant of Personal Key (but FTB Teams is not here yet)

[18.1.1]
- Fixed meta blockstate loading errors (though they were harmless)
- Fixed model and texture loading errors (also harmless)
- Renamed several list config entries for consistency with 1.20.1, check your values
- Fixed incorrect default ore types in conversion config
- Fixed rendering of framed controller io blocks
- Fixed conversion rules not loading
- Fixed incorrect default ore types in conversion config
- Added option to disable logging startup rules, deny lists, etc.
- Fixed scheduled tick errors in log
- Fixed server startup crash due to loading client class file
- Removed COFH personal key recipe

[18.1.0]
- New config file (storagedrawers-common-v2.toml)
- Reworked storage tiers
- Added copper and netherite storage upgrades
- Partial support for cutout/translucent materials.
- Framed blocks do not occlude light
- Fixed gold_keyrings tag error

WARNING: This release REPLACES the existing storage drawers config file.
The original file will be left unchanged, but it will not be read from.
If you've changed your config, take a look at the new storagedrawers-common-v2.toml
file and make any changes you need.  The options available are not 1:1 with the old config.

[18.0.5]
- Fixed framing table leaving extra component on unframed drawer
- Fixed crash pulling drawer with >99 items
- Fixed crash pulling/creating empty drawer
- Fixed startup crash when Epic Fight or other specific mods are present
- Fixed drawers with remote upgrades not maintaining link when moved by external mods
- Piglins love keys
- Removed space in dist jar
- NEO/FORGE: Fixed upgrade swapping not working when moving to smaller storage upgrades

[18.0.4]
- Fixed shading for item labels
- Fixed detached drawers not showing contents in tooltip
- Possible fix for restoring detached drawer leaving drawer without a label
- Prevent drawers with contents being able to stack if contents identical
- Prevent drawers from being stored in bundles and shulker boxes
- Added config option to prevent storing filled drawers in drawers
- Added config option to blacklist items or namespaces from being stored
- Fixed scheduled tick exceptions showing in logs
- Added admin personal key to unlock any player's drawers (creative/no-recipe)
- NEO/FORGE: Shift+clicking an upgrade on a drawer will store it

[18.0.3]
- Fixed key buttons on Controller IO blocks
- Fixed pick block not selecting matching hotbar framed blocks
- Adjusted framed material heuristic to exclude some additional transparent blocks
- Restyled keyring tooltip to match newer bundles
- Fixed keyring losing custom name when adding keys
- Add short cooldown to keys and keyrings
- Fixed particle icon of framed drawers not using material

[18.0.2]
- Fixed shift+clicking additional blocks into framing table not updating output
- Fixed framing tables not dropping contents when broken
- Limit framed materials to solid opaque blocks (within reasonable heuristic)
- Added restrictFramingMaterials config option to control new material limit
- Fixed wrong framed drawer rendering in GUI item slots
- NEO/FORGE: Fixed items on framing table vanishing at oblique viewing angles

[18.0.1]
- Updated to Minecraft 1.21.8

[18.0.0]
- Updated to Minecraft 1.21.7

[17.0.2]
- Fix regression in Controller IO block name

[17.0.1]
- Fix remote group upgrade language key (contrib by Jack McKalling)
- Fix framing table sometimes not persisting inventory changes on save/quit
- Fix inventory crash after crafting drawer with upgrade
- FABRIC: Fix blocks inserting into 2-tier compacting drawers (contrib by ellellie)
- FABRIC: Fix model compatibility issue with ModernFix (contrib by embeddedt)

[17.0.0]
- Updated to Minecraft 1.21.6

[16.0.0]
- Updated to Minecraft 1.21.5

[15.0.2]
- Fixed possible server hang during chunk saving when remote upgrades in use
- Fixed GUI names for drawers displaying as "Framing Table"
- Fixed framing table consuming contents of drawers (conrib by sharpedmimishee)
- Added Spanish (es_es) translation (contrib by vatusai)
- Added Chinese Simplified (zh_cn) translation (contrib by suoyuki, CTidy, zizunsi)
- FABRIC: Fix void upgrade ignored when interacting with controllers (contrib by Pikachyuu)

[15.0.1]
- Fixed framed item rendering issue
- NEOFORGE: Fixed crash from item rendering issue

[15.0.0]
- Ported to Minecraft 1.21.4
- Added pale oak wood variants

[14.0.4]
- Change debugTrace config item default value back to false
  - If your value was already written as true, you will need to change this yourself
- Remove item logging from block "take" function
- Re-enable compacting tiers config option
- Fix item insert overflow with creative storage upgrade
- Fix pick block not cloning framed blocks correctly
- Fix block names for compacting drawers
- Fix lore descriptions on most items
- Fix keyring recipe losing key ingredient
- Fix enable portability upgrade option being ignored
- FORGE: Fix key buttons rendering black

[14.0.3]
- Fixed framing table voiding framed input if materials already present
- Fixed framing table voiding framed input if materials already present
- Fix drawer block with removed drawers reverting to default drawer when broken and placed agian.
- Fix remote upgrade not respecting controller range
- Framing tables no longer interact with hoppers
- Add upgrade recipes to turn remote upgrades into group versions
- Add zh_tw translation (contrib by Lobster0228)
- Fix framing table voiding unused materials
- Prevent framed trim from being used for retrimming
- Add renaming support for drawers
- Adds back conversion upgrade
  - Whitelists a few ore-based items by default, see config
- Added de_de translation (contrib by Der-Kanzler)
- Fix obsidian storage upgrade recipe

[14.0.2]
- Fix uncommon crash when remote upgrades invalidate (contrib by HugoSandell)
- Fix keyring keeping current selection when it was removed
- Fix keyring losing its name when modified or rotated
- Fix keyring recipe not adding key from recipe
- Fix key buttons not working when on floor or ceiling
- FABRIC: Fix drawers not honoring void upgrade when other inventories try to insert into them

[14.0.1]
- FABRIC: Restore forge config api port support

[14.0.0]
- Updated to Minecraft 1.21.2 / 1.21.3

[13.8.0]
- Added back support for framed drawers
  - Includes support for standard drawers, compacting drawers, trim, controllers, and slaves
- Added back framing table
  - Place any normal supported block on the table to create a framed version
  - Place framed version on table to get back materials and original block
  - Works for drawers that already hold contents
- Fix typo in remote upgrade description
- Fix drawer puller not working when on keyring
- Add several missing recipe advancement entries
- Fixed controller, io, and trims not dropping when broken
- Added ja_jp translation (contrib by sharpedmimishee)
- Added ru_ru translation (contrib by gri3229)

[13.7.4]
- FABRIC: Fix item transactions not working correctly with other mods

[13.7.3]
- FORGE: Fix server crash with latest 52.0.18 Forge release

[13.7.2]
- FABRIC: Made dependency on Forge Config API Port optional
    - If the mod is not installed, config loading will not be supported

[13.7.1]
- Fix crash when using a remote upgrade

[13.7.0]
- FABRIC: Added config support via Forge Config API Port required dependency

[13.6.0]
- Fixed item descriptions not breaking on newlines
- Added priority key and 5 priority levels to drawers
- Added Remote Upgrade to connect drawers to controller remotely
- Added Remote Group Upgrade to connect group of connected drawers to controller remotely
- Added more config entries to disable upgrades

[13.5.2]
- FABRIC: Fixed void upgrade recipe
- FABRIC: Fixed client crash when hovering over keyring or detached drawer items

[13.5.1]
- FABRIC: Fixed drawer key recipe for older Fabric API versions

[13.5.0]
- Added 2-tier variation of compacting drawers
- Added half-depth version of both compacting drawers
- Brought back the personal key
- Fixed compacting drawers not rendering overlays

[13.4.0]
- Fixed major regression from last beta that prevented items from being withdrawn
- Added detached drawers, which can be placed in empty slots in drawer blocks
- Added drawer puller tool to remove drawers from blocks
- Added config entries for detached drawers support
- Added balanced fill upgrade
- Added per-drawer stack capacity to drawer GUI
- Added re-trimming by sneak-using trim on a drawer block
- Added re-partitioning by sneak-using another drawer block on a drawer block if all slots are same item or empty
- Added heavy block option and upgrade (contrib. by loglob)
- Fixed invertShift and invertClick options in server environment

[13.3.1]
- More multi-loader refactoring
- Fixed drawer GUI titles
- FABRIC: Workaround for left-click pulling multiple items

[13.3.0]
- Multi-loader refactor.  Testing is appreciated.

[All Previous]
- All previous changesets can be found through commit history
or file listing on CurseForge