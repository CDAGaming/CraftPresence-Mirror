# CraftPresence Changes

## v2.5.1 (09/19/2024)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.5.0...release%2Fv2.5.1)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* (Backend) Updated Build Dependencies (Please see the appropriate repositories for changes)
    * Unimined (`1.3.4` -> `1.3.9`)
    * JVMDowngrader (`1.0.0` -> `1.1.3`)
    * Shadow (`8.1.8` -> `8.3.0`)
    * ClassGraph (`4.8.174` -> `4.8.175`)
    * UniLib (`1.0.1` -> `1.0.2`)
    * Fabric Loader (`0.15.11` -> `0.16.5`)
* Added a new `useClassLoader` option in `Advanced` settings
    * This setting only effects the results of `FileUtils#loadClass` when using certain reflection functions while
      creating RPC placeholders
    * This option will reset to its default when transferring a config to a new Minecraft Version
    * This option defaults to `true` if below Java 16, and `false` otherwise

### Fixes

* Removed the `quilted_fabric_api` requirement from Quilt Jars
    * Dependencies are now JIJd the same way as Fabric Jars are

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
