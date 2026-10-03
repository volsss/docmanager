# Теория Kotlin: Часть 2

В первой части мы познакомились с базовым синтаксисом языка **Kotlin**: 
переменными, типами данных, строками, условиями, циклами и вводом/выводом.

## Функции

Функции позволяют объединять несколько инструкций в отдельную логическую 
единицу.

Функция может получать данные через параметры и возвращать результат.

Функция объявляется с помощью ключевого слова `fun`.

```kotlin
fun sum(a: Int, b: Int): Int {
    return a + b
}
```

| Код              | Описание                   |
|------------------|----------------------------|
| `fun`            | объявление функции         |
| `sum`            | имя функции                |
| `a: Int, b: Int` | параметры                  |
| `Int` после `):` | тип возвращаемого значения |
| `return`         | возвращаем результат       |


Вызвать функцию можно по имени:

```kotlin
val result = sum(2, 3)

println(result) // 5
```

### Параметры функции

Параметры указываются в круглых скобках после имени функции:

```kotlin
fun greet(name: String) {
    println("Hello, $name!")
}
```

Вызов:

```kotlin
greet("Alex")
```

Функция может иметь несколько параметров:

```kotlin
fun printUser(name: String, age: Int) {
    println("Name: $name")
    println("Age: $age")
}
```

Вызов:

```kotlin
printUser("Alex", 25)
```

Тип каждого параметра указывается после его имени: `name: String`, 
`age: Int`

### Возвращаемое значение

Функция может вернуть результат с помощью `return`.

```kotlin
fun multiply(a: Int, b: Int): Int {
return a * b
}
```

Использование:

```kotlin
val result = multiply(4, 5)

println(result) // 20
```

После `return` функция завершает своё выполнение и передаёт указанное 
значение вызывающему коду.

Например:

```kotlin
fun getSign(number: Int): String {
    if (number > 0) {
        return "Положительное"
    }
    return "Не положительное"
}
```

### Unit

Функция, которая ничего не возвращает в практическом смысле, имеет 
возвращаемый тип `Unit`.

Обычно **Kotlin** позволяет не указывать `Unit` явно:

```kotlin
fun sayHello(name: String) {
    println("Hello, $name!")
}
```

То же самое с явным указанием типа:

```kotlin
fun sayHello(name: String): Unit {
    println("Hello, $name!")
}
```

`Unit` является полноценным типом **Kotlin** и имеет единственное значение 
— `Unit`.

В **Java**, **C++**, **C#** и других языках похожую роль выполняет `void`.

Для обычных функций, которые только выполняют действие, чаще всего 
пишут вариант без `: Unit`.

### Функция с одним выражением

Если тело функции состоит из одного выражения, можно использовать 
сокращённый синтаксис:

```kotlin
fun sum(a: Int, b: Int) = a + b
```

Вместо:

```kotlin
fun sum(a: Int, b: Int): Int {
    return a + b
}
```

**Kotlin** выводит тип возвращаемого значения из выражения.

При необходимости тип можно указать явно:

```kotlin
fun sum(a: Int, b: Int): Int = a + b
```

Такой синтаксис особенно удобен для небольших функций.

Например:

```kotlin
fun square(number: Int) = number * number
```

<!-- prettier-ignore -->
> [!CAUTION]
> В функции с телом-выражением `return` не используется
> 
> ```kotlin
> fun sum(a: Int, b: Int) = a + b
> ```
> 
> Нельзя писать:
> 
> ```kotlin
> // fun sum(a: Int, b: Int) = return a + b
> ```
> 
> `return` используется в функциях с блочным телом:
> 
> ```kotlin
> fun sum(a: Int, b: Int) {
>     return a + b
> }
> ```

### Параметры по умолчанию

Для параметров можно указать значение по умолчанию.

```kotlin
fun greet(
    name: String, 
    greeting: String = "Hello"
) {
    println("$greeting, $name!")
}
```

Теперь функцию можно вызвать с двумя аргументами:

```kotlin
greet("Alex", "Hi")
```

или только с одним:

```kotlin
greet("Alex")
```

Во втором случае будет использовано значение `"Hello"`.

Параметров со значениями по умолчанию может быть несколько:

