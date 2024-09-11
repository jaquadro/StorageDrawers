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