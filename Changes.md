# CraftPresence Changes

## v1.9.0 (09/??/2022)

_A Detailed Changelog from the last release is available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv1.8.12...release%2Fv1.9.0)_

### Changes

*   Added a new `buttonMessages` config field for full frontend support of the buttons array in Rich Presence
    *   The buttons array will only take the first two detected entries, excluding `default` (Which is a template), since Discord's RPC only allows two buttons
    *   This can be customized in the `Presence Settings` menu of the Config UI, and all text placeholders are supported.
*   Unknown Placeholders after parsing will now be filtered out of the final Rich Presence Builder, if `formatWords` is enabled
*   Support for Sub-Arguments (Calling upon the individual inner arguments without it needing to be in a specialized message) has been implemented
    *   This allows much more customization, and the ability to use different types of a module in different areas more easily
    *   Format: `&<moduleName>:<innerPlaceholder>&` (For example: `&server:IP&` for the Server's `&IP&` inner-placeholder)
    *   Special Thanks to [this ticket](https://gitlab.com/CDAGaming/CraftPresence/-/issues/114) for suggesting the idea!

### Fixes

*   Fixed the alignment of elements in the `DynamicEditor` GUIs to avoid Gaps in certain circumstances
*   Fixed a visual error in `UpdateState` caused by a typo in retrieving the display name

___

### More Information

#### v1.9.0 Upgrade Info

v1.9.0 of CraftPresence is the next major feature and technical update after the v1.8.x pipeline.
It is a culmination of long-standing requests and fixes that have been sent in over the last few months.
It also is the first incubation period for the Beta MC ports, and their acheivements!

While no config migrations are necessary at this time for updating to v1.9.x, this can change as time goes on in the v1.8.x Pipeline, and will be noted here as such when and if these types of changes occur.

More features will additionally be planned and added for later in the v1.9.x Pipeline as further releases arrive (and as they are requested).

#### 1.13.x Build Info

The Rift Edition of this Mod Requires the [Rift ModLoader](https://www.curseforge.com/minecraft/mc-mods/rift) and contains the following differences to take Note of:

*   KeyCodes have changed from an LWJGL Upgrade! Be Sure to check and edit your KeyBinds if migrating from 1.12.2 and below.

Starting in v1.5.0, The 1.13 Rift Port of CraftPresence was deprecated in favor of the 1.13.2 Rift Port.

Starting in v1.7.0, The aforementioned KeyCode warning is now void, due to new systems introduced to convert keybindings between LWJGL versions, and this message will be removed in v1.7.1.

Note: Due to Maven Troubles on behalf of the Rift Team, Rift Versions are no longer supported as of v1.6.1, though the differences do still take effect for Forge.

#### 1.14+ Build Info

Some 1.14+ Ports of this Mod require the [FabricMC ModLoader](https://www.curseforge.com/minecraft/mc-mods/fabric-api) and contains the same differences as the 1.13.x Port.

#### Snapshot Build Info

Some Versions of this Mod are for Minecraft Snapshots or Experimental Versions, and as such, caution should be noted.

Any Snapshot Build released will be marked as BETA to match its Snapshot Status depending on tests done before release and issues found.

Snapshot Builds, depending on circumstances, may also contain changes for a future version of the mod, and will be noted as so if this is the case.

#### Legacy Build Info (Minecraft Versions 1.5.2 and Below)

Ports of this Mod for Minecraft Versions 1.5.2 and Lower are on very limited support, if using CraftPresence v1.8.11 and lower.

Please keep in mind the following:

*   Ports for MC 1.1.0 and lower are only available on Modrinth, due to lack of support in CurseForge
*   Ports for MC 1.6.4 and lower will now show Images for `ServerData` type Scroll Lists, as Mojang did not implement the logic for this until MC 1.7 and above
*   The MC a1.1.2_01 Port has its Dimension and Biome Modules **disabled**, as Mojang did not implement the logic for this until MC a1.2.x and above
*   Bugs that are related to or are caused by issues in the Vanilla Codebase, are unlikely able to be fixed due to Minecraft's limitations

See the Mod Description // README for More Info
