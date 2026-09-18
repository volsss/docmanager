package ru.example.docmanager.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.example.docmanager.database.models.references.ReferenceModel
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
    val selectedReferenceRepository: StateFlow<ReferenceRepository<out ReferenceModel>?>
        field = MutableStateFlow<ReferenceRepository<out ReferenceModel>?>(null)
    val referencesItems: StateFlow<Map<ReferenceRepository<out ReferenceModel>, List<ReferenceModel>>?>
        field = MutableStateFlow<Map<ReferenceRepository<out ReferenceModel>, List<ReferenceModel>>?>(null)

    val references = listOf(
        individualsRepository,
        organizationsRepository,
        productsRepository,
        suppliersRepository
    )

    suspend fun removeItem(referenceRepository: ReferenceRepository<out ReferenceModel>, item: ReferenceModel) {
        referencesItems.value?.let { current ->
            referenceRepository.deleteItem(item.id)
            referencesItems.value = current + (referenceRepository to ((current[referenceRepository] ?: emptyList()).filter { it != item }))
        }
    }

    suspend fun <T : ReferenceModel> newItem(referenceRepository: ReferenceRepository<T>) {
        referencesItems.value?.let { current ->
            val created = referenceRepository.newItem()
            referencesItems.value = current + (referenceRepository to ((current[referenceRepository] ?: emptyList()) + created))
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun saveAll(
        referenceRepository: ReferenceRepository<out ReferenceModel>,
        itemId: Int,
        fields: Map<String, Any>
    ) {
        val currentItems = referencesItems.value?.get(referenceRepository) ?: return
        val updatedItems = currentItems.map { item ->
            if (item.id == itemId) {
                val newItem = item.copyWithFields(fields)
                (referenceRepository as ReferenceRepository<ReferenceModel>).updateItem(newItem)
                newItem
            } else item
        }
        referencesItems.value = (referencesItems.value ?: emptyMap()) + (referenceRepository to updatedItems)
    }

    suspend fun loadReferenceItems() {
        referencesItems.value = references.associateWith { it.getItems() }
    }

    fun <T : ReferenceModel> selectReference(referenceRepository: ReferenceRepository<T>) {
        selectedReferenceRepository.value = referenceRepository
    }
}