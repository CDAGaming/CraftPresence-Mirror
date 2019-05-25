package com.gitlab.cdagaming.craftpresence.handler.server;

import com.gitlab.cdagaming.craftpresence.Constants;
import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.handler.FileHandler;
import com.gitlab.cdagaming.craftpresence.handler.StringHandler;
import com.gitlab.cdagaming.craftpresence.handler.discord.assets.DiscordAsset;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.NetHandlerPlayClient;

import java.util.List;

public class ServerHandler {
    public boolean enabled = false, isInUse = false;

    public List<String> knownAddresses = Lists.newArrayList();
    public String currentServer_IP;
    private String currentServer_Name, currentServer_MOTD, currentServerMSG, timeString;
    private int currentPlayers, maxPlayers, serverIndex;
    private ServerData currentServerData, requestedServerData;
    private NetHandlerPlayClient currentConnection;

    private boolean queuedForUpdate = false, joinInProgress = false, isOnLAN = false;

    private void emptyData() {
        knownAddresses.clear();
        clearClientData();
    }

    public void clearClientData() {
        currentServer_IP = null;
        currentServer_MOTD = null;
        currentServer_Name = null;
        currentServerData = null;
        currentConnection = null;
        timeString = null;
        currentPlayers = 0;
        maxPlayers = 0;

        queuedForUpdate = false;
        isOnLAN = false;
        isInUse = false;

        if (!joinInProgress) {
            requestedServerData = null;
        }
    }

    public void onTick() {
        joinInProgress = StringHandler.isNullOrEmpty(CraftPresence.CLIENT.STATUS) || (CraftPresence.CLIENT.STATUS.equalsIgnoreCase("joinGame") || CraftPresence.CLIENT.STATUS.equalsIgnoreCase("spectateGame"));
        enabled = !CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.showGameState : enabled;
        final boolean needsUpdate = enabled && knownAddresses.isEmpty();

        if (needsUpdate) {
            getServerAddresses();
        }

        if (enabled) {
            if (CraftPresence.player != null && !joinInProgress) {
                isInUse = true;
                updateServerData();
            } else {
                clearClientData();
            }
        } else {
            emptyData();
        }

        if (joinInProgress && requestedServerData != null) {
            joinServer(requestedServerData);
        }
    }

