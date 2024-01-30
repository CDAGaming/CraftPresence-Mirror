/*
 * MIT License
 *
 * Copyright (c) 2018 - 2024 CDAGaming (cstack2011@yahoo.com)
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
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.core.impl.Module;
import com.gitlab.cdagaming.craftpresence.core.impl.discord.DiscordStatus;
import com.gitlab.cdagaming.craftpresence.core.impl.discord.PartyPrivacy;
import com.gitlab.cdagaming.craftpresence.utils.entity.EntityUtils;
import io.github.cdagaming.unicore.impl.Pair;
import io.github.cdagaming.unicore.impl.Tuple;
import io.github.cdagaming.unicore.utils.MathUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import io.github.cdagaming.unicore.utils.TimeUtils;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Server Utilities used to Parse Server Data and handle related RPC Events
 *
 * @author CDAGaming
 */
@SuppressWarnings("DuplicatedCode")
public class ServerUtils implements Module {
    /**
     * The List of invalid MOTD (Message of the Day) Translations
     */
    private static final List<String> invalidMotds = StringUtils.newArrayList(
            "multiplayer.status.cannot_connect",
            "multiplayer.status.cannot_resolve",
            "multiplayer.status.polling",
            "multiplayer.status.pinging"
    );
    /**
     * The List of invalid Server Name Translations
     */
    private static final List<String> invalidNames = StringUtils.newArrayList(
            "selectServer.defaultName"
    );
    /**
     * Whether this module is allowed to start and enabled
     */
    public boolean enabled = false;
    /**
     * The Current Player Map, if available
     */
    public List<NetworkPlayerInfo> currentPlayerList = StringUtils.newArrayList();
    /**
     * A List of the detected Server Addresses
     */
    public List<String> knownAddresses = StringUtils.newArrayList();
    /**
     * A List of the default detected Server Addresses
     */
    public List<String> defaultAddresses = StringUtils.newArrayList();
    /**
     * A List of the detected Server Data from NBT
     */
    public Map<String, ServerData> knownServerData = StringUtils.newHashMap();
    /**
     * Whether this module is active and currently in use
     */
    private boolean isInUse = false;
    /**
     * Whether this module has performed an initial retrieval of items
     */
    private boolean hasScanned = false;
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
     * The Message of the Day, split by new lines, of the Current Server the Player is in
     */
    private List<String> currentServer_MOTD_Lines = StringUtils.newArrayList();
    /**
     * The Current Server RPC Message being used, with Arguments
     */
    private String currentServerMessage = "";
    /**
     * The Current Server RPC Icon being used, with Arguments
     */
    private String currentServerIcon = "";
    /**
     * The current World Time data
     */
    private Pair<Long, Instant> worldTimeData;
    /**
     * The Current Formatted World Time (24-hour Format), as a String
     */
    private String timeString24;
    /**
     * The Current Formatted World Time (12-hour Format), as a String
     */
    private String timeString12;
    /**
     * The Current Formatted World Days, as a String
     */
    private long dayCount;
    /**
     * The Current World's Difficulty
     */
    private String currentDifficulty;
    /**
     * The Current World's Name
     */
    private String currentWorldName;
    /**
     * The Current World's Weather Name
     */
    private String currentWeatherName;
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
    private Tuple<Double, Double, Double> currentCoordinates;
    /**
     * Mapping storing the Current and Maximum Health the Player currently has in a World
     */
    private Pair<Double, Double> currentHealth;
    /**
     * The Current Server Connection Data and Info
     */
    private ServerData currentServerData;
    /**
     * The Player's Current Connection Data
     */
    private NetHandlerPlayClient currentConnection;
    /**
     * If the RPC needs to be Updated or Re-Synchronized<p>
     * Needed here for Multiple-Condition RPC Triggers
     */
    private boolean queuedForUpdate = false;
    /**
     * Whether a Join Request is currently in progress
     */
    private boolean joinInProgress = false;
    /**
     * If the Current Server is on a LAN-Based Connection (A Local Network Game)
     */
    private boolean isOnLAN = false;

    @Override
    public void emptyData() {
        hasScanned = false;
        currentPlayerList.clear();
        defaultAddresses.clear();
        knownAddresses.clear();
        knownServerData.clear();
        clearClientData();
    }

