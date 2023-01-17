# CraftPresence

Completely customize the way others see you play Minecraft via Discord's Rich Presence API & the DiscordIPC API
by [jagrosh](https://github.com/jagrosh)!

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Crowdin](https://badges.crowdin.net/craftpresence/localized.svg)](https://crowdin.com/project/craftpresence)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/5e0667f7208b49ecab1a6affbfa6cbf7)](https://www.codacy.com/gl/CDAGaming/CraftPresence/dashboard?utm_source=gitlab.com&amp;utm_medium=referral&amp;utm_content=CDAGaming/CraftPresence&amp;utm_campaign=Badge_Grade)
[![Pipeline Status](https://github.com/CDAGaming/CraftPresence-Mirror/actions/workflows/build.yml/badge.svg?branch=master)](https://gitlab.com/CDAGaming/CraftPresence/commits/master)

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

* `/cp compile "<expr>"` - Test the output of a placeholder expression, via Starscript
* `/cp search (type:typeName, <searchTerm>, all)` - Search for valid placeholders available to use with Rich Presence
* `/cp reload` - Reloads mod data
* `/cp request` - View Join Request Info
* `/cp export` - View export commands for mod data
* `/cp view` - Help command to display the commands available to view and control a variety of display data
    * `/cp view placeholders` - Displays all available placeholders for use in the RPC
    * `/cp view currentData` - Displays your Current RPC Data, in text form
    * `/cp view assets (custom | all)` - Displays all asset icon keys available to you
    * `/cp view dimensions` - Displays all Dimension Names available for use, requires `Show Current Dimension` to be
      enabled
    * `/cp view biomes` - Displays all Biome Names available for use, requires `Show Current Biome` to be enabled
    * `/cp view servers` - Displays all Server Addresses available for use, requires `Show Game State` to be enabled
    * `/cp view screens` - Displays all Gui Names available for use, if Per-Gui is enabled
    * `/cp view items` - Displays all Item Names available for use, if Per-Item is enabled
    * `/cp view entities` - Displays all Entity Names available for use, if Per-Entity is enabled
* `/cp reboot` - Reboots the RPC
* `/cp shutdown` - Shutdown the RPC (Can be turned on from `/cp reboot`)
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

## About Placeholders and Functions

In some configuration areas, CraftPresence provides some placeholders and functions to make things easier:

Keep in mind the following:

* In v2.0.0, placeholders have been rewritten to be compatible
  with [Starscript](https://github.com/MeteorDevelopment/starscript)
    * The older list of this section can be
      viewed [here](https://gitlab.com/CDAGaming/CraftPresence/-/wikis/Legacy-Placeholders-(v1.x))
    * All Placeholders, functions, and code expressions must be surrounded with curly brackets (Example: `{foo.bar}`)
    * In the event that you need to combine a placeholder with other data in a function argument, use the `getResult`
      function
    * Additional functions and standard variables are available
      within [StandardLib](https://github.com/MeteorDevelopment/starscript/wiki)

___

### Placeholder List

The following placeholders are available for use anywhere in CraftPresence:

* General Placeholders:
    * `general.brand` - The Minecraft branding label
    * `general.icon` - The default display icon
    * `general.version` - The Minecraft version label
    * `general.mods` - The amount of mods currently in your mods folder
* Menu Event Placeholders (Loading and Main Menu):
    * `menu.message` - The main menu's display data, while applicable
    * `menu.icon` - The main menu's display icon, while applicable
* Pack Placeholders:
    * `pack.name` - The currently detected pack's name
    * `pack.icon` - The currently detected pack's icon
* Player Placeholders:
    * `player.name` - Your username
    * `player.uuid.short` - Your UUID (Trimmed Format)
    * `player.uuid.full` - Your UUID (Full Format, if valid UUID)
    * `player.icon` - Your player head icon, while applicable
    * `player.position.x` - Your current in-game X position
    * `player.position.y` - Your current in-game Y position
    * `player.position.z` - Your current in-game Z position
    * `player.health.current` - Your current in-game health
    * `player.health.max` - Your current in-game maximum health
* Gui Placeholders:
    * `screen.message` - The current Gui Screen's display data, while applicable
    * `screen.name` - The current Gui Screen name
    * `screen.icon` - The current Gui Screen icon
    * `screen.default.icon` - The default Gui Screen icon
* Biome Placeholders:
    * `biome.message` - The current biome's display data, while in-game
    * `biome.name` - The current biome name
    * `biome.icon` - The current biome icon
    * `biome.default.icon` - The default biome icon
* Dimension Placeholders:
    * `dimension.message` - The current dimension's display data, while in-game
    * `dimension.name` - The current dimension name
    * `dimension.icon` - The current dimension icon
    * `dimension.default.icon` - The default dimension icon
* Entity Placeholders:
    * `entity.default.icon` - The default entity icon
    * `entity.target.message` - The currently targeted entity's display data, while applicable
    * `entity.target.name` - The currently targeted entity's name
    * `entity.target.icon` - The currently targeted entity's icon
    * `entity.riding.message` - The currently riding entity's display data, while applicable
    * `entity.riding.name` - The currently riding entity's name
    * `entity.riding.icon` - The currently riding entity's icon
* World Placeholders:
    * `world.difficulty` - The current world's difficulty
    * `world.name` - The name of the current world
    * `world.time24` - The current world's in-game time (24-hour format)
    * `world.time12` - The current world's in-game time (12-hour format)
    * `world.day` - The current world's in-game day count
* Server Placeholders:
    * `server.message` - The current server's display data, while in-game
    * `server.icon` - The current server icon
    * `server.default.icon` - The default server icon
    * `server.players.current` - The server's current player count
    * `server.players.max` - The server's maximum player count
    * `server.address.full` - (MP) The raw current server address
    * `server.address.short` - (MP) The formatted current server address
    * `server.name` - (MP) The current server name
    * `server.motd.raw` - (MP) The current raw server motd
* Item Placeholders:
    * `item.message.default` - The default item display data, while applicable
    * `item.message.holding` - The held item(s) display data, while applicable
    * `item.message.equipped` - The equipped item(s) display data, while applicable
    * `item.[slotId].name` - Current `slotId` item name
    * `item.[slotId].message` - Current `slotId` item message
* Integration - Replay Mod:
    * `replaymod.time.current` - When in the Video Renderer, retrieves the `renderTimeTaken` field
    * `replaymod.time.remaining` - When in the Video Renderer, retrieves the `renderTimeLeft` field
* Extra Placeholders (Advanced Usage):
    * `_general.instance` - The `Minecraft` Instance
    * `_config.instance` - The Mod Config Instance
    * `_[moduleName].instance` - An instance of one of the modules CraftPresence has
        * Module Order: `biome, dimension, entity, item, screen, server, <...>`
    * `data.entity.target.instance` - An instance of the currently targeted entity
    * `data.entity.target.class` - The class object for the currently targeted entity
    * `data.entity.riding.instance` - An instance of the currently riding entity
    * `data.entity.riding.class` - The class object for the currently riding entity
    * `data.item.[slotId].instance` - An instance of the current `slotId`
    * `data.item.[slotId].class` - The class object for the current `slotId`
    * `data.item.[slotId].[tagName]` - The nbt tag `tagName`, within the current `slotId`, if said NBT exists
    * `data.screen.instance` - An instance of the current Gui Screen
    * `data.screen.class` - The class object for the current Gui Screen
    * `data.server.motd.line.[number]` - Retrieves a specific line of `server.motd.raw`

### Function List

The following functions are available for use anywhere in CraftPresence:

* `getClass(reference=Object|String)` - Attempt to retrieve a class object, via the string path or object reference
* `getOrDefault(target, alternative ?: '')` - Retrieve the primary value if non-empty; Otherwise, use the secondary
  value
* `minify(input, length)` - Reduces the Length of a String to the Specified Length
* `getFirst(args)` - Retrieve the first non-null string from the specified arguments, or return null
* `formatIdentifier(target, formatToId ?: false, avoid ?: false)` - Converts an Identifier into a properly formatted and
  interpretable Name
* `stripColors(input)` - Strips Color and Formatting Codes from the inputted String
* `getField(classObj=Object|String|Class ?: instance.getClass(), instance=Object, fieldName=String)` - Retrieves the
  Specified Field(s) via Reflection
* `isUuid(input)` - Checks via Regex whether the specified String classifies as a valid Uuid
* `hasWhitespace(input)` - Whether the specified string contains whitespace characters
* `getJsonElement(url|jsonString, path=Object...)` - Retrieves the json element from the specified content, or null if
  unable
* `asIcon(input)` - Converts a String into a Valid and Acceptable Icon Format
* `rgbaToHex(r,g,b,a ?: 255)` - Converts the specified RGBA color into a Hexadecimal String
* `convertTime(input, originalPattern, newPattern)` - Convert the specified string into the specified date format, if
  able
* `randomString(args)` - Retrieves a random element from the specified arguments, as a string
* `randomAsset()` - Attempts to retrieve a Random Icon Key from the available assets
* `capitalizeWords(input, timesToCheck ?: -1)` - Capitalizes the words within a specified string
* `executeMethod(classToAccess=Object|String|Class, instance=Object ?: null, methodName, <parameterType, parameter>...)` -
  Invokes the specified Method in the Target Class via Reflection
* `asProperWord(input, avoid ?: false, skipSymbolReplacement ?: false, caseCheckTimes ?: -1)` - Converts input into a
  Properly Readable String
* `replaceAnyCase(input, from, to)` - Replaces Data in a String with Case-Insensitivity
* `hasAlphaNumeric(input)` - Whether the specified string contains alpha-numeric characters
* `isColor(input)` - Determines whether an inputted String classifies as a valid Color Code
* `removeRepeatWords(input)` - Removes Duplicated Words within an inputted String
* `formatAddress(input, returnPort ?: false)` - Formats an IP Address based on Input
* `nullOrEmpty(input, allowWhitespace ?: false)` - Determines whether a String classifies as NULL or EMPTY
* `length(input)` - Returns the length of the specified string
* `toCamelCase(input)` - Converts a String into a Valid and Acceptable Camel-Case Format
* `getResult(input)` - Perform recursive conversion on the specified input
* `hasField(classToAccess, fieldName)` - Retrieves whether the specified class contains the specified field name

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

* [Starscript](https://github.com/MeteorDevelopment/starscript)
  by [MeteorDevelopment](https://github.com/MeteorDevelopment) on v2.0 and above
* [DiscordIPC API](https://github.com/jagrosh/DiscordIPC) by [jagrosh](https://github.com/jagrosh)
    * [JUnixSocket](https://github.com/kohlschutter/junixsocket) by [kohlschutter](https://github.com/kohlschutter)
* [Google's Guava Api](https://github.com/google/guava) by [Google](https://github.com/google/)
* [Java Native Access (JNA) API](https://github.com/java-native-access/jna) on v1.5.x and below

#### Discord Terms of Service

As with other RPC Mods, this Mod uses your in-game data to send display information to a 3rd party service (In this
Case, Discord).

The terms of service relating to Creating a Discord ID for icons can be
found [here](https://discord.com/developers/docs/legal)

The terms of service for using Discord as a service can additionally be located [here](https://discord.com/new/terms)
