/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.database

data class Metadata(
    val name: String,
    val version: String,
    val creationDate: String,
    val author: String
)