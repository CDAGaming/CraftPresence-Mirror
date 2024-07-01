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

package com.gitlab.cdagaming.unilib.utils;

import com.gitlab.cdagaming.unilib.ModUtils;
import com.gitlab.cdagaming.unilib.core.CoreUtils;
import com.gitlab.cdagaming.unilib.core.impl.KeyConverter;
import io.github.cdagaming.unicore.impl.TriFunction;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Keyboard Utilities to Parse KeyCodes and handle KeyCode Events
 *
 * @author CDAGaming
 */
public class KeyUtils {
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
     * The game instance for this module
     */
    private final Supplier<Minecraft> instance;
    /**
     * The game protocol for this module
     */
    private final int protocol;
    /**
     * Determines whether KeyBindings have been fully registered and attached to needed systems.
     */
    private boolean keysRegistered = false;
    /**
     * Whether registered keys can be iterated over
     */
    private Supplier<Boolean> canCheckKeys = () -> true;
    /**
     * Whether key sync operations are allowed
     */
    private Supplier<Boolean> canSyncKeys = () -> true;

    /**
     * Create an instance of this class.
     *
     * @param instance The game instance for this module
     * @param protocol The game protocol for this module
     */
    public KeyUtils(final Supplier<Minecraft> instance, final int protocol) {
        this.instance = instance;
        this.protocol = protocol;
    }

    /**
     * Create an instance of this class.
     */
    public KeyUtils() {
        this(ModUtils.INSTANCE_GETTER, ModUtils.MCProtocolID);
    }

    /**
     * Retrieve the game instance for this module
     *
     * @return the game instance for this module
     */
    public Minecraft getInstance() {
        return instance.get();
    }

    /**
     * Retrieve whether registered keys can be iterated over
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public boolean canCheckKeys() {
        return canCheckKeys.get();
    }

    /**
     * Retrieve whether key sync operations are allowed
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public boolean canSyncKeys() {
        return canSyncKeys.get();
    }

    /**
     * Sets whether registered keys can be iterated over
     *
     * @param canCheckKeys the new condition
     * @return the current instance of this module
     */
    public KeyUtils setCanCheckKeys(final Supplier<Boolean> canCheckKeys) {
        this.canCheckKeys = canCheckKeys;
        return this;
    }

    /**
     * Sets whether registered keys can be iterated over
     *
     * @param canCheckKeys the new condition
     * @return the current instance of this module
     */
    public KeyUtils setCanCheckKeys(final boolean canCheckKeys) {
        return setCanCheckKeys(() -> canCheckKeys);
    }

    /**
     * Sets whether key sync operations are allowed
     *
     * @param canSyncKeys the new condition
     * @return the current instance of this module
     */
    public KeyUtils setCanSyncKeys(final Supplier<Boolean> canSyncKeys) {
        this.canSyncKeys = canSyncKeys;
        return this;
    }

    /**
     * Sets whether key sync operations are allowed
     *
     * @param canSyncKeys the new condition
     * @return the current instance of this module
     */
    public KeyUtils setCanSyncKeys(final boolean canSyncKeys) {
        return setCanSyncKeys(() -> canSyncKeys);
    }

    /**
     * Determine if the Source KeyCode fulfills the following conditions
     * <p>
     * 1) Is Not Contained or falls under a valid key condition mapping
     *
     * @param sourceKeyCode The Source KeyCode to Check
     * @return {@link Boolean#TRUE} if and only if a Valid KeyCode
     */
    public boolean isValidKeyCode(final int sourceKeyCode) {
        return KeyConverter.isValidKeyCode(sourceKeyCode, protocol);
    }

    /**
     * Determine if the Source KeyCode fulfills the following conditions
     * <p>
     * 1) Is Not Contained or Listed within clearKeys
     *
     * @param sourceKeyCode The Source KeyCode to Check
     * @return {@link Boolean#TRUE} if and only if a Valid KeyCode
     */
    public boolean isValidClearCode(final int sourceKeyCode) {
        return KeyConverter.isValidClearCode(sourceKeyCode, protocol);
    }

