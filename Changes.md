# CraftPresence Changes

## v2.2.6 (12/19/2023)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.2.5...release%2Fv2.2.6)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* Backend: Updated Build Dependencies (Please see the appropriate repositories for changes)
    * Unimined (`1.1.0-SNAPSHOT` -> `1.1.0`)
    * Spotless Plugin (`6.22.0` -> `6.23.3`)
    * Spotless Annotations (`4.7.3` -> `4.8.2`)
    * Lenni Reflect (`1.3.0` -> `1.3.2`)
    * Gradle (`8.4` -> `8.5`)
    * Fabric Loader (`0.14.24` -> `0.15.3`)
* Added Initial MC 1.20.3, 1.20.4, and 1.20.5 Snapshot Support
    * The MC 1.20.4 Build contains runtime support for MC 1.20.3
    * The MC 1.20.2 Build has been updated to prevent running it on MC 1.20.3 and above

### Fixes

* (Backend) Fixed an issue where negative numbers were not allowed in the `MathUtils#IsWithinValue` functions
    * This fix was previously included in
      the [World of Warcraft Addon](https://github.com/CDAGaming/CraftPresence-Wow-Edition)
* (Backend) Fixed an issue where multi-line text was not preserving formatting codes between lines
    * Effects `drawMultiLineString`, `renderNotice`, and `TextDisplayWidget#draw`
    * `RenderUtils#sizeStringToWidth` and `StringUtils#getFormatFromString` also adjusted
    * `isFormatColor` and `isFormatSpecial` added to `StringUtils`
* (Backend) Fixed an issue on MC 1.5.2 and below causing certain text to be un-translated in Mojang UIs
    * Example: The MC Controls Gui showing an un-translated KeyBind name on these versions
    * This fix involves translation injection on these versions, using a new `onLanguageSync` event added
      to `TranslationUtils`

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
