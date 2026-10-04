package ru.example.docmanager.ui.screens.forms.powerOfAttorney

import ru.example.docmanager.database.models.references.Product
import ru.example.docmanager.ui.screens.forms.BodyItem

data class PowerOfAttorneyBodyItem (
    override val id: Int,
    val product: Product? = null,
    val unit: String = "шт",
    val count: String = "Один"
): BodyItem(id)