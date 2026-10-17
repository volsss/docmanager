/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.document.type.powerofattorney

data class PowerOfAttorneyBodyItem(
    override val id: Int,
    var count: String = "",
    var unit: String = "",
    val product: ru.example.docmanager.data.reference.Product
) : ru.example.docmanager.data.document.body.DocumentBodyItem
