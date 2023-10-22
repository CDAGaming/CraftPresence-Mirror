# CraftPresence Changes

## v2.2.4 (10/26/2023)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.2.3...release%2Fv2.2.4)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* Backend: Updated Build Dependencies (Please see the appropriate repositories for changes)
    * Gradle (`8.3` -> `8.4`)
    * Fabric Loader (`0.14.22` -> `0.14.23`)
    * Lenni Reflect (`1.2.4` -> `1.3.0`)
    * Starscript (`0.2.5` -> `0.2.6`)
    * Classgraph (`4.8.162` -> `4.8.163`)
    * JUnixSocket (`2.7.0` -> `2.8.1`)
* Adjusted Mod Initialization in Forge 1.13+ to better detect (and avoid) running the mod on server-side
* Adjusted the `general.brand` placeholder to support the `minecraft.launcher.brand` System Property
    * The prior implementation of this placeholder will be used if this property is not present
* Updated Translations for Belarusian, Pirate English, French, and Russian
    * Please note these are community-created translations from Crowdin, and issues may be present!

### Fixes

* Fixed a discrepancy between the author name between Forge and Fabric
* Modified the 1.14+ Forge `DISPLAYTEST` fix to also support 1.13.2 Forge Users
    * This resolves an issue where an "Incompatible FML modded server" X indicator could appear in multiplayer
    * This fix only applies to Forge Versions above `1.13.2-25.0.103`

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
