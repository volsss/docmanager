/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.example.docmanager.database.repositories.references.*
import ru.example.docmanager.reference.Reference
import ru.example.docmanager.reference.ReferenceType

class ReferenceViewModel(
    val individualsRepository: IndividualsRepository,
    val organizationsRepository: OrganizationsRepository,
    val productsRepository: ProductsRepository,
    val suppliersRepository: SuppliersRepository
): ViewModel() {
    val formState = ReferenceState()

    private val repositories = mapOf(
        ReferenceType.INDIVIDUAL to individualsRepository,
        ReferenceType.ORGANIZATION to organizationsRepository,
        ReferenceType.PRODUCT to productsRepository,
        ReferenceType.SUPPLIER to suppliersRepository
    )

    suspend fun loadReferenceItems() {
        formState.setAllItems(
            repositories.mapValues { (_, repository) ->
                repository.getItems()
            }
        )
    }

    fun setType(referenceType: ReferenceType) {
        formState.referenceType = referenceType
        formState.syncFieldValues()
        formState.statusMessage = null
    }

    fun createItem() {
        viewModelScope.launch {
            val referenceType = formState.referenceType
            val repository = repositories[referenceType] ?: return@launch

            val created = repository.createItem()
            formState.addItem(referenceType, created)
            formState.statusMessage = "Запись создана"
        }
    }

    fun deleteItem(item: Reference) {
        viewModelScope.launch {
            val referenceType = formState.referenceType
            val repository = repositories[referenceType] ?: return@launch

            repository.deleteItem(item.id)
            formState.removeItem(referenceType, item)
            formState.statusMessage = "Запись удалена"
        }
    }

    fun saveAll() {
        viewModelScope.launch {
            val referenceType = formState.referenceType
            val repository = repositories[referenceType] ?: return@launch

            val updatedItems = formState.buildUpdatedItems()
            updatedItems.forEach { item ->
                updateItem(repository, item)
            }
            formState.setItems(referenceType, updatedItems)
            formState.statusMessage = "Изменения сохранены"
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun updateItem(
        repository: ReferenceRepository<out Reference>,
        item: Reference
    ) {
        (repository as ReferenceRepository<Reference>).updateItem(item)
    }
}