    private void updateServerData() {
        final ServerData newServerData = CraftPresence.instance.getCurrentServerData();
        final NetHandlerPlayClient newConnection = CraftPresence.instance.getConnection();

        if (!joinInProgress) {
            final int newCurrentPlayers = newConnection != null ? newConnection.getPlayerInfoMap().size() : 1;
            final int newMaxPlayers = newConnection != null && newConnection.currentServerMaxPlayers >= newCurrentPlayers ? newConnection.currentServerMaxPlayers : newCurrentPlayers + 1;
            final boolean newLANStatus = (CraftPresence.instance.isSingleplayer() && newCurrentPlayers > 1) || (newServerData != null && newServerData.isOnLAN());

            final String newServer_IP = newServerData != null && !StringHandler.isNullOrEmpty(newServerData.serverIP) ? newServerData.serverIP : "127.0.0.1";
            final String newServer_Name = newServerData != null && !StringHandler.isNullOrEmpty(newServerData.serverName) ? newServerData.serverName : CraftPresence.CONFIG.defaultServerName;
            final String newServer_MOTD = !isOnLAN && !CraftPresence.instance.isSingleplayer() && (newServerData != null && !StringHandler.isNullOrEmpty(newServerData.serverMOTD)) && !(newServerData.serverMOTD.equalsIgnoreCase(Constants.TRANSLATOR.translate("craftpresence.multiplayer.status.cannot_connect")) || newServerData.serverMOTD.equalsIgnoreCase(Constants.TRANSLATOR.translate("craftpresence.multiplayer.status.cannot_resolve")) || newServerData.serverMOTD.equalsIgnoreCase(Constants.TRANSLATOR.translate("craftpresence.multiplayer.status.polling")) || newServerData.serverMOTD.equalsIgnoreCase(Constants.TRANSLATOR.translate("craftpresence.multiplayer.status.pinging"))) ? StringHandler.stripColors(newServerData.serverMOTD) : CraftPresence.CONFIG.defaultServerMOTD;
            final String newGameTime = CraftPresence.player != null ? getTimeString(CraftPresence.player.world.getWorldTime()) : null;

            if (newLANStatus != isOnLAN || ((newServerData != null && !newServerData.equals(currentServerData)) || (newServerData == null && currentServerData != null)) || (newConnection != null && !newConnection.equals(currentConnection)) || !newServer_IP.equals(currentServer_IP) || (!StringHandler.isNullOrEmpty(newServer_MOTD) && !newServer_MOTD.equals(currentServer_MOTD)) || (!StringHandler.isNullOrEmpty(newServer_Name) && !newServer_Name.equals(currentServer_Name))) {
                currentServer_IP = newServer_IP;
                currentServer_MOTD = newServer_MOTD;
                currentServer_Name = newServer_Name;
                currentServerData = newServerData;
                currentConnection = newConnection;
                isOnLAN = newLANStatus;
                queuedForUpdate = true;

                if (!StringHandler.isNullOrEmpty(currentServer_IP) && !knownAddresses.contains(currentServer_IP.contains(":") ? currentServer_IP : StringHandler.formatIP(currentServer_IP, false))) {
                    knownAddresses.add(currentServer_IP.contains(":") ? currentServer_IP : StringHandler.formatIP(currentServer_IP, false));
                }

                final ServerList serverList = new ServerList(CraftPresence.instance);
                serverList.loadServerList();
                if (serverList.countServers() != serverIndex || CraftPresence.CONFIG.serverMessages.length != serverIndex) {
                    getServerAddresses();
                }
            }

            // NOTE: Universal Events
            if (!StringHandler.isNullOrEmpty(currentServerMSG)) {
                if (currentServerMSG.toLowerCase().contains("&time&") && (!StringHandler.isNullOrEmpty(newGameTime) && !newGameTime.equals(timeString))) {
                    timeString = newGameTime;
                    queuedForUpdate = true;
                }

                if ((currentServerMSG.toLowerCase().contains("&players&") || CraftPresence.CONFIG.enableJoinRequest) && (newCurrentPlayers != currentPlayers || newMaxPlayers != maxPlayers)) {
                    currentPlayers = newCurrentPlayers;
                    maxPlayers = newMaxPlayers;
                    queuedForUpdate = true;
                }
            }
        }

        if (queuedForUpdate) {
            updateServerPresence();
        }
    }

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

    private String makeSecret() {
        String formattedKey = CraftPresence.CLIENT.CLIENT_ID + "";
        boolean containsServerName = false;
        boolean containsServerIP = false;

        if (!StringHandler.isNullOrEmpty(currentServer_Name)) {
            formattedKey += "-" + currentServer_Name.toLowerCase();
            containsServerName = true;
        }
        if (!StringHandler.isNullOrEmpty(currentServer_IP)) {
            formattedKey += "-" + currentServer_IP.toLowerCase();
            containsServerIP = true;
        }

        formattedKey += ";" + containsServerName + ";" + containsServerIP;
        return formattedKey;
    }

    public void verifyAndJoin(final String secret) {
        String[] boolParts = secret.split(";");
        String[] stringParts = boolParts[0].split("-");
        boolean containsValidClientID = StringHandler.elementExists(stringParts, 0) && (stringParts[0].length() == 18 && StringHandler.isValidLong(stringParts[0]));
        boolean containsServerName = StringHandler.elementExists(boolParts, 1) && StringHandler.elementExists(stringParts, 1) && Boolean.parseBoolean(boolParts[1]);
        boolean containsServerIP = StringHandler.elementExists(boolParts, 2) && StringHandler.elementExists(stringParts, 2) && Boolean.parseBoolean(boolParts[2]);
        String serverName = containsServerName ? stringParts[1] : CraftPresence.CONFIG.defaultServerName;
        String serverIP = containsServerIP ? stringParts[2] : "";
        boolean isValidSecret = boolParts.length <= 4 && stringParts.length <= 3 && containsValidClientID;

        if (isValidSecret) {
            if (CraftPresence.CONFIG.enableJoinRequest) {
                requestedServerData = new ServerData(serverName, serverIP, false);
            } else {
                Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.warning.config.disabled.enablejoinrequest"));
            }
        } else {
            Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.discord.join", secret));
        }
    }

