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

package com.gitlab.cdagaming.craftpresence.integrations.discord;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.core.utils.*;
import com.gitlab.cdagaming.craftpresence.utils.NbtUtils;
import com.gitlab.cdagaming.craftpresence.utils.SystemUtils;
import com.gitlab.cdagaming.craftpresence.utils.TranslationUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.meteordev.starscript.StandardLib;
import org.meteordev.starscript.Starscript;
import org.meteordev.starscript.value.Value;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Standard library with some default functions and variables.
 *
 * @author CDAGaming
 */
public class FunctionsLib {
    public static void init(Starscript ss) {
        StandardLib.init(ss);

        // General Functions
        ss.set("format", FunctionsLib::format);
        ss.set("translate", FunctionsLib::translate);
        ss.set("mcTranslate", FunctionsLib::mcTranslate);
        ss.set("getFields", FunctionsLib::getFields);
        ss.set("getMethods", FunctionsLib::getMethods);
        ss.set("getJsonElement", FunctionsLib::getJsonElement);
        ss.set("randomString", FunctionsLib::randomString);
        ss.set("getFirst", FunctionsLib::getFirst);
        ss.set("getNbt", FunctionsLib::getNbt);
        ss.set("getNamespace", FunctionsLib::getNamespace);
        ss.set("getPath", FunctionsLib::getPath);

        // MathUtils
        ss.set("isWithinValue", FunctionsLib::isWithinValue);

        // DiscordUtils
        ss.set("getResult", FunctionsLib::getResult);
        ss.set("randomAsset", FunctionsLib::randomAsset);
        ss.set("isValidId", FunctionsLib::isValidId);
        ss.set("isValidAsset", FunctionsLib::isValidAsset);
        ss.set("isCustomAsset", FunctionsLib::isCustomAsset);
        ss.set("getAsset", FunctionsLib::getAsset);
        ss.set("getAssetKey", FunctionsLib::getAssetKey);
        ss.set("getAssetId", FunctionsLib::getAssetId);
        ss.set("getAssetType", FunctionsLib::getAssetType);
        ss.set("getAssetUrl", FunctionsLib::getAssetUrl);

        // StringUtils
        ss.set("getOrDefault", FunctionsLib::getOrDefault);
        ss.set("replace", FunctionsLib::replace);
        ss.set("length", FunctionsLib::length);
        ss.set("split", FunctionsLib::split);
        ss.set("getArrayElement", FunctionsLib::getArrayElement);
        ss.set("minify", FunctionsLib::minify);
        ss.set("nullOrEmpty", FunctionsLib::nullOrEmpty);
        ss.set("formatAddress", FunctionsLib::formatAddress);
        ss.set("hasWhitespace", FunctionsLib::hasWhitespace);
        ss.set("hasAlphaNumeric", FunctionsLib::hasAlphaNumeric);
        ss.set("isUuid", FunctionsLib::isUuid);
        ss.set("isColor", FunctionsLib::isColor);
        ss.set("toCamelCase", FunctionsLib::toCamelCase);
        ss.set("asIcon", FunctionsLib::asIcon);
        ss.set("asProperWord", FunctionsLib::asProperWord);
        ss.set("removeRepeatWords", FunctionsLib::removeRepeatWords);
        ss.set("asIdentifier", FunctionsLib::asIdentifier);
        ss.set("capitalizeWords", FunctionsLib::capitalizeWords);
        ss.set("getField", FunctionsLib::getField);
        ss.set("getClass", FunctionsLib::getClass);
        ss.set("hasField", FunctionsLib::hasField);
        ss.set("executeMethod", FunctionsLib::executeMethod);
        ss.set("stripColors", FunctionsLib::stripColors);

        // TimeUtils
        ss.set("getCurrentTime", FunctionsLib::getCurrentTime);
        ss.set("timeToEpoch", FunctionsLib::timeToEpoch); // toEpoch
        ss.set("timeFromEpoch", FunctionsLib::timeFromEpoch); // fromEpoch
        ss.set("dateToEpoch", FunctionsLib::dateToEpoch); // stringToEpoch
        ss.set("epochToDate", FunctionsLib::epochToDate); // epochToString
        ss.set("convertTimeZone", FunctionsLib::convertTimeZone); // convertZone
        ss.set("convertTimeFormat", FunctionsLib::convertTimeFormat); // convertFormat
        ss.set("convertTime", FunctionsLib::convertTime);
        ss.set("timeFromString", FunctionsLib::timeFromString); // toInstance
        ss.set("timeToString", FunctionsLib::timeToString); // toString
    }

