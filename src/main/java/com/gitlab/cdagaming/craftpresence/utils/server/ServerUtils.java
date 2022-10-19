/*
 * MIT License
 *
 * Copyright (c) 2018 - 2022 CDAGaming (cstack2011@yahoo.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.gitlab.cdagaming.craftpresence.utils.server;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.impl.discord.ArgumentType;
import com.gitlab.cdagaming.craftpresence.impl.discord.DiscordStatus;
import com.gitlab.cdagaming.craftpresence.impl.discord.PartyPrivacy;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.resolver.ServerAddress;

import java.util.List;
import java.util.Map;

/**
 * Server Utilities used to Parse Server Data and handle related RPC Events
 *
 * @author CDAGaming
 */
@SuppressWarnings("DuplicatedCode")
public class ServerUtils {
    /**
     * The argument format to follow for Rich Presence Data
     */
    private final String argumentFormat = "&SERVER&";
    /**
     * The sub-argument format to follow for Rich Presence Data
     */
    private final String subArgumentFormat = "&SERVER:";
    /**
     * A Mapping of the Arguments attached to the &SERVER& RPC Message placeholder
     */
    private final List<Pair<String, String>> serverArgs = Lists.newArrayList();
    /**
     * A Mapping of the Arguments attached to the &SERVER& RPC Image placeholder
     */
    private final List<Pair<String, String>> iconArgs = Lists.newArrayList();
    /**
     * A Mapping of the Arguments attached to the &SERVER:PLAYERINFO& RPC Message placeholder
     */
    private final List<Pair<String, String>> playerDataArgs = Lists.newArrayList();
    /**
     * A Mapping of the Arguments attached to the &SERVER:PLAYERS& RPC Message placeholder
     */
    private final List<Pair<String, String>> playerAmountArgs = Lists.newArrayList();
    /**
     * A Mapping of the Arguments attached to the &SERVER:WORLDINFO& RPC Message placeholder
     */
    private final List<Pair<String, String>> worldDataArgs = Lists.newArrayList();
    /**
     * A Mapping of the Arguments attached to the &SERVER:PLAYERINFO:COORDS& RPC Message placeholder
     */
    private final List<Pair<String, String>> coordinateArgs = Lists.newArrayList();
    /**
     * A Mapping of the Arguments attached to the &SERVER:PLAYERINFO:HEALTH& RPC Message placeholder
     */
    private final List<Pair<String, String>> healthArgs = Lists.newArrayList();
    /**
     * A Mapping of the Arguments attached to the &SERVER& RPC Message sub-argument placeholders
     */
    private final Map<String, List<Pair<String, String>>> subArgumentData = Maps.newHashMap();
    /**
     * Whether this module is active and currently in use
     */
    public boolean isInUse = false;
    /**
     * Whether this module is allowed to start and enabled
     */
    public boolean enabled = false;
    /**
     * The Current Player Map, if available
     */
    public List<PlayerInfo> currentPlayerList = Lists.newArrayList();
    /**
     * A List of the detected Server Addresses
     */
    public List<String> knownAddresses = Lists.newArrayList();
    /**
     * A List of the detected Server Data from NBT
     */
    public Map<String, ServerData> knownServerData = Maps.newHashMap();
    /**
     * The IP Address of the Current Server the Player is in
     */
    private String currentServer_IP;
    /**
     * The Name of the Current Server the Player is in
     */
    private String currentServer_Name;
    /**
     * The Message of the Day of the Current Server the Player is in
     */
    private String currentServer_MOTD;
    /**
     * The Current Server RPC Message being used, with Arguments
     */
    private String currentServerMessage = "";
    /**
     * The Current Server RPC Icon being used, with Arguments
     */
    private String currentServerIcon = "";
    /**
     * The Current Formatted World Time, as a String
     */
    private String timeString;
    /**
     * The Current Formatted World Days, as a String
     */
    private String dayString;
    /**
     * The Current World's Difficulty
     */
    private String currentDifficulty;
    /**
     * The Current World's Name
     */
    private String currentWorldName;
    /**
     * The Amount of Players in the Current Server the Player is in
     */
    private int currentPlayers;
    /**
     * The Maximum Amount of Players allowed in the Current Server the Player is in
     */
    private int maxPlayers;
    /**
     * The amount of Currently detected Server Addresses
     */
    private int serverIndex;
    /**
     * Mapping storing the Current X, Y and Z Position of the Player in a World
     * Format: Position (X, Y, Z)
     */
    private Tuple<Double, Double, Double> currentCoordinates = new Tuple<>(0.0D, 0.0D, 0.0D);
    /**
     * Mapping storing the Current and Maximum Health the Player currently has in a World
     */
    private Pair<Double, Double> currentHealth = new Pair<>(0.0D, 0.0D);
    /**
     * The Current Server Connection Data and Info
     */
    private ServerData currentServerData;
    /**
     * The Queued Server Connection Data and Info to Join, if any
     */
    private ServerData requestedServerData;
    /**
     * The Player's Current Connection Data
     */
    private ClientPacketListener currentConnection;
    /**
     * If the RPC needs to be Updated or Re-Synchronized<p>
     * Needed here for Multiple-Condition RPC Triggers
     */
    private boolean queuedForUpdate = false;
    /**
     * If in Progress of Joining a World/Server from another World/Server
     */
    private boolean joinInProgress = false;
    /**
     * If the Current Server is on a LAN-Based Connection (A Local Network Game)
     */
    private boolean isOnLAN = false;

