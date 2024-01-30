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

package com.gitlab.cdagaming.craftpresence.utils.gui.impl;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.utils.KeyUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.RenderUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.PaginatedScreen;
import io.github.cdagaming.unicore.impl.Pair;
import io.github.cdagaming.unicore.impl.Tuple;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ControlsGui extends PaginatedScreen {

    private static final int maxElementsPerPage = 7, startRow = 1;
    // Format: See KeyUtils#KEY_MAPPINGS
    private final Map<String, Tuple<KeyBinding, Tuple<Runnable, BiConsumer<Integer, Boolean>, Predicate<Integer>>, Consumer<Throwable>>> keyMappings;
    // Format: categoryName:keyNames
    private final Map<String, List<String>> categorizedNames = StringUtils.newHashMap();
    // Format: pageNumber:[elementText:[xPos:yPos]:color]
    private final Map<Integer, List<Tuple<String, Pair<Float, Float>, Integer>>> preRenderQueue = StringUtils.newHashMap(), postRenderQueue = StringUtils.newHashMap();
    // Pair Format: buttonToModify, Config Field to Edit
    // (Store a Backup of Prior Text just in case)
    private String backupKeyString;
    private Tuple<ExtendedButtonControl, String, Tuple<KeyBinding, Tuple<Runnable, BiConsumer<Integer, Boolean>, Predicate<Integer>>, Consumer<Throwable>>> entryData = null;
    private int currentAllocatedRow = startRow, currentAllocatedPage = startPage;

    public ControlsGui(GuiScreen parentScreen) {
        super(parentScreen);
        this.keyMappings = CraftPresence.KEYBINDINGS.getKeyMappings();

        sortMappings();
    }

    public ControlsGui(GuiScreen parentScreen, KeyUtils.FilterMode filterMode, List<String> filterData) {
        super(parentScreen);
        this.keyMappings = CraftPresence.KEYBINDINGS.getKeyMappings(filterMode, filterData);

        sortMappings();
    }

    public ControlsGui(GuiScreen parentScreen, KeyUtils.FilterMode filterMode, String... filterData) {
        this(parentScreen, filterMode, StringUtils.newArrayList(filterData));
    }

    @Override
    public void initializeUi() {
        setupScreenData();

        super.initializeUi();

        backButton.setOnClick(
                () -> {
                    if (entryData == null) {
                        openScreen(parentScreen);
                    }
                }
        );
    }

    @Override
    public void renderExtra() {
        final String mainTitle = Constants.TRANSLATOR.translate("gui.config.title");
        final String subTitle = Constants.TRANSLATOR.translate("gui.config.message.button.controls");
        renderCenteredString(mainTitle, 10, 0xFFFFFF);
        renderCenteredString(subTitle, 20, 0xFFFFFF);

        super.renderExtra();

        for (Map.Entry<Integer, List<Tuple<String, Pair<Float, Float>, Integer>>> entry : preRenderQueue.entrySet()) {
            final Integer pageNumber = entry.getKey();
            final List<Tuple<String, Pair<Float, Float>, Integer>> elementList = entry.getValue();
            for (Tuple<String, Pair<Float, Float>, Integer> elementData : elementList) {
                renderString(Constants.TRANSLATOR.translate(elementData.getFirst()), elementData.getSecond().getFirst(), elementData.getSecond().getSecond(), elementData.getThird(), pageNumber);
            }
        }
    }

    @Override
    public void postRender() {
        for (Map.Entry<Integer, List<Tuple<String, Pair<Float, Float>, Integer>>> entry : postRenderQueue.entrySet()) {
            final Integer pageNumber = entry.getKey();
            final List<Tuple<String, Pair<Float, Float>, Integer>> elementList = entry.getValue();
            for (Tuple<String, Pair<Float, Float>, Integer> elementData : elementList) {
                if (currentPage == pageNumber && RenderUtils.isMouseOver(getMouseX(), getMouseY(), elementData.getSecond().getFirst(), elementData.getSecond().getSecond(), getStringWidth(Constants.TRANSLATOR.translate(elementData.getFirst())), getFontHeight())) {
                    drawMultiLineString(
                            StringUtils.splitTextByNewLine(
                                    Constants.TRANSLATOR.translate(elementData.getFirst().replace(".name", ".description"))
                            )
                    );
                }
            }
        }

        super.postRender();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (entryData != null) {
            setKeyData(keyCode);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    /**
     * Sort Key Mappings via their categories, used for placement into gui
     */
    private void sortMappings() {
        for (Map.Entry<String, Tuple<KeyBinding, Tuple<Runnable, BiConsumer<Integer, Boolean>, Predicate<Integer>>, Consumer<Throwable>>> entry : keyMappings.entrySet()) {
            final String keyName = entry.getKey();
            final Tuple<KeyBinding, Tuple<Runnable, BiConsumer<Integer, Boolean>, Predicate<Integer>>, Consumer<Throwable>> keyData = entry.getValue();
            if (!categorizedNames.containsKey(keyData.getFirst().getKeyCategory())) {
                categorizedNames.put(keyData.getFirst().getKeyCategory(), StringUtils.newArrayList(keyName));
            } else if (!categorizedNames.get(keyData.getFirst().getKeyCategory()).contains(keyName)) {
                categorizedNames.get(keyData.getFirst().getKeyCategory()).add(keyName);
            }
        }
    }

    /**
     * Setup Rendering Queues for different parts of the Screen
     */
    private void setupScreenData() {
        // Clear any Prior Data beforehand
        preRenderQueue.clear();
        postRenderQueue.clear();

        final int renderPosition = (getScreenWidth() / 2) + 3;
        for (Map.Entry<String, List<String>> entry : categorizedNames.entrySet()) {
            syncPageData();
            final String categoryName = entry.getKey();
            final Tuple<String, Pair<Float, Float>, Integer> categoryData = new Tuple<>(categoryName, new Pair<>((getScreenWidth() / 2f) - (getStringWidth(categoryName) / 2f), (float) getButtonY(currentAllocatedRow, 5)), 0xFFFFFF);
            if (!preRenderQueue.containsKey(currentAllocatedPage)) {
                preRenderQueue.put(currentAllocatedPage, StringUtils.newArrayList());
            }
            if (!Constants.IS_LEGACY_SOFT) {
                preRenderQueue.get(currentAllocatedPage).add(categoryData);
            }

            final List<String> keyNames = entry.getValue();
            currentAllocatedRow++;

            for (String keyName : keyNames) {
                final Tuple<KeyBinding, Tuple<Runnable, BiConsumer<Integer, Boolean>, Predicate<Integer>>, Consumer<Throwable>> keyData = keyMappings.get(keyName);
                final Tuple<String, Pair<Float, Float>, Integer> positionData = new Tuple<>(keyData.getFirst().getKeyDescription(), new Pair<>((getScreenWidth() / 2f) - 130, (float) getButtonY(currentAllocatedRow, 5)), 0xFFFFFF);
                if (!preRenderQueue.containsKey(currentAllocatedPage)) {
                    preRenderQueue.put(currentAllocatedPage, StringUtils.newArrayList(positionData));
                } else {
                    preRenderQueue.get(currentAllocatedPage).add(positionData);
                }

                if (!postRenderQueue.containsKey(currentAllocatedPage)) {
                    postRenderQueue.put(currentAllocatedPage, StringUtils.newArrayList(positionData));
                } else {
                    postRenderQueue.get(currentAllocatedPage).add(positionData);
                }

                final ExtendedButtonControl keyCodeButton = new ExtendedButtonControl(
                        renderPosition + 20, getButtonY(currentAllocatedRow),
                        120, 20,
                        KeyUtils.getKeyName(keyData.getFirst().getKeyCode()),
                        keyName
                );
                keyCodeButton.setOnClick(() -> setupEntryData(keyCodeButton, keyData));

                addControl(keyCodeButton, currentAllocatedPage);
                currentAllocatedRow++;
                syncPageData();
            }
        }
    }

    /**
     * Synchronize Page Data based on placed elements
     */
    private void syncPageData() {
        if (currentAllocatedRow >= maxElementsPerPage) {
            currentAllocatedPage++;
            currentAllocatedRow = startRow;
        }
    }

    /**
     * Setup for Key Entry and Save Backup of Prior Setting, if a valid Key Button
     *
     * @param button  The Pressed upon KeyCode Button
     * @param keyData The key data attached to the entry
     */
    private void setupEntryData(final ExtendedButtonControl button, final Tuple<KeyBinding, Tuple<Runnable, BiConsumer<Integer, Boolean>, Predicate<Integer>>, Consumer<Throwable>> keyData) {
        if (entryData == null && button.getOptionalArgs() != null) {
            entryData = new Tuple<>(button, button.getOptionalArgs()[0], keyData);

            backupKeyString = button.getControlMessage();
            button.setControlMessage("gui.config.message.editor.enter_key");
        }
    }

    /**
     * Sets a New KeyCode for the currently queued entry data
     * Format -> buttonToModify, Config Field to Edit
     *
     * @param keyCode The New KeyCode for modifying
     */
    private void setKeyData(final int keyCode) {
        int keyToSubmit = keyCode;

        // Ensure a Valid KeyCode is entered
        if (!KeyUtils.isValidKeyCode(keyToSubmit) || KeyUtils.isValidClearCode(keyToSubmit)) {
            keyToSubmit = Keyboard.KEY_NONE;
        }

        final String formattedKey = KeyUtils.getKeyName(keyToSubmit);

        // If KeyCode Field to modify is not null or empty, attempt to queue change
        try {
            entryData.getThird().getSecond().getSecond().accept(keyToSubmit, false);
            CraftPresence.KEYBINDINGS.keySyncQueue.put(entryData.getSecond(), keyToSubmit);
            CraftPresence.CONFIG.hasChanged = true;

            entryData.getFirst().setControlMessage(formattedKey);
        } catch (Throwable ex) {
            entryData.getFirst().setControlMessage(backupKeyString);
            Constants.LOG.debugError(ex);
        }

        // Clear Data
        backupKeyString = null;
        entryData = null;
    }
}

