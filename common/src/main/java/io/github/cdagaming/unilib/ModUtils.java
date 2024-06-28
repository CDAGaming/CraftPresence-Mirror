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

package io.github.cdagaming.unilib;

import io.github.cdagaming.unicore.utils.TranslationUtils;
import io.github.cdagaming.unilib.core.CoreUtils;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsSharedConstants;

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
    public static final Supplier<Minecraft> INSTANCE_GETTER = Minecraft::getMinecraft;
}