    /**
     * Clears FULL Data from this Module
     */
    private void emptyData() {
        currentPlayerList.clear();
        knownAddresses.clear();
        knownServerData.clear();
        clearClientData();
    }

    /**
     * Clears Runtime Client Data from this Module (PARTIAL Clear)
     */
    public void clearClientData() {
        currentServer_IP = null;
        currentServer_MOTD = null;
        currentServer_Name = null;
        currentServerData = null;
        currentConnection = null;
        currentCoordinates = new Tuple<>(0.0D, 0.0D, 0.0D);
        currentHealth = new Pair<>(0.0D, 0.0D);
        currentDifficulty = null;
        currentWorldName = null;
        currentServerMessage = "";
        currentServerIcon = "";
        timeString = null;
        dayString = null;
        currentPlayers = 0;
        maxPlayers = 0;

        serverArgs.clear();
        iconArgs.clear();

        playerDataArgs.clear();
        playerAmountArgs.clear();
        worldDataArgs.clear();
        coordinateArgs.clear();
        healthArgs.clear();

        queuedForUpdate = false;
        isOnLAN = false;
        isInUse = false;

        if (!joinInProgress) {
            requestedServerData = null;
        }

        // Clear Sub-Argument Mappings
        for (String entry : subArgumentData.keySet()) {
            CraftPresence.CLIENT.removeArgumentsMatching(entry);
        }
        subArgumentData.clear();

        CraftPresence.CLIENT.initArgument(argumentFormat);
        CraftPresence.CLIENT.clearPartyData(true, false);
    }

    /**
     * Module Event to Occur on each tick within the Application
     */
    public void onTick() {
        joinInProgress = CraftPresence.CLIENT.STATUS == DiscordStatus.JoinGame || CraftPresence.CLIENT.STATUS == DiscordStatus.SpectateGame;
        enabled = !CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.detectWorldData : enabled;
        final boolean needsUpdate = enabled && knownAddresses.isEmpty();

        if (needsUpdate) {
            getServerAddresses();
        }

        if (enabled) {
            if (CraftPresence.player != null && !joinInProgress) {
                isInUse = true;
                updateServerData();
            } else if (isInUse) {
                clearClientData();
            }
        } else if (isInUse) {
            emptyData();
        }

        if (joinInProgress && requestedServerData != null) {
            CraftPresence.instance.execute(() -> joinServer(requestedServerData));
        }
    }

