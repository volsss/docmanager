package ru.example.docmanager.ui.screens.forms.powerOfAttorney

import ru.example.docmanager.database.models.references.Product

data class PowerOfAttorneyBodyItem(
    val product: Product? = null,
    val unit: String = "шт",
    val count: String = "Один"
)