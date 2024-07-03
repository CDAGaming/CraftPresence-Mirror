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

package com.gitlab.cdagaming.unilib;

import com.gitlab.cdagaming.unilib.core.CoreUtils;
import io.github.cdagaming.unicore.utils.TranslationUtils;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.realms.RealmsSharedConstants;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Constant Variables and Methods used throughout the Application
 *
 * @author CDAGaming
 */
public class ModUtils {
    /**
     * The Detected Minecraft Version
     */
    public static final String MCVersion = RealmsSharedConstants.VERSION_STRING;

    /**
     * The Detected Minecraft Protocol Version
     */
    public static final int MCProtocolID = RealmsSharedConstants.NETWORK_PROTOCOL_VERSION;

    /**
     * The Detected Brand Information within Minecraft
     */
    public static final String BRAND = CoreUtils.findGameBrand(ClientBrandRetriever.getClientModName());

    /**
     * The Main Game's Instance of {@link TranslationUtils} for Localization and Translating Data Strings
     */
    public static final TranslationUtils RAW_TRANSLATOR = CoreUtils.findGameTranslations(MCProtocolID);

    /**
     * Getter for the Game Client Instance
     */
    private static final Supplier<Minecraft> INSTANCE_GETTER = Minecraft::getMinecraft;

    /**
     * Consumer Event for Resource Reload Listener Registration
     */
    private static final BiConsumer<String, IResourceManagerReloadListener> RELOAD_LISTENER_HOOK = (
            (id, listener) -> ((SimpleReloadableResourceManager) getMinecraft().getResourceManager()).registerReloadListener(listener)
    );

    /**
     * Consumer Event for running events on the Main Game Thread
     */
    private static final BiConsumer<Minecraft, Runnable> MAIN_THREAD_EXECUTOR = Minecraft::addScheduledTask;

    /**
     * Retrieve the Game Client Instance Supplier
     *
     * @return the Game Client Instance Supplier
     */
    public static Supplier<Minecraft> getMinecraftSupplier() {
        return INSTANCE_GETTER;
    }

    /**
     * Retrieve the Game Client Instance
     *
     * @return the Game Client Instance
     */
    public static Minecraft getMinecraft() {
        return getMinecraftSupplier().get();
    }

    /**
     * Register a Resource Reload Listener
     *
     * @param id       The ID for the listener
     * @param listener The Listener to register
     */
    public static void registerReloadListener(final String id, final IResourceManagerReloadListener listener) {
        RELOAD_LISTENER_HOOK.accept(id, listener);
    }

    /**
     * Execute and Event to run on the Main Game Thread, if possible
     *
     * @param client the game client instance
     * @param event  the event to run
     */
    public static void executeOnMainThread(final Minecraft client, final Runnable event) {
        MAIN_THREAD_EXECUTOR.accept(client, event);
    }

    /**
     * Execute and Event to run on the Main Game Thread, if possible
     *
     * @param event the event to run
     */
    public static void executeOnMainThread(final Runnable event) {
        MAIN_THREAD_EXECUTOR.accept(getMinecraft(), event);
    }
}
