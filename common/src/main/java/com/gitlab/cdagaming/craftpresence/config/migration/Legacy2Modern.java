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

package com.gitlab.cdagaming.craftpresence.config.migration;

import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.element.Button;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.google.gson.JsonElement;
import io.github.cdagaming.unicore.impl.HashMapBuilder;
import io.github.cdagaming.unicore.impl.Pair;
import io.github.cdagaming.unicore.impl.Tuple;
import io.github.cdagaming.unicore.utils.StringUtils;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;

/**
 * Migration from v1 (v1.x) to v2 (v2.0 - Latest Schema) Configs
 */
public class Legacy2Modern implements DataMigrator {
    private final File configFile;
    private final String encoding;
    private final Properties properties = new Properties();

    private final Predicate<String> IS_TEXT = (id) -> id.equalsIgnoreCase("text");
    private final Predicate<String> IS_ICON = (id) -> id.equalsIgnoreCase("icon");
    private final Predicate<String> IS_DIM_MODULE = (e) -> e.equals("dimensionMessages");
    private final Predicate<String> IS_BIOME_MODULE = (e) -> e.equals("biomeMessages");
    private final Predicate<String> IS_SERVER_MODULE = (e) -> e.equals("serverMessages");
    private final Predicate<String> IS_SCREEN_MODULE = (e) -> e.equals("guiMessages");
    private final Predicate<String> IS_ITEM_MODULE = (e) -> e.equals("itemMessages");
    private final Predicate<String> IS_ENTITY_TARGET_MODULE = (e) -> e.equals("entityTargetMessages");
    private final Predicate<String> IS_ENTITY_RIDING_MODULE = (e) -> e.equals("entityRidingMessages");

    private final Predicate<String> IS_PLAYER_OUTER_INFO_PLACEHOLDER = (e) -> e.equalsIgnoreCase("playerOuterInfoPlaceholder");
    private final Predicate<String> IS_MODS_PLACEHOLDER = (e) -> e.equalsIgnoreCase("modsPlaceholder");
    private final Predicate<String> IS_MODPACK_PLACEHOLDER = (e) -> e.equalsIgnoreCase("modpackMessage");
    private final Predicate<String> IS_PLAYER_INNER_INFO_PLACEHOLDER = (e) -> e.equalsIgnoreCase("playerInnerInfoPlaceholder");
    private final Predicate<String> IS_PLAYER_COORDINATE_PLACEHOLDER = (e) -> e.equalsIgnoreCase("playerCoordinatePlaceholder");
    private final Predicate<String> IS_PLAYER_HEALTH_PLACEHOLDER = (e) -> e.equalsIgnoreCase("playerHealthPlaceholder");
    private final Predicate<String> IS_PLAYER_LIST_PLACEHOLDER = (e) -> e.equalsIgnoreCase("playerListPlaceholder");
    private final Predicate<String> IS_WORLD_DATA_PLACEHOLDER = (e) -> e.equalsIgnoreCase("worldDataPlaceholder");
    private final Predicate<String> IS_PLAYER_ITEMS_PLACEHOLDER = (e) -> e.equalsIgnoreCase("playerItemsPlaceholder");

