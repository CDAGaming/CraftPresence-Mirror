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

package com.gitlab.cdagaming.craftpresence.core.integrations.discord;

import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.integrations.discord.assets.DiscordAssetUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.cdagaming.unicore.utils.*;
import org.meteordev.starscript.StandardLib;
import org.meteordev.starscript.Starscript;
import org.meteordev.starscript.value.Value;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Standard library with some default functions and variables.
 *
 * @author CDAGaming
 */
@SuppressWarnings("DuplicatedCode")
public class FunctionsLib {
    public static void init(DiscordUtils client) {
        StandardLib.init(client.scriptEngine);

        // General Functions
        client.syncFunction("format", (ss, argCount) -> FunctionsLib.format(client, ss, argCount));
        client.syncFunction("translate", (ss, argCount) -> FunctionsLib.translate(client, ss, argCount));
        client.syncFunction("getFields", FunctionsLib::getFields);
        client.syncFunction("getMethods", FunctionsLib::getMethods);
        client.syncFunction("getJsonElement", FunctionsLib::getJsonElement);
        client.syncFunction("randomString", FunctionsLib::randomString);
        client.syncFunction("getFirst", FunctionsLib::getFirst);
        client.syncFunction("getNamespace", FunctionsLib::getNamespace);
        client.syncFunction("getPath", FunctionsLib::getPath);

        // MathUtils
        client.syncFunction("isWithinValue", FunctionsLib::isWithinValue);
        client.syncFunction("roundDouble", FunctionsLib::roundDouble);
        client.syncFunction("clampInt", FunctionsLib::clampInt);
        client.syncFunction("clampLong", FunctionsLib::clampLong);
        client.syncFunction("clampFloat", FunctionsLib::clampFloat);
        client.syncFunction("clampDouble", FunctionsLib::clampDouble);
        client.syncFunction("lerpFloat", FunctionsLib::lerpFloat);
        client.syncFunction("lerpDouble", FunctionsLib::lerpDouble);
        client.syncFunction("snapToStep", FunctionsLib::snapToStep);

        // DiscordUtils
        client.syncFunction("getResult", (ss, argCount) -> FunctionsLib.getResult(client, ss, argCount));
        client.syncFunction("randomAsset", FunctionsLib::randomAsset);
        client.syncFunction("isValidId", FunctionsLib::isValidId);
        client.syncFunction("isValidAsset", FunctionsLib::isValidAsset);
        client.syncFunction("isCustomAsset", FunctionsLib::isCustomAsset);
        client.syncFunction("getAsset", FunctionsLib::getAsset);
        client.syncFunction("getAssetKey", FunctionsLib::getAssetKey);
        client.syncFunction("getAssetId", FunctionsLib::getAssetId);
        client.syncFunction("getAssetType", FunctionsLib::getAssetType);
        client.syncFunction("getAssetUrl", (ss, argCount) -> FunctionsLib.getAssetUrl(client, ss, argCount));

        // StringUtils
        client.syncFunction("getOrDefault", FunctionsLib::getOrDefault);
        client.syncFunction("length", FunctionsLib::length);
        client.syncFunction("split", FunctionsLib::split);
        client.syncFunction("getArrayElement", (ss, argCount) -> FunctionsLib.getArrayElement(client, ss, argCount));
        client.syncFunction("minify", FunctionsLib::minify);
        client.syncFunction("nullOrEmpty", FunctionsLib::nullOrEmpty);
        client.syncFunction("cast", (ss, argCount) -> FunctionsLib.cast(client, ss, argCount));
        client.syncFunction("formatAddress", FunctionsLib::formatAddress);
        client.syncFunction("isUuid", FunctionsLib::isUuid);
        client.syncFunction("isColor", FunctionsLib::isColor);
        client.syncFunction("toCamelCase", FunctionsLib::toCamelCase);
        client.syncFunction("asIcon", FunctionsLib::asIcon);
        client.syncFunction("asProperWord", FunctionsLib::asProperWord);
        client.syncFunction("removeRepeatWords", FunctionsLib::removeRepeatWords);
        client.syncFunction("asIdentifier", FunctionsLib::asIdentifier);
        client.syncFunction("capitalizeWords", FunctionsLib::capitalizeWords);
        client.syncFunction("getField", (ss, argCount) -> FunctionsLib.getField(client, ss, argCount));
        client.syncFunction("getClass", FunctionsLib::getClass);
        client.syncFunction("hasField", FunctionsLib::hasField);
        client.syncFunction("executeMethod", (ss, argCount) -> FunctionsLib.executeMethod(client, ss, argCount));
        client.syncFunction("stripColors", FunctionsLib::stripColors);
        client.syncFunction("stripFormatting", FunctionsLib::stripFormatting);
        client.syncFunction("stripAllFormatting", FunctionsLib::stripAllFormatting);

        // TimeUtils
        client.syncFunction("getCurrentTime", FunctionsLib::getCurrentTime);
        client.syncFunction("getElapsedNanos", FunctionsLib::getElapsedNanos);
        client.syncFunction("getElapsedMillis", FunctionsLib::getElapsedMillis);
        client.syncFunction("getElapsedSeconds", FunctionsLib::getElapsedSeconds);
        client.syncFunction("timeToEpochSecond", FunctionsLib::timeToEpochSecond); // toEpochSecond
        client.syncFunction("timeToEpochMilli", FunctionsLib::timeToEpochMilli); // toEpochMilli
        client.syncFunction("timeFromEpochSecond", FunctionsLib::timeFromEpochSecond); // fromEpochSecond
        client.syncFunction("timeFromEpochMilli", FunctionsLib::timeFromEpochMilli); // fromEpochMilli
        client.syncFunction("dateToEpochSecond", FunctionsLib::dateToEpochSecond); // stringToEpochSecond
        client.syncFunction("dateToEpochMilli", FunctionsLib::dateToEpochMilli); // stringToEpochMilli
        client.syncFunction("epochSecondToDate", FunctionsLib::epochSecondToDate); // epochSecondToString
        client.syncFunction("epochMilliToDate", FunctionsLib::epochMilliToDate); // epochMilliToString
        client.syncFunction("convertTimeZone", FunctionsLib::convertTimeZone); // convertZone
        client.syncFunction("convertTimeFormat", FunctionsLib::convertTimeFormat); // convertFormat
        client.syncFunction("convertTime", FunctionsLib::convertTime);
        client.syncFunction("timeFromString", FunctionsLib::timeFromString); // toInstance
        client.syncFunction("timeToString", FunctionsLib::timeToString); // toString
    }

