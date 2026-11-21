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

package ru.example.docmanager.ui.viewmodel.forms

import ru.example.docmanager.domain.document.Document

sealed interface FormEvent {

    data class Status(val message: String) : FormEvent
    data class DocumentSaved<T : Document>(val document: T) : FormEvent
    data object DocumentUpdated : FormEvent
    data object DocumentDeleted : FormEvent
    data object DocumentExported : FormEvent
    data object DocumentPrinted : FormEvent
    data class Error(val message: String) : FormEvent
}
