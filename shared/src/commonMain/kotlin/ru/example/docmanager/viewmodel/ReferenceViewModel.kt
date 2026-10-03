package ru.example.docmanager.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.example.docmanager.database.models.references.Reference
import ru.example.docmanager.database.repositories.references.ReferenceRepository
import ru.example.docmanager.database.repositories.references.IndividualsRepository
import ru.example.docmanager.database.repositories.references.OrganizationsRepository
import ru.example.docmanager.database.repositories.references.ProductsRepository
import ru.example.docmanager.database.repositories.references.SuppliersRepository

class ReferenceViewModel(
    val individualsRepository: IndividualsRepository,
    val organizationsRepository: OrganizationsRepository,
    val productsRepository: ProductsRepository,
    val suppliersRepository: SuppliersRepository
) {
    val selectedReferenceRepository: StateFlow<ReferenceRepository<out Reference>?>
        field = MutableStateFlow<ReferenceRepository<out Reference>?>(null)
    val referencesItems: StateFlow<Map<ReferenceRepository<out Reference>, List<Reference>>?>
        field = MutableStateFlow<Map<ReferenceRepository<out Reference>, List<Reference>>?>(null)

    val references = listOf(
        individualsRepository,
        organizationsRepository,
        productsRepository,
        suppliersRepository
    )

    suspend fun removeItem(referenceRepository: ReferenceRepository<out Reference>, item: Reference) {
        referencesItems.value?.let { current ->
            referenceRepository.deleteItem(item.id)
            referencesItems.value = current + (referenceRepository to ((current[referenceRepository] ?: emptyList()).filter { it != item }))
        }
    }

    suspend fun <T : Reference> newItem(referenceRepository: ReferenceRepository<T>) {
        referencesItems.value?.let { current ->
            val created = referenceRepository.createItem()
            referencesItems.value = current + (referenceRepository to ((current[referenceRepository] ?: emptyList()) + created))
        }
    }

    // TODO: RETHINK THIS
    @Suppress("UNCHECKED_CAST")
    suspend fun saveAll(
        referenceRepository: ReferenceRepository<out Reference>,
        itemId: Int,
        fields: Map<String, Any>
    ) {
        val currentItems = referencesItems.value?.get(referenceRepository) ?: return
        val updatedItems = currentItems.map { item ->
            if (item.id == itemId) {
                val newItem = item.copyWithFields(fields)
                (referenceRepository as ReferenceRepository<Reference>).updateItem(newItem)
                newItem
            } else item
        }
        referencesItems.value = (referencesItems.value ?: emptyMap()) + (referenceRepository to updatedItems)
    }

    suspend fun loadReferenceItems() {
        referencesItems.value = references.associateWith { it.getItems() }
    }

    fun <T : Reference> selectReference(referenceRepository: ReferenceRepository<T>) {
        selectedReferenceRepository.value = referenceRepository
    }
}