    // oldName -> newName:replaceCondition
    private final List<Tuple<Pair<String, String>, Predicate<String>, Predicate<String>>> placeholderMappings = Arrays.asList(
            generatePair("&DEFAULT&", "{general.icon}", IS_ICON),
            generatePair("&MAINMENU&", "{menu.message}", IS_TEXT),
            generatePair("&MAINMENU&", "{menu.icon}", IS_ICON),
            generatePair("&BRAND&", "{general.brand}", IS_TEXT),
            generatePair("&MCVERSION&", "{general.title}", IS_TEXT),
            //
            generatePair("&IGN&", "{custom.player_info_out}", IS_TEXT),
            generatePair("&IGN&", "{player.icon}", IS_ICON),
            generatePair("&IGN:NAME&", "{player.name}", IS_TEXT),
            generatePair("&NAME&", "{player.name}", IS_TEXT, IS_PLAYER_OUTER_INFO_PLACEHOLDER),
            generatePair("&IGN:UUID&", "{player.uuid.short}", IS_TEXT),
            generatePair("&UUID&", "{player.uuid.short}", IS_TEXT, IS_PLAYER_OUTER_INFO_PLACEHOLDER),
            generatePair("&IGN:UUID_FULL&", "{player.uuid.full}", IS_TEXT),
            generatePair("&UUID_FULL&", "{player.uuid.full}", IS_TEXT, IS_PLAYER_OUTER_INFO_PLACEHOLDER),
            //
            generatePair("&MODS&", "{custom.mods}", IS_TEXT),
            generatePair("&MODS:MODCOUNT&", "{general.mods}", IS_TEXT),
            generatePair("&MODCOUNT&", "{general.mods}", IS_TEXT, IS_MODS_PLACEHOLDER),
            //
            generatePair("&PACK&", "{custom.pack}", IS_TEXT),
            generatePair("&PACK&", "{pack.icon}", IS_ICON),
            generatePair("&PACK:NAME&", "{pack.name}", IS_TEXT),
            generatePair("&NAME&", "{pack.name}", IS_TEXT, IS_MODPACK_PLACEHOLDER),
            //
            generatePair("&DIMENSION:DIMENSION&", "{dimension.name}", IS_TEXT),
            generatePair("&DIMENSION&", "{dimension.name}", IS_TEXT, IS_DIM_MODULE),
            generatePair("&DIMENSION&", "{dimension.message}", IS_TEXT),
            generatePair("&DIMENSION:ICON&", "{dimension.icon}", IS_ICON),
            generatePair("&ICON&", "{dimension.icon}", IS_ICON, IS_DIM_MODULE),
            generatePair("&DIMENSION&", "{dimension.icon}", IS_ICON),
            //
            generatePair("&BIOME:BIOME&", "{biome.name}", IS_TEXT),
            generatePair("&BIOME&", "{biome.name}", IS_TEXT, IS_BIOME_MODULE),
            generatePair("&BIOME&", "{biome.message}", IS_TEXT),
            generatePair("&BIOME:ICON&", "{biome.icon}", IS_ICON),
            generatePair("&ICON&", "{biome.icon}", IS_ICON, IS_BIOME_MODULE),
            generatePair("&BIOME&", "{biome.icon}", IS_ICON),
            //
            generatePair("&SERVER:IP&", "{server.address.short}", IS_TEXT),
            generatePair("&IP&", "{server.address.short}", IS_TEXT, IS_SERVER_MODULE),
            generatePair("&SERVER:NAME&", "{server.name}", IS_TEXT),
            generatePair("&NAME&", "{server.name}", IS_TEXT, IS_SERVER_MODULE),
            generatePair("&SERVER:MOTD&", "{server.motd.raw}", IS_TEXT),
            generatePair("&MOTD&", "{server.motd.raw}", IS_TEXT, IS_SERVER_MODULE),
            generatePair("&SERVER&", "{server.message}", IS_TEXT),
            generatePair("&SERVER:ICON&", "{server.icon}", IS_ICON),
            generatePair("&ICON&", "{server.icon}", IS_ICON, IS_SERVER_MODULE),
            generatePair("&SERVER&", "{server.icon}", IS_ICON),
            //
            generatePair("&SERVER:PLAYERS&", "{custom.players}", IS_TEXT),
            generatePair("&PLAYERS&", "{custom.players}", IS_TEXT, IS_SERVER_MODULE),
            //
            generatePair("&SERVER:WORLDINFO&", "{custom.world_info}", IS_TEXT),
            generatePair("&WORLDINFO&", "{custom.world_info}", IS_TEXT, IS_SERVER_MODULE),
            //
            generatePair("&SERVER:PLAYERINFO&", "{custom.player_info_in}", IS_TEXT),
            generatePair("&PLAYERINFO&", "{custom.player_info_in}", IS_TEXT, IS_SERVER_MODULE),
            //
            generatePair("&SERVER:PLAYERINFO:COORDS&", "{custom.player_info_coordinate}", IS_TEXT),
            generatePair("&PLAYERINFO:COORDS&", "{custom.player_info_coordinate}", IS_TEXT, IS_SERVER_MODULE),
            generatePair("&COORDS&", "{custom.player_info_coordinate}", IS_TEXT, IS_PLAYER_INNER_INFO_PLACEHOLDER),
            //
            generatePair("&SERVER:PLAYERINFO:HEALTH&", "{custom.player_info_health}", IS_TEXT),
            generatePair("&PLAYERINFO:HEALTH&", "{custom.player_info_health}", IS_TEXT, IS_SERVER_MODULE),
            generatePair("&HEALTH&", "{custom.player_info_health}", IS_TEXT, IS_PLAYER_INNER_INFO_PLACEHOLDER),
            //
            generatePair("&SERVER:PLAYERINFO:COORDS:xPosition&", "{player.position.x}", IS_TEXT),
            generatePair("&PLAYERINFO:COORDS:xPosition&", "{player.position.x}", IS_TEXT, IS_SERVER_MODULE),
            generatePair("&COORDS:xPosition&", "{player.position.x}", IS_TEXT, IS_PLAYER_INNER_INFO_PLACEHOLDER),
            generatePair("&xPosition&", "{player.position.x}", IS_TEXT, IS_PLAYER_COORDINATE_PLACEHOLDER),
            //
            generatePair("&SERVER:PLAYERINFO:COORDS:yPosition&", "{player.position.y}", IS_TEXT),
            generatePair("&PLAYERINFO:COORDS:yPosition&", "{player.position.y}", IS_TEXT, IS_SERVER_MODULE),
            generatePair("&COORDS:yPosition&", "{player.position.y}", IS_TEXT, IS_PLAYER_INNER_INFO_PLACEHOLDER),
            generatePair("&yPosition&", "{player.position.y}", IS_TEXT, IS_PLAYER_COORDINATE_PLACEHOLDER),
            //
            generatePair("&SERVER:PLAYERINFO:COORDS:zPosition&", "{player.position.z}", IS_TEXT),
            generatePair("&PLAYERINFO:COORDS:zPosition&", "{player.position.z}", IS_TEXT, IS_SERVER_MODULE),
            generatePair("&COORDS:zPosition&", "{player.position.z}", IS_TEXT, IS_PLAYER_INNER_INFO_PLACEHOLDER),
            generatePair("&zPosition&", "{player.position.z}", IS_TEXT, IS_PLAYER_COORDINATE_PLACEHOLDER),
            //
            generatePair("&SERVER:PLAYERINFO:HEALTH:CURRENT&", "{player.health.current}", IS_TEXT),
            generatePair("&PLAYERINFO:HEALTH:CURRENT&", "{player.health.current}", IS_TEXT, IS_SERVER_MODULE),
            generatePair("&HEALTH:CURRENT&", "{player.health.current}", IS_TEXT, IS_PLAYER_INNER_INFO_PLACEHOLDER),
            generatePair("&CURRENT&", "{player.health.current}", IS_TEXT, IS_PLAYER_HEALTH_PLACEHOLDER),
            //
            generatePair("&SERVER:PLAYERINFO:HEALTH:MAX&", "{player.health.max}", IS_TEXT),
            generatePair("&PLAYERINFO:HEALTH:MAX&", "{player.health.max}", IS_TEXT, IS_SERVER_MODULE),
            generatePair("&HEALTH:MAX&", "{player.health.max}", IS_TEXT, IS_PLAYER_INNER_INFO_PLACEHOLDER),
            generatePair("&MAX&", "{player.health.max}", IS_TEXT, IS_PLAYER_HEALTH_PLACEHOLDER),
            //
            generatePair("&SERVER:PLAYERS:CURRENT&", "{server.players.current}", IS_TEXT),
            generatePair("&PLAYERS:CURRENT&", "{server.players.current}", IS_TEXT, IS_SERVER_MODULE),
            generatePair("&CURRENT&", "{server.players.current}", IS_TEXT, IS_PLAYER_LIST_PLACEHOLDER),
            //
            generatePair("&SERVER:PLAYERS:MAX&", "{server.players.max}", IS_TEXT),
            generatePair("&PLAYERS:MAX&", "{server.players.max}", IS_TEXT, IS_SERVER_MODULE),
            generatePair("&MAX&", "{server.players.max}", IS_TEXT, IS_PLAYER_LIST_PLACEHOLDER),
            //
            generatePair("&SERVER:WORLDINFO:DIFFICULTY&", "{world.difficulty}", IS_TEXT),
            generatePair("&WORLDINFO:DIFFICULTY&", "{world.difficulty}", IS_TEXT, IS_SERVER_MODULE),
            generatePair("&DIFFICULTY&", "{world.difficulty}", IS_TEXT, IS_WORLD_DATA_PLACEHOLDER),
            //
            generatePair("&SERVER:WORLDINFO:WORLDNAME&", "{world.name}", IS_TEXT),
            generatePair("&WORLDINFO:WORLDNAME&", "{world.name}", IS_TEXT, IS_SERVER_MODULE),
            generatePair("&WORLDNAME&", "{world.name}", IS_TEXT, IS_WORLD_DATA_PLACEHOLDER),
            //
            generatePair("&SERVER:WORLDINFO:WORLDTIME&", "{world.time.format_24}", IS_TEXT),
            generatePair("&WORLDINFO:WORLDTIME&", "{world.time.format_24}", IS_TEXT, IS_SERVER_MODULE),
            generatePair("&WORLDTIME&", "{world.time.format_24}", IS_TEXT, IS_WORLD_DATA_PLACEHOLDER),
            //
            generatePair("&SERVER:WORLDINFO:WORLDTIME12&", "{world.time.format_12}", IS_TEXT),
            generatePair("&WORLDINFO:WORLDTIME12&", "{world.time.format_12}", IS_TEXT, IS_SERVER_MODULE),
            generatePair("&WORLDTIME12&", "{world.time.format_12}", IS_TEXT, IS_WORLD_DATA_PLACEHOLDER),
            //
            generatePair("&SERVER:WORLDINFO:WORLDDAY&", "{world.time.day}", IS_TEXT),
            generatePair("&WORLDINFO:WORLDDAY&", "{world.time.day}", IS_TEXT, IS_SERVER_MODULE),
            generatePair("&WORLDDAY&", "{world.time.day}", IS_TEXT, IS_WORLD_DATA_PLACEHOLDER),
            //
            generatePair("&SCREEN:SCREEN&", "{screen.name}", IS_TEXT),
            generatePair("&SCREEN&", "{screen.name}", IS_TEXT, IS_SCREEN_MODULE),
            generatePair("&SCREEN&", "{screen.message}", IS_TEXT),
            generatePair("&SCREEN:ICON&", "{screen.icon}", IS_ICON),
            generatePair("&ICON&", "{screen.icon}", IS_ICON, IS_SCREEN_MODULE),
            generatePair("&SCREEN&", "{screen.icon}", IS_ICON),
            generatePair("&SCREEN:CLASS&", "{getClass(data.screen.instance)}", IS_TEXT),
            generatePair("&CLASS&", "{getClass(data.screen.instance)}", IS_TEXT, IS_SERVER_MODULE),
            //
            generatePair("&TARGETENTITY:ENTITY&", "{entity.target.name}", IS_TEXT),
            generatePair("&ENTITY&", "{entity.target.name}", IS_TEXT, IS_ENTITY_TARGET_MODULE),
            generatePair("&TARGETENTITY&", "{entity.target.message}", IS_TEXT),
            generatePair("&TARGETENTITY:ICON&", "{entity.target.icon}", IS_ICON),
            generatePair("&ICON&", "{entity.target.icon}", IS_ICON, IS_ENTITY_TARGET_MODULE),
            generatePair("&TARGETENTITY&", "{entity.target.icon}", IS_ICON),
            //
            generatePair("&RIDINGENTITY:ENTITY&", "{entity.riding.name}", IS_TEXT),
            generatePair("&ENTITY&", "{entity.riding.name}", IS_TEXT, IS_ENTITY_RIDING_MODULE),
            generatePair("&RIDINGENTITY&", "{entity.riding.message}", IS_TEXT),
            generatePair("&RIDINGENTITY:ICON&", "{entity.riding.icon}", IS_ICON),
            generatePair("&ICON&", "{entity.riding.icon}", IS_ICON, IS_ENTITY_RIDING_MODULE),
            generatePair("&RIDINGENTITY&", "{entity.riding.icon}", IS_ICON),
            //
            generatePair("&TILEENTITY:MAIN&", "{item.main_hand.message}", IS_TEXT),
            generatePair("&MAIN&", "{item.main_hand.message}", IS_TEXT, IS_PLAYER_ITEMS_PLACEHOLDER),
            generatePair("&TILEENTITY:OFFHAND&", "{item.off_hand.message}", IS_TEXT),
            generatePair("&OFFHAND&", "{item.off_hand.message}", IS_TEXT, IS_PLAYER_ITEMS_PLACEHOLDER),
            generatePair("&TILEENTITY:HELMET&", "{item.helmet.message}", IS_TEXT),
            generatePair("&HELMET&", "{item.helmet.message}", IS_TEXT, IS_PLAYER_ITEMS_PLACEHOLDER),
            generatePair("&TILEENTITY:CHEST&", "{item.chestplate.message}", IS_TEXT),
            generatePair("&CHEST&", "{item.chestplate.message}", IS_TEXT, IS_PLAYER_ITEMS_PLACEHOLDER),
            generatePair("&TILEENTITY:CHEST&", "{item.chestplate.message}", IS_TEXT),
            generatePair("&CHEST&", "{item.chestplate.message}", IS_TEXT, IS_PLAYER_ITEMS_PLACEHOLDER),
            generatePair("&TILEENTITY:LEGS&", "{item.leggings.message}", IS_TEXT),
            generatePair("&LEGS&", "{item.leggings.message}", IS_TEXT, IS_PLAYER_ITEMS_PLACEHOLDER),
            generatePair("&TILEENTITY:BOOTS&", "{item.boots.message}", IS_TEXT),
            generatePair("&BOOTS&", "{item.boots.message}", IS_TEXT, IS_PLAYER_ITEMS_PLACEHOLDER),
            //
            generatePair("&TILEENTITY&", "{item.message.default}", IS_TEXT),
            generatePair("&ITEM&", "{item.message.holding}", IS_TEXT, IS_ITEM_MODULE)
    );