    public static Value parseWith(TranslationUtils instance, Starscript ss, int argCount) {
        final List<Value> args = StringUtils.newArrayList();
        if (argCount < 1)
            ss.error("parseWith() requires one or more arguments, got %d.", argCount);
        for (int i = 0; i < argCount; i++) {
            args.add(ss.pop());
        }
        StringUtils.revlist(args);

        // Parse, then remove the source entity from arguments, if valid
        Value source = args.get(0);
        String data = null;
        if (source.isString()) {
            data = source.getString();
        }

        if (data == null) {
            ss.error("First argument to parseWith() needs to be a valid String.");
        }
        args.remove(0);

        String result;
        // Parse remaining data into an Objects List
        if (!args.isEmpty() && data != null) {
            final List<Object> value = StringUtils.newArrayList();
            for (Value info : args) {
                value.add(CraftPresence.CLIENT.fromValue(info));
            }
            Object[] formatArgs = value.toArray(new Object[0]);
            result = instance != null ? instance.translate(data, formatArgs) : String.format(data, formatArgs);
        } else {
            result = instance != null ? instance.translate(data) : data;
        }
        return !StringUtils.isNullOrEmpty(result) ? Value.string(result) : Value.null_();
    }

    public static Value format(Starscript ss, int argCount) {
        return parseWith(null, ss, argCount);
    }

    public static Value translate(Starscript ss, int argCount) {
        if (ModUtils.TRANSLATOR == null) ss.error("No available translations from mod data, try again later.");
        return parseWith(ModUtils.TRANSLATOR, ss, argCount);
    }

    public static Value mcTranslate(Starscript ss, int argCount) {
        if (ModUtils.RAW_TRANSLATOR == null) ss.error("No available translations from game data, try again later.");
        return parseWith(ModUtils.RAW_TRANSLATOR, ss, argCount);
    }

    public static Value getJsonElement(Starscript ss, int argCount) {
        final List<String> path = StringUtils.newArrayList();
        String source, json = "";
        JsonObject contents;
        JsonElement result;
        // Argument Collection
        if (argCount < 1)
            ss.error("getJsonElement() requires one or more arguments, got %d.", argCount);
        for (int i = 0; i < argCount; i++) {
            if (i == argCount - 1) {
                source = ss.pop().toString();
                if (source.toLowerCase().startsWith("http")) {
                    try {
                        json = UrlUtils.getURLText(source, "UTF-8");
                    } catch (Exception ex) {
                        ss.error("Unable to parse URL for getJsonElement(), try again.");
                    }
                } else {
                    json = source;
                }
            } else {
                path.add(ss.pop().toString());
            }
        }
        result = contents = FileUtils.getJsonData(json).getAsJsonObject();
        StringUtils.revlist(path);

        boolean needsIndex = false;
        for (String part : path) {
            JsonElement element;
            if (needsIndex) {
                result = element = result.getAsJsonArray().get(
                        StringUtils.getValidInteger(part).getSecond()
                );
            } else {
                result = element = contents.get(part);
            }

            if (element.isJsonObject()) {
                contents = element.getAsJsonObject();
            } else if (element.isJsonNull()) {
                return Value.null_();
            } else if (element.isJsonArray()) {
                needsIndex = true;
            } else if (element.isJsonPrimitive()) {
                final JsonPrimitive inner = element.getAsJsonPrimitive();
                if (inner.isBoolean()) {
                    return Value.bool(inner.getAsBoolean());
                } else if (inner.isNumber()) {
                    return Value.number(inner.getAsDouble());
                } else if (inner.isString()) {
                    return Value.string(inner.getAsString());
                } else {
                    return Value.object(inner);
                }
            }
        }
        return result != null ? Value.object(result) : Value.null_();
    }

    public static Value getResult(Starscript ss, int argCount) {
        if (argCount != 1)
            ss.error("getResult() can only be used with one argument, got %d.", argCount);
        return Value.string(CraftPresence.CLIENT.getResult(ss.pop().toString()));
    }

    public static Value isValidId(Starscript ss, int argCount) {
        if (argCount != 1)
            ss.error("isValidId() can only be used with one argument, got %d.", argCount);
        return Value.bool(DiscordAssetUtils.isValidId(ss.pop().toString()));
    }

    public static Value isValidAsset(Starscript ss, int argCount) {
        if (argCount != 1)
            ss.error("isValidAsset() can only be used with one argument, got %d.", argCount);
        return Value.bool(DiscordAssetUtils.contains(ss.pop().toString()));
    }