    /**
     * Synchronizes Data related to this module, if needed
     */
    private void updateServerData() {
        final ServerData newServerData = CraftPresence.instance.getCurrentServer();
        final ClientPacketListener newConnection = CraftPresence.instance.getConnection();

        if (!joinInProgress) {
            final List<PlayerInfo> newPlayerList = newConnection != null ? Lists.newArrayList(newConnection.getOnlinePlayers()) : Lists.newArrayList();
            final int newCurrentPlayers = newConnection != null ? newConnection.getOnlinePlayers().size() : 1;

            // 1.13+ Check for New Maximum Players
            int newMaxPlayers;
            if (newServerData != null) {
                try {
                    newMaxPlayers = StringUtils.getValidInteger(StringUtils.stripColors(newServerData.status.getString()).split("/")[1]).getSecond();

                    if (newMaxPlayers < newCurrentPlayers) {
                        newMaxPlayers = newCurrentPlayers + 1;
                    }
                } catch (Exception ex) {
                    newMaxPlayers = newCurrentPlayers + 1;
                }
            } else {
                newMaxPlayers = newCurrentPlayers + 1;
            }

            final boolean newLANStatus = (CraftPresence.instance.isLocalServer() && newCurrentPlayers > 1) || (newServerData != null && newServerData.isLan());
            final boolean isMotdValid = newServerData != null && newServerData.motd != null && !StringUtils.isNullOrEmpty(newServerData.motd.getString());

            final String newServer_IP = newServerData != null && !StringUtils.isNullOrEmpty(newServerData.ip) ? newServerData.ip : "127.0.0.1";
            final String newServer_Name = newServerData != null && !StringUtils.isNullOrEmpty(newServerData.name) ? newServerData.name : CraftPresence.CONFIG.defaultServerName;
            final String newServer_MOTD = !isOnLAN && !CraftPresence.instance.isLocalServer() && (newServerData != null && isMotdValid) &&
                    !(newServerData.motd.getString().equalsIgnoreCase(ModUtils.TRANSLATOR.translate("craftpresence.multiplayer.status.cannot_connect")) ||
                            newServerData.motd.getString().equalsIgnoreCase(ModUtils.TRANSLATOR.translate("craftpresence.multiplayer.status.cannot_resolve")) ||
                            newServerData.motd.getString().equalsIgnoreCase(ModUtils.TRANSLATOR.translate("craftpresence.multiplayer.status.polling")) ||
                            newServerData.motd.getString().equalsIgnoreCase(ModUtils.TRANSLATOR.translate("craftpresence.multiplayer.status.pinging"))) ? StringUtils.stripColors(newServerData.motd.getString()) : CraftPresence.CONFIG.defaultServerMotd;

            if (newLANStatus != isOnLAN || ((newServerData != null && !newServerData.equals(currentServerData)) ||
                    (newServerData == null && currentServerData != null)) ||
                    (newConnection != null && !newConnection.equals(currentConnection)) || !newServer_IP.equals(currentServer_IP) ||
                    (!StringUtils.isNullOrEmpty(newServer_MOTD) && !newServer_MOTD.equals(currentServer_MOTD)) ||
                    (!StringUtils.isNullOrEmpty(newServer_Name) && !newServer_Name.equals(currentServer_Name))) {
                currentServer_IP = newServer_IP;
                currentServer_MOTD = newServer_MOTD;
                currentServer_Name = newServer_Name;
                currentServerData = newServerData;
                currentConnection = newConnection;
                isOnLAN = newLANStatus;
                queuedForUpdate = true;

                if (!StringUtils.isNullOrEmpty(currentServer_IP) && !knownAddresses.contains(currentServer_IP.contains(":") ? currentServer_IP : StringUtils.formatAddress(currentServer_IP, false))) {
                    knownAddresses.add(currentServer_IP.contains(":") ? currentServer_IP : StringUtils.formatAddress(currentServer_IP, false));
                }

                final ServerList serverList = new ServerList(CraftPresence.instance);
                serverList.load();
                if (serverList.size() != serverIndex || CraftPresence.CONFIG.serverMessages.length != serverIndex) {
                    getServerAddresses();
                }
            }

            // NOTE: Universal + Custom Events
            List<String> matchingArgs = CraftPresence.CLIENT.getArgumentEntries(ArgumentType.Text, true, subArgumentFormat);
            String item, subItem;
            if (!StringUtils.isNullOrEmpty(currentServerMessage) || !matchingArgs.isEmpty()) {
                // &PLAYERINFO& Sub-Arguments
                item = "&playerinfo&";
                subItem = subArgumentFormat + item.substring(1);
                if (currentServerMessage.toLowerCase().contains(item) || matchingArgs.contains(subItem.toLowerCase())) {
                    // &coords& Argument = Current Coordinates of Player
                    if (CraftPresence.CONFIG.innerPlayerPlaceholderMessage.toLowerCase().contains("&coords&")) {
                        final double newX = StringUtils.roundDouble(CraftPresence.player != null ? CraftPresence.player.getX() : 0.0D, CraftPresence.CONFIG.roundSize);
                        final double newY = StringUtils.roundDouble(CraftPresence.player != null ? CraftPresence.player.getY() : 0.0D, CraftPresence.CONFIG.roundSize);
                        final double newZ = StringUtils.roundDouble(CraftPresence.player != null ? CraftPresence.player.getZ() : 0.0D, CraftPresence.CONFIG.roundSize);
                        final Tuple<Double, Double, Double> newCoordinates = new Tuple<>(newX, newY, newZ);
                        if (!newCoordinates.equals(currentCoordinates)) {
                            currentCoordinates = newCoordinates;
                            queuedForUpdate = true;
                        }
                    }

                    // &health& Argument = Current and Maximum Health of Player
                    if (CraftPresence.CONFIG.innerPlayerPlaceholderMessage.toLowerCase().contains("&health&")) {
                        final Pair<Double, Double> newHealth = CraftPresence.player != null ? new Pair<>(StringUtils.roundDouble(CraftPresence.player.getHealth(), 0), StringUtils.roundDouble(CraftPresence.player.getMaxHealth(), 0)) : new Pair<>(0.0D, 0.0D);
                        if (!newHealth.equals(currentHealth)) {
                            currentHealth = newHealth;
                            queuedForUpdate = true;
                        }
                    }
                }

                // &WORLDINFO& Sub-Arguments
                item = "&worldinfo&";
                subItem = subArgumentFormat + item.substring(1);
                if (currentServerMessage.toLowerCase().contains(item) || matchingArgs.contains(subItem.toLowerCase())) {
                    // &difficulty& Argument = Current Difficulty of the World
                    if (CraftPresence.CONFIG.worldPlaceholderMessage.toLowerCase().contains("&difficulty&")) {
                        final String newDifficulty = CraftPresence.player != null ?
                                (CraftPresence.player.level.getLevelData().isHardcore() ? ModUtils.TRANSLATOR.translate("craftpresence.defaults.mode.hardcore") : CraftPresence.player.level.getDifficulty().name()) :
                                "";
                        if (!newDifficulty.equals(currentDifficulty)) {
                            currentDifficulty = newDifficulty;
                            queuedForUpdate = true;
                        }
                    }

                    // &worldname& Argument = Current Name of the World
                    if (CraftPresence.CONFIG.worldPlaceholderMessage.toLowerCase().contains("&worldname&")) {
                        final String primaryWorldName = CraftPresence.instance.getSingleplayerServer() != null ? CraftPresence.instance.getSingleplayerServer().getWorldData().getLevelName() : "";
                        final String secondaryWorldName = CraftPresence.player != null && CraftPresence.player.level != null && CraftPresence.player.level.getServer() != null ?
                                CraftPresence.player.level.getServer().getWorldData().getLevelName() :
                                ModUtils.TRANSLATOR.translate("craftpresence.defaults.world_name");
                        final String newWorldName = !StringUtils.isNullOrEmpty(primaryWorldName) ? primaryWorldName : secondaryWorldName;
                        if (!newWorldName.equals(currentWorldName)) {
                            currentWorldName = newWorldName;
                            queuedForUpdate = true;
                        }
                    }

                    // &worldtime& Argument = Current Time in World
                    if (CraftPresence.CONFIG.worldPlaceholderMessage.toLowerCase().contains("&worldtime&")) {
                        final String newGameTime = CraftPresence.player != null ? getTimeString(CraftPresence.player.level.getDayTime()) : null;
                        if (!StringUtils.isNullOrEmpty(newGameTime) && !newGameTime.equals(timeString)) {
                            timeString = newGameTime;
                            queuedForUpdate = true;
                        }
                    }

                    // &worldday& Argument = Current Amount of Days in World
                    if (CraftPresence.CONFIG.worldPlaceholderMessage.toLowerCase().contains("&worldday&")) {
                        final String newGameDay = CraftPresence.player != null ? String.format("%d", CraftPresence.player.level.getDayTime() / 24000L) : null;
                        if (!StringUtils.isNullOrEmpty(newGameDay) && !newGameDay.equals(dayString)) {
                            dayString = newGameDay;
                            queuedForUpdate = true;
                        }
                    }
                }

                // &players& Argument = Current and Maximum Allowed Players in Server/World
                item = "&players&";
                subItem = subArgumentFormat + item.substring(1);
                if ((currentServerMessage.toLowerCase().contains(item) || matchingArgs.contains(subItem.toLowerCase()) || CraftPresence.CONFIG.enableJoinRequest) && (newCurrentPlayers != currentPlayers || newMaxPlayers != maxPlayers)) {
                    currentPlayers = newCurrentPlayers;
                    maxPlayers = newMaxPlayers;
                    queuedForUpdate = true;
                }
            }

            // Update Player List as needed, and Sync with Entity System if enabled
            if (!newPlayerList.equals(currentPlayerList)) {
                currentPlayerList = newPlayerList;

                if (CraftPresence.ENTITIES.enabled) {
                    CraftPresence.ENTITIES.getEntities();
                }
            }
        }

        if (queuedForUpdate) {
            updateServerPresence();
        }
    }

