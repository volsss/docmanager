package ru.example.docmanager.database.models.references

import ru.example.docmanager.database.models.FieldType

data class Organization(
    override var id: Int = -1,
    val name: String = "",
    val consumer: String = "",
    val payer: String = "",
    val account: String = ""
) : Reference(id) {
    override fun toMap() = mapOf(
        FieldType.ORGANIZATION_NAME to name,
        FieldType.ORGANIZATION_CONSUMER to consumer,
        FieldType.ORGANIZATION_PAYER to payer,
        FieldType.ORGANIZATION_ACCOUNT to account,
    )

    override fun copyWithFields(fields: Map<FieldType, String>) = copy(
        name = (fields[FieldType.ORGANIZATION_NAME] ?: name),
        consumer = (fields[FieldType.ORGANIZATION_CONSUMER] ?: consumer),
        payer = (fields[FieldType.ORGANIZATION_PAYER] ?: payer),
        account = (fields[FieldType.ORGANIZATION_ACCOUNT] ?: account)
    )
}