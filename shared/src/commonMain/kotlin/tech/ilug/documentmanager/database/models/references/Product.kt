package tech.ilug.documentmanager.database.models.references

data class Product(
    override val id: Int = -1,
    val name: String = ""
) : ReferenceModel(id) {
    override fun asFields(): List<ReferenceField<out Any>> = listOf(ReferenceField("productName", name))
    override fun copyWithFields(fields: Map<String, Any>) = copy(
        name = (fields["productName"] ?: name) as String
    )
}