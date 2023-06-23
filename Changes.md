# CraftPresence Changes

## v2.1.0 (06/27/2023)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.0.7...release%2Fv2.1.0)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* Backend: Numerous System Changes to the RPC and Scripting Engine to allow for proper "offline mode" support
    * These changes are designed to add support for re-connecting to Discord automatically following a disconnect
    * They also allow for configuring the Modules without needing the RPC to be active.
    * Two new options added to `Advanced`: `allowDuplicatePackets` and `maxConnectionAttempts`
* Backend: Buildscripts have been overhauled to allow for future enhancements to CraftPresence
    * This includes the possibility of new ports *below* a1.1.2_01
    * DiscordIPC has also been updated to integrate the Pomelo API Changes (Nicknames + New Username System)
* Backend: Added `NBTLongArray` support in `NBTUtils#parseTag` in MC 1.13+
    * May adjust the output of some `getNbt` parses
* Backend: Adjusted Button detection to resolve an IPC error
    * As per Discord: `secrets cannot currently be sent with buttons`
    * When a `JOIN`, `MATCH`, or `SPECTATE` secret is active, Button Data is now discarded to prevent this issue
* Added a `Copy` Button to the Commands GUI
    * This allows for easily copying the output of a command for easier debugging and overall accessibility
* Added two new function placeholders, for Identifier/ResourceLocation usage
    * `getNamespace(input)` - Retrieve the namespace portion of an Identifier-Style Object
    * `getPath(input)` - Retrieve the path portion of an Identifier-Style Object
* All existing `.time` placeholders now are sync'd as `milliseconds` instead of `seconds`
    * To account for this change going over Starscript's limits, it's type is now a `String`
    * This type can however be converted back into the type that you need it for
* Backend: Adjusted `GuiUtils#isFocused` to include a check for in-world interactions
    * This was mostly an issue in 1.14+ where certain screens did not follow GUI `getFocused` calls
    * To mitigate this, if the focused state is null, but we are in a world, we'll still consider `isFocused` as true
    * This resolves issues of the Config GUI opening in other in-world GUIs, at the cost of no longer allowing in-world
      GUI opening from another UI
* Misc Changes to remove and/or consolodate deprecated/unused data
    * Backend: Removed `loadFileAsDLL` and related translations -- unused since v1.6.0
    * Added a new `general.title` placeholder (`Minecraft {general.version}`) -- `Legacy2Modern` layer also updated
    * Removed the `roundSize` and `includeExtraGuiClasses` Advanced options
    * Moved the `renderTooltips` option from `Advanced` to `Accessibility` (Schema now v6)
    * Adjusted the `Strip Translation Colors` tooltip message when `IS_TEXT_COLORS_BLOCKED` flag is enabled
    * Removed several unused/deprecated translations (See Technical Changelog)
    * Renamed some Translation Identifiers to comply with namespace restrictions

### Fixes

* Fixed extra data being added to `NBTUtils` when parsing an `NBTTagCompound`
    * May adjust the output of some `getNbt` parses
* Fixed multiple bypasses for Icon Formatting Logic (`:` and other symbols)
* Fixed Join Secret Creation to avoid converting data to lowercase
    * Was causing cases of mismatched info (There are also plans to write a more secure secret system)
* Backend: Fixed `ServerUtils#isInvalidMotd` throwing an exception when `serverMotd` was null
    * Primarily occurs with Direct Connections to a server
* Fixed Logging not properly working on select MC versions
* Fixed cases of Log Spam when locating a Discord Image
    * Occurs when Verbose Mode was enabled, and it diverted to a cached icon from a prior lookup
    * This area of code now properly respects the `showLogging` parameter to avoid this
* Misc. UI Rendering Fixes
    * Migrations to `GLStateManager` and `RenderSystem` as necesary
    * Fixed cases of incorrect Render Phases in Scroll Lists, causing some visual discrepencies
* Fixed a regression where Pack Data was being interpreted despite not being enabled
* Fixed a potential `NullPointerException` with `pack.type` in the MultiMC Pack Integration
* Fixed some Line Seperator Characters not being properly normalized in Logging
    * The new check now match the same Regex as the `splitTextByNewLine` method instead of just `\\n`
* Removed the ability for Color Codes to be present in Text Logs
    * This was normally only supposed to be allowed when Logs can be pushed to chat
* Fixed an Issue in Starscript that would cause some placeholders to be unexpectedly removed
* Fixed an Issue where Modules could load earlier than expected in 1.13+

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