    public static Value isCustomAsset(Starscript ss, int argCount) {
        if (argCount != 1)
            ss.error("isCustomAsset() can only be used with one argument, got %d.", argCount);
        return Value.bool(DiscordAssetUtils.isCustom(ss.pop().toString()));
    }

    public static Value getAsset(Starscript ss, int argCount) {
        if (argCount != 1)
            ss.error("getAsset() can only be used with one argument, got %d.", argCount);
        return Value.object(DiscordAssetUtils.get(ss.pop().toString()));
    }

    public static Value getAssetKey(Starscript ss, int argCount) {
        if (argCount != 1)
            ss.error("getAssetKey() can only be used with one argument, got %d.", argCount);
        return Value.string(DiscordAssetUtils.getKey(ss.pop().toString()));
    }

    public static Value getAssetId(Starscript ss, int argCount) {
        if (argCount != 1)
            ss.error("getAssetId() can only be used with one argument, got %d.", argCount);
        return Value.string(DiscordAssetUtils.getId(ss.pop().toString()));
    }

    public static Value getAssetType(Starscript ss, int argCount) {
        if (argCount != 1)
            ss.error("getAssetType() can only be used with one argument, got %d.", argCount);
        return Value.object(DiscordAssetUtils.getType(ss.pop().toString()));
    }

    public static Value getAssetUrl(Starscript ss, int argCount) {
        if (argCount != 1)
            ss.error("getAssetUrl() can only be used with one argument, got %d.", argCount);
        return Value.string(DiscordAssetUtils.getUrl(ss.pop().toString()));
    }

    public static Value getFirst(Starscript ss, int argCount) {
        final List<String> args = StringUtils.newArrayList();
        if (argCount < 1)
            ss.error("getFirst() requires one or more arguments, got %d.", argCount);
        for (int i = 0; i < argCount; i++) {
            args.add(ss.pop().toString());
        }
        StringUtils.revlist(args);

        for (String arg : args) {
            if (!StringUtils.isNullOrEmpty(arg)) {
                return Value.string(arg);
            }
        }
        return Value.null_();
    }

    public static Value getNbt(Starscript ss, int argCount) {
        final List<Value> args = StringUtils.newArrayList();
        if (argCount < 1)
            ss.error("getNbt() requires one or more arguments, got %d.", argCount);
        for (int i = 0; i < argCount; i++) {
            args.add(ss.pop());
        }
        StringUtils.revlist(args);

        // Parse, then remove the source entity from arguments, if valid
        Value source = args.get(0);
        Object data = null;
        if (source.isObject()) {
            data = source.getObject();
        }

        if (data == null) {
            ss.error("First argument to getNbt() needs to be a valid Entity or ItemStack object.");
        }
        args.remove(0);

        final List<String> path = StringUtils.newArrayList();
        for (Value info : args) {
            path.add(info.toString());
        }
        final Object result = NbtUtils.parseTag(
                NbtUtils.getNbt(data, path.toArray(new String[0]))
        );
        return result != null ? Value.object(result) : Value.null_();
    }

    public static Value getNamespace(Starscript ss, int argCount) {
        if (argCount != 1)
            ss.error("getNamespace() can only be used with one argument, got %d.", argCount);
        return Value.string(ss.pop().toString().split(":")[0]);
    }

    public static Value getPath(Starscript ss, int argCount) {
        if (argCount != 1)
            ss.error("getPath() can only be used with one argument, got %d.", argCount);
        return Value.string(ss.pop().toString().split(":")[1]);
    }

