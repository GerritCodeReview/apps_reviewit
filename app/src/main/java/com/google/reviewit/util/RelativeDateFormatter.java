// Copyright (C) 2013 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.reviewit.util;

import android.content.Context;

import com.google.reviewit.R;

import java.util.Date;

/**
 * Formatter to format timestamps relative to the current time using time units
 * in the format defined by {@code git log --relative-date}.
 */
public class RelativeDateFormatter {
  static final long SECOND_IN_MILLIS = 1000;
  static final long MINUTE_IN_MILLIS = 60 * SECOND_IN_MILLIS;
  static final long HOUR_IN_MILLIS = 60 * MINUTE_IN_MILLIS;
  static final long DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS;
  static final long WEEK_IN_MILLIS = 7 * DAY_IN_MILLIS;
  static final long MONTH_IN_MILLIS = 30 * DAY_IN_MILLIS;
  static final long YEAR_IN_MILLIS = 365 * DAY_IN_MILLIS;

  private final Context context;

  public RelativeDateFormatter(Context context) {
    this.context = context;
  }

  /**
   * @param when {@link Date} to format
   * @return age of given {@link Date} compared to now formatted in the same
   * relative format as returned by {@code git log --relative-date}
   */
  @SuppressWarnings("boxing")
  public String format(Date when) {
    long ageMillis = (new Date()).getTime() - when.getTime();

    // shouldn't happen in a perfect world
    if (ageMillis < 0) {
      return context.getString(R.string.in_the_future);
    }

    // seconds
    if (ageMillis < upperLimit(MINUTE_IN_MILLIS)) {
      long seconds = round(ageMillis, SECOND_IN_MILLIS);
      if (seconds == 1) {
        return context.getString(R.string.one_second_ago);
      } else {

        return context.getString(R.string.seconds_ago, seconds);
      }
    }

    // minutes
    if (ageMillis < upperLimit(HOUR_IN_MILLIS)) {
      long minutes = round(ageMillis, MINUTE_IN_MILLIS);
      if (minutes == 1) {
        return context.getString(R.string.one_minute_ago);
      } else {
        return context.getString(R.string.minutes_ago, minutes);
      }
    }

    // hours
    if (ageMillis < upperLimit(DAY_IN_MILLIS)) {
      long hours = round(ageMillis, HOUR_IN_MILLIS);
      if (hours == 1) {
        return context.getString(R.string.one_hour_ago);
      } else {
        return context.getString(R.string.hours_ago, hours);
      }
    }

    // up to 14 days use days
    if (ageMillis < 14 * DAY_IN_MILLIS) {
      long days = round(ageMillis, DAY_IN_MILLIS);
      if (days == 1) {
        return context.getString(R.string.one_day_ago);
      } else {
        return context.getString(R.string.days_ago, days);
      }
    }

    // up to 10 weeks use weeks
    if (ageMillis < 10 * WEEK_IN_MILLIS) {
      long weeks = round(ageMillis, WEEK_IN_MILLIS);
      if (weeks == 1) {
        return context.getString(R.string.one_week_ago);
      } else {
        return context.getString(R.string.weeks_ago, weeks);
      }
    }

    // months
    if (ageMillis < YEAR_IN_MILLIS) {
      long months = round(ageMillis, MONTH_IN_MILLIS);
      if (months == 1) {
        return context.getString(R.string.one_month_ago);
      } else {
        return context.getString(R.string.months_ago, months);
      }
    }

    // up to 5 years use "year, months" rounded to months
    if (ageMillis < 5 * YEAR_IN_MILLIS) {
      long years = ageMillis / YEAR_IN_MILLIS;
      String yearLabel = (years > 1) ? context.getString(R.string.years) :
          context.getString(R.string.year);
      long months = round(ageMillis % YEAR_IN_MILLIS, MONTH_IN_MILLIS);
      String monthLabel = (months > 1) ? context.getString(R.string.months) :
          (months == 1 ? context.getString(R.string.month) : "");
      if (months == 0) {
        return context.getString(R.string.years_0_months_ago, years, yearLabel);
      } else {
        return context.getString(R.string.years_months_ago, years, yearLabel,
            months, monthLabel);
      }
    }

    // years
    long years = round(ageMillis, YEAR_IN_MILLIS);
    if (years == 1) {
      return context.getString(R.string.one_year_ago);
    } else {
      return context.getString(R.string.years_ago, years);
    }
  }

  private static long upperLimit(long unit) {
    return unit + unit / 2;
  }

  private static long round(long n, long unit) {
    return (n + unit / 2) / unit;
  }
}
