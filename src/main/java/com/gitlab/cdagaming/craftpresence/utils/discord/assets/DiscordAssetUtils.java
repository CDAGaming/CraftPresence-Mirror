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

package com.gitlab.cdagaming.craftpresence.utils.discord.assets;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.UrlUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Utilities related to locating and Parsing available Discord Assets
 * <p>
 * Uses the current Client ID in use to locate Discord Icons and related Assets
 *
 * @author CDAGaming
 */
public class DiscordAssetUtils {
    /**
     * The endpoint url for the Discord Applications backend
     */
    private static final String applicationEndpoint = "https://discord.com/api/oauth2/applications/";
    /**
     * The endpoint url for the Discord Application Assets backend
     */
    private static final String assetsEndpoint = "https://cdn.discordapp.com/app-assets/";
    /**
     * A List of the Icon IDs available as ImageType SMALL
     */
    private static final List<String> SMALL_IDS = Lists.newArrayList();
    /**
     * A List of the Icon IDs available as ImageType LARGE
     */
    private static final List<String> LARGE_IDS = Lists.newArrayList();
    /**
     * A List of all the Icon IDs available within the Current Client ID
     */
    private static final List<String> ICON_IDS = Lists.newArrayList();
    /**
     * If the Asset Check had completed
     */
    public static boolean syncCompleted = false;
    /**
     * A List of the Icons available as ImageType SMALL
     */
    public static List<String> SMALL_ICONS = Lists.newArrayList();
    /**
     * A List of the Icons available as ImageType LARGE
     */
    public static List<String> LARGE_ICONS = Lists.newArrayList();
    /**
     * A List of all the Icons available within the Current Client ID
     */
    public static List<String> ICON_LIST = Lists.newArrayList();
    /**
     * A List of all the Icons available from dynamic data
     */
    public static List<String> CUSTOM_ICON_LIST = Lists.newArrayList();
    /**
     * Mapping storing the Icon Keys and Asset Data attached to the Current Client
     * ID
     */
    public static Map<String, DiscordAsset> ASSET_LIST = Maps.newHashMap();
    /**
     * Mapping storing the Icon Keys and Asset Data attached from dynamic data
     */
    public static Map<String, DiscordAsset> CUSTOM_ASSET_LIST = Maps.newHashMap();

    /**
     * Determines if the Specified Icon Key is present under the specified list
     *
     * @param list The list to iterate through
     * @param key The Specified Icon Key to Check
     * @return {@code true} if the Icon Key is present and able to be used
     */
    public static boolean contains(final Map<String, DiscordAsset> list, final String key) {
        final String formattedKey = StringUtils.isNullOrEmpty(key) ? ""
                : StringUtils.formatAsIcon(key.replace(" ", "_"));
        return list.containsKey(formattedKey);
    }

    /**
     * Determines if the Specified Icon Key is present under the Current Client ID
     *
     * @param key The Specified Icon Key to Check
     * @return {@code true} if the Icon Key is present and able to be used
     */
    public static boolean contains(final String key) {
        return contains(ASSET_LIST, key);
    }

    /**
     * Retrieves the Specified {@link DiscordAsset} data from an Icon Key, if
     * present
     *
     * @param list The list to iterate through
     * @param key The Specified Icon Key to gain info for
     * @return The {@link DiscordAsset} data for this Icon Key
     */
    public static DiscordAsset get(final Map<String, DiscordAsset> list, final String key) {
        final String formattedKey = StringUtils.isNullOrEmpty(key) ? ""
                : StringUtils.formatAsIcon(key.replace(" ", "_"));
        return contains(formattedKey) ? list.get(formattedKey) : null;
    }

    /**
     * Retrieves the Specified {@link DiscordAsset} data from an Icon Key, if
     * present
     *
     * @param key The Specified Icon Key to gain info for
     * @return The {@link DiscordAsset} data for this Icon Key
     */
    public static DiscordAsset get(final String key) {
        return get(ASSET_LIST, key);
    }

