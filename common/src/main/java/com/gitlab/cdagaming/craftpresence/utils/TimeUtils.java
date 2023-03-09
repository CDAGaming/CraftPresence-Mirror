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

package com.gitlab.cdagaming.craftpresence.utils;

import com.gitlab.cdagaming.craftpresence.impl.Pair;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

/**
 * Time String Utilities for interpreting and converting between differing Time Formats
 *
 * @author CDAGaming
 */
public class TimeUtils {
    /**
     * Format a Date String using the specified timezone and format.
     *
     * @param date       The {@link Instant} info to interpret.
     * @param toFormat   Target format string.
     * @param toTimeZone Target timezone string.
     * @return Date String in the target timezone and format.
     */
    public static String toString(final Instant date, final String toFormat, final String toTimeZone) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(toFormat);
        if (date != null && !StringUtils.isNullOrEmpty(toTimeZone)) {
            final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(
                    date, ZoneId.of("UTC")
            ).withZoneSameInstant(ZoneId.of(toTimeZone));
            return formatter.format(zonedDateTime);
        } else {
            return date != null ? formatter.format(date.atZone(ZoneOffset.UTC)) : "";
        }
    }

    /**
     * Format a Date String using the specified timezone and format.
     *
     * @param date     The {@link Instant} info to interpret.
     * @param toFormat Target format string.
     * @return Date String in the target timezone and format.
     */
    public static String toString(final Instant date, final String toFormat) {
        return toString(date, toFormat, null);
    }

    /**
     * Format a Date String from one timezone and format into a valid {@link Instant} instance.
     *
     * @param dateString   Date String in the original timezone and format.
     * @param fromFormat   Original format string.
     * @param fromTimeZone Original timezone string.
     * @return Date String in the target timezone and format.
     */
    public static Instant toInstance(final String dateString, final String fromFormat, final String fromTimeZone) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fromFormat);
            if (!StringUtils.isNullOrEmpty(fromTimeZone)) {
                formatter = formatter.withZone(ZoneId.of(fromTimeZone));
            }
            return formatter.parse(dateString, Instant::from);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Format a Date String from one timezone and format into a valid {@link Instant} instance.
     *
     * @param dateString Date String in the original timezone and format.
     * @param fromFormat Original format string.
     * @return Date String in the target timezone and format.
     */
    public static Instant toInstance(final String dateString, final String fromFormat) {
        return toInstance(dateString, fromFormat, null);
    }

    /**
     * Convert a Date String from one timezone to another timezone and format.
     *
     * @param dateString   Date String in the original timezone and format.
     * @param fromFormat   Original format string.
     * @param fromTimeZone Original timezone string.
     * @param toFormat     Target format string.
     * @param toTimeZone   Target timezone string.
     * @return Date String in the target timezone and format.
     */
    public static String convertTime(final String dateString, final String fromFormat, final String fromTimeZone, final String toFormat, final String toTimeZone) {
        return toString(toInstance(dateString, fromFormat, fromTimeZone), toFormat, toTimeZone);
    }

    /**
     * Convert a Date String from one format to another format.
     *
     * @param dateString Date String in the original format.
     * @param fromFormat Original format string.
     * @param toFormat   Target format string.
     * @return Date String in the target timezone and format.
     */
    public static String convertFormat(final String dateString, final String fromFormat, final String toFormat) {
        return convertTime(dateString, fromFormat, null, toFormat, null);
    }

    /**
     * Convert a Date String from one timezone to another timezone.
     *
     * @param dateString   Date String in the original timezone.
     * @param fromFormat   Original format string.
     * @param fromTimeZone Original timezone string.
     * @param toTimeZone   Target timezone string.
     * @return Date String in the target timezone and format.
     */
    public static String convertZone(final String dateString, final String fromFormat, final String fromTimeZone, final String toTimeZone) {
        return convertTime(dateString, fromFormat, fromTimeZone, fromFormat, toTimeZone);
    }

    /**
     * Converts a Raw World Time Long into a Readable 24-Hour Time String
     *
     * @param worldTime      The raw World Time
     * @param tickOffset     The amount of time to offset the dayTicks (worldTime % ticksPerDay)
     * @param ticksPerDay    The ticks per game day
     * @param ticksPerHour   The ticks per game hour
     * @param ticksPerMinute The ticks per game minute
     * @return The converted and readable 24-hour time string
     */
    public static Pair<Long, Instant> fromWorldTime(final long worldTime, final long tickOffset, final long ticksPerDay, final long ticksPerHour, final long ticksPerMinute) {
        long days = worldTime / ticksPerDay;
        long dayTicks = worldTime % ticksPerDay;
        dayTicks += tickOffset;
        if (dayTicks > ticksPerDay) dayTicks -= ticksPerDay;

        final long hourTicks = dayTicks % ticksPerHour;
        final long minuteTicks = hourTicks % ticksPerMinute;

        final long hours = dayTicks / ticksPerHour;
        final long minutes = hourTicks / ticksPerMinute;
        final long seconds = minuteTicks * (60 / ticksPerMinute);

        final long millis = ((hours * 60L + minutes) * 60L + seconds) * 1000L;

        return new Pair<>(days, Instant.ofEpochMilli(millis));
    }

    /**
     * Converts a Raw World Time Long into a Readable 24-Hour Time String
     *
     * @param worldTime      The raw World Time
     * @param ticksPerDay    The ticks per game day
     * @param ticksPerHour   The ticks per game hour
     * @param ticksPerMinute The ticks per game minute
     * @return The converted and readable 24-hour time string
     */
    public static Pair<Long, Instant> fromWorldTime(final long worldTime, final long ticksPerDay, final long ticksPerHour, final long ticksPerMinute) {
        return fromWorldTime(worldTime, 0L, ticksPerDay, ticksPerHour, ticksPerMinute);
    }

    /**
     * Converts a Raw World Time Long into a Readable 24-Hour Time String
     *
     * @param worldTime The raw World Time
     * @return The converted and readable 24-hour time string
     */
    public static Pair<Long, Instant> fromWorldTime(final long worldTime) {
        return fromWorldTime(worldTime, 6000L, 24000L, 1000L, 16L);
    }

    /**
     * Convert Epoch Timestamp to Date String in the given format and timezone.
     *
     * @param epochTime Epoch Timestamp in seconds.
     * @param format    Date format string.
     * @param timeZone  Timezone string.
     * @return Date String in the specified format and timezone.
     */
    public static String epochToString(final long epochTime, final String format, final String timeZone) {
        // Convert seconds to milliseconds
        return toString(fromEpoch(epochTime), format, timeZone);
    }

    /**
     * Convert Epoch Timestamp to Date String in the given format and timezone.
     *
     * @param epochTime Epoch Timestamp in seconds.
     * @param format    Date format string.
     * @return Date String in the specified format and timezone.
     */
    public static String epochToString(final long epochTime, final String format) {
        // Convert seconds to milliseconds
        return epochToString(epochTime, format, null);
    }

    /**
     * Convert Date String to Epoch Timestamp in seconds.
     *
     * @param dateString Date String in the given format and timezone.
     * @param format     Date format string.
     * @param timeZone   Timezone string.
     * @return Epoch Timestamp in seconds.
     */
    public static long stringToEpoch(final String dateString, final String format, final String timeZone) {
        return toEpoch(toInstance(dateString, format, timeZone));
    }

    /**
     * Convert Date String to Epoch Timestamp in seconds.
     *
     * @param dateString Date String in the given format and timezone.
     * @param format     Date format string.
     * @return Epoch Timestamp in seconds.
     */
    public static long stringToEpoch(final String dateString, final String format) {
        return stringToEpoch(dateString, format, null);
    }

    /**
     * Retrive a Time {@link Instant} from the specified epoch time
     *
     * @param epochTime The epoch time to interpret
     * @return the converted {@link Instant} representing the epoch time
     */
    public static Instant fromEpoch(final long epochTime) {
        return Instant.ofEpochSecond(epochTime);
    }

    /**
     * Gets the number of seconds from the Java Epoch, derived from specified args
     *
     * @param data The timestamp data to interpret
     * @return the number of seconds from the Java Epoch, from specified args
     */
    public static long toEpoch(final Instant data) {
        // Convert milliseconds to seconds
        return data != null ? data.getEpochSecond() : 0L;
    }

    /**
     * Retrieve the current time
     *
     * @return the current timestamp, as an {@link Instant}
     */
    public static Instant getCurrentTime() {
        return Instant.now();
    }

    /**
     * Retrieve the {@link Duration} between two {@link Temporal} points in time
     *
     * @param start The starting {@link Temporal} point
     * @param end   The ending {@link Temporal} point
     * @return the {@link Duration} between the two points
     */
    public static Duration getDuration(final Temporal start, final Temporal end) {
        return Duration.between(start, end);
    }

    /**
     * Retrieve the {@link Duration} from the {@link Temporal} point in time to the current time
     *
     * @param start The {@link Temporal} point to interpret
     * @return the {@link Duration} between the two points
     */
    public static Duration getDurationFrom(final Temporal start) {
        return getDuration(start, getCurrentTime());
    }

    /**
     * Retrieve the {@link Duration} to the {@link Temporal} point in time from the current time
     *
     * @param end The {@link Temporal} point to interpret
     * @return the {@link Duration} between the two points
     */
    public static Duration getDurationTo(final Temporal end) {
        return getDuration(getCurrentTime(), end);
    }

    /**
     * Retrieves the {@link ChronoUnit} from a string, if any
     *
     * @param name The string to interpret
     * @return the resulting {@link ChronoUnit} if any
     */
    public static TemporalUnit getUnitFrom(final String name) {
        return ChronoUnit.valueOf(name.toUpperCase());
    }

    /**
     * Modify the specified {@link Temporal} with the specified arguments
     *
     * @param temporal the original {@link Temporal}
     * @param amount   the amount of the specified unit to add, may be negative
     * @param unit     the unit of the amount to add, can not be null
     * @return an object of the same type with the specified period added, if not null
     */
    public static Temporal appendTime(final Temporal temporal, final long amount, final TemporalUnit unit) {
        return temporal.plus(amount, unit);
    }

    /**
     * Modify the specified {@link Temporal} with the specified arguments
     *
     * @param temporal the original {@link Temporal}
     * @param amount   the amount of the specified unit to add, may be negative
     * @param unit     the unit of the amount to add, can not be null
     * @return an object of the same type with the specified period added, if not null
     */
    public static Temporal appendTime(final Temporal temporal, final long amount, final String unit) {
        return appendTime(temporal, amount, getUnitFrom(unit));
    }
}
