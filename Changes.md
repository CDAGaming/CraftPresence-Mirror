# CraftPresence Changes

## v2.4.1 (??/??/2024)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.4.0...release%2Fv2.4.1)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* (Backend) Updated Build Dependencies (Please see the appropriate repositories for changes)
    * Shadow (`com.github.johnrengelman.shadow@8.1.1` -> `io.github.goooler.shadow@8.1.7`)
    * Gradle (`8.7` -> `8.8`)
    * Unimined (`1.2.7` -> `1.2.9`)
    * JVMDowngrader (`0.7.1` -> `0.7.2`)
    * UniCore (`1.1.9` -> `1.1.10-SNAPSHOT`)
    * DiscordIPC (`0.9.1` -> `0.9.2-SNAPSHOT`)
    * Starscript (`0.3.3` -> `0.3.4-SNAPSHOT`)

### Fixes

* TBD

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
