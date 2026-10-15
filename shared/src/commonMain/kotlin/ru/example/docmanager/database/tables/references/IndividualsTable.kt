/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.database.tables.references

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.javatime.date

object IndividualsTable : IntIdTable("individuals") {
    val job = varchar("job", 128)
    val name = varchar("name", 128)
    val series = varchar("series", 16)
    val number = varchar("number", 16)
    val issued = varchar("issued", 128)
    val date = date("date")
}