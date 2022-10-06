# CraftPresence

Completely customize the way others see you play Minecraft via Discord's Rich Presence API & the DiscordIPC API
by [jagrosh](https://github.com/jagrosh)!

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Crowdin](https://badges.crowdin.net/craftpresence/localized.svg)](https://crowdin.com/project/craftpresence)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/5e0667f7208b49ecab1a6affbfa6cbf7)](https://www.codacy.com/gl/CDAGaming/CraftPresence/dashboard?utm_source=gitlab.com&amp;utm_medium=referral&amp;utm_content=CDAGaming/CraftPresence&amp;utm_campaign=Badge_Grade)
[![Pipeline Status](https://gitlab.com/CDAGaming/CraftPresence/badges/master/pipeline.svg)](https://gitlab.com/CDAGaming/CraftPresence/commits/master)

[![CurseForge-Downloads](https://cf.way2muchnoise.eu/full_craftpresence_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/craftpresence)
[![CurseForge-Availability](https://cf.way2muchnoise.eu/versions/craftpresence.svg)](https://www.curseforge.com/minecraft/mc-mods/craftpresence)

[![Modrinth-Downloads](https://modrinth-utils.vercel.app/api/badge/downloads?id=DFqQfIBR&logo=true)](https://modrinth.com/mod/craftpresence)
[![Modrinth-Availability](https://modrinth-utils.vercel.app/api/badge/versions?id=DFqQfIBR&logo=true)](https://modrinth.com/mod/craftpresence)

## Port Notes

* Some versions of CraftPresence for Minecraft 1.14.x and above require
  the [FabricMC ModLoader](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
* Some versions of CraftPresence for Minecraft 1.13.x require
  the [Rift ModLoader](https://www.curseforge.com/minecraft/mc-mods/rift)
* Versions of CraftPresence for Minecraft 1.1.0 and below
  require [Risugami's ModLoader](https://mcarchive.net/mods/modloader)

## Features

In addition to having the ability to change your discord status from "Playing Minecraft",
This mod offers plenty of customization options to specify entirely how others see you play.
From having your current biome show up, to which dimension your in, as well as which server you're in, and more.
The customization possibilities are limitless, with the only real limit being how creative you customize your display.

### Launcher and Pack Integration Support

CraftPresence will detect whether your Launch Directory contains:

* A valid Twitch/Overwolf/Curse/GDLauncher Manifest (manifest.json, minecraftinstance.json)
* A MultiMC Instance (instance.cfg)
* A MCUpdater Instance (instance.json)
* A Technic installedPacks File (installedPacks)

If using any of these launchers, it'll put the packs name in your display as well as show its icon (when not in a
server).

Note: In v1.6.0 and above, it'll instead parse the Pack's name into the `&PACK&` placeholder, that you can configure for
usage in the RPC

As an example, this is how the mod will convert a pack's name to an iconKey:

Example: `All the Mods 7` would parse as `allthemods7`

Note: MultiMC natively has an Icon Key Property that is used instead of converting from the Pack's Display Name

## Commands

CraftPresence currently offers the following Commands:

Keep in mind the following:

* Commands must be prefixed by either `/craftpresence` or `/cp`
* In v1.5.0 and above, these commands are only usable via the Commands Gui, found within the Config Gui

___

* `/cp view` - Help command to display the commands available to view and control a variety of display data
* `/cp reload` - Reloads mod data (In v1.4.8 and Above, this forces a Tick Event)
* `/cp reboot` - Reboots the RPC
* `/cp shutdown` - Shutdown the RPC (Can be turned on from `/cp reboot`)
* `/cp request` - View Join Request Info
* `/cp view currentData` - Displays your Current RPC Data, in text form
* `/cp view assets (custom | all)` - Displays all asset icon keys available to you
* `/cp view dimensions` - Displays all Dimension Names available for use, requires `Show Current Dimension` to be
  enabled
* `/cp view biomes` - Displays all Biome Names available for use, requires `Show Current Biome` to be enabled
* `/cp view servers` - Displays all Server Addresses available for use, requires `Show Game State` to be enabled
* `/cp view screens` - Displays all Gui Names available for use, if Per-Gui is enabled
* `/cp view items` - Displays all Item Names available for use, if Per-Item is enabled
* `/cp view entities` - Displays all Entity Names available for use, if Per-Entity is enabled
* `/cp (help | ?)` - Help Command to display the above commands and these explanations

## KeyBinds

CraftPresence currently contains the following KeyBinds:

Notes:

* In v1.5.5 up to v1.8.0, KeyBinds are now customized in the Accessibility Settings in the Config Gui, and not the
  normal controls menu
* In v1.8.0 and above, KeyBinds can now be customized in either the dedicated menu in the Config Gui or the normal
  control menu on applicable versions

___

* `Open Config Gui` - KeyBind to open the CraftPresence Config Gui (Default: GRAVE/TILDE Key)

## Placeholders

In some configuration areas, CraftPresence provides some placeholders to make things easier:

Keep in mind the following:

* In v1.6.0 and above, you can now define where in the Rich Presence the messages should go
* Placeholders are not case-sensitive, but should be entered lowercase to prevent issues with recognizing them on v1.5.x
  and below
* As of v1.6.8, you can now also use minified versions of placeholders, which are trimmed down to a length of 4; `&DIM&`
  and `&DIMENSION&` are the same in this case
* As of v1.9.0, you can now use the inner-placeholders of modules in a sub-argument format, such as `&SERVER:IP&`, to
  allow using a module's placeholders in multiple areas without the global placeholder

___

### Global Placeholders

These placeholders are used in the `Presence Settings` menu within the Config Gui.

* `&MAINMENU&` - The message to display whilst in the main menu.
    * See `Status Messages::Main Menu Message` for more info.
* `&PACK&` - The message to display whilst using a valid modpack.
    * See `Status Messages::Modpack Message` for more info.
* `&DIMENSION&` - The Dimension Message, if in use.
    * See `Dimension Messages` for more info.
* `&BIOME&` - The Biome Message, if in use.
    * See `Biome Messages` for more info.
* `&SERVER&` - The Server/SinglePlayer Message, if in use.
    * See `Server Messages` (Or `Status Messages::Singleplayer Game Message`) for more info.
* `&SCREEN&` - The Gui Screen Message, if in use.
    * See `Advanced Settings::Gui Messages` for more info.
* `&TILEENTITY&` - The TileEntity (Block/Item) Message, if in use.
    * See `Advanced Settings::Item Messages` for more info.
* `&TARGETENTITY&` - The Targeted Entity Message, if in use.
    * See `Advanced Settings::Entity Target Messages` for more info.
* `&RIDINGENTITY&` - The Riding Entity Message, if in use.
    * See `Advanced Settings::Entity Riding Messages` for more info.

___

### General Placeholders

As these placeholders are global, they can be set in any of the RPC fields within `Presence Settings` as well as
anywhere else in the config, without needing any extra formatting.

They can also be customized at a deeper level via their sub-placeholders, where applicable.

* `&BRAND&` - The minecraft branding label, displayed as interpreted by minecraft.
* `&MCVERSION&` - The minecraft version, displayed as interpreted by minecraft.
* `&IGN&` - The non-world player info message.
    * See `Status Messages::Player Outer Info` for more info.
* `&MODS&` - The message to display with your mod count.
    * See `Status Messages::Mods Placeholder` for more info.

___

### Biome Placeholders

These placeholders translate to the `&BIOME&` Global Placeholder in the `Presence Settings` menu within the Config Gui.

You can configure these Sub-Placeholders throughout the `Biome Messages` area of the Config Gui.

If you wish to use these placeholders on their own, you can do so via the `&BIOME:[placeholderName]&` format.

Example: `&BIOME:BIOME&` == `&BIOME&`

* `&BIOME&` - The Current Biome Name
* `&ICON&` - The Default Biome Icon Name

___

### Dimension Placeholders

These placeholders translate to the `&DIMENSION&` Global Placeholder in the `Presence Settings` menu within the Config
Gui.

You can configure these Sub-Placeholders throughout the `Dimension Messages` area of the Config Gui.

If you wish to use these placeholders on their own, you can do so via the `&DIMENSION:[placeholderName]&` format.

Example: `&DIMENSION:DIMENSION&` == `&DIMENSION&`

* `&DIMENSION&` - The Current Dimension Name
* `&ICON&` - The Default Dimension Icon Name

___

### Server/LAN Message Placeholders

These placeholders translate to the `&SERVER&` Global Placeholder in the `Presence Settings` menu within the Config Gui.

You can configure these Sub-Placeholders throughout the `Server Messages` area of the Config Gui.

If you wish to use these placeholders on their own, you can do so via the `&SERVER:[placeholderName]&` format.

Example: `&SERVER:IP&` == `&IP&`

* `&PLAYERINFO&` - Your in-world player info message
    * See `Status Messages::Player Inner Info` for more info.
* `&WORLDINFO&` - Your in-world game info message
    * See `Status Messages::World Data Placeholder` for more info.
* `&IP&` - The Current Server IP Address
* `&NAME&` - The Current Server Name
* `&MOTD&` - The Current Server MOTD (Message of The Day)
* `&PLAYERS&` - The Current Player Count `(10 / 100 Players)`
  * See `Status Messages::Player List Placeholder` for more info.
* `&ICON&` - The Default Server Icon Name

___

### Server Player List Placeholders

These placeholders translate to the `&PLAYERS&` Placeholder from the `Server Settings` menu within the Config Gui.

You can configure these Sub-Placeholders within the `Status Messages::Player List Placeholder` setting.

If you wish to use these placeholders on their own, you can do so via the `&SERVER:PLAYERS:[placeholderName]&` format.

Example: `&SERVER:PLAYERS:CURRENT&` == `&CURRENT&`

* `&CURRENT&` - Current player count
* `&MAX&` - Maximum player count

___

### Singleplayer Placeholders

These placeholders translate to the `&SERVER&` Global Placeholder in the `Presence Settings` menu within the Config Gui.

You can configure these Sub-Placeholders within the `Status Messages::Singleplayer Message` setting.

If you wish to use these placeholders on their own, you can do so via the `&SERVER:[placeholderName]&` format.

Example: `&SERVER:IP&` == `&IP&`

* `&PLAYERINFO&` - Your in-world player info message
    * See `Status Messages::Player Inner Info` for more info.
* `&WORLDINFO&` - Your in-world game info message
    * See `Status Messages::World Data Placeholder` for more info.

___

### Outer Player Info Placeholders

These placeholders translate to the `&IGN&` Placeholder from the `Presence Settings` menu within the Config Gui.

You can configure these Sub-Placeholders within the `Status Messages::Player Outer Info` setting.

If you wish to use these placeholders on their own, you can do so via the `&IGN:[placeholderName]&` format.

Example: `&IGN:NAME&` == `&NAME&`

* `&NAME&` - Your username
* `&UUID&` - Your UUID (Trimmed Format)
* `&UUID_FULL&` - Your UUID (Full Format, if valid UUID)

___

### Inner Player Info Placeholders

These placeholders translate to the `&PLAYERINFO&` Placeholder from the `Server Settings` menu (Or `Status Messages::Singleplayer Game Message`) within the Config Gui.

You can configure these Sub-Placeholders within the `Status Messages::Player Inner Info` setting.

If you wish to use these placeholders on their own, you can do so via the `&SERVER:PLAYERINFO:[placeholderName]&` format.

Example: `&SERVER:PLAYERINFO:COORDS&` == `&COORDS&`

* `&COORDS&` - The player's coordinate placeholder message
  * See `Status Messages:Player Coordinate Placeholder` for more info.
* `&HEALTH&` - The player's health placeholder message
  * See `Status Messages:Player Health Placeholder` for more info.

___

### World Info Placeholders

These placeholders translate to the `&WORLDINFO&` Placeholder from the `Server Settings` menu (Or `Status Messages::Singleplayer Game Message`) within the Config Gui.

You can configure these Sub-Placeholders within the `Status Messages::World Data Placeholder` setting.

If you wish to use these placeholders on their own, you can do so via the `&SERVER:WORLDINFO:[placeholderName]&` format.

Example: `&SERVER:WORLDINFO:DIFFICULTY&` == `&DIFFICULTY&`

* `&DIFFICULTY&` - The current world's difficulty
* `&WORLDNAME&` - The name of the current world
* `&WORLDTIME&` - The current world's in-game time
* `&WORLDDAY&` - The current world's in-game day count

___

### Gui Placeholders

These placeholders translate to the `&SCREEN&` Global Placeholder in the `Presence Settings` menu within the Config Gui.

You can configure these Sub-Placeholders throughout the `Advanced Settings::Gui Messages` area of the Config Gui.

If you wish to use these placeholders on their own, you can do so via the `&SCREEN:[placeholderName]&` format.

Example: `&SCREEN:SCREEN&` == `&SCREEN&`

* `&SCREEN&` - The Current Gui Screen Name (Supports `Container` and `Screen` type interfaces)
* `&CLASS&` - The Current Gui Class Name (Ex: The `xxx` part of `net.minecraft.xxx`)

___

### Item Placeholders

These placeholders translate to the `&TILEENTITY&` Global Placeholder in the `Presence Settings` menu within the Config
Gui.

You can configure these Sub-Placeholders throughout the `Advanced Settings::Item Messages` area of the Config Gui.

* `&MAIN&` - The Current Item your Main Hand is Holding
* `&OFFHAND&` - The Current Item your Off Hand is Holding
* `&HELMET&` - The Current Helmet Armor Piece you have Equipped
* `&CHEST&` - The Current Chest Armor Piece you have Equipped
* `&LEGS&` - The Current Leggings Armor Piece you have Equipped
* `&BOOTS&` - The Current Boots Armor Piece you have Equipped

___

## Entity Placeholders

These placeholders translate to the `&TARGETENTITY&` and `&RIDINGENTITY&` Global Placeholder in
the `Presence Settings` menu within the Config Gui.

You can configure these Sub-Placeholders throughout the `Advanced Settings::Entity [Target,Riding] Messages`
area of the Config Gui.

If you wish to use these placeholders on their own, you can do so via
the `&[TARGETENTITY|RIDINGENTITY]:[placeholderName]&` format.

Example: `&[TARGETENTITY|RIDINGENTITY]:ENTITY&` == `&ENTITY&`

* `&ENTITY&` - The Entity Name

Additionally, these sub placeholders support nbt data, where in this case the Entity's nbt data is parsed into
sub-placeholders (Outlined in Tooltips)

## Versions of CraftPresence

Beginning in v1.5.2, CraftPresence is now split into different editions, based on the Minecraft Version you use it in:

* Legacy Version (Minecraft 1.2.5 and Below):
    * Server Support is unavailable in 1.2.5 and Below (Only SinglePlayer will work with showGameStatus enabled)
    * Minecraft 1.1.0 and below may not work on Forge, and may require a Modified Minecraft Jar with Risugami's
      ModLoader + ModLoaderMP
    * Alpha 1.1.2_01 has its Dimension and Biome Modules disabled, since the logic for that was not present in the game
      until a1.2.x

**Support for issues related to Vanilla code, Forge, or older/deprecated ModLoaders is extremely limited**

## Disclaimers & Additional Info

### Minecraft Object Obfuscation

Due to obfuscation in Minecraft, some of Minecraft Objects such as Screens, Dimensions, or Servers must be opened once
in the session to be separately customized.

This has been resolved in v1.8.12 for MC 1.12.x and below, due to newer Translation Logic, but some issues may still be
present.

### Icon Requesting

Not seeing an Icon you like or have a suggestion for an Icon to add/modify on the default Client ID?

If so, you can make a request on my [Issue Tracker](https://gitlab.com/CDAGaming/CraftPresence/issues/), with the
following requirements:

* If adding an Icon from a dimension, specify the Mod's link that the dimension derives from
    * This is because specific Icon IDs must be used, which can be found by the mod or from checking your Logs/Chat
      after entering the dimension as CraftPresence will tell you the ID expected
* An Icon of size between `512x512` and `1024x1024` to be used (Either minimum or recommended size for best quality)
    * Icons between these sizes can be used, but may not give a great final quality
* If requesting an icon to be modified or removed from the Default Client ID, please specify a reason why
    * Mostly just so it can be logged why it was changed for future reference

Additionally, you can also create your own Set of Icons by
following [this guide](https://gitlab.com/CDAGaming/CraftPresence/-/wikis/Making-your-Own-Client-ID-+-Using-your-own-Images)

### Support

Need some assistance with one of my mods or wish to provide feedback?

I can be contacted via the following methods:

* [Email](mailto:cstack2011@yahoo.com)
* [CurseForge](https://www.curseforge.com/minecraft/mc-mods/craftpresence)
* [Discord :: ![Discord Chat](https://img.shields.io/discord/455206084907368457.svg)](https://discord.com/invite/BdKkbpP)

Additionally, codebase documentation for this mod is
available [here](https://cdagaming.gitlab.io/craftpresence-documentation/) with further guides available
on [the wiki](https://gitlab.com/CDAGaming/CraftPresence/-/wikis/Home)

#### Licensing

This Mod is under the MIT License as well as the Apache 2.0 License

This project makes usage of the following dependencies internally:

* [DiscordIPC API](https://github.com/jagrosh/DiscordIPC) by [jagrosh](https://github.com/jagrosh)
    * [JUnixSocket](https://github.com/kohlschutter/junixsocket) by [kohlschutter](https://github.com/kohlschutter)
* [Google's Guava Api](https://github.com/google/guava) by [Google](https://github.com/google/)
* [Java Native Access (JNA) API](https://github.com/java-native-access/jna) on v1.5.x and Below

#### Discord Terms of Service

As with other RPC Mods, this Mod uses your in-game data to send display information to a 3rd party service (In this
Case, Discord).

The terms of service relating to Creating a Discord ID for icons can be
found [here](https://discord.com/developers/docs/legal)

The terms of service for using Discord as a service can additionally be located [here](https://discord.com/new/terms)
