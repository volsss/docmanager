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

import androidx.compose.ui.util.fastFilteredMap
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.form_error_empty_required
import kotlinx.datetime.*
import org.jetbrains.compose.resources.getString
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

    override val documentId: Int? = null,
    override val number: Int = 1,
    override val dischargeDate: LocalDate =
        Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val endDate: LocalDate = dischargeDate.plus(DatePeriod(days = 30)),
    val organization: Organization? = null,
    val individual: Individual? = null,
    val supplier: Supplier? = null,
    val supplierAgreement: String = "",

    var bodyItems: List<PowerOfAttorneyBodyItemState> = emptyList(),

    override val isLoading: Boolean = true
) : FormState<PowerOfAttorney>(DocumentType.POWER_OF_ATTORNEY) {

    override suspend fun toDocument(
        onError: ((String) -> Unit)?
    ): PowerOfAttorney? {
        if (organization == null || individual == null || supplier == null
        ) {
            onError?.invoke(getString(Res.string.form_error_empty_required))
            return null
        }

        val header = PowerOfAttorneyHeader(
            id = documentId ?: 0,
            organization = organization,
            number = number,
            dischargeDate = dischargeDate,
            endDate = endDate,
            individual = individual,
            supplier = supplier,
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