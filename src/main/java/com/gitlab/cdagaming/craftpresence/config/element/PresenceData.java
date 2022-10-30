package com.gitlab.cdagaming.craftpresence.config.element;

import java.io.Serializable;
import java.util.List;

public class PresenceData implements Serializable {
    private static final long serialVersionUID = -7560029890988753870L;

    public String details;
    public String gameState;
    public String largeImageKey;
    public String largeImageText;
    public String smallImageKey;
    public String smallImageText;
    public List<Button> buttons;

    public PresenceData setDetails(String details) {
        this.details = details;
        return this;
    }

    public PresenceData setGameState(String gameState) {
        this.gameState = gameState;
        return this;
    }

    public PresenceData setLargeImage(String imageKey, String imageText) {
        this.largeImageKey = imageKey;
        this.largeImageText = imageText;
        return this;
    }

    public PresenceData setSmallImage(String imageKey, String imageText) {
        this.smallImageKey = imageKey;
        this.smallImageText = imageText;
        return this;
    }

    public PresenceData addButton(Button button) {
        this.buttons.add(button);
        return this;
    }

    public PresenceData removeButton(Button button) {
        this.buttons.remove(button);
        return this;
    }
}
