/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.viewmodel.reference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.example.docmanager.data.document.header.HeaderFieldType
import ru.example.docmanager.data.reference.*
import ru.example.docmanager.data.repositories.references.IndividualsRepository
import ru.example.docmanager.data.repositories.references.OrganizationsRepository
import ru.example.docmanager.data.repositories.references.ProductsRepository
import ru.example.docmanager.data.repositories.references.ReferenceRepository
import ru.example.docmanager.data.repositories.references.SuppliersRepository

class ReferenceViewModel(
    individualsRepository: IndividualsRepository,
    organizationsRepository: OrganizationsRepository,
    productsRepository: ProductsRepository,
    suppliersRepository: SuppliersRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReferenceUiState())
    val uiState: StateFlow<ReferenceUiState> = _uiState.asStateFlow()

    /**
     * Backward-compatible property exposing current state snapshot.
     */
    val formState: ReferenceUiState
        get() = _uiState.value

    private val repositories: Map<ReferenceType, ReferenceRepository<out Reference>> = mapOf(
        ReferenceType.INDIVIDUAL to individualsRepository,
        ReferenceType.ORGANIZATION to organizationsRepository,
        ReferenceType.PRODUCT to productsRepository,
        ReferenceType.SUPPLIER to suppliersRepository
    )

    fun loadReferenceItems() {
        viewModelScope.launch {
            loadReferenceItemsSuspend()
        }
    }

    suspend fun loadReferenceItemsSuspend() {
        _uiState.update { it.copy(isLoading = true) }
        runCatching {
            repositories.mapValues { (_, repository) ->
                repository.getItems()
            }
        }.onSuccess { items ->
            _uiState.update { state ->
                state.copy(
                    itemsByType = items,
                    isLoading = false
                )
            }
        }.onFailure { error ->
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    statusMessage = "Ошибка при загрузке справочников: ${error.message}"
                )
            }
        }
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }

    fun setType(referenceType: ReferenceType) {
        _uiState.update { it.copy(referenceType = referenceType) }
    }

    fun updateField(itemId: Int, headerFieldType: HeaderFieldType, value: String) {
        val currentType = _uiState.value.referenceType
        updateField(currentType, itemId, headerFieldType, value)
    }

    fun updateField(referenceType: ReferenceType, itemId: Int, headerFieldType: HeaderFieldType, value: String) {
        _uiState.update { state ->
            val typeEdits = state.fieldValues[referenceType].orEmpty()
            val itemEdits = typeEdits[itemId] ?: state.itemsByType[referenceType]
                ?.firstOrNull { it.id == itemId }
                ?.toMap()
                .orEmpty()
            val updatedItemEdits = itemEdits + (headerFieldType to value)
            val updatedTypeEdits = typeEdits + (itemId to updatedItemEdits)
            val updatedFieldValues = state.fieldValues + (referenceType to updatedTypeEdits)
            state.copy(fieldValues = updatedFieldValues)
        }
    }

    fun createItem() {
        viewModelScope.launch {
            createItemSuspend()
        }
    }

    suspend fun createItemSuspend() {
        val referenceType = _uiState.value.referenceType
        val repository = repositories[referenceType] ?: return

        _uiState.update { it.copy(isLoading = true) }
        runCatching {
            repository.createItem()
        }.onSuccess { created ->
            _uiState.update { state ->
                val updatedList = state.itemsByType[referenceType].orEmpty() + created
                val updatedItemsByType = state.itemsByType + (referenceType to updatedList)
                val typeEdits = state.fieldValues[referenceType].orEmpty()
                val updatedTypeEdits = typeEdits + (created.id to created.toMap())
                val updatedFieldValues = state.fieldValues + (referenceType to updatedTypeEdits)
                state.copy(
                    itemsByType = updatedItemsByType,
                    fieldValues = updatedFieldValues,
                    isLoading = false,
                    statusMessage = "Запись создана"
                )
            }
        }.onFailure { error ->
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    statusMessage = "Ошибка при создании записи: ${error.message}"
                )
            }
        }
    }

    fun deleteItem(item: Reference) {
        viewModelScope.launch {
            deleteItemSuspend(item)
        }
    }

    suspend fun deleteItemSuspend(item: Reference) {
        val referenceType = _uiState.value.referenceType
        val repository = repositories[referenceType] ?: return

        _uiState.update { it.copy(isLoading = true) }
        runCatching {
            repository.deleteItem(item.id)
        }.onSuccess {
            _uiState.update { state ->
                val updatedList = state.itemsByType[referenceType].orEmpty().filterNot { it.id == item.id }
                val updatedItemsByType = state.itemsByType + (referenceType to updatedList)
                val typeEdits = state.fieldValues[referenceType].orEmpty() - item.id
                val updatedFieldValues = state.fieldValues + (referenceType to typeEdits)
                state.copy(
                    itemsByType = updatedItemsByType,
                    fieldValues = updatedFieldValues,
                    isLoading = false,
                    statusMessage = "Запись удалена"
                )
            }
        }.onFailure { error ->
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    statusMessage = "Ошибка при удалении записи: ${error.message}"
                )
            }
        }
    }

    fun saveAll() {
        viewModelScope.launch {
            saveAllSuspend()
        }
    }

    suspend fun saveAllSuspend() {
        val referenceType = _uiState.value.referenceType
        val repository = repositories[referenceType] ?: return

        _uiState.update { it.copy(isLoading = true) }
        runCatching {
            val updatedItems = _uiState.value.buildUpdatedItems(referenceType)
            updatedItems.forEach { item ->
                updateItem(repository, item)
            }
            updatedItems
        }.onSuccess { updatedItems ->
            _uiState.update { state ->
                val updatedItemsByType = state.itemsByType + (referenceType to updatedItems)
                val updatedFieldValues = state.fieldValues - referenceType
                state.copy(
                    itemsByType = updatedItemsByType,
                    fieldValues = updatedFieldValues,
                    isLoading = false,
                    statusMessage = "Изменения сохранены"
                )
            }
        }.onFailure { error ->
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    statusMessage = "Ошибка при сохранении изменений: ${error.message}"
                )
            }
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