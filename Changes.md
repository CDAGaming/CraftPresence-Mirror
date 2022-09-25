# CraftPresence Changes

## v1.9.2 (09/29/2022)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv1.9.0...release%2Fv1.9.1)_

### Changes

* Allow the usage of placeholders in Dynamic Icon Urls
    * You cannot use them in name's, since those are meant to always be static for easy retrieval
* You can now use the sub-arguments from `&IGN&` and `&MODS&` independently from their placeholders
* Adjusted the `Language ID` setting, so that on MC 1.1.0 and above, the game's language setting has priority
    * In versions below MC 1.1.0, this behavior is unchanged from the prior version.
* Added Support for more Image Types with Scrollable Lists and Dynamic Icons
    * For specifics: GIFs are now supported in `ImageUtils` in url form, so long as it ends in `.gif`
* Misc. Backend Tweaks to allow all MC versions to use JSON for translations
    * JSON and LANG files are now supplied with each build, where LANG files are auto-generated from their JSON
      equivalent
    * Crowdin Integration has also been implemented and can be
      accessed [here](https://crowdin.com/project/craftpresence)

### Fixes

* Backend: Added additional String<->UUID conversion utils to ensure anything involving UUIDs follows Mojang's format
* Backend: Rewritten `UrlUtils#openUrl` to support Linux + Modern Versions of Windows and Java
* Backend: Fixed `DiscordUtils#updateTimestamp` not properly working
* Fixed a regression in the Entity Module, exposing a (now-fixed) flaw where `StringUtils#isValidUUID` could accept a
  null input, causing a crash
* Fixed an oversight in Translation logic that could cause the final value to be cut off when `usingJson` is true
    * For CraftPresence, the `craftpresence.exception.config.prop.null` translation was effected by this
* Fixed an oversight causing RPC Assets to not be re-synced when resetting the config or syncing the local config to the
  game
* Fixed an issue where added dynamic icons did not appear until after a client restart
* Fixed an issue where recursive entry into the same dynamic editor can cause excess gaps in the GUI
    * An example of this behavior is choosing an icon in the gui multiple times

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
