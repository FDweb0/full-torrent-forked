/*
 * Copyright (C) 2019 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of Full Torrent.
 *
 * Full Torrent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Full Torrent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Full Torrent.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.full.torrent.no.ads.private1.encrypted.core.storage.converter;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

public class UriConverter
{
    @TypeConverter
    public static Uri toUri(@NonNull String uriStr)
    {
        return Uri.parse(uriStr);
    }

    @TypeConverter
    public static String fromUri(@NonNull Uri uri)
    {
        return uri.toString();
    }
}