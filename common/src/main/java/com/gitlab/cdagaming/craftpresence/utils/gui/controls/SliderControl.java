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

package com.gitlab.cdagaming.craftpresence.utils.gui.controls;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.MathUtils;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;

/**
 * Gui Widget for a Movable Slider between a beginning and maximum value
 *
 * @author CDAGaming
 */
public class SliderControl extends ExtendedButtonControl {
    /**
     * The Minimum Value the Slider can reach
     */
    private final float minValue;

    /**
     * The Maximum Value the Slider can Reach
     */
    private final float maxValue;

    /**
     * The rate at which the Slider is able to move at on each move
     */
    private final float valueStep;
    /**
     * The Starting Slider Name to display as
     */
    private final String windowTitle;
    /**
     * The Normalized Slider Value between 0.0f and 1.0f
     */
    private float sliderValue;
    /**
     * The denormalized Slider value between the minimum and maximum values
     */
    private float denormalizedSlideValue;
    /**
     * Whether the Slider is currently being dragged
     */
    private boolean dragging;
    /**
     * The event to occur when sliding occurs
     */
    private Runnable onSlideEvent;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param buttonId      The ID for the control to Identify as
     * @param positionData  The Starting X and Y Positions to place the control in a Gui
     * @param dimensions    The Width and Height dimensions for the control
     * @param startValue    The Starting Value between the minimum and maximum value to set the slider at
     * @param minValue      The Minimum Value the Slider is allowed to be -- denormalized
     * @param maxValue      The Maximum Value the Slider is allowed to be -- denormalized
     * @param valueStep     The rate at which each move to the slider adjusts its value
     * @param displayString The title to display in the center of the slider
     */
    public SliderControl(int buttonId, Pair<Integer, Integer> positionData, Pair<Integer, Integer> dimensions, float startValue, float minValue, float maxValue, float valueStep, String displayString) {
        super(buttonId, positionData.getFirst(), positionData.getSecond(), dimensions.getFirst(), dimensions.getSecond(), "");

        setSliderValue(startValue);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.valueStep = valueStep;
        this.displayString = displayString + ": " + denormalizedSlideValue;
        this.windowTitle = displayString;
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param buttonId      The ID for the control to Identify as
     * @param positionData  The Starting X and Y Positions to place the control in a Gui
     * @param dimensions    The Width and Height dimensions for the control
     * @param startValue    The Starting Value between the minimum and maximum value to set the slider at
     * @param minValue      The Minimum Value the Slider is allowed to be -- denormalized
     * @param maxValue      The Maximum Value the Slider is allowed to be -- denormalized
     * @param valueStep     The rate at which each move to the slider adjusts its value
     * @param displayString The title to display in the center of the slider
     * @param onPushEvent   The Click Event to Occur when this control is clicked
     */
    public SliderControl(int buttonId, Pair<Integer, Integer> positionData, Pair<Integer, Integer> dimensions, float startValue, float minValue, float maxValue, float valueStep, String displayString, Runnable onPushEvent) {
        this(buttonId, positionData, dimensions, startValue, minValue, maxValue, valueStep, displayString);
        setOnClick(onPushEvent);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param buttonId      The ID for the control to Identify as
     * @param positionData  The Starting X and Y Positions to place the control in a Gui
     * @param dimensions    The Width and Height dimensions for the control
     * @param startValue    The Starting Value between the minimum and maximum value to set the slider at
     * @param minValue      The Minimum Value the Slider is allowed to be -- denormalized
     * @param maxValue      The Maximum Value the Slider is allowed to be -- denormalized
     * @param valueStep     The rate at which each move to the slider adjusts its value
     * @param displayString The title to display in the center of the slider
     * @param events        The events to occur when this control is modified
     */
    public SliderControl(int buttonId, Pair<Integer, Integer> positionData, Pair<Integer, Integer> dimensions, float startValue, float minValue, float maxValue, float valueStep, String displayString, Pair<Runnable, Runnable> events) {
        this(buttonId, positionData, dimensions, startValue, minValue, maxValue, valueStep, displayString, events.getFirst());
        setOnHover(events.getSecond());
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param positionData  The Starting X and Y Positions to place the control in a Gui
     * @param dimensions    The Width and Height dimensions for the control
     * @param startValue    The Starting Value between the minimum and maximum value to set the slider at
     * @param minValue      The Minimum Value the Slider is allowed to be -- denormalized
     * @param maxValue      The Maximum Value the Slider is allowed to be -- denormalized
     * @param valueStep     The rate at which each move to the slider adjusts its value
     * @param displayString The title to display in the center of the slider
     */
    public SliderControl(Pair<Integer, Integer> positionData, Pair<Integer, Integer> dimensions, float startValue, float minValue, float maxValue, float valueStep, String displayString) {
        this(CraftPresence.GUIS.getNextIndex(), positionData, dimensions, startValue, minValue, maxValue, valueStep, displayString, new Pair<>());
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param positionData  The Starting X and Y Positions to place the control in a Gui
     * @param dimensions    The Width and Height dimensions for the control
     * @param startValue    The Starting Value between the minimum and maximum value to set the slider at
     * @param minValue      The Minimum Value the Slider is allowed to be -- denormalized
     * @param maxValue      The Maximum Value the Slider is allowed to be -- denormalized
     * @param valueStep     The rate at which each move to the slider adjusts its value
     * @param displayString The title to display in the center of the slider
     * @param onPushEvent   The Click Event to Occur when this control is clicked
     */
    public SliderControl(Pair<Integer, Integer> positionData, Pair<Integer, Integer> dimensions, float startValue, float minValue, float maxValue, float valueStep, String displayString, Runnable onPushEvent) {
        this(positionData, dimensions, startValue, minValue, maxValue, valueStep, displayString);
        setOnClick(onPushEvent);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param positionData  The Starting X and Y Positions to place the control in a Gui
     * @param dimensions    The Width and Height dimensions for the control
     * @param startValue    The Starting Value between the minimum and maximum value to set the slider at
     * @param minValue      The Minimum Value the Slider is allowed to be -- denormalized
     * @param maxValue      The Maximum Value the Slider is allowed to be -- denormalized
     * @param valueStep     The rate at which each move to the slider adjusts its value
     * @param displayString The title to display in the center of the slider
     * @param events        The events to occur when this control is modified
     */
    public SliderControl(Pair<Integer, Integer> positionData, Pair<Integer, Integer> dimensions, float startValue, float minValue, float maxValue, float valueStep, String displayString, Pair<Runnable, Runnable> events) {
        this(positionData, dimensions, startValue, minValue, maxValue, valueStep, displayString, events.getFirst());
        setOnHover(events.getSecond());
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param positionData  The Starting X and Y Positions to place the control in a Gui
     * @param dimensions    The Width and Height dimensions for the control
     * @param startValue    The Starting Value between the minimum and maximum value to set the slider at
     * @param minValue      The Minimum Value the Slider is allowed to be -- denormalized
     * @param maxValue      The Maximum Value the Slider is allowed to be -- denormalized
     * @param valueStep     The rate at which each move to the slider adjusts its value
     * @param displayString The title to display in the center of the slider
     * @param events        The events to occur when this control is modified
     */
    public SliderControl(Pair<Integer, Integer> positionData, Pair<Integer, Integer> dimensions, float startValue, float minValue, float maxValue, float valueStep, String displayString, Tuple<Runnable, Runnable, Runnable> events) {
        this(positionData, dimensions, startValue, minValue, maxValue, valueStep, displayString, new Pair<>(events.getFirst(), events.getSecond()));
        setOnSlide(events.getThird());
    }

    /**
     * Returns the current Hover state of this control
     * <p>
     * 0 if the button is disabled<p>
     * 1 if the mouse is NOT hovering over this button<p>
     * 2 if it IS hovering over this button.
     */
    @Override
    protected int getHoverState(boolean mouseOver) {
        return 0;
    }

    /**
     * Fired when the mouse button is dragged.<p>
     * Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    @Override
    protected void mouseDragged(@Nonnull Minecraft mc, int mouseX, int mouseY) {
        if (visible) {
            if (dragging) {
                sliderValue = (float) (mouseX - (getX() + 4)) / (float) (getWidth() - 8);
                sliderValue = MathUtils.clamp(sliderValue, 0.0F, 1.0F);
                denormalizedSlideValue = MathUtils.denormalizeValue(sliderValue, valueStep, minValue, maxValue);

                setControlMessage(windowTitle + ": " + denormalizedSlideValue);
            }

            onSlide();
            final int hoverValue = (hovered ? 2 : 1) * 20;
            CraftPresence.GUIS.renderSlider(getX() + (int) (sliderValue * (float) (getWidth() - 8)), getY(), 0, 46 + hoverValue, 4, 20, zLevel, BUTTON_TEXTURES);
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control.<p>
     * Equivalent of MouseListener.mousePressed(MouseEvent e).
     */
    @Override
    public boolean mousePressed(@Nonnull Minecraft mc, int mouseX, int mouseY) {
        if (isOverScreen() && super.mousePressed(mc, mouseX, mouseY)) {
            sliderValue = (float) (mouseX - (getX() + 4)) / (float) (getWidth() - 8);
            sliderValue = MathUtils.clamp(sliderValue, 0.0F, 1.0F);
            denormalizedSlideValue = MathUtils.denormalizeValue(sliderValue, valueStep, minValue, maxValue);

            setControlMessage(windowTitle + ": " + denormalizedSlideValue);
            dragging = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Updates the Current Slider Value<p>
     * Note: Both Normalized and denormalized values are supported
     *
     * @param newValue The New Slider Value
     */
    public void setSliderValue(float newValue) {
        if (newValue >= 0.0f && newValue <= 1.0f) {
            sliderValue = newValue;
            denormalizedSlideValue = MathUtils.denormalizeValue(newValue, valueStep, minValue, maxValue);
        } else {
            sliderValue = MathUtils.normalizeValue(newValue, valueStep, minValue, maxValue);
            denormalizedSlideValue = newValue;
        }
        setControlMessage(windowTitle + ": " + denormalizedSlideValue);
    }

    /**
     * Retrieves the Current Normalized / denormalized Slider Value
     *
     * @param useNormal Whether to get the normalized value
     * @return The Current Normalized / denormalized Slider Value
     */
    public float getSliderValue(boolean useNormal) {
        return useNormal ? sliderValue : denormalizedSlideValue;
    }

    /**
     * Fired when the mouse button is released.<p>
     * Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        dragging = false;
    }

    /**
     * Sets the Event to occur upon Sliding
     *
     * @param event The event to occur
     */
    public void setOnSlide(Runnable event) {
        onSlideEvent = event;
    }

    /**
     * Triggers the onSlide event to occur
     */
    public void onSlide() {
        if (onSlideEvent != null) {
            onSlideEvent.run();
        }
    }

    /**
     * Retrieves whether the Slider is currently being manually dragged
     *
     * @return whether the Slider is currently being manually dragged
     */
    public boolean isDragging() {
        return dragging;
    }
}