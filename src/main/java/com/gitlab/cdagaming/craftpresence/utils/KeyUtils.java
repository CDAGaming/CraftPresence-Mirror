/*
 * MIT License
 *
 * Copyright (c) 2018 - 2022 CDAGaming (cstack2011@yahoo.com)
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

package com.gitlab.cdagaming.craftpresence.utils;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.gui.MainGui;
import com.gitlab.cdagaming.craftpresence.impl.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.settings.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Map;

/**
 * Keyboard Utilities to Parse KeyCodes and handle KeyCode Events
 *
 * @author CDAGaming
 */
public class KeyUtils {
    /**
     * KeyCodes that when pressed will be interpreted as NONE/UNKNOWN
     * After ESC and Including any KeyCodes under 0x00
     * <p>
     * Notes:
     * LWJGL 2: ESC = 0x01
     * LWJGL 3: ESC = 256
     */
    private static final List<Integer> clearKeys = Lists.newArrayList();

    /**
     * Allowed KeyCode Start Limit and Individual Filters
     */
    private static final List<Integer> invalidKeys = Lists.newArrayList();
    /**
     * Key Mappings for Vanilla MC KeyBind Schema
     * <p>
     * Format: rawKeyField:[keyBindInstance:(runEvent,updatePredicate):errorCallback]
     */
    private final Map<String, Tuple<KeyBinding, Pair<Runnable, Predicate<Integer>>, DataConsumer<Throwable>>> KEY_MAPPINGS = Maps.newHashMap();
    public Map<String, Integer> keySyncQueue = Maps.newHashMap();
    /**
     * Determines whether KeyBindings have been fully registered and attached to needed systems.
     */
    private boolean keysRegistered = false;

    /**
     * Determine if the Source KeyCode fulfills the following conditions
     * <p>
     * 1) Is Not Contained or Listed within {@link KeyUtils#invalidKeys}
     *
     * @param sourceKeyCode The Source KeyCode to Check
     * @return {@code true} if and only if a Valid KeyCode
     */
    public static boolean isValidKeyCode(int sourceKeyCode) {
        return !invalidKeys.contains(sourceKeyCode);
    }

    /**
     * Determine if the Source KeyCode fulfills the following conditions
     * <p>
     * 1) Is Not Contained or Listed within {@link KeyUtils#clearKeys}
     *
     * @param sourceKeyCode The Source KeyCode to Check
     * @return {@code true} if and only if a Valid KeyCode
     */
    public static boolean isValidClearCode(int sourceKeyCode) {
        if (clearKeys.isEmpty()) {
            // Register Invalid Keys, dependent on protocol version
            // - These keys will identify as NONE within the game
            clearKeys.add(ModUtils.MCProtocolID > 340 ? 256 : 1); // ESC
        }
        return clearKeys.contains(sourceKeyCode);
    }

    /**
     * Determine the LWJGL KeyCode Name for the inputted KeyCode
     *
     * @param original A KeyCode, in Integer Form
     * @return Either an LWJGL KeyCode Name or the KeyCode if none can be found
     */
    public static String getKeyName(final int original) {
        final int unknownKeyCode = (ModUtils.MCProtocolID <= 340 ? -1 : 0);
        final String unknownKeyName = (ModUtils.MCProtocolID <= 340 ? KeyConverter.fromGlfw.get(unknownKeyCode) : KeyConverter.toGlfw.get(unknownKeyCode)).getSecond();
        if (isValidKeyCode(original)) {
            // If Input is a valid Integer and Valid KeyCode,
            // Parse depending on Protocol
            if (ModUtils.MCProtocolID <= 340 && KeyConverter.toGlfw.containsKey(original)) {
                return KeyConverter.toGlfw.get(original).getSecond();
            } else if (ModUtils.MCProtocolID > 340 && KeyConverter.fromGlfw.containsKey(original)) {
                return KeyConverter.fromGlfw.get(original).getSecond();
            } else {
                // If no other Mapping Layer contains the KeyCode Name,
                // Fallback to LWJGL Methods to retrieve the KeyCode Name
                final String altKeyName = Integer.toString(original);
                String keyName;
                if (original != unknownKeyCode) {
                    keyName = Keyboard.getKeyName(original);
                } else {
                    keyName = unknownKeyName;
                }

                // If Key Name is not Empty or Null, use that, otherwise use original
                if (!StringUtils.isNullOrEmpty(keyName)) {
                    return keyName;
                } else {
                    return altKeyName;
                }
            }
        } else {
            // If Not a Valid KeyCode, return the appropriate Unknown Keycode
            return unknownKeyName;
        }
    }