    // oldName -> newName
    private final Map<String, String> configNameMappings = new HashMapBuilder<String, String>()
            .put("lastMcVersionId", "_lastMCVersionId")
            //
            .put("detectCurseManifest", "generalSettings.detectCurseManifest")
            .put("detectMultimcInstance", "generalSettings.detectMultiMCManifest")
            .put("detectMcupdaterInstance", "generalSettings.detectMCUpdaterInstance")
            .put("detectTechnicPack", "generalSettings.detectTechnicPack")
            .put("detectBiomeData", "generalSettings.detectBiomeData")
            .put("detectDimensionData", "generalSettings.detectDimensionData")
            .put("detectWorldData", "generalSettings.detectWorldData")
            .put("clientId", "generalSettings.clientId")
            .put("defaultIcon", "generalSettings.defaultIcon")
            .put("enableJoinRequests", "generalSettings.enableJoinRequests")
            .put("partyPrivacy", "generalSettings.partyPrivacyLevel")
            .put("preferredClient", "generalSettings.preferredClientLevel")
            .put("resetTimeOnInit", "generalSettings.resetTimeOnInit")
            .put("autoRegister", "generalSettings.autoRegister")
            //
            .put("defaultBiomeIcon", "biomeSettings.fallbackBiomeIcon")
            .put("biomeMessages", "biomeSettings.biomeData")
            //
            .put("defaultDimensionIcon", "dimensionSettings.fallbackDimensionIcon")
            .put("dimensionMessages", "dimensionSettings.dimensionData")
            //
            .put("defaultServerIcon", "serverSettings.fallbackServerIcon")
            .put("defaultServerName", "serverSettings.fallbackServerName")
            .put("defaultServerMotd", "serverSettings.fallbackServerMotd")
            .put("serverMessages", "serverSettings.serverData")
            //
            .put("mainMenuMessage", "statusMessages.mainMenuData.textOverride")
            .put("loadingMessage", "statusMessages.loadingData.textOverride")
            .put("lanGameMessage", "statusMessages.lanData.textOverride")
            .put("singleplayerGameMessage", "statusMessages.singleplayerData.textOverride")
            //
            .put("modpackMessage", "displaySettings.dynamicVariables.pack")
            .put("playerOuterInfoPlaceholder", "displaySettings.dynamicVariables.player_info_out")
            .put("playerInnerInfoPlaceholder", "displaySettings.dynamicVariables.player_info_in")
            .put("playerCoordinatePlaceholder", "displaySettings.dynamicVariables.player_info_coordinate")
            .put("playerHealthPlaceholder", "displaySettings.dynamicVariables.player_info_health")
            .put("playerListPlaceholder", "displaySettings.dynamicVariables.players")
            .put("playerItemsPlaceholder", "displaySettings.dynamicVariables.player_info_items")
            .put("worldDataPlaceholder", "displaySettings.dynamicVariables.world_info")
            .put("modsPlaceholder", "displaySettings.dynamicVariables.mods")
            //
            .put("enablePerGuiSystem", "advancedSettings.enablePerGui")
            .put("enablePerItemSystem", "advancedSettings.enablePerItem")
            .put("enablePerEntitySystem", "advancedSettings.enablePerEntity")
            .put("formatWords", "advancedSettings.formatWords")
            .put("debugMode", "advancedSettings.debugMode")
            .put("verboseMode", "advancedSettings.verboseMode")
            .put("refreshRate", "advancedSettings.refreshRate")
            .put("allowPlaceholderPreviews", "advancedSettings.allowPlaceholderPreviews")
            .put("allowPlaceholderOperators", "advancedSettings.allowPlaceholderOperators")
            .put("guiMessages", "advancedSettings.guiSettings.guiData")
            .put("itemMessages", "advancedSettings.itemMessages")
            .put("entityTargetMessages", "advancedSettings.entitySettings.targetData")
            .put("entityRidingMessages", "advancedSettings.entitySettings.ridingData")
            .put("playerSkinEndpoint", "advancedSettings.playerSkinEndpoint")
            //
            .put("languageId", "accessibilitySettings.languageId")
            .put("stripTranslationColors", "accessibilitySettings.stripTranslationColors")
            .put("stripExtraGuiElements", "accessibilitySettings.stripExtraGuiElements")
            .put("renderTooltips", "accessibilitySettings.renderTooltips")
            .put("configGuiKeybind", "accessibilitySettings.configKeyCode")
            //
            .put("gameStateMessageFormat", "displaySettings.presenceData.gameState")
            .put("detailsMessageFormat", "displaySettings.presenceData.details")
            .put("largeImageTextFormat", "displaySettings.presenceData.largeImageText")
            .put("smallImageTextFormat", "displaySettings.presenceData.smallImageText")
            .put("largeImageKeyFormat", "displaySettings.presenceData.largeImageKey")
            .put("smallImageKeyFormat", "displaySettings.presenceData.smallImageKey")
            .put("extraButtonMessages", "displaySettings.presenceData.buttons")
            .put("dynamicIcons", "displaySettings.dynamicIcons")
            .build();
    private final List<String> excludedOptions = StringUtils.newArrayList(
            "schemaVersion", "splitCharacter",
            "tooltipBackgroundColor", "tooltipBorderColor"
    );

