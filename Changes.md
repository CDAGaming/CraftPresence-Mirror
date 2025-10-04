# CraftPresence Changes

## v2.7.0 (10/24/2025)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.6.2...release%2Fv2.7.0)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* (Backend) Updated Build Dependencies (Please see the appropriate repositories for changes)
    * Fabric Loader (`0.16.14` -> `0.17.2`)
    * Spotless (`7.2.1` -> `8.0.0`)
    * Shadow (`8.3.8` -> `9.2.2`)
    * ModPublisher (`2.1.6` -> `2.1.8`)
    * SpotBugs Annotations (`4.8.6` -> `4.9.6`)
    * UniLib (`1.1.1` -> `1.2.0`)
* UniLib minimum requirement has been bumped to `v1.2.0` due to adjustments to Screen API behaviors
* Updated Modrinth pack integration to support newer launcher versions
* Minor translation changes due to MC 1.21.9 updates from `-Staging`

### Fixes

* Fixed `custom.enabled` not being used in the SimpleRPC config migration layer

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