    /**
     * Retrieves the Parsed Icon Key from the specified key, if present
     *
     * @param list The list to iterate through
     * @param key The Specified Key to gain info for
     * @return The Parsed Icon Key from the {@link DiscordAsset} data
     */
    public static String getKey(final Map<String, DiscordAsset> list, final String key) {
        final DiscordAsset asset = get(list, key);
        return asset != null ? asset.getName() : "";
    }

    /**
     * Retrieves the Parsed Icon Key from the specified key, if present
     *
     * @param key The Specified Key to gain info for
     * @return The Parsed Icon Key from the {@link DiscordAsset} data
     */
    public static String getKey(final String key) {
        return getKey(ASSET_LIST, key);
    }

    /**
     * Retrieves the Parsed Icon ID from the specified key, if present
     *
     * @param list The list to iterate through
     * @param key The Specified Key to gain info for
     * @return The Parsed Icon ID from the {@link DiscordAsset} data
     */
    public static String getId(final Map<String, DiscordAsset> list, final String key) {
        final DiscordAsset asset = get(list, key);
        return asset != null ? asset.getId() : "";
    }

    /**
     * Retrieves the Parsed Icon ID from the specified key, if present
     *
     * @param key The Specified Key to gain info for
     * @return The Parsed Icon ID from the {@link DiscordAsset} data
     */
    public static String getId(final String key) {
        return getId(ASSET_LIST, key);
    }

    /**
     * Retrieves the Parsed Image Type from the specified key, if present
     *
     * @param list The list to iterate through
     * @param key The Specified Key to gain info for
     * @return The Parsed Image Type from the {@link DiscordAsset} data
     */
    public static DiscordAsset.AssetType getType(final Map<String, DiscordAsset> list, final String key) {
        final DiscordAsset asset = get(list, key);
        return asset != null ? asset.getType() : DiscordAsset.AssetType.LARGE;
    }

    /**
     * Retrieves the Parsed Image Type from the specified key, if present
     *
     * @param key The Specified Key to gain info for
     * @return The Parsed Image Type from the {@link DiscordAsset} data
     */
    public static DiscordAsset.AssetType getType(final String key) {
        return getType(ASSET_LIST, key);
    }

    /**
     * Retrieves the Parsed Image Url from the specified key, if present
     *
     * @param key The Specified Key to gain info for
     * @return The Parsed Image Url from the {@link DiscordAsset} data
     */
    public static String getUrl(final Map<String, DiscordAsset> list, final String key) {
        final DiscordAsset asset = get(list, key);
        if (asset != null) {
            if (!StringUtils.isNullOrEmpty(asset.getId())) {
                return getDiscordAssetUrl(asset.getName());
            } else {
                return asset.getUrl();
            }
        }
        return "";
    }

    /**
     * Retrieves the Parsed Image Url from the specified key, if present
     *
     * @param key The Specified Key to gain info for
     * @return The Parsed Image Url from the {@link DiscordAsset} data
     */
    public static String getUrl(final String key) {
        return getUrl(ASSET_LIST, key);
    }

    /**
     * Clears FULL Data from this Module
     */
    public static void emptyData() {
        ASSET_LIST.clear();
        SMALL_ICONS.clear();
        SMALL_IDS.clear();
        LARGE_ICONS.clear();
        LARGE_IDS.clear();
        ICON_LIST.clear();
        ICON_IDS.clear();
        CUSTOM_ASSET_LIST.clear();
        CUSTOM_ICON_LIST.clear();

        clearClientData();
    }

    /**
     * Clears Runtime Client Data from this Module (PARTIAL Clear)
     */
    public static void clearClientData() {
        syncCompleted = false;
    }

