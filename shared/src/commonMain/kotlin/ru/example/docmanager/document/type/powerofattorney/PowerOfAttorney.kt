/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.document.type.powerofattorney

import ru.example.docmanager.document.Document
import ru.example.docmanager.document.DocumentType
import ru.example.docmanager.document.WithBody

class PowerOfAttorney (
    override val header: PowerOfAttorneyHeader,
    override val body: PowerOfAttorneyBody,
    override val type: DocumentType = DocumentType.POWER_OF_ATTORNEY
): Document, WithBody<PowerOfAttorneyBodyItem>
