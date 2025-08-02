- Fixed rendering of framed drawer item blocks
- Fixed shift+clicking additional blocks into framing table not updating output
- Fixed framing tables not dropping contents when broken
- Limit framed materials to solid opaque blocks (within reasonable heuristic)
- Added restrictFramingMaterials config option to control new material limit
- NEO/FORGE: Fixed items on framing table vanishing at oblique viewing angles

Note: This is the first Neo/Forge release built from the multi-project sources
(the ones used to build Fabric).  It should be functionally the same as the previous
12.9.x releases, but there are many under-the-hood code changes to support the
multi-project environment.  This first release is marked BETA for this reason.  Please
report any regressions to the project discord.