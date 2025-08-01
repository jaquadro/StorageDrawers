[13.8.11]
- Fixed rendering of framed drawer item blocks
- Fixed shift+clicking additional blocks into framing table not updating output

[13.8.10]
- Fix GUI not rendering when FTB Library is in mod list

[13.8.9]
- Fix framing table voiding contents of drawers
- Fix framing table not saving slots on reload

[13.8.8]
- Fix remote group upgrade language key (contrib by Jack McKalling)
- Fix inventory crash after crafting drawer with upgrade
- FABRIC: Fix blocks inserting into 2-tier compacting drawers (contrib by ellellie)
- FABRIC: Fix model compatibility issue with ModernFix (contrib by embeddedt)

[13.8.7]
- FABRIC: Fix void upgrade ignored when interacting with controllers (contrib by Pikachyuu)

[13.8.6]
- Fixed possible server hang during chunk saving when remote upgrades in use
- Fixed GUI names for drawers displaying as "Framing Table"
- Added Spanish (es_es) translation (contrib by vatusai)
- Added Chinese Simplified (zh_cn) translation (contrib by suoyuki, CTidy, zizunsi)
- NEOFORGE: Fix server crash on load when TheOneProbe is present

[13.8.5]
- Change debugTrace config item default value back to false
  - If your value was already written as true, you will need to change this yourself
- Remove item logging from block "take" function
- Re-enable compacting tiers config option
- Fix item insert overflow with creative storage upgrade
- Fix pick block not cloning framed blocks correctly
- Fix enable portability upgrade option being ignored
- NEOFORGE: Restore The One Probe support

[13.8.4]
- Fix framing table voiding unused materials
- Prevent framed trim from being used for retrimming
- Add renaming support for drawers
- Adds back conversion upgrade
  - Whitelists a few ore-based items by default, see config
- Added de_de translation (contrib by Der-Kanzler)
- Fix obsidian storage upgrade recipe

[13.8.3]
- Fixed framing table voiding framed input if materials already present
- Fix drawer block with removed drawers reverting to default drawer when broken and placed agian.
- Fix remote upgrade not respecting controller range
- Framing tables no longer interact with hoppers
- Add upgrade recipes to turn remote upgrades into group versions
- Add zh_tw translation (contrib by Lobster0228)

[13.8.2]
- Fix uncommon crash when remote upgrades invalidate (contrib by HugoSandell)
- Fix keyring keeping current selection when it was removed
- Fix keyring losing its name when modified or rotated
- Fix keyring recipe not adding key from recipe
- Fix key buttons not working when on floor or ceiling
- FABRIC: Fix drawers not honoring void upgrade when other inventories try to insert into them

[13.8.1]
- Refactored internal Chameleon API to be fully self-contained
- FABRIC: Fixed item interop clearing items from locked drawers

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