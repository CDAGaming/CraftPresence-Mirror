package com.gitlab.cdagaming.craftpresence.config.element;

import java.io.Serializable;

public class Button implements Serializable {
    private static final long serialVersionUID = 636718807992670138L;

    public String label;
    public String url;

    public Button(String label, String url) {
        this.label = label;
        this.url = url;
    }
}
