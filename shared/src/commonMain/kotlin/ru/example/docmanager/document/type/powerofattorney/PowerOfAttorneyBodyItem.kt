package ru.example.docmanager.document.type.powerofattorney

import ru.example.docmanager.document.DocumentBodyItem
import ru.example.docmanager.reference.Product

data class PowerOfAttorneyBodyItem (
    override val id: Int,
    var count: String = "",
    var unit: String = "",
    val product: Product
): DocumentBodyItem
