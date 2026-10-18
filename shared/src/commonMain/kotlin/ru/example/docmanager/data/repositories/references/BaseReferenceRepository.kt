/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.repositories.references

import ru.example.docmanager.data.reference.ReferenceType

interface BaseReferenceRepository {
    val type: ReferenceType
}