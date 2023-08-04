# CraftPresence Changes

## v2.2.1 (08/04/2023)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.2.0...release%2Fv2.2.1)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* Added Initial Support for Minecraft 1.20.2
    * Multiple refactors have been made to Controls and the UI, see the full changelog for more info
* Added Initial Support for Minecraft BTA 1.7.7.0
    * In a future update, `ModUtils.RAW_TRANSLATOR` will be adjusted to support BTAs Unique Locale System
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
* Removed the `buttonBackground` color setting in `Accessibility`
    * This option was under-developed and is no longer compatible with MC 1.20.2

### Fixes

* Fixed Jar Metadata being missing, causing misc. runtime issues
    * This was a hotfix for v2.2.0, but it has been noted here as well
* Fixed rare cases of exceptions during file I/O operations
    * This primarily includes config saving, downloading files, and the `/cp export assets` logic
* Fixed missing Command Feedback with the `/cp reboot` command
* Fixed a visual glitch where `TextWidget` controls can render incorrectly when a Screen first opens
* Fixed a visual glitch caused by an incorrect render order in `SelectorGui`

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