    private void joinServer(final ServerData serverData) {
        try {
            if (CraftPresence.player != null) {
                CraftPresence.player.world.sendQuittingDisconnectingPacket();
                CraftPresence.instance.loadWorld(null);
            }
            CraftPresence.instance.displayGuiScreen(new GuiConnecting(CraftPresence.instance.currentScreen != null ? CraftPresence.instance.currentScreen : new GuiMainMenu(), CraftPresence.instance, serverData));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            requestedServerData = null;
        }
    }

    public void updateServerPresence() {
        if (!CraftPresence.instance.isSingleplayer() && !isOnLAN && currentServerData != null) {
            // NOTE: Server-Only Presence Updates
            final String defaultServerMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.serverMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
            final String alternateServerMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.serverMessages, currentServer_Name, 0, 1, CraftPresence.CONFIG.splitCharacter, defaultServerMSG);
            final String alternateServerIcon = StringHandler.getConfigPart(CraftPresence.CONFIG.serverMessages, currentServer_Name, 0, 2, CraftPresence.CONFIG.splitCharacter, currentServer_Name);

            currentServerMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.serverMessages, StringHandler.formatIP(currentServer_IP, false), 0, 1, CraftPresence.CONFIG.splitCharacter, alternateServerMSG);
            final String currentServerIcon = StringHandler.getConfigPart(CraftPresence.CONFIG.serverMessages, StringHandler.formatIP(currentServer_IP, false), 0, 2, CraftPresence.CONFIG.splitCharacter, alternateServerIcon);
            final String formattedServerIconKey = StringHandler.formatPackIcon(currentServerIcon.replace(" ", "_"));

            CraftPresence.CLIENT.GAME_STATE = currentServerMSG.replace("&ip&", StringHandler.formatIP(currentServer_IP, false)).replace("&name&", currentServer_Name).replace("&motd&", currentServer_MOTD).replace("&players&", CraftPresence.CONFIG.playerAmountPlaceholderMSG.replace("&current&", Integer.toString(currentPlayers)).replace("&max&", Integer.toString(maxPlayers))).replace("&ign&", CraftPresence.CONFIG.playerPlaceholderMSG.replace("&name&", Constants.USERNAME)).replace("&time&", CraftPresence.CONFIG.gameTimePlaceholderMSG.replace("&worldtime&", !StringHandler.isNullOrEmpty(timeString) ? timeString : "")).replace("&mods&", CraftPresence.CONFIG.modsPlaceholderMSG.replace("&modcount&", Integer.toString(FileHandler.getModCount())));
            if (CraftPresence.CONFIG.enableJoinRequest) {
                if (!StringHandler.isNullOrEmpty(currentServer_Name) && !currentServer_Name.equalsIgnoreCase(CraftPresence.CONFIG.defaultServerName)) {
                    CraftPresence.CLIENT.PARTY_ID = "Join Server: " + currentServer_Name;
                } else {
                    CraftPresence.CLIENT.PARTY_ID = "Join Server: " + currentServer_IP;
                }
                CraftPresence.CLIENT.JOIN_SECRET = makeSecret();
                CraftPresence.CLIENT.PARTY_SIZE = currentPlayers;
                CraftPresence.CLIENT.PARTY_MAX = maxPlayers;
            }

            if (!CraftPresence.CONFIG.overwriteServerIcon || !CraftPresence.packFound) {
                CraftPresence.CLIENT.setImage(formattedServerIconKey.replace("&icon&", CraftPresence.CONFIG.defaultServerIcon), DiscordAsset.AssetType.SMALL);
                CraftPresence.CLIENT.SMALLIMAGETEXT = CraftPresence.CLIENT.GAME_STATE;
            }