    @Override
    public void clearClientData() {
        currentServer_IP = null;
        currentServer_MOTD = null;
        currentServer_MOTD_Lines.clear();
        currentServer_Name = null;
        currentServerData = null;
        currentConnection = null;
        currentCoordinates = null;
        currentHealth = null;
        currentDifficulty = null;
        currentWorldName = null;
        currentWeatherName = null;
        currentServerMessage = "";
        currentServerIcon = "";
        worldTimeData = null;
        timeString24 = null;
        timeString12 = null;
        dayCount = 0L;
        currentPlayers = 0;
        maxPlayers = 0;

        queuedForUpdate = false;
        joinInProgress = false;
        isOnLAN = false;
        setInUse(false);

        CraftPresence.CLIENT.removeArguments("server", "data.server", "world", "data.world", "player");
        CraftPresence.CLIENT.clearOverride("server.message", "server.icon");
        CraftPresence.CLIENT.clearPartyData();
    }

    @Override
    public void onTick() {
        joinInProgress = CraftPresence.CLIENT.STATUS == DiscordStatus.JoinGame || CraftPresence.CLIENT.STATUS == DiscordStatus.SpectateGame;
        enabled = !CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.generalSettings.detectWorldData : enabled;
        final boolean needsUpdate = enabled && !hasScanned && canFetchData();

        if (needsUpdate) {
            scanForData();
            hasScanned = true;
        }

        if (enabled) {
            if (CraftPresence.player != null && !joinInProgress) {
                setInUse(true);
                updateData();
            } else if (isInUse()) {
                clearClientData();
            }
        } else if (isInUse()) {
            emptyData();
        }
    }

