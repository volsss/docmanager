/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License v3.0.
 *
 * Distributed WITHOUT ANY WARRANTY. See the GNU General Public License
 * for more details: <https://gnu.org>
 */

package ru.example.docmanager.ui.viewmodel.reference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.example.docmanager.data.repositories.references.BaseReferenceRepository
import ru.example.docmanager.data.repositories.references.ReferenceRepository
import ru.example.docmanager.domain.document.header.HeaderFieldType
import ru.example.docmanager.domain.reference.Reference
import ru.example.docmanager.domain.reference.ReferenceType
import ru.example.docmanager.ui.router.Route

class ReferenceViewModel(
    private val repositories: Map<ReferenceType, BaseReferenceRepository>,
    private val destination: Route.Reference
) : ViewModel() {
    val uiState: StateFlow<ReferenceUiState>
        field = MutableStateFlow(ReferenceUiState())

    fun loadReferenceItems(
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            loadReferenceItemsSuspend(onError)
        }
    }

    suspend fun loadReferenceItemsSuspend(
        onError: (String) -> Unit
    ) {
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
            if (error is CancellationException) {
                currentCoroutineContext().ensureActive()
            }
            uiState.update { state ->
                state.copy(isLoading = false)
            }
            onError.invoke("Ошибка при загрузке справочников: ${error.message}")
        }
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

    fun createItem(
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            createItemSuspend(onError)
        }
    }

    suspend fun createItemSuspend(
        onError: (String) -> Unit
    ) {
        val referenceType = uiState.value.referenceType
        val repository = (repositories[referenceType] as? ReferenceRepository<out Reference>)
            ?: return

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
                    fieldValues = updatedFieldValues
                )
            }
        }.onFailure { error ->
            if (error is CancellationException) {
                currentCoroutineContext().ensureActive()
            }
            onError("Ошибка при создании записи: ${error.message}")
        }
    }

    fun deleteItem(
        item: Reference,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            deleteItemSuspend(item, onError)
        }
    }

    suspend fun deleteItemSuspend(
        item: Reference,
        onError: (String) -> Unit
    ) {
        val referenceType = uiState.value.referenceType
        val repository = (repositories[referenceType] as? ReferenceRepository<out Reference>)
            ?: return

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
                    fieldValues = updatedFieldValues
                )
            }
        }.onFailure { error ->
            if (error is CancellationException) {
                currentCoroutineContext().ensureActive()
            }
            onError("Ошибка при удалении записи: ${error.message}")
        }
    }

    fun saveAll(
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            saveAllSuspend(onError)
        }
    }

    suspend fun saveAllSuspend(
        onError: (String) -> Unit
    ) {
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
                    isLoading = false
                )
            }
        }.onFailure { error ->
            if (error is CancellationException) {
                currentCoroutineContext().ensureActive()
            }
            uiState.update { state ->
                state.copy(
                    isLoading = false
                )
            }
            onError("Ошибка при сохранении изменений: ${error.message}")
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