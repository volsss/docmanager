/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.document.type.powerofattorney

import ru.example.docmanager.data.document.body.DocumentBodyItem
import ru.example.docmanager.data.reference.Product

data class PowerOfAttorneyBodyItem(
    override val id: Int,
    var count: String = "",
    var unit: String = "",
    val product: Product
) : DocumentBodyItem
