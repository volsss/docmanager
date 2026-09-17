package tech.ilug.documentmanager.ui.screens.forms.powerOfAttorney

import tech.ilug.documentmanager.database.models.references.Product

data class PowerOfAttorneyBodyItem(
    val product: Product? = null,
    val unit: String = "шт",
    val count: String = "Один"
)