    /**
     * Initializes this {@link DataMigrator}
     *
     * @param configFile the location of the v1 config
     * @param encoding   the encoding of the v1 config
     */
    public Legacy2Modern(File configFile, String encoding) {
        this.configFile = configFile;
        this.encoding = encoding;
    }

    @Override
    public Config apply(Config instance, JsonElement rawJson, Object... args) {
        try (Reader configReader = new InputStreamReader(Files.newInputStream(configFile.toPath()), Charset.forName(encoding))) {
            properties.load(configReader);
        } catch (Exception ex) {
            Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.config.save"));
            Constants.LOG.debugError(ex);
        } finally {
            String originalName, newName;
            Object originalValue, currentValue, newValue;
            Class<?> expectedClass;

            final String splitCharacter = properties.getProperty("splitCharacter", ";");
            for (String property : properties.stringPropertyNames()) {
                originalName = StringUtils.formatToCamel(property);
                if (!excludedOptions.contains(originalName)) {
                    newName = configNameMappings.getOrDefault(originalName, originalName);
                    originalValue = properties.get(property);
                    newValue = currentValue = instance.getProperty(newName);

                    if (currentValue != null) {
                        expectedClass = currentValue.getClass();
                        if (expectedClass == Boolean.class &&
                                StringUtils.isValidBoolean(originalValue)) {
                            newValue = Boolean.parseBoolean(originalValue.toString());
                        } else if (expectedClass == Integer.class &&
                                StringUtils.getValidInteger(originalValue).getFirst()) {
                            final Pair<Boolean, Integer> boolData = StringUtils.getValidInteger(originalValue);
                            if (boolData.getFirst()) {
                                newValue = boolData.getSecond();
                            }
                        } else if (currentValue instanceof Map<?, ?>) {
                            final String convertedString = StringUtils.removeMatches("\\[([^\\s]+?)\\]", originalValue.toString(), 0, 1);
                            final String[] oldArray;

                            final Map<Object, Object> newData = StringUtils.newHashMap((Map<?, ?>) currentValue);
                            final Class<?> expectedSecondaryClass = newData.get("default").getClass();

                            if (!StringUtils.isNullOrEmpty(convertedString) &&
                                    (convertedString.startsWith("[") && convertedString.endsWith("]"))) {
                                // If Valid, interpret into formatted Array
                                final String preArrayString = convertedString.replaceAll("\\[", "").replaceAll("]", "");
                                if (preArrayString.contains(", ")) {
                                    oldArray = preArrayString.split(", ");
                                } else if (preArrayString.contains(",")) {
                                    oldArray = preArrayString.split(",");
                                } else {
                                    oldArray = new String[]{preArrayString};
                                }
                            } else {
                                oldArray = null;
                            }

                            if (oldArray != null) {
                                newData.clear();
                                for (String entry : oldArray) {
                                    if (!StringUtils.isNullOrEmpty(entry)) {
                                        final String[] part = entry.split(splitCharacter);
                                        if (!StringUtils.isNullOrEmpty(part[0])) {
                                            if (expectedSecondaryClass == Pair.class) {
                                                newData.put(part[0], new Pair<>(
                                                        part.length >= 2 ? process(part[1], originalName, "text") : null,
                                                        part.length >= 3 ? process(part[2], originalName, "icon") : null
                                                ));
                                            } else if (expectedSecondaryClass == Tuple.class) {
                                                newData.put(part[0], new Tuple<>(
                                                        part.length >= 2 ? process(part[1], originalName, "text") : null,
                                                        part.length >= 3 ? process(part[2], originalName, "text") : null,
                                                        part.length >= 4 ? process(part[3], originalName, "text") : null
                                                ));
                                            } else if (expectedSecondaryClass == ModuleData.class) {
                                                newData.put(part[0], new ModuleData(
                                                        part.length >= 2 ? process(part[1], originalName, "text") : null,
                                                        part.length >= 3 ? process(part[2], originalName, "icon") : null
                                                ));
                                            } else if (expectedSecondaryClass == Button.class) {
                                                newData.put(part[0], new Button(
                                                        part.length >= 2 ? process(part[1], originalName, "text") : null,
                                                        part.length >= 3 ? process(part[2], originalName, "text") : null
                                                ));
                                            } else {
                                                newData.put(part[0], part.length >= 2 ? process(part[1], originalName, "text") : null);
                                            }
                                        }
                                    }
                                }
                                newValue = newData;
                            }
                        } else {
                            newValue = process(originalValue.toString(), originalName, originalName.contains("ImageKey") ? "icon" : "text");
                        }

                        if (!currentValue.equals(newValue)) {
                            Constants.LOG.debugInfo("Migrating modified legacy property " + originalName + " to JSON property " + newName);
                            instance.setProperty(newName, newValue);
                        }
                    }
                }
            }
        }

        if (!configFile.delete()) {
            Constants.LOG.error("Failed to remove: " + configFile.getName());
        }
        // Force Schema Version to the latest schema, before saving
        instance._schemaVersion = Config.getSchemaVersion();
        instance.save();
        return instance;
    }