    /**
     * Converts a Raw World Time Long into a Readable 24-Hour Time String
     *
     * @param worldTime The raw World Time
     * @return The converted and readable 24-hour time string
     */
    private String getTimeString(long worldTime) {
        long hour = 0;
        long minute = 0;
        long dayLength = 24000;
        long remainingTicks = worldTime % dayLength;

        while (remainingTicks >= 1000) {
            remainingTicks -= 1000;
            hour++;
            if (hour > 24)
                hour -= 24;
        }
        remainingTicks *= 3;
        while (remainingTicks >= 50) {
            remainingTicks -= 50;
            minute++;
        }

        String formattedHour = String.valueOf(hour).length() == 1 ? String.format("%02d", hour) : String.valueOf(hour);
        String formattedMinute = String.valueOf(minute).length() == 1 ? String.format("%02d", minute) : String.valueOf(minute);

        return formattedHour + ":" + formattedMinute;
    }

    /**
     * Creates a Secret Key to use in Sending Requested Server Data from Discord Join Requests
     *
     * @return The Parsable Secret Key
     */
    private String makeSecret() {
        String formattedKey = CraftPresence.CLIENT.CLIENT_ID + "";
        boolean containsServerName = false;
        boolean containsServerIP = false;

        if (!StringUtils.isNullOrEmpty(currentServer_Name)) {
            formattedKey += "-" + currentServer_Name.toLowerCase();
            containsServerName = true;
        }
        if (!StringUtils.isNullOrEmpty(currentServer_IP)) {
            formattedKey += "-" + currentServer_IP.toLowerCase();
            containsServerIP = true;
        }

        formattedKey += ";" + containsServerName + ";" + containsServerIP;
        return formattedKey;
    }

