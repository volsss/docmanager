package tech.ilug.documentmanager.ui.screens.forms.powerOfAttorney

import tech.ilug.documentmanager.model.PowerOfAttorney

data class PowerOfAttorneyBodyItem(
    val product: PowerOfAttorney.Product? = null,
    val unit: String = "шт",
    val count: String = "Один"
)