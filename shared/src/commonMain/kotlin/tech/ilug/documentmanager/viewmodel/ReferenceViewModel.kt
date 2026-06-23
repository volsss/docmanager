package tech.ilug.documentmanager.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import tech.ilug.documentmanager.model.Reference
import tech.ilug.documentmanager.repository.references.IndividualsRepository
import tech.ilug.documentmanager.repository.references.OrganizationsRepository
import tech.ilug.documentmanager.repository.references.ProductsRepository
import tech.ilug.documentmanager.repository.references.SuppliersRepository

class ReferenceViewModel (
    val individualsRepository: IndividualsRepository,
    val organizationsRepository: OrganizationsRepository,
    val productsRepository: ProductsRepository,
    val suppliersRepository: SuppliersRepository
) {
    val selectedReference: StateFlow<Reference<out Reference.Item>?>
        field = MutableStateFlow<Reference<out Reference.Item>?>(null)
    val referencesItems: StateFlow<Map<Reference<out Reference.Item>, List<Reference.Item>>?>
        field = MutableStateFlow<Map<Reference<out Reference.Item>, List<Reference.Item>>?>(null)

    val references = listOf(
        individualsRepository,
        organizationsRepository,
        productsRepository,
        suppliersRepository
    )

    suspend fun removeItem (reference: Reference<out Reference.Item>, item: Reference.Item) {
        referencesItems.value?.let {
            reference.deleteItem(item.id)
            referencesItems.value = it.toMutableMap().apply {
                this[reference] = (this[reference] ?: emptyList()).filter { it != item }
            }
        }
    }

    suspend fun <T : Reference.Item> newItem(reference: Reference<T>) {
        referencesItems.value?.let {
            referencesItems.value = it.toMutableMap().apply {
                this[reference] = (this[reference] ?: emptyList()) + reference.newItem()
            }
        }
    }

    suspend fun saveAll(
        reference: Reference<out Reference.Item>,
        itemId: Int,
        fields: Map<String, Any>
    ) {
        val currentItems = referencesItems.value?.get(reference) ?: return
        val updatedItems = currentItems.map { item ->
            if (item.id == itemId) {
                val newItem = item.copyWithFields(fields)
                (reference as Reference<Reference.Item>).updateItem(newItem)
                newItem
            } else item
        }
        referencesItems.value?.let {
            referencesItems.value = it.toMutableMap().apply {
                this[reference] = updatedItems
            }
        }
    }

    suspend fun loadReferenceItems () {
        referencesItems.value = references.associateWith { reference -> reference.getItems().toMutableList() }
    }

    fun <T : Reference.Item> selectReference(reference: Reference<T>) {
        selectedReference.value = reference
    }
}