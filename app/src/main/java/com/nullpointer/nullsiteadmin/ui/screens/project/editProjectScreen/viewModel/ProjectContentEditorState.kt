package com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import com.nullpointer.nullsiteadmin.core.delagetes.SavableComposeState
import com.nullpointer.nullsiteadmin.models.project.data.*
import kotlinx.serialization.encodeToString

class ProjectContentEditorState(state: SavedStateHandle) {
    private var draft by SavableComposeState(state, "PROJECT_CONTENT_DRAFT", "")
    var locale by SavableComposeState(state, "PROJECT_CONTENT_LOCALE", "es")
        private set
    var technologiesText by SavableComposeState(state, "PROJECT_TECHNOLOGIES_TEXT", "")
        private set
    var orderText by SavableComposeState(state, "PROJECT_SORT_TEXT", "")
        private set
    val content by derivedStateOf { draft.takeIf { it.isNotEmpty() }?.let(ProjectContentContract::decode) ?: ProjectContent() }
    val images by derivedStateOf { ProjectContentContract.effectiveGallery(content) }
    val isValid get() = ProjectContentContract.valid(content) && (orderText.isBlank() || orderText.toLongOrNull() != null)
    fun initialize(value: ProjectContent) { if (draft.isEmpty()) {
        change(value); technologiesText = value.technologyIds.orEmpty().joinToString(", "); orderText = value.sortOrder?.toString().orEmpty()
    } }
    fun changeTechnologies(value: String) {
        technologiesText = value
        change(content.copy(technologyIds = value.split(',').map(String::trim).filter(String::isNotEmpty)))
    }
    fun changeOrder(value: String) {
        orderText = value
        if (value.isBlank() || value.toLongOrNull() != null) change(content.copy(sortOrder = value.toLongOrNull() ?: 0))
    }
    fun change(value: ProjectContent) { draft = ProjectContentContract.encode(value) }
    fun selectLocale(value: String) { require(value in listOf("es", "en")); locale = value }
    fun translation(field: String) = content.translations[locale]?.get(field).orEmpty()
    fun changeTranslation(field: String, text: String) {
        require(field in ProjectContentContract.fields)
        change(content.copy(translations = content.translations + (locale to (content.translations[locale].orEmpty() + (field to text)))))
    }
    private fun gallery(value: List<ProjectImage>) {
        val ordered = value.mapIndexed { index, image -> image.copy(order = index.toLong(), presentFields = null) }
        change(content.copy(gallery = ordered.map { it.url }, galleryItems = ordered))
    }
    fun addImage(image: ProjectImage = ProjectImage()) { require(images.size < 60); gallery(images + image) }
    fun editImage(index: Int, image: ProjectImage) { gallery(images.toMutableList().apply { this[index] = image }) }
    fun removeImage(index: Int) { gallery(images.toMutableList().apply { removeAt(index) }) }
    fun moveImage(index: Int, offset: Int) {
        val target = index + offset
        if (index !in images.indices || target !in images.indices) return
        gallery(images.toMutableList().apply { add(target, removeAt(index)) })
    }
    fun addLink() { change(content.copy(links = content.links.orEmpty() + ProjectLink())) }
    fun editLink(index: Int, link: ProjectLink) { change(content.copy(links = content.links.orEmpty().toMutableList().apply { this[index] = link })) }
    fun removeLink(index: Int) { change(content.copy(links = content.links.orEmpty().toMutableList().apply { removeAt(index) })) }
}