    public static Value isWithinValue(Starscript ss, int argCount) {
        final List<Value> args = StringUtils.newArrayList();
        if (argCount < 3 || argCount > 6)
            ss.error("isWithinValue() can only be used with 3-6 arguments, got %d.", argCount);
        for (int i = 0; i < argCount; i++) {
            args.add(ss.pop());
        }
        StringUtils.revlist(args);

        Value currentArg;

        currentArg = args.get(0);
        args.remove(0);

        double value = 0;
        if (currentArg.isNumber()) {
            value = currentArg.getNumber();
        } else {
            ss.error("First argument to isWithinValue() needs to be a number.");
        }

        currentArg = args.get(0);
        args.remove(0);

        double min = value;
        if (currentArg.isNumber()) {
            min = currentArg.getNumber();
        } else {
            ss.error("Second argument to isWithinValue() needs to be a number.");
        }

        currentArg = args.get(0);
        args.remove(0);

        double max = min;
        if (currentArg.isNumber()) {
            max = currentArg.getNumber();
        } else {
            ss.error("Third argument to isWithinValue() needs to be a number.");
        }

        // Optional arguments
        boolean contains_min = false;
        boolean contains_max = false;
        boolean check_sanity = true;

        if (argCount >= 5) {
            currentArg = args.get(0);
            args.remove(0);

            if (currentArg.isBool()) {
                contains_min = currentArg.getBool();
            } else {
                ss.error("Fourth argument to isWithinValue() needs to be a boolean.");
            }

            currentArg = args.get(0);
            args.remove(0);

            if (currentArg.isBool()) {
                contains_max = currentArg.getBool();
            } else {
                ss.error("Fifth argument to isWithinValue() needs to be a boolean.");
            }

            if (argCount == 6) {
                currentArg = args.get(0);
                args.remove(0);

                if (currentArg.isBool()) {
                    check_sanity = currentArg.getBool();
                } else {
                    ss.error("Sixth argument to isWithinValue() needs to be a boolean.");
                }
            }
        }
        return Value.bool(MathUtils.isWithinValue(value, min, max, contains_min, contains_max, check_sanity));
    }

    public static Value randomAsset(Starscript ss, int argCount) {
        return Value.string(DiscordAssetUtils.getRandomAssetName());
    }

    public static Value randomString(Starscript ss, int argCount) {
        final List<String> args = StringUtils.newArrayList();
        if (argCount < 1)
            ss.error("randomString() requires one or more arguments, got %d.", argCount);
        for (int i = 0; i < argCount; i++) {
            args.add(ss.pop().toString());
        }
        StringUtils.revlist(args);
        return Value.string(args.get(SystemUtils.RANDOM.nextInt(argCount)));
    }

    public static Value getOrDefault(Starscript ss, int argCount) {
        if (argCount < 1 || argCount > 2)
            ss.error("getOrDefault() can only be used with 1-2 arguments, got %d.", argCount);
        String alternative = "";
        if (argCount == 2) {
            alternative = ss.pop().toString();
        }
        String target = ss.pop().toString();
        return Value.string(StringUtils.getOrDefault(target, alternative));
    }

    public static Value replace(Starscript ss, int argCount) {
        final List<Value> args = StringUtils.newArrayList();
        if (argCount < 3)
            ss.error("replace() requires at least 3 arguments, got %d.", argCount);
        for (int i = 0; i < argCount; i++) {
            args.add(ss.pop());
        }
        StringUtils.revlist(args);

        Value currentArg;
        String source = "";

        // Parse, then remove the source string from arguments
        currentArg = args.get(0);
        if (currentArg.isString()) {
            source = currentArg.getString();
        } else {
            ss.error("First argument to replace() needs to be a string.");
        }
        args.remove(0);

        boolean matchCase = false;
        boolean matchWholeWord = false;
        boolean useRegex = true;

        // Parse optional arguments (All-or-none style)
        currentArg = args.get(0);
        if (currentArg.isBool()) {
            matchCase = currentArg.getBool();
            args.remove(0);

            currentArg = args.get(0);
            if (currentArg.isBool()) {
                matchWholeWord = currentArg.getBool();
                args.remove(0);

                currentArg = args.get(0);
                if (currentArg.isBool()) {
                    useRegex = currentArg.getBool();
                    args.remove(0);
                }
            }
        }

        final Map<String, String> data = StringUtils.newHashMap();
        String tempKey = null;
        if (!args.isEmpty()) {
            for (Value info : args) {
                if (!info.isString())
                    ss.error("Incorrect type data supplied for replace(), please check input and documentation.");

                final String param = info.getString();
                if (StringUtils.isNullOrEmpty(tempKey)) {
                    tempKey = param;
                } else {
                    data.put(tempKey, param);
                    tempKey = null;
                }
            }
        }
        if (!StringUtils.isNullOrEmpty(tempKey)) {
            ss.error("Incomplete data supplied for replace(), please check input and documentation.");
        }
        return Value.string(StringUtils.sequentialReplace(source, matchCase, matchWholeWord, useRegex, data));
    }

    public static Value length(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("length() requires 1 argument, got %d.", argCount);
        String source = ss.popString("First argument to length() needs to be a string.");
        return Value.number(source.length());
    }

    public static Value split(Starscript ss, int argCount) {
        if (argCount < 2 || argCount > 3)
            ss.error("split() can only be used with 2-3 arguments, got %d.", argCount);
        int limit = 0;
        if (argCount == 3) {
            limit = (int) ss.popNumber("Third argument to split() needs to be a number.");
        }
        String regex = ss.popString("Second argument to split() needs to be a string.");
        String source = ss.popString("First argument to split() needs to be a string.");
        return Value.object(source.split(regex, limit));
    }

