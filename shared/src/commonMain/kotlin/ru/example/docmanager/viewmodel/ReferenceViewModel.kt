/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.example.docmanager.database.repositories.references.ReferenceRepository
import ru.example.docmanager.reference.*

class ReferenceViewModel(
    individualsRepository: ReferenceRepository<Individual>,
    organizationsRepository: ReferenceRepository<Organization>,
    productsRepository: ReferenceRepository<Product>,
    suppliersRepository: ReferenceRepository<Supplier>
): ViewModel() {
    val formState = ReferenceState()

    private val repositories: Map<ReferenceType, ReferenceRepository<out Reference>> = mapOf(
        ReferenceType.INDIVIDUAL to individualsRepository,
        ReferenceType.ORGANIZATION to organizationsRepository,
        ReferenceType.PRODUCT to productsRepository,
        ReferenceType.SUPPLIER to suppliersRepository
    )

    init {
        loadReferenceItems()
    }

    fun loadReferenceItems() {
        viewModelScope.launch {
            runCatching {
                repositories.mapValues { (_, repository) ->
                    repository.getItems()
                }
            }.onSuccess { items ->
                formState.setAllItems(items)
            }.onFailure { error ->
                formState.statusMessage = "Ошибка при загрузке справочников: ${error.message}"
            }
        }
    }

    fun clearStatusMessage() {
        formState.clearStatusMessage()
    }

    fun setType(referenceType: ReferenceType) {
        formState.referenceType = referenceType
        formState.syncFieldValues()
        formState.clearStatusMessage()
    }

    fun createItem() {
        viewModelScope.launch {
            createItemSuspend()
        }
    }

    suspend fun createItemSuspend() {
        val referenceType = formState.referenceType
        val repository = repositories[referenceType] ?: return

        runCatching {
            repository.createItem()
        }.onSuccess { created ->
            formState.addItem(referenceType, created)
            formState.statusMessage = "Запись создана"
        }.onFailure { error ->
            formState.statusMessage = "Ошибка при создании записи: ${error.message}"
        }
    }

    fun deleteItem(item: Reference) {
        viewModelScope.launch {
            deleteItemSuspend(item)
        }
    }

    suspend fun deleteItemSuspend(item: Reference) {
        val referenceType = formState.referenceType
        val repository = repositories[referenceType] ?: return

        runCatching {
            repository.deleteItem(item.id)
        }.onSuccess {
            formState.removeItem(referenceType, item)
            formState.statusMessage = "Запись удалена"
        }.onFailure { error ->
            formState.statusMessage = "Ошибка при удалении записи: ${error.message}"
        }
    }

    fun saveAll() {
        viewModelScope.launch {
            saveAllSuspend()
        }
    }

    suspend fun saveAllSuspend() {
        val referenceType = formState.referenceType
        val repository = repositories[referenceType] ?: return

        runCatching {
            val updatedItems = formState.buildUpdatedItems()
            updatedItems.forEach { item ->
                updateItem(repository, item)
            }
            formState.setItems(referenceType, updatedItems)
        }.onSuccess {
            formState.statusMessage = "Изменения сохранены"
        }.onFailure { error ->
            formState.statusMessage = "Ошибка при сохранении изменений: ${error.message}"
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