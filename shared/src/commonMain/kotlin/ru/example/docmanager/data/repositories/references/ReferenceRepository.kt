/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.repositories.references

import ru.example.docmanager.data.reference.Reference

abstract class ReferenceRepository<T : Reference>(
    val name: String
) {
    abstract suspend fun getItem(id: Int): T
    abstract suspend fun getItems(): List<T>
    abstract suspend fun createItem(item: T): T
    abstract suspend fun createItem(): T
    abstract suspend fun updateItem(item: T)
    abstract suspend fun deleteItem(id: Int)
}