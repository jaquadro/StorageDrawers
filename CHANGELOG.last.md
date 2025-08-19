- Fixed meta blockstate loading errors (though they were harmless)
- Fixed conversion rules not loading
- Added option to disable logging startup rules, deny lists, etc.
- Fixed swapping upgrade with stack count > 1 voiding extra items
- Fixed admin key not locking if you don't own the drawer
- Improved consistency with claimed/protected chunks (e.g. via FTB Chunks)
- Added FTB variant of Personal Key to support team access
- Fixed remote upgrades showing bound description when not bound
- Added FTB Chunks support
- Added FTB Teams support

WARNING: This release REPLACES the existing storage drawers config file.
The original file will be left unchanged, but it will not be read from.
If you've changed your config, take a look at the new storagedrawers-common-v2.toml
file and make any changes you need.  The options available are not 1:1 with the old config.