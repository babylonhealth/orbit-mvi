/*
 * Copyright 2020 Babylon Partners Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.babylon.orbit2.sample.posts.data.posts.database

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString

class PostDataOverviewMapperShould {

    private val avatarUrlGenerator = mock<AvatarUrlGenerator>()

    @BeforeEach
    fun setupMocks() {
        whenever(avatarUrlGenerator.generateUrl(anyString()))
            .then { (it.getArgument(0) as String).reversed() }
    }

    @Test
    fun `map when no comments`() {
        // given we configure details with no comments
        val postData = PostDataOverview(1, "title", "name", "email", 5)

        // when we convert
        val result = PostDataOverviewMapper(avatarUrlGenerator).convert(postData)

        // then we get the expected data
        assertEquals(1, result.id)
        assertEquals("liame", result.avatarUrl)
        assertEquals("title", result.title)
        assertEquals("name", result.username)
        assertEquals(5, result.comments)
    }
}
