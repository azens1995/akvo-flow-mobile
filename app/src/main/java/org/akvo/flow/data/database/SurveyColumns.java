/*
 * Copyright (C) 2010-2016 Stichting Akvo (Akvo Foundation)
 *
 * This file is part of Akvo FLOW.
 *
 * Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 * the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 * either version 3 of the License or any later version.
 *
 * Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License included below for more details.
 *
 * The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 *
 */

package org.akvo.flow.data.database;

public interface SurveyColumns {
    String _ID = "_id";
    String SURVEY_ID = "survey_id";
    String SURVEY_GROUP_ID = "survey_group_id";
    String NAME = "display_name";
    String VERSION = "version";
    String TYPE = "type";
    String LOCATION = "location";
    String FILENAME = "filename";
    String LANGUAGE = "language";
    String HELP_DOWNLOADED = "help_downloaded_flag";
    String DELETED = "deleted";
}
