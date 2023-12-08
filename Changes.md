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
    * Lenni Reflect (`1.3.0` -> `1.3.1`)
    * Gradle (`8.4` -> `8.5`)
    * Fabric Loader (`0.14.24` -> `0.15.1`)
* Added Initial MC 1.20.3 and 1.20.4 Support
    * The MC 1.20.4 Build contains runtime support for MC 1.20.3
    * The MC 1.20.2 Build has been updated to prevent running it on MC 1.20.3 and above

### Fixes

* [Backend] Fixed an issue where negative numbers were not allowed in the `MathUtils#IsWithinValue` functions
    * This fix was previously included in
      the [World of Warcraft Addon](https://github.com/CDAGaming/CraftPresence-Wow-Edition)

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