    public static Value getArrayElement(Starscript ss, int argCount) {
        if (argCount != 2) ss.error("getArrayElement() requires 2 arguments, %d.", argCount);
        int element = (int) ss.popNumber("Second argument to getArrayElement() needs to be a number.");
        Object data = ss.popObject("First argument to getArrayElement() needs to be an object.");
        Object[] array = StringUtils.getDynamicArray(data);
        if (array != null) {
            return CraftPresence.CLIENT.toValue(array[element], true);
        } else {
            ss.error("Invalid array information supplied for getArrayElement(), please check input and documentation.");
        }
        return Value.null_();
    }

    public static Value minify(Starscript ss, int argCount) {
        if (argCount != 2) ss.error("minify() requires 2 arguments, got %d.", argCount);
        int length = (int) ss.popNumber("Second argument to minify() needs to be a number.");
        String source = ss.popString("First argument to minify() needs to be a string.");
        return Value.string(StringUtils.minifyString(source, length));
    }

    public static Value nullOrEmpty(Starscript ss, int argCount) {
        if (argCount < 1 || argCount > 2)
            ss.error("nullOrEmpty() can only be used with 1-2 arguments, got %d.", argCount);
        boolean allowWhitespace = false;
        if (argCount == 2) {
            allowWhitespace = ss.popBool("Second argument to nullOrEmpty() needs to be a boolean.");
        }
        String target = ss.popString("First argument to nullOrEmpty() needs to be a string.");
        return Value.bool(StringUtils.isNullOrEmpty(target, allowWhitespace));
    }

    public static Value formatAddress(Starscript ss, int argCount) {
        if (argCount < 1 || argCount > 2)
            ss.error("formatAddress() can only be used with 1-2 arguments, got %d.", argCount);
        boolean returnPort = false;
        if (argCount == 2) {
            returnPort = ss.popBool("Second argument to formatAddress() needs to be a boolean.");
        }
        String target = ss.popString("First argument to formatAddress() needs to be a string.");
        return Value.string(StringUtils.formatAddress(target, returnPort));
    }

    public static Value hasWhitespace(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("hasWhitespace() requires 1 argument, got %d.", argCount);
        String source = ss.popString("First argument to hasWhitespace() needs to be a string.");
        return Value.bool(StringUtils.containsWhitespace(source));
    }

    public static Value hasAlphaNumeric(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("hasAlphaNumeric() requires 1 argument, got %d.", argCount);
        String source = ss.popString("First argument to hasAlphaNumeric() needs to be a string.");
        return Value.bool(StringUtils.containsAlphaNumeric(source));
    }

    public static Value isUuid(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("isUuid() requires 1 argument, got %d.", argCount);
        String source = ss.popString("First argument to isUuid() needs to be a string.");
        return Value.bool(StringUtils.isValidUuid(source));
    }

    public static Value isColor(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("isColor() requires 1 argument, got %d.", argCount);
        String source = ss.popString("First argument to isColor() needs to be a string.");
        return Value.bool(StringUtils.isValidColorCode(source));
    }

    public static Value toCamelCase(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("toCamelCase() requires 1 argument, got %d.", argCount);
        String source = ss.popString("First argument to toCamelCase() needs to be a string.");
        return Value.string(StringUtils.formatToCamel(source));
    }

    public static Value asIcon(Starscript ss, int argCount) {
        if (argCount < 1 || argCount > 2)
            ss.error("asIcon() can only be used with 1-2 arguments, got %d.", argCount);
        String whitespaceIndex = "";
        if (argCount >= 2) {
            whitespaceIndex = ss.popString("Second argument to asIcon() needs to be a string.");
        }
        String source = ss.popString("First argument to asIcon() needs to be a string.");
        return Value.string(StringUtils.formatAsIcon(source, whitespaceIndex));
    }

    public static Value asProperWord(Starscript ss, int argCount) {
        if (argCount < 1 || argCount > 4)
            ss.error("asProperWord() can only be used with 1-4 arguments, got %d.", argCount);
        int caseCheckTimes = -1;
        if (argCount == 4) {
            caseCheckTimes = (int) ss.popNumber("Fourth argument to asProperWord() needs to be a number.");
        }
        boolean skipSymbolReplacement = false;
        if (argCount >= 3) {
            skipSymbolReplacement = ss.popBool("Third argument to asProperWord() needs to be a boolean.");
        }
        boolean avoid = false;
        if (argCount >= 2) {
            avoid = ss.popBool("Second argument to asProperWord() needs to be a boolean.");
        }
        String target = ss.popString("First argument to asProperWord() needs to be a string.");
        return Value.string(StringUtils.formatWord(target, avoid, skipSymbolReplacement, caseCheckTimes));
    }

