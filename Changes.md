# CraftPresence Changes

## v2.0.5 (06/03/2023)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.0.0...release%2Fv2.0.5)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* Backend: Rewritten Classpath Scanning to fully utilize [Classgraph](https://github.com/classgraph/classgraph)
    * Several APIs in `FileUtils` and `MappingUtils` have been revised for this change
    * Fixes Issues related to [this ticket](https://gitlab.com/CDAGaming/CraftPresence/issues/192)
    * Improved performance and stability in the Per-GUI module, as well as the Dimension/Biome modules (On certain MC
      Versions)
    * Removed the ability for the Dimension/Biome Modules, on certain MC versions, to create WorldProviders
      via `.newInstance()` due to the logic being error-prone and causing various incompatibilities
    * Removed the `data.screen.class` placeholder (Use `getClass(data.screen.instance)` instead)

### Fixes

* Fixed error spam when using `RenderUtils#drawItemStack` under certain Blocks/Items
    * Errors now only display if in Verbose Mode
    * A blacklist has also been added to the backend to prevent repeated failed renders

___

### More Information

#### Known Issues

Despite configuration compatibility being ensured between v1.8.x/v1.9.x and v2.0,
caution is advised to ensure the best experience, while also baring in mind that features can be adjusted, removed, or
added/iterated upon between releases.

The following known issues are present in this build:

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