    @Override
    public void updateData() {
        final ServerData newServerData = CraftPresence.instance.getCurrentServerData();
        final NetHandlerPlayClient newConnection = CraftPresence.instance.getConnection();

        if (!joinInProgress) {
            final List<NetworkPlayerInfo> newPlayerList = newConnection != null ? StringUtils.newArrayList(newConnection.getPlayerInfoMap()) : StringUtils.newArrayList();
            final int newCurrentPlayers = newConnection != null ? newConnection.getPlayerInfoMap().size() : 1;
            final int newMaxPlayers = newConnection != null && newConnection.currentServerMaxPlayers >= newCurrentPlayers ? newConnection.currentServerMaxPlayers : newCurrentPlayers + 1;
            final boolean newLANStatus = (CraftPresence.instance.isSingleplayer() && newCurrentPlayers > 1) || (newServerData != null && newServerData.isOnLAN());

            final String newServer_IP = newServerData != null && !StringUtils.isNullOrEmpty(newServerData.serverIP) ? newServerData.serverIP : "127.0.0.1";
            final String newServer_Name = newServerData != null && !isInvalidName(newServerData.serverName) ? newServerData.serverName : CraftPresence.CONFIG.serverSettings.fallbackServerName;
            final String newServer_MOTD = !isOnLAN && !CraftPresence.instance.isSingleplayer() &&
                    newServerData != null && !isInvalidMotd(newServerData.serverMOTD) ? StringUtils.stripAllFormatting(newServerData.serverMOTD) : CraftPresence.CONFIG.serverSettings.fallbackServerMotd;

            if (newLANStatus != isOnLAN || ((newServerData != null && !newServerData.equals(currentServerData)) ||
                    (newServerData == null && currentServerData != null)) ||
                    (newConnection != null && !newConnection.equals(currentConnection)) || !newServer_IP.equals(currentServer_IP) ||
                    (!StringUtils.isNullOrEmpty(newServer_MOTD) && !newServer_MOTD.equals(currentServer_MOTD)) ||
                    (!StringUtils.isNullOrEmpty(newServer_Name) && !newServer_Name.equals(currentServer_Name))) {
                currentServer_IP = newServer_IP;

                if (!newServer_MOTD.equals(currentServer_MOTD)) {
                    currentServer_MOTD = newServer_MOTD;
                    currentServer_MOTD_Lines = StringUtils.splitTextByNewLine(newServer_MOTD);
                }
                currentServer_Name = newServer_Name;
                currentServerData = newServerData;
                currentConnection = newConnection;
                isOnLAN = newLANStatus;
                queuedForUpdate = true;

                if (!StringUtils.isNullOrEmpty(currentServer_IP)) {
                    final String formattedIP = currentServer_IP.contains(":") ? StringUtils.formatAddress(currentServer_IP, false) : currentServer_IP;
                    if (!defaultAddresses.contains(formattedIP)) {
                        defaultAddresses.add(formattedIP);
                    }
                    if (!knownAddresses.contains(formattedIP)) {
                        knownAddresses.add(formattedIP);
                    }
                }

                final ServerList serverList = new ServerList(CraftPresence.instance);
                serverList.loadServerList();
                if (serverList.countServers() != serverIndex || CraftPresence.CONFIG.serverSettings.serverData.size() != serverIndex) {
                    scanForData();
                }
            }

            // NOTE: Universal + Custom Events

            // `player` Sub-Arguments

            // `player.position` Argument = Current Coordinates of Player
            final Tuple<Double, Double, Double> newCoordinates = new Tuple<>(
                    MathUtils.roundDouble(CraftPresence.player.posX, 3),
                    MathUtils.roundDouble(CraftPresence.player.posY, 3),
                    MathUtils.roundDouble(CraftPresence.player.posZ, 3)
            );
            if (!Objects.equals(newCoordinates, currentCoordinates)) {
                currentCoordinates = newCoordinates;
                queuedForUpdate = true;
            }

            // 'player.health' Argument = Current and Maximum Health of Player
            final Pair<Double, Double> newHealth = new Pair<>(
                    MathUtils.roundDouble(CraftPresence.player.getHealth(), 0),
                    MathUtils.roundDouble(CraftPresence.player.getMaxHealth(), 0)
            );
            if (!Objects.equals(newHealth, currentHealth)) {
                currentHealth = newHealth;
                queuedForUpdate = true;
            }

            // 'world' Sub-Arguments

            // 'world.difficulty' Argument = Current Difficulty of the World
            final String newDifficulty = CraftPresence.player.world.getWorldInfo().isHardcoreModeEnabled() && ModUtils.RAW_TRANSLATOR != null ?
                    ModUtils.RAW_TRANSLATOR.translate("selectWorld.gameMode.hardcore") :
                    StringUtils.formatWord(CraftPresence.player.world.getDifficulty().name().toLowerCase());
            if (!newDifficulty.equals(currentDifficulty)) {
                currentDifficulty = newDifficulty;
                queuedForUpdate = true;
            }

            // `world.weather` Arguments = Current Weather Data of the World
            final String newWeatherData = EntityUtils.getWeather(CraftPresence.player);
            final String newWeatherName = Constants.TRANSLATOR.translate("craftpresence.defaults.weather." + newWeatherData);
            if (!newWeatherName.equals(currentWeatherName)) {
                currentWeatherName = newWeatherName;
                queuedForUpdate = true;
            }

            // 'world.name' Argument = Current Name of the World
            final String primaryWorldName = CraftPresence.instance.getIntegratedServer() != null ? CraftPresence.instance.getIntegratedServer().getWorldName() : "";
            final String secondaryWorldName = StringUtils.getOrDefault(CraftPresence.player.world.getWorldInfo().getWorldName(), Constants.TRANSLATOR.translate("craftpresence.defaults.world_name"));
            final String newWorldName = StringUtils.getOrDefault(primaryWorldName, secondaryWorldName);
            if (!newWorldName.equals(currentWorldName)) {
                currentWorldName = newWorldName;
                queuedForUpdate = true;
            }

            // 'world.time' Arguments = Current Time Data of the World
            final Pair<Long, Instant> newTimeData = TimeUtils.fromWorldTime(CraftPresence.player.world.getWorldTime());
            if (!Objects.equals(newTimeData, worldTimeData)) {
                dayCount = newTimeData.getFirst();
                timeString24 = TimeUtils.toString(newTimeData.getSecond(), "HH:mm");
                timeString12 = TimeUtils.toString(newTimeData.getSecond(), "hh:mm a");
                worldTimeData = newTimeData;
                queuedForUpdate = true;
            }

            // 'server.players' Argument = Current and Maximum Allowed Players in Server/World
            if (newCurrentPlayers != currentPlayers || newMaxPlayers != maxPlayers) {
                currentPlayers = newCurrentPlayers;
                maxPlayers = newMaxPlayers;
                queuedForUpdate = true;
            }

            // Update Player List as needed, and Sync with Entity System if enabled
            if (!newPlayerList.equals(currentPlayerList)) {
                currentPlayerList = newPlayerList;

                if (CraftPresence.ENTITIES.enabled) {
                    CraftPresence.ENTITIES.ENTITY_NAMES.removeAll(CraftPresence.ENTITIES.PLAYER_BINDINGS.keySet());
                    CraftPresence.ENTITIES.scanForData();
                }
            }
        }

        if (queuedForUpdate) {
            updatePresence();
        }
    }

