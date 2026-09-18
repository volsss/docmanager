package ru.example.docmanager.database.models.references

data class Supplier(
    override val id: Int = -1,
    val name: String = ""
) : ReferenceModel(id) {
    fun replacements(): Map<String, String> = mapOf("supplierName" to name)
    override fun asFields(): List<ReferenceField<out Any>> = listOf(ReferenceField("supplierName", name))
    override fun copyWithFields(fields: Map<String, Any>) = copy(
        name = (fields["supplierName"] ?: name) as String
    )
}