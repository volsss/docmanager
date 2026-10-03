package ru.example.docmanager.database.models.references

data class Organization(
    override var id: Int = -1,
    val name: String = "",
    val consumer: String = "",
    val payer: String = "",
    val account: String = ""
) : Reference(id) {
    override fun toMap(): Map<String, String> = mapOf(
        "organizationName" to name,
        "organizationConsumer" to consumer,
        "organizationPayer" to payer,
        "organizationAccount" to account,
    )

    override fun copyWithFields(fields: Map<String, Any>) = copy(
        name = (fields["organizationName"] ?: name) as String,
        consumer = (fields["organizationConsumer"] ?: consumer) as String,
        payer = (fields["organizationPayer"] ?: payer) as String,
        account = (fields["organizationAccount"] ?: account) as String
    )
}