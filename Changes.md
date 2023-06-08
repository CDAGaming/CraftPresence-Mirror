# CraftPresence Changes

## v2.0.7 (06/08/2023)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.0.5...release%2Fv2.0.7)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* Adjusted `enableJoinRequests` to where the option is no longer needed for accepting requests from others
    * The option is now only used for the Party Info which controls whether players can "Ask to Join" and overall Game
      Invite Functionality (IE Being able to create Game Invites yourself for others to join)
    * The Chat Notification for when a Join Request is ignored has been removed
* Adjusted how Modules and Placeholders are Ticked to resolve data preservance flaws
    * Now only polled when the RPC State is valid (IE neither `disconnected` or `invalid`)
    * Module Exceptions are now properly logged rather than being silent
        * These exceptions now appear similar to the compiler/parser error logging
    * In the event of an unrecoverable error, the RPC will now shut down to prevent further issues
        * The user has the ability to reboot the RPC via `/cp reboot` but it will shut down again if issues persist
* Backend: Adjusted ReplayMod Integration to be a reflection-based module
    * This means it is now in the common sourceSet and is using the same logic on both platforms

### Fixes

* Fixed a regression in v2.0.5 causing Biome/Dimension Auto-Lookups to fail in certain circumstances
* Fixed possible cases of Classpath Scanning causing uncaught exceptions
    * Exception visibility is controlled via the verbose mode setting
* Backend: Fixed launching the `CommandsGui` with a prefilled command
    * Constructor now has a `commandArgs` param to do this, do not use the `executeCommand` method
* Fixed a possible `NullPointerException` that can occur with the `world.difficulty` placeholder, related to Hardcore
  Mode
* Backend: Multiple fixes to `DiscordUtils`, `CommandsGui`, and `ServerUtils` to restore Join Request support
    * The `DiscordIPC` dependency has also been bumped to fix an exception for users with no avatar icon

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
