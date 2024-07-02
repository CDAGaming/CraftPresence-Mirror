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

package com.gitlab.cdagaming.unilib.utils.gui.impl;

import com.gitlab.cdagaming.unilib.core.CoreUtils;
import com.gitlab.cdagaming.unilib.utils.KeyUtils;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ScrollPane;
import com.gitlab.cdagaming.unilib.utils.gui.widgets.ButtonWidget;
import com.gitlab.cdagaming.unilib.utils.gui.widgets.ScrollableTextWidget;
import io.github.cdagaming.unicore.impl.Tuple;
import io.github.cdagaming.unicore.utils.StringUtils;

import java.util.List;
import java.util.Map;

public class ControlsGui extends ExtendedScreen {
    // Format: See KeyUtils#KEY_MAPPINGS
    private final Map<String, KeyUtils.KeyBindData> keyMappings;
    // The KeyUtils Instance
    private final KeyUtils instance;
    // On Key Changed Event
    private final Runnable onKeyChanged;
    // Format: categoryName:keyNames
    private final Map<String, List<String>> categorizedNames = StringUtils.newHashMap();
    // Format: categoryName:displayName
    private final Map<String, String> categoryNames = StringUtils.newHashMap();
    // Pair Format: buttonToModify, Config Field to Edit
    // (Store a Backup of Prior Text just in case)
    private String backupKeyString;
    private Tuple<ExtendedButtonControl, ExtendedButtonControl, KeyUtils.KeyBindData> entryData = null;
    private ScrollPane childFrame;

    public ControlsGui(final KeyUtils instance, final Runnable onKeyChanged, final Map<String, KeyUtils.KeyBindData> keyMappings) {
        super("Controls");
        this.instance = instance;
        this.onKeyChanged = onKeyChanged;
        this.keyMappings = keyMappings;
        sortMappings();
    }

    public ControlsGui(final KeyUtils instance, final Runnable onKeyChanged, final List<String> filterData) {
        this(instance, onKeyChanged, instance.getKeyMappings(filterData));
    }

    public ControlsGui(final KeyUtils instance, final Runnable onKeyChanged, final String... filterData) {
        this(instance, onKeyChanged, instance.getKeyMappings(filterData));
    }

    @Override
    public void initializeUi() {
        // Adding Back Button
        addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90,
                        (getScreenHeight() - 26),
                        180, 20,
                        "Back",
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
        for (Map.Entry<String, KeyUtils.KeyBindData> entry : keyMappings.entrySet()) {
            final String keyName = entry.getKey();
            final KeyUtils.KeyBindData keyData = entry.getValue();
            final String keyCategory = keyData.category();
            if (!categorizedNames.containsKey(keyCategory)) {
                categorizedNames.put(keyCategory, StringUtils.newArrayList(keyName));
            } else if (!categorizedNames.get(keyCategory).contains(keyName)) {
                categorizedNames.get(keyCategory).add(keyName);
            }

            if (!categoryNames.containsKey(keyCategory)) {
                categoryNames.put(keyCategory, keyData.categoryName());
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
            childFrame.addWidget(new ScrollableTextWidget(
                    true,
                    0, getButtonY(currentAllocatedRow),
                    childFrame.getScreenWidth(),
                    categoryNames.get(entry.getKey())
            ));

            currentAllocatedRow++;

            final int middle = (childFrame.getScreenWidth() / 2) + 3;
            for (String keyName : entry.getValue()) {
                final KeyUtils.KeyBindData keyData = keyMappings.get(keyName);

                final String keyTitle = keyData.displayName();
                final int keyCode = instance.keySyncQueue.getOrDefault(keyName, keyData.keyCode());
                final ButtonWidget keyCodeWidget = new ButtonWidget(
                        getButtonY(currentAllocatedRow),
                        95, 20,
                        instance.getKeyName(keyCode),
                        keyTitle,
                        () -> {
                            final String details = keyData.details();
                            if (!StringUtils.isNullOrEmpty(details)) {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                details
                                        )
                                );
                            }
                        },
                        keyName
                );

                final ExtendedButtonControl keyResetButton = new ExtendedButtonControl(
                        middle + keyCodeWidget.getControlWidth() + 15,
                        getButtonY(currentAllocatedRow),
                        70, 20,
                        "Reset"
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
    private void setupEntryData(final ExtendedButtonControl button, final ExtendedButtonControl resetButton, final KeyUtils.KeyBindData keyData) {
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
    private void resetEntryData(final ExtendedButtonControl button, final ExtendedButtonControl resetButton, final KeyUtils.KeyBindData keyData) {
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
        if (!instance.isValidKeyCode(keyToSubmit) || instance.isValidClearCode(keyToSubmit)) {
            keyToSubmit = getKeyByVersion(0, -1); // KEY_NONE
        }

        final String formattedKey = instance.getKeyName(keyToSubmit);
        final String internalName = entryData.getFirst().getOptionalArgs()[0];

        // If KeyCode Field to modify is not null or empty, attempt to queue change
        try {
            entryData.getThird().configEvent().accept(keyToSubmit, false);
            instance.keySyncQueue.put(internalName, keyToSubmit);
            if (onKeyChanged != null) {
                onKeyChanged.run();
            }

            entryData.getFirst().setControlMessage(formattedKey);
        } catch (Throwable ex) {
            entryData.getFirst().setControlMessage(backupKeyString);
            CoreUtils.LOG.debugError(ex);
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
