package ru.example.docmanager.references

import ru.example.docmanager.documents.base.HeaderFieldType
import ru.example.docmanager.references.base.Reference

data class Supplier(
    override var id: Int = -1,
    val name: String = ""
) : Reference(id) {
    override fun toMap() = mapOf(
        HeaderFieldType.SUPPLIER_NAME to name
    )
    override fun copyWithFields(fields: Map<HeaderFieldType, String>) = copy(
        name = (fields[HeaderFieldType.SUPPLIER_NAME] ?: name)
    )
}