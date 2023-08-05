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

package com.gitlab.cdagaming.craftpresence.utils;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.gui.MainGui;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.impl.KeyConverter;
import com.gitlab.cdagaming.craftpresence.core.impl.Pair;
import com.gitlab.cdagaming.craftpresence.core.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.core.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.RenderUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
    private static final List<Integer> clearKeys = StringUtils.newArrayList();

    /**
     * Allowed KeyCode Start Limit and Individual Filters
     */
    private static final List<Integer> invalidKeys = StringUtils.newArrayList();
    /**
     * Key Mappings for Vanilla MC KeyBind Schema
     * <p>
     * Format: rawKeyField:[keyBindInstance:(runEvent,configEvent,vanillaPredicate):errorCallback]
     */
    private final Map<String, Tuple<KeyBinding, Tuple<Runnable, BiConsumer<Integer, Boolean>, Predicate<Integer>>, Consumer<Throwable>>> KEY_MAPPINGS = StringUtils.newHashMap();
    /**
     * List of Keys that are in queue for later syncing operations
     */
    public Map<String, Integer> keySyncQueue = StringUtils.newHashMap();
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
     * @return {@link Boolean#TRUE} if and only if a Valid KeyCode
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
     * @return {@link Boolean#TRUE} if and only if a Valid KeyCode
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
                return StringUtils.getOrDefault(keyName, altKeyName);
            }
        } else {
            // If Not a Valid KeyCode, return the appropriate Unknown Keycode
            return unknownKeyName;
        }
    }

    /**
     * Converts a KeyCode using the Specified Conversion Mode, if possible
     * <p>
     * Note: If None is Used on a Valid Value, this function can be used as verification, if any
     *
     * @param originalKey The original Key to Convert
     * @param protocol    The Protocol to Target for this conversion
     * @param mode        The Conversion Mode to convert the keycode to
     * @return The resulting converted KeyCode, or the mode's unknown key
     */
    public static int convertKey(final int originalKey, final int protocol, final KeyConverter.ConversionMode mode) {
        final Pair<Integer, String> unknownKeyData = mode == KeyConverter.ConversionMode.Lwjgl2 ? KeyConverter.fromGlfw.get(-1) : KeyConverter.toGlfw.get(0);
        int resultKey = (protocol <= 340 ? -1 : 0);

        if (mode == KeyConverter.ConversionMode.Lwjgl2) {
            resultKey = KeyConverter.fromGlfw.getOrDefault(originalKey, unknownKeyData).getFirst();
        } else if (mode == KeyConverter.ConversionMode.Lwjgl3) {
            resultKey = KeyConverter.toGlfw.getOrDefault(originalKey, unknownKeyData).getFirst();
        } else if (mode == KeyConverter.ConversionMode.None) {
            // If Input is a valid Integer and Valid KeyCode,
            // Retain the Original Value
            if (protocol <= 340 && KeyConverter.toGlfw.containsKey(originalKey)) {
                resultKey = originalKey;
            } else if (protocol > 340 && KeyConverter.fromGlfw.containsKey(originalKey)) {
                resultKey = originalKey;
            }
        }

        if (resultKey == originalKey && mode != KeyConverter.ConversionMode.None) {
            Constants.LOG.debugWarn(Constants.TRANSLATOR.translate("craftpresence.logger.warning.convert.invalid", Integer.toString(resultKey), mode.name()));
        }

        return resultKey;
    }

    /**
     * Create a new KeyBinding with the specified info
     *
     * @param id         The keybinding internal identifier, used for the Key Sync Queue
     * @param name       The name or description of the keybinding
     * @param category   The category for the keybinding
     * @param defaultKey The default key for this binding
     * @param currentKey The current key for this binding
     * @return the created KeyBind
     */
    KeyBinding createKey(final String id, final String name, final String category, final int defaultKey, final int currentKey) {
        final KeyBinding result = new KeyBinding(name, defaultKey, category);
        keySyncQueue.put(id, currentKey);
        return result;
    }

    /**
     * Set the key for the specified KeyBinding
     *
     * @param instance the KeyBind instance to modify
     * @param newKey   the new key for the specified KeyBinding
     */
    void setKey(final KeyBinding instance, final int newKey) {
        instance.setKeyCode(newKey);
        KeyBinding.resetKeyBindingArrayAndHash();
    }

    /**
     * Registers KeyBindings and critical KeyCode information to MC's KeyCode systems
     * <p>Note: It's mandatory for KeyBindings to be registered here, or they will not be recognized on either end
     */
    void register() {
        KEY_MAPPINGS.put(
                "configKeyCode",
                new Tuple<>(
                        createKey(
                                "configKeyCode",
                                "key.craftpresence.config_keycode.name",
                                "key.craftpresence.category",
                                CraftPresence.CONFIG.accessibilitySettings.getDefaults().configKeyCode,
                                CraftPresence.CONFIG.accessibilitySettings.configKeyCode
                        ),
                        new Tuple<>(
                                () -> {
                                    if (!CraftPresence.GUIS.isFocused && !(CraftPresence.instance.currentScreen instanceof ExtendedScreen)) {
                                        RenderUtils.openScreen(CraftPresence.instance, new MainGui(CraftPresence.instance.currentScreen));
                                    }
                                },
                                (keyCode, shouldSave) -> {
                                    CraftPresence.CONFIG.accessibilitySettings.configKeyCode = keyCode;
                                    if (shouldSave) {
                                        CraftPresence.CONFIG.save();
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
     * @return {@link Boolean#TRUE} if and only if the keybindings are successfully registered
     */
    public boolean areKeysRegistered() {
        return keysRegistered;
    }

    /**
     * Retrieves the unfiltered Key Mappings for Vanilla MC KeyBind Schema
     * <p>
     * Format: rawKeyField:[keyBindInstance:(runEvent,configEvent,updatePredicate):errorCallback]
     *
     * @return The unfiltered key mappings
     */
    public Map<String, Tuple<KeyBinding, Tuple<Runnable, BiConsumer<Integer, Boolean>, Predicate<Integer>>, Consumer<Throwable>>> getRawKeyMappings() {
        return StringUtils.newHashMap(KEY_MAPPINGS);
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
                for (Map.Entry<String, Tuple<KeyBinding, Tuple<Runnable, BiConsumer<Integer, Boolean>, Predicate<Integer>>, Consumer<Throwable>>> entry : KEY_MAPPINGS.entrySet()) {
                    final KeyBinding mapping = entry.getValue().getFirst();
                    final Map<String, Integer> categoryMap = KeyBinding.CATEGORY_ORDER;
                    if (!categoryMap.containsKey(mapping.getKeyCategory())) {
                        final Optional<Integer> largest = categoryMap.values().stream().max(Integer::compareTo);
                        final int largestInt = largest.orElse(0);
                        categoryMap.put(mapping.getKeyCategory(), largestInt + 1);
                    }
                    CraftPresence.instance.gameSettings.keyBindings = StringUtils.addToArray(CraftPresence.instance.gameSettings.keyBindings, mapping);
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
                for (Map.Entry<String, Tuple<KeyBinding, Tuple<Runnable, BiConsumer<Integer, Boolean>, Predicate<Integer>>, Consumer<Throwable>>> entry : KEY_MAPPINGS.entrySet()) {
                    final String keyName = entry.getKey();
                    final Tuple<KeyBinding, Tuple<Runnable, BiConsumer<Integer, Boolean>, Predicate<Integer>>, Consumer<Throwable>> keyData = entry.getValue();
                    final KeyBinding keyBind = keyData.getFirst();
                    final Tuple<Runnable, BiConsumer<Integer, Boolean>, Predicate<Integer>> callbackData = keyData.getSecond();
                    final int currentBind = keyBind.getKeyCode();
                    boolean hasBeenRun = false;

                    if (!getKeyName(currentBind).equals(unknownKeyName) && !isValidClearCode(currentBind)) {
                        // Only process the key if it is not an unknown or invalid key
                        if (Keyboard.isKeyDown(currentBind) && !(CraftPresence.instance.currentScreen instanceof GuiControls)) {
                            try {
                                callbackData.getFirst().run();
                            } catch (Throwable ex) {
                                if (keyData.getThird() != null) {
                                    keyData.getThird().accept(ex);
                                } else {
                                    Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.keycode", keyBind.getKeyDescription()));
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
                        } else if (callbackData.getThird().test(currentBind)) {
                            syncKeyData(keyName, ImportMode.Vanilla, currentBind);
                        }
                    }
                }
            } catch (Throwable ex) {
                Constants.LOG.debugError(ex);
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
        final Tuple<KeyBinding, Tuple<Runnable, BiConsumer<Integer, Boolean>, Predicate<Integer>>, Consumer<Throwable>> keyData = KEY_MAPPINGS.getOrDefault(keyName, null);
        if (mode == ImportMode.Config) {
            setKey(keyData.getFirst(), keyCode);
        } else if (mode == ImportMode.Vanilla) {
            keyData.getSecond().getSecond().accept(keyCode, true);
        } else if (mode == ImportMode.Specific) {
            syncKeyData(keyData.getFirst().getKeyDescription(), ImportMode.Config, keyCode);
            syncKeyData(keyName, ImportMode.Vanilla, keyCode);
        } else {
            Constants.LOG.debugWarn(Constants.TRANSLATOR.translate("craftpresence.logger.warning.convert.invalid", keyName, mode.name()));
        }
    }

    /**
     * Filter Key Mappings based on the specified filter mode and filter data
     *
     * @param mode       The filter mode to interpret data by
     * @param filterData The filter data to attach to the filter mode
     * @return The filtered key mappings
     */
    public Map<String, Tuple<KeyBinding, Tuple<Runnable, BiConsumer<Integer, Boolean>, Predicate<Integer>>, Consumer<Throwable>>> getKeyMappings(final FilterMode mode, final List<String> filterData) {
        final Map<String, Tuple<KeyBinding, Tuple<Runnable, BiConsumer<Integer, Boolean>, Predicate<Integer>>, Consumer<Throwable>>> filteredMappings = StringUtils.newHashMap();

        for (Map.Entry<String, Tuple<KeyBinding, Tuple<Runnable, BiConsumer<Integer, Boolean>, Predicate<Integer>>, Consumer<Throwable>>> entry : KEY_MAPPINGS.entrySet()) {
            final String keyName = entry.getKey();
            if (mode == FilterMode.None ||
                    mode == FilterMode.Category ||
                    mode == FilterMode.Id ||
                    (mode == FilterMode.Name && filterData.contains(keyName))
            ) {
                final Tuple<KeyBinding, Tuple<Runnable, BiConsumer<Integer, Boolean>, Predicate<Integer>>, Consumer<Throwable>> keyData = entry.getValue();
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
    public Map<String, Tuple<KeyBinding, Tuple<Runnable, BiConsumer<Integer, Boolean>, Predicate<Integer>>, Consumer<Throwable>>> getKeyMappings() {
        return getKeyMappings(FilterMode.None, StringUtils.newArrayList());
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
        /**
         * Constant for the "Config" Import Mode.
         */
        Config,
        /**
         * Constant for the "Vanilla" Import Mode.
         */
        Vanilla,
        /**
         * Constant for the "Specific" Import Mode.
         */
        Specific
    }

    /**
     * Constants representing various Filter Mode Types
     */
    public enum FilterMode {
        /**
         * Constant for the "Category" Filter Mode.
         */
        Category,
        /**
         * Constant for the "Name" Filter Mode.
         */
        Name,
        /**
         * Constant for the "Id" Filter Mode.
         */
        Id,
        /**
         * Constant for the "None" Filter Mode.
         */
        None
    }
}
