package com.gitlab.cdagaming.craftpresence.handler;

import com.gitlab.cdagaming.craftpresence.Constants;
import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.handler.curse.ManifestHandler;
import com.gitlab.cdagaming.craftpresence.handler.discord.assets.DiscordAsset;
import com.gitlab.cdagaming.craftpresence.handler.discord.assets.DiscordAssetHandler;
import com.gitlab.cdagaming.craftpresence.handler.mcupdater.MCUpdaterHandler;
import com.gitlab.cdagaming.craftpresence.handler.multimc.InstanceHandler;
import com.gitlab.cdagaming.craftpresence.handler.technic.PackHandler;
import net.minecraft.client.Minecraft;

public class CommandHandler {
    public static void reloadData(final boolean forceUpdateRPC) {
        CraftPresence.instance = Minecraft.getMinecraft();
        CraftPresence.player = CraftPresence.instance.player;

        Constants.TRANSLATOR.tick();
        CraftPresence.KEYBINDINGS.onTick();
        CraftPresence.BIOMES.onTick();
        CraftPresence.DIMENSIONS.onTick();
        CraftPresence.GUIS.onTick();
        CraftPresence.ENTITIES.onTick();
        CraftPresence.SERVER.onTick();

        if (forceUpdateRPC) {
            if (CraftPresence.DIMENSIONS.isInUse) {
                CraftPresence.DIMENSIONS.updateDimensionPresence();
            }
            if (CraftPresence.GUIS.isInUse) {
                CraftPresence.GUIS.updateGUIPresence();
            }
            if (CraftPresence.ENTITIES.isInUse) {
                CraftPresence.ENTITIES.updateEntityPresence();
            }
            if (CraftPresence.SERVER.isInUse) {
                CraftPresence.SERVER.updateServerPresence();
            }
            if (CraftPresence.BIOMES.isInUse) {
                CraftPresence.BIOMES.updateBiomePresence();
            }
        }
    }

    public static void rebootRPC() {
        CraftPresence.CLIENT.shutDown();
        if (!CraftPresence.CLIENT.CLIENT_ID.equals(CraftPresence.CONFIG.clientID)) {
            DiscordAssetHandler.emptyData();
            CraftPresence.CLIENT.CLIENT_ID = CraftPresence.CONFIG.clientID;
        }
        DiscordAssetHandler.loadAssets();
        CraftPresence.CLIENT.init();
        CraftPresence.CLIENT.updatePresence(CraftPresence.CLIENT.buildRichPresence());
    }

    public static void init() {
        if (CraftPresence.CONFIG.detectCurseManifest && !CraftPresence.packFound) {
            ManifestHandler.loadManifest();
        }
        if (CraftPresence.CONFIG.detectMultiMCManifest && !CraftPresence.packFound) {
            InstanceHandler.loadInstance();
        }
        if (CraftPresence.CONFIG.detectMCUpdaterInstance && !CraftPresence.packFound) {
            MCUpdaterHandler.loadInstance();
        }
        if (CraftPresence.CONFIG.detectTechnicPack && !CraftPresence.packFound) {
            PackHandler.loadPack();
        }
        DiscordAssetHandler.loadAssets();
    }

    public static void setMainMenuPresence() {
        CraftPresence.CLIENT.STATUS = "ready";
        CraftPresence.CLIENT.SMALLIMAGEKEY = "";
        CraftPresence.CLIENT.SMALLIMAGETEXT = "";
        CraftPresence.CLIENT.GAME_STATE = "";
        CraftPresence.CLIENT.PARTY_ID = "";
        CraftPresence.CLIENT.PARTY_SIZE = 0;
        CraftPresence.CLIENT.PARTY_MAX = 0;
        CraftPresence.CLIENT.JOIN_SECRET = "";
        CraftPresence.CLIENT.DETAILS = CraftPresence.SERVER.enabled ? CraftPresence.CONFIG.mainmenuMSG.replace("&ign&", CraftPresence.CONFIG.playerPlaceholderMSG.replace("&name&", Constants.USERNAME)).replace("&mods&", CraftPresence.CONFIG.modsPlaceholderMSG.replace("&modcount&", Integer.toString(FileHandler.getModCount()))) : "";
        CraftPresence.CLIENT.setImage(CraftPresence.CONFIG.defaultIcon, DiscordAsset.AssetType.LARGE);
        CraftPresence.CLIENT.LARGEIMAGETEXT = Constants.TRANSLATOR.translate("craftpresence.defaults.state.mcversion", Constants.MCVersion);
        CraftPresence.CLIENT.updatePresence(CraftPresence.CLIENT.buildRichPresence());
    }

    public static Boolean isOnMainMenuPresence() {
        return !CraftPresence.CONFIG.hasChanged &&
                StringHandler.isNullOrEmpty(CraftPresence.CLIENT.SMALLIMAGEKEY) &&
                StringHandler.isNullOrEmpty(CraftPresence.CLIENT.SMALLIMAGETEXT) &&
                StringHandler.isNullOrEmpty(CraftPresence.CLIENT.GAME_STATE) &&
                StringHandler.isNullOrEmpty(CraftPresence.CLIENT.PARTY_ID) &&
                CraftPresence.CLIENT.PARTY_SIZE == 0 &&
                CraftPresence.CLIENT.PARTY_MAX == 0 &&
                StringHandler.isNullOrEmpty(CraftPresence.CLIENT.JOIN_SECRET) &&
                (!StringHandler.isNullOrEmpty(CraftPresence.CLIENT.DETAILS) &&
                        CraftPresence.CLIENT.DETAILS.equals(CraftPresence.SERVER.enabled ? CraftPresence.CONFIG.mainmenuMSG.replace("&ign&", CraftPresence.CONFIG.playerPlaceholderMSG.replace("&name&", Constants.USERNAME)).replace("&mods&", CraftPresence.CONFIG.modsPlaceholderMSG.replace("&modcount&", Integer.toString(FileHandler.getModCount()))) : "")
                ) &&
                (!StringHandler.isNullOrEmpty(CraftPresence.CLIENT.LARGEIMAGEKEY) &&
                        CraftPresence.CLIENT.LARGEIMAGEKEY.equals(CraftPresence.CONFIG.defaultIcon)
                ) &&
                (!StringHandler.isNullOrEmpty(CraftPresence.CLIENT.LARGEIMAGETEXT) &&
                        CraftPresence.CLIENT.LARGEIMAGETEXT.equals(Constants.TRANSLATOR.translate("craftpresence.defaults.state.mcversion", Constants.MCVersion))
                ) &&
                (!StringHandler.isNullOrEmpty(CraftPresence.CLIENT.STATUS) && CraftPresence.CLIENT.STATUS.equalsIgnoreCase("ready"));
    }
}
