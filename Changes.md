# CraftPresence Changes

## v2.3.9 (04/25/2024)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.3.8...release%2Fv2.3.9)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* (Backend) Updated Build Dependencies (Please see the appropriate repositories for changes)
    * Fabric Loader (`0.15.9` -> `0.15.10`)
    * Unimined (`1.2.0-SNAPSHOT` -> `1.2.0`)
    * Classgraph (`4.8.170` -> `4.8.172`)
    * DiscordIPC (`0.8.6` -> `0.8.7`)
    * UniCore (`1.0.9` -> `1.0.10`)
    * SpotBugs (`4.8.3` -> `4.8.4`)
* Quality of Life UI Improvements
    * Reset and Instance support has been implemented for the `Presence Settings` screen -- additionally allowing for
      content to be preserved through resizing
    * A new `Display Settings` UI has been added to the Main Config GUI, to decouple the Display-Specific options from
      the `Presence Settings` screen (`Dynamic Icons`, `Dynamic Variables`)
    * The `Presence Settings` UI has been renamed to `Presence Editor` and has received several layout updates for a
      cleaner look
    * Removed `Sync Config` support for sub-categories due to recently discovered tech limitations
    * `Accessibility Settings` has received layout improvements as well as adjustments for MC 1.20.5 changes

### Fixes

* Fixed Issues relating to `Reset` and `Sync` config operations in the `ConfigurationGui`
    * `Reset` now properly adjusts the `Instance` data instead of the `Current` data, fixing early changes in
      sub-categories
    * `Sync` now also adjusts the `Instance` data instead of just the `Current` data, fixing false save indicators
* Resolved an issue where `markAsChanged()` was being triggered early in `Presence Settings`
* Fixed a missing tooltip for the `Presence Settings` button in the `DynamicEditorGui`
* Fixed a possible `NullPointerException` that could occur in the `ColorEditorGui`
    * Occurs if the `DEFAULTS` field is `null`
    * The appropriate buttons will now be disabled if these are `null`
* Fixed config data loss related to `ColorEditorGui` and `DisplaySettingsGui` changes
    * This is the same issue that occurred with the `DynamicEditorGui` in v2.3.8

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