    public static Value throwUnimplemented(Starscript ss) {
        ss.error(Constants.TRANSLATOR.translate("craftpresence.message.unsupported"));
        return Value.null_();
    }

    public static Value parseWith(DiscordUtils client, TranslationUtils instance, Starscript ss, int argCount) {
        final List<Value> args = StringUtils.newArrayList();
        if (argCount < 1)
            ss.error("parseWith() requires one or more arguments, got %d.", argCount);
        for (int i = 0; i < argCount; i++) {
            args.add(ss.pop());
        }
        StringUtils.revlist(args);

        // Parse, then remove the source entity from arguments, if valid
        Value source = args.getFirst();
        String data = null;
        if (source.isString()) {
            data = source.getString();
        }

        if (data == null) {
            ss.error("First argument to parseWith() needs to be a valid String.");
        }
        args.removeFirst();

        String result;
        // Parse remaining data into an Objects List
        if (!args.isEmpty() && data != null) {
            final List<Object> value = StringUtils.newArrayList();
            for (Value info : args) {
                value.add(client.fromValue(info));
            }
            Object[] formatArgs = value.toArray(new Object[0]);
            result = instance != null ? instance.translate(data, formatArgs) : String.format(data, formatArgs);
        } else {
            result = instance != null ? instance.translate(data) : data;
        }
        return !StringUtils.isNullOrEmpty(result) ? Value.string(result) : Value.null_();
    }

