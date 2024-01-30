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

package com.gitlab.cdagaming.craftpresence.core.utils;

import com.gitlab.cdagaming.craftpresence.core.Constants;
import io.github.cdagaming.unicore.integrations.versioning.VersionComparator;
import io.github.cdagaming.unicore.utils.FileUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import io.github.cdagaming.unicore.utils.UrlUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Set of Utilities used for Retrieving and Alerting on Any Mod Updates
 *
 * @author CDAGaming
 */
public class ModUpdaterUtils {
    /**
     * The Current Update State for this Mod Updater Instance
     */
    public UpdateState currentState = UpdateState.PENDING;
    /**
     * The MOD ID attached to this Mod Updater Instance
     */
    public String modID;
    /**
     * The Update URL to retrieve Updates from in this Instance
     */
    public String updateUrl;
    /**
     * The Download Url, references as the "homepage" element in the Json
     */
    public String downloadUrl;
    /**
     * The Target Latest/Unstable Version for this Instance, based on retrieved data
     */
    public String targetLatestVersion;
    /**
     * The Target Recommended/Stable Version for this Instance, based on retrieved data
     */
    public String targetRecommendedVersion;
    /**
     * The Target Main Version for this Instance, dependent on Update State
     */
    public String targetVersion;
    /**
     * The Changelog Data attached to the Target Version, if any
     */
    public Map<String, String> changelogData = new LinkedHashMap<>();
    /**
     * The Current Version attached to this Instance
     */
    public String currentVersion;
    /**
     * The Current Game Version attached to this Instance
     */
    public String currentGameVersion;

    /**
     * Initializes this Module from the Specified Arguments
     *
     * @param modID              The Mod ID for this Instance
     * @param updateUrl          The Update Url to attach to this Instance
     * @param currentVersion     The Current Version to attach to this Instance
     * @param currentGameVersion The Current Game Version to attach to this Instance
     */
    public ModUpdaterUtils(final String modID, final String updateUrl, final String currentVersion, final String currentGameVersion) {
        this.modID = modID;
        this.updateUrl = updateUrl;
        this.currentGameVersion = currentGameVersion;
        this.currentVersion = currentVersion;
    }

    /**
     * Clears Runtime Client Data from this Module
     */
    public void flushData() {
        currentState = UpdateState.PENDING;
        targetRecommendedVersion = "";
        targetLatestVersion = "";
        targetVersion = "";
        downloadUrl = "";
        changelogData.clear();
    }

    /**
     * Checks for Updates, updating the Current Update Check State
     */
    public void checkForUpdates() {
        checkForUpdates(null);
    }

    /**
     * Checks for Updates, updating the Current Update Check State
     *
     * @param callback The callback to run after Update Events
     */
    public void checkForUpdates(final Runnable callback) {
        FileUtils.getThreadFactory().newThread(() -> process(callback)).start();
    }