```kotlin
fun createUser(
    name: String,
    age: Int = 18,
    city: String = "Unknown"
) {
    println("$name, $age, $city")
}
```

### Именованные аргументы

Аргументы функции можно передавать по имени:

```kotlin
fun createUser(
    name: String, 
    age: Int
) {
    println("$name: $age")
}

createUser(name = "Alex", age = 25)
```

Именованные аргументы особенно полезны, когда у функции много 
параметров или несколько параметров имеют одинаковый тип.

Можно указать аргументы в другом порядке:

```kotlin
createUser(
    age = 25,
    name = "Alex"
)
```

Именованные аргументы также удобно комбинировать со значениями по умолчанию:

```kotlin
fun greet(
    name: String,
    greeting: String = "Hello",
    punctuation: String = "!"
) {
    println("$greeting, $name$punctuation")
}

greet(
    name = "Alex",
    punctuation = "."
)
```

### Область видимости переменных

Переменная, объявленная внутри функции, доступна только внутри 
этой функции:

```kotlin
fun test() {
    val message = "Hello"

    println(message)
}
```

Нельзя обратиться к message из другой функции:

```kotlin
fun test() {
    val message = "Hello"
}

fun main() {
// println(message) // ошибка
}
```

Такие переменные называются локальными.

## Null safety

Одна из важных особенностей **Kotlin** — система типов, позволяющая 
явно различать значения, которые могут содержать `null`, 
и значения, которые не могут его содержать.

### Что такое null

`null` означает отсутствие значения.

Например:

```kotlin
val name: String? = null
```

Здесь переменная `name` в данный момент не содержит строку.

### Nullable и non-nullable типы

Обычный тип `String` не может содержать `null`:

```kotlin
val name: String = "Kotlin"
```

Следующий код недопустим:

```kotlin
// val name: String = null
```

Чтобы разрешить `null`, после типа ставится `?`:

```kotlin
val name: String? = null
```

Теперь переменная может содержать:

```kotlin
val name1: String? = "Kotlin"
val name2: String? = null
```

Важно понимать: `String` и `String?` — это разные типы.

### Почему нельзя просто обратиться к nullable-значению

Рассмотрим:

```kotlin
val name: String? = null
```

У строки есть свойство `length`:

```kotlin
val name: String = "Kotlin"

println(name.length)
```

Но для `String?` напрямую обратиться к `length` нельзя:

```kotlin
val name: String? = null

// println(name.length)
```

Компилятор не позволяет это сделать, потому что `name` может оказаться 
равным `null`.

Это одна из основных идей `null safety` — обработка возможности 
отсутствия значения до того, как программа попытается использовать 
это значение как обычный объект.

### Безопасный вызов

Для безопасного доступа к nullable-значению используется оператор `?.`:

```kotlin
val name: String? = null

println(name?.length)
```

Если `name` не равен `null`, будет выполнен доступ к `length`.

Если `name == null`, результатом выражения будет `null`.

Например:

```kotlin
val name: String? = "Kotlin"

println(name?.length) // 6
```

и:

```kotlin
val name: String? = null

println(name?.length) // null
```

Тип результата в таком случае тоже nullable:

```kotlin
val length: Int? = name?.length
```

Оператор ?. можно использовать не только со свойствами:

```kotlin
name?.uppercase()
```

### Оператор Elvis

Иногда вместо `null` необходимо получить запасное значение.

Для этого используется оператор Elvis:

```kotlin
val name: String? = null

val length = name?.length ?: 0

println(length) // 0
```

Логика:

````mermaid
graph TD;
    A[name?.length]-->B[!= null];
    A[name?.length]-->C[== null];

    B-->D[использовать name.length];
    C-->E[использовать 0];
````

Ещё пример:

```kotlin
val name: String? = null

val displayName = name ?: "Гость"

println(displayName)
```

Если `name == null`, в `displayName` будет `"Гость"`.

### Явная проверка на `null`

Можно выполнить обычную проверку:

```kotlin
val name: String? = "Kotlin"

if (name != null) {
    println(name.length)
}
```

После проверки **Kotlin** может использовать значение как 
non-nullable в соответствующей области кода.

Например:

```kotlin
val name: String? = "Kotlin"

if (name != null) {
    val length: Int = name.length

    println(length)
}
```

