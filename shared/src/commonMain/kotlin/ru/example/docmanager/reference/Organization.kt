package ru.example.docmanager.reference

import ru.example.docmanager.document.HeaderFieldType

data class Organization(
    override var id: Int = -1,
    val name: String = "",
    val consumer: String = "",
    val payer: String = "",
    val account: String = ""
) : Reference(id) {
    override fun toMap() = mapOf(
        HeaderFieldType.ORGANIZATION_NAME to name,
        HeaderFieldType.ORGANIZATION_CONSUMER to consumer,
        HeaderFieldType.ORGANIZATION_PAYER to payer,
        HeaderFieldType.ORGANIZATION_ACCOUNT to account,
    )

    override fun copyWithFields(fields: Map<HeaderFieldType, String>) = copy(
        name = (fields[HeaderFieldType.ORGANIZATION_NAME] ?: name),
        consumer = (fields[HeaderFieldType.ORGANIZATION_CONSUMER] ?: consumer),
        payer = (fields[HeaderFieldType.ORGANIZATION_PAYER] ?: payer),
        account = (fields[HeaderFieldType.ORGANIZATION_ACCOUNT] ?: account)
    )
}