# CraftPresence Changes

## v2.1.2 (07/??/2023)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.1.0...release%2Fv2.1.2)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* Backend: Multiple Buildscript Overhauls to centralize repeated data
    * These changes also allow marking builds of CraftPresence for multiple versions
    * They also allow for officially marking support for other ModLoaders without their own sourceSet, such as Quilt.
* Placeholder Adjustments for `general` data:
    * On 1.7.10 and above: `general.version` now properly retrieves the detected MC Version (On lower versions, prior
      behavior is used)
    * Added a `general.protocol` version to allow a frontend method to retrieve the detected MC Protocol Version
    * Added a `data` version for the aforementioned placeholders to differentiate between `detected` and `compiled for`
      data

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
