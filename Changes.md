# CraftPresence Changes

## v1.9.3 (10/11/2022)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv1.9.2...release%2Fv1.9.3)_

### Changes

* Added `&UUID_FULL&` to `&IGN&`'s sub-placeholders, to allow having the dashed format or the trimmed format of UUIDs
* Overhaul the way tooltips with placeholders are displayed, both for easier translating and for better readability
    * Aditionally, systems have been added to allow deeper levels of sub-placeholders, expanding on prior v1.9.x systems
    * Enabling `Debug Mode` will allow you to see previews of currently active placeholders within the tooltip (If the
      preview is over 128 characters, it'll show `<...>` as an indicator that it is too large)
* Added new translations for Default Config Options and for the individual placeholder descriptions
* Updated existing translations in: `de_de`, `es_es`, `fr_fr`, and `ru_ru`
* Backend Optimizations and Refactors applied to reduce Beta and Alpha MC changes as well as misc. performance
  improvements

### Fixes

* Fixed a regression from v1.9.0 that prevented Tag Placeholders in Per-Entity and Per-Items to display in some cases
* Fixed rendering issues with text boxes in Paginated Screens on MC 1.3.2 and 1.2.5
* Fixed `&SERVER:WORLDINFO:WORLDNAME&` not displaying properly on some versions
    * Now displays the Level Name in Integrated Server Environments, and prior behavior anywhere else
    * Also adds a fallback default translation to use for it, so Beta and Alpha MC can use it
* Fixed a regression where some default options only used `en_us` localization, where it was meant to be the current
  language
* Backend: Fixed an issue preventing `Pair#equals` and `Tuple#equals` from working properly
* Backend: Removed unnecesary data from the Discord `ArgumentType` enum (`Button` and `Invalid`)
* Backend: Removed unnecesary code in the GUI Module, where an empty image argument was always assigned, when Images are
  not supported for it

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