    public static Value removeRepeatWords(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("removeRepeatWords() requires 1 argument, got %d.", argCount);
        String source = ss.popString("First argument to removeRepeatWords() needs to be a string.");
        return Value.string(StringUtils.removeRepeatWords(source));
    }

    public static Value asIdentifier(Starscript ss, int argCount) {
        if (argCount < 1 || argCount > 3)
            ss.error("asIdentifier() can only be used with 1-3 arguments, got %d.", argCount);
        boolean avoid = false;
        if (argCount == 3) {
            avoid = ss.popBool("Third argument to asIdentifier() needs to be a boolean.");
        }
        boolean formatToId = false;
        if (argCount >= 2) {
            formatToId = ss.popBool("Second argument to asIdentifier() needs to be a boolean.");
        }
        String target = ss.popString("First argument to asIdentifier() needs to be a string.");
        return Value.string(StringUtils.formatIdentifier(target, formatToId, avoid));
    }

    public static Value capitalizeWords(Starscript ss, int argCount) {
        if (argCount < 1 || argCount > 2)
            ss.error("capitalizeWords() can only be used with 1-2 arguments, got %d.", argCount);
        int timesToCheck = -1;
        if (argCount == 2) {
            timesToCheck = (int) ss.popNumber("Second argument to capitalizeWords() needs to be a number.");
        }
        String target = ss.popString("First argument to capitalizeWords() needs to be a string.");
        return Value.string(StringUtils.capitalizeWord(target, timesToCheck));
    }

    public static Value getFields(Starscript ss, int argCount) {
        if (argCount < 1) ss.error("getFields() can only be used with one argument, got %d.", argCount);
        final Value data = getClass(ss, argCount);
        if (!data.isNull()) {
            return Value.string(StringUtils.getFieldList((Class<?>) data.getObject()));
        } else {
            ss.error("First argument to getFields() needs to be a valid class-compatible object.");
        }
        return Value.null_();
    }

    public static Value getMethods(Starscript ss, int argCount) {
        if (argCount < 1) ss.error("getMethods() can only be used with one argument, got %d.", argCount);
        final Value data = getClass(ss, argCount);
        if (!data.isNull()) {
            return Value.string(StringUtils.getMethodList((Class<?>) data.getObject()));
        } else {
            ss.error("First argument to getMethods() needs to be a valid class-compatible object.");
        }
        return Value.null_();
    }

    public static Value getField(Starscript ss, int argCount) {
        if (argCount < 2 || argCount > 3) ss.error("getField() can only be used with 2-3 arguments, got %d.", argCount);
        String fieldName;
        Object instance, result = null;
        if (argCount == 2) {
            fieldName = ss.popString("Second argument to getField(instance, fieldName) needs to be a string.");
            instance = CraftPresence.CLIENT.fromValue(ss.pop());
            result = StringUtils.getField(instance.getClass(), instance, fieldName);
        } else {
            fieldName = ss.popString("Third argument to getField(classObj, instance, fieldName) needs to be a string.");
            instance = CraftPresence.CLIENT.fromValue(ss.pop());

            Value classObject = ss.pop();
            if (classObject.isObject()) {
                result = StringUtils.getField(classObject.getObject(), instance, fieldName);
            } else if (classObject.isString()) {
                result = StringUtils.getField(classObject.getString(), instance, fieldName);
            } else {
                ss.error("First argument to getField() needs to be a string, object, or class.");
            }
        }

        return result != null ? Value.object(result) : Value.null_();
    }

    public static Value getClass(Starscript ss, int argCount) {
        if (argCount < 1) ss.error("getClass() can only be used with one argument, got %d.", argCount);
        Value value = ss.pop();
        Class<?> result = null;
        if (value.isObject()) {
            result = value.getObject().getClass();
        } else if (value.isString()) {
            result = FileUtils.findValidClass(value.getString());
        } else {
            ss.error("First argument to getClass() needs to be a valid class-compatible object.");
        }
        return result != null ? Value.object(result) : Value.null_();
    }

