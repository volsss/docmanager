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

package ru.example.docmanager.data.repositories.references

import ru.example.docmanager.data.reference.Reference

abstract class ReferenceRepository<T : Reference>(
    val name: String
) : BaseReferenceRepository {
    abstract suspend fun getItem(id: Int): T
    abstract suspend fun getItems(): List<T>
    abstract suspend fun createItem(item: T): T
    abstract suspend fun createItem(): T
    abstract suspend fun updateItem(item: T)
    abstract suspend fun deleteItem(id: Int)
}