            CraftPresence.CLIENT.updatePresence(CraftPresence.CLIENT.buildRichPresence());
            queuedForUpdate = false;
        } else if (isOnLAN) {
            // NOTE: LAN-Only Presence Updates
            final String alternateServerIcon = StringHandler.getConfigPart(CraftPresence.CONFIG.serverMessages, currentServer_Name, 0, 2, CraftPresence.CONFIG.splitCharacter, currentServer_Name);
            final String currentServerIcon = StringHandler.getConfigPart(CraftPresence.CONFIG.serverMessages, StringHandler.formatIP(currentServer_IP, false), 0, 2, CraftPresence.CONFIG.splitCharacter, alternateServerIcon);
            final String formattedServerIconKey = StringHandler.formatPackIcon(currentServerIcon.replace(" ", "_"));

            currentServerMSG = CraftPresence.CONFIG.lanMSG;
            CraftPresence.CLIENT.GAME_STATE = currentServerMSG.replace("&ip&", StringHandler.formatIP(currentServer_IP, false)).replace("&name&", currentServer_Name).replace("&motd&", currentServer_MOTD).replace("&players&", CraftPresence.CONFIG.playerAmountPlaceholderMSG.replace("&current&", Integer.toString(currentPlayers)).replace("&max&", Integer.toString(maxPlayers))).replace("&ign&", CraftPresence.CONFIG.playerPlaceholderMSG.replace("&name&", Constants.USERNAME)).replace("&time&", CraftPresence.CONFIG.gameTimePlaceholderMSG.replace("&worldtime&", !StringHandler.isNullOrEmpty(timeString) ? timeString : "")).replace("&mods&", CraftPresence.CONFIG.modsPlaceholderMSG.replace("&modcount&", Integer.toString(FileHandler.getModCount())));

            if (!CraftPresence.CONFIG.overwriteServerIcon || !CraftPresence.packFound) {
                CraftPresence.CLIENT.setImage(formattedServerIconKey.replace("&icon&", CraftPresence.CONFIG.defaultServerIcon), DiscordAsset.AssetType.SMALL);
                CraftPresence.CLIENT.SMALLIMAGETEXT = CraftPresence.CLIENT.GAME_STATE;
            }

            CraftPresence.CLIENT.updatePresence(CraftPresence.CLIENT.buildRichPresence());
            queuedForUpdate = false;
        } else if (CraftPresence.instance.isSingleplayer()) {
            // NOTE: SinglePlayer-Only Presence Updates
            currentServerMSG = CraftPresence.CONFIG.singleplayerMSG;

            CraftPresence.CLIENT.GAME_STATE = currentServerMSG.replace("&ign&", CraftPresence.CONFIG.playerPlaceholderMSG.replace("&name&", Constants.USERNAME)).replace("&time&", CraftPresence.CONFIG.gameTimePlaceholderMSG.replace("&worldtime&", !StringHandler.isNullOrEmpty(timeString) ? timeString : "")).replace("&mods&", CraftPresence.CONFIG.modsPlaceholderMSG.replace("&modcount&", Integer.toString(FileHandler.getModCount())));
            CraftPresence.CLIENT.SMALLIMAGEKEY = "";
            CraftPresence.CLIENT.SMALLIMAGETEXT = "";
            CraftPresence.CLIENT.updatePresence(CraftPresence.CLIENT.buildRichPresence());
            queuedForUpdate = false;
        }
    }

    public void getServerAddresses() {
        final ServerList serverList = new ServerList(CraftPresence.instance);
        serverList.loadServerList();
        serverIndex = serverList.countServers();

        for (int currentIndex = 0; currentIndex < serverIndex; currentIndex++) {
            final ServerData data = serverList.getServerData(currentIndex);
            if (!StringHandler.isNullOrEmpty(data.serverIP) && !knownAddresses.contains(data.serverIP.contains(":") ? data.serverIP : StringHandler.formatIP(data.serverIP, false))) {
                knownAddresses.add(data.serverIP.contains(":") ? data.serverIP : StringHandler.formatIP(data.serverIP, false));
            }
        }

        for (String serverMessage : CraftPresence.CONFIG.serverMessages) {
            if (!StringHandler.isNullOrEmpty(serverMessage)) {
                final String[] part = serverMessage.split(CraftPresence.CONFIG.splitCharacter);
                if (!StringHandler.isNullOrEmpty(part[0]) && !knownAddresses.contains(part[0])) {
                    knownAddresses.add(part[0]);
                }
            }
        }
    }
}
