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

package com.gitlab.cdagaming.craftpresence.config.category;

import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.Module;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import io.github.cdagaming.unicore.utils.StringUtils;

import java.io.Serializable;
import java.util.Objects;

public class Status extends Module implements Serializable {
    private static final long serialVersionUID = 3055410101315942491L;
    private static final Status DEFAULT = new Status();
    public ModuleData mainMenuData = new ModuleData(
            Constants.TRANSLATOR.translate("craftpresence.defaults.state.main_menu"),
            null
    );
    public ModuleData loadingData = new ModuleData(
            Constants.TRANSLATOR.translate("craftpresence.defaults.state.loading"),
            null
    );
    public ModuleData lanData = new ModuleData(
            Constants.TRANSLATOR.translate("craftpresence.defaults.state.lan"),
            null
    );
    public ModuleData singleplayerData = new ModuleData(
            Constants.TRANSLATOR.translate("craftpresence.defaults.state.single_player"),
            null
    );

    public Status(final Status other) {
        transferFrom(other);
    }

    public Status() {
        // N/A
    }

    @Override
    public Status getDefaults() {
        return new Status(DEFAULT);
    }

    @Override
    public Status copy() {
        return new Status(this);
    }

    @Override
    public void transferFrom(Module target) {
        if (target instanceof Status && !equals(target)) {
            final Status data = (Status) target;

            mainMenuData = new ModuleData(data.mainMenuData);
            loadingData = new ModuleData(data.loadingData);
            lanData = new ModuleData(data.lanData);
            singleplayerData = new ModuleData(data.singleplayerData);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Status)) {
            return false;
        }

        final Status other = (Status) obj;

        return Objects.equals(other.mainMenuData, mainMenuData) &&
                Objects.equals(other.loadingData, loadingData) &&
                Objects.equals(other.lanData, lanData) &&
                Objects.equals(other.singleplayerData, singleplayerData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                mainMenuData, loadingData, lanData, singleplayerData
        );
    }
}