    /**
     * Registers KeyBindings and critical KeyCode information to MC's KeyCode systems
     * <p>Note: It's mandatory for KeyBindings to be registered here, or they will not be recognized on either end
     */
    void register() {
        KEY_MAPPINGS.put(
                "configKeyCode",
                new Tuple<>(
                        new KeyBinding("key.craftpresence.config_keycode.name", CraftPresence.CONFIG.accessibilitySettings.configKeyCode, "key.craftpresence.category"),
                        new Pair<>(
                                () -> {
                                    if (!CraftPresence.GUIS.isFocused && !CraftPresence.GUIS.configGUIOpened) {
                                        CraftPresence.GUIS.openScreen(new MainGui(CraftPresence.instance.currentScreen));
                                    }
                                },
                                vanillaBind -> vanillaBind != CraftPresence.CONFIG.accessibilitySettings.configKeyCode
                        ), null
                )
        );
    }

    /**
     * Retrieve if the keybindings are successfully registered to necessary systems
     *
     * @return {@code true} if and only if the keybindings are successfully registered
     */
    public boolean areKeysRegistered() {
        return keysRegistered;
    }

    /**
     * Retrieves the unfiltered Key Mappings for Vanilla MC KeyBind Schema
     * <p>
     * Format: rawKeyField:[keyBindInstance:(runEvent,updatePredicate):errorCallback]
     *
     * @return The unfiltered key mappings
     */
    public Map<String, Tuple<KeyBinding, Pair<Runnable, Predicate<Integer>>, DataConsumer<Throwable>>> getRawKeyMappings() {
        return KEY_MAPPINGS;
    }

    /**
     * Determine the LWJGL KeyCode Name for the inputted KeyCode
     *
     * @param original A KeyCode, converted to String
     * @return Either an LWJGL KeyCode Name or the KeyCode if none can be found
     */
    public String getKeyName(final String original) {
        final String unknownKeyName = (ModUtils.MCProtocolID <= 340 ? KeyConverter.fromGlfw.get(-1) : KeyConverter.toGlfw.get(0)).getSecond();
        if (!StringUtils.isNullOrEmpty(original)) {
            final Pair<Boolean, Integer> integerData = StringUtils.getValidInteger(original);

            if (integerData.getFirst()) {
                return getKeyName(integerData.getSecond());
            } else {
                // If Not a Valid Integer, return the appropriate Unknown Keycode
                return unknownKeyName;
            }
        } else {
            // If input is a Null Value, return the appropriate Unknown Keycode
            return unknownKeyName;
        }
    }

