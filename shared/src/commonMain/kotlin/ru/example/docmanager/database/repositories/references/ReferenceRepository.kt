package ru.example.docmanager.database.repositories.references

import ru.example.docmanager.database.models.references.ReferenceModel

abstract class ReferenceRepository<T : ReferenceModel>(val name: String) {
    abstract suspend fun getItem(id: Int): T
    abstract suspend fun getItems(): List<T>
    abstract suspend fun newItem(): T
    abstract suspend fun createItem(item: T): T
    abstract suspend fun updateItem(item: T)
    abstract suspend fun deleteItem(id: Int)
}