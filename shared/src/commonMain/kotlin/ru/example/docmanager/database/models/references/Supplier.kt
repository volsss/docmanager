package ru.example.docmanager.database.models.references

data class Supplier(
    override var id: Int = -1,
    val name: String = ""
) : Reference(id) {
    override fun toMap(): Map<String, String> = mapOf(
        "supplierName" to name
    )
    override fun copyWithFields(fields: Map<String, Any>) = copy(
        name = (fields["supplierName"] ?: name) as String
    )
}