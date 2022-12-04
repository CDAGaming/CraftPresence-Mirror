# CraftPresence Changes

## v2.0.0 Alpha 2 (02/??/2023)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv1.9.6...release%2Fv2.0.0-alpha.2)_

### Changes

* Java 7 is no longer supported! (You should be using at least Java 8 by now!)
    * The mod will still display as using Java 7 bytecode on legacy versions, but will be utilizing Java 8 APIs
    * The mod will crash on initialization with a `RuntimeException` when used on anything below Java 8
* Reworked the way placeholders are interpreted to utilize [Starscript](https://github.com/MeteorDevelopment/starscript)
    * This integration will allow for significantly more flexibility and overall control over placeholders and how they
      can be used
    * Due to this change, all placeholder names have been adjusted (See the `Placeholders` section of this changelog)
    * Aditionally, the `allowPlaceholderOperators` option has been removed, due to being redundant
* Adjusted module logic to perform within their own sub-threads, in an effort to avoid waiting on them to retrieve data
    * IE the initial retrieval of data when a module is first enabled is now multi-threaded, taking up much less time!
* Migrated the Config Systems from `Properties` to `GSON`
    * A one-time migration layer has been put into place to migrate your v1.x settings over to the v2.x format
    * The logic behind data validation has been condensed to be more performant, and this change allows config settings
      to be more easily migrated across major updates
    * Background Options, such as the tooltip and GUI backgrounds, have been reset, since `splitCharacter` was also
      removed, since we don't use Arrays in this new system
* Added the ability for Module elements to supply their own `PresenceData`
    * When supplied and enabled, this will do one of two things:
        * If `useAsMain` is enabled, this will allow an event to become the generic event rather then simple argument
          replacement, which is similar to the
          way [SimpleRPC (By Hypherion)](https://www.curseforge.com/minecraft/mc-mods/simple-discord-rpc) presents it's
          data.
        * Otherwise, if `useAsMain` is disabled, this allows a placeholder to be interpreted differently depending on
          the RPC field that placeholder is within. An example of this would be being able to make `&DIMENSION&`
          equal `this` if it is used in the `Details` Presence
          Field, while equaling `that` if used in the `Game State` Presence Field.
* Added more flexibility and usage to endpoint icons, including the addition of the `allowEndpointIcons`
    * For users, the new usages also include fetching the server icon in the Server Settings Scroll Lists, if the Base64
      icon is unavailable
    * It also allows for fetching a dynamic server icon if the icon's the module is looking for do not exist within the
      Discord Asset List, and doing similar activity for the player's head icon.
* Multiple Accessibility improvements have been made to the GUI in an effort to be more descriptive and user-friendly
* Added support for the per-gui, per-item, and per-entity systems to have RPC Icon Support
* Added support for transferring a Simple RPC (By HypherionSA) config to CraftPresence (With permission, of course!)
* Removed the ViveCraft Message Option and Fallback Placeholder Message
    * Alpha Note: The ViveCraft Option will be replaced by something before v2.0 fully releases!
* UUIDs are now refreshed in the Entity Module List when the Server's Player List changes
    * This prevents a lot of extra elements from coming into the module list, which should keep things cleaner
* Backend: Increased the default text limit for all `ExtendedTextControl`'s
    * Due to this change, minified placeholder support has been removed from the backend

### Fixes

* Fixed the GUI module systems not working properly on 1.14+ ports
    * Note: Different Loaders may have different screen names, depending on mappings
* Fixed improper options being available in the Dynamic Editor Screen when adding data that was preliminary-supplied
  from other modules
* Fixed interpreting Dynamic Icons with Spaces in them (`formatAsIcon` is now ignored for Custom Assets, but null checks
  do remain)
* Fixed Texture saving issues for the `tooltipBackgroundColor`, `tooltipBorderColor`, `guiBackgroundColor`, and
  the `buttonBackgroundColor` setting
* Fixed issues that could occur when `setControlMessage` was fired with a null argument
* Fixed the UUID placeholders in `&IGN&` being available, even if it wasn't a valid UUID
* Fixed a regression in MultiMC-type instance detection from `v1.9.0` that caused a normal error to not be suppressed
  properly
* Fixed multiple issues preventing the ability to hide placeholder output depending on a per-module value
    * IE You can set the `textOverride` to be an empty string in the frontend, and the mod will respect that
* Fixed preliminary-supplied data being able to be removed via the Dynamic Editor Screen
    * Only the config entry should have been effected, not the actual module data list
* Fixed Out-Of-Bound issues when there are less then 3 or 4 search results in a Scrollable List
    * This issue caused no elements to display until you scrolled, causing it to clamp back to normal values
    * The list will now reset the scroll when the list is updated
* Fixed Issues where data relying on the `children` list was unavailable in 1.13+ ports (Tab-Focus changing)
* Fixed Issues where the focus status was not being checked for `keyPressed` and `charTyped` on 1.13+ ports
    * This also fixes hearing a clicking sound when `KP_ENTER`, `ENTER` or the spacebar was pushed while focused on a
      text box
* Backend: Fixed `ImageUtils` dynamic texture creation not complying with 1.13+ namespace requirements

### Placeholders

One of the foundational changes that have been made to CraftPresence, is with placeholder interpetation.

With the integration of Starscript, several changes, additions, and removals have been made to placeholders and their
related systems:

* Placeholders can now be used anywhere (Gone with `ArgumentType` and freedom to customize)
* Programmer expressions (Such as formatting, operators, as well as custom functions) have been implemented to allow an
  even greater level of customizability then we've ever had prior
    * See [their wiki](https://github.com/MeteorDevelopment/starscript/wiki) for some of the standard functions now
      available
* The OR operator (Initially added in v1.9.x) has been removed
    * Prior usages will migrate to an `{foo != null ? foo : bar}` format to replicate the prior behavior
* All Placeholders have been renamed, following a base format of converting to an `{foo.bar}` format instead
  of `&FOO:BAR&`
    * All prior usages from v1 configs will also be migrated to follow the new names as mentioned below:

* Renamed Placeholders (`old` => `new` (`conditions`)):
    * `TBD` => `TBD` ()
* Added Placeholders:
    * TBD
* Removed (or moved) Placeholders:
    * TBD

### Translations

The following changes have been made for translations:

* Added:
    * `gui.config.message.editor.original`
    * `gui.config.message.button.remove`
    * `gui.config.{name,defaults}.advanced.allow_endpoint_icons` (Added Property)
    * `gui.config.{name,comment}.advanced.server_icon_endpoint` (Added Property)
    * `craftpresence.placeholders.*` (See `Placeholders` Section)
* Modified:
    * `gui.config.comment.button.sync.config` (Modified for new config file name)
* Removed:
    * `craftpresence.logger.error.config.adjust.global`
    * `craftpresence.logger.info.config.notice`
    * `craftpresence.exception.config.prop.null`
    * `gui.config.{name,comment}.advanced.split_character` (Removed Property)

___

### More Information

#### Known Issues

This release represents a work-in-progress build of CraftPresence's v2.0 release, planned for 2023.
It does not in any way, shape, or form represent a final product, in either it's backend or frontend implementation.

Despite configuration compatibility being ensured between v1.8.x/v1.9.x and v2.0,
caution is advised to ensure the best experience, while also baring in mind that features can change, be added, or
outright removed without forewarning and without compatibility
between future Alpha releases.

The following known issues are present in this build:

* Text with colors do not retain those colors if that text moves to a newline in the CraftPresence UIs
* The behavior for Resetting and Syncing a Local Config has been changed and may have issues!
* The HypherionMC Config Layer (To Convert a Simple RPC config to CraftPresence) is heavily work in progress:
    * Placeholders related to the realm and Replay Mod Integration are currently unimplemented and parse as `&unknown&`.
    * `%weather%` is also unimplemented at this time, and will also parse as `{''}'`

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
