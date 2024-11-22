# CraftPresence Changes

## v2.5.2 (11/26/2024)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.5.1...release%2Fv2.5.2)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* (Backend) Updated Build Dependencies (Please see the appropriate repositories for changes)
    * Lenni Reflect (`1.3.4` -> `1.4.0`)
    * JVMDowngrader (`1.1.3` -> `1.2.1`)
    * ClassGraph (`4.8.177` -> `4.8.179`)
    * UniLib (`1.0.3` -> `1.0.4`)
    * ASM (`9.7` -> `9.7.1`)
    * Fabric Loader (`0.16.5` -> `0.16.9`)
    * Commons Compress (`1.26.1` -> `1.27.1`)
    * Gradle (`8.10.2` -> `8.11`)
    * Shadow (`8.3.3` -> `8.3.5`)
    * DiscordIPC (`0.10.1` -> `0.10.2`)

### Fixes

* (Backend) Fixed a misc. build config issue preventing some branches from compiling
* Stability Fixes for `Server` Module for MC 1.2.5 Clients and below
    * Server IP and Port are now retrieved from the correct location
    * `ServerList` and `ServerData` behaviors corrected down to MC b1.7.3
    * In General, users from MC b1.8.1 to 1.2.5 should have their `Server` module be fully functional
    * Users below b1.7.3 will still have fixes for proper IP and Port reading, so the module is usable

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
