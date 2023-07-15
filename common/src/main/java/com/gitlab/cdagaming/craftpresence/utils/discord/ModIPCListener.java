/*
 * MIT License
 *
 * Copyright (c) 2018 - 2023 CDAGaming (cstack2011@yahoo.com)
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

package com.gitlab.cdagaming.craftpresence.utils.discord;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.impl.discord.DiscordStatus;
import com.gitlab.cdagaming.craftpresence.core.impl.discord.PartyPrivacy;
import com.gitlab.cdagaming.craftpresence.utils.gui.RenderUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.CommandsGui;
import com.google.gson.JsonObject;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.Packet;
import com.jagrosh.discordipc.entities.User;

/**
 * Listener to Interpret Discord IPC Events, on received
 * <p>See {@link IPCListener} for more Info
 *
 * @author CDAGaming
 */
public class ModIPCListener implements IPCListener {
    @Override
    public void onActivityJoin(IPCClient client, String secret) {
        // On Accepting and Queuing a Join Request
        if (CraftPresence.CLIENT.STATUS != DiscordStatus.JoinGame) {
            CraftPresence.CLIENT.STATUS = DiscordStatus.JoinGame;
            CraftPresence.SERVER.verifyAndJoin(secret);
        }
    }

    @Override
    public void onActivitySpectate(IPCClient client, String secret) {
        // Spectating Game, Unimplemented for now
        if (CraftPresence.CLIENT.STATUS != DiscordStatus.SpectateGame) {
            CraftPresence.CLIENT.STATUS = DiscordStatus.SpectateGame;
        }
    }

    @Override
    public void onActivityJoinRequest(IPCClient client, String secret, User user) {
        // On Receiving a New Join Request (Applies when Party is not Public)
        if (CraftPresence.CLIENT.PARTY_PRIVACY != PartyPrivacy.Public && (
                CraftPresence.CLIENT.STATUS != DiscordStatus.JoinRequest ||
                        !CraftPresence.CLIENT.REQUESTER_USER.equals(user)
        )) {
            CraftPresence.SCHEDULER.TIMER = 30;
            CraftPresence.CLIENT.STATUS = DiscordStatus.JoinRequest;
            CraftPresence.CLIENT.REQUESTER_USER = user;

            if (!(CraftPresence.instance.currentScreen instanceof CommandsGui)) {
                RenderUtils.openScreen(CraftPresence.instance, new CommandsGui(CraftPresence.instance.currentScreen, "request"));
            } else {
                ((CommandsGui) CraftPresence.instance.currentScreen).executeCommand("request");
            }
        }
    }

    @Override
    public void onClose(IPCClient client, JsonObject json) {
        // Closing the Game, nothing else to do here
        if (CraftPresence.CLIENT.isAvailable()) {
            CraftPresence.CLIENT.STATUS = DiscordStatus.Closed;
        }
    }

    @Override
    public void onDisconnect(IPCClient client, Throwable t) {
        if (CraftPresence.CLIENT.STATUS != DiscordStatus.Disconnected) {
            Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.rpc", t.getMessage()));
            CraftPresence.CLIENT.shutDown(true);
        }
    }

    @Override
    public void onPacketReceived(IPCClient client, Packet packet) {
        // N/A
    }

    @Override
    public void onPacketSent(IPCClient client, Packet packet) {
        // N/A
    }

    @Override
    public void onReady(IPCClient client) {
        if (CraftPresence.CLIENT.STATUS != DiscordStatus.Ready) {
            CraftPresence.CLIENT.STATUS = DiscordStatus.Ready;
            CraftPresence.CLIENT.CURRENT_USER = client.getCurrentUser();
            Constants.LOG.info(Constants.TRANSLATOR.translate("craftpresence.logger.info.load", CraftPresence.CLIENT.CLIENT_ID, CraftPresence.CLIENT.CURRENT_USER != null ? CraftPresence.CLIENT.CURRENT_USER.getEffectiveName() : "null"));
        }
    }
}
