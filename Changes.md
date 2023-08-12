# CraftPresence Changes

## v2.2.2 (09/01/2023)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.2.1...release%2Fv2.2.2)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* Pack Integration Changes:
    * The Modrinth Launcher is now supported! (Read more about it [here](https://blog.modrinth.com/p/launcher))
    * The `pack.icon` placeholder will now fall back to the `pack.type` placeholder value if the originally requested
      icon does not exist
* Backend Core API Improvements (`FileUtils` and `TranslationUtils`)
    * Adjusted Mod Initialization to allow "Developer Mode" logging to properly display again
    * New Functions have been added to provide more usability to some utilities, as well as to resolve issues
      with `special/xxx` builds

### Fixes

* Fixed an issue with the Default KeyBinding in the MC Controls Gui being improperly set for keybinds registered
  through `KeyUtils`
    * For developers: Use `KeyUtils#createKey` for creating the MC KeyBinding or refer to the Detailed Changelog for
      more info
* Fixed an Issue with syncing `KeyUtils` keybindings from the Mod Config to Vanilla
    * For developers: Use `KeyUtils#setKey` to resolve this issue, which now resets the Key Hash to properly match,
      similar to how the MC Controls Gui operates for setting or resetting keybindings
* Fixed a `NullPointerException` relating to adding `TranslationManager` modules
    * Caused from a missing null check for the `ModUtils#RAW_TRANSLATOR` instance
* Fixed an incorrect order with `syncArgument` calls in `DiscordUtils#setup`
    * This had a rare chance to lock-up and eventually crash the mod, for some reason
* Fixed a possible `NullPointerException` in mod initialization
    * Caused by a missing null check in `CommandUtils#updateModes`

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
