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

package com.gitlab.cdagaming.craftpresence.core.integrations.discord.assets;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The Json Parsing Syntax for a Discord Asset
 *
 * @author CDAGaming
 */
public class DiscordAsset {
    /**
     * The {@link AssetType} of this Asset
     */
    @SerializedName("type")
    @Expose
    private AssetType type;

    /**
     * The Parsed ID for this Asset
     * (Leave this empty if this is a custom asset)
     */
    @SerializedName("id")
    @Expose
    private String id;

    /**
     * The Parsed Name for this Asset
     */
    @SerializedName("name")
    @Expose
    private String name;

    /**
     * The Url for this asset
     * (This is optional, unless this is a custom asset)
     */
    private String url;

    /**
     * Retrieves the {@link AssetType} for this Asset
     *
     * @return The parsed {@link AssetType} for this Asset
     */
    public AssetType getType() {
        return type;
    }

    /**
     * Sets the {@link AssetType} for this Asset
     *
     * @param type The new {@link AssetType} to assign
     * @return The parsed {@link DiscordAsset}
     */
    public DiscordAsset setType(AssetType type) {
        this.type = type;
        return this;
    }

    /**
     * Retrieves the Parsed ID for this Asset
     *
     * @return The Parsed ID for this Asset
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the Parsed ID for this Asset
     *
     * @param id The new identifier to assign
     * @return The parsed {@link DiscordAsset}
     */
    public DiscordAsset setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Retrieves the Parsed Name for this Asset
     *
     * @return The Parsed Name for this Asset
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the Parsed Name for this Asset
     *
     * @param name The new name to assign
     * @return The parsed {@link DiscordAsset}
     */
    public DiscordAsset setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Retrieves the URL for this Asset
     *
     * @return The parsed URL for this Asset
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL for this Asset
     *
     * @param url The new url to assign
     * @return The parsed {@link DiscordAsset}
     */
    public DiscordAsset setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * Prints Asset Data as a Readable String
     *
     * @return A readable version of this Asset
     */
    @Override
    public String toString() {
        String result = "DiscordAsset{" + "type=" + getType();
        if (getType().equals(AssetType.CUSTOM)) {
            result += ", url='" + getUrl() + '\'';
        } else {
            result += ", id='" + getId() + '\'';
        }
        result += ", name='" + getName() + '\'' + '}';
        return result;
    }

    /**
     * A Mapping for the Parsed Asset Type for this Asset
     */
    public enum AssetType {
        /**
         * Constant for a "Small" Discord RPC Asset Type.
         */
        @SerializedName("1")
        @Expose
        SMALL,
        /**
         * Constant for a "Large" Discord RPC Asset Type.
         */
        @SerializedName("2")
        @Expose
        LARGE,
        /**
         * Constant for a "Custom" Discord RPC Asset Type.
         */
        @SerializedName("-999")
        @Expose
        CUSTOM
    }
}
