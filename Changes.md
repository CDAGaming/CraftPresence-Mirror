# CraftPresence Changes

## v2.3.5 (03/07/2024)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.3.0...release%2Fv2.3.5)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* (Backend) Updated Build Dependencies (Please see the appropriate repositories for changes)
    * Spotless Plugin (`6.24.0` -> `6.25.0`)
    * UniCore (`1.0.6`, Separates the `core` package into its own project)
    * JUnixSocket (`2.8.3` -> `2.9.0`)
    * Fabric Loader (`0.15.6` -> `0.15.7`)
    * DiscordIPC (`0.8.1` -> `0.8.4`)
    * Starscript (`0.2.6` -> `0.3.0`)
    * Gradle (`8.5` -> `8.6`)
    * ModFusioner (`1.0.9` -> `1.0.10`)
    * ModPublisher (`1.0.20` -> `2.0.5`)
* Added new functions for placeholders:
    * `clampDouble(num, min, max)` - Clamps the Specified Number between a minimum and maximum limit
    * `clampFloat(num, min, max)` - Clamps the Specified Number between a minimum and maximum limit
    * `clampInt(num, min, max)` - Clamps the Specified Number between a minimum and maximum limit
    * `clampLong(num, min, max)` - Clamps the Specified Number between a minimum and maximum limit
    * `getElapsedMillis()` - Retrieve the elapsed time, in milliseconds
    * `getElapsedNanos()` - Retrieve the elapsed time, in nanoseconds
    * `getElapsedSeconds()` - Retrieve the elapsed time, in seconds
    * `lerpDouble(num, min, max)` - Linearly Interpolate between the specified values
    * `lerpFloat(num, min, max)` - Linearly Interpolate between the specified values
    * `roundDouble(num, places ?: 0)` - Rounds a Double to the defined decimal place, if possible
    * `snapToStep(num, valueStep)` - Rounds the Specified Value to the nearest value, using the Step Rate Value
* Adjusted existing functions for placeholders:
    * `dateToEpoch`, `epochToDate`, `timeFromEpoch`, `timeToEpoch` - Split into separate functions for milliseconds and
      seconds
* Implemented various QOL UI Rendering changes:
    * Scrolling String Support has been implemented for various UI controls, allowing for text to scroll horizontally if
      too large (Especially useful for localizations having text out of bounds)
    * (Backend) GLScissor behavior has been improved, supporting `left,top,right,bottom` alignments as well as
      auto-scale support
    * (Backend) Refactored String Rendering methods for `float` and `int` positioning (Also fixes some inconsistencies
      across the board)
    * The Search Box in the `SelectorGui` now resizes based on available space, rather than being a static size
    * `TextWidget` controls are now left-aligned instead of having centered text (Rendering performance has also been
      improved)
* Adjusted GUI Background Rendering Logic for MC 1.20.5 Changes
    * The `worldGuiBackground` config setting has been renamed to `altGuiBackground` (Existing value is reset, since MC
      1.20.5 changes the rules this screen appears for)
    * Both Gui Background settings are now reset when changing MC versions, to ensure setting validity
* Misc. Code Optimizations and Bug Fixes

### Fixes

* Fixed inconsistent Checkbox control alignment in `General Settings` and `Presence Settings` UI
* Fixed the beginning Base64 identifier for `ServerData` scroll lists (`png` instead of `unknown`)
* Fixed an inconsistent y-coord positioning on the "Search" text in the `SelectorGui`
* Fixed cases of incorrect GUI background rendering in some Scroll Lists
* Fixed `TexturedWidget` elements incorrectly scrolling in some cases
    * Its UV values are now locked to `0,0` instead of `left,top`
    * This bug was most commonly seen in the `Color Editor` GUIs

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
