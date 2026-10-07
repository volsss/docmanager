package ru.example.docmanager.ui.screens.forms.powerOfAttorney

import ru.example.docmanager.reference.Product
import ru.example.docmanager.ui.screens.forms.BodyItem

data class PowerOfAttorneyBodyItemState (
    override val id: Int,
    val product: Product? = null,
    val unit: String = "шт",
    val count: String = "Один"
): BodyItem(id)