    /**
     * Verifies the Inputted secret Key, and upon match, Form Server Data to join a Server
     *
     * @param secret The secret key to test against for validity
     */
    public void verifyAndJoin(final String secret) {
        String[] boolParts = secret.split(";");
        String[] stringParts = boolParts[0].split("-");
        boolean containsValidClientID = StringUtils.elementExists(stringParts, 0) && (stringParts[0].length() >= 18 && StringUtils.getValidLong(stringParts[0]).getFirst());
        boolean containsServerName = StringUtils.elementExists(boolParts, 1) && StringUtils.elementExists(stringParts, 1) && Boolean.parseBoolean(boolParts[1]);
        boolean containsServerIP = StringUtils.elementExists(boolParts, 2) && StringUtils.elementExists(stringParts, 2) && Boolean.parseBoolean(boolParts[2]);
        String serverName = containsServerName ? stringParts[1] : CraftPresence.CONFIG.defaultServerName;
        String serverIP = containsServerIP ? stringParts[2] : "";
        boolean isValidSecret = boolParts.length <= 4 && stringParts.length <= 3 && containsValidClientID;

        if (isValidSecret) {
            if (CraftPresence.CONFIG.enableJoinRequest) {
                requestedServerData = new ServerData(serverName, serverIP, false);
            } else {
                ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.warning.config.disabled.enable_join_request"));
            }
        } else {
            ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.error.discord.join", secret));
        }
    }

    /**
     * Joins a Server/World based on Server Data requested
     *
     * @param serverData The Requested Server Data to Join
     */
    private void joinServer(final ServerData serverData) {
        try {
            ConnectScreen.startConnecting(CraftPresence.instance.screen != null ? CraftPresence.instance.screen : new TitleScreen(), CraftPresence.instance, ServerAddress.parseString(serverData.ip), serverData);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            requestedServerData = null;
        }
    }

