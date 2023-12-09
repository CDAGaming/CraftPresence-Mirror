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
import com.gitlab.cdagaming.craftpresence.core.impl.LockObject;

import java.time.Instant;

/**
 * Utilities relating to Timing and Scheduling Tasks
 *
 * @author CDAGaming
 */
public class ScheduleUtils {
    /**
     * The minimum time to wait by default (In Seconds) before callbacks refresh
     */
    public static final int MINIMUM_REFRESH_RATE = 2;
    /**
     * An instance of {@link LockObject} to await certain tasks
     */
    public final LockObject TICK_LOCK = new LockObject();
    /**
     * The Current Time Remaining on the Timer
     */
    public int TIMER = 0;
    /**
     * The Current Epoch Unix Timestamp in Milliseconds
     */
    public Instant CURRENT_INSTANT;
    /**
     * The refresh rate for the callback event
     */
    private int refreshRate;
    /**
     * The event to perform every {@link ScheduleUtils#refreshRate}
     */
    private Runnable callbackEvent;
    /**
     * Whether the Timer is Currently Active
     */
    private boolean isTiming = false;
    /**
     * Whether the Callbacks related to the Mod have been refreshed
     * <p>
     * In this case, the RPC Updates every 2 Seconds with this check ensuring such
     */
    private boolean refreshedCallbacks = false;
    /**
     * The Beginning Unix Timestamp to count down from
     */
    private Instant BEGINNING_INSTANT;
    /**
     * The Elapsed Time since the application started (In Seconds)
     */
    private long ELAPSED_TIME;
    /**
     * The Last Timestamp at which the refresh rate was successfully passed (and related events ticked)
     */
    private long LAST_TICKED;

    /**
     * Initialize Timer Information
     *
     * @param rate  The refresh rate for the callback event
     * @param event The event to perform every {@link ScheduleUtils#refreshRate}
     */
    public ScheduleUtils(final int rate, final Runnable event) {
        CURRENT_INSTANT = TimeUtils.getCurrentTime();
        ELAPSED_TIME = 0;
        setRefreshRate(rate);
        setCallbackEvent(event);
        TICK_LOCK.unlock();
    }

    /**
     * Initialize Timer Information
     *
     * @param event The event to perform every {@link ScheduleUtils#refreshRate}
     */
    public ScheduleUtils(final Runnable event) {
        this(MINIMUM_REFRESH_RATE, event);
    }

    /**
     * The Event to Run on each Client Tick, if passed initialization events
     * <p>
     * Consists of Synchronizing Data, and Updating Timer-Related Data as needed
     */
    public void onTick() {
        ELAPSED_TIME = TimeUtils.getDurationFrom(CURRENT_INSTANT).getSeconds();

        if (TIMER > 0) {
            if (!isTiming) {
                startTimer();
            } else {
                checkTimer();
            }
        } else if (isTiming) {
            isTiming = false;
        }

        // Every <passTime> Seconds, refresh Callbacks and load state status
        if (LAST_TICKED != ELAPSED_TIME && ELAPSED_TIME % getRefreshRate() == 0) {
            if (!refreshedCallbacks) {
                refreshedCallbacks = true; // Signal awaiting postTick events
            }
        }
    }

    /**
     * The Event to Run on each Client Tick, after passing initialization events
     * <p>
     * Consists of Scheduling awaited tasks, after a successful {@link ScheduleUtils#onTick()}
     */
    public void postTick() {
        if (refreshedCallbacks) {
            try {
                TICK_LOCK.waitForUnlock((() -> {
                    if (getCallbackEvent() != null) {
                        getCallbackEvent().run();
                    }

                    LAST_TICKED = ELAPSED_TIME;
                    refreshedCallbacks = false;
                }));
            } catch (Exception ex) {
                Constants.LOG.error(ex);
            }
        }
    }

    /**
     * Retrieve the current rate at which to execute callbacks
     *
     * @return the current refresh rate
     */
    public int getRefreshRate() {
        return refreshRate;
    }

    /**
     * Sets the current rate at which to execute callbacks
     *
     * @param refreshRate the new refresh rate
     */
    public void setRefreshRate(final int refreshRate) {
        this.refreshRate = Math.max(MINIMUM_REFRESH_RATE, refreshRate);
    }

    /**
     * Gets the event to perform every {@link ScheduleUtils#refreshRate}
     *
     * @return the current callback event
     */
    public Runnable getCallbackEvent() {
        return callbackEvent;
    }

    /**
     * Sets the event to perform every {@link ScheduleUtils#refreshRate}
     *
     * @param callbackEvent the new callback event
     */
    public void setCallbackEvent(final Runnable callbackEvent) {
        this.callbackEvent = callbackEvent;
    }

    /**
     * Begins the Timer, counting down from {@link ScheduleUtils#BEGINNING_INSTANT}
     */
    private void startTimer() {
        BEGINNING_INSTANT = TimeUtils.getCurrentTime().plusSeconds(TIMER);
        isTiming = true;
    }

    /**
     * Determines the Remaining Time until 0, and Stops the Timer @ 0 remaining
     */
    private void checkTimer() {
        if (TIMER > 0) {
            final long remainingTime = BEGINNING_INSTANT.getEpochSecond() - TimeUtils.getCurrentTime().getEpochSecond();
            TIMER = (int) remainingTime;
        }
    }
}
