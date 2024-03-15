# CraftPresence Changes

## v2.4.0 (05/??/2024)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.3.5...release%2Fv2.4.0)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* (Backend) Updated Build Dependencies (Please see the appropriate repositories for changes)
    * ClassGraph (`4.8.165` -> `4.8.168`)
* (Backend) Added two new UI Widget Types: `ButtonWidget` and `ScrollableTextWidget`
    * `ButtonWidget`: A Row-Style button widget, based on the `TextWidget` implementation
    * `ScrollableTextWidget`: A single-line implementation of `TextDisplayWidget`, also using the new scrolling string
      functions rather then `drawMultilineString`
* Implemented Several frontend UI Improvements:
    * The Controls Screen has been improved to use a `ScrollPane` as well as adding `Reset Key` support
    * Most String UI elements have been migrated from `TextDisplayWidget` to `ScrollableTextWidget` controls

### Fixes

* Fixed incorrect KeyCode widget creation when re-entering Controls Screen
    * Occurs when making a change, then leaving and re-entering the Controls Screen
* (Backend) Fixed missing `super` calls with `TextWidget` controls
* (Backend) Fixed `ScrollPane#checkScrollbarClick()` not checking `needsScrollbar()`
    * This would have a chance to return a false-positive when clicking on the right-side of the pane
* (Backend) Fixed `ScrollPane#getScrollBarWidth()` not checking `needsScrollbar()`
    * Now returns 0 if false; also adjusts several previously mis-aligned UI elements
* (Backend) Fixed an incorrect resize event call on some Minecraft versions
    * Additionally, several UIs have been adjusted to restore widget settings (text, checked status, etc.) upon resizing

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
