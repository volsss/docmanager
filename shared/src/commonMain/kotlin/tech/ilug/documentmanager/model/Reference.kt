package tech.ilug.documentmanager.model

abstract class Reference<T : Reference.Item>(val name: String) {
    abstract suspend fun getItem(id: Int): T
    abstract suspend fun getItems(): List<T>
    abstract suspend fun newItem(): T
    abstract suspend fun createItem(item: T): T
    abstract suspend fun updateItem(item: T)
    abstract suspend fun deleteItem(id: Int)

    abstract class Item(open val id: Int) {
        abstract fun asFields(): List<Field<out Any>>
        abstract fun copyWithFields(fields: Map<String, Any>): Item

        override fun equals(other: Any?): Boolean = other is Item && this.id == other.id
        override fun hashCode(): Int = id
        override fun toString(): String = "Item(id=$id)"
    }

    class Field<T>(val name: String, var value: T)
}