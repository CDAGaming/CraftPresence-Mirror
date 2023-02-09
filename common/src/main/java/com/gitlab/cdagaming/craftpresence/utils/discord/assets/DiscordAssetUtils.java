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

package com.gitlab.cdagaming.craftpresence.utils.discord.assets;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.SystemUtils;
import com.gitlab.cdagaming.craftpresence.utils.UrlUtils;
import com.google.common.collect.Maps;

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
     * If the Asset Check had completed
     */
    public static boolean syncCompleted = false;
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
     * Determines if the specified Client ID is valid
     *
     * @param clientId The id to interpret
     * @return {@link Boolean#TRUE} if the client ID is valid
     */
    public static boolean isValidId(final String clientId) {
        return !StringUtils.isNullOrEmpty(clientId) &&
                clientId.length() >= 18 &&
                StringUtils.getValidLong(clientId).getFirst();
    }

    /**
     * Determines if the Specified Icon Key is present under the specified list
     *
     * @param list The list to iterate through
     * @param key  The Specified Icon Key to Check
     * @return {@link Boolean#TRUE} if the Icon Key is present and able to be used
     */
    public static boolean contains(final Map<String, DiscordAsset> list, final String key) {
        return !StringUtils.isNullOrEmpty(key) && list.containsKey(key);
    }

    /**
     * Determines if the Specified Icon Key is present under the Current Client ID
     *
     * @param key The Specified Icon Key to Check
     * @return {@link Boolean#TRUE} if the Icon Key is present and able to be used
     */
    public static boolean contains(final String key) {
        return contains(ASSET_LIST, key);
    }

    /**
     * Determines if the Specified Icon Key is present under the Custom Assets List
     *
     * @param key The Specified Icon Key to Check
     * @return {@link Boolean#TRUE} if the Icon Key is present and able to be used
     */
    public static boolean isCustom(final String key) {
        return contains(CUSTOM_ASSET_LIST, key);
    }

    /**
     * Retrieves the Specified {@link DiscordAsset} data from an Icon Key, if
     * present
     *
     * @param list The list to iterate through
     * @param key  The Specified Icon Key to gain info for
     * @return The {@link DiscordAsset} data for this Icon Key
     */
    public static DiscordAsset get(final Map<String, DiscordAsset> list, final String key) {
        String formattedKey = key;
        if (!StringUtils.isNullOrEmpty(formattedKey)) {
            if (!list.equals(CUSTOM_ASSET_LIST) && !isCustom(formattedKey)) {
                formattedKey = StringUtils.formatAsIcon(formattedKey, "_");
            }

            if (contains(list, formattedKey)) {
                return list.get(formattedKey);
            }
        }
        return null;
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
     * @param key  The Specified Key to gain info for
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
     * @param key  The Specified Key to gain info for
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
     * @param key  The Specified Key to gain info for
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
     * @param list The list to iterate through
     * @param key  The Specified Key to gain info for
     * @return The Parsed Image Url from the {@link DiscordAsset} data
     */
    public static String getUrl(final Map<String, DiscordAsset> list, final String key) {
        final DiscordAsset asset = get(list, key);
        if (asset != null) {
            if (!StringUtils.isNullOrEmpty(asset.getId())) {
                return getDiscordAssetUrl(asset.getName());
            } else {
                return CraftPresence.CLIENT.getResult(asset.getUrl());
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
        CUSTOM_ASSET_LIST.clear();

        clearClientData();
    }

    /**
     * Clears Runtime Client Data from this Module (PARTIAL Clear)
     */
    public static void clearClientData() {
        syncCompleted = false;
    }

    /**
     * Attempts to retrieve a Random Icon from the available assets
     *
     * @return A Randomly retrieved Icon, if found
     */
    public static DiscordAsset getRandomAsset() {
        try {
            DiscordAsset[] values = ASSET_LIST.values().toArray(new DiscordAsset[0]);
            return values[SystemUtils.RANDOM.nextInt(values.length)];
        } catch (Exception ex) {
            ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.error.config.invalid.icon.empty"));
            if (ModUtils.IS_VERBOSE) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Attempts to retrieve a Random Icon Key from the available assets
     *
     * @return A Randomly retrieved Icon Key, if found
     */
    public static String getRandomAssetName() {
        final DiscordAsset randAsset = getRandomAsset();
        return randAsset != null ? randAsset.getName() : "";
    }

    /**
     * Attempts to retrieve the Asset Url from the specified icon key, if present
     * <p>
     * Url Format: [assetsEndpoint]/[clientId]/[id].png
     *
     * @param clientId    The client id to load asset data from
     * @param keyId       The Specified Key ID to gain info for (Can only be a key name if isLocalName is true)
     * @param isLocalName Whether the specified Key ID is a Key name derived from the currently synced Client ID
     * @return The asset url in String form (As in Url form, it'll only work if it is a valid Client ID)
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
     * @return The asset url in String form (As in Url form, it'll only work if it is a valid Client ID)
     */
    public static String getDiscordAssetUrl(final String clientId, final String keyId) {
        return getDiscordAssetUrl(clientId, keyId, clientId.equals(CraftPresence.CONFIG.generalSettings.clientId));
    }

    /**
     * Attempts to retrieve the Asset Url from the specified icon key, if present
     * <p>
     * Url Format: [assetsEndpoint]/[clientId]/[id].png
     *
     * @param keyId The Specified Key ID to gain info for (Can only be a key name if isLocalName is true)
     * @return The asset url in String form (As in Url form, it'll only work if it is a valid Client ID)
     */
    public static String getDiscordAssetUrl(final String keyId) {
        return getDiscordAssetUrl(CraftPresence.CONFIG.generalSettings.clientId, keyId);
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
                if (assets != null) {
                    for (DiscordAsset asset : assets) {
                        // Ensure URL is set beforehand for non-custom Assets
                        // isLocalName is made false to avoid unneeded calls
                        if (!StringUtils.isNullOrEmpty(asset.getUrl()) && asset.getType() != DiscordAsset.AssetType.CUSTOM) {
                            asset.setUrl(getDiscordAssetUrl(clientId, asset.getId(), false));
                        }
                        if (!ASSET_LIST.containsKey(asset.getName())) {
                            ASSET_LIST.put(asset.getName(), asset);
                        }
                    }
                }
                syncCustomAssets();
            }
            return assets;
        } catch (Exception ex) {
            ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.error.discord.assets.load"));
            if (ModUtils.IS_VERBOSE) {
                ex.printStackTrace();
            }
            return null;
        } finally {
            syncCompleted = true;
            ModUtils.LOG.info(ModUtils.TRANSLATOR.translate("craftpresence.logger.info.discord.assets.detected", String.valueOf(ASSET_LIST.size())));
        }
    }

    /**
     * Synchronize and detect any dynamic assets available for this instance
     */
    public static void syncCustomAssets() {
        CUSTOM_ASSET_LIST = Maps.newHashMap();
        for (Map.Entry<String, String> iconData : CraftPresence.CONFIG.displaySettings.dynamicIcons.entrySet()) {
            if (!StringUtils.isNullOrEmpty(iconData.getKey()) && !StringUtils.isNullOrEmpty(iconData.getValue())) {
                final DiscordAsset asset = new DiscordAsset()
                        .setName(iconData.getKey())
                        .setUrl(iconData.getValue())
                        .setType(DiscordAsset.AssetType.CUSTOM);
                if (!CUSTOM_ASSET_LIST.containsKey(asset.getName())) {
                    CUSTOM_ASSET_LIST.put(asset.getName(), asset);
                }
                // If a Discord Icon exists with the same name, give priority to the custom one
                // Unless the icon is the default template, in which we don't add it at all
                if (!asset.getName().equalsIgnoreCase("default")) {
                    ASSET_LIST.put(asset.getName(), asset);
                }
            }
        }
    }
}
