/*
 * Copyright (C) 2017 Stichting Akvo (Akvo Foundation)
 *
 * This file is part of Akvo Flow.
 *
 * Akvo Flow is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Akvo Flow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Akvo Flow.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.akvo.flow.presentation.settings;

public class ViewUserSettings {
    private final boolean screenOn;
    private final boolean dataEnabled;
    private final int language;
    private final int imageSize;
    private final String identifier;

    public ViewUserSettings(boolean screenOn, boolean dataEnabled, int language, int imageSize,
            String identifier) {
        this.screenOn = screenOn;
        this.dataEnabled = dataEnabled;
        this.language = language;
        this.imageSize = imageSize;
        this.identifier = identifier;
    }

    public boolean isScreenOn() {
        return screenOn;
    }

    public boolean isDataEnabled() {
        return dataEnabled;
    }

    public int getLanguage() {
        return language;
    }

    public int getImageSize() {
        return imageSize;
    }

    public String getIdentifier() {
        return identifier;
    }
}