    public static Value hasField(Starscript ss, int argCount) {
        if (argCount != 2) ss.error("hasField() requires 2 arguments, got %d.", argCount);
        String b = ss.popString("Second argument to hasField() needs to be a string.");
        final Value data = getClass(ss, argCount);
        if (!data.isNull()) {
            final Class<?> result = (Class<?>) data.getObject();
            return Value.bool(StringUtils.getValidField(result, b).getFirst());
        } else {
            ss.error("First argument to hasField() needs to be a valid class-compatible object.");
        }
        return Value.null_();
    }

    public static Value executeMethod(Starscript ss, int argCount) {
        final List<Value> args = StringUtils.newArrayList();
        if (argCount < 2)
            ss.error("executeMethod() requires two or more arguments, got %d.", argCount);
        for (int i = 0; i < argCount; i++) {
            args.add(ss.pop());
        }
        StringUtils.revlist(args);
        Value target = args.get(0);

        Class<?> classToAccess = null;
        Object instance = null;
        String methodName = null;

        // Argument 1: classToAccess
        if (target.isObject()) {
            Object temp = target.getObject();
            if (temp instanceof Class<?>) {
                classToAccess = (Class<?>) temp;
            } else {
                classToAccess = temp.getClass();
                instance = temp;
            }
        } else if (target.isString()) {
            classToAccess = FileUtils.findValidClass(target.getString());
        } else {
            ss.error("First argument to executeMethod(), classToAccess, needs to be either a string, object, or class.");
        }
        args.remove(0); // Remove the classToAccess from parsing

        target = args.get(0); // This will either be the instance or methodName
        boolean isInstanceNull = (instance == null);
        if (isInstanceNull) {
            // 2nd argument as instance (Object)
            if (target.isObject()) {
                instance = target.getObject();
                args.remove(0); // Remove the instance from parsing
                isInstanceNull = false;
            }
        }

        // Either the 2nd or 3rd param will be methodName, depending on args
        if (!args.isEmpty()) {
            target = args.get(0); // Refresh arg table
            if (target.isString()) {
                methodName = target.getString();
            } else {
                ss.error((isInstanceNull ? "Second" : "Third") + " argument to executeMethod(), methodName, needs to be a string.");
            }
            args.remove(0); // Remove the methodName from parsing
        }

        // If the className or methodName is null, error as such
        // instanceName, and the parameterTypes and parameters tables can be null
        if (classToAccess == null || methodName == null) {
            ss.error("Insufficient or null arguments provided for required executeMethod() params, please try again.");
        }

        // For remaining args, you specify the parameter type, then the parameter itself
        List<Class<?>> parameterTypes = null;
        List<Object> parameters = null;

        if (!args.isEmpty()) {
            parameterTypes = StringUtils.newArrayList();
            parameters = StringUtils.newArrayList();

            boolean classMode = true;
            for (Value data : args) {
                if (classMode) {
                    Class<?> classObj = null;
                    if (data.isObject()) {
                        Object temp = data.getObject();
                        if (temp instanceof Class<?>) {
                            classObj = (Class<?>) temp;
                        }
                    } else if (data.isString()) {
                        classObj = FileUtils.findValidClass(data.getString());
                    }

                    if (classObj == null) {
                        ss.error("Class argument for executeMethod() parameterTypes, must be a class object or string.");
                    } else {
                        parameterTypes.add(classObj);
                    }
                } else {
                    parameters.add(CraftPresence.CLIENT.fromValue(data));
                }
                classMode = !classMode;
            }
        }

        return Value.object(StringUtils.executeMethod(classToAccess, instance, methodName,
                parameterTypes != null ? parameterTypes.toArray(new Class<?>[0]) : null,
                parameters != null ? parameters.toArray(new Object[0]) : null
        ));
    }

    public static Value stripColors(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("stripColors() requires 1 argument, got %d.", argCount);
        String a = ss.popString("Argument to stripColors() needs to be a string.");
        return Value.string(StringUtils.stripColors(a));
    }

    public static Value getCurrentTime(Starscript ss, int argCount) {
        return Value.object(TimeUtils.getCurrentTime());
    }

    public static Value timeToEpoch(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("timeToEpoch() requires 1 argument, got %d.", argCount);
        Object a = ss.popObject("Argument to timeToEpoch() needs to be an object.");
        if (a instanceof Instant) {
            return Value.number(TimeUtils.toEpoch((Instant) a));
        } else {
            ss.error("Argument to timeToEpoch() needs to be a valid Instant Object.");
        }
        return Value.null_();
    }

    public static Value timeFromEpoch(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("timeFromEpoch() requires 1 argument, got %d.", argCount);
        double a = ss.popNumber("Argument to timeFromEpoch() needs to be a number.");
        return Value.object(TimeUtils.fromEpoch((long) a));
    }

