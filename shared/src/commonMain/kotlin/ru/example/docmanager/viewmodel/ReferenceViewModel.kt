/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.viewmodel

import androidx.lifecycle.ViewModel
import ru.example.docmanager.reference.Reference
import ru.example.docmanager.reference.ReferenceType
import ru.example.docmanager.database.repositories.references.IndividualsRepository
import ru.example.docmanager.database.repositories.references.OrganizationsRepository
import ru.example.docmanager.database.repositories.references.ProductsRepository
import ru.example.docmanager.database.repositories.references.ReferenceRepository
import ru.example.docmanager.database.repositories.references.SuppliersRepository

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
    }

    suspend fun createItem() {
        val referenceType = formState.referenceType
        val repository = repositories[referenceType] ?: return

        val created = repository.createItem()
        formState.addItem(referenceType, created)
        formState.statusMessage = "Запись создана"
    }

    suspend fun deleteItem(item: Reference) {
        val referenceType = formState.referenceType
        val repository = repositories[referenceType] ?: return

        repository.deleteItem(item.id)
        formState.removeItem(referenceType, item)
        formState.statusMessage = "Запись удалена"
    }

    suspend fun saveAll() {
        val referenceType = formState.referenceType
        val repository = repositories[referenceType] ?: return

        formState.buildUpdatedItems().forEach { item ->
            updateItem(repository, item)
            formState.replaceItem(referenceType, item)
        }

        formState.statusMessage = "Изменения сохранены"
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun updateItem(
        repository: ReferenceRepository<out Reference>,
        item: Reference
    ) {
        (repository as ReferenceRepository<Reference>).updateItem(item)
    }
}