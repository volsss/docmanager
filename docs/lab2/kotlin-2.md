# Теория Kotlin: Часть 2

## Функции

Функция объявляется с помощью ключевого слова `fun`.

После имени функции в круглых скобках указываются параметры, а после 
двоеточия — тип возвращаемого значения:

```kotlin
fun sum(a: Int, b: Int): Int {
    return a + b
}
```

Вызвать функцию можно по её имени:

```kotlin
val result = sum(2, 3)
println(result) // 5
```

Тип возвращаемого значения можно не указывать для функций, которые 
возвращают `Unit`:

```kotlin
fun sayHello(name: String) {
    println("Hello, $name")
}
```

`Unit` означает, что функция не возвращает полезного значения. 
Он примерно соответствует `void` в **Java**, но в Kotlin `Unit` является 
полноценным типом.

### Одно выражение

Если тело функции состоит из одного выражения, фигурные скобки и `return` 
можно не использовать:

```kotlin
fun sum(a: Int, b: Int) = a + b
```

**Kotlin** автоматически выводит тип результата из выражения.

При желании тип можно указать явно:

```kotlin
fun sum(a: Int, b: Int): Int = a + b
```

Такой синтаксис особенно часто используется для небольших функций.

### Параметры по умолчанию

Для параметров можно задать значение по умолчанию:

```kotlin
fun greet(name: String, greeting: String = "Hello") {
    println("$greeting, $name!")
}
```

Теперь функцию можно вызвать как с двумя аргументами:

```kotlin
greet("Alex", "Hi")
```

так и только с одним:

```kotlin
greet("Alex")
```

В последнем случае будет использовано значение `"Hello"`.

### Именованные аргументы

Аргументы можно передавать по имени:

```kotlin
fun createUser(name: String, age: Int) {
    println("$name: $age")
}

createUser(name = "Alex", age = 25)
```

Это делает вызов более понятным и позволяет передавать аргументы не строго 
в порядке объявления параметров.

## Null safety

Одна из важных особенностей **Kotlin** — встроенная защита от `null`.

По умолчанию обычная переменная не может содержать `null`:

```kotlin
val name: String = "Kotlin"
```

Следующая запись вызовет ошибку компиляции:

```kotlin
// val name: String = null
```

Чтобы разрешить `null`, используется nullable-тип со знаком `?`:

```kotlin
val name: String? = null
```

Теперь переменная может содержать как строку, так и `null`.

### Безопасный вызов

Чтобы безопасно обратиться к объекту, который может быть `null`, 
используется оператор `?.`:

```kotlin
val name: String? = null

println(name?.length)
```

Если `name` содержит `null`, выражение `name?.length` также даст `null` 
вместо возникновения `NullPointerException`.

### Оператор Elvis

Оператор `?:` позволяет указать значение, которое будет использовано 
вместо `null`:

```kotlin
val name: String? = null

val length = name?.length ?: 0

println(length) // 0
```

Читается это примерно как:

> если `name?.length` не `null`, используй его; иначе используй `0`.

### Явная проверка на `null`

Можно выполнить обычную проверку:

```kotlin
val name: String? = "Kotlin"

if (name != null) {
    println(name.length)
}
```

После проверки Kotlin обычно автоматически учитывает, что внутри этой 
ветки значение не равно `null`.

### Оператор `!!`

Оператор `!!` принудительно рассматривает nullable-значение как не `null`:

```kotlin
val name: String? = "Kotlin"

println(name!!.length)
```

Если в момент выполнения `name` окажется равен `null`, будет выброшен 
`NullPointerException`.

Поэтому `!!` следует использовать осторожно. Во многих случаях безопаснее 
применить `?.`, проверку на `null` или оператор `?:`.

## Классы

Класс объявляется с помощью ключевого слова `class`:

```kotlin
class User
```

Создать объект класса можно напрямую:

```kotlin
val user = User()
```

Свойства можно объявлять прямо в заголовке класса:

```kotlin
class User(
    val name: String,
    val age: Int
)
```

Теперь объект можно создать следующим образом:

```kotlin
val user = User("Alex", 25)

println(user.name)
println(user.age)
```

Такой синтаксис позволяет одновременно объявить конструктор и свойства класса.

По умолчанию классы **Kotlin** являются `final`, то есть от них нельзя наследоваться. 
Чтобы разрешить наследование, используется ключевое слово `open`:

```kotlin
open class Animal

class Dog : Animal()
```

В **Kotlin** наследование указывается после двоеточия.

## Коллекции

Для хранения нескольких значений **Kotlin** предоставляет коллекции. Наиболее 
часто используются `List`, `Set` и `Map`.

`List` хранит элементы в определённом порядке и допускает повторения:

```kotlin
val fruits = listOf("apple", "banana", "apple")

println(fruits[0])
println(fruits.size)
```

Для изменяемого списка используется `MutableList`:

```kotlin
val fruits = mutableListOf("apple", "banana")

fruits.add("orange")
fruits.remove("apple")
```

`Set` хранит уникальные элементы:

```kotlin
val numbers = setOf(1, 2, 3, 3)

println(numbers) // [1, 2, 3]
```

`Map` хранит пары «ключ — значение»:

```kotlin
val users = mapOf(
    "Alex" to 25,
    "Maria" to 30
)

println(users["Alex"])
```

Коллекции можно перебирать с помощью уже знакомого цикла `for`:

```kotlin
for (fruit in fruits) {
    println(fruit)
}
```
