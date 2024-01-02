# CraftPresence Changes

## v2.3.0 (??/??/2024)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.2.6...release%2Fv2.3.0)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* (Backend) Updated Build Dependencies (Please see the appropriate repositories for changes)
    * Unimined (`1.1.0` -> `1.1.1`)
* (Backend) Major API Refactors involving `StringUtils`, `RenderUtils`, and various other areas
    * View the Full Changelog for more details regarding these changes
* Modified the `stripTranslationColors` config setting into two: one for colors, and one for special
  formatting (`stripTranslationFormatting`)
    * This adjusts the limiter placed on MC 1.1.0 and below so that text *colors* are now allowed, but special
      formatting is not
    * This also adjusts various APIs, in addition to two new script functions: `stripFormatting`
      and `stripAllFormatting(input)`

### Fixes

* (Backend) Fixed an issue where `TextDisplayWidget`, `MessageGui`, and `AboutGui` String Rendering could result in
  incorrect results
    * The `ExtendedScreen#renderNotice` methods have been deprecated, with the features being replaced
      by `RenderUtils#drawMultilineString` improvements
    * `TextDisplayWidget` objects now have an additional fix where `padding` is accounted for in the final height
      calculation
* (Backend) Fixed an issue where `ScrollPane#getOffset` would have an incorrect result if there was no scrollbar
    * This method now returns the parent screen offset in this case
* (Backend) Fixed a visual issue in `RenderUtils#drawMultilineString` where the `fontHeight` was not properly considered
    * This uses an actual `fontHeight + 1` calculation, rather than a magic number (`10`)
    * This will cause tooltips in various MC versions to look different, but more correctly positioned
* (Backend) Fixed several issues related to Text Boxes in MC 1.1.0 and below
  * `ExtendedTextControl#setControlMessage` now properly sets the text, resolving an issue where the cursor position was not updated
  * Fixed the `CTRL+A` event not matching Vanilla MC
  * Fixed a visual bug where clicking the text box was not clearing the `isTextSelected` flag
  * Fixed the selection background not matching Vanilla MC Implementation
  * Fixed the `_` and `|` cursor rendering not matching Vanilla MC Implementation

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
