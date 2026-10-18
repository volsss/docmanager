/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.viewmodel.reference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.example.docmanager.data.document.header.HeaderFieldType
import ru.example.docmanager.data.reference.Reference
import ru.example.docmanager.data.reference.ReferenceType
import ru.example.docmanager.data.repositories.references.BaseReferenceRepository
import ru.example.docmanager.data.repositories.references.ReferenceRepository
import ru.example.docmanager.ui.router.Route

class ReferenceViewModel(
    private val repositories: Map<ReferenceType, BaseReferenceRepository>,
    private val destination: Route.Reference
) : ViewModel() {
    val uiState: StateFlow<ReferenceUiState>
        field = MutableStateFlow(ReferenceUiState())

    fun loadReferenceItems() {
        viewModelScope.launch {
            loadReferenceItemsSuspend()
        }
    }

    suspend fun loadReferenceItemsSuspend() {
        uiState.update { it.copy(isLoading = true) }
        runCatching {
            repositories.mapValues { (_, repository) ->
                (repository as ReferenceRepository<out Reference>).getItems()
            }
        }.onSuccess { items ->
            uiState.update { state ->
                state.copy(
                    itemsByType = items,
                    isLoading = false
                )
            }
        }.onFailure { error ->
            uiState.update { state ->
                state.copy(
                    isLoading = false,
                    statusMessage = "Ошибка при загрузке справочников: ${error.message}"
                )
            }
        }
    }

    fun clearStatusMessage() {
        uiState.update { it.copy(statusMessage = null) }
    }

    fun setType(referenceType: ReferenceType) {
        uiState.update { it.copy(referenceType = referenceType) }
    }

    fun updateField(itemId: Int, headerFieldType: HeaderFieldType, value: String) {
        val currentType = uiState.value.referenceType
        updateField(currentType, itemId, headerFieldType, value)
    }

    fun updateField(referenceType: ReferenceType, itemId: Int, headerFieldType: HeaderFieldType, value: String) {
        uiState.update { state ->
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
        val referenceType = uiState.value.referenceType
        val repository = (repositories[referenceType] as? ReferenceRepository<out Reference>)
            ?: return

        uiState.update { it.copy(isLoading = true) }
        runCatching {
            repository.createItem()
        }.onSuccess { created ->
            uiState.update { state ->
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
            uiState.update { state ->
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
        val referenceType = uiState.value.referenceType
        val repository = (repositories[referenceType] as? ReferenceRepository<out Reference>)
            ?: return

        uiState.update { it.copy(isLoading = true) }
        runCatching {
            repository.deleteItem(item.id)
        }.onSuccess {
            uiState.update { state ->
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
            uiState.update { state ->
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
        val referenceType = uiState.value.referenceType
        val repository = (repositories[referenceType] as? ReferenceRepository<out Reference>)
            ?: return

        uiState.update { it.copy(isLoading = true) }
        runCatching {
            val updatedItems = uiState.value.buildUpdatedItems(referenceType)
            updatedItems.forEach { item ->
                updateItem(repository, item)
            }
            updatedItems
        }.onSuccess { updatedItems ->
            uiState.update { state ->
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
            uiState.update { state ->
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