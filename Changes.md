# CraftPresence Changes

## v2.4.0 (??/??/2024)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.3.9...release%2Fv2.4.0)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* (Backend) Updated Build Dependencies (Please see the appropriate repositories for changes)
    * Unimined (`1.2.3` -> `1.2.4`)
    * ModPublisher (`2.1.1` -> `2.1.2`)
    * Fabric Loader (`0.15.10` -> `0.15.11`)
* Adjusted the way `Dimension` and `Biome` module data is loaded in MC 1.16+
    * These changes are designed to support auto-locating data related to data pack additions
    * These changes also resolve issues where repeated Registry Lookups could cause a crash in both modules
* Added a new script function, `getComponent`, to support the new `DataComponent` system introduced in MC 1.20.5+
    * Usage: `getComponent(data=DataComponentHolder, path=String)`
    * On versions below MC 1.20.5, an error will appear instead

### Fixes

* (Backend) Modified `ExtendedScreen#getButtonY` to properly respond to `ScrollPane` padding and to better clarify the
  logic
    * For screens where this is used, this places UI elements in the proper position comparable to v1.x versions
    * Old Calculation: `(40 + (25 * (order - 1)))`
    * New Calculation `topPosition + (DEFAULT_ELEMENT_PADDING * (order + 1)) + (DEFAULT_ELEMENT_HEIGHT * order)`
* (Backend) Fixed an issue where `ColorData#setEndColor` and `ColorData#setTexLocation` could be applied incorrectly
    * This fix was previously present in `ColorEditorGui#setCurrentData` but has been moved into `ColorData` to remove
      duplicated logic and to resolve some edge-cases

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
