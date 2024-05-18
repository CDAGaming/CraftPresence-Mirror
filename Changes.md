# CraftPresence Changes

## v2.4.0 (06/04/2024)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.3.9...release%2Fv2.4.0)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* (Backend) Updated Build Dependencies (Please see the appropriate repositories for changes)
    * Unimined (`1.2.3` -> `1.2.6-SNAPSHOT`)
    * JVMDowngrader (`local` -> `0.4.0`)
    * ModPublisher (`2.1.1` -> `2.1.2`)
    * Fabric Loader (`0.15.10` -> `0.15.11`)
    * Lenni Reflect (`1.3.2` -> `1.3.4`)
    * UniCore (`1.0.10` -> `1.1.3`)
    * DiscordIPC (`0.8.7` -> `0.8.8`)
* Adjusted the way `Dimension` and `Biome` module data is loaded in MC 1.16+
    * These changes are designed to support auto-locating data related to data pack additions
    * These changes also resolve issues where repeated Registry Lookups could cause a crash in both modules
* Added a new script function, `getComponent`, to support the new `DataComponent` system introduced in MC 1.20.5+
    * Usage: `getComponent(data=DataComponentHolder, path=String)`
    * On versions below MC 1.20.5, an error will appear instead
* (Backend) The default formatting for a slider's display value is now `%.1f` instead of a raw append
* Added a `Sync End Color` button to the `Color Editor` UI
    * Pressing this button, will set the `endData` to the current `startColor` data, effectively useful for quickly
      removing the `endColor` data from the final saving
* Adjusted Mod Initialization in MC 1.7.10 and below to better detect (and avoid) running the mod on server-side
    * This uses a similar system implemented for MC 1.13+ in v2.2.4
    * This change is only applied for Forge, Risugami ModLoader, or users on similar modloaders
* Added a new `Advanced` Config Setting to toggle `ClassGraph` functionality
    * This option allows disabling several backend functions, improving memory usage at the cost of some module
      functionality
    * Disabling this feature will prevent module auto-lookup functionality in some cases, but is set by default to be
      disabled in response to several concerns over memory usage
* (Backend) Adjusted Module Auto-Retrieval Processes for improved usability and error prevention
    * `Module#getAllData` has been split into `Module#getInternalData` and `Module#getConfigData`
    * `Module#scanForData` has been similarly split up, with separate threads now spawned for each part of the
      auto-lookup
    * `Module#canFetchData` has been split into `Module#canFetchInternals` and `Module#canFetchConfig`
    * `Module#hasScanned...` and `Module#queue...` functions have also been added for configuring when to scan for what
      data
    * To align better with MC 1.20.5 and above, the scan order has been inverted for modules, with `config` data being
      scanned first before `internal` data
* (Backend) Added `printException` for Config Modules, now used in `setProperty`
    * These exceptions will only display while `Debug Mode` is enabled
* Adjusted the way the `general.mods` placeholder is calculated (`Constants#getModCount`)
    * Now uses a `Supplier` from each implemented loader rather than Reflection
      Statements (`Constants#MOD_COUNT_SUPPLIER`)
    * Quilt now falls back to `getRawModCount` due to not having an independent build layer
    * Added support for Rift, Flint, and Risugami ModLoader mod counts
    * Fixes placeholder functionality for Forge 1.13+

### Fixes

* (Backend) Modified `ExtendedScreen#getButtonY` to properly respond to `ScrollPane` padding and to better clarify the
  logic
    * For screens where this is used, this places UI elements in the proper position comparable to v1.x versions
    * Old Calculation: `(40 + (25 * (order - 1)))`
    * New Calculation `topPosition + (DEFAULT_ELEMENT_PADDING * (order + 1)) + (DEFAULT_ELEMENT_HEIGHT * order)`
* (Backend) Fixed an issue where `ColorData#setEndColor` and `ColorData#setTexLocation` could be applied incorrectly
    * This fix was previously present in `ColorEditorGui#setCurrentData` but has been moved into `ColorData` to remove
      duplicated logic and to resolve some edge-cases