    /**
     * Tick Method for KeyUtils, that runs on each tick
     * <p>
     * Implemented @ {@link CommandUtils#reloadData}
     */
    void onTick() {
        if (!keysRegistered) {
            if (CraftPresence.instance.gameSettings != null) {
                for (String keyName : KEY_MAPPINGS.keySet()) {
                    CraftPresence.instance.gameSettings.keyBindings = ArrayUtils.add(CraftPresence.instance.gameSettings.keyBindings, KEY_MAPPINGS.get(keyName).getFirst());
                }
                keysRegistered = true;
            } else {
                return;
            }
        }

        if (Keyboard.isCreated() && CraftPresence.CONFIG != null) {
            final int unknownKeyCode = (ModUtils.MCProtocolID <= 340 ? -1 : 0);
            final String unknownKeyName = (ModUtils.MCProtocolID <= 340 ? KeyConverter.fromGlfw.get(unknownKeyCode) : KeyConverter.toGlfw.get(unknownKeyCode)).getSecond();
            try {
                for (String keyName : KEY_MAPPINGS.keySet()) {
                    final Tuple<KeyBinding, Pair<Runnable, Predicate<Integer>>, DataConsumer<Throwable>> keyData = KEY_MAPPINGS.get(keyName);
                    final KeyBinding keyBind = keyData.getFirst();
                    final Pair<Runnable, Predicate<Integer>> callbackData = keyData.getSecond();
                    final int currentBind = keyBind.getKeyCode();
                    boolean hasBeenRun = false;

                    if (!getKeyName(currentBind).equals(unknownKeyName) && !isValidClearCode(currentBind)) {
                        // Only process the key if it is not an unknown or invalid key
                        if (Keyboard.isKeyDown(currentBind) && !(CraftPresence.instance.currentScreen instanceof GuiControls)) {
                            try {
                                callbackData.getFirst().run();
                            } catch (Exception | Error ex) {
                                if (keyData.getThird() != null) {
                                    keyData.getThird().accept(ex);
                                } else {
                                    ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.error.keycode", keyBind.getKeyDescription()));
                                    syncKeyData(keyName, ImportMode.Specific, keyBind.getKeyCodeDefault());
                                }
                            } finally {
                                hasBeenRun = true;
                            }
                        }
                    }

                    // Only check for Keyboard updates if the key is not active but is in queue for a sync
                    if (!hasBeenRun && !CraftPresence.CONFIG.hasChanged) {
                        if (keySyncQueue.containsKey(keyName)) {
                            syncKeyData(keyName, ImportMode.Config, keySyncQueue.get(keyName));
                            keySyncQueue.remove(keyName);
                        } else if (callbackData.getSecond().test(currentBind)) {
                            syncKeyData(keyName, ImportMode.Vanilla, currentBind);
                        }
                    }
                }
            } catch (Exception | Error ex) {
                if (ModUtils.IS_VERBOSE) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Synchronizes KeyBind data from the Import Mode to the opposing mode
     *
     * @param keyName The raw name of the KeyBind, often the field name
     * @param mode    The origin import mode, depicting where the new keycode is coming from
     * @param keyCode The new keycode to synchronize
     */
    private void syncKeyData(final String keyName, final ImportMode mode, final int keyCode) {
        final Tuple<KeyBinding, Pair<Runnable, Predicate<Integer>>, DataConsumer<Throwable>> keyData = KEY_MAPPINGS.getOrDefault(keyName, null);
        if (mode == ImportMode.Config) {
            keyData.getFirst().setKeyCode(keyCode);
        } else if (mode == ImportMode.Vanilla) {
            CraftPresence.CONFIG.setProperty(keyName, keyCode);
            CraftPresence.CONFIG.save();
        } else if (mode == ImportMode.Specific) {
            syncKeyData(keyData.getFirst().getKeyDescription(), ImportMode.Config, keyCode);
            syncKeyData(keyName, ImportMode.Vanilla, keyCode);
        } else {
            if (ModUtils.IS_VERBOSE) {
                ModUtils.LOG.debugWarn(ModUtils.TRANSLATOR.translate("craftpresence.logger.warning.convert.invalid", keyName, mode.name()));
            }
        }
    }

    /**
     * Filter Key Mappings based on the specified filter mode and filter data
     *
     * @param mode       The filter mode to interpret data by
     * @param filterData The filter data to attach to the filter mode
     * @return The filtered key mappings
     */
    public Map<String, Tuple<KeyBinding, Pair<Runnable, Predicate<Integer>>, DataConsumer<Throwable>>> getKeyMappings(final FilterMode mode, final List<String> filterData) {
        final Map<String, Tuple<KeyBinding, Pair<Runnable, Predicate<Integer>>, DataConsumer<Throwable>>> filteredMappings = Maps.newHashMap();

        for (String keyName : KEY_MAPPINGS.keySet()) {
            if (mode == FilterMode.None ||
                    mode == FilterMode.Category ||
                    mode == FilterMode.Id ||
                    (mode == FilterMode.Name && filterData.contains(keyName))
            ) {
                final Tuple<KeyBinding, Pair<Runnable, Predicate<Integer>>, DataConsumer<Throwable>> keyData = KEY_MAPPINGS.get(keyName);
                if (mode == FilterMode.None ||
                        (mode == FilterMode.Category && filterData.contains(keyData.getFirst().getKeyCategory())) ||
                        (mode == FilterMode.Id && filterData.contains(keyData.getFirst().getKeyDescription())) ||
                        mode == FilterMode.Name
                ) {
                    filteredMappings.put(keyName, keyData);
                }
            }
        }
        return filteredMappings;
    }

    /**
     * Filter Key Mappings based on the specified filter mode and filter data
     *
     * @return The filtered key mappings
     */
    public Map<String, Tuple<KeyBinding, Pair<Runnable, Predicate<Integer>>, DataConsumer<Throwable>>> getKeyMappings() {
        return getKeyMappings(FilterMode.None, Lists.newArrayList());
    }

    /**
     * Enum Mapping dictating where KeyBind Data is deriving from
     * <p>
     * Format:
     * - Config: Signals Client to use the Config Value to Sync to the Controls Menu
     * * The keyName in syncKeyData in this case should be the namespaced value
     * - Vanilla: Signals Client to use the Controls menu to Sync to the Config Value
     * * The keyName in syncKeyData in this case should be the field name from ConfigUtils
     * - Specific: Signals Client to force both the controls menu and config value to a specific value
     */
    public enum ImportMode {
        Config, Vanilla, Specific
    }

    public enum FilterMode {
        Category, Name, Id, None
    }
}
