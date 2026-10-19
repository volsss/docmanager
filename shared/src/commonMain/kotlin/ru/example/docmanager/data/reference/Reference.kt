/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License v3.0.
 *
 * Distributed WITHOUT ANY WARRANTY. See the GNU General Public License
 * for more details: <https://gnu.org>
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