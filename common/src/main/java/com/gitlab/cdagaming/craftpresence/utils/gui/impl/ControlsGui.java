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
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.utils.KeyUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ScrollPane;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.ButtonWidget;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.ScrollableTextWidget;
import io.github.cdagaming.unicore.impl.Tuple;
import io.github.cdagaming.unicore.utils.StringUtils;

import java.util.List;
import java.util.Map;

public class ControlsGui extends ExtendedScreen {
    // Format: See KeyUtils#KEY_MAPPINGS
    private final Map<String, KeyUtils.KeyMapping> keyMappings;
    // Format: categoryName:keyNames
    private final Map<String, List<String>> categorizedNames = StringUtils.newHashMap();
    // Pair Format: buttonToModify, Config Field to Edit
    // (Store a Backup of Prior Text just in case)
    private String backupKeyString;
    private Tuple<ExtendedButtonControl, ExtendedButtonControl, KeyUtils.KeyMapping> entryData = null;
    private ScrollPane childFrame;

    public ControlsGui() {
        super();
        this.keyMappings = CraftPresence.KEYBINDINGS.getKeyMappings();

        sortMappings();
    }

    public ControlsGui(KeyUtils.FilterMode filterMode, List<String> filterData) {
        super();
        this.keyMappings = CraftPresence.KEYBINDINGS.getKeyMappings(filterMode, filterData);

        sortMappings();
    }

    public ControlsGui(KeyUtils.FilterMode filterMode, String... filterData) {
        this(filterMode, StringUtils.newArrayList(filterData));
    }

    @Override
    public void initializeUi() {
        // Adding Back Button
        addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90,
                        (getScreenHeight() - 26),
                        180, 20,
                        "gui.config.message.button.back",
                        () -> {
                            if (entryData == null) {
                                openScreen(getParent());
                            }
                        }
                )
        );
        childFrame = addControl(
                new ScrollPane(
                        0, 32,
                        getScreenWidth(), getScreenHeight() - 32
                )
        );
        setupScreenData();

        super.initializeUi();
    }

    @Override
    public void renderExtra() {
        final String mainTitle = Constants.TRANSLATOR.translate("gui.config.title");
        final String subTitle = Constants.TRANSLATOR.translate("gui.config.message.button.controls");
        renderScrollingString(
                mainTitle,
                30, 2,
                getScreenWidth() - 30, 16,
                0xFFFFFF
        );
        renderScrollingString(
                subTitle,
                30, 16,
                getScreenWidth() - 30, 30,
                0xFFFFFF
        );

        super.renderExtra();
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
        for (Map.Entry<String, KeyUtils.KeyMapping> entry : keyMappings.entrySet()) {
            final String keyName = entry.getKey();
            final KeyUtils.KeyMapping keyData = entry.getValue();
            if (!categorizedNames.containsKey(keyData.category())) {
                categorizedNames.put(keyData.category(), StringUtils.newArrayList(keyName));
            } else if (!categorizedNames.get(keyData.category()).contains(keyName)) {
                categorizedNames.get(keyData.category()).add(keyName);
            }
        }
    }

    /**
     * Setup Rendering Queues for different parts of the Screen
     */
    private void setupScreenData() {
        // Clear any Prior Data beforehand
        clearEntryData();

        int currentAllocatedRow = 0;

        for (Map.Entry<String, List<String>> entry : categorizedNames.entrySet()) {
            if (!Constants.IS_LEGACY_SOFT) {
                childFrame.addWidget(new ScrollableTextWidget(
                        true,
                        0, getButtonY(currentAllocatedRow),
                        childFrame.getScreenWidth(),
                        Constants.TRANSLATOR.translate(entry.getKey())
                ));
            }

            currentAllocatedRow++;

            final int middle = (childFrame.getScreenWidth() / 2) + 3;
            for (String keyName : entry.getValue()) {
                final KeyUtils.KeyMapping keyData = keyMappings.get(keyName);

                final String keyTitle = keyData.description();
                final int keyCode = CraftPresence.KEYBINDINGS.keySyncQueue.getOrDefault(keyName, keyData.keyCode());
                final ButtonWidget keyCodeWidget = new ButtonWidget(
                        getButtonY(currentAllocatedRow),
                        95, 20,
                        KeyUtils.getKeyName(keyCode),
                        keyTitle,
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate(keyTitle.replace(".name", ".description"))
                                )
                        ),
                        keyName
                );

                final ExtendedButtonControl keyResetButton = new ExtendedButtonControl(
                        middle + keyCodeWidget.getControlWidth() + 15,
                        getButtonY(currentAllocatedRow),
                        70, 20,
                        "gui.config.message.button.reset"
                );

                keyResetButton.setOnClick(() -> resetEntryData(keyCodeWidget, keyResetButton, keyData));
                keyCodeWidget.setOnClick(() -> setupEntryData(keyCodeWidget, keyResetButton, keyData));

                keyResetButton.setControlEnabled(keyCode != keyData.defaultKeyCode());

                childFrame.addControl(keyCodeWidget);
                childFrame.addControl(keyResetButton);

                currentAllocatedRow++;
            }
        }
    }

    /**
     * Setup for Key Entry and Save Backup of Prior Setting, if a valid Key Button
     *
     * @param button      The Pressed upon KeyCode Button
     * @param resetButton The Reset Button related to the KeyCode Button
     * @param keyData     The key data attached to the entry
     */
    private void setupEntryData(final ExtendedButtonControl button, final ExtendedButtonControl resetButton, final KeyUtils.KeyMapping keyData) {
        if (entryData == null && button.getOptionalArgs() != null) {
            entryData = new Tuple<>(button, resetButton, keyData);

            backupKeyString = button.getControlMessage();
            button.setControlMessage("gui.config.message.editor.enter_key");
        }
    }

    /**
     * Setup for Key Entry and Save Backup of Prior Setting, if a valid Key Button
     *
     * @param button      The Pressed upon KeyCode Button
     * @param resetButton The Reset Button related to the KeyCode Button
     * @param keyData     The key data attached to the entry
     */
    private void resetEntryData(final ExtendedButtonControl button, final ExtendedButtonControl resetButton, final KeyUtils.KeyMapping keyData) {
        if (entryData == null && button.getOptionalArgs() != null) {
            entryData = new Tuple<>(button, resetButton, keyData);
            setKeyData(keyData.defaultKeyCode());
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
            keyToSubmit = ModUtils.MCProtocolID > 340 ? -1 : 0; // KEY_NONE
        }

        final String formattedKey = KeyUtils.getKeyName(keyToSubmit);
        final String internalName = entryData.getFirst().getOptionalArgs()[0];

        // If KeyCode Field to modify is not null or empty, attempt to queue change
        try {
            entryData.getThird().configEvent().accept(keyToSubmit, false);
            CraftPresence.KEYBINDINGS.keySyncQueue.put(internalName, keyToSubmit);
            CraftPresence.CONFIG.hasChanged = true;

            entryData.getFirst().setControlMessage(formattedKey);
        } catch (Throwable ex) {
            entryData.getFirst().setControlMessage(backupKeyString);
            Constants.LOG.debugError(ex);
        }

        entryData.getSecond().setControlEnabled(
                keyToSubmit != entryData.getThird().defaultKeyCode()
        );

        clearEntryData();
    }

    /**
     * Reset Entry Data to their defaults
     */
    private void clearEntryData() {
        backupKeyString = null;
        entryData = null;
    }
}
