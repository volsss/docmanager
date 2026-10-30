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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.getString
import ru.example.docmanager.data.document.Document
import ru.example.docmanager.data.document.type.powerofattorney.PowerOfAttorney
import ru.example.docmanager.data.reference.Individual
import ru.example.docmanager.data.reference.Organization
import ru.example.docmanager.data.reference.Supplier
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

    fun onNumberChanged(number: Int) {
        uiState.update { it.copy(number = number) }
    }

    fun onDischargeDateChanged(dischargeDate: LocalDate) {
        uiState.update { it.copy(dischargeDate = dischargeDate) }
    }

    fun onEndDateChanged(endDate: LocalDate) {
        uiState.update { it.copy(endDate = endDate) }
    }

    fun onOrganizationChanged(organization: Organization?) {
        uiState.update { it.copy(organization = organization) }
    }

    fun onIndividualChanged(individual: Individual?) {
        uiState.update { it.copy(individual = individual) }
    }

    fun onSupplierChanged(supplier: Supplier?) {
        uiState.update { it.copy(supplier = supplier) }
    }

    fun onSupplierAgreementChanged(supplierAgreement: String) {
        uiState.update { it.copy(supplierAgreement = supplierAgreement) }
    }

    fun addBodyItem() {
        uiState.update {
            it.copy(
                bodyItems = it.bodyItems + PowerOfAttorneyBodyItemState(
                    id = it.bodyItems.size
                )
            )
        }
    }

    fun updateBodyItem(index: Int, item: PowerOfAttorneyBodyItemState) {
        uiState.update { old ->
            old.copy(
                bodyItems = old.bodyItems.toMutableList().also {
                    if (index in it.indices) it[index] = item
                }
            )
        }
    }

    fun removeBodyItem(index: Int) {
        uiState.update {
            it.copy(
                bodyItems = if (it.bodyItems.size > 1) {
                    it.bodyItems.filterIndexed { i, _ -> i != index }
                } else {
                    listOf()
                }
            )
        }
    }

    override fun onInit(
        onStatusUpdate: (String) -> Unit,
        document: Document?,
    ) {
        viewModelScope.launch {
            setLoading(true)

            try {
                val individualsDeferred = async {
                    individualsRepository.getItems()
                }
                val suppliersDeferred = async {
                    suppliersRepository.getItems()
                }
                val organizationsDeferred = async {
                    organizationsRepository.getItems()
                }
                val productsDeferred = async {
                    productsRepository.getItems()
                }

                val individuals = individualsDeferred.await()
                val suppliers = suppliersDeferred.await()
                val organizations = organizationsDeferred.await()
                val products = productsDeferred.await()

                uiState.update {
                    it.copy(
                        individuals = individuals,
                        suppliers = suppliers,
                        organizations = organizations,
                        products = products
                    )
                }

                when (document) {
                    null -> Unit
                    is PowerOfAttorney -> {
                        uiState.update { old ->
                            old.copy(
                                documentId = document.header.id,

                                number = document.header.number,
                                dischargeDate = document.header.dischargeDate,
                                endDate = document.header.endDate,
                                organization = organizations.firstOrNull {
                                    it.id == document.header.organization.id
                                } ?: document.header.organization,
                                individual = individuals.firstOrNull {
                                    it.id == document.header.individual.id
                                } ?: document.header.individual,
                                supplier = suppliers.firstOrNull {
                                    it.id == document.header.supplier.id
                                } ?: document.header.supplier,
                                supplierAgreement = document.header.supplierAgreement,

                                bodyItems = if (document.body.items.isNotEmpty()) {
                                    document.body.items.map { body ->
                                        PowerOfAttorneyBodyItemState(
                                            id = body.id,
                                            product = products.firstOrNull {
                                                it.id == body.product.id
                                            } ?: body.product,
                                            unit = body.unit,
                                            count = body.count
                                        )
                                    }
                                } else {
                                    listOf()
                                }
                            )
                        }
                    }

                    else -> {
                        onStatusUpdate.invoke(
                            getString(Res.string.form_error_failed_init)
                        )
                    }
                }
            } finally {
                setLoading(false)
            }
        }
    }

    override fun setLoading(loading: Boolean) {
        uiState.update { it.copy(isLoading = loading) }
    }
}
