package com.gitlab.cdagaming.craftpresence.config.gui;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.handler.CommandHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

public class ConfigGUI_Main extends GuiScreen {
    private final GuiScreen parentscreen, currentscreen;
    private EntityPlayer player;
    private GuiButton generalSet, biomeSet, dimensionSet, serverSet, statusSet, advancedSet, proceedButton, aboutButton;

    public ConfigGUI_Main(GuiScreen parentScreen) {
        mc = Minecraft.getMinecraft();
        player = mc.player;
        currentscreen = this;
        parentscreen = parentScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        ScaledResolution sr = new ScaledResolution(mc);

        generalSet = new GuiButton(100, (sr.getScaledWidth() / 2) - 90, CraftPresence.GUIS.getButtonY(1), 180, 20, I18n.format("gui.config.title.general"));
        biomeSet = new GuiButton(200, (sr.getScaledWidth() / 2) - 90, CraftPresence.GUIS.getButtonY(2), 180, 20, I18n.format("gui.config.title.biomemessages"));
        dimensionSet = new GuiButton(300, (sr.getScaledWidth() / 2) - 90, CraftPresence.GUIS.getButtonY(3), 180, 20, I18n.format("gui.config.title.dimensionmessages"));
        serverSet = new GuiButton(400, (sr.getScaledWidth() / 2) - 90, CraftPresence.GUIS.getButtonY(4), 180, 20, I18n.format("gui.config.title.servermessages"));
        statusSet = new GuiButton(500, (sr.getScaledWidth() / 2) - 90, CraftPresence.GUIS.getButtonY(5), 180, 20, I18n.format("gui.config.title.statusmessages"));
        advancedSet = new GuiButton(600, (sr.getScaledWidth() / 2) - 90, CraftPresence.GUIS.getButtonY(6), 180, 20, I18n.format("gui.config.title.advanced"));
        proceedButton = new GuiButton(700, (sr.getScaledWidth() / 2) - 90, (sr.getScaledHeight() - 30), 180, 20, "Back");
        aboutButton = new GuiButton(800, 10, (sr.getScaledHeight() - 30), 20, 20, "?");

        buttonList.add(generalSet);
        buttonList.add(biomeSet);
        buttonList.add(dimensionSet);
        buttonList.add(serverSet);
        buttonList.add(statusSet);
        buttonList.add(advancedSet);
        buttonList.add(proceedButton);
        buttonList.add(aboutButton);
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        String title = I18n.format("gui.config.title");
        ScaledResolution sr = new ScaledResolution(mc);
        drawDefaultBackground();
        drawString(fontRenderer, title, (sr.getScaledWidth() / 2) - (fontRenderer.getStringWidth(title) / 2), 20, 0xFFFFFF);

        biomeSet.enabled = CraftPresence.CONFIG.showCurrentBiome;
        dimensionSet.enabled = CraftPresence.CONFIG.showCurrentDimension;
        serverSet.enabled = CraftPresence.CONFIG.showGameState;
        statusSet.enabled = CraftPresence.CONFIG.showGameState;

        if (CraftPresence.CONFIG.hasChanged) {
            proceedButton.displayString = "Save";
        } else {
            proceedButton.displayString = "Back";
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (generalSet.isMouseOver()) {
            drawHoveringText(CraftPresence.GUIS.formatText(I18n.format("gui.config.comment.title.general")), mouseX, mouseY);
        }
        if (biomeSet.isMouseOver()) {
            if (!biomeSet.enabled) {
                drawHoveringText(CraftPresence.GUIS.formatText(I18n.format("gui.config.hoverMessage.access", I18n.format("gui.config.name.general.showbiome"))), mouseX, mouseY);
            } else {
                drawHoveringText(CraftPresence.GUIS.formatText(I18n.format("gui.config.comment.title.biomemessages")), mouseX, mouseY);
            }
        }
        if (dimensionSet.isMouseOver()) {
            if (!dimensionSet.enabled) {
                drawHoveringText(CraftPresence.GUIS.formatText(I18n.format("gui.config.hoverMessage.access", I18n.format("gui.config.name.general.showdimension"))), mouseX, mouseY);
            } else {
                drawHoveringText(CraftPresence.GUIS.formatText(I18n.format("gui.config.comment.title.dimensionmessages")), mouseX, mouseY);
            }
        }
        if (serverSet.isMouseOver()) {
            if (!serverSet.enabled) {
                drawHoveringText(CraftPresence.GUIS.formatText(I18n.format("gui.config.hoverMessage.access", I18n.format("gui.config.name.general.showstate"))), mouseX, mouseY);
            } else {
                drawHoveringText(CraftPresence.GUIS.formatText(I18n.format("gui.config.comment.title.servermessages")), mouseX, mouseY);
            }
        }
        if (statusSet.isMouseOver()) {
            if (!statusSet.enabled) {
                drawHoveringText(CraftPresence.GUIS.formatText(I18n.format("gui.config.hoverMessage.access", I18n.format("gui.config.name.general.showstate"))), mouseX, mouseY);
            } else {
                drawHoveringText(CraftPresence.GUIS.formatText(I18n.format("gui.config.comment.title.statusmessages")), mouseX, mouseY);
            }
        }
        if (advancedSet.isMouseOver()) {
            drawHoveringText(CraftPresence.GUIS.formatText(I18n.format("gui.config.comment.title.advanced")), mouseX, mouseY);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == generalSet.id) {
            mc.displayGuiScreen(new ConfigGUI_GeneralSettings(currentscreen));
        } else if (button.id == biomeSet.id) {
            mc.displayGuiScreen(new ConfigGUI_BiomeSettings(currentscreen));
        } else if (button.id == dimensionSet.id) {
            mc.displayGuiScreen(new ConfigGUI_DimensionSettings(currentscreen));
        } else if (button.id == serverSet.id) {
            mc.displayGuiScreen(new ConfigGUI_ServerSettings(currentscreen));
        } else if (button.id == statusSet.id) {
            mc.displayGuiScreen(new ConfigGUI_StatusMessages(currentscreen));
        } else if (button.id == advancedSet.id) {
            mc.displayGuiScreen(new ConfigGUI_AdvancedSettings(currentscreen));
        } else if (button.id == proceedButton.id) {
            boolean rebooted = false;
            if (CraftPresence.CONFIG.hasChanged) {
                CraftPresence.CONFIG.updateConfig();
                CraftPresence.CONFIG.read();
                if (CraftPresence.CONFIG.hasClientPropertiesChanged) {
                    CommandHandler.rebootRPC();
                    rebooted = true;
                    CraftPresence.CONFIG.hasClientPropertiesChanged = false;
                }
                if (player != null && CraftPresence.CONFIG.rebootOnWorldLoad) {
                    if (!rebooted) {
                        CommandHandler.rebootRPC();
                    }
                    CraftPresence.CONFIG.rebootOnWorldLoad = false;
                }
                CraftPresence.CONFIG.hasChanged = false;
            }

            if (player != null) {
                player.closeScreen();
            } else {
                mc.displayGuiScreen(parentscreen);
            }
        } else if (button.id == aboutButton.id) {
            mc.displayGuiScreen(new ConfigGUI_About(currentscreen));
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}
