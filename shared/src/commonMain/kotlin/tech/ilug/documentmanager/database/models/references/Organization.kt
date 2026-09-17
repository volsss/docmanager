package tech.ilug.documentmanager.database.models.references

import tech.ilug.documentmanager.database.repositories.references.ReferenceRepository

data class Organization(
    override val id: Int = -1,
    val name: String = "",
    val consumer: String = "",
    val payer: String = "",
    val account: String = ""
) : ReferenceModel(id) {
    fun replacements(): Map<String, String> = mapOf(
        "organizationName" to name,
        "organizationConsumer" to consumer,
        "organizationPayer" to payer,
        "organizationAccount" to account,
    )

    override fun asFields(): List<ReferenceField<out Any>> = listOf(
        ReferenceField("organizationName", name),
        ReferenceField("organizationConsumer", consumer),
        ReferenceField("organizationPayer", payer),
        ReferenceField("organizationAccount", account)
    )

    override fun copyWithFields(fields: Map<String, Any>) = copy(
        name = (fields["organizationName"] ?: name) as String,
        consumer = (fields["organizationConsumer"] ?: consumer) as String,
        payer = (fields["organizationPayer"] ?: payer) as String,
        account = (fields["organizationAccount"] ?: account) as String
    )
}