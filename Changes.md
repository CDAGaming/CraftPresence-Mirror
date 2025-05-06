# CraftPresence Changes

## v2.6.0 (05/08/2025)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.5.5...release%2Fv2.6.0)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* (Backend) Updated Build Dependencies (Please see the appropriate repositories for changes)
    * Spotless (`7.0.2` -> `7.0.3`)
    * UniLib (`1.0.6` -> `1.1.0`)
    * Lenni Reflect (`1.4.0` -> `1.5.0`)
* UniLib Base Requirement increased from `1.0.1` to `1.1.0`
    * `DynamicSelectorGui` no longer requires any entries in the entry list
    * Several API fixes for `FileUtils` (`findClass` and `loadClass`)
* Removed template data from the config settings, and related limitations
    * `PresenceData#buttons#default`
    * `displaySettings.dynamicIcons#default`
    * `displaySettings.dynamicVariables#default`
* Removed the `useClassLoader` setting from `Advanced` options in the config
    * This has been replaced with a revised implementation in `UniLib` and `UniCore`
* Significantly Improved `ReplayMod` integration reliability
    * Also added `data.` variants of `replaymod.time.current` and `replaymod.time.remaining`
* Added support for placeholder parsing for module icons (`.icon` placeholders in modules)
* Updated Translations

### Fixes

* Fixed ReplayMod not adding new defaults to the `GUI` config, when creating a new config file
* Fixed ClassGraph functions not working on initial launch if config settings matched internal settings
* Fixed potential inaccuracies in Dimension and Biome module updates
* Fixed missing SimpleRPC migration for `{{player.uuid}}` migration
* (Backend) Fixed some NPEs related to `DiscordUtils#getOverrideText`

___

### More Information

#### Known Issues

Despite configuration compatibility often being ensured between versions,
caution is advised to ensure the best experience, while also baring in mind that features can be adjusted, removed, or
added/iterated upon between releases.

Please refer to the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) to view more info relating
to known issues.

#### Snapshot Build Info

Some Versions of this Mod are for Minecraft Snapshots or Experimental Versions, and as such, caution should be noted.

Any Snapshot Build released will be marked as **ALPHA** to match its Snapshot Status depending on tests done before
release
and issues found.

Snapshot Builds, depending on circumstances, may also contain changes for a future version of the mod, and will be noted
as so if this is the case with the `-Staging` label.