    /**
     * Updates RPC Data related to this Module
     */
    public void updateServerPresence() {
        // Form General Argument Lists & Sub Argument Lists
        serverArgs.clear();
        iconArgs.clear();

        playerDataArgs.clear();
        worldDataArgs.clear();
        coordinateArgs.clear();
        healthArgs.clear();

        // Clear Sub-Argument Mappings
        for (String entry : subArgumentData.keySet()) {
            CraftPresence.CLIENT.removeArgumentsMatching(entry);
        }
        subArgumentData.clear();

        coordinateArgs.add(new Pair<>("&xPosition&", currentCoordinates.getFirst().toString()));
        coordinateArgs.add(new Pair<>("&yPosition&", currentCoordinates.getSecond().toString()));
        coordinateArgs.add(new Pair<>("&zPosition&", currentCoordinates.getThird().toString()));

        healthArgs.add(new Pair<>("&CURRENT&", currentHealth.getFirst().toString()));
        healthArgs.add(new Pair<>("&MAX&", currentHealth.getSecond().toString()));

        // Player Data Arguments
        playerDataArgs.add(new Pair<>("&COORDS&", StringUtils.sequentialReplaceAnyCase(CraftPresence.CONFIG.playerCoordinatePlaceholderMessage, coordinateArgs)));
        playerDataArgs.add(new Pair<>("&HEALTH&", StringUtils.sequentialReplaceAnyCase(CraftPresence.CONFIG.playerHealthPlaceholderMessage, healthArgs)));

        // World Data Arguments
        worldDataArgs.add(new Pair<>("&DIFFICULTY&", !StringUtils.isNullOrEmpty(currentDifficulty) ? currentDifficulty : ""));
        worldDataArgs.add(new Pair<>("&WORLDNAME&", !StringUtils.isNullOrEmpty(currentWorldName) ? currentWorldName : ""));
        worldDataArgs.add(new Pair<>("&WORLDTIME&", !StringUtils.isNullOrEmpty(timeString) ? timeString : ""));
        worldDataArgs.add(new Pair<>("&WORLDDAY&", !StringUtils.isNullOrEmpty(dayString) ? dayString : ""));

        // Server Data Arguments (Universal)
        serverArgs.add(new Pair<>("&PLAYERINFO&", StringUtils.sequentialReplaceAnyCase(CraftPresence.CONFIG.innerPlayerPlaceholderMessage, playerDataArgs)));
        serverArgs.add(new Pair<>("&WORLDINFO&", StringUtils.sequentialReplaceAnyCase(CraftPresence.CONFIG.worldPlaceholderMessage, worldDataArgs)));

        iconArgs.add(new Pair<>("&ICON&", CraftPresence.CONFIG.defaultServerIcon));

        if (!CraftPresence.instance.isLocalServer() && currentServerData != null) {
            // Form Pair List of Argument for Servers/LAN Games
            playerAmountArgs.clear();

            // Player Amount Arguments
            playerAmountArgs.add(new Pair<>("&CURRENT&", Integer.toString(currentPlayers)));
            playerAmountArgs.add(new Pair<>("&MAX&", Integer.toString(maxPlayers)));

            // Server Data Arguments (Multiplayer)
            serverArgs.add(new Pair<>("&IP&", StringUtils.formatAddress(currentServer_IP, false)));
            serverArgs.add(new Pair<>("&NAME&", currentServer_Name));
            serverArgs.add(new Pair<>("&MOTD&", currentServer_MOTD));
            serverArgs.add(new Pair<>("&PLAYERS&", StringUtils.sequentialReplaceAnyCase(CraftPresence.CONFIG.playerAmountPlaceholderMessage, playerAmountArgs)));

            if (isOnLAN) {
                // NOTE: LAN-Only Presence Updates
                final String alternateServerIcon = StringUtils.getConfigPart(CraftPresence.CONFIG.serverMessages, currentServer_Name, 0, 2, CraftPresence.CONFIG.splitCharacter, currentServer_Name);
                final String primaryServerIcon = StringUtils.getConfigPart(CraftPresence.CONFIG.serverMessages, StringUtils.formatAddress(currentServer_IP, false), 0, 2, CraftPresence.CONFIG.splitCharacter, alternateServerIcon);
                final String formattedServerIconKey = StringUtils.formatAsIcon(primaryServerIcon.replace(" ", "_"));

                currentServerIcon = StringUtils.sequentialReplaceAnyCase(formattedServerIconKey, iconArgs);
                currentServerMessage = CraftPresence.CONFIG.lanMessage;
            } else {
                // NOTE: Server-Only Presence Updates
                final String defaultServerMessage = StringUtils.getConfigPart(CraftPresence.CONFIG.serverMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
                final String alternateServerMessage = StringUtils.getConfigPart(CraftPresence.CONFIG.serverMessages, currentServer_Name, 0, 1, CraftPresence.CONFIG.splitCharacter, defaultServerMessage);
                final String alternateServerIcon = StringUtils.getConfigPart(CraftPresence.CONFIG.serverMessages, currentServer_Name, 0, 2, CraftPresence.CONFIG.splitCharacter, currentServer_Name);

                currentServerMessage = StringUtils.getConfigPart(CraftPresence.CONFIG.serverMessages, StringUtils.formatAddress(currentServer_IP, false), 0, 1, CraftPresence.CONFIG.splitCharacter, alternateServerMessage);
                final String primaryServerIcon = StringUtils.getConfigPart(CraftPresence.CONFIG.serverMessages, StringUtils.formatAddress(currentServer_IP, false), 0, 2, CraftPresence.CONFIG.splitCharacter, alternateServerIcon);
                final String formattedServerIconKey = StringUtils.formatAsIcon(primaryServerIcon.replace(" ", "_"));

                currentServerIcon = StringUtils.sequentialReplaceAnyCase(formattedServerIconKey, iconArgs);

                // If join requests are enabled, parse the appropriate data
                // to form party information.
                //
                // Note: The party privacy level is appended by modulus division to prevent
                // it being anything other then valid privacy levels
                if (CraftPresence.CONFIG.enableJoinRequest) {
                    if (!StringUtils.isNullOrEmpty(currentServer_Name) && !currentServer_Name.equalsIgnoreCase(CraftPresence.CONFIG.defaultServerName)) {
                        CraftPresence.CLIENT.PARTY_ID = "Join Server: " + currentServer_Name;
                    } else {
                        CraftPresence.CLIENT.PARTY_ID = "Join Server: " + currentServer_IP;
                    }
                    CraftPresence.CLIENT.JOIN_SECRET = makeSecret();
                    CraftPresence.CLIENT.PARTY_SIZE = currentPlayers;
                    CraftPresence.CLIENT.PARTY_MAX = maxPlayers;
                    CraftPresence.CLIENT.PARTY_PRIVACY = PartyPrivacy.from(CraftPresence.CONFIG.partyPrivacyLevel % 2);
                }
            }
        } else if (CraftPresence.instance.isLocalServer()) {
            // NOTE: SinglePlayer-Only Presence Updates
            currentServerMessage = CraftPresence.CONFIG.singlePlayerMessage;
            currentServerIcon = "";
        }

        // Add applicable args as sub-placeholders
        subArgumentData.put(subArgumentFormat, serverArgs);
        subArgumentData.put(subArgumentFormat + "PLAYERINFO:", playerDataArgs);
        subArgumentData.put(subArgumentFormat + "PLAYERINFO:COORDS:", coordinateArgs);
        subArgumentData.put(subArgumentFormat + "PLAYERINFO:HEALTH:", healthArgs);
        subArgumentData.put(subArgumentFormat + "PLAYERS:", playerAmountArgs);
        subArgumentData.put(subArgumentFormat + "WORLDINFO:", worldDataArgs);
        for (Map.Entry<String, List<Pair<String, String>>> entry : subArgumentData.entrySet()) {
            if (!StringUtils.isNullOrEmpty(entry.getKey()) && !entry.getValue().isEmpty()) {
                for (Pair<String, String> argumentData : entry.getValue()) {
                    CraftPresence.CLIENT.syncArgument(
                            entry.getKey() + argumentData.getFirst().substring(1),
                            argumentData.getSecond(),
                            ArgumentType.Text
                    );
                }
            }
        }

        // Add All Generalized Arguments, if any
        if (!CraftPresence.CLIENT.generalArgs.isEmpty()) {
            StringUtils.addEntriesNotPresent(serverArgs, CraftPresence.CLIENT.generalArgs);
        }

        CraftPresence.CLIENT.syncArgument(argumentFormat, StringUtils.sequentialReplaceAnyCase(currentServerMessage, serverArgs), ArgumentType.Text);
        if (!StringUtils.isNullOrEmpty(currentServerIcon)) {
            CraftPresence.CLIENT.syncArgument(argumentFormat, CraftPresence.CLIENT.imageOf(argumentFormat, true, currentServerIcon, CraftPresence.CONFIG.defaultServerIcon), ArgumentType.Image);
        } else {
            CraftPresence.CLIENT.initArgument(ArgumentType.Image, argumentFormat);
        }
        queuedForUpdate = false;
    }

    /**
     * Retrieves and Synchronizes detected Server Addresses from the Server List
     */
    public void getServerAddresses() {
        try {
            final ServerList serverList = new ServerList(CraftPresence.instance);
            serverList.load();
            serverIndex = serverList.size();

            for (int currentIndex = 0; currentIndex < serverIndex; currentIndex++) {
                final ServerData data = serverList.get(currentIndex);
                if (!StringUtils.isNullOrEmpty(data.ip) && !knownAddresses.contains(data.ip.contains(":") ? StringUtils.formatAddress(data.ip, false) : data.ip)) {
                    knownAddresses.add(data.ip.contains(":") ? StringUtils.formatAddress(data.ip, false) : data.ip);
                }

                if (!StringUtils.isNullOrEmpty(data.ip) && !knownServerData.containsKey(data.ip)) {
                    knownServerData.put(data.ip, data);
                }
            }
        } catch (Exception ex) {
            if (ModUtils.IS_VERBOSE) {
                ex.printStackTrace();
            }
        }

        for (String serverMessage : CraftPresence.CONFIG.serverMessages) {
            if (!StringUtils.isNullOrEmpty(serverMessage)) {
                final String[] part = serverMessage.split(CraftPresence.CONFIG.splitCharacter);
                if (!StringUtils.isNullOrEmpty(part[0]) && !knownAddresses.contains(part[0])) {
                    knownAddresses.add(part[0]);
                }
            }
        }
    }

    /**
     * Retrieves server data for the specified address, if available
     *
     * @param serverAddress The Server's identifying address
     * @return Server data for the specified address, if available
     */
    public ServerData getDataFromName(final String serverAddress) {
        return knownServerData.getOrDefault(serverAddress, null);
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param types The argument types to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final String argumentFormat, final String subArgumentFormat, ArgumentType... types) {
        types = (types != null && types.length > 0 ? types : ArgumentType.values());
        final Map<ArgumentType, List<String>> argumentData = Maps.newHashMap();
        List<String> queuedEntries;
        for (ArgumentType type : types) {
            queuedEntries = Lists.newArrayList();
            if (type == ArgumentType.Image) {
                queuedEntries.add(subArgumentFormat + "ICON&");
            } else if (type == ArgumentType.Text) {
                if (subArgumentFormat.endsWith("PLAYERINFO:")) {
                    queuedEntries.add(subArgumentFormat + "COORDS&");
                    queuedEntries.add(subArgumentFormat + "HEALTH&");
                } else if (subArgumentFormat.endsWith("PLAYERINFO:COORDS:")) {
                    queuedEntries.add(subArgumentFormat + "xPosition&");
                    queuedEntries.add(subArgumentFormat + "yPosition&");
                    queuedEntries.add(subArgumentFormat + "zPosition&");
                } else if (subArgumentFormat.endsWith("PLAYERINFO:HEALTH:")) {
                    queuedEntries.add(subArgumentFormat + "CURRENT&");
                    queuedEntries.add(subArgumentFormat + "MAX&");
                } else if (subArgumentFormat.endsWith("PLAYERS:")) {
                    queuedEntries.add(subArgumentFormat + "CURRENT&");
                    queuedEntries.add(subArgumentFormat + "MAX&");
                } else if (subArgumentFormat.endsWith("WORLDINFO:")) {
                    queuedEntries.add(subArgumentFormat + "DIFFICULTY&");
                    queuedEntries.add(subArgumentFormat + "WORLDNAME&");
                    queuedEntries.add(subArgumentFormat + "WORLDTIME&");
                    queuedEntries.add(subArgumentFormat + "WORLDDAY&");
                } else {
                    queuedEntries.add(subArgumentFormat + "PLAYERINFO&");
                    queuedEntries.add(subArgumentFormat + "WORLDINFO&");
                    queuedEntries.add(subArgumentFormat + "IP&");
                    queuedEntries.add(subArgumentFormat + "NAME&");
                    queuedEntries.add(subArgumentFormat + "MOTD&");
                    queuedEntries.add(subArgumentFormat + "PLAYERS&");
                }
            }
            argumentData.put(type, queuedEntries);
        }
        return CraftPresence.CLIENT.generateArgumentMessage(argumentFormat, subArgumentFormat, argumentData);
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param types The argument types to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(ArgumentType... types) {
        return generateArgumentMessage(argumentFormat, subArgumentFormat, types);
    }
}
