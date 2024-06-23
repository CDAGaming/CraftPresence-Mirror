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

package com.gitlab.cdagaming.craftpresence.utils;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.gui.MainGui;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.impl.KeyConverter;
import com.gitlab.cdagaming.craftpresence.utils.gui.RenderUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
     * List of Keys that are in queue for later syncing operations
     */
    public final Map<String, Integer> keySyncQueue = StringUtils.newHashMap();
    /**
     * Key Mappings for Vanilla MC KeyBind Schema
     * <p>
     * Format: rawKeyField:keyMapping
     */
    private final Map<String, KeyBindData> KEY_MAPPINGS = StringUtils.newHashMap();
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
    public static boolean isValidKeyCode(final int sourceKeyCode) {
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
    public static boolean isValidClearCode(final int sourceKeyCode) {
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
        final String unknownKeyName = (ModUtils.MCProtocolID <= 340 ? KeyConverter.fromGlfw.get(unknownKeyCode) : KeyConverter.toGlfw.get(unknownKeyCode)).name();
        if (isValidKeyCode(original)) {
            // If Input is a valid Integer and Valid KeyCode,
            // Parse depending on Protocol
            if (ModUtils.MCProtocolID <= 340 && KeyConverter.toGlfw.containsKey(original)) {
                return KeyConverter.toGlfw.get(original).name();
            } else if (ModUtils.MCProtocolID > 340 && KeyConverter.fromGlfw.containsKey(original)) {
                return KeyConverter.fromGlfw.get(original).name();
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
     * Create a new KeyBinding with the specified info
     *
     * @param id         The keybinding internal identifier, used for the Key Sync Queue
     * @param name       The name or description of the keybinding
     * @param category   The category for the keybinding
     * @param defaultKey The default key for this binding
     * @param currentKey The current key for this binding
     * @return the created KeyBind instance
     */
    KeyBinding createKey(final String id, final String name, final String category, final int defaultKey, final int currentKey) {
        final KeyBinding result = new KeyBinding(name, defaultKey, category);
        keySyncQueue.put(id, currentKey);
        return result;
    }

    /**
     * Registers a new Keybinding with the specified info
     *
     * @param id         The keybinding internal identifier, used for the Key Sync Queue
     * @param name       The name or description of the keybinding
     * @param category   The category for the keybinding
     * @param defaultKey The default key for this binding
     * @param currentKey The current key for this binding
     * @param onPress    The event to execute when the KeyBind is being pressed
     * @param onBind     The event to execute when the KeyBind is being rebound to another key
     * @param onOutdated The event to determine whether the KeyBind is up-to-date (Ex: Vanilla==Config)
     * @param callback   The event to execute upon an exception occurring during KeyBind events
     * @return the created and registered KeyBind instance
     */
    KeyBinding registerKey(final String id, final String name,
                           final String category,
                           final int defaultKey, final int currentKey,
                           final Runnable onPress,
                           final BiConsumer<Integer, Boolean> onBind,
                           final Predicate<Integer> onOutdated,
                           final Consumer<Throwable> callback) {
        if (areKeysRegistered()) {
            throw new UnsupportedOperationException("KeyBindings already registered!");
        }

        final KeyBinding keyBind = createKey(id, name, category, defaultKey, currentKey);
        KEY_MAPPINGS.put(
                id,
                new KeyBindData(
                        keyBind,
                        keyBind::getKeyCategory,
                        keyBind::getKeyCodeDefault,
                        onPress, onBind, onOutdated,
                        callback
                )
        );
        return keyBind;
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
     * Registers KeyBindings and critical KeyCode information to the game's KeyCode systems
     * <p>Note: It's mandatory for KeyBindings to be registered here, or they will not be recognized on either end
     */
    void register() {
        registerKey(
                "configKeyCode",
                "key.craftpresence.config_keycode.name",
                "key.craftpresence.category",
                CraftPresence.CONFIG.accessibilitySettings.getDefaults().configKeyCode,
                CraftPresence.CONFIG.accessibilitySettings.configKeyCode,
                () -> {
                    if (!CraftPresence.GUIS.isFocused && !(CraftPresence.instance.currentScreen instanceof ExtendedScreen)) {
                        RenderUtils.openScreen(CraftPresence.instance, new MainGui(), CraftPresence.instance.currentScreen);
                    }
                },
                (keyCode, shouldSave) -> {
                    CraftPresence.CONFIG.accessibilitySettings.configKeyCode = keyCode;
                    if (shouldSave) {
                        CraftPresence.CONFIG.save();
                    }
                },
                vanillaBind -> vanillaBind != CraftPresence.CONFIG.accessibilitySettings.configKeyCode,
                null
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
     * Retrieves the unfiltered Key Names for Vanilla MC KeyBind Schema
     *
     * @return The unfiltered key names
     */
    public Set<String> getKeys() {
        return KEY_MAPPINGS.keySet();
    }

    /**
     * Retrieves the unfiltered Key Mappings for Vanilla MC KeyBind Schema
     * <p>
     * Format: rawKeyField:keyMapping
     *
     * @return The unfiltered key mappings
     */
    public Set<Map.Entry<String, KeyBindData>> getKeyEntries() {
        return KEY_MAPPINGS.entrySet();
    }

    /**
     * Tick Method for KeyUtils, that runs on each tick
     * <p>
     * Implemented @ {@link CommandUtils#reloadData}
     */
    void onTick() {
        if (!areKeysRegistered()) {
            if (CraftPresence.instance.gameSettings != null) {
                for (KeyBindData entry : KEY_MAPPINGS.values()) {
                    final String category = entry.category();
                    final Map<String, Integer> categoryMap = KeyBinding.CATEGORY_ORDER;
                    if (!categoryMap.containsKey(category)) {
                        final Optional<Integer> largest = categoryMap.values().stream().max(Integer::compareTo);
                        final int largestInt = largest.orElse(0);
                        categoryMap.put(category, largestInt + 1);
                    }
                    CraftPresence.instance.gameSettings.keyBindings = StringUtils.addToArray(CraftPresence.instance.gameSettings.keyBindings, entry.binding());
                }
                keysRegistered = true;
            } else {
                return;
            }
        }

        if (Keyboard.isCreated() && CraftPresence.CONFIG != null) {
            final int unknownKeyCode = (ModUtils.MCProtocolID <= 340 ? -1 : 0);
            final String unknownKeyName = (ModUtils.MCProtocolID <= 340 ? KeyConverter.fromGlfw.get(unknownKeyCode) : KeyConverter.toGlfw.get(unknownKeyCode)).name();
            try {
                for (Map.Entry<String, KeyBindData> entry : getKeyEntries()) {
                    final String keyName = entry.getKey();
                    final KeyBindData keyData = entry.getValue();
                    final int currentBind = keyData.keyCode();
                    boolean hasBeenRun = false;

                    if (!getKeyName(currentBind).equals(unknownKeyName) && !isValidClearCode(currentBind)) {
                        // Only process the key if it is not an unknown or invalid key
                        if (Keyboard.isKeyDown(currentBind) && !(CraftPresence.instance.currentScreen instanceof GuiControls)) {
                            try {
                                keyData.runEvent().run();
                            } catch (Throwable ex) {
                                if (keyData.errorCallback() != null) {
                                    keyData.errorCallback().accept(ex);
                                } else {
                                    Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.keycode", keyData.description()));
                                    syncKeyData(keyName, ImportMode.Specific, keyData.defaultKeyCode());
                                }
                            } finally {
                                hasBeenRun = true;
                            }
                        }
                    }

                    // Only check for Keyboard updates if the key is not active but is in queue for a sync
                    if (!hasBeenRun && !CraftPresence.CONFIG.hasChanged()) {
                        if (keySyncQueue.containsKey(keyName)) {
                            syncKeyData(keyName, ImportMode.Config, keySyncQueue.get(keyName));
                            keySyncQueue.remove(keyName);
                        } else if (keyData.vanillaPredicate().test(currentBind)) {
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
        final KeyBindData keyData = KEY_MAPPINGS.getOrDefault(keyName, null);
        if (mode == ImportMode.Config) {
            setKey(keyData.binding(), keyCode);
        } else if (mode == ImportMode.Vanilla) {
            keyData.configEvent().accept(keyCode, true);
        } else if (mode == ImportMode.Specific) {
            syncKeyData(keyData.description(), ImportMode.Config, keyCode);
            syncKeyData(keyName, ImportMode.Vanilla, keyCode);
        } else {
            Constants.LOG.debugWarn(Constants.TRANSLATOR.translate("craftpresence.logger.warning.convert.invalid", keyName, mode.name()));
        }
    }

    /**
     * Filter Key Mappings based on the specified filter data
     *
     * @param filterData The filter data to attach, representing categories
     * @return The filtered key mappings
     */
    public Map<String, KeyBindData> getKeyMappings(final List<String> filterData) {
        final Map<String, KeyBindData> filteredMappings = StringUtils.newHashMap();
        if (filterData == null || filterData.isEmpty()) {
            filteredMappings.putAll(KEY_MAPPINGS);
            return filteredMappings;
        }

        for (Map.Entry<String, KeyBindData> entry : getKeyEntries()) {
            final String keyName = entry.getKey();
            final KeyBindData keyData = entry.getValue();
            if (filterData.contains(keyData.category())) {
                filteredMappings.put(keyName, keyData);
            }
        }
        return filteredMappings;
    }

    /**
     * Filter Key Mappings based on the specified filter data
     *
     * @param filterData The filter data to attach, representing categories
     * @return The filtered key mappings
     */
    public Map<String, KeyBindData> getKeyMappings(final String... filterData) {
        return getKeyMappings((filterData == null || filterData.length == 0) ? null : StringUtils.newArrayList(filterData));
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
     * Mapping dictating KeyBind data attributes
     *
     * @param binding            The KeyBinding object attached to this instance
     * @param categorySupplier   The supplier for the KeyBind category title
     * @param defaultKeySupplier The supplier for the default key for this KeyBind
     * @param runEvent           The event to execute when the KeyBind is being pressed
     * @param configEvent        The event to execute when the KeyBind is being rebound to another key
     * @param vanillaPredicate   The event to determine whether the KeyBind is up-to-date (Ex: Vanilla==Config)
     * @param errorCallback      The event to execute upon an exception occurring during KeyBind events
     */
    public record KeyBindData(KeyBinding binding,
                              Supplier<String> categorySupplier,
                              Supplier<Integer> defaultKeySupplier,
                              Runnable runEvent, BiConsumer<Integer, Boolean> configEvent,
                              Predicate<Integer> vanillaPredicate, Consumer<Throwable> errorCallback) {
        /**
         * Retrieve the category for this KeyBind
         *
         * @return the KeyBind category
         */
        public String category() {
            return categorySupplier().get();
        }

        /**
         * Retrieve the description for this KeyBind
         *
         * @return the KeyBind description
         */
        public String description() {
            return binding().getKeyDescription();
        }

        /**
         * Retrieve the current key for this KeyBind
         *
         * @return the currently assigned key code
         */
        public int keyCode() {
            return binding().getKeyCode();
        }

        /**
         * Retrieve the default key for this KeyBind
         *
         * @return the default assigned key code
         */
        public int defaultKeyCode() {
            return defaultKeySupplier().get();
        }
    }
}
