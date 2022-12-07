package com.gitlab.cdagaming.craftpresence.forge.integrations.replaymod;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.impl.Module;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.GuiUtils;
import com.replaymod.lib.de.johni0702.minecraft.gui.container.AbstractGuiOverlay;
import com.replaymod.lib.de.johni0702.minecraft.gui.container.AbstractGuiScreen;
import com.replaymod.lib.de.johni0702.minecraft.gui.container.GuiOverlay;
import com.replaymod.lib.de.johni0702.minecraft.gui.container.GuiScreen;
import com.replaymod.render.gui.GuiVideoRenderer;
import com.replaymod.replay.gui.overlay.GuiReplayOverlay;
import com.replaymod.replay.gui.screen.GuiReplayViewer;

@SuppressWarnings("ConstantConditions")
public class ReplayModUtils implements Module {

    @Override
    public void onTick() {
        CraftPresence.CLIENT.removeArguments("replaymod.");
        final AbstractGuiScreen<?> possibleScreen = GuiScreen.from(CraftPresence.instance.currentScreen);
        final AbstractGuiOverlay<?> possibleOverlay = GuiOverlay.from(CraftPresence.instance.currentScreen);
        if (possibleScreen instanceof GuiReplayViewer) {
            // TODO
        } else if (possibleOverlay instanceof GuiReplayOverlay) {
            // TODO
        } else if (possibleScreen instanceof GuiVideoRenderer) {
            // TODO
            CraftPresence.CLIENT.syncArgument("replaymod.time.current", secToString(
                    (Integer) StringUtils.lookupObject(
                            GuiVideoRenderer.class, possibleScreen, "renderTimeTaken"
                    ) / 1000
            ));
            CraftPresence.CLIENT.syncArgument("replaymod.time.remaining", secToString(
                    (Integer) StringUtils.lookupObject(
                            GuiVideoRenderer.class, possibleScreen, "renderTimeLeft"
                    ) / 1000
            ));
        }
    }

    private String secToString(int seconds) {
        int hours = seconds / 3600;
        int min = seconds / 60 - hours * 60;
        int sec = seconds - (min * 60 + hours * 60 * 60);
        StringBuilder builder = new StringBuilder();
        if (hours > 0) {
            builder.append(hours).append("hour(s)");
        }
        if (min > 0 || hours > 0) {
            builder.append(min).append("minute(s)");
        }
        builder.append(sec).append("second(s)");
        return builder.toString();
    }
}