    /**
     * Whether the supplied server element contains invalid characters
     *
     * @param input        The server element to interpret
     * @param invalidItems The list of items to iterate over for validity
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    private boolean isInvalidData(final String input, final List<String> invalidItems) {
        if (!StringUtils.isNullOrEmpty(input)) {
            for (String item : invalidItems) {
                if (ModUtils.RAW_TRANSLATOR != null && ModUtils.RAW_TRANSLATOR.hasTranslation(item) && input.equalsIgnoreCase(ModUtils.RAW_TRANSLATOR.translate(item))) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Whether the supplied server MOTD (Message of the Day) contains invalid characters
     *
     * @param serverMotd the server MOTD (Message of the day) to interpret
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    private boolean isInvalidMotd(final String serverMotd) {
        return isInvalidData(serverMotd, invalidMotds);
    }

    /**
     * Whether the supplied server name contains invalid characters
     *
     * @param serverName the server name to interpret
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    private boolean isInvalidName(final String serverName) {
        return isInvalidData(serverName, invalidNames);
    }

    /**
     * Creates a Secret Key to use in Sending Requested Server Data from Discord Join Requests
     *
     * @return The Parsable Secret Key
     */
    private String makeSecret() {
        String formattedKey = CraftPresence.CLIENT.CLIENT_ID;
        boolean containsServerName = false;
        boolean containsServerIP = false;

        if (!StringUtils.isNullOrEmpty(currentServer_Name)) {
            formattedKey += "-" + currentServer_Name;
            containsServerName = true;
        }
        if (!StringUtils.isNullOrEmpty(currentServer_IP)) {
            formattedKey += "-" + currentServer_IP;
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
        final String[] boolParts = secret.split(";");
        final String[] stringParts = boolParts[0].split("-");
        final boolean containsValidClientID = StringUtils.elementExists(stringParts, 0) && (stringParts[0].length() >= 18 && StringUtils.getValidLong(stringParts[0]).getFirst());
        final boolean containsServerName = StringUtils.elementExists(boolParts, 1) && StringUtils.elementExists(stringParts, 1) && Boolean.parseBoolean(boolParts[1]);
        final boolean containsServerIP = StringUtils.elementExists(boolParts, 2) && StringUtils.elementExists(stringParts, 2) && Boolean.parseBoolean(boolParts[2]);
        final String serverName = containsServerName ? stringParts[1] : CraftPresence.CONFIG.serverSettings.fallbackServerName;
        final String serverIP = containsServerIP ? stringParts[2] : "";
        final boolean isValidSecret = boolParts.length <= 4 && stringParts.length <= 3 && containsValidClientID;

        if (isValidSecret) {
            CraftPresence.instance.addScheduledTask(() -> joinServer(new ServerData(serverName, serverIP, false)));
        } else {
            Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.discord.join", secret));
        }
        CraftPresence.CLIENT.STATUS = DiscordStatus.Ready;
    }

    /**
     * Joins a Server/World based on Server Data requested
     *
     * @param serverData The Requested Server Data to Join
     */
    private void joinServer(final ServerData serverData) {
        try {
            if (!serverData.pinged) {
                // Stub Server Data if not pinged
                serverData.pinged = true;
                serverData.pingToServer = -2L;
                serverData.serverMOTD = "";
                serverData.populationInfo = "";
            }

            if (CraftPresence.player != null) {
                CraftPresence.player.world.sendQuittingDisconnectingPacket();
                CraftPresence.instance.loadWorld(null);
            }
            CraftPresence.instance.displayGuiScreen(new GuiConnecting(CraftPresence.instance.currentScreen != null ? CraftPresence.instance.currentScreen : new GuiMainMenu(), CraftPresence.instance, serverData));
        } catch (Exception ex) {
            Constants.LOG.debugError(ex);
        }
    }

    @Override
    public void updatePresence() {
        // Form General Argument Lists & Sub Argument Lists
        CraftPresence.CLIENT.syncArgument("player.position.x", currentCoordinates.getFirst());
        CraftPresence.CLIENT.syncArgument("player.position.y", currentCoordinates.getSecond());
        CraftPresence.CLIENT.syncArgument("player.position.z", currentCoordinates.getThird());

        CraftPresence.CLIENT.syncArgument("player.health.current", currentHealth.getFirst());
        CraftPresence.CLIENT.syncArgument("player.health.max", currentHealth.getSecond());

        // World Data Arguments
        CraftPresence.CLIENT.syncArgument("world.difficulty", StringUtils.getOrDefault(currentDifficulty));
        CraftPresence.CLIENT.syncArgument("world.weather.name", StringUtils.getOrDefault(currentWeatherName));
        CraftPresence.CLIENT.syncArgument("world.name", StringUtils.getOrDefault(currentWorldName));
        if (worldTimeData != null) {
            CraftPresence.CLIENT.syncArgument("world.time.day", dayCount);
            CraftPresence.CLIENT.syncArgument("world.time.format_24", timeString24);
            CraftPresence.CLIENT.syncArgument("world.time.format_12", timeString12);
            CraftPresence.CLIENT.syncArgument("data.world.time.instance", worldTimeData);
        } else {
            CraftPresence.CLIENT.removeArguments("world.time", "data.world.time");
        }

        CraftPresence.CLIENT.syncArgument("server.default.icon", CraftPresence.CONFIG.serverSettings.fallbackServerIcon);

        ModuleData resultData = new ModuleData();
        String formattedIcon;
        if (!CraftPresence.instance.isSingleplayer() && currentServerData != null) {
            // Player Amount Arguments
            CraftPresence.CLIENT.syncArgument("server.players.current", currentPlayers);
            CraftPresence.CLIENT.syncArgument("server.players.max", maxPlayers);

            // Server Data Arguments (Multiplayer)
            final String formattedIP = currentServer_IP.contains(":") ? StringUtils.formatAddress(currentServer_IP, false) : currentServer_IP;
            CraftPresence.CLIENT.syncArgument("server.address.full", currentServer_IP);
            CraftPresence.CLIENT.syncArgument("server.address.short", formattedIP);
            CraftPresence.CLIENT.syncArgument("server.name", currentServer_Name);
            CraftPresence.CLIENT.syncArgument("server.motd.raw", currentServer_MOTD);
            if (!currentServer_MOTD_Lines.isEmpty()) {
                int index = 1;
                for (String motdPart : currentServer_MOTD_Lines) {
                    CraftPresence.CLIENT.syncArgument("data.server.motd.line_" + index, motdPart);
                    index++;
                }
            }

            final ModuleData defaultData = CraftPresence.CONFIG.serverSettings.serverData.get("default");
            final ModuleData alternateData = CraftPresence.CONFIG.serverSettings.serverData.get(currentServer_Name);
            final ModuleData primaryData = CraftPresence.CONFIG.serverSettings.serverData.get(formattedIP);

            final String defaultIcon = Config.isValidProperty(defaultData, "iconOverride") ? defaultData.getIconOverride() : "";
            final String alternateIcon = Config.isValidProperty(alternateData, "iconOverride") ? alternateData.getIconOverride() : defaultIcon;
            final String currentIcon = Config.isValidProperty(primaryData, "iconOverride") ? primaryData.getIconOverride() : alternateIcon;

            resultData = primaryData != null ? primaryData : (alternateData != null ? alternateData : defaultData);
            currentServerIcon = currentIcon;

            // Attempt to find alternative icons, if no overrides are present
            if (StringUtils.isNullOrEmpty(currentServerIcon)) {
                if (CraftPresence.CLIENT.addEndpointIcon(
                        CraftPresence.CONFIG,
                        CraftPresence.CONFIG.advancedSettings.serverIconEndpoint,
                        formattedIP
                )) {
                    currentServerIcon = formattedIP;
                } else {
                    currentServerIcon = currentServer_Name;
                }
            }

            if (isOnLAN) {
                // NOTE: LAN-Only Presence Updates
                resultData = CraftPresence.CONFIG.statusMessages.lanData;
                currentServerMessage = Config.isValidProperty(resultData, "textOverride") ? resultData.getTextOverride() : "";
                currentServerIcon = Config.isValidProperty(resultData, "iconOverride") ? resultData.getIconOverride() : "";
            } else {
                // NOTE: Server-Only Presence Updates
                final String defaultMessage = Config.isValidProperty(defaultData, "textOverride") ? defaultData.getTextOverride() : "";
                final String alternateMessage = alternateData != null && Config.isValidProperty(alternateData, "textOverride") ? alternateData.getTextOverride() : defaultMessage;
                currentServerMessage = primaryData != null && Config.isValidProperty(primaryData, "textOverride") ? primaryData.getTextOverride() : alternateMessage;

                // If join requests are enabled, parse the appropriate data
                // to form party information.
                //
                // Note: The party privacy level is appended by modulus division to prevent
                // it being anything other than valid privacy levels
                if (CraftPresence.CONFIG.generalSettings.enableJoinRequests) {
                    if (!StringUtils.isNullOrEmpty(currentServer_Name) && !currentServer_Name.equalsIgnoreCase(CraftPresence.CONFIG.serverSettings.fallbackServerName)) {
                        CraftPresence.CLIENT.PARTY_ID = "Join Server: " + currentServer_Name;
                    } else {
                        CraftPresence.CLIENT.PARTY_ID = "Join Server: " + currentServer_IP;
                    }
                    CraftPresence.CLIENT.JOIN_SECRET = makeSecret();
                    CraftPresence.CLIENT.PARTY_SIZE = currentPlayers;
                    CraftPresence.CLIENT.PARTY_MAX = maxPlayers;
                    CraftPresence.CLIENT.PARTY_PRIVACY = PartyPrivacy.from(CraftPresence.CONFIG.generalSettings.partyPrivacyLevel % 2);
                }
            }
        } else if (CraftPresence.instance.isSingleplayer()) {
            // NOTE: SinglePlayer-Only Presence Updates
            resultData = CraftPresence.CONFIG.statusMessages.singleplayerData;
            currentServerMessage = Config.isValidProperty(resultData, "textOverride") ? resultData.getTextOverride() : "";
            currentServerIcon = Config.isValidProperty(resultData, "iconOverride") ? resultData.getIconOverride() : "";
        }
        formattedIcon = CraftPresence.CLIENT.imageOf("server.icon", true, currentServerIcon, CraftPresence.CONFIG.serverSettings.fallbackServerIcon);

        CraftPresence.CLIENT.syncOverride(resultData, "server.message", "server.icon");
        CraftPresence.CLIENT.syncArgument("server.message", currentServerMessage);
        CraftPresence.CLIENT.syncArgument("server.icon", formattedIcon);
        queuedForUpdate = false;
    }

    @Override
    public void getAllData() {
        try {
            final ServerList serverList = new ServerList(CraftPresence.instance);
            serverList.loadServerList();
            serverIndex = serverList.countServers();

            for (int currentIndex = 0; currentIndex < serverIndex; currentIndex++) {
                final ServerData data = serverList.getServerData(currentIndex);
                if (!StringUtils.isNullOrEmpty(data.serverIP)) {
                    final String formattedIP = data.serverIP.contains(":") ? StringUtils.formatAddress(data.serverIP, false) : data.serverIP;
                    if (!defaultAddresses.contains(formattedIP)) {
                        defaultAddresses.add(formattedIP);
                    }
                    if (!knownAddresses.contains(formattedIP)) {
                        knownAddresses.add(formattedIP);
                    }
                    if (!knownServerData.containsKey(data.serverIP)) {
                        knownServerData.put(data.serverIP, data);
                    }
                }
            }
        } catch (Exception ex) {
            Constants.LOG.debugError(ex);
        }

        for (String serverEntry : CraftPresence.CONFIG.serverSettings.serverData.keySet()) {
            if (!StringUtils.isNullOrEmpty(serverEntry) && !knownAddresses.contains(serverEntry)) {
                knownAddresses.add(serverEntry);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean state) {
        this.enabled = state;
    }

    @Override
    public boolean isInUse() {
        return isInUse;
    }

    @Override
    public void setInUse(boolean state) {
        if (state && !this.isInUse) {
            CraftPresence.CLIENT.syncTimestamp("data.server.time");
        }
        this.isInUse = state;
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
}
