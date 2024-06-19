# CraftPresence Changes

## v2.4.2 (06/20/2024)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.4.0...release%2Fv2.4.2)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* (Backend) Updated Build Dependencies (Please see the appropriate repositories for changes)
    * Shadow (`com.github.johnrengelman.shadow@8.1.1` -> `io.github.goooler.shadow@8.1.7`)
    * Gradle (`8.7` -> `8.8`)
    * Unimined (`1.2.7` -> `1.2.9`)
    * JVMDowngrader (`0.7.1` -> `0.7.2`)
    * UniCore (`1.1.9` -> `1.1.10`)
    * DiscordIPC (`0.9.1` -> `0.9.2`)
    * Starscript (`0.3.3` -> `0.3.4`)
* Implemented full support for Minecraft Realms Status Configuration
  * Added a new `Realm Message` option to `Status Messages`
  * Adjusted `server` override, `server.message`, and `server.icon` placeholders to use `realmData` instead of `serverData`
  * Adjusted `server.name` and `server.motd` placeholders to use the Realm Name and Description accordingly
  * Adjusted `server.icon` to fall back to the Realm minigame icon, if available
  * Added `server.minigame` as a Realm Exclusive placeholder for the minigame name
* Several changes to the Simple RPC Config Migration Layer (`HypherConverter`)
  * Now supported on MC 1.6.4 and below (Some settings are skipped on some MC versions)
  * Added support for the `%realmname%`, `%realmdescription%`, `%realmgame%`, and `%realmicon%` placeholders
  * Added support for the `realms` event
* Misc. API and Performance Improvements

### Fixes

* Fixed a runtime ASM crash that could occur on Forge clients in MC 1.13.x and 1.14.x
    * This issue could also occur on certain versions of Fabric Loader
* Fixed the "Config" button being unavailable on the Forge Mod Menu on MC 1.13.x and above
    * An error will also be logged if the mod fails to register a config factory
* Fixed an oversight causing the `HypherConverter` to crash in newer-than-supported versions of Simple RPC
    * An error will now be logged when a newer-than-supported version is used, similar to the error for
      older-than-supported version
* Fixed a race-case issue that could cause a `Config` to incorrectly modify default settings when it has no prior data
    * This could cause issues such as the `Reset to Default` button to be unavailable on first launch
* Fixed false-positive save indicators on the `MainUI` when changing keybindings in `ControlsGui`
* Fixed an issue where the Current GUI Screen Name could be null on some MC versions under certain conditions
    * When this occurs, the Screen Name will fall back to `GuiScreen` rather than being an empty string
    * This also resolves empty Selector List entries in the Per-GUI system as well
* Fixed an issue in the `HypherConverter` that could cause the `single_player` and `multi_player` events to be converted incorrectly if `dimension_overrides.enabled` was `true`

___

### More Information

#### Known Issues

Despite configuration compatibility often being ensured between versions,
caution is advised to ensure the best experience, while also baring in mind that features can be adjusted, removed, or
added/iterated upon between releases.

Please refer to the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) to view more info relating
to known issues.

#### Snapshot Build Info

Some Versions of this Mod are for Minecraft Snapshots or Experimental Versions, and as such, caution should be noted.

Any Snapshot Build released will be marked as **ALPHA** to match its Snapshot Status depending on tests done before
release
and issues found.

Snapshot Builds, depending on circumstances, may also contain changes for a future version of the mod, and will be noted
as so if this is the case with the `-Staging` label.
