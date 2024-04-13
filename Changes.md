# CraftPresence Changes

## v2.3.8 (04/16/2024)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.3.7...release%2Fv2.3.8)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* (Backend) Updated Build Dependencies (Please see the appropriate repositories for changes)
    * ClassGraph (`4.8.168` -> `4.8.170`)
    * JUnixSocket (`2.9.0` -> `2.9.1`)
    * UniCore (`1.0.8` -> `1.0.9`)
    * DiscordIPC (`0.8.5` -> `0.8.6`)
* Quality of Life UI Improvements
    * `DynamicEditorGui`: Now uses the new Icon Selection System, improved rendering order, and the `ScrollPane` layout
      introduced in past updates
    * `UpdateInfoGui`: Moved tooltip rendering from UI title sections to a separate button
* Removed excessive debug logging from `DiscordUtils#imageOf`, when using a cached icon
    * This had resulted in user confusion and never really worked properly due to parallel usage
    * An example of one such fail case is using the Per-GUI system with Pack Integration

### Fixes

* Fixed incorrect Tooltip Rendering in `Server Messages` Entry Editor
* (Backend) Fixed issues in `Module#transferFrom` function calls
    * This was the root cause behind the `Save` indicator failing to appear in certain cases
    * It also caused some data to be saved early, `ModuleData` as an example

___

### More Information

#### Known Issues

Despite configuration compatibility being ensured between v1.8.x/v1.9.x and v2.0,
caution is advised to ensure the best experience, while also baring in mind that features can be adjusted, removed, or
added/iterated upon between releases.

The following known issues are present in this build:

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
