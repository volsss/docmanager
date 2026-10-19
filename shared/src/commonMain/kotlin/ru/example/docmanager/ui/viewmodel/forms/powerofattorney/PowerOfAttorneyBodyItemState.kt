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

import ru.example.docmanager.data.reference.Product
import ru.example.docmanager.ui.viewmodel.forms.BodyItem

data class PowerOfAttorneyBodyItemState(
    override val id: Int,
    val product: Product? = null,
    val unit: String = "шт",
    val count: String = "Один"
) : BodyItem(id)