    /**
     * Determine the LWJGL KeyCode Name for the inputted KeyCode
     *
     * @param original A KeyCode, in Integer Form
     * @return Either an LWJGL KeyCode Name or the KeyCode if none can be found
     */
    public String getKeyName(final int original) {
        return KeyConverter.getKeyName(original, (originalKeyCode, unknownKeyCode, unknownKeyName) -> {
            // If no other Mapping Layer contains the KeyCode Name,
            // Fallback to LWJGL Methods to retrieve the KeyCode Name
            final String altKeyName = Integer.toString(originalKeyCode);
            String keyName;
            if (!originalKeyCode.equals(unknownKeyCode)) {
                keyName = Keyboard.getKeyName(originalKeyCode);
            } else {
                keyName = unknownKeyName;
            }

            // If Key Name is not Empty or Null, use that, otherwise use original
            return StringUtils.getOrDefault(keyName, altKeyName);
        }, protocol);
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
    private KeyBinding createKey(final String id, final String name, final String category, final int defaultKey, final int currentKey) {
        final KeyBinding result = new KeyBinding(name, defaultKey, category);
        keySyncQueue.put(id, currentKey);
        return result;
    }

    /**
     * Registers a new Keybinding with the specified info
     *
     * @param id                The keybinding internal identifier, used for the Key Sync Queue
     * @param name              The name or description of the keybinding
     * @param nameFormatter     The Function to supply extra formatting to the key name
     * @param category          The category for the keybinding
     * @param categoryFormatter The Function to supply extra formatting to the key category
     * @param defaultKey        The default key for this binding
     * @param currentKey        The current key for this binding
     * @param detailsSupplier   The supplier for additional key details
     * @param onPress           The event to execute when the KeyBind is being pressed
     * @param onBind            The event to execute when the KeyBind is being rebound to another key
     * @param onOutdated        The event to determine whether the KeyBind is up-to-date (Ex: Vanilla==Config)
     * @param callback          The event to execute upon an exception occurring during KeyBind events
     * @return the created and registered KeyBind instance
     */
    public KeyBinding registerKey(final String id, final String name,
                                  final Function<String, String> nameFormatter,
                                  final String category,
                                  final Function<String, String> categoryFormatter,
                                  final int defaultKey, final int currentKey,
                                  final Supplier<String> detailsSupplier,
                                  final Runnable onPress,
                                  final BiConsumer<Integer, Boolean> onBind,
                                  final Predicate<Integer> onOutdated,
                                  final TriFunction<Throwable, String, KeyBindData, Boolean> callback) {
        if (areKeysRegistered()) {
            throw new UnsupportedOperationException("KeyBindings already registered!");
        }

        final KeyBinding keyBind = createKey(id, name, category, defaultKey, currentKey);
        KEY_MAPPINGS.put(
                id,
                new KeyBindData(
                        keyBind,
                        nameFormatter,
                        keyBind::getKeyCategory,
                        categoryFormatter,
                        keyBind::getKeyCodeDefault,
                        detailsSupplier,
                        onPress, onBind, onOutdated,
                        callback
                )
        );
        return keyBind;
    }

    /**
     * Registers a new Keybinding with the specified info
     *
     * @param id              The keybinding internal identifier, used for the Key Sync Queue
     * @param name            The name or description of the keybinding
     * @param category        The category for the keybinding
     * @param defaultKey      The default key for this binding
     * @param currentKey      The current key for this binding
     * @param detailsSupplier The supplier for additional key details
     * @param onPress         The event to execute when the KeyBind is being pressed
     * @param onBind          The event to execute when the KeyBind is being rebound to another key
     * @param onOutdated      The event to determine whether the KeyBind is up-to-date (Ex: Vanilla==Config)
     * @param callback        The event to execute upon an exception occurring during KeyBind events
     * @return the created and registered KeyBind instance
     */
    public KeyBinding registerKey(final String id, final String name,
                                  final String category,
                                  final int defaultKey, final int currentKey,
                                  final Supplier<String> detailsSupplier,
                                  final Runnable onPress,
                                  final BiConsumer<Integer, Boolean> onBind,
                                  final Predicate<Integer> onOutdated,
                                  final TriFunction<Throwable, String, KeyBindData, Boolean> callback) {
        return registerKey(
                id, name, (description) -> description,
                category, (categoryName) -> categoryName,
                defaultKey, currentKey,
                detailsSupplier,
                onPress, onBind, onOutdated,
                callback
        );
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
    public KeyBinding registerKey(final String id, final String name,
                                  final String category,
                                  final int defaultKey, final int currentKey,
                                  final Runnable onPress,
                                  final BiConsumer<Integer, Boolean> onBind,
                                  final Predicate<Integer> onOutdated,
                                  final TriFunction<Throwable, String, KeyBindData, Boolean> callback) {
        return registerKey(
                id, name,
                category,
                defaultKey, currentKey,
                () -> "",
                onPress, onBind, onOutdated,
                callback
        );
    }

    /**
     * Set the key for the specified KeyBinding
     *
     * @param instance the KeyBind instance to modify
     * @param newKey   the new key for the specified KeyBinding
     */
    private void setKey(final KeyBinding instance, final int newKey) {
        instance.setKeyCode(newKey);
        KeyBinding.resetKeyBindingArrayAndHash();
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
     * Tick Method for KeyUtils, that should run on each tick
     */
    public void onTick() {
        if (instance == null || getInstance() == null) {
            return;
        }

        if (!areKeysRegistered()) {
            if (getInstance().gameSettings != null) {
                for (KeyBindData entry : KEY_MAPPINGS.values()) {
                    final String category = entry.category();
                    final Map<String, Integer> categoryMap = KeyBinding.CATEGORY_ORDER;
                    if (!categoryMap.containsKey(category)) {
                        final Optional<Integer> largest = categoryMap.values().stream().max(Integer::compareTo);
                        final int largestInt = largest.orElse(0);
                        categoryMap.put(category, largestInt + 1);
                    }
                    getInstance().gameSettings.keyBindings = StringUtils.addToArray(getInstance().gameSettings.keyBindings, entry.binding());
                }
                keysRegistered = true;
            } else {
                return;
            }
        }

        if (Keyboard.isCreated() && canCheckKeys()) {
            final boolean isLwjgl2 = protocol <= 340;
            final int unknownKeyCode = isLwjgl2 ? -1 : 0;
            final String unknownKeyName = (isLwjgl2 ? KeyConverter.fromGlfw : KeyConverter.toGlfw).get(unknownKeyCode).name();
            try {
                for (Map.Entry<String, KeyBindData> entry : getKeyEntries()) {
                    final String keyName = entry.getKey();
                    final KeyBindData keyData = entry.getValue();
                    final int currentBind = keyData.keyCode();
                    boolean hasBeenRun = false;

                    if (!getKeyName(currentBind).equals(unknownKeyName) && !isValidClearCode(currentBind)) {
                        // Only process the key if it is not an unknown or invalid key
                        if (Keyboard.isKeyDown(currentBind) && !(getInstance().currentScreen instanceof GuiControls)) {
                            try {
                                keyData.runEvent().run();
                            } catch (Throwable ex) {
                                boolean resetKey;
                                if (keyData.errorCallback() != null) {
                                    resetKey = keyData.errorCallback().apply(ex, keyName, keyData);
                                } else {
                                    CoreUtils.LOG.error(ex);
                                    resetKey = true;
                                }

                                if (resetKey) {
                                    syncKeyData(keyName, ImportMode.Specific, keyData.defaultKeyCode());
                                }
                            } finally {
                                hasBeenRun = true;
                            }
                        }
                    }

                    // Only check for Keyboard updates if the key is not active but is in queue for a sync
                    if (!hasBeenRun && canSyncKeys()) {
                        if (keySyncQueue.containsKey(keyName)) {
                            syncKeyData(keyName, ImportMode.Config, keySyncQueue.get(keyName));
                            keySyncQueue.remove(keyName);
                        } else if (keyData.vanillaPredicate().test(currentBind)) {
                            syncKeyData(keyName, ImportMode.Vanilla, currentBind);
                        }
                    }
                }
            } catch (Throwable ex) {
                CoreUtils.LOG.debugError(ex);
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
     * @param nameFormatter      The Function to supply extra formatting to the key name
     * @param categorySupplier   The supplier for the KeyBind category title
     * @param categoryFormatter  The Function to supply extra formatting to the key category
     * @param defaultKeySupplier The supplier for the default key for this KeyBind
     * @param detailsSupplier    The supplier for additional key details
     * @param runEvent           The event to execute when the KeyBind is being pressed
     * @param configEvent        The event to execute when the KeyBind is being rebound to another key
     * @param vanillaPredicate   The event to determine whether the KeyBind is up-to-date (Ex: Vanilla==Config)
     * @param errorCallback      The event to execute upon an exception occurring during KeyBind events (Format: [exception,keyName,keyData] returns resetKey)
     */
    public record KeyBindData(KeyBinding binding,
                              Function<String, String> nameFormatter,
                              Supplier<String> categorySupplier,
                              Function<String, String> categoryFormatter,
                              Supplier<Integer> defaultKeySupplier,
                              Supplier<String> detailsSupplier,
                              Runnable runEvent, BiConsumer<Integer, Boolean> configEvent,
                              Predicate<Integer> vanillaPredicate,
                              TriFunction<Throwable, String, KeyBindData, Boolean> errorCallback) {
        /**
         * Retrieve the category for this KeyBind
         *
         * @return the KeyBind category
         */
        public String category() {
            return categorySupplier().get();
        }

        /**
         * Retrieve the category name for this KeyBind
         *
         * @return the KeyBind category name
         */
        public String categoryName() {
            return categoryFormatter().apply(category());
        }

        /**
         * Retrieve the additional KeyBind details
         *
         * @return the additional KeyBind details
         */
        public String details() {
            return detailsSupplier().get();
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
         * Retrieve the display name for this KeyBind
         *
         * @return the KeyBind display name
         */
        public String displayName() {
            return nameFormatter().apply(description());
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
