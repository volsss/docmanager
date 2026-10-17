/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.document.type.powerofattorney

import ru.example.docmanager.data.document.DocumentType

class PowerOfAttorney(
    override val header: PowerOfAttorneyHeader,
    override val body: PowerOfAttorneyBody,
    override val type: DocumentType = DocumentType.POWER_OF_ATTORNEY
) : ru.example.docmanager.data.document.Document,
    ru.example.docmanager.data.document.WithBody<PowerOfAttorneyBodyItem>
