package ru.example.docmanager.database.models.references

data class Product(
    override var id: Int = -1,
    val name: String = ""
) : Reference(id) {
    override fun toMap() = mapOf(
        "productName" to name,
    )
    override fun copyWithFields(fields: Map<String, Any>) = copy(
        name = (fields["productName"] ?: name) as String
    )
}