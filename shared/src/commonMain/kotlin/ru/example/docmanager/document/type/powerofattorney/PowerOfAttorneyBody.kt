/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.document.type.powerofattorney

import ru.example.docmanager.document.body.BodyFieldType
import ru.example.docmanager.document.body.DocumentBody

data class PowerOfAttorneyBody (
    override val items: List<PowerOfAttorneyBodyItem>
): DocumentBody<PowerOfAttorneyBodyItem> {
    override fun toMapByColumns() = mapOf(
        BodyFieldType.NUMBER_SORTED to List(items.size) { idx -> (idx + 1).toString() },
        BodyFieldType.PRODUCTS to items.map { it.product.name },
        BodyFieldType.UNIT to items.map { it.unit },
        BodyFieldType.COUNT to items.map { it.count }
    )
}