    public static Value format(DiscordUtils client, Starscript ss, int argCount) {
        return parseWith(client, null, ss, argCount);
    }

    public static Value translate(DiscordUtils client, Starscript ss, int argCount) {
        if (Constants.TRANSLATOR == null) ss.error("No available translations from mod data, try again later.");
        return parseWith(client, Constants.TRANSLATOR, ss, argCount);
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
        result = contents = FileUtils.getJsonData(json, JsonElement.class).getAsJsonObject();
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

    public static Value getResult(DiscordUtils client, Starscript ss, int argCount) {
        if (argCount != 1)
            ss.error("getResult() can only be used with one argument, got %d.", argCount);
        return Value.string(client.getResult(ss.pop().toString()));
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

    public static Value getAssetUrl(DiscordUtils client, Starscript ss, int argCount) {
        if (argCount != 1)
            ss.error("getAssetUrl() can only be used with one argument, got %d.", argCount);
        return Value.string(DiscordAssetUtils.getUrl(
                ss.pop().toString(), client::getResult
        ));
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

        currentArg = args.getFirst();
        args.removeFirst();

        double value = 0;
        if (currentArg.isNumber()) {
            value = currentArg.getNumber();
        } else {
            ss.error("First argument to isWithinValue() needs to be a number.");
        }

        currentArg = args.getFirst();
        args.removeFirst();

        double min = value;
        if (currentArg.isNumber()) {
            min = currentArg.getNumber();
        } else {
            ss.error("Second argument to isWithinValue() needs to be a number.");
        }

        currentArg = args.getFirst();
        args.removeFirst();

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
            currentArg = args.getFirst();
            args.removeFirst();

            if (currentArg.isBool()) {
                contains_min = currentArg.getBool();
            } else {
                ss.error("Fourth argument to isWithinValue() needs to be a boolean.");
            }

            currentArg = args.getFirst();
            args.removeFirst();

            if (currentArg.isBool()) {
                contains_max = currentArg.getBool();
            } else {
                ss.error("Fifth argument to isWithinValue() needs to be a boolean.");
            }

            if (argCount == 6) {
                currentArg = args.getFirst();
                args.removeFirst();

                if (currentArg.isBool()) {
                    check_sanity = currentArg.getBool();
                } else {
                    ss.error("Sixth argument to isWithinValue() needs to be a boolean.");
                }
            }
        }
        return Value.bool(MathUtils.isWithinValue(value, min, max, contains_min, contains_max, check_sanity));
    }

    public static Value roundDouble(Starscript ss, int argCount) {
        if (argCount < 1 || argCount > 2)
            ss.error("roundDouble() can only be used with 1-2 arguments, got %d.", argCount);
        int places = 0;
        if (argCount == 2) {
            places = (int) ss.popNumber("Second argument to roundDouble() needs to be a number.");
        }
        double target = ss.popNumber("First argument to roundDouble() needs to be a number.");
        return Value.number(MathUtils.roundDouble(target, places));
    }

    public static Value clampInt(Starscript ss, int argCount) {
        if (argCount != 3) ss.error("clampInt() requires 3 arguments, got %d.", argCount);
        int max = (int) ss.popNumber("Third argument to clampInt() needs to be a number.");
        int min = (int) ss.popNumber("Second argument to clampInt() needs to be a number.");
        int num = (int) ss.popNumber("First argument to clampInt() needs to be a number.");
        return Value.number(MathUtils.clamp(num, min, max));
    }

    public static Value clampLong(Starscript ss, int argCount) {
        if (argCount != 3) ss.error("clampLong() requires 3 arguments, got %d.", argCount);
        long max = (long) ss.popNumber("Third argument to clampLong() needs to be a number.");
        long min = (long) ss.popNumber("Second argument to clampLong() needs to be a number.");
        long num = (long) ss.popNumber("First argument to clampLong() needs to be a number.");
        return Value.number(MathUtils.clamp(num, min, max));
    }

    public static Value clampFloat(Starscript ss, int argCount) {
        if (argCount != 3) ss.error("clampFloat() requires 3 arguments, got %d.", argCount);
        float max = (float) ss.popNumber("Third argument to clampFloat() needs to be a number.");
        float min = (float) ss.popNumber("Second argument to clampFloat() needs to be a number.");
        float num = (float) ss.popNumber("First argument to clampFloat() needs to be a number.");
        return Value.number(MathUtils.clamp(num, min, max));
    }

    public static Value clampDouble(Starscript ss, int argCount) {
        if (argCount != 3) ss.error("clampDouble() requires 3 arguments, got %d.", argCount);
        double max = ss.popNumber("Third argument to clampDouble() needs to be a number.");
        double min = ss.popNumber("Second argument to clampDouble() needs to be a number.");
        double num = ss.popNumber("First argument to clampDouble() needs to be a number.");
        return Value.number(MathUtils.clamp(num, min, max));
    }

    public static Value lerpFloat(Starscript ss, int argCount) {
        if (argCount != 3) ss.error("lerpFloat() requires 3 arguments, got %d.", argCount);
        float max = (float) ss.popNumber("Third argument to lerpFloat() needs to be a number.");
        float min = (float) ss.popNumber("Second argument to lerpFloat() needs to be a number.");
        float num = (float) ss.popNumber("First argument to lerpFloat() needs to be a number.");
        return Value.number(MathUtils.lerp(num, min, max));
    }

    public static Value lerpDouble(Starscript ss, int argCount) {
        if (argCount != 3) ss.error("lerpDouble() requires 3 arguments, got %d.", argCount);
        double max = ss.popNumber("Third argument to lerpDouble() needs to be a number.");
        double min = ss.popNumber("Second argument to lerpDouble() needs to be a number.");
        double num = ss.popNumber("First argument to lerpDouble() needs to be a number.");
        return Value.number(MathUtils.lerp(num, min, max));
    }

    public static Value snapToStep(Starscript ss, int argCount) {
        if (argCount != 2) ss.error("snapToStep() requires 2 arguments, got %d.", argCount);
        float valueStep = (float) ss.popNumber("Second argument to snapToStep() needs to be a number.");
        float num = (float) ss.popNumber("First argument to snapToStep() needs to be a number.");
        return Value.number(MathUtils.snapToStep(num, valueStep));
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
        return Value.string(args.get(OSUtils.RANDOM.nextInt(argCount)));
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

    public static Value getArrayElement(DiscordUtils client, Starscript ss, int argCount) {
        if (argCount != 2) ss.error("getArrayElement() requires 2 arguments, %d.", argCount);
        int element = (int) ss.popNumber("Second argument to getArrayElement() needs to be a number.");
        Object data = ss.popObject("First argument to getArrayElement() needs to be an object.");
        Object[] array = StringUtils.getDynamicArray(data);
        if (array != null) {
            return client.toValue(array[element], true);
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

    public static Value cast(DiscordUtils client, Starscript ss, int argCount) {
        final List<Value> args = StringUtils.newArrayList();
        if (argCount != 2)
            ss.error("cast() can only be used with two arguments, got %d.", argCount);
        for (int i = 0; i < argCount; i++) {
            args.add(ss.pop());
        }
        StringUtils.revlist(args);
        Value target = args.getFirst();

        // Argument 1: castObject
        Object instance = client.fromValue(target);
        args.removeFirst();
        target = args.getFirst();
        Class<?> classToAccess = null;

        // Argument 2: classToAccess
        if (target.isObject()) {
            Object temp = target.getObject();
            if (temp instanceof Class<?> classObj) {
                classToAccess = classObj;
            } else {
                classToAccess = temp.getClass();
                instance = temp;
            }
        } else if (target.isString()) {
            classToAccess = FileUtils.loadClass(target.getString());
        } else {
            ss.error("Second argument to cast(), classToAccess, needs to be either a string, object, or class.");
        }
        args.removeFirst(); // Remove the classToAccess from parsing

        Object result = null;

        // If the className or field list is null, error as such
        if (classToAccess == null || instance == null) {
            ss.error("Insufficient or null arguments provided for required cast() params, please try again.");
        } else {
            result = FileUtils.castOrConvert(instance, classToAccess);
        }

        return result != null ? client.toValue(result, true) : Value.null_();
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

    public static Value getField(DiscordUtils client, Starscript ss, int argCount) {
        final List<Value> args = StringUtils.newArrayList();
        if (argCount < 2)
            ss.error("getField() requires two or more arguments, got %d.", argCount);
        for (int i = 0; i < argCount; i++) {
            args.add(ss.pop());
        }
        StringUtils.revlist(args);
        Value target = args.getFirst();

        Class<?> classToAccess = null;
        Object instance = null;
        List<String> fields = null;

        // Argument 1: classToAccess
        if (target.isObject()) {
            Object temp = target.getObject();
            if (temp instanceof Class<?> classObj) {
                classToAccess = classObj;
            } else {
                classToAccess = temp.getClass();
                instance = temp;
            }
        } else if (target.isString()) {
            classToAccess = FileUtils.loadClass(target.getString());
        } else {
            ss.error("First argument to getField(), classToAccess, needs to be either a string, object, or class.");
        }
        args.removeFirst(); // Remove the classToAccess from parsing

        // Argument 2 becomes instance, if not already supplied
        if (instance == null) {
            target = args.getFirst();
            instance = client.fromValue(target);
            args.removeFirst(); // Remove the instance from parsing
        }

        // Collect Field Names from remaining args
        if (!args.isEmpty()) {
            fields = StringUtils.newArrayList();

            for (Value data : args) {
                if (data.isString()) {
                    fields.add(data.getString());
                } else {
                    ss.error("Incorrect argument type for getField() param, fieldName (needs to be a string).");
                }
            }
        }

        Object result = null;

        // If the className or field list is null, error as such
        if (classToAccess == null || fields == null) {
            ss.error("Insufficient or null arguments provided for required getField() params, please try again.");
        } else {
            result = StringUtils.getField(classToAccess, instance, fields.toArray(new String[0]));
        }

        return result != null ? client.toValue(result, true) : Value.null_();
    }

    public static Value getClass(Starscript ss, int argCount) {
        if (argCount < 1) ss.error("getClass() can only be used with one argument, got %d.", argCount);
        Value value = ss.pop();
        Class<?> result = null;
        if (value.isObject()) {
            result = value.getObject().getClass();
        } else if (value.isString()) {
            result = FileUtils.loadClass(value.getString());
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
            return Value.bool(StringUtils.getValidField(result, b).isPresent());
        } else {
            ss.error("First argument to hasField() needs to be a valid class-compatible object.");
        }
        return Value.null_();
    }

    public static Value executeMethod(DiscordUtils client, Starscript ss, int argCount) {
        final List<Value> args = StringUtils.newArrayList();
        if (argCount < 2)
            ss.error("executeMethod() requires two or more arguments, got %d.", argCount);
        for (int i = 0; i < argCount; i++) {
            args.add(ss.pop());
        }
        StringUtils.revlist(args);
        Value target = args.getFirst();

        Class<?> classToAccess = null;
        Object instance = null;
        List<String> methodNames = StringUtils.newArrayList();

        // Argument 1: classToAccess
        if (target.isObject()) {
            Object temp = target.getObject();
            if (temp instanceof Class<?> classObj) {
                classToAccess = classObj;
            } else {
                classToAccess = temp.getClass();
                instance = temp;
            }
        } else if (target.isString()) {
            classToAccess = FileUtils.loadClass(target.getString());
        } else {
            ss.error("First argument to executeMethod(), classToAccess, needs to be either a string, object, or class.");
        }
        args.removeFirst(); // Remove the classToAccess from parsing

        // Argument 2 becomes instance, if not already supplied
        if (instance == null) {
            target = args.getFirst();
            instance = client.fromValue(target);
            args.removeFirst(); // Remove the instance from parsing
        }

        // Either the 2nd or 3rd param will be methodName, depending on args
        if (!args.isEmpty()) {
            target = args.getFirst(); // Refresh arg table
            if (target.isString()) {
                final String name = target.getString();
                if (name.contains(",")) {
                    Collections.addAll(methodNames, name.split(","));
                } else {
                    methodNames.add(name);
                }
            } else {
                ss.error("Incorrect argument type for executeMethod() param, methodName (needs to be a string).");
            }
            args.removeFirst(); // Remove the methodName from parsing
        }

        // If the className or methodName is null, error as such
        // instanceName, and the parameterTypes and parameters tables can be null
        if (classToAccess == null || methodNames.isEmpty()) {
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
                        if (temp instanceof Class<?> paramClass) {
                            classObj = paramClass;
                        }
                    } else if (data.isString()) {
                        classObj = FileUtils.loadClass(data.getString());
                    }

                    if (classObj == null) {
                        ss.error("Class argument for executeMethod() parameterTypes, must be a class object or string.");
                    } else {
                        parameterTypes.add(classObj);
                    }
                } else {
                    parameters.add(client.fromValue(data));
                }
                classMode = !classMode;
            }
        }

        Object result = StringUtils.executeMethod(classToAccess, instance,
                parameterTypes != null ? parameterTypes.toArray(new Class<?>[0]) : null,
                parameters != null ? parameters.toArray(new Object[0]) : null,
                methodNames.toArray(new String[0])
        );

        return result != null ? client.toValue(result, true) : Value.null_();
    }

    public static Value stripColors(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("stripColors() requires 1 argument, got %d.", argCount);
        String a = ss.popString("Argument to stripColors() needs to be a string.");
        return Value.string(StringUtils.stripColors(a));
    }

    public static Value stripFormatting(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("stripFormatting() requires 1 argument, got %d.", argCount);
        String a = ss.popString("Argument to stripFormatting() needs to be a string.");
        return Value.string(StringUtils.stripFormatting(a));
    }

    public static Value stripAllFormatting(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("stripAllFormatting() requires 1 argument, got %d.", argCount);
        String a = ss.popString("Argument to stripAllFormatting() needs to be a string.");
        return Value.string(StringUtils.stripAllFormatting(a));
    }

    public static Value getCurrentTime(Starscript ss, int argCount) {
        return Value.object(TimeUtils.getCurrentTime());
    }

    public static Value getElapsedNanos(Starscript ss, int argCount) {
        return Value.number(TimeUtils.getElapsedNanos());
    }

    public static Value getElapsedMillis(Starscript ss, int argCount) {
        return Value.number(TimeUtils.getElapsedMillis());
    }

    public static Value getElapsedSeconds(Starscript ss, int argCount) {
        return Value.number(TimeUtils.getElapsedSeconds());
    }

    public static Value timeToEpochSecond(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("timeToEpochSecond() requires 1 argument, got %d.", argCount);
        Object a = ss.popObject("Argument to timeToEpochSecond() needs to be an object.");
        if (a instanceof Instant data) {
            return Value.number(TimeUtils.toEpochSecond(data));
        } else {
            ss.error("Argument to timeToEpochSecond() needs to be a valid Instant Object.");
        }
        return Value.null_();
    }

    public static Value timeToEpochMilli(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("timeToEpochMilli() requires 1 argument, got %d.", argCount);
        Object a = ss.popObject("Argument to timeToEpochMilli() needs to be an object.");
        if (a instanceof Instant data) {
            return Value.number(TimeUtils.toEpochMilli(data));
        } else {
            ss.error("Argument to timeToEpochMilli() needs to be a valid Instant Object.");
        }
        return Value.null_();
    }

    public static Value timeFromEpochSecond(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("timeFromEpochSecond() requires 1 argument, got %d.", argCount);
        double a = ss.popNumber("Argument to timeFromEpochSecond() needs to be a number.");
        return Value.object(TimeUtils.fromEpochSecond((long) a));
    }

    public static Value timeFromEpochMilli(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("timeFromEpochMilli() requires 1 argument, got %d.", argCount);
        double a = ss.popNumber("Argument to timeFromEpochMilli() needs to be a number.");
        return Value.object(TimeUtils.fromEpochMilli((long) a));
    }

    public static Value dateToEpochSecond(Starscript ss, int argCount) {
        if (argCount < 2 || argCount > 3)
            ss.error("dateToEpochSecond() can only be used with 2-3 arguments, got %d.", argCount);
        String timeZone = TimeUtils.DEFAULT_ZONE;
        if (argCount == 3) {
            timeZone = ss.popString("Third argument to dateToEpochSecond() needs to be a string.");
        }
        String format = ss.popString("Second argument to dateToEpochSecond() needs to be a string.");
        String dateString = ss.popString("First argument to dateToEpochSecond() needs to be a string.");
        return Value.number(TimeUtils.stringToEpochSecond(dateString, format, timeZone));
    }

    public static Value dateToEpochMilli(Starscript ss, int argCount) {
        if (argCount < 2 || argCount > 3)
            ss.error("dateToEpochMilli() can only be used with 2-3 arguments, got %d.", argCount);
        String timeZone = TimeUtils.DEFAULT_ZONE;
        if (argCount == 3) {
            timeZone = ss.popString("Third argument to dateToEpochMilli() needs to be a string.");
        }
        String format = ss.popString("Second argument to dateToEpochMilli() needs to be a string.");
        String dateString = ss.popString("First argument to dateToEpochMilli() needs to be a string.");
        return Value.number(TimeUtils.stringToEpochMilli(dateString, format, timeZone));
    }

    public static Value epochSecondToDate(Starscript ss, int argCount) {
        if (argCount < 2 || argCount > 3)
            ss.error("epochSecondToDate() can only be used with 2-3 arguments, got %d.", argCount);
        String timeZone = TimeUtils.DEFAULT_ZONE;
        if (argCount == 3) {
            timeZone = ss.popString("Third argument to epochSecondToDate() needs to be a string.");
        }
        String format = ss.popString("Second argument to epochSecondToDate() needs to be a string.");
        double dateString = ss.popNumber("First argument to epochSecondToDate() needs to be a number.");
        return Value.string(TimeUtils.epochSecondToString((long) dateString, format, timeZone));
    }

    public static Value epochMilliToDate(Starscript ss, int argCount) {
        if (argCount < 2 || argCount > 3)
            ss.error("epochMilliToDate() can only be used with 2-3 arguments, got %d.", argCount);
        String timeZone = TimeUtils.DEFAULT_ZONE;
        if (argCount == 3) {
            timeZone = ss.popString("Third argument to epochMilliToDate() needs to be a string.");
        }
        String format = ss.popString("Second argument to epochMilliToDate() needs to be a string.");
        double dateString = ss.popNumber("First argument to epochMilliToDate() needs to be a number.");
        return Value.string(TimeUtils.epochMilliToString((long) dateString, format, timeZone));
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
        String fromTimeZone = TimeUtils.DEFAULT_ZONE;
        if (argCount == 3) {
            fromTimeZone = ss.popString("Third argument to timeFromString() needs to be a string.");
        }
        String fromFormat = ss.popString("Second argument to timeFromString() needs to be a string.");
        String dateString = ss.popString("First argument to timeFromString() needs to be a string.");
        return Value.object(TimeUtils.toInstant(dateString, fromFormat, fromTimeZone));
    }

    public static Value timeToString(Starscript ss, int argCount) {
        if (argCount < 2 || argCount > 3)
            ss.error("timeToString() can only be used with 2-3 arguments, got %d.", argCount);
        String toTimeZone = TimeUtils.DEFAULT_ZONE;
        if (argCount == 3) {
            toTimeZone = ss.popString("Third argument to timeToString() needs to be a string.");
        }
        String toFormat = ss.popString("Second argument to timeToString() needs to be a string.");
        Object date = ss.popObject("First argument to timeToString() needs to be an object.");
        if (date instanceof Instant data) {
            return Value.string(TimeUtils.toString(data, toFormat, toTimeZone));
        } else {
            ss.error("First Argument to timeToString() needs to be a valid Instant Object.");
        }
        return Value.null_();
    }
}
