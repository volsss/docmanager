/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License v3.0.
 *
 * Distributed WITHOUT ANY WARRANTY. See the GNU General Public License
 * for more details: <https://gnu.org>
 */

package ru.example.docmanager.data.document.type.powerofattorney

import ru.example.docmanager.data.document.Document
import ru.example.docmanager.data.document.DocumentType
import ru.example.docmanager.data.document.WithBody

class PowerOfAttorney(
    override val header: PowerOfAttorneyHeader,
    override val body: PowerOfAttorneyBody,
    override val type: DocumentType = DocumentType.POWER_OF_ATTORNEY
) : Document, WithBody<PowerOfAttorneyBodyItem>