    public static Value dateToEpoch(Starscript ss, int argCount) {
        if (argCount < 2 || argCount > 3)
            ss.error("dateToEpoch() can only be used with 2-3 arguments, got %d.", argCount);
        String timeZone = null;
        if (argCount == 3) {
            timeZone = ss.popString("Third argument to dateToEpoch() needs to be a string.");
        }
        String format = ss.popString("Second argument to dateToEpoch() needs to be a string.");
        String dateString = ss.popString("First argument to dateToEpoch() needs to be a string.");
        return Value.number(TimeUtils.stringToEpoch(dateString, format, timeZone));
    }

    public static Value epochToDate(Starscript ss, int argCount) {
        if (argCount < 2 || argCount > 3)
            ss.error("epochToDate() can only be used with 2-3 arguments, got %d.", argCount);
        String timeZone = null;
        if (argCount == 3) {
            timeZone = ss.popString("Third argument to epochToDate() needs to be a string.");
        }
        String format = ss.popString("Second argument to epochToDate() needs to be a string.");
        double dateString = ss.popNumber("First argument to epochToDate() needs to be a number.");
        return Value.string(TimeUtils.epochToString((long) dateString, format, timeZone));
    }

    public static Value convertTimeZone(Starscript ss, int argCount) {
        if (argCount != 4) ss.error("convertTimeZone() can only be used with 4 arguments, got %d.", argCount);
        String toTimeZone = ss.popString("Fourth argument to convertTimeZone() needs to be a string.");
        String fromTimeZone = ss.popString("Third argument to convertTimeZone() needs to be a string.");
        String fromFormat = ss.popString("Second argument to convertTimeZone() needs to be a string.");
        String dateString = ss.popString("First argument to convertTimeZone() needs to be a string.");
        return Value.string(TimeUtils.convertZone(dateString, fromFormat, fromTimeZone, toTimeZone));
    }

    public static Value convertTimeFormat(Starscript ss, int argCount) {
        if (argCount != 3) ss.error("convertTimeFormat() can only be used with 3 arguments, got %d.", argCount);
        String toFormat = ss.popString("Third argument to convertTimeFormat() needs to be a string.");
        String fromFormat = ss.popString("Second argument to convertTimeFormat() needs to be a string.");
        String dateString = ss.popString("First argument to convertTimeFormat() needs to be a string.");
        return Value.string(TimeUtils.convertFormat(dateString, fromFormat, toFormat));
    }

    public static Value convertTime(Starscript ss, int argCount) {
        if (argCount != 5) ss.error("convertTime() can only be used with 5 arguments, got %d.", argCount);
        String toTimeZone = ss.popString("Fifth argument to convertTime() needs to be a string.");
        String toFormat = ss.popString("Fourth argument to convertTime() needs to be a string.");
        String fromTimeZone = ss.popString("Third argument to convertTime() needs to be a string.");
        String fromFormat = ss.popString("Second argument to convertTime() needs to be a string.");
        String dateString = ss.popString("First argument to convertTime() needs to be a string.");
        return Value.string(TimeUtils.convertTime(dateString, fromFormat, fromTimeZone, toFormat, toTimeZone));
    }

    public static Value timeFromString(Starscript ss, int argCount) {
        if (argCount < 2 || argCount > 3)
            ss.error("timeFromString() can only be used with 2-3 arguments, got %d.", argCount);
        String fromTimeZone = null;
        if (argCount == 3) {
            fromTimeZone = ss.popString("Third argument to timeFromString() needs to be a string.");
        }
        String fromFormat = ss.popString("Second argument to timeFromString() needs to be a string.");
        String dateString = ss.popString("First argument to timeFromString() needs to be a string.");
        return Value.object(TimeUtils.toInstance(dateString, fromFormat, fromTimeZone));
    }

    public static Value timeToString(Starscript ss, int argCount) {
        if (argCount < 2 || argCount > 3)
            ss.error("timeToString() can only be used with 2-3 arguments, got %d.", argCount);
        String toTimeZone = null;
        if (argCount == 3) {
            toTimeZone = ss.popString("Third argument to timeToString() needs to be a string.");
        }
        String toFormat = ss.popString("Second argument to timeToString() needs to be a string.");
        Object date = ss.popObject("First argument to timeToString() needs to be an object.");
        if (date instanceof Instant) {
            return Value.string(TimeUtils.toString((Instant) date, toFormat, toTimeZone));
        } else {
            ss.error("First Argument to timeToString() needs to be a valid Instant Object.");
        }
        return Value.null_();
    }
}
