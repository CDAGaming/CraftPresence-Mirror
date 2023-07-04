# CraftPresence Changes

## v2.1.2 (07/11/2023)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.1.0...release%2Fv2.1.2)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* Backend: Multiple Buildscript Overhauls to centralize repeated data and allow for new versioning support
    * Added support for 1.14, 1.14.1, 1.14.2, and 1.14.3 for the 1.14.4 Build
    * Added support for 1.15 and 1.15.1 for the 1.15.2 Build
    * Added a new 1.16.1 Build to cover both MC 1.16.1 and 1.16
    * Added support for 1.16.2 for the 1.16.3 Build
    * Added support for 1.16.4 for the 1.16.5 Build
    * Added support for 1.17 for the 1.17.1 Build
    * Added support for 1.18 for the 1.18.1 Build
    * Added support for 1.19.1 for the 1.19.2 Build
    * Added support for 1.20 for the 1.20.1 Build
* Placeholder Adjustments for `general` data:
    * On 1.7.10 and above: `general.version` now properly retrieves the detected MC Version (On lower versions, prior
      behavior is used)
    * Added a `general.protocol` version to allow a frontend method to retrieve the detected MC Protocol Version
    * Added a `data` version for the aforementioned placeholders to differentiate between `detected` and `compiled for`
      data
* Added a notice in the `MainUI` for when the Game Version differs from the Game Version the mod was compiled with
    * Given the newer relaxed versioning, there are cases where things may not work 100% which this warning is meant to
      symbolize
    * Primarily, the Per-GUI module may fail in certain cases, due to mismatches in the mapping file that it uses for
      auto-lookups

### Fixes

* Fixed an issue where Background Rendering could be incorrect in some UIs
    * Background Rendering now override's stock MC methods rather than using its own method
    * This will allow some mods to also be able to properly override and interpret the Background State
    * An additional fix has been made to properly apply the Background to Scroll Lists as well

___

### More Information

#### Known Issues

Despite configuration compatibility being ensured between v1.8.x/v1.9.x and v2.0,
caution is advised to ensure the best experience, while also baring in mind that features can be adjusted, removed, or
added/iterated upon between releases.

The following known issues are present in this build:

* On certain MC versions, Scrolling while in a Scroll List drawing `ItemStack`'s may cause GUI distortions
* Text with colors do not retain those colors if that text moves to a newline in the CraftPresence UIs
* The HypherionMC Config Layer (To Convert a Simple RPC config to CraftPresence) contains the following known issues:
    * Placeholders related to the realm event are currently unimplemented and parse as `{''}`.

#### Snapshot Build Info

Some Versions of this Mod are for Minecraft Snapshots or Experimental Versions, and as such, caution should be noted.

Any Snapshot Build released will be marked as **ALPHA** to match its Snapshot Status depending on tests done before
release
and issues found.

Snapshot Builds, depending on circumstances, may also contain changes for a future version of the mod, and will be noted
as so if this is the case with the `-Staging` label.
