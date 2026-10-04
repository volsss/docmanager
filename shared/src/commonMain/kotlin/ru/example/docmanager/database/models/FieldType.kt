package ru.example.docmanager.database.models

import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.dischargeDate
import docmanager.shared.generated.resources.endDate
import docmanager.shared.generated.resources.individual
import docmanager.shared.generated.resources.individualDate
import docmanager.shared.generated.resources.individualIssued
import docmanager.shared.generated.resources.individualJob
import docmanager.shared.generated.resources.individualName
import docmanager.shared.generated.resources.individualNumber
import docmanager.shared.generated.resources.individualSeries
import docmanager.shared.generated.resources.number
import docmanager.shared.generated.resources.organization
import docmanager.shared.generated.resources.organizationAccount
import docmanager.shared.generated.resources.organizationConsumer
import docmanager.shared.generated.resources.organizationName
import docmanager.shared.generated.resources.organizationPayer
import docmanager.shared.generated.resources.product
import docmanager.shared.generated.resources.productName
import docmanager.shared.generated.resources.supplier
import docmanager.shared.generated.resources.supplierAgreement
import docmanager.shared.generated.resources.supplierName
import org.jetbrains.compose.resources.StringResource

enum class FieldType (
    val stringResource: StringResource,
    val placeholder: String,
) {
    // ------------------   DEFAULT   --------------------
    NUMBER(Res.string.number, "{{number}}"),
    DISCHARGE_DATE(Res.string.dischargeDate, "{{dischargeDate}}"),

    // --------------  POWER OF ATTORNEY  ------------------
    END_DATE(Res.string.endDate, "{{endDate}}"),
    SUPPLIER_AGREEMENT(Res.string.supplierAgreement, "{{supplierAgreement}}"),

    // -----------------  REFERENCES  --------------------
    INDIVIDUAL_JOB(Res.string.individualJob, "{{individualJob}}"),
    INDIVIDUAL_NAME(Res.string.individualName, "{{individualName}}"),
    INDIVIDUAL_SERIES(Res.string.individualSeries, "{{individualSeries}}"),
    INDIVIDUAL_NUMBER(Res.string.individualNumber, "{{individualNumber}}"),
    INDIVIDUAL_ISSUED(Res.string.individualIssued, "{{individualIssued}}"),
    INDIVIDUAL_DATE(Res.string.individualDate, "{{individualDate}}"),

    ORGANIZATION_NAME(Res.string.organizationName, "{{organizationName}}"),
    ORGANIZATION_CONSUMER(Res.string.organizationConsumer, "{{organizationConsumer}}"),
    ORGANIZATION_PAYER(Res.string.organizationPayer, "{{organizationPayer}}"),
    ORGANIZATION_ACCOUNT(Res.string.organizationAccount, "{{organizationAccount}}"),

    PRODUCT_NAME(Res.string.productName, "{{productName}}"),

    SUPPLIER_NAME(Res.string.supplierName, "{{supplierName}}"),
}