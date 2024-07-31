# CraftPresence Changes

## v2.5.0 (08/08/2024)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.4.3...release%2Fv2.5.0)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* (Backend) Updated Build Dependencies (Please see the appropriate repositories for changes)
    * Unimined (`1.2.14` -> `1.3.4`)
    * JVMDowngrader (`0.7.2` -> `0.9.1`)
    * Shadow (`8.1.7` -> `8.1.8`)
    * Gradle (`8.8` -> `8.9`)
    * ModPublisher (`2.1.4` -> `2.1.6`)
    * ModFusioner (`1.0.12` -> `removed`)
    * UniLib (`local` -> `1.0.0`)
    * DiscordIPC (`0.9.2` -> `0.10.0`)
* Several packaging changes have been made for ease-of-access and for future development:
    * Added publishing support for [Nightbloom](https://nightbloom.cc/project/craftpresence)
    * Re-Added support for [OG Forge](https://files.minecraftforge.net/) for MC 1.20.2+ Users
    * Added support for Flint Loader for MC 1.21
    * Deployed files are now split per-loader, rather than as one "fused" jar per MC version
    * [UniLib](https://www.curseforge.com/minecraft/mc-mods/unilib) is now a required dependency
* Removed several config options that were not applicable to CraftPresence or were redundant metadata:
    * From `main`: `_README`, `_SOURCE`
    * From `accessibility`: `tooltipBackground`, `tooltipBorder`, `guiBackground`, `altGuiBackground`, `renderTooltips`
* Moved `Party Privacy` setting from `General` to `Presence Editor (PresenceData)`
    * This makes this setting an instance property instead of a global setting
    * Due to the changes behind this method, this property is also reset to its default
* Improved LAN support for the `server` module
    * Several fixes have been made for "hosting" LAN servers alongside fixes for LAN in general
    * This also effects Essential Mod support, given the similar networking tech involved
* Added an RPC Visualizer to the `Presence Editor` and `Display Settings` Screens
    * Replicates the look and feel of Discord's Visualizer, allowing users an "at-a-glance" preview of their RPC before
      saving
    * Only displays if `Use As Main` is enabled or is the Default Module
    * Can be disabled with the `Strip Extra Gui Elements` setting in `Accessibility`
* Added support for Discord Activity Types
    * Reference: [GitHub](https://github.com/discord/discord-api-docs/pull/7033)
* Misc. Optimizations and Performance Improvements across several APIs
    * A large portion of CraftPresence's APIs have now branched off into [UniLib](https://gitlab.com/CDAGaming/UniLib),
      now being served as a required dependency for CraftPresence
    * Many fixes and tweaks have been made over the last few months to make these APIs more usable for the public with
      better reliability and stability, especially on older versions of Minecraft

### Fixes

_A portion of these fixes are related to API functions that have been transferred
to [UniLib](https://gitlab.com/CDAGaming/UniLib)_

* (Backend) Fixed `Config#transferFrom` not properly considering flag data (Schema, MC Version, etc.)
    * This regression could cause config corruption, esp. when using the `HypherConverter` or `KeyConverter`
* (Backend) Fixed the `RAW_TRANSLATOR` not setting the proper `stripFormatting` flags
* Fixed `enabled` and `visible` state discrepancies on various UI Text Widgets
* Fixed issues where Slot List scrolling was inconsistent on some Legacy MC versions compared to others
* Fixed an issue in the BTA port where weather retrieval was not properly working
* Fixed inconsistent world data retrieval on some Legacy MC versions compared to others
* Fixed [an issue](https://gitlab.com/CDAGaming/CraftPresence/-/issues/224) where a crash can occur while registering
  Translation Listeners
* Fixed potential issues where `server.players.current` could be above `server.players.max`
* (Backend) Fixed issues relating to text going off-screen with some `ScrollableTextWidget` elements
    * Effected the `Advanced` and `Presence Editor` UIs

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