    private void process(final Runnable callback) {
        try {
            flushData();

            if (StringUtils.isNullOrEmpty(updateUrl)) return;
            Constants.LOG.info(Constants.TRANSLATOR.translate("craftpresence.logger.info.updater.init", modID, currentGameVersion, updateUrl));

            final String data = UrlUtils.getURLText(updateUrl, "UTF-8");

            Constants.LOG.debugInfo(Constants.TRANSLATOR.translate("craftpresence.logger.info.updater.receive.data", data));

            @SuppressWarnings("unchecked") final Map<String, Object> json = FileUtils.getJsonData(data, Map.class);
            @SuppressWarnings("unchecked") final Map<String, String> promos = (Map<String, String>) json.get("promos");
            downloadUrl = (String) json.get("homepage");

            targetRecommendedVersion = promos.get(currentGameVersion + "-recommended");
            targetLatestVersion = promos.get(currentGameVersion + "-latest");

            final VersionComparator cmp = new VersionComparator();
            final boolean hasRecommended = !StringUtils.isNullOrEmpty(targetRecommendedVersion);
            final boolean hasLatest = !StringUtils.isNullOrEmpty(targetLatestVersion);

            if (hasRecommended) {
                final int diff = cmp.compare(targetRecommendedVersion, currentVersion);

                if (diff == 0)
                    currentState = UpdateState.UP_TO_DATE;
                else if (diff < 0) {
                    currentState = UpdateState.AHEAD;
                    if (hasLatest && cmp.compare(currentVersion, targetLatestVersion) < 0) {
                        currentState = UpdateState.OUTDATED;
                        targetVersion = targetLatestVersion;
                    }
                } else {
                    currentState = UpdateState.OUTDATED;
                    targetVersion = targetRecommendedVersion;
                }
            } else if (hasLatest) {
                if (cmp.compare(currentVersion, targetLatestVersion) < 0)
                    currentState = UpdateState.BETA_OUTDATED;
                else
                    currentState = UpdateState.BETA;
                targetVersion = targetLatestVersion;
            } else
                currentState = UpdateState.BETA;

            Constants.LOG.info(Constants.TRANSLATOR.translate("craftpresence.logger.info.updater.receive.status", modID, currentState.getDisplayName(), targetVersion));

            changelogData.clear();
            @SuppressWarnings("unchecked") final Map<String, String> tmp = (Map<String, String>) json.get(currentGameVersion);
            if (tmp != null) {
                final List<String> ordered = StringUtils.newArrayList();
                for (String key : tmp.keySet()) {
                    if (cmp.compare(key, currentVersion) > 0 && (StringUtils.isNullOrEmpty(targetVersion) || cmp.compare(key, targetVersion) < 1)) {
                        ordered.add(key);
                    }
                }
                Collections.sort(ordered);

                for (String ver : ordered) {
                    changelogData.put(ver, tmp.get(ver));
                }
            }
        } catch (Throwable ex) {
            // Log Failure and Set Update State to FAILED
            Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.updater.failed"));
            Constants.LOG.debugError(ex);
            currentState = UpdateState.FAILED;
        } finally {
            if (callback != null) {
                callback.run();
            }
        }
    }

    /**
     * Mapping for CFU State (Based on <a href="https://docs.minecraftforge.net/en/latest/misc/updatechecker/">Forge Systems</a>)
     *
     * <p>FAILED: The version checker could not connect to the URL provided.
     * <p>UP_TO_DATE: The current version is equal to the recommended version.
     * <p>AHEAD: The current version is newer than the recommended version if there is not latest version.
     * <p>OUTDATED: There is a new recommended or latest version.
     * <p>BETA_OUTDATED: There is a new latest version.
     * <p>BETA: The current version is equal to or newer than the latest version.
     * <p>PENDING: The result requested has not finished yet, so you should try again in a little bit.
     */
    public enum UpdateState {
        /**
         * The CFU State representing a "Failed" status
         */
        FAILED,
        /**
         * The CFU State representing an "Up to Date" status
         */
        UP_TO_DATE("Release"),
        /**
         * The CFU State representing an "Ahead" status
         */
        AHEAD,
        /**
         * The CFU State representing an "Outdated" status
         */
        OUTDATED,
        /**
         * The CFU State representing an "Outdated Beta" status
         */
        BETA_OUTDATED("Beta (Outdated)"),
        /**
         * The CFU State representing a "Beta" status
         */
        BETA,
        /**
         * The CFU State representing a "Pending" status
         */
        PENDING;

        final String displayName;

        UpdateState() {
            displayName = StringUtils.formatWord(name().toLowerCase());
        }

        UpdateState(final String displayName) {
            this.displayName = displayName;
        }

        /**
         * Retrieves the display name for the specified {@link UpdateState}
         *
         * @return The display name corresponding to the {@link UpdateState}
         */
        public String getDisplayName() {
            return displayName;
        }
    }
}
