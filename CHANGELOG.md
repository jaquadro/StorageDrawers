[12.9.8]
- Add several missing recipe advancement entries

[12.9.7]
- Fix framing table voiding framed drawer if quick-moved to input with materials already present
- Fix framing table collision shapes not matching block

[12.9.6]
- Fix framing table not respecting stack sizes during quick craft

[12.9.5]
- Fix framing table not respecting item stack sizes

[12.9.4]
- Fix drawer puller not working when on keyring

[12.9.3]
- Fix typo in remote upgrade description
- Fix framing table not being on the axe minable list
- Fix framing table breaking one block at a time
- Fix framing table hanging game when quick-moving items

[12.9.2]
- Fix crash when using remote upgrade

[12.9.1]
- Fix startup crash

[12.9.0]
- Added back support for framed drawers
  - Includes support for standard drawers, compacting drawers, trim, controllers, and slaves
- Added back framing table
  - Place any normal supported block on the table to create a framed version
  - Place framed version on table to get back materials and original block
  - Works for drawers that already hold contents

[12.8.0]
- Fixed item descriptions not breaking on newlines
- Added priority key and 5 priority levels to drawers
- Added Remote Upgrade to connect drawers to controller remotely
- Added Remote Group Upgrade to connect group of connected drawers to controller remotely
- Added more config entries to disable upgrades
- Fixed crash when trying to open Fluid Drawer GUIs (Legacy Fluid Drawers mod)

[12.7.2]
- Fixed crash when using keyring on drawers
- Fixed broken GUI texture on compacting drawers

[12.7.1]
- Fixed empty drawer items showing too-heavy message
- Brought back the personal key and CoFH variant of it

[12.7.0]
- Added 2-tier version of compacting drawers
- Added half-depth versions of both compacting drawers
- Max capacity checking for pulled drawers is default off in new configs
- BREAKING: Some resource filenames related to compacting drawers have changed, which may affect data packs.  Compacting drawers will likely fall back to their default resources.

[12.6.6]
- Fixed pulled drawer losing its capacity data if pulled when empty

[12.6.5]
- Fixed keyring representing a key that has been removed
- Fixed keyring losing extra metadata when last key is removed
- Fixed keyring not showing a key's custom name
- Fixed keyring description not rendering newlines correctly in some cases

[12.6.4]
- Fixed broken drawers causing slowness even when heavy config is disabled
- Added some overflow checking for when upgrades are configured very large

[12.6.3]
- Fix failure in recipe reload when EMI is present

[12.6.2]
- Added disabled messages to balance upgrade and drawer pulled if disabled in config
- Extended "heavy" drawer mode to detached drawers
  - considered heavy if containing more than stacksize of items
  - and was taken from drawer block that didn't have portability upgrade
- Fixed heavy drawer mode to check player inventory
- Made it possible to insert upgrades as normal items if sneak-clicking
- Detached drawer tooltip won't show stack limit if stack limit is not enforced in config

[12.6.1]
- Fixed accidental hard dependency on The One Probe

[12.6.0]
- Added detached drawers, which can be placed in empty slots in drawer blocks
- Added drawer puller tool to remove drawers from blocks
- Added config entries for detached drawers support
- Added balanced fill upgrade
- Added per-drawer stack capacity to drawer GUI
- Added re-trimming by sneak-using trim on a drawer block
- Added re-partitioning by sneak-using another drawer block on a drawer block if all slots are same item or empty
- Added heavy block option and upgrade (contrib. by loglob)
- Fixed invertShift and invertClick options in server environment
- Fixed TOP registration crash in server dev environment
- Fixed drawer screen title

[All Previous]
- All previous changesets can be found through commit history
or file listing on CurseForge