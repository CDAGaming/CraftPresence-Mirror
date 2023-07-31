# CraftPresence Changes

## v2.2.1 (08/03/2023)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.2.0...release%2Fv2.2.1)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* Backend: Tweaked Exception Logging to use `LoggingImpl` instead of `System.err`
    * This only effects the mod's logging, not dependency logging
    * New Methods have been implemented in `LoggingImpl`, making use of `StringUtils#getStackTrace`
* Quality-Of-Life UI Improvements:
    * The Default Icon Button in `General Settings`, `Biome Settings`, `Dimension Settings`, and `Server Settings` have
      been changed to use the new Icon Selector from `Presence Settings`
    * Adjusted the positioning of several elements within `Accessibility Settings`
    * Added `maxConnectionAttempts`, `playerSkinEndpoint`, and `serverIconEndpoint` to the `Advanced Settings` UI
    * Removed the 3-digit limit for the `refreshRate` textbox
    * Removed the `512` character limit for the `Commands` UI Input Textbox

### Fixes

* Fixed Jar Metadata being missing, causing misc. runtime issues
    * This was a hotfix for v2.2.0, but it has been noted here as well
* Fixed rare cases of exceptions during file I/O operations
    * This primarily includes config saving, downloading files, and the `/cp export assets` logic
* Fixed a possible Concurrent Exception when Closing a GUI
* Fixed missing Command Feedback with the `/cp reboot` command

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
