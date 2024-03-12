package com.gitlab.cdagaming.craftpresence.utils.gui.widgets;

import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ScrollPane;

import java.util.List;

/**
 * Implementation for a Scrollable Text-Only Widget
 * <p>This is designed for single-line text, use {@link TextDisplayWidget} for multi-line
 *
 * @author CDAGaming
 */
public class ScrollableTextWidget extends TextDisplayWidget {
    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parent  The parent or source screen to refer to
     * @param startX  The starting X position of the widget
     * @param startY  The starting Y position of the widget
     * @param width   The width of the widget
     * @param message The text to be rendered with this widget
     */
    public ScrollableTextWidget(final ExtendedScreen parent, final int startX, final int startY, final int width, final String message) {
        super(parent, startX, startY, width, message);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parent The parent or source screen to refer to
     * @param startX The starting X position of the widget
     * @param startY The starting Y position of the widget
     * @param width  The width of the widget
     */
    public ScrollableTextWidget(final ExtendedScreen parent, final int startX, final int startY, final int width) {
        super(parent, startX, startY, width);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parent  The parent or source screen to refer to
     * @param width   The width of the widget
     * @param message The text to be rendered with this widget
     */
    public ScrollableTextWidget(final ExtendedScreen parent, final int width, final String message) {
        super(parent, width, message);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parent The parent or source screen to refer to
     * @param width  The width of the widget
     */
    public ScrollableTextWidget(final ExtendedScreen parent, final int width) {
        super(parent, width);
    }

    @Override
    public void draw(ExtendedScreen screen) {
        int padding = 0, barWidth = 0;
        if (screen instanceof ScrollPane) {
            final ScrollPane pane = ((ScrollPane) screen);
            padding = pane.getPadding();
            barWidth = pane.getScrollBarWidth();
        }

        screen.renderScrollingString(
                getMessage(),
                getControlPosX() + padding,
                getControlPosY() + padding,
                getRight() - padding - barWidth,
                getBottom() - padding,
                0xFFFFFF
        );
    }

    @Override
    public boolean isCentered() {
        return true;
    }

    @Override
    public List<String> refreshContent() {
        return null;
    }

    @Override
    public List<String> getRenderLines() {
        return null;
    }

    @Override
    public int getControlHeight() {
        return 20;
    }
}
