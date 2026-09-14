package com.nullpointer.nullsiteadmin.models.project

import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import com.nullpointer.nullsiteadmin.models.project.entity.ProjectEntity
import java.util.Date
import org.junit.Assert.assertEquals
import org.junit.Test

class ProjectDataTest {
    @Test
    fun project_data_round_trip_preserves_cache_fields() {
        val created = Date(1_000)
        val updated = Date(2_000)
        val data = ProjectData("id", "name", "description", "image", "repo", created, updated, true)

        val entity = ProjectEntity.fromProjectData(data)
        val restored = ProjectData.fromProjectEntity(entity)

        assertEquals(data, restored)
    }
}
