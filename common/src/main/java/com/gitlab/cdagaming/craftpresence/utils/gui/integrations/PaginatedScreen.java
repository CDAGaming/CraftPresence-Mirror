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

package com.gitlab.cdagaming.craftpresence.utils.gui.integrations;

import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.RenderUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.DynamicWidget;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * The Paginated Gui Screen Implementation
 */
public class PaginatedScreen extends ExtendedScreen {
    private final Map<Integer, List<DynamicWidget>> paginatedWidgets = StringUtils.newHashMap();
    private final Map<Integer, List<Gui>> paginatedControls = StringUtils.newHashMap();
    private final Map<Integer, List<ScrollableListControl>> paginatedLists = StringUtils.newHashMap();
    /**
     * The "Next Page" Button
     */
    protected ExtendedButtonControl nextPageButton;
    /**
     * The "Previous Page" Button
     */
    protected ExtendedButtonControl previousPageButton;
    /**
     * The Back Button, normally bound to the parent screen
     */
    protected ExtendedButtonControl backButton;
    /**
     * The starting page to open this screen on
     */
    protected int startPage = 0;
    /**
     * The currently displayed page
     */
    protected int currentPage = startPage;
    /**
     * The maximum pages that can be displayed
     */
    protected int maxPages = startPage;
    private List<Gui> defaultButtons = StringUtils.newArrayList();
    private List<DynamicWidget> defaultWidgets = StringUtils.newArrayList();
    private Runnable onPageChange;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parentScreen The Parent Screen for this Instance
     */
    public PaginatedScreen(final GuiScreen parentScreen) {
        super(parentScreen);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parentScreen The Parent Screen for this Instance
     * @param debugMode    Whether debug mode should be enabled for this screen
     */
    public PaginatedScreen(final GuiScreen parentScreen, final boolean debugMode) {
        super(parentScreen, debugMode);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parentScreen The Parent Screen for this Instance
     * @param debugMode    Whether debug mode should be enabled for this screen
     * @param verboseMode  Whether verbose mode should be enabled for this screen
     */
    public PaginatedScreen(final GuiScreen parentScreen, final boolean debugMode, final boolean verboseMode) {
        super(parentScreen, debugMode, verboseMode);
    }

    @Override
    public void initializeUi() {
        backButton = addControl(
                new ExtendedButtonControl(
                        hasPages() ? (getScreenWidth() / 2) - 65 : (getScreenWidth() / 2) - 90,
                        (getScreenHeight() - 26),
                        hasPages() ? 130 : 180, 20,
                        "gui.config.message.button.back",
                        () -> openScreen(parentScreen)
                )
        );
        if (hasPages()) {
            previousPageButton = addControl(
                    new ExtendedButtonControl(
                            backButton.getLeft() - 32, (getScreenHeight() - 26),
                            30, 20,
                            "<--",
                            () -> {
                                if (currentPage > startPage) {
                                    currentPage--;
                                    if (onPageChange != null) {
                                        onPageChange.run();
                                    }
                                }
                            }
                    )
            );
            nextPageButton = addControl(
                    new ExtendedButtonControl(
                            backButton.getRight() + 2, (getScreenHeight() - 26),
                            30, 20,
                            "-->",
                            () -> {
                                if (currentPage < maxPages) {
                                    currentPage++;
                                    if (onPageChange != null) {
                                        onPageChange.run();
                                    }
                                }
                            }
                    )
            );
        }
        super.initializeUi();
    }

    /**
     * Adds a Compatible Control to this Screen with specified type
     *
     * @param buttonIn     The Control to add to this Screen
     * @param <T>          The Control's Class Type
     * @param renderTarget The Control's render target, or page to render on
     * @return The added control with attached class type
     */
    @Nonnull
    public <T extends Gui> T addControl(@Nonnull T buttonIn, final int renderTarget) {
        if (buttonIn instanceof DynamicWidget) {
            addWidget((DynamicWidget) buttonIn, renderTarget);
        }
        if (!paginatedControls.containsKey(renderTarget)) {
            paginatedControls.put(renderTarget, StringUtils.newArrayList(buttonIn));
            if (renderTarget > maxPages) {
                maxPages = renderTarget;
            }
        } else {
            paginatedControls.get(renderTarget).add(buttonIn);
        }
        return super.addControl(buttonIn);
    }

    /**
     * Adds a Compatible Scroll List to this Screen with specified type
     *
     * @param buttonIn     The Scroll List to add to this Screen
     * @param <T>          The Scroll List's Class Type
     * @param renderTarget The Control's render target, or page to render on
     * @return The added scroll list with attached class type
     */
    @Nonnull
    public <T extends ScrollableListControl> T addList(@Nonnull T buttonIn, final int renderTarget) {
        if (!paginatedLists.containsKey(renderTarget)) {
            paginatedLists.put(renderTarget, StringUtils.newArrayList(buttonIn));
            if (renderTarget > maxPages) {
                maxPages = renderTarget;
            }
        } else {
            paginatedLists.get(renderTarget).add(buttonIn);
        }
        return super.addList(buttonIn);
    }

    /**
     * Adds a Compatible Control to this Screen with specified type
     *
     * @param buttonIn     The Control to add to this Screen
     * @param <T>          The Control's Class Type
     * @param renderTarget The Control's render target, or page to render on
     * @return The added control with attached class type
     */
    @Nonnull
    public <T extends DynamicWidget> T addWidget(@Nonnull T buttonIn, final int renderTarget) {
        if (!paginatedWidgets.containsKey(renderTarget)) {
            paginatedWidgets.put(renderTarget, StringUtils.newArrayList(buttonIn));
            if (renderTarget > maxPages) {
                maxPages = renderTarget;
            }
        } else {
            paginatedWidgets.get(renderTarget).add(buttonIn);
        }
        return super.addWidget(buttonIn);
    }

    @Override
    public void preRender() {
        ensureDefaults();
        final List<DynamicWidget> widgetsToDraw = paginatedWidgets.getOrDefault(currentPage, defaultWidgets);
        final List<Gui> elementsToRender = paginatedControls.getOrDefault(currentPage, defaultButtons);
        final List<ScrollableListControl> listsToRender = paginatedLists.getOrDefault(currentPage, StringUtils.newArrayList());

        for (DynamicWidget widget : getWidgets()) {
            final boolean isDefault = defaultWidgets.contains(widget);
            final boolean isRendering = widgetsToDraw.contains(widget);
            if (isDefault || isRendering) {
                widget.draw(this);
            }
        }
        for (Gui extendedControl : getControls()) {
            final boolean isDefault = defaultButtons.contains(extendedControl);
            if (!isDefault) {
                final boolean isRendering = elementsToRender.contains(extendedControl);

                // Toggle visibility/disable element is not on page
                if (extendedControl instanceof ExtendedButtonControl) {
                    ((ExtendedButtonControl) extendedControl).setControlVisible(isRendering);
                    ((ExtendedButtonControl) extendedControl).setControlEnabled(isRendering);
                }
                if (extendedControl instanceof ExtendedTextControl) {
                    ((ExtendedTextControl) extendedControl).setVisible(isRendering);
                    ((ExtendedTextControl) extendedControl).setEnabled(isRendering);
                }
            }
        }
        for (ScrollableListControl listControl : getLists()) {
            listControl.setEnabled(listsToRender.contains(listControl));
        }
    }

    @Override
    public void postRender() {
        ensureDefaults();
        final List<DynamicWidget> widgetsToDraw = paginatedWidgets.getOrDefault(currentPage, defaultWidgets);
        final List<Gui> elementsToRender = paginatedControls.getOrDefault(currentPage, defaultButtons);
        for (DynamicWidget widget : getWidgets()) {
            final boolean isDefault = defaultWidgets.contains(widget);
            final boolean isRendering = widgetsToDraw.contains(widget);
            if (isDefault || isRendering) {
                widget.postDraw(this);
            }
        }
        for (Gui extendedControl : getControls()) {
            final boolean isDefault = defaultButtons.contains(extendedControl);
            final boolean isRendering = elementsToRender.contains(extendedControl);

            if (isDefault || isRendering) {
                if (extendedControl instanceof ExtendedButtonControl) {
                    final ExtendedButtonControl extendedButton = (ExtendedButtonControl) extendedControl;
                    if (isOverScreen() && RenderUtils.isMouseOver(getMouseX(), getMouseY(), extendedButton)) {
                        extendedButton.onHover();
                    }
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (hasPages()) {
            if (keyCode == Keyboard.KEY_UP && currentPage > startPage) {
                currentPage--;
            }

            if (keyCode == Keyboard.KEY_DOWN && currentPage < maxPages) {
                currentPage++;
            }
        }

        super.keyTyped(typedChar, keyCode);
    }

    /**
     * Ensure that Default Elements are populated correctly
     */
    private void ensureDefaults() {
        defaultButtons = StringUtils.newArrayList(backButton);
        if (hasPages()) {
            defaultButtons.add(previousPageButton);
            defaultButtons.add(nextPageButton);
            previousPageButton.setControlEnabled(currentPage > startPage);
            nextPageButton.setControlEnabled(currentPage < maxPages);
        }
        if (paginatedControls.containsKey(-1)) {
            defaultButtons.addAll(paginatedControls.get(-1));
        }

        defaultWidgets = StringUtils.newArrayList(backButton);
        if (hasPages()) {
            defaultWidgets.add(previousPageButton);
            defaultWidgets.add(nextPageButton);
            previousPageButton.setControlEnabled(currentPage > startPage);
            nextPageButton.setControlEnabled(currentPage < maxPages);
        }
        if (paginatedControls.containsKey(-1)) {
            for (Gui controlIn : paginatedControls.get(-1)) {
                if (controlIn instanceof DynamicWidget) {
                    defaultWidgets.add((DynamicWidget) controlIn);
                }
            }
        }
    }

    /**
     * Renders a String in the Screen, in the style of normal text
     *
     * @param text         The text to render to the screen
     * @param xPos         The X position to render the text at
     * @param yPos         The Y position to render the text at
     * @param color        The color to render the text in
     * @param renderTarget The Control's render target, or page to render on
     */
    public void renderString(final String text, final float xPos, final float yPos, final int color, final int renderTarget) {
        if (renderTarget == currentPage) {
            renderString(text, xPos, yPos, color);
        }
    }

    /**
     * Set the Event to trigger upon page change
     *
     * @param onPageChange The new event to be triggered
     */
    public void setOnPageChange(final Runnable onPageChange) {
        this.onPageChange = onPageChange;
    }

    /**
     * Determine if this UI has any applicable pages
     *
     * @return if maxPages is above zero
     */
    public boolean hasPages() {
        return maxPages > 0;
    }
}
