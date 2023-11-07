# CraftPresence Changes

## v2.2.5 (12/??/2023)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.2.4...release%2Fv2.2.5)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* Backend: Updated Build Dependencies (Please see the appropriate repositories for changes)
    * Unimined (`1.0.5` -> `1.1.0-SNAPSHOT` with NeoForge Support)
    * Forgix (Plugin swapped with [ModFusioner](https://github.com/firstdarkdev/modfusioner))
    * Classgraph (`4.8.163` -> `4.8.164`)
    * JUnixSocket (`2.8.1` -> `2.8.2`)
* Miscellaneous Refactors and optimizations, relating to API functions and locations

### Fixes

* Resolved a memory leak in the Dimension and Biome Modules, relating to `WorldProvider` storage
  * Credits to the GTNH Team and these PRs: [#1](https://gitlab.com/CDAGaming/CraftPresence/-/merge_requests/115), [#2](https://github.com/GTNewHorizons/CraftPresence/pull/2)

___

### More Information

#### Known Issues

Despite configuration compatibility being ensured between v1.8.x/v1.9.x and v2.0,
caution is advised to ensure the best experience, while also baring in mind that features can be adjusted, removed, or
added/iterated upon between releases.

The following known issues are present in this build:

* Text with colors do not retain those colors if that text moves to a newline in the CraftPresence UIs
* The HypherionMC Config Layer (To Convert a Simple RPC config to CraftPresence) contains the following known issues:
    * Placeholders related to the realm event are currently unimplemented and parse as `{''}`.

Please refer to the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) to view more info relating
to known issues.

#### Snapshot Build Info

Some Versions of this Mod are for Minecraft Snapshots or Experimental Versions, and as such, caution should be noted.

Any Snapshot Build released will be marked as **ALPHA** to match its Snapshot Status depending on tests done before
release
and issues found.

Snapshot Builds, depending on circumstances, may also contain changes for a future version of the mod, and will be noted
as so if this is the case with the `-Staging` label.
