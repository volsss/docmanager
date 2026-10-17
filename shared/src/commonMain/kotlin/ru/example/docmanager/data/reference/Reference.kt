/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.reference

import ru.example.docmanager.data.document.header.HeaderFieldType

abstract class Reference(
    open var id: Int,
    val type: ReferenceType
) {
    abstract fun toMap(): Map<HeaderFieldType, String>
    abstract fun copyWithFields(fields: Map<HeaderFieldType, String>): Reference

    override fun equals(other: Any?): Boolean =
        other is Reference && this.id == other.id

    override fun hashCode(): Int = id
    override fun toString(): String = "Item(id=$id)"
}