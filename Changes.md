# CraftPresence Changes

## v2.0.10 (06/20/2023)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.0.7...release%2Fv2.0.10)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* Backend: Buildscripts have been overhauled to allow for future enhancements to CraftPresence
    * This includes the possibility of new ports *below* a1.1.2_01
* Backend: Added `NBTLongArray` support in `NBTUtils#parseTag` in MC 1.13+
    * May adjust the output of some `getNbt` parses
* Backend: Adjusted Button detection to resolve an IPC error
    * As per Discord: `secrets cannot currently be sent with buttons`
    * When a `JOIN`, `MATCH`, or `SPECTATE` secret is active, Button Data is now discarded to prevent this issue

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

___

### More Information

#### Known Issues

Despite configuration compatibility being ensured between v1.8.x/v1.9.x and v2.0,
caution is advised to ensure the best experience, while also baring in mind that features can be adjusted, removed, or
added/iterated upon between releases.

The following known issues are present in this build:

* On 1.14+, the `isFocused` state in the GUI module is improperly represented
    * This can cause issues such as the Config GUI opening while typing into another GUI
* On certain MC versions, Scrolling while in a Scroll List drawing `ItemStack`'s may cause GUI Scale distortions for a
  few frames
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