Это называется **smart cast**.

**Kotlin** анализирует проверку и понимает, что внутри этой ветки 
значение не равно `null`, если компилятор может гарантировать, что 
значение не изменилось между проверкой и использованием.

### Оператор !!

Оператор `!!` принудительно рассматривает nullable-значение как не `null`:

```kotlin
val name: String? = "Kotlin"

println(name!!.length)
```

Если в момент выполнения `name` окажется равен `null`, будет выброшен 
`NullPointerException`.

Поэтому `!!` следует использовать осторожно. Во многих случаях безопаснее 
применить `?.`, проверку на `null` или оператор `?:`.

### null при вводе данных

`null safety` особенно полезна при обработке ввода.

Например:

```kotlin
val age = readln().toIntOrNull()
```

Тип `age`:

```kotlin
Int?
```

То есть преобразование либо вернёт число, либо `null`.

Можно проверить результат:

```kotlin
val age = readln().toIntOrNull()

if (age != null) {
    println("Возраст: $age")
} else {
    println("Некорректный ввод")
}
```

Или использовать Elvis:

```kotlin
val age = readln().toIntOrNull() ?: 0
```

Здесь при неправильном вводе будет использовано значение `0`.

## Лямбда-выражения

В **Kotlin** функции являются обычными значениями.

Это означает, что функцию можно:

* сохранить в переменную;
* передать в другую функцию;
* вернуть из функции.

Для небольших функций удобно использовать лямбда-выражения.

Простейшая лямбда:

```kotlin
val sum = { a: Int, b: Int -> a + b }
```

Теперь `sum` содержит функцию, которую можно вызвать:

```kotlin
println(sum(2, 3)) // 5
```

Общая форма лямбды:

```
{ параметры -> тело }
```

Например:

```kotlin
val square = { number: Int -> number * number }

println(square(5)) // 25
```

### Лямбда без параметров

Если параметров нет, стрелку можно не использовать:

```kotlin
val hello = {
    println("Hello!")
}

hello()
```

### Лямбда с одним параметром

Если лямбда принимает один параметр, **Kotlin** позволяет
обращаться к нему через специальное имя `it`:

```kotlin
val square: (Int) -> Int = {
    it * it
}
```

Вызов:

```kotlin
println(square(5)) // 25
```

Здесь:

```kotlin
(Int) -> Int
```

означает, что функция принимает `Int` и возвращает `Int`

## Тип функции

Функции тоже имеют тип.

Например:

```kotlin
(Int) -> String
```

означает:

> функция принимает `Int` и возвращает `String`.

Можно явно указать такой тип:

```kotlin
val describe: (Int) -> String = {
    "Число: $it"
}
```

Использование:

```kotlin
println(describe(10)) // Число: 10
```

Другой пример:

```kotlin
val isEven: (Int) -> Boolean = {
    it % 2 == 0
}
```

## Функции высшего порядка

Функция называется функцией высшего порядка, если она принимает
другую функцию в качестве параметра или возвращает функцию.

Например:

```kotlin
fun calculate(
    a: Int,
    b: Int,
    operation: (Int, Int) -> Int
): Int {
    return operation(a, b)
}
```

Теперь можно передавать разные операции:

```kotlin
val sum = calculate(2, 3, { a, b ->
    a + b
})

// или за скобкой (trailing lambda):

val multiply = calculate(2, 3) { a, b ->
    a * b
}

println(sum)      // 5
println(multiply) // 6
```

Одна и та же функция `calculate()` умеет выполнять разные операции
в зависимости от переданной функции.

## Trailing lambda

Если последним параметром функции является другая функция,
лямбду можно вынести за круглые скобки.

Например:

```kotlin
fun calculate(
    a: Int,
    b: Int,
    operation: (Int, Int) -> Int
): Int {
    return operation(a, b)
}
```

Вызов:

```kotlin
val result = calculate(2, 3) { a, b ->
    a + b
}
// или
val result = calculate(6, 1) { 
    a, b -> a + b
}
// или
val result = calculate(6, 1) { a, b -> a + b }
```

Такой синтаксис называется `trailing lambda`.

Он очень часто встречается в **Kotlin**.