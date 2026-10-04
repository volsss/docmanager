package ru.example.docmanager.database.models.references

import ru.example.docmanager.database.models.FieldType

data class Supplier(
    override var id: Int = -1,
    val name: String = ""
) : Reference(id) {
    override fun toMap() = mapOf(
        FieldType.SUPPLIER_NAME to name
    )
    override fun copyWithFields(fields: Map<FieldType, String>) = copy(
        name = (fields[FieldType.SUPPLIER_NAME] ?: name)
    )
}