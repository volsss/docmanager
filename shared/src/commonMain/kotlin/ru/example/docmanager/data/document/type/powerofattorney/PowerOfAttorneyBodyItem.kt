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

package ru.example.docmanager.data.document.type.powerofattorney

import ru.example.docmanager.data.document.body.DocumentBodyItem
import ru.example.docmanager.data.reference.Product

data class PowerOfAttorneyBodyItem(
    override val id: Int,
    var count: String = "",
    var unit: String = "",
    val product: Product
) : DocumentBodyItem
