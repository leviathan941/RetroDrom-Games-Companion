/*
 * Copyright 2024 Alexey Kuzin <amkuzink@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.leviathan941.retrodromcompanion

class AppVersion {
    val code: Int by lazy {
        val minor = fixVersionPart(MINOR)
        val patch = fixVersionPart(PATCH)
        "$MAJOR$minor$patch".toInt()
    }

    val name = "$MAJOR.$MINOR.$PATCH"

    private fun fixVersionPart(part: String) = if (part.length > 1) {
        part
    } else {
        "0$part"
    }

    companion object {
        const val MAJOR = "1"
        const val MINOR = "7" // from 0 to 99
        const val PATCH = "0" // from 0 to 99
    }
}
