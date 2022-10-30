package com.gitlab.cdagaming.craftpresence.config.element;

import java.io.Serializable;

public class ModuleData implements Serializable {
    private static final long serialVersionUID = -5846802181463006850L;

    private String textOverride;
    private String iconOverride;
    private PresenceData data;

    public ModuleData(String textOverride, String iconOverride) {
        this.textOverride = textOverride;
        this.iconOverride = iconOverride;
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
}
