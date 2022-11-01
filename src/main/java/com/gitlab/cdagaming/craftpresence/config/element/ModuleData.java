package com.gitlab.cdagaming.craftpresence.config.element;

import com.gitlab.cdagaming.craftpresence.config.Module;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;

import java.io.Serializable;

public class ModuleData extends Module implements Serializable {
    private static final long serialVersionUID = -5846802181463006850L;
    private static ModuleData DEFAULT;

    private String textOverride;
    private String iconOverride;
    private PresenceData data;

    public ModuleData(String textOverride, String iconOverride, PresenceData data) {
        this.textOverride = textOverride;
        this.iconOverride = iconOverride;
        this.data = data;
    }

    public ModuleData(String textOverride, String iconOverride) {
        this(textOverride, iconOverride, new PresenceData());
    }

    public ModuleData() {
        this("", "");
    }

    public String getTextOverride() {
        return textOverride;
    }

    public ModuleData setTextOverride(String textOverride) {
        this.textOverride = textOverride;
        return this;
    }

    public String getIconOverride() {
        return iconOverride;
    }

    public ModuleData setIconOverride(String iconOverride) {
        this.iconOverride = iconOverride;
        return this;
    }

    public PresenceData getData() {
        return data;
    }

    public void setData(PresenceData data) {
        this.data = data;
    }

    public static ModuleData getDefaults() {
        if (DEFAULT == null) {
            DEFAULT = new ModuleData();
        }
        return DEFAULT;
    }

    @Override
    public Object getProperty(final String name) {
        return StringUtils.lookupObject(ModuleData.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(ModuleData.class, this, new Tuple<>(name, value, null));
    }

    @Override
    public void resetProperty(final String name) {
        setProperty(name, getDefaults().getProperty(name));
    }
}
