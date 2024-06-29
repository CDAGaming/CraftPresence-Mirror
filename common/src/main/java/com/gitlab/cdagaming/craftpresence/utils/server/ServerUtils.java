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
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.Config;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.core.impl.ExtendedModule;
import com.gitlab.cdagaming.craftpresence.core.impl.discord.DiscordStatus;
import com.gitlab.cdagaming.craftpresence.core.impl.discord.PartyPrivacy;
import com.gitlab.cdagaming.craftpresence.utils.entity.EntityUtils;
import com.gitlab.cdagaming.unilib.ModUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import io.github.cdagaming.unicore.impl.Pair;
import io.github.cdagaming.unicore.utils.MathUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import io.github.cdagaming.unicore.utils.TimeUtils;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Server Utilities used to Parse Server Data and handle related RPC Events
 *
 * @author CDAGaming
 */
@SuppressWarnings("DuplicatedCode")
public class ServerUtils implements ExtendedModule {
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
     * Whether this module is allowed to start and enabled
     */
    private boolean enabled = false;
    /**
     * Whether this module is active and currently in use
     */
    private boolean isInUse = false;
    /**
     * Whether this module has performed an initial retrieval of config items
     */
    private boolean hasScannedConfig = false;
    /**
     * Whether this module has performed an initial retrieval of internal items
     */
    private boolean hasScannedInternals = false;
    /**
     * Whether this module has performed an initial event sync
     */
    private boolean hasInitialized = false;
    /**
     * Whether placeholders for the server data (if in use) have been initialized
     */
    private boolean hasInitializedServer = false;
    /**
     * Whether placeholders for the realm data (if in use) have been initialized
     */
    private boolean hasInitializedRealm = false;
    /**
     * Whether an initial realm scan has been performed
     */
    private boolean hasCheckedRealm = false;
    /**
     * The IP Address of the Current Server the Player is in
     */
    private String currentServer_IP;
    /**
     * The IP Address of the Current Server the Player is in
     */
    private String formattedServer_IP;
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
     * The Amount of Players in the Current Server the Player is in
     */
    private int currentPlayers;
    /**
     * The Maximum Amount of Players allowed in the Current Server the Player is in
     */
    private int maxPlayers;
    /**
     * The current server list, derived from internal data
     */
    private ServerList serverList;
    /**
     * The amount of Currently detected Server Addresses
     */
    private int serverIndex = 0;
    /**
     * The Current Server Connection Data and Info
     */
    private ServerData currentServerData;
    /**
     * The Current Realm Connection Data and Info
     */
    private RealmsServer currentRealmData;
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
    /**
     * If the Current Server is a Local Single-Player Connection
     */
    private boolean isOnSinglePlayer = false;
    /**
     * If the Current Server is a Realm Connection
     */
    private boolean isOnRealm = false;

    @Override
    public void clearFieldData() {
        currentPlayerList.clear();
        defaultAddresses.clear();
        knownAddresses.clear();
        knownServerData.clear();
        serverList = null;
        serverIndex = 0;
    }

    @Override
    public void clearAttributes() {
        currentServer_IP = null;
        formattedServer_IP = null;
        currentServer_MOTD = null;
        currentServer_MOTD_Lines.clear();
        currentServer_Name = null;
        currentServerData = null;
        currentRealmData = null;
        currentConnection = null;
        currentPlayers = 0;
        maxPlayers = 0;

        queuedForUpdate = false;
        joinInProgress = false;
        isOnLAN = false;
        isOnSinglePlayer = false;
        isOnRealm = false;

        CraftPresence.CLIENT.removeArguments("server", "data.server", "world", "data.world", "player");
        CraftPresence.CLIENT.removeForcedData("server");
        CraftPresence.CLIENT.clearPartyData();
        hasInitialized = false;
        hasInitializedServer = false;
        hasInitializedRealm = false;
        hasCheckedRealm = false;
    }

    @Override
    public void preTick() {
        joinInProgress = CraftPresence.CLIENT.STATUS == DiscordStatus.JoinGame || CraftPresence.CLIENT.STATUS == DiscordStatus.SpectateGame;
    }

