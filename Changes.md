# CraftPresence Changes

## v2.0.0 Alpha 1 (11/29/2022)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv1.9.6...release%2Fv2.0.0+Alpha.1)_

### Changes

* Migrated the Config Systems from `Properties` to `GSON`
    * A one-time migration layer has been put into place to migrate your v1.x settings over to the v2.x format
    * The logic behind data validation has been condensed to be more performant, and this change allows config settings
      to be more easily migrated across major updates
    * Background Options, such as the tooltip and GUI backgrounds, have been reset, since `splitCharacter` was also
      removed, since we don't use Arrays in this new system
* Added the ability for placeholders to be interpreted differently depending on the RPC field
    * An example of this would be being able to make `&DIMENSION&` equal `this` if it is used in the `Details` Presence
      Field, while equaling `that` if used in the `Game State` Presence Field
* Added more flexibility and usage to endpoint icons, including the addition of the `allowEndpointIcons`
    * For users, the new usages also include fetching the server icon in the Server Settings Scroll Lists, if the Base64
      icon is unavailable
    * It also allows for fetching a dynamic server icon if the icon's the module is looking for do not exist within the
      Discord Asset List, and doing similar activity for the player's head icon.
* Multiple Accessibility improvements have been made to the GUI in an effort to be more descriptive and user-friendly
* Added support for the per-gui, per-item, and per-entity systems to have RPC Icon Support
* Added support for transferring a Simple RPC (By HypherionSA) config to CraftPresence (With permission, of course!)
* Removed the ViveCraft Message Option and Fallback Placeholder Message
  * Alpha Note: The ViveCraft Option will be replaced by something later in the development pipeline before v2.0s full release!
* UUIDs are now refreshed in the Entity Module List when the Server's Player List changes
    * This prevents a lot of extra elements from coming into the module list, which should keep things cleaner
* Backend: Increased the default text limit for all `ExtendedTextControl`'s
    * Due to this change, minified placeholder support has been removed from the backend (Can be re-added in a future
      update, if needed)
* Added placeholders:
    * `&SERVER:WORLDINFO:WORLDTIME12&` - A 12-hour format of the `&WORLDTIME&` placeholder
    * `&SERVER:PLAYERS:{CURRENT,MAX}EXCL&` - Alternatives for the `&CURRENT&` and `&MAX&` counterparts, but excludes yourself

### Fixes

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

### Translations

The following changes have been made for translations:

* Added:
    * `gui.config.message.editor.original`
    * `gui.config.message.button.remove`
    * `gui.config.{name,defaults}.advanced.allow_endpoint_icons` (Added Property)
    * `gui.config.{name,comment}.advanced.server_icon_endpoint` (Added Property)
    * `craftpresence.placeholders.SERVER.WORLDINFO.WORLDTIME12.description` (Added Placeholder)
    * `craftpresence.placeholders.{SCREEN,TARGETENTITY,RIDINGENTITY}.ICON.description` (Added Placeholders)
* Modified:
    * `gui.config.comment.button.sync.config` (Modified for new config file name)
    * `craftpresence.placeholders.SERVER.WORLDINFO.WORLDTIME.description` (Adjusted description to clarify 24-hour
      format)
* Removed:
    * `craftpresence.logger.error.config.adjust.global`
    * `craftpresence.logger.info.config.notice`
    * `craftpresence.exception.config.prop.null`
    * `gui.config.{name,comment}.advanced.split_character` (Removed Property)

___

### More Information

#### v2.0.0 Upgrade Info

TBD

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
