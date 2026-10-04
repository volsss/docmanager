package ru.example.docmanager.database.models.references

import ru.example.docmanager.database.models.FieldType

data class Product(
    override var id: Int = -1,
    val name: String = ""
) : Reference(id) {
    override fun toMap() = mapOf(
        FieldType.PRODUCT_NAME to name,
    )

    override fun copyWithFields(fields: Map<FieldType, String>) = copy(
        name = (fields[FieldType.PRODUCT_NAME] ?: name)
    )
}