    /**
     * Attempt to locate Realm Data from the current connection
     *
     * @param connection The Server Connection Data and Info
     * @return the found realm data, or null
     */
    @SuppressWarnings("RedundantCast")
    private RealmsServer findRealmData(final NetHandlerPlayClient connection) {
        try {
            if (connection.guiScreenServer instanceof GuiScreenRealmsProxy realmsProxy &&
                    realmsProxy.getProxy() instanceof RealmsMainScreen realmsMainScreen) {
                return (RealmsServer) StringUtils.executeMethod(
                        RealmsMainScreen.class, realmsMainScreen,
                        new Class[]{long.class},
                        new Object[]{(long) StringUtils.getField(
                                RealmsMainScreen.class, realmsMainScreen,
                                "selectedServerId"
                        )},
                        "findServer"
                );
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    @Override
    public void updateData() {
        final ServerData newServerData = CraftPresence.instance.getCurrentServerData();
        final NetHandlerPlayClient newConnection = CraftPresence.instance.getConnection();

        if (!joinInProgress) {
            // If connected to a Realm, locate the RealmServer instance
            // before continuing any further in module ticking
            //
            // Note: A Realm is only checked for *once* under set conditions
            if (!hasCheckedRealm) {
                final boolean canCheckRealm = newServerData != null && newConnection != null;
                if (canCheckRealm && CraftPresence.instance.isConnectedToRealms()) {
                    currentRealmData = findRealmData(newConnection);
                    isOnRealm = currentRealmData != null;
                }
                hasCheckedRealm = true;
            }

            if (isOnRealm) {
                processRealmData(newServerData, newConnection);
            } else {
                processServerData(newServerData, newConnection);
            }
        }

        if (queuedForUpdate) {
            if (!hasInitialized) {
                initPresence();
                hasInitialized = true;
            }
            updatePresence();
            queuedForUpdate = false;
        }
    }

    /**
     * Process Server Data from the supplied arguments
     *
     * @param newLANStatus          If the Current Server is on a LAN-Based Connection (A Local Network Game)
     * @param newSinglePlayerStatus If the Current Server is a Local Single-Player Connection
     * @param newServerData         The Current Server Connection Data and Info
     * @param newConnection         The Player's Current Connection Data
     * @param newServer_IP          The IP Address of the Current Server the Player is in
     * @param newServer_MOTD        The Message of the Day of the Current Server the Player is in
     * @param newServer_Name        The Name of the Current Server the Player is in
     * @param newCurrentPlayers     The Amount of Players in the Current Server the Player is in
     * @param newMaxPlayers         The Maximum Amount of Players allowed in the Current Server the Player is in
     * @param newPlayerList         The Current Player Map, if available
     */
    private void processData(final boolean newLANStatus, final boolean newSinglePlayerStatus,
                             final ServerData newServerData, final NetHandlerPlayClient newConnection,
                             final String newServer_IP, final String newServer_MOTD, final String newServer_Name,
                             final int newCurrentPlayers, final int newMaxPlayers, final List<NetworkPlayerInfo> newPlayerList) {
        if (newLANStatus != isOnLAN || newSinglePlayerStatus != isOnSinglePlayer || ((newServerData != null && !newServerData.equals(currentServerData)) ||
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
            isOnSinglePlayer = newSinglePlayerStatus;
            queuedForUpdate = true;

            if (!StringUtils.isNullOrEmpty(currentServer_IP)) {
                formattedServer_IP = currentServer_IP.contains(":") ? StringUtils.formatAddress(currentServer_IP, false) : currentServer_IP;

                if (!isOnRealm && !isOnSinglePlayer && !isOnLAN) {
                    if (!defaultAddresses.contains(formattedServer_IP)) {
                        defaultAddresses.add(formattedServer_IP);
                    }
                    if (!knownAddresses.contains(formattedServer_IP)) {
                        knownAddresses.add(formattedServer_IP);
                    }
                }
            }

            if (serverList != null) {
                serverList.loadServerList();
                if (serverList.countServers() != serverIndex) {
                    queueInternalScan();
                }
            }
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

            if (CraftPresence.ENTITIES.isEnabled()) {
                CraftPresence.ENTITIES.ENTITY_NAMES.removeAll(CraftPresence.ENTITIES.PLAYER_BINDINGS.keySet());
                CraftPresence.ENTITIES.queueInternalScan();
            }
        }
    }

    /**
     * Retrieve the server address from the specified data
     *
     * @param newServerData The Current Server Connection Data and Info
     * @return the found server address
     */
    private String getServerAddress(final ServerData newServerData) {
        return newServerData != null && !StringUtils.isNullOrEmpty(newServerData.serverIP) ? newServerData.serverIP : "127.0.0.1";
    }

    /**
     * Process Realm Data from the supplied arguments
     *
     * @param newServerData The Current Server Connection Data and Info
     * @param newConnection The Player's Current Connection Data
     */
    private void processRealmData(final ServerData newServerData, final NetHandlerPlayClient newConnection) {
        final List<NetworkPlayerInfo> newPlayerList = newConnection != null ? StringUtils.newArrayList(newConnection.getPlayerInfoMap()) : StringUtils.newArrayList();
        final int newCurrentPlayers = newConnection != null ? newConnection.getPlayerInfoMap().size() : 1;

        final String newServer_IP = getServerAddress(newServerData);
        final String newServer_Name = currentRealmData.getName();
        final String newServer_MOTD = !isInvalidMotd(currentRealmData.getDescription()) ?
                StringUtils.stripAllFormatting(currentRealmData.getDescription()) : CraftPresence.CONFIG.serverSettings.fallbackServerMotd;

        processData(false, false,
                newServerData, newConnection,
                newServer_IP, newServer_MOTD, newServer_Name,
                newCurrentPlayers, 10,
                newPlayerList
        );
    }

    /**
     * Process Server Data from the supplied arguments
     *
     * @param newServerData The Current Server Connection Data and Info
     * @param newConnection The Player's Current Connection Data
     */
    private void processServerData(final ServerData newServerData, final NetHandlerPlayClient newConnection) {
        final List<NetworkPlayerInfo> newPlayerList = newConnection != null ? StringUtils.newArrayList(newConnection.getPlayerInfoMap()) : StringUtils.newArrayList();
        final int newCurrentPlayers = newConnection != null ? newConnection.getPlayerInfoMap().size() : 1;
        final int newMaxPlayers = newConnection != null && newConnection.currentServerMaxPlayers >= newCurrentPlayers ? newConnection.currentServerMaxPlayers : newCurrentPlayers + 1;
        final boolean newSinglePlayerStatus = CraftPresence.instance.isSingleplayer();
        final boolean newLANStatus = (newSinglePlayerStatus && newCurrentPlayers > 1) || (newServerData != null && newServerData.isOnLAN());

        final String newServer_IP = getServerAddress(newServerData);
        final String newServer_Name = newServerData != null && !isInvalidName(newServerData.serverName) ? newServerData.serverName : CraftPresence.CONFIG.serverSettings.fallbackServerName;
        final String newServer_MOTD = !newLANStatus && !newSinglePlayerStatus &&
                newServerData != null && !isInvalidMotd(newServerData.serverMOTD) ? StringUtils.stripAllFormatting(newServerData.serverMOTD) : CraftPresence.CONFIG.serverSettings.fallbackServerMotd;

        processData(newLANStatus, newSinglePlayerStatus,
                newServerData, newConnection,
                newServer_IP, newServer_MOTD, newServer_Name,
                newCurrentPlayers, newMaxPlayers,
                newPlayerList
        );
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
        } catch (Throwable ex) {
            printException(ex);
        }
    }

    @Override
    public void initPresence() {
        // Player Position Arguments
        syncArgument("player.position.x", () -> MathUtils.roundDouble(CraftPresence.player.posX, 3));
        syncArgument("player.position.y", () -> MathUtils.roundDouble(CraftPresence.player.posY, 3));
        syncArgument("player.position.z", () -> MathUtils.roundDouble(CraftPresence.player.posZ, 3));

        // Player Health Arguments
        syncArgument("player.health.current", () -> MathUtils.roundDouble(CraftPresence.player.getHealth(), 0));
        syncArgument("player.health.max", () -> MathUtils.roundDouble(CraftPresence.player.getMaxHealth(), 0));

        // World Data Arguments
        syncArgument("world.difficulty", () -> {
            final String newDifficulty = CraftPresence.player.world.getWorldInfo().isHardcoreModeEnabled() && ModUtils.RAW_TRANSLATOR != null ?
                    ModUtils.RAW_TRANSLATOR.translate("selectWorld.gameMode.hardcore") :
                    StringUtils.formatWord(CraftPresence.player.world.getDifficulty().name().toLowerCase());
            return StringUtils.getOrDefault(newDifficulty);
        });
        syncArgument("world.weather.name", () -> {
            final String newWeatherData = EntityUtils.getWeather(CraftPresence.player);
            final String newWeatherName = Constants.TRANSLATOR.translate("craftpresence.defaults.weather." + newWeatherData);
            return StringUtils.getOrDefault(newWeatherName);
        });
        syncArgument("world.name", () -> {
            final String primaryWorldName = CraftPresence.instance.getIntegratedServer() != null ? CraftPresence.instance.getIntegratedServer().getWorldName() : "";
            final String secondaryWorldName = StringUtils.getOrDefault(CraftPresence.player.world.getWorldInfo().getWorldName(), Constants.TRANSLATOR.translate("craftpresence.defaults.world_name"));
            final String newWorldName = StringUtils.getOrDefault(primaryWorldName, secondaryWorldName);
            return StringUtils.getOrDefault(newWorldName);
        });

        // World Time Arguments
        syncArgument("world.time.day", () ->
                TimeUtils.fromWorldTime(CraftPresence.player.world.getWorldTime()).getFirst()
        );
        syncArgument("world.time.format_24", () ->
                        TimeUtils.toString(
                                TimeUtils.fromWorldTime(CraftPresence.player.world.getWorldTime()).getSecond(),
                                "HH:mm"
                        )
                , true);
        syncArgument("world.time.format_12", () ->
                        TimeUtils.toString(
                                TimeUtils.fromWorldTime(CraftPresence.player.world.getWorldTime()).getSecond(),
                                "HH:mm a"
                        )
                , true);
        syncArgument("data.world.time.instance", () ->
                TimeUtils.fromWorldTime(CraftPresence.player.world.getWorldTime())
        );

        // Default Arguments
        syncArgument("server.default.icon", () -> CraftPresence.CONFIG.serverSettings.fallbackServerIcon);

        syncArgument("server.message", () -> {
            final boolean inServer = !isOnRealm && !isOnSinglePlayer && currentServerData != null;
            if (inServer) {
                if (isOnLAN) {
                    final ModuleData primaryData = CraftPresence.CONFIG.statusMessages.lanData;
                    return getResult(Config.isValidProperty(primaryData, "textOverride") ? primaryData.getTextOverride() : "", primaryData);
                } else {
                    final ModuleData defaultData = getDefaultData();
                    final ModuleData alternateData = getData(currentServer_Name);
                    final ModuleData primaryData = getData(formattedServer_IP);

                    final String defaultMessage = Config.isValidProperty(defaultData, "textOverride") ? defaultData.getTextOverride() : "";
                    final String alternateMessage = Config.isValidProperty(alternateData, "textOverride") ? alternateData.getTextOverride() : defaultMessage;
                    return getResult(Config.isValidProperty(primaryData, "textOverride") ? primaryData.getTextOverride() : alternateMessage,
                            formattedServer_IP, currentServer_Name);
                }
            } else if (isOnSinglePlayer) {
                final ModuleData primaryData = CraftPresence.CONFIG.statusMessages.singleplayerData;
                return getResult(Config.isValidProperty(primaryData, "textOverride") ? primaryData.getTextOverride() : "", primaryData);
            } else if (isOnRealm) {
                final ModuleData primaryData = CraftPresence.CONFIG.statusMessages.realmData;
                return getResult(Config.isValidProperty(primaryData, "textOverride") ? primaryData.getTextOverride() : "", primaryData);
            }
            return null;
        });
        syncArgument("server.icon", () -> {
            final boolean inServer = !isOnRealm && !isOnSinglePlayer && currentServerData != null;
            String currentServerIcon = "";
            ModuleData resultData = null;
            if (inServer) {
                if (isOnLAN) {
                    resultData = CraftPresence.CONFIG.statusMessages.lanData;
                    currentServerIcon = Config.isValidProperty(resultData, "iconOverride") ? resultData.getIconOverride() : "";
                } else {
                    final ModuleData defaultData = getDefaultData();
                    final ModuleData alternateData = getData(currentServer_Name);
                    final ModuleData primaryData = getData(formattedServer_IP);
                    resultData = getOrDefault(primaryData, alternateData);

                    final String defaultIcon = Config.isValidProperty(defaultData, "iconOverride") ? defaultData.getIconOverride() : "";
                    final String alternateIcon = Config.isValidProperty(alternateData, "iconOverride") ? alternateData.getIconOverride() : defaultIcon;
                    currentServerIcon = Config.isValidProperty(primaryData, "iconOverride") ? primaryData.getIconOverride() : alternateIcon;

                    // Attempt to find alternative icons, if no overrides are present
                    if (StringUtils.isNullOrEmpty(currentServerIcon)) {
                        if (CraftPresence.CLIENT.addEndpointIcon(
                                CraftPresence.CONFIG,
                                CraftPresence.CONFIG.advancedSettings.serverIconEndpoint,
                                formattedServer_IP,
                                true
                        )) {
                            currentServerIcon = formattedServer_IP;
                        } else {
                            currentServerIcon = currentServer_Name;
                        }
                    }
                }
            } else if (isOnSinglePlayer) {
                resultData = CraftPresence.CONFIG.statusMessages.singleplayerData;
                currentServerIcon = Config.isValidProperty(resultData, "iconOverride") ? resultData.getIconOverride() : "";
            } else if (isOnRealm) {
                resultData = CraftPresence.CONFIG.statusMessages.realmData;
                currentServerIcon = Config.isValidProperty(resultData, "iconOverride") ? resultData.getIconOverride() : "";

                // Attempt to find alternative icons, if no overrides are present
                if (StringUtils.isNullOrEmpty(currentServerIcon)) {
                    // Logic cloned from ScrollableListControl#renderSlotItem
                    final String originalName = currentRealmData.ownerUUID;
                    final boolean isValidUuid = StringUtils.isValidUuid(originalName);
                    if (!CraftPresence.CONFIG.hasChanged() && CraftPresence.CONFIG.advancedSettings.allowEndpointIcons &&
                            !StringUtils.isNullOrEmpty(CraftPresence.CONFIG.advancedSettings.playerSkinEndpoint)) {
                        return CraftPresence.CLIENT.compileData(String.format(
                                        CraftPresence.CONFIG.advancedSettings.playerSkinEndpoint,
                                        originalName
                                ),
                                new Pair<>("player.name", () -> originalName),
                                new Pair<>("player.uuid.full", () -> isValidUuid ? StringUtils.getFromUuid(originalName, false) : ""),
                                new Pair<>("player.uuid.short", () -> isValidUuid ? StringUtils.getFromUuid(originalName, true) : "")
                        ).get().toString();
                    }
                }
            }
            return getResult(CraftPresence.CLIENT.imageOf(true, currentServerIcon, CraftPresence.CONFIG.serverSettings.fallbackServerIcon), resultData);
        });
        CraftPresence.CLIENT.addForcedData("server", () -> {
            if (!isInUse()) return null;

            ModuleData resultData = null;
            if (!isOnRealm && !isOnSinglePlayer && currentServerData != null) {
                if (isOnLAN) {
                    resultData = CraftPresence.CONFIG.statusMessages.lanData;
                } else {
                    resultData = getOrDefault(formattedServer_IP, currentServer_Name);
                }
            } else if (isOnSinglePlayer) {
                resultData = CraftPresence.CONFIG.statusMessages.singleplayerData;
            } else if (isOnRealm) {
                resultData = CraftPresence.CONFIG.statusMessages.realmData;
            }
            return getPresenceData(resultData);
        });
        CraftPresence.CLIENT.syncTimestamp("data.server.time");
    }

    private void initServerArgs() {
        // Player Amount Arguments
        syncArgument("server.players.current", () -> currentPlayers);
        syncArgument("server.players.max", () -> maxPlayers);

        // Server Data Arguments (Multiplayer)
        syncArgument("server.address.full", () -> currentServer_IP);
        syncArgument("server.address.short", () -> formattedServer_IP);
        syncArgument("server.name", () -> currentServer_Name);
        syncArgument("server.motd.raw", () -> currentServer_MOTD);
        if (!currentServer_MOTD_Lines.isEmpty()) {
            for (int i = 0; i < currentServer_MOTD_Lines.size(); i++) {
                final int index = i + 1;
                syncArgument("data.server.motd.line_" + index, () -> currentServer_MOTD_Lines.get(index));
            }
        }
    }

    private void initRealmArgs() {
        // Setup Realm Exclusive Data
        syncArgument("server.minigame", () -> currentRealmData.getMinigameName());
    }

    @Override
    public void updatePresence() {
        if (!isOnSinglePlayer && currentServerData != null) {
            if (!hasInitializedServer) {
                initServerArgs();
                hasInitializedServer = true;
            }

            if (isOnRealm && !hasInitializedRealm) {
                initRealmArgs();
                hasInitializedRealm = true;
            }

            if (!isOnRealm && !isOnLAN) {
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
        }
    }

    @Override
    public void getInternalData() {
        try {
            if (serverList == null) {
                serverList = new ServerList(CraftPresence.instance);
                serverList.loadServerList();
            }
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
        } catch (Throwable ex) {
            printException(ex);
        }
    }

    @Override
    public void getConfigData() {
        for (String serverEntry : CraftPresence.CONFIG.serverSettings.serverData.keySet()) {
            if (!StringUtils.isNullOrEmpty(serverEntry) && !knownAddresses.contains(serverEntry)) {
                knownAddresses.add(serverEntry);
            }
        }
    }

    @Override
    public void syncArgument(String argumentName, Supplier<Boolean> condition, Supplier<Object> event, boolean plain) {
        CraftPresence.CLIENT.syncArgument(argumentName, getModuleFunction(condition, event), plain);
    }

    @Override
    public ModuleData getData(String key) {
        return CraftPresence.CONFIG.serverSettings.serverData.get(key);
    }

    @Override
    public String getOverrideText(ModuleData data) {
        return CraftPresence.CLIENT.getOverrideText(getPresenceData(data));
    }

    @Override
    public boolean hasScannedInternals() {
        return hasScannedInternals;
    }

    @Override
    public void setScannedInternals(final boolean state) {
        hasScannedInternals = state;
    }

    @Override
    public boolean canFetchConfig() {
        return CraftPresence.CONFIG != null;
    }

    @Override
    public boolean hasScannedConfig() {
        return hasScannedConfig;
    }

    @Override
    public void setScannedConfig(final boolean state) {
        hasScannedConfig = state;
    }

    @Override
    public boolean canBeEnabled() {
        return !CraftPresence.CONFIG.hasChanged() ? CraftPresence.CONFIG.generalSettings.detectWorldData : isEnabled();
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
    public boolean canBeUsed() {
        return CraftPresence.player != null && !joinInProgress;
    }

    @Override
    public boolean isInUse() {
        return isInUse;
    }

    @Override
    public void setInUse(boolean state) {
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