* Fixed redundant formatting in `Color Editor` Slider UI elements
    * The RGBA sliders now use `Integer` formatting instead of `Float`
    * The `Tint Factor` slider now uses a percentage value instead of `Float`
* Fixed edge-cases in the `Color Editor` where editing a null `endColor` after editing `startColor` would result in an
  incorrect result
    * This is caused from the `getEnd` call pulling the `startColor` if null, and only adjusting the value of one slider
      rather than all four
    * An additional case has been resolved where the `endColor` data wasn't being created, if the new `startColor`
      differs when it didn't before when using `setStartColor`
* (Backend) Fixed memory leaks that could occur through repeated `Module#toString` calls
    * This occurs due to this function utilizing GSON, and since we used this in `Module#hashCode` and `Module#equals`,
      it was being called excessively
    * With this fix, `hashCode` and `equals` functions for all Config Categories should be much more performant,
      following `PresenceData` formatting with `Objects#hash` and `Objects#equals` usage
* (Backend) Fixed memory leaks that could occur through repeated `FileUtils#findValidClass` calls
    * Caching has been added to this method, preventing repetitive calls to `Class#forName`
* (Backend) Fixed a possible discrepancy between using `FileUtils#findValidClass` and `FileUtils#scanClasses`
    * This relates to the `useClassLoader` param in `findValidClass`, which now only defaults to true if below Java 16
    * This behavior matches the behavior used in `ClassGraph` within `scanClasses
* (Backend) Fixed memory leaks that could occur through `Lenni Reflect` reflection operations
    * This issue effects `StringUtils#getFields`, `StringUtils#getMethods`, and methods using these functions
    * Caching has been implemented to the `RStream#of`, `RStream#methods`, and `RStream#fields` functions to avoid
      repetitive allocation
* (Backend) Fixed memory leaks caused from unnecessary `DiscordUtils#removeArguments` calls
    * This issue occurred in `DiscordUtils#syncPlaceholders` in how we were synchronizing `custom.` arguments
    * The old method has been replaced with a new more performant system
      for removals and iteration
    * Several methods have also been adjusted to avoid excessive calls to this function as well
* Fixed the `Sync Config` button in the `Main Gui` not properly applying settings
    * This was caused by the `Config#applyFrom(Config)` function not being called
* (Backend) Fixed an issue where the `Server` module was improperly checking for new `ServerList` entries in some cases
    * This also fixes an issue where adjusting `config` data could trigger an `internal` scan
* (Backend) Fixed an issue where the `ReplayMod` module could be improperly cleared in some cases
* (Backend) Fixed memory leaks that could occur through `MappingUtils` due to excessive `String#replace` operations
    * Both `MappingUtils` and the way the mappings file is generated have been improved to avoid this issue
* (Backend) Fixed memory leaks that could occur through `FileUtils#getClassNamesMatchingSuperType`
  and `FileUtils#isSubclassOf`
    * Both functions have been rewritten to be significantly more performant compared to past releases
* (Backend) Fixed memory leaks that could occur due to excessive Reflection Usage in Config Modules
    * This additionally improves the performance of `Config#handleVerification`
    * This fix primarily effects `Module#getProperty` and `Module#setProperty` usage to be static-defined rather than
      relying on reflection
* Fixed an issue where `overrides.` placeholders could still be added, even if there was no override `PresenceData`
  defined
* Fixed an issue where empty parts of the `Presence Editor` could be ignored if `useAsMain` was false
    * This change may affect configs relying on this for `overrides.` placeholders, please check and adjust your
      settings
* (Backend) Fixed some instances of excessive calls to `DiscordUtils#syncArgument`
    * Better checks to ensure the object has changed have been implemented to prevent excessive calls to this function
    * Further changes are planned for upcoming releases in order to further increase the performance of this function

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
