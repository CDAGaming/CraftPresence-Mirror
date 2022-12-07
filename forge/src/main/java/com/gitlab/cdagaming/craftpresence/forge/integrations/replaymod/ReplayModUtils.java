package com.gitlab.cdagaming.craftpresence.forge.integrations.replaymod;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.GuiUtils;
import com.replaymod.lib.de.johni0702.minecraft.gui.container.AbstractGuiOverlay;
import com.replaymod.lib.de.johni0702.minecraft.gui.container.AbstractGuiScreen;
import com.replaymod.lib.de.johni0702.minecraft.gui.container.GuiOverlay;
import com.replaymod.lib.de.johni0702.minecraft.gui.container.GuiScreen;
import com.replaymod.render.gui.GuiVideoRenderer;
import com.replaymod.replay.gui.overlay.GuiReplayOverlay;
import com.replaymod.replay.gui.screen.GuiReplayViewer;

public class ReplayModUtils extends GuiUtils {
    @Override
    public void updatePresence() {
        CraftPresence.CLIENT.removeArguments("replaymod.");
        AbstractGuiScreen<?> possibleScreen = GuiScreen.from(CURRENT_SCREEN);
        AbstractGuiOverlay<?> possibleOverlay = GuiOverlay.from(CURRENT_SCREEN);
        if (possibleScreen instanceof GuiReplayViewer) {
            ModUtils.LOG.info("VIEWING Replay Browser");
        } else if (possibleOverlay instanceof GuiReplayOverlay) {
            ModUtils.LOG.info("VIEWING Replay Editor");
        } else if (possibleScreen instanceof GuiVideoRenderer) {
            ModUtils.LOG.info("VIEWING Renderer");
        }
    }
}
