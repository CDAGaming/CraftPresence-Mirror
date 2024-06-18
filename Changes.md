# CraftPresence Changes

## v2.4.1 (06/18/2024)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.4.0...release%2Fv2.4.1)_

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
* The Simple RPC Config Migration Layer (`HypherConverter`) is now supported on MC 1.6.4 and below
    * Certain Settings, such as the `realms_list` event, will not migrate on these versions
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

___

### More Information

#### Known Issues

Despite configuration compatibility being ensured between v1.8.x/v1.9.x and v2.0,
caution is advised to ensure the best experience, while also baring in mind that features can be adjusted, removed, or
added/iterated upon between releases.

The following known issues are present in this build:

* The HypherionMC Config Layer (To Convert a Simple RPC config to CraftPresence) contains the following known issues:
    * Placeholders related to the realm event are currently unimplemented and parse as `{''}`.

Please refer to the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) to view more info relating
to known issues.

#### Snapshot Build Info

Some Versions of this Mod are for Minecraft Snapshots or Experimental Versions, and as such, caution should be noted.

Any Snapshot Build released will be marked as **ALPHA** to match its Snapshot Status depending on tests done before
release
and issues found.

Snapshot Builds, depending on circumstances, may also contain changes for a future version of the mod, and will be noted
as so if this is the case with the `-Staging` label.
