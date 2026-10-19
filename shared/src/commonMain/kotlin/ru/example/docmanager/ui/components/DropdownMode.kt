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

package ru.example.docmanager.ui.components

enum class DropdownMode {
    /**
     * Пользователь может только выбрать значение из списка.
     * Ручной ввод запрещен.
     */
    VALUES_ONLY,

    /**
     * Пользователь может:
     * - выбрать значение из списка
     * - ввести любое произвольное значение.
     */
    VALUES_AND_INPUT,

    /**
     * Пользователь может печатать для поиска,
     * но итоговое значение обязано быть одним из `items`.
     *
     * То есть:
     * `draft != committedValue`
     * пока пользователь печатает,
     * но `onValueChange` вызывается только для валидного значения.
     */
    STRICT_VALUES_AND_INPUT,
}