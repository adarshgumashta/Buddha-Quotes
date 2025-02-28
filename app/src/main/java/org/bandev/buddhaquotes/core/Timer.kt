/**

Buddha Quotes
Copyright (C) 2021  BanDev

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

 */

package org.bandev.buddhaquotes.core

import android.content.Context
import android.content.SharedPreferences
import org.bandev.buddhaquotes.R

/**
 * Timer helps with the meditation timer
 */

class Timer {

    /**
     * Converts the time given to a nice string
     *
     * @param minutes [Long]
     * @param seconds [Long]
     * @param context [Context]
     * @return The time as a [String]
     */
    fun convertToString(minutes: Long, seconds: Long, context: Context): String {
        return if (minutes > 0) {
            // There is at least a minute
            if (seconds == 0L) {
                // There are no seconds e.g. exactly 5 mins
                "$minutes " + context.getString(R.string.minutes)
            } else {
                // There is at least a second
                "$minutes " + context.getString(R.string.minutes) + " " + context.getString(R.string.and) + " $seconds" + context.getString(
                    R.string.seconds
                )
            }
        } else {
            // There is less than a minute
            "$seconds " + context.getString(R.string.seconds)
        }
    }

    inner class Settings(context: Context) {
        // Setup the sharedPrefs & editor
        private var sharedPrefs: SharedPreferences = context.getSharedPreferences("Timer", 0)
        private var editor: SharedPreferences.Editor = sharedPrefs.edit()

        // Vibrate the phone on each second?
        var vibrateSecond: Boolean
            get() = sharedPrefs.getBoolean("vibrateSecond", false)
            set(value) {
                editor.putBoolean("vibrateSecond", value)
                editor.commit()
            }

        // Play a sound at the end?
        var endSoundID: Int
            get() = sharedPrefs.getInt("endSoundID", 0)
            set(value) {
                editor.putInt("endSoundID", value)
                editor.commit()
            }

        // Show a notification with progress?
        var showNotificaton: Boolean
            get() = sharedPrefs.getBoolean("showNotificaton", false)
            set(value) {
                editor.putBoolean("showNotificaton", value)
                editor.commit()
            }
    }
}