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

package ru.example.docmanager.ui.viewmodel.forms.powerofattorney

import androidx.lifecycle.viewModelScope
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.document_power_of_attorney
import docmanager.shared.generated.resources.document_power_of_attorney_resource
import docmanager.shared.generated.resources.form_error_failed_init
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import ru.example.docmanager.data.document.Document
import ru.example.docmanager.data.document.type.powerofattorney.PowerOfAttorney
import ru.example.docmanager.data.repositories.documents.PowerOfAttorneyRepository
import ru.example.docmanager.data.repositories.references.IndividualsRepository
import ru.example.docmanager.data.repositories.references.OrganizationsRepository
import ru.example.docmanager.data.repositories.references.ProductsRepository
import ru.example.docmanager.data.repositories.references.SuppliersRepository
import ru.example.docmanager.ui.viewmodel.forms.FormViewModel


class PowerOfAttorneyViewModel(
    powerOfAttorneyRepository: PowerOfAttorneyRepository,
    private val individualsRepository: IndividualsRepository,
    private val organizationsRepository: OrganizationsRepository,
    private val productsRepository: ProductsRepository,
    private val suppliersRepository: SuppliersRepository
) : FormViewModel<PowerOfAttorney, PowerOfAttorneyUiState>(
    repository = powerOfAttorneyRepository,
    documentName = Res.string.document_power_of_attorney,
    documentResource = Res.string.document_power_of_attorney_resource
) {
    override val uiState: StateFlow<PowerOfAttorneyUiState>
        field = MutableStateFlow(PowerOfAttorneyUiState())

    override fun onInit(
        onStatusUpdate: (String) -> Unit,
        document: Document?,
    ) {
        viewModelScope.launch {
            setLoading(true)
            uiState.value = uiState.value.copy(
                individuals = individualsRepository.getItems(),
                suppliers = suppliersRepository.getItems(),
                organizations = organizationsRepository.getItems(),
                products = productsRepository.getItems()
            )

            when (document) {
                null -> Unit
                is PowerOfAttorney -> {
                    uiState.value.populateFromDocument(document)
                }

                else -> {
                    onStatusUpdate.invoke(
                        getString(Res.string.form_error_failed_init)
                    )
                }
            }
            setLoading(false)
        }
    }

    override fun setLoading(loading: Boolean) {
        uiState.value.isLoading = loading
    }
}
