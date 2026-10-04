package ru.example.docmanager.database.models.references

import ru.example.docmanager.database.models.FieldType

abstract class Reference(
    open var id: Int
) {
    abstract fun toMap(): Map<FieldType, String>
    abstract fun copyWithFields(fields: Map<FieldType, String>): Reference

    override fun equals(other: Any?): Boolean =
        other is Reference && this.id == other.id
    override fun hashCode(): Int = id
    override fun toString(): String = "Item(id=$id)"
}