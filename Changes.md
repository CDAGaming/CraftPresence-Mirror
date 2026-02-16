# CraftPresence Changes

## v2.7.1 (03/02/2026)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.6.2...release%2Fv2.7.0)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* (Backend) Updated Build Dependencies (Please see the appropriate repositories for changes)
    * Unimined (`1.3.14` -> `1.0.5+1.4.2-SNAPSHOT`)
    * JVMDowngrader (`1.2.2` -> `1.3.6`)
    * Spotless (`8.0.0` -> `8.2.1`)
    * Shadow (`9.2.2` -> `9.3.1`)
    * SpotBugs Annotations (`4.9.6` -> `4.9.8`)
    * ASM (`9.8` -> `9.9.1`)
    * Fabric Loader (`0.17.2` -> `0.18.4`)
    * UniCore (`1.3.3` -> `1.3.4`)
    * LenniReflect (`1.5.0` -> `1.6.2`)
    * ClassGraph (`4.8.181` -> `4.8.184`)
    * Gradle (`8.14.3` -> `9.2.1`)
    * DiscordIPC (`0.10.6` -> `0.11.3`)
* Reduced file size from de-duplicating dependencies from `UniLib`
* Added support for several new Rich Presence fields
    * `Status Display Type` => Allows determining whether to display the `Name`, `State`, or `Details` section of the RPC as the short activity text
    * URL Support for `State`, `Details`, `Large Image`, and `Small Image`
    * `Application Name` => Allows for setting a custom name for the Discord Application being used
* Updated the Rich Presence Visualizer to better represent what Discord would display
    * Both Highlight and URL support have also been added, for the new RPC fields

### Fixes

* Fixed cases of the rich presence being unable to close once the game was exited, due to a Discord API change

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