    /**
     * Attempts to retrieve a Random Icon Key from the available assets
     *
     * @return A Randomly retrieved Icon Key, if found
     */
    public static String getRandomAsset() {
        try {
            final Random randomObj = new Random();
            return ICON_LIST.get(randomObj.nextInt(ICON_LIST.size()));
        } catch (Exception ex) {
            ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.error.config.invalid.icon.empty"));
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * Attempts to retrieve the Asset Url from the specified icon key, if present
     * <p>
     * Url Format: [assetsEndpoint]/[clientId]/[id].png
     *
     * @param clientId    The client id to load asset data from
     * @param keyId       The Specified Key ID to gain info for (Can only be a key name if isLocalName is true)
     * @param isLocalName Whether the specified Key ID is a Key name derived from the currently synced client id
     * @return The asset url in String form (As in Url form, it'll only work if it is a valid Client Id)
     */
    public static String getDiscordAssetUrl(final String clientId, final String keyId, final boolean isLocalName) {
        return !StringUtils.isNullOrEmpty(keyId) ? assetsEndpoint
                + clientId + "/" + (isLocalName ? getId(keyId) : keyId) + ".png" : "";
    }

    /**
     * Attempts to retrieve the Asset Url from the specified icon key, if present
     * <p>
     * Url Format: [assetsEndpoint]/[clientId]/[id].png
     *
     * @param clientId The client id to load asset data from
     * @param keyId    The Specified Key ID to gain info for (Can only be a key name if isLocalName is true)
     * @return The asset url in String form (As in Url form, it'll only work if it is a valid Client Id)
     */
    public static String getDiscordAssetUrl(final String clientId, final String keyId) {
        return getDiscordAssetUrl(clientId, keyId, clientId.equals(CraftPresence.CONFIG.clientId));
    }

    /**
     * Attempts to retrieve the Asset Url from the specified icon key, if present
     * <p>
     * Url Format: [assetsEndpoint]/[clientId]/[id].png
     *
     * @param keyId The Specified Key ID to gain info for (Can only be a key name if isLocalName is true)
     * @return The asset url in String form (As in Url form, it'll only work if it is a valid Client Id)
     */
    public static String getDiscordAssetUrl(final String keyId) {
        return getDiscordAssetUrl(CraftPresence.CONFIG.clientId, keyId);
    }

    /**
     * Retrieves and Synchronizes the List of Available Discord Assets from the Client ID
     * <p>
     * Default Url Format: [applicationEndpoint]/[clientId]/assets
     *
     * @param clientId     The client id to load asset data from
     * @param filterToMain Whether this client id is submitting its assets as the assets to use in CraftPresence
     * @return The list of discord asset data attached to this client id
     */
    public static DiscordAsset[] loadAssets(final String clientId, final boolean filterToMain) {
        ModUtils.LOG.info(ModUtils.TRANSLATOR.translate("craftpresence.logger.info.discord.assets.load", clientId));
        ModUtils.LOG.info(ModUtils.TRANSLATOR.translate("craftpresence.logger.info.discord.assets.load.credits"));

        try {
            final String url = applicationEndpoint + clientId + "/assets";
            final DiscordAsset[] assets = UrlUtils.getJSONFromURL(url, DiscordAsset[].class);

            if (filterToMain) {
                // Setup Data
                ASSET_LIST = Maps.newHashMap();
                CUSTOM_ASSET_LIST = Maps.newHashMap();
                if (assets != null) {
                    for (DiscordAsset asset : assets) {
                        // Ensure URL is set before-hand for non-custom Assets
                        // localName set to false to avoid unneeded calls
                        if (!StringUtils.isNullOrEmpty(asset.getUrl()) && asset.getType() != DiscordAsset.AssetType.CUSTOM) {
                            asset.setUrl(getDiscordAssetUrl(clientId, asset.getId(), false));
                        }
                        if (asset.getType().equals(DiscordAsset.AssetType.LARGE)) {
                            if (!LARGE_ICONS.contains(asset.getName())) {
                                LARGE_ICONS.add(asset.getName());
                            }
                            if (!LARGE_IDS.contains(asset.getId())) {
                                LARGE_IDS.add(asset.getId());
                            }
                        }
                        if (asset.getType().equals(DiscordAsset.AssetType.SMALL)) {
                            if (!SMALL_ICONS.contains(asset.getName())) {
                                SMALL_ICONS.add(asset.getName());
                            }
                            if (!SMALL_IDS.contains(asset.getId())) {
                                SMALL_IDS.add(asset.getId());
                            }
                        }
                        if (asset.getType().equals(DiscordAsset.AssetType.CUSTOM)) {
                            if (!CUSTOM_ICON_LIST.contains(asset.getName())) {
                                CUSTOM_ICON_LIST.add(asset.getName());
                            }
                        }
                        if (!ICON_LIST.contains(asset.getName())) {
                            ICON_LIST.add(asset.getName());
                        }
                        if (!ASSET_LIST.containsKey(asset.getName())) {
                            ASSET_LIST.put(asset.getName(), asset);
                        }
                        if (!ICON_IDS.contains(asset.getId())) {
                            ICON_IDS.add(asset.getId());
                        }
                    }
                }

                for (String iconData : CraftPresence.CONFIG.dynamicIcons) {
                    if (!StringUtils.isNullOrEmpty(iconData)) {
                        final String[] part = iconData.split(CraftPresence.CONFIG.splitCharacter);
                        if (!StringUtils.isNullOrEmpty(part[0]) && !StringUtils.isNullOrEmpty(part[1])) {
                            DiscordAsset asset = new DiscordAsset()
                                    .setName(part[0])
                                    .setUrl(part[1])
                                    .setType(DiscordAsset.AssetType.CUSTOM);

                            if (!CUSTOM_ICON_LIST.contains(asset.getName())) {
                                CUSTOM_ICON_LIST.add(asset.getName());
                            }
                            if (!CUSTOM_ASSET_LIST.containsKey(asset.getName())) {
                                CUSTOM_ASSET_LIST.put(asset.getName(), asset);
                            }
                            // If a Discord Icon exists with the same name, give priority to the custom one
                            // Unless the icon is the default template, in which we don't add it at all
                            if (!asset.getName().equalsIgnoreCase("default")) {
                                ICON_LIST.remove(asset.getName());
                                ICON_LIST.add(asset.getName());
                                ASSET_LIST.put(asset.getName(), asset);
                            }
                        }
                    }
                }
            }
            return assets;
        } catch (Exception ex) {
            ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.error.discord.assets.load"));
            ex.printStackTrace();

            return null;
        } finally {
            verifyConfigAssets();
            syncCompleted = true;
            ModUtils.LOG.info(ModUtils.TRANSLATOR.translate("craftpresence.logger.info.discord.assets.detected", String.valueOf(ASSET_LIST.size())));
        }
    }

    /**
     * Ensures any Default Icons in the Config exist within the Client ID
     */
    private static void verifyConfigAssets() {
        boolean needsFullUpdate = false;
        for (String property : CraftPresence.CONFIG.properties.stringPropertyNames()) {
            if ((property.equals(CraftPresence.CONFIG.NAME_defaultIcon) || property.equals(CraftPresence.CONFIG.NAME_defaultDimensionIcon) || property.equals(CraftPresence.CONFIG.NAME_defaultServerIcon)) && !contains(CraftPresence.CONFIG.properties.getProperty(property))) {
                final String newAsset = contains(CraftPresence.CONFIG.defaultIcon) ? CraftPresence.CONFIG.defaultIcon : getRandomAsset();
                ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.error.config.invalid.icon.pre", CraftPresence.CONFIG.properties.getProperty(property), property));
                CraftPresence.CONFIG.properties.setProperty(property, newAsset);
                needsFullUpdate = true;
                ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.error.config.invalid.icon.post", property, newAsset));
            }
        }

        if (needsFullUpdate) {
            CraftPresence.CONFIG.save("UTF-8");
            CraftPresence.CONFIG.read(false, "UTF-8");
        }
    }
}
