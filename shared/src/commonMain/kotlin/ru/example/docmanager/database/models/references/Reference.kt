package ru.example.docmanager.database.models.references

abstract class Reference(
    open var id: Int
) {
    abstract fun toMap(): Map<String, Any>
    abstract fun copyWithFields(fields: Map<String, Any>): Reference

    override fun equals(other: Any?): Boolean =
        other is Reference && this.id == other.id
    override fun hashCode(): Int = id
    override fun toString(): String = "Item(id=$id)"
}