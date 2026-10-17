/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.repositories.references

abstract class ReferenceRepository<T : ru.example.docmanager.data.reference.Reference>(
    val name: String
) {
    abstract suspend fun getItem(id: Int): T
    abstract suspend fun getItems(): List<T>
    abstract suspend fun createItem(item: T): T
    abstract suspend fun createItem(): T
    abstract suspend fun updateItem(item: T)
    abstract suspend fun deleteItem(id: Int)
}