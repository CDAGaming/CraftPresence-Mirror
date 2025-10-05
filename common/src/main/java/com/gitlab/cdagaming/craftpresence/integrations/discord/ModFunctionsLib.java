/*
 * MIT License
 *
 * Copyright (c) 2018 - 2025 CDAGaming (cstack2011@yahoo.com)
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

package com.gitlab.cdagaming.craftpresence.integrations.discord;

import com.gitlab.cdagaming.craftpresence.core.integrations.discord.DiscordUtils;
import com.gitlab.cdagaming.craftpresence.core.integrations.discord.FunctionsLib;
import com.gitlab.cdagaming.unilib.ModUtils;
import com.gitlab.cdagaming.unilib.utils.ComponentUtils;
import com.gitlab.cdagaming.unilib.utils.NbtUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import org.meteordev.starscript.Starscript;
import org.meteordev.starscript.value.Value;

import java.util.List;

/**
 * Standard library with some mod-specific functions and variables.
 *
 * @author CDAGaming
 */
public class ModFunctionsLib {
    public static void init(DiscordUtils client) {
        // General Functions
        client.syncFunction("mcTranslate", (ss, argCount) -> ModFunctionsLib.mcTranslate(client, ss, argCount));
        client.syncFunction("getNbt", (ss, argCount) -> ModFunctionsLib.getNbt(client, ss, argCount));
        client.syncFunction("getComponent", (ss, argCount) -> ModFunctionsLib.getComponent(client, ss, argCount)); // MC 1.20.5+
    }

    public static Value mcTranslate(DiscordUtils client, Starscript ss, int argCount) {
        if (ModUtils.RAW_TRANSLATOR == null) ss.error("No available translations from game data, try again later.");
        return FunctionsLib.parseWith(client, ModUtils.RAW_TRANSLATOR, ss, argCount);
    }

    public static Value getComponent(DiscordUtils client, Starscript ss, int argCount) {
        if (argCount < 1 || argCount > 2)
            ss.error("getComponent() can only be used with 1-2 arguments, got %d.", argCount);
        Object result;

        String path = null;
        if (argCount == 2) {
            path = ss.popString("Second argument to getComponent() needs to be a string.");
        }
        Object data = ss.popObject("First argument to getComponent() needs to be a valid DataComponent object.");

        if (!StringUtils.isNullOrEmpty(path)) {
            result = ComponentUtils.parseComponent(
                    ComponentUtils.getComponent(data, path)
            );
        } else {
            result = ComponentUtils.getComponentMap(data);
        }
        return result != null ? client.toValue(result, true) : Value.null_();
    }

    public static Value getNbt(DiscordUtils client, Starscript ss, int argCount) {
        final List<Value> args = StringUtils.newArrayList();
        if (argCount < 1)
            ss.error("getNbt() requires one or more arguments, got %d.", argCount);
        for (int i = 0; i < argCount; i++) {
            args.add(ss.pop());
        }
        StringUtils.revlist(args);

        // Parse, then remove the source entity from arguments, if valid
        Value source = args.getFirst();
        Object data = null;
        if (source.isObject()) {
            data = source.getObject();
        }

        if (data == null) {
            ss.error("First argument to getNbt() needs to be a valid Entity or DataComponent object.");
        }
        args.removeFirst();

        final List<String> path = StringUtils.newArrayList();
        for (Value info : args) {
            path.add(info.toString());
        }
        final Object result = NbtUtils.parseTag(
                NbtUtils.getNbt(data, path.toArray(new String[0]))
        );
        return result != null ? client.toValue(result, true) : Value.null_();
    }
}
