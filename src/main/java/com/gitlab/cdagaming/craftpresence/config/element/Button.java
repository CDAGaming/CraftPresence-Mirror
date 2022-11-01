package com.gitlab.cdagaming.craftpresence.config.element;

import com.gitlab.cdagaming.craftpresence.config.Module;
import com.gitlab.cdagaming.craftpresence.config.category.Status;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;

import java.io.Serializable;

public class Button extends Module implements Serializable {
    private static final long serialVersionUID = 636718807992670138L;
    private static Button DEFAULT;

    public String label;
    public String url;

    public Button(String label, String url) {
        this.label = label;
        this.url = url;
    }

    public static Button getDefaults() {
        if (DEFAULT == null) {
            DEFAULT = new Button("", "");
        }
        return DEFAULT;
    }

    @Override
    public Object getProperty(final String name) {
        return StringUtils.lookupObject(Button.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(Button.class, this, new Tuple<>(name, value, null));
    }

    @Override
    public void resetProperty(final String name) {
        setProperty(name, getDefaults().getProperty(name));
    }
}
