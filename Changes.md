# CraftPresence Changes

## v2.5.4 (03/??/2025)

_A Detailed Changelog from the last release is
available [here](https://gitlab.com/CDAGaming/CraftPresence/-/compare/release%2Fv2.5.3...release%2Fv2.5.4)_

See the Mod Description or [README](https://gitlab.com/CDAGaming/CraftPresence) for more info regarding the mod.

### Changes

* Updated the SimpleRPC Config Migration Layer for V4 users
    * Schema Versions 24 (`USE_MULTI_RPC`) and 25 (`PAUSE_EVENT`) are now marked as supported
    * Schemas between version 18 and 24 are marked as unsupported and will print a warning to update your config before
      retrying migration
    * Effecting all schemas, conversion for the `%position% / {{player.position}}` placeholder has been adjusted to no
      longer use `custom.player_info_coordinate`
    * In the event that multiple `presence` elements are present for an RPC event, CraftPresence will only convert the
      first one found (This might change in a future update)
    * Effecting all schemas, conversion for the `server_list` event now also applies for the `GuiDisconnected` screen
* Added new placeholders to the `server` module: `world.type` and `server.type`
    * These placeholders retrieve the world type for either a realm or the world, depending on what is used
    * Results may be inaccurate or absent if the server your on does not make that info known to the user
    * The available world types also differ between playing on a realm and on a normal world
* Adjusted forced/event-based RPC functionality (`PresenceData#useAsMain`)
    * When multiple event-based RPC modules are active, the active data will now use the *last* applicable data entry
      instead of the *first*
    * Additionally, an event priority order has been made to ensure proper ordering of events, rather than a randomly
      sorted list that could result in the wrong event
        * Event Order (First->Last):
          `"biome", "dimension", "item", "entity.riding", "entity.target", "server", "menu", "screen"`
        * (Backend) This list is automatically appended with `DiscordUtils#addForcedData`
        * (Backend, Breaking) Usages of `DiscordUtils#removeForcedData` have changed to `clearForcedData`, the
          difference being the latter preserves the event ordering

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
