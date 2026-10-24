/*
 * RetroDrom Games Companion
 * Copyright (C) 2026 Alexey Kuzin <amkuzink@gmail.com>.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


package org.leviathan941.retrodromcompanion.rssreader

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.Instant

/**
 * Pins RSS `pubDate` parsing to what `DateTimeFormatter.RFC_1123_DATE_TIME` produced.
 */
class PublicationDateTest {

    @Test
    fun `feed date with a numeric offset`() {
        assertEquals(
            Instant.parse("2026-09-04T04:45:38Z"),
            RssPublicationDate("Fri, 04 Sep 2026 04:45:38 +0000").asInstant(),
        )
    }

    @Test
    fun `GMT zone`() {
        assertEquals(
            Instant.parse("2008-06-30T11:05:30Z"),
            "Mon, 30 Jun 2008 11:05:30 GMT".asInstant(),
        )
    }

    @Test
    fun `non-zero offset is applied`() {
        assertEquals(
            Instant.parse("2026-09-04T01:45:38Z"),
            "Fri, 04 Sep 2026 04:45:38 +0300".asInstant(),
        )
    }

    @Test
    fun `single-digit day of month`() {
        assertEquals(
            Instant.parse("2026-09-04T04:45:38Z"),
            "Fri, 4 Sep 2026 04:45:38 +0000".asInstant(),
        )
    }

    @Test
    fun `day of week is optional`() {
        assertEquals(
            Instant.parse("2026-09-04T04:45:38Z"),
            "04 Sep 2026 04:45:38 +0000".asInstant(),
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `malformed date is rejected`() {
        "2026-09-04T04:45:38Z".asInstant()
    }
}
