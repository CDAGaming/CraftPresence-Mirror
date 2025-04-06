# CraftPresence Changes

## v2.5.5 (04/21/2025)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.5.4...release%2Fv2.5.5)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* (Backend) Updated Build Dependencies (Please see the appropriate repositories for changes)
    * Gradle (`8.12` -> `8.13`)
    * Unimined (`1.3.12` -> `1.3.14`)
    * JVMDowngrader (`1.2.1` -> `1.2.2`)
    * Spotless (`6.25.0` -> `7.0.2`)
    * Shadow (`8.3.5` -> `8.3.6`)
* Updated Translations
* Added support for direct-input image URLs in `PresenceData` config sections
    * Allows for using url's directly in the config, without needing to add an icon to Discord OR to the `Dynamic Icons`
      section

### Fixes

* Fixed an issue with SimpleRPC config conversions, if the SimpleRPC config has single-element array entries
* Fixed a broken endpoint url for the template button url config sections
    * There is not a migration layer for this, as the template section is not used in any active RPC data

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
