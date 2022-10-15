# CraftPresence Changes

## v1.9.4 (10/18/2022)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv1.9.3...release%2Fv1.9.4)_

### Changes

* Added support for the `|` (OR) Operator in `Presence Settings` fields, if `allowPlaceholderOperators` is true
    * This change can be used to combine 2 placeholders, in which the mod will choose the first
      non-null placeholder. One such example is that doing `&SERVER&|&PACK&` will show the pack message, if the server
      message
      is null, and will show the `&SERVER&` data if it is not null (Even if the `&PACK&` data is also not null)
    * As a result of this change, the `detailsMessage`, `largeImageMessage`, `largeImageKey`, and `smallImageKey` have
      been revised to include OR operators. Users can either manually use the OR operator or reset those fields to have
      them automatically applied upon config regeneration.
    * This can also be considered a fix for when using a server icon while a pack icon was also available, which is a
      scenario that can happen under default behavior, and the reasoning for this addition.
* Added `allowPlaceholderPreviews` as an Advanced Setting to replace needing `Debug Mode` for placeholder previews

### Fixes

* Backend: Fixed shadowed dependencies breaking Fabric and Forge Developer Environment functionality
* Backend: Fixed an issue where `getArgumentsMatching` could sometimes lead to unintended behavior
* Backend: Fixed a NPE from `generateArgumentMessage` if it is fired before the IPC instance is initialized
* Fixed an regression causing null characters to be considered valid for the `splitCharacter` setting
* Fixed possible NPEs within `ServerUtils` if `currentServerMessage` or `currentServerIcon` was null
    * This also fixes compatibility with Replay Mod and similar mods

___

### More Information

#### v1.9.0 Upgrade Info

v1.9.0 of CraftPresence is the next major feature and technical update after the v1.8.x pipeline.
It is a culmination of long-standing requests and fixes that have been sent in over the last few months.
It also is the first incubation period for the Beta MC ports, and their acheivements!

While no config migrations are necessary at this time for updating to v1.9.x, this can change as time goes on in the
v1.8.x Pipeline, and will be noted here as such when and if these types of changes occur.

More features will additionally be planned and added for later in the v1.9.x Pipeline as further releases arrive (and as
they are requested).

#### 1.13.x Build Info

The Rift Edition of this Mod Requires the [Rift ModLoader](https://www.curseforge.com/minecraft/mc-mods/rift) and
contains the following differences to take Note of:

* KeyCodes have changed from an LWJGL Upgrade! Be Sure to check and edit your KeyBinds if migrating from 1.12.2 and
  below.

Starting in v1.5.0, The 1.13 Rift Port of CraftPresence was deprecated in favor of the 1.13.2 Rift Port.

Starting in v1.7.0, The aforementioned KeyCode warning is now void, due to new systems introduced to convert keybindings
between LWJGL versions, and this message will be removed in v1.7.1.

Note: Due to Maven Troubles on behalf of the Rift Team, Rift Versions are no longer supported as of v1.6.1, though the
differences do still take effect for Forge.

#### 1.14+ Build Info

Some 1.14+ Ports of this Mod require the [FabricMC ModLoader](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
and contains the same differences as the 1.13.x Port.

#### Snapshot Build Info

Some Versions of this Mod are for Minecraft Snapshots or Experimental Versions, and as such, caution should be noted.

Any Snapshot Build released will be marked as BETA to match its Snapshot Status depending on tests done before release
and issues found.

Snapshot Builds, depending on circumstances, may also contain changes for a future version of the mod, and will be noted
as so if this is the case.

#### Legacy Build Info (Minecraft Versions 1.5.2 and Below)

Ports of this Mod for Minecraft Versions 1.5.2 and Lower are on very limited support, if using CraftPresence v1.8.11 and
lower.

Please keep in mind the following:

* Ports for MC 1.6.4 and lower will now show Images for `ServerData` type Scroll Lists, as Mojang did not implement the
  logic for this until MC 1.7 and above
* The MC a1.1.2_01 Port has its Dimension and Biome Modules **disabled**, as Mojang did not implement the logic for this
  until MC a1.2.x and above
* Bugs that are related to or are caused by issues in the Vanilla Codebase, are unlikely able to be fixed due to
  Minecraft's limitations

See the Mod Description // README for More Info
