/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens.document.forms.powerOfAttorney

import ru.example.docmanager.data.reference.Product
import ru.example.docmanager.ui.screens.document.forms.BodyItem

data class PowerOfAttorneyBodyItemState(
    override val id: Int,
    val product: Product? = null,
    val unit: String = "шт",
    val count: String = "Один"
) : BodyItem(id)