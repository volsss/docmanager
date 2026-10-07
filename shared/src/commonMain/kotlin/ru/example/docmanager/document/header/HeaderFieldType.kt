package ru.example.docmanager.document.header

import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.header_field_discharge_date
import docmanager.shared.generated.resources.header_field_end_date
import docmanager.shared.generated.resources.header_field_individual_date
import docmanager.shared.generated.resources.header_field_individual_issued
import docmanager.shared.generated.resources.header_field_individual_job
import docmanager.shared.generated.resources.header_field_individual_name
import docmanager.shared.generated.resources.header_field_individual_number
import docmanager.shared.generated.resources.header_field_individual_series
import docmanager.shared.generated.resources.header_field_number
import docmanager.shared.generated.resources.header_field_organization_account
import docmanager.shared.generated.resources.header_field_organization_consumer
import docmanager.shared.generated.resources.header_field_organization_name
import docmanager.shared.generated.resources.header_field_organization_payer
import docmanager.shared.generated.resources.header_field_product_name
import docmanager.shared.generated.resources.header_field_supplier_agreement
import docmanager.shared.generated.resources.header_field_supplier_name
import org.jetbrains.compose.resources.StringResource

enum class HeaderFieldType (
    val stringResource: StringResource,
    val placeholder: String,
) {
    // ------------------   DEFAULT   --------------------
    NUMBER(Res.string.header_field_number, "{{number}}"),
    DISCHARGE_DATE(Res.string.header_field_discharge_date, "{{dischargeDate}}"),

    // --------------  POWER OF ATTORNEY  ------------------
    END_DATE(Res.string.header_field_end_date, "{{endDate}}"),
    SUPPLIER_AGREEMENT(Res.string.header_field_supplier_agreement, "{{supplierAgreement}}"),
    INDIVIDUAL_JOB(Res.string.header_field_individual_job, "{{individualJob}}"),
    INDIVIDUAL_NAME(Res.string.header_field_individual_name, "{{individualName}}"),
    INDIVIDUAL_SERIES(Res.string.header_field_individual_series, "{{individualSeries}}"),
    INDIVIDUAL_NUMBER(Res.string.header_field_individual_number, "{{individualNumber}}"),
    INDIVIDUAL_ISSUED(Res.string.header_field_individual_issued, "{{individualIssued}}"),
    INDIVIDUAL_DATE(Res.string.header_field_individual_date, "{{individualDate}}"),
    ORGANIZATION_NAME(Res.string.header_field_organization_name, "{{organizationName}}"),
    ORGANIZATION_CONSUMER(Res.string.header_field_organization_consumer, "{{organizationConsumer}}"),
    ORGANIZATION_PAYER(Res.string.header_field_organization_payer, "{{organizationPayer}}"),
    ORGANIZATION_ACCOUNT(Res.string.header_field_organization_account, "{{organizationAccount}}"),
    PRODUCT_NAME(Res.string.header_field_product_name, "{{productName}}"),
    SUPPLIER_NAME(Res.string.header_field_supplier_name, "{{supplierName}}"),
}