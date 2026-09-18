package ru.example.docmanager.database.models.references

abstract class ReferenceModel(open val id: Int) {
    abstract fun asFields(): List<ReferenceField<out Any>>
    abstract fun copyWithFields(fields: Map<String, Any>): ReferenceModel

    override fun equals(other: Any?): Boolean = other is ReferenceModel && this.id == other.id
    override fun hashCode(): Int = id
    override fun toString(): String = "Item(id=$id)"
}