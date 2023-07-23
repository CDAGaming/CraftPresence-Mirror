# CraftPresence Changes

## v2.2.0 (07/25/2023)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.1.2...release%2Fv2.2.0)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* Backend: Added a new `core` module, splitting the Mod API into its pure-java segments and game-segments
    * This changes comes with many API changes, rewrites, and adjustments to critical logic
    * Please report any issues with existing features breaking or unexpected crashes
* Backend: Adjusted `TranslationUtils#translateFrom` exception reporting
    * If a parser exception occurs, the raw `translationKey` is early-returned to prevent early logging and cases of
      spam
    * Falling back to the default language has its logging only show up as debug logging, since it isn't necesarily an
      error
* Backend: Removed excess logging relating to `MappingUtils` (`Debug` instead of `Info` level)
* Backend: Adjusted the `/cp export assets` command to always generate a `downloads.txt` file, regardless
  of `doFullCopy` status
* Removed the `showLoggingInChat` option from the `Accessibility` Section
    * This option was hardly used, and hardly iterated on since its original creation in early v1
    * It was removed as a result to the aforementioned refactors

### Fixes

* Fixed various issues with Translations being incorrect in several languages
    * Aditionally, only approved translations are now filtered into the repository
    * Several obsolete translations have also been removed (Or had its existing translation reset)

___

### More Information

#### Known Issues

Despite configuration compatibility being ensured between v1.8.x/v1.9.x and v2.0,
caution is advised to ensure the best experience, while also baring in mind that features can be adjusted, removed, or
added/iterated upon between releases.

The following known issues are present in this build:

* On certain MC versions, Scrolling while in a Scroll List drawing `ItemStack`'s may cause GUI distortions
* Text with colors do not retain those colors if that text moves to a newline in the CraftPresence UIs
* The HypherionMC Config Layer (To Convert a Simple RPC config to CraftPresence) contains the following known issues:
    * Placeholders related to the realm event are currently unimplemented and parse as `{''}`.

#### Snapshot Build Info

Some Versions of this Mod are for Minecraft Snapshots or Experimental Versions, and as such, caution should be noted.

Any Snapshot Build released will be marked as **ALPHA** to match its Snapshot Status depending on tests done before
release
and issues found.

Snapshot Builds, depending on circumstances, may also contain changes for a future version of the mod, and will be noted
as so if this is the case with the `-Staging` label.