    private String process(final String input, final String originalName, final String argumentType) {
        String result = input;

        for (Tuple<Pair<String, String>, Predicate<String>, Predicate<String>> entry : placeholderMappings) {
            final Pair<String, String> replacer = entry.getFirst();
            final String original = replacer.getFirst();
            final String newValue = replacer.getSecond();
            final Predicate<String> typeCheck = entry.getSecond();
            final Predicate<String> optionCheck = entry.getThird();
            if (typeCheck.test(argumentType) && optionCheck.test(originalName) && result.toLowerCase().contains(original.toLowerCase())) {
                Constants.LOG.debugInfo("Replacing statement in property \"%1$s\" (%2$s): \"%3$s\" => \"%4$s\"", originalName, argumentType, original, newValue);
                result = StringUtils.replace(result, original, newValue, false, false, true); // v1 Placeholders were case-insensitive
            }
        }

        final List<String> operatorMatches = StringUtils.getMatches("\\{[^{}]*}[|]\\{[^{}]*}", result);
        if (!operatorMatches.isEmpty()) {
            for (String match : operatorMatches) {
                final String[] split = match.split("\\|");
                split[0] = split[0].replaceAll("[{}]", "");
                split[1] = split[1].replaceAll("[{}]", "");
                final String replacement = String.format("{getOrDefault(%1$s, %2$s)}", split[0], split[1]);
                Constants.LOG.debugInfo("Replacing statement in property \"%1$s\" (%2$s): \"%3$s\" => \"%4$s\"", originalName, argumentType, match, replacement);
                result = result.replace(match, replacement);
            }
        }
        return result;
    }

    private Tuple<Pair<String, String>, Predicate<String>, Predicate<String>> generatePair(final String original, final String name, final Predicate<String> typeCheck, final Predicate<String> optionCheck) {
        return new Tuple<>(new Pair<>(original, name), typeCheck, optionCheck);
    }

    private Tuple<Pair<String, String>, Predicate<String>, Predicate<String>> generatePair(final String original, final String name, final Predicate<String> typeCheck) {
        return generatePair(original, name, typeCheck, (e) -> true);
    }
}
