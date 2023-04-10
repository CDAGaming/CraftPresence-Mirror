# CraftPresence Changes

## v2.0.0 Release Candidate 3 (05/16/2023)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv1.9.6...release%2Fv2.0.0-rc.3)_

### Changes

* Java 7 is no longer supported! (You should be using at least Java 8 by now!)
    * The mod will still display as using Java 7 bytecode on legacy versions, but will be utilizing Java 8 APIs
    * The mod will crash on initialization with a `UnsupportedOperationException` when used on anything below Java 8
* Reworked the way placeholders are interpreted to utilize [Starscript](https://github.com/MeteorDevelopment/starscript)
    * This integration will allow for significantly more flexibility and overall control over placeholders and how they
      can be used
    * Due to this change, all placeholder names have been adjusted (See the `Placeholders` section of this changelog)
    * Over 40+ new functions have also been added, including Reflection, JSON, and additional backend utilities for
      users to create custom placeholders with!
    * Additionally, the `allowPlaceholderOperators` option has been removed, due to being redundant
    * Several new commands, such as `/cp compile` and `/cp search` have also been implemented
* Adjusted module logic to perform within their own sub-threads, in an effort to avoid waiting on them to retrieve data
    * IE the initial retrieval of data when a module is first enabled is now multithreaded, taking up much less time!
* Migrated the Config Systems from `Properties` to `GSON`
    * A one-time migration layer has been put into place to migrate your v1.x settings over to the v2.x format
    * The logic behind data validation has been condensed to be more performant, and this change allows config settings
      to be more easily migrated across major updates
    * Background Options, such as the tooltip and GUI backgrounds, have been reset, since `splitCharacter` was also
      removed, since we don't use Arrays in this new system
* Added the ability for Module elements to supply their own `PresenceData`
    * When supplied and enabled, this will do one of two things:
        * If `useAsMain` is enabled, this will allow an event to become the generic event rather than simple argument
          replacement, which is similar to the
          way [SimpleRPC (By Hypherion)](https://www.curseforge.com/minecraft/mc-mods/simple-discord-rpc) presents its
          data.
        * Otherwise, if `useAsMain` is disabled, this allows a placeholder to be interpreted differently depending on
          the RPC field that placeholder is within. An example of this would be being able to make a module's
          placeholder (`[module].message, [module].icon`)
          equal `this` if it is used in the `Details` Presence
          Field, while equaling `that` if used in the `Game State` Presence Field.
* Added more flexibility and usage to endpoint icons, including the addition of the `allowEndpointIcons`
    * For users, the new usages also include fetching the server icon in the Server Settings Scroll Lists, if the Base64
      icon is unavailable
    * It also allows for fetching a dynamic server icon if the icon's the module is looking for do not exist within the
      Discord Asset List, and doing similar activity for the player's head icon.
* Multiple Accessibility improvements have been made to the GUI in an effort to be more descriptive and user-friendly
* Added support for the per-gui, per-item, and per-entity systems to have RPC Icon Support
* Added support for transferring
  a [SimpleRPC (By Hypherion)](https://www.curseforge.com/minecraft/mc-mods/simple-discord-rpc) config to
  CraftPresence (With permission, of course!)
* Removed the ViveCraft Message Option and Fallback Placeholder Message
    * Developer Note: You can now use Starscript functions to make custom placeholders for specific brands
* UUIDs are now refreshed in the Entity Module List when the Server's Player List changes
    * This prevents a lot of extra elements from coming into the module list, which should keep things cleaner
* Backend: Increased the default text limit for all `ExtendedTextControl`'s
    * Due to this change, minified placeholder support has been removed from the backend

### Fixes

* Fixed the GUI module systems not working properly on 1.14+ ports
    * Note: Different Loaders may have different screen names, depending on mappings
* Fixed improper options being available in the Dynamic Editor Screen when adding data that was preliminary-supplied
  from other modules
* Fixed interpreting Dynamic Icons with Spaces in them
    * As part of this fix, `formatAsIcon` is now ignored for Custom Assets, but null checks
      do remain
* Fixed Texture saving issues for the `tooltipBackgroundColor`, `tooltipBorderColor`, `guiBackgroundColor`, and
  `buttonBackgroundColor` setting
* Backend: Fixed issues that could occur when `ExtendedTextControl#setControlMessage` was fired with a null argument
* Fixed the UUID placeholders in `&IGN&` (Now known as `player.*`) being available, even if it wasn't a valid UUID
* Fixed a regression in MultiMC-type instance detection from `v1.9.0` that caused a normal error to not be suppressed
  properly
* Fixed multiple issues that were preventing the ability to hide placeholder output depending on a per-module value
    * IE You can now set the `textOverride` to be an empty string in the frontend, and the mod will respect that
* Fixed preliminary-supplied data being able to be removed via the Dynamic Editor Screen
    * Only the config entry should have been effected, not the actual module data list
* Fixed Out-Of-Bound issues when there are less than 3 or 4 search results in a Scrollable List
    * This issue caused no elements to display until you scrolled, causing it to clamp back to normal values
    * The list will now reset the scroll when the list is updated
* Fixed Issues where data relying on the `children` list was unavailable in 1.13+ ports (Tab-Focus changing)
* Fixed Issues where the focus status was not being checked for `keyPressed` and `charTyped` on 1.13+ ports
    * This also fixes hearing a clicking sound when `KP_ENTER`, `ENTER` or the spacebar was pushed while focused on a
      text box
* Backend: Fixed `ImageUtils` dynamic texture creation not complying with 1.13+ namespace requirements

### Placeholders

One of the foundational changes that have been made to CraftPresence, is with placeholder interpretation.

With the integration of Starscript, several changes, additions, and removals have been made to placeholders and their
related systems:

* Placeholders can now be used anywhere, without restrictions
* Programmer expressions (Such as formatting, operators, as well as custom functions) have been implemented to allow an
  even greater level of configurability then we've ever had prior
    * See [their wiki](https://github.com/MeteorDevelopment/starscript/wiki) for some standard functions now
      available
* The OR operator, initially added in v1.9.x, has been replaced with Starscript usages
    * Prior usages will migrate to an `{getOrDefault(foo, bar)}` format to replicate the prior behavior
* All Placeholders have been renamed, converting to an `{foo.bar}` format instead
  of `&FOO:BAR&`
    * All prior usages from v1 configs will also be migrated to follow the new names as mentioned below:

* Renamed Placeholders (`old` => `new`, Surround with `{}` when using new names):
    * `&DEFAULT&` => `general.icon` (Icons Only)
    * `&MAINMENU&` => `menu.message`, `menu.icon` (Depends on config setting)
    * `&BRAND&` => `general.brand`
    * `&MCVERSION&` => `general.version`
    * `&IGN&` => `custom.player_info_out`, `player.icon` (Depends on config setting)
    * `&IGN:NAME&`, `&NAME&` (From `playerOuterInfoPlaceholder`) => `player.name`
    * `&IGN:UUID&`, `&UUID&` (From `playerOuterInfoPlaceholder`) => `player.uuid.short`
    * `&IGN:UUID_FULL&`, `&UUID_FULL&` (From `playerOuterInfoPlaceholder`) => `player.uuid.full`
    * `&MODS` => `custom.mods`
    * `&MODS:MODCOUNT&`, `&MODCOUNT&` (From `modsPlaceholder`) => `general.mods`
    * `&PACK&` => `custom.pack`, `pack.icon` (Depends on config setting)
    * `&PACK:NAME&`, `&NAME&` (From `modpackMessage`) => `pack.name`
    * `&DIMENSION:DIMENSION&`, `&DIMENSION&` (From Dimension Settings) => `dimension.name`
    * `&DIMENSION:ICON&`, `&ICON&` (From Dimension Settings) => `dimension.icon`
    * `&DIMENSION&` => `dimension.message`, `dimension.icon` (Depends on config setting)
    * `&BIOME:BIOME&`, `&BIOME&` (From Biome Settings) => `biome.name`
    * `&BIOME:ICON&`, `&ICON&` (From Biome Settings) => `biome.icon`
    * `&BIOME&` => `biome.message`, `biome.icon` (Depends on config setting)
    * `&SERVER:IP&`, `&IP&` (From Server Settings) => `server.address.short`
    * `&SERVER:NAME&`, `&NAME&` (From Server Settings) => `server.name`
    * `&SERVER:MOTD&`, `&MOTD&` (From Server Settings) => `server.motd.raw`
    * `&SERVER:ICON&`, `&ICON&` (From Server Settings) => `server.icon`
    * `&SERVER&` => `server.message`, `server.icon` (Depends on config setting)
    * `&SERVER:PLAYERS&`, `&PLAYERS&` (From Server Settings) => `custom.players`
    * `&SERVER:WORLDINFO&`, `&WORLDINFO&` (From Server Settings) => `custom.world_info`
    * `&SERVER:PLAYERINFO&`, `&PLAYERINFO&` (From Server Settings) => `custom.player_info_in`
    * `&SERVER:PLAYERINFO:COORDS&`, `&PLAYERINFO:COORDS&` (From Server Settings), `&COORDS&` (
      From `playerInnerInfoPlaceholder`) => `custom.player_info_coordinate`
    * `&SERVER:PLAYERINFO:HEALTH&`, `&PLAYERINFO:HEALTH&` (From Server Settings), `&HEALTH&` (
      From `playerInnerInfoPlaceholder`) => `custom.player_info_health`
    * `&SERVER:PLAYERINFO:COORDS:xPosition&`, `&PLAYERINFO:COORDS:xPosition&` (From Server Settings)
      , `&COORDS:xPosition&` (From `playerInnerInfoPlaceholder`), `&xPosition&` (From `playerCoordinatePlaceholder`)
      => `player.position.x`
    * `&SERVER:PLAYERINFO:COORDS:yPosition&`, `&PLAYERINFO:COORDS:yPosition&` (From Server Settings)
      , `&COORDS:yPosition&` (From `playerInnerInfoPlaceholder`), `&yPosition&` (From `playerCoordinatePlaceholder`)
      => `player.position.y`
    * `&SERVER:PLAYERINFO:COORDS:zPosition&`, `&PLAYERINFO:COORDS:zPosition&` (From Server Settings)
      , `&COORDS:zPosition&` (From `playerInnerInfoPlaceholder`), `&zPosition&` (From `playerCoordinatePlaceholder`)
      => `player.position.z`
    * `&SERVER:PLAYERINFO:HEALTH:CURRENT&`, `&PLAYERINFO:HEALTH:CURRENT&` (From Server Settings), `&HEALTH:CURRENT&` (
      From `playerInnerInfoPlaceholder`), `&CURRENT&` (From `playerHealthPlaceholder`) => `player.health.current`
    * `&SERVER:PLAYERINFO:HEALTH:MAX&`, `&PLAYERINFO:HEALTH:MAX&` (From Server Settings), `&HEALTH:MAX&` (
      From `playerInnerInfoPlaceholder`), `&MAX&` (From `playerHealthPlaceholder`) => `player.health.max`
    * `&SERVER:PLAYERS:CURRENT&`, `&PLAYERS:CURRENT&` (From Server Settings), `&CURRENT&` (From `playerListPlaceholder`)
      => `server.players.current`
    * `&SERVER:PLAYERS:MAX&`, `&PLAYERS:MAX&` (From Server Settings), `&MAX&` (From `playerListPlaceholder`)
      => `server.players.max`
    * `&SERVER:WORLDINFO:DIFFICULTY&`, `&WORLDINFO:DIFFICULTY&` (From Server Settings), `&DIFFICULTY&` (
      From `worldDataPlaceholder`) => `world.difficulty`
    * `&SERVER:WORLDINFO:WORLDNAME&`, `&WORLDINFO:WORLDNAME&` (From Server Settings), `&WORLDNAME&` (
      From `worldDataPlaceholder`) => `world.name`
    * `&SERVER:WORLDINFO:WORLDTIME&`, `&WORLDINFO:WORLDTIME&` (From Server Settings), `&WORLDTIME&` (
      From `worldDataPlaceholder`) => `world.time.format_24`
    * `&SERVER:WORLDINFO:WORLDTIME12&`, `&WORLDINFO:WORLDTIME12&` (From Server Settings), `&WORLDTIME12&` (
      From `worldDataPlaceholder`) => `world.time.format_12`
    * `&SERVER:WORLDINFO:WORLDDAY&`, `&WORLDINFO:WORLDDAY&` (From Server Settings), `&WORLDDAY&` (
      From `worldDataPlaceholder`) => `world.time.day`
    * `&SCREEN:SCREEN&`, `&SCREEN&` (From Gui Settings) => `screen.name`
    * `&SCREEN:ICON&`, `&ICON&` (From Gui Settings) => `screen.icon`
    * `&SCREEN:CLASS&`, `&CLASS&` (From Gui Settings) => `data.screen.class`
    * `&SCREEN&` => `screen.message`, `screen.icon` (Depends on config setting)
    * `&TARGETENTITY:ENTITY&`, `&ENTITY&` (From Target Entity Settings) => `entity.target.name`
    * `&TARGETENTITY:ICON&`, `&ICON&` (From Target Entity Settings) => `entity.target.icon`
    * `&TARGETENTITY&` => `entity.target.message`, `entity.target.icon` (Depends on config setting)
    * `&RIDINGENTITY:ENTITY&`, `&ENTITY&` (From Riding Entity Settings) => `entity.riding.name`
    * `&RIDINGENTITY:ICON&`, `&ICON&` (From Riding Entity Settings) => `entity.riding.icon`
    * `&RIDINGENTITY&` => `entity.riding.message`, `entity.riding.icon` (Depends on config setting)
    * `&TILEENTITY:[SLOT]&`, `&[SLOT]&` (From `playerItemsPlaceholder`) => `item.[slotIdentifier].message`
    * `&TILEENTITY&` => `item.message.default`
    * `&ITEM&` (From Item Settings) => `item.message.holding`
* Added Placeholders:
    * `data.server.motd.line.*` added to display only the specified line from `server.motd.raw`
    * `world.time.format_12` added to display `world.time.format_24` in an `xx:xx AM/PM` format
    * `player.icon` added if `allowEndpointIcons` is active and a valid `playerSkinEndpoint` is supplied
    * `*.instance` and `*.class` placeholders added to relevant Modules
* Removed (or moved) Placeholders:
    * NBT placeholders have been removed for the entity and item modules, replaced by the `getNbt` function
    * `player.position.*` and `player.health.*` placeholders now identify as `Double` instead of Strings
    * `server.players.*` placeholders now identify as `Integer` instead of Strings

### Translations

The following changes have been made for translations:

* Added:
    * `gui.config.message.button.remove` (Used in Dynamic Editors)
    * `gui.config.message.editor.original` (Used in Scroll List tooltips)
    * `gui.config.message.editor.description` (Used in Scroll List tooltips)
    * `gui.config.message.editor.usage` (Used in Scroll List tooltips)
    * `gui.config.{name,comment}.advanced.allow_endpoint_icons` (Added Property)
    * `gui.config.{name,comment}.advanced.server_icon_endpoint` (Added Property)
    * `gui.config.{name,comment}.display.dynamic_variables` (Added Property)
    * `gui.config.{name,comment}.display.enabled` (Added Property)
    * `gui.config.{name,comment}.display.use_as_main` (Added Property)
    * `craftpresence.defaults.integrations.*` (New namespace for Mod Integrations, only ReplayMod at the moment)
    * `craftpresence.command.export.progress` (Added Progress notifier for `/cp export assets`)
    * `craftpresence.command.compile` (Output for `/cp compile`)
    * `craftpresence.command.usage.{compile,search}` (Outputs for `/cp compile` and `/cp search` usage)
    * `craftpresence.placeholder.notes` (Added generalized translation for placeholder notes)
* Modified:
    * `gui.config.message.presence.{generalArgs,iconArgs}` (Edited to move duplicated data
      to `craftpresence.placeholders.notes`)
    * `gui.config.comment.button.sync.config` (Modified for new config file name)
    * `gui.config.comment.advanced.{item,entity_target,entity_riding}_messages` (Modified to remove separate tag
      placeholder section)
    * `gui.config.comment.display.{button_messages,dynamic_icons}` (Modified to remove outdated info)
    * `craftpresence.defaults.*` (Related to `Placeholders` Section)
    * `craftpresence.placeholders.*` (See `Placeholders` Section)
    * `craftpresence.command.usage.main` (Modified for `/cp compile` command)
* Removed:
    * `craftpresence.logger.error.config.adjust.global` (Obsolete, removed from Config System Upgrade)
    * `craftpresence.logger.info.config.notice` (Obsolete, removed from Config System Upgrade)
    * `craftpresence.exception.config.prop.null` (Obsolete, removed from Config System Upgrade)
    * `gui.config.{name,comment}.advanced.split_character` (Removed Property)
    * `gui.config.{name,comment}.advanced.allow_placeholder_operators` (Removed Property)
    * `gui.config.message.remove` (Replaced via button in related areas)
    * `gui.config.message.tags` (Merged into main placeholder tooltips)
    * `gui.config.[name,comment].status_messages.placeholder.*` (Moved to the Dynamic Variables UI)

___

### More Information

#### Known Issues

This release represents a work-in-progress build of CraftPresence's v2.0 release.
It does not in any way, shape, or form represent a final product, in either it's backend or frontend implementation.

Despite configuration compatibility being ensured between v1.8.x/v1.9.x and v2.0,
caution is advised to ensure the best experience, while also baring in mind that features can change, be added, or
outright removed without forewarning and without compatibility
between future work-in-progress releases.

The following known issues are present in this build:

* Text with colors do not retain those colors if that text moves to a newline in the CraftPresence UIs
* The HypherionMC Config Layer (To Convert a Simple RPC config to CraftPresence) is heavily work in progress:
    * Placeholders related to the realm event are currently unimplemented and parse as `{''}`.

#### 1.13.x Build Info

The Rift Edition of this Mod Requires the [Rift ModLoader](https://www.curseforge.com/minecraft/mc-mods/rift) and
contains the following differences to take Note of:

* KeyCodes have changed from an LWJGL Upgrade! Be Sure to check and edit your KeyBinds if migrating from 1.12.2 and
  below.

Starting in v1.5.0, The 1.13 Rift Port of CraftPresence was deprecated in favor of the 1.13.2 Rift Port.

Starting in v1.7.0, The aforementioned KeyCode warning is now void, due to new systems introduced to convert keybindings
between LWJGL versions.

Starting in v1.6.1, Rift Versions are no longer supported, though the differences do still take effect for Forge.

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

* Ports for MC 1.6.4 and lower will not show Image Previews in Scroll Lists, as Mojang did not implement the
  logic for this until MC 1.7 and above
* The MC a1.1.2_01 Port has its Dimension and Biome Modules **disabled**, as Mojang did not implement the logic for this
  until MC a1.2.x and above
* Bugs that are related to or are caused by issues in the Vanilla Codebase, are unlikely to be fixed due to
  Minecraft's limitations

See the Mod Description // README for More Info
