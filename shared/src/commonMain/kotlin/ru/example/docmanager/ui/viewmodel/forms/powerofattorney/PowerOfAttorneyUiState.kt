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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.fastFilteredMap
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import ru.example.docmanager.data.document.DocumentType
import ru.example.docmanager.data.document.type.powerofattorney.PowerOfAttorney
import ru.example.docmanager.data.document.type.powerofattorney.PowerOfAttorneyBody
import ru.example.docmanager.data.document.type.powerofattorney.PowerOfAttorneyBodyItem
import ru.example.docmanager.data.document.type.powerofattorney.PowerOfAttorneyHeader
import ru.example.docmanager.data.reference.Individual
import ru.example.docmanager.data.reference.Organization
import ru.example.docmanager.data.reference.Product
import ru.example.docmanager.data.reference.Supplier
import ru.example.docmanager.ui.viewmodel.forms.FormState
import kotlin.time.Clock

data class PowerOfAttorneyUiState(
    val individuals: List<Individual> = emptyList(),
    val organizations: List<Organization> = emptyList(),
    val products: List<Product> = emptyList(),
    val suppliers: List<Supplier> = emptyList(),
) : FormState<PowerOfAttorney, PowerOfAttorneyBodyItemState>(
    type = DocumentType.POWER_OF_ATTORNEY,
    today = Clock.System.todayIn(TimeZone.currentSystemDefault())
) {
    val defaultEndDate = today.plus(DatePeriod(days = 30))
    var endDate by mutableStateOf(defaultEndDate)
    var selectedOrganization by mutableStateOf<Organization?>(null)
    var selectedIndividual by mutableStateOf<Individual?>(null)
    var selectedSupplier by mutableStateOf<Supplier?>(null)
    var supplierAgreement by mutableStateOf("")

    override fun addBodyItem() {
        bodyItems = bodyItems + PowerOfAttorneyBodyItemState(
            id = bodyItems.size
        )
    }

    override fun populateFromDocument(
        document: PowerOfAttorney
    ) {
        selectedDocumentId = document.header.id
        number = document.header.number.toString()
        dischargeDate = document.header.dischargeDate
        endDate = document.header.endDate
        selectedOrganization =
            organizations.firstOrNull {
                it.id == document.header.organization.id
            } ?: document.header.organization
        selectedIndividual =
            individuals.firstOrNull {
                it.id == document.header.individual.id
            } ?: document.header.individual
        selectedSupplier =
            suppliers.firstOrNull {
                it.id == document.header.supplier.id
            } ?: document.header.supplier
        supplierAgreement = document.header.supplierAgreement
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
    }

    override fun toDocument(): PowerOfAttorney? {
        val org = selectedOrganization ?: return null
        val ind = selectedIndividual ?: return null
        val sup = selectedSupplier ?: return null

        val header = PowerOfAttorneyHeader(
            id = selectedDocumentId ?: 0,
            organization = org,
            number = number.toIntOrNull() ?: 0,
            dischargeDate = dischargeDate,
            endDate = endDate,
            individual = ind,
            supplier = sup,
            supplierAgreement = supplierAgreement
        )

        val bodyItemList = bodyItems.fastFilteredMap(
            { it.product != null },
        ) { item ->
            PowerOfAttorneyBodyItem(
                id = 0,
                count = item.count,
                unit = item.unit,
                product = item.product!!
            )
        }

        val body = PowerOfAttorneyBody(bodyItemList)

        return PowerOfAttorney(header, body)
    }
}