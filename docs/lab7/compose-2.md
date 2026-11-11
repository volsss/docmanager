# Глава 4. Основные UI-компоненты Jetpack Compose

## 4.1. UI-компоненты и Material Design 3

В **Jetpack Compose** интерфейс строится из composable-функций. В предыдущей главе мы рассмотрели контейнеры и механизмы
компоновки. Теперь изучим компоненты, из которых непосредственно состоит пользовательский интерфейс.

Для большинства стандартных элементов рекомендуется использовать **Material Design 3**.

**Material 3** — система проектирования интерфейсов, определяющая внешний вид компонентов, цветовые схемы, типографику,
формы, состояния взаимодействия и другие визуальные характеристики.

В Compose она представлена библиотекой **Material 3** и набором готовых composable-компонентов.

Основные категории компонентов:

| Категория              | Компоненты                                     |
|------------------------|------------------------------------------------|
| Отображение информации | `Text`, `Icon`, `Image`, `Card`                |
| Взаимодействие         | `Button`, `IconButton`, `FloatingActionButton` |
| Ввод информации        | `TextField`, `OutlinedTextField`               |
| Выбор значений         | `Checkbox`, `Switch`, `RadioButton`, `Slider`  |
| Обратная связь         | `ProgressIndicator`, `Snackbar`, `AlertDialog` |

Полный каталог доступен в официальной документации Android Developers.

### Почему стоит использовать готовые компоненты

Разработчик может самостоятельно создать кнопку с помощью `Box`, `Text`, `background()` и `clickable()`.

Однако такой компонент потребует самостоятельной реализации:

- визуальных состояний;
- обработки доступности;
- обратной связи при нажатии;
- правильной интерактивной области;
- поведения при отключении;
- особенностей взаимодействия с клавиатурой и другими средствами управления.

**Material 3** уже предоставляет готовые реализации многих стандартных компонентов.

Поэтому собственные компоненты следует создавать, когда стандартных возможностей действительно недостаточно.

## 4.2. Text — отображение текста

`Text` является одним из наиболее часто используемых компонентов **Compose**.

Он предназначен для отображения текстовой информации.

Простейший пример:

```kotlin
@Composable
fun Greeting() {
    Text(
        text = "Привет, Compose!"
    )
}
```

У `Text` большое количество параметров, позволяющих управлять отображением.

### 4.2.1. Основные параметры Text

Рассмотрим наиболее важные параметры.

#### text

Текст, который необходимо отобразить.

```kotlin
Text(
    text = "Название приложения"
)
```

#### color

Цвет текста.

```kotlin
Text(
    text = "Важное сообщение",
    color = Color.Red
)
```

#### fontSize

Размер шрифта.

```kotlin
Text(
    text = "Заголовок",
    fontSize = 24.sp
)
```

#### fontWeight

Толщина начертания.

```kotlin
Text(
    text = "Жирный текст",
    fontWeight = FontWeight.Bold
)
```

#### fontStyle

Стиль начертания, например обычный или курсивный.

```kotlin
Text(
    text = "Курсивный текст",
    fontStyle = FontStyle.Italic
)
```

#### textAlign

Выравнивание текста внутри доступной области.

```kotlin
Text(
    text = "Текст по центру",
    modifier = Modifier.fillMaxWidth(),
    textAlign = TextAlign.Center
)
```

#### maxLines

Максимальное количество отображаемых строк.

```kotlin
Text(
    text = "Очень длинное описание товара...",
    maxLines = 2
)
```

#### overflow

Определяет поведение при переполнении текстом доступной области.

```kotlin
Text(
    text = "Очень длинное описание товара...",
    maxLines = 2,
    overflow = TextOverflow.Ellipsis
)
```

В результате лишний текст будет заменён многоточием.

### 4.2.2. Использование Typography

В реальных приложениях не рекомендуется вручную задавать размер, начертание и цвет для каждого текстового компонента.

Для этого используется `Typography` из **Material 3**.

Пример:

```kotlin
@Composable
fun ProfileHeader() {
    Column {
        Text(
            text = "Профиль",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Личная информация",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Редактировать",
            style = MaterialTheme.typography.labelLarge
        )
    }

}
```

Основные категории типографики:

| Категория | Назначение                        |
|-----------|-----------------------------------|
| display   | Крупные заголовки                 |
| headline  | Заголовки экранов и секций        |
| title     | Названия блоков и элементов       |
| body      | Основной текст                    |
| label     | Подписи и вспомогательные надписи |

Каждая категория содержит варианты размеров, например `bodyLarge`, `bodyMedium`, `bodySmall`.

Использование типографики из `MaterialTheme` позволяет централизованно управлять стилем приложения.

Настройку собственной типографики и темы рассмотрим в отдельной главе.

### 4.2.3. AnnotatedString

Иногда необходимо отображать текст, в котором отдельные фрагменты имеют различные стили.

Например, часть предложения должна быть выделена жирным шрифтом.

Для этого используется `AnnotatedString`.

```kotlin
@Composable
fun StyledText() {
    Text(
        buildAnnotatedString {
            append("Стоимость: ")

            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            ) {
                append("99 €")
            }
        }
    )

}
```

В этом примере:

- первая часть отображается обычным стилем;
- цена выделяется жирным шрифтом и красным цветом.

`SpanStyle` определяет оформление отдельных фрагментов текста.

Для управления стилем целых абзацев используется `ParagraphStyle`.

Когда применять `AnnotatedString`

Этот механизм полезен для:

- форматированных сообщений;
- выделения поисковых совпадений;
- отображения ссылок;
- отображения текста с различными стилями;
- создания информационных сообщений.

Не следует использовать несколько `Text` внутри `Row` только для того, чтобы получить несколько разных стилей в одном
предложении. В таких случаях `AnnotatedString` обычно удобнее.

## 4.3. Button — кнопки

Кнопки позволяют пользователю инициировать действия.

В **Compose** для этого предусмотрены различные компоненты **Material 3**.

Основные типы:

| Компонент           | Назначение                           |
|---------------------|--------------------------------------|
| `Button`            | Главное действие                     |
| `FilledTonalButton` | Значимое дополнительное действие     |
| `OutlinedButton`    | Альтернативное действие              |
| `ElevatedButton`    | Действие с визуальным возвышением    |
| `TextButton`        | Малозаметное дополнительное действие |

### 4.3.1. Button

```kotlin
@Composable
fun SaveButton(
    onSave: () -> Unit
) {
    Button(
        onClick = onSave
    ) {
        Text("Сохранить")
    }
}
```

Основные параметры:

- `onClick` — обработчик нажатия;
- `modifier` — модификатор компонента;
- `enabled` — определяет, доступна ли кнопка;
- `colors` — цветовая схема;
- `shape` — форма;
- `contentPadding` — внутренние отступы.

### 4.3.2. Состояние доступности

Кнопка может находиться в активном или неактивном состоянии.

```kotlin
@Composable
fun SubmitButton(
    isValid: Boolean,
    onSubmit: () -> Unit
) {
    Button(
        onClick = onSubmit,
        enabled = isValid
    ) {
        Text("Отправить")
    }
}
```

Если `isValid` равен `false`, кнопка становится недоступной для взаимодействия.

> [!WARNING]
> Состояние доступности передаётся через параметр. Компонент не должен самостоятельно решать, корректны ли введённые
пользователем данные.
>
> Это решение принимает `ViewModel` или другой владелец состояния.

### 4.3.3. FilledTonalButton

Используется для значимых действий, которые не должны визуально конкурировать с основным действием.

```kotlin
@Composable
fun SecondaryAction(
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick
    ) {
        Text("Подробнее")
    }
}
```

### 4.3.4. OutlinedButton

Кнопка с контуром.

```kotlin
OutlinedButton(
    onClick = {}
) {
    Text("Отмена")
}
```

### 4.3.5. TextButton

Кнопка без выраженного фона и контура.

```kotlin
TextButton(
    onClick = {}
) {
    Text("Пропустить")
}
```

### 4.3.6. Кнопка с иконкой

Внутри кнопки можно размещать другие composable-компоненты.

```kotlin
Button(
    onClick = {}
) {
    Icon(
        imageVector = Icons.Default.Save,
        contentDescription = null
    )

    Spacer(
        modifier = Modifier.width(8.dp)
    )

    Text("Сохранить")
}
```

`contentDescription = null` здесь допустим, поскольку иконка дополняет текст кнопки, который уже передаёт смысл
действия.

#### Практическое правило

Не стоит использовать одинаковый визуальный акцент для всех действий на экране.

Например, на форме регистрации основное действие "Зарегистрироваться" можно оформить через `Button`, а действие "Уже
есть аккаунт?" — через `TextButton`.

Так пользователю проще определить главное действие.

## 4.4. Icon и IconButton — иконки

Иконки используются для визуального обозначения действий, состояний и категорий информации.

В **Compose** существует компонент `Icon`.

```kotlin
@Composable
fun FavoriteIcon() {
    Icon(
        imageVector = Icons.Default.Favorite,
        contentDescription = "Избранное"
    )
}
```

Параметр `imageVector` принимает векторное изображение.

Параметр `contentDescription` передаёт текстовое описание иконки, которое может использоваться средствами доступности.

### 4.4.1. Декоративные и функциональные иконки

Следует различать два случая.

Декоративная иконка не несёт самостоятельного смысла.

Например, иконка рядом с текстом "Настройки".

В этом случае допустимо:

```kotlin
Icon(
    imageVector = Icons.Default.Settings,
    contentDescription = null
)
```

Функциональная иконка представляет самостоятельное действие.

Например, кнопка удаления.

Её необходимо снабдить понятным описанием.

### 4.4.2. IconButton

`IconButton` представляет собой интерактивную кнопку, содержимым которой обычно является иконка.

```kotlin
@Composable
fun FavoriteButton(
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = "Добавить в избранное"
        )
    }
}
```

`IconButton` предпочтительнее комбинации `Icon` и произвольного `clickable`, поскольку это готовый компонент с
предусмотренным поведением и семантикой кнопки.

### 4.4.3. FloatingActionButton

`FloatingActionButton` используется для основного действия на экране.

Например, добавление новой записи.

```kotlin
@Composable
fun AddButton(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Добавить"
        )
    }
}
```

Обычно такой компонент размещается в составе общей структуры экрана.

#### Откуда брать иконки

В **Compose** можно использовать:

- Material Icons;
- собственные ImageVector;
- векторные ресурсы;
- изображения из ресурсов проекта.

Для использования набора расширенных **Material Icons** может понадобиться соответствующая зависимость
`material-icons-extended`.

> В **Compose Multiplatform** доступность конкретного набора ресурсов необходимо проверять для используемых платформ и
> версий библиотек.

## 4.5. Image — изображения

`Image` предназначен для отображения изображений внутри интерфейса.

В отличие от `Text`, компоненту необходимо передать объект `Painter`, а не просто строку с адресом изображения.

Простейший пример:

```kotlin
@Composable
fun ProfileAvatar(
    painter: Painter
) {
    Image(
        painter = painter,
        contentDescription = "Фото пользователя"
    )
}
```

Передача `Painter` через параметр позволяет отделить сам UI-компонент от механизма получения изображения.

Это полезно для повторного использования компонента.

### 4.5.1. Размер изображения

Размер задаётся через `Modifier`.

```kotlin
Image(
    painter = painter,
    contentDescription = "Фото пользователя",
    modifier = Modifier.size(120.dp)
)
```

### 4.5.2. ContentScale

contentScale определяет, как изображение масштабируется относительно доступной области.

Основные варианты:

| Значение   | Поведение                                                    |
|------------|--------------------------------------------------------------|
| Fit        | Вписывает изображение с сохранением пропорций                |
| Crop       | Заполняет область, обрезая лишнее                            |
| FillBounds | Заполняет всю область, потенциально искажая пропорции        |
| Inside     | Вписывает изображение без увеличения сверх исходного размера |
| None       | Не масштабирует изображение                                  |

Например:

```kotlin
Image(
    painter = painter,
    contentDescription = "Фото товара",
    contentScale = ContentScale.Crop,
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
)
```

Так изображение заполняет заданную область, сохраняя пропорции и обрезая части, которые не помещаются.

### 4.5.3. Округление изображения

```kotlin
Image(
    painter = painter,
    contentDescription = "Аватар пользователя",
    contentScale = ContentScale.Crop,
    modifier = Modifier
        .size(100.dp)
        .clip(CircleShape)
)
```

### 4.5.4. Изображения в Compose Multiplatform

Для общего кода не следует обращаться напрямую к **Android**-ресурсам через `R.drawable.`

**Compose Multiplatform** предоставляет собственную систему ресурсов.

Изображения размещаются в каталоге:

```mermaid
treeView-beta
commonMain/
composeResources/
drawable/
avatar.png
logo.png
product.png
```

После генерации ресурсных accessor-классов изображение можно использовать следующим образом:

```kotlin
@Composable
fun ProductImage() {
    Image(
        painter = painterResource(
            Res.drawable.product
        ),
        contentDescription = "Товар",
        modifier = Modifier.size(160.dp)
    )
}
```

Здесь `painterResource` импортируется из:

```kotlin
import org.jetbrains.compose.resources.painterResource
```

Пакет сгенерированного класса `Res` зависит от конфигурации модуля.

Для удалённых изображений потребуется отдельная библиотека загрузки изображений или собственная реализация получения и
декодирования данных. Сам `Image` не выполняет HTTP-запрос по URL.

## 4.6. Card — карточки

`Card` — контейнер **Material 3** для представления законченного блока информации.

Например:

- карточка товара;
- карточка пользователя;
- карточка новости;
- карточка сообщения;
- карточка статистики.

Простейший пример:

```kotlin
@Composable
fun UserCard() {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Александр")

            Text("Android-разработчик")
        }
    }
}
```

Основная задача карточки — визуально объединить связанные элементы.

### 4.6.1. Типы карточек

**Material 3** предоставляет несколько вариантов.

#### Card

Базовая карточка.

#### ElevatedCard

Карточка с визуальным возвышением относительно фона.

#### OutlinedCard

Карточка с контуром.

Для выбора конкретного варианта следует ориентироваться на визуальную иерархию и дизайн приложения.

### 4.6.2. Card и Surface

Оба компонента относятся к визуальным строительным блокам **Material 3**, но имеют разные акценты.

Card предназначен для представления отдельного содержательного блока.

`Surface` является более общим контейнером, который позволяет управлять фоном, формой, границами, цветом и другими
визуальными свойствами поверхности.

Не следует использовать `Card` для каждого контейнера на экране. Обычные `Column`, `Row` и `Box` часто являются более
подходящими решениями.

### 4.6.3. Пример карточки товара

```kotlin
data class ProductUiModel(
    val name: String,
    val description: String,
    val price: String,
    val isAvailable: Boolean
)

@Composable
fun ProductCard(
    product: ProductUiModel,
    onBuyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = product.price,
                style = MaterialTheme.typography.titleMedium
            )

            Button(
                onClick = onBuyClick,
                enabled = product.isAvailable
            ) {
                Text("Купить")
            }
        }
    }
}
```

Карточка получает модель отображения и обработчик действия.

Она не обращается к `ViewModel` и не принимает решений о покупке. Эти задачи относятся к другим слоям приложения.

## 4.7. TextField — поля ввода

`TextField` — компонент **Material 3** для ввода текста пользователем.

В отличие от `Text`, который только отображает информацию, `TextField` позволяет редактировать содержимое.

Простейший пример:

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = { Text("Имя") }
)
```

Компонент работает по принципу unidirectional data flow: значение хранится в состоянии, а изменения передаются через
обратный вызов.

### 4.7.1. Основные параметры TextField

| Параметр          | Назначение                                        |
|-------------------|---------------------------------------------------|
| `value`           | Текущее значение текста                           |
| `onValueChange`   | Обработчик изменения текста                       |
| `label`           | Подсказка, отображаемая внутри поля               |
| `placeholder`     | Текст-заполнитель, отображаемый при пустом поле   |
| `leadingIcon`     | Иконка слева от поля                              |
| `trailingIcon`    | Иконка справа от поля                             |
| `modifier`        | Модификатор компонента                            |
| `enabled`         | Доступность поля для взаимодействия               |
| `readOnly`        | Режим только для чтения                           |
| `singleLine`      | Ограничение ввода одной строкой                   |
| `maxLines`        | Максимальное количество строк                     |
| `keyboardOptions` | Настройки клавиатуры (тип, автокапитализация)     |
| `keyboardActions` | Обработчики действий клавиатуры (например, Enter) |
| `colors`          | Цветовая схема компонента                         |
| `shape`           | Форма поля                                        |

### 4.7.2. TextField и OutlinedTextField

**Material 3** предоставляет два основных варианта.

#### TextField

Поле с фоном и плавающим лейблом.

```kotlin
TextField(
    value = text,
    onValueChange = { text = it },
    label = { Text("Email") }
)
```

#### OutlinedTextField

Поле с контуром и плавающим лейблом.

```kotlin
OutlinedTextField(
    value = text,
    onValueChange = { text = it },
    label = { Text("Email") }
)
```

Выбор между ними зависит от визуальной иерархии экрана. `OutlinedTextField` лучше подходит для форм, где важно
чётко выделить границы каждого поля.

### 4.7.3. Управление состоянием

Поле ввода должно реагировать на состояние валидации.

```kotlin
@Composable
fun EmailField(
    email: String,
    onEmailChange: (String) -> Unit,
    isError: Boolean
) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Email") },
        isError = isError,
        supportingText = {
            if (isError) {
                Text("Введите корректный email")
            }
        }
    )
}
```

Параметр `isError` визуально выделяет поле, а `supportingText` позволяет отобразить поясняющее сообщение.

> [!WARNING]
> Логика валидации не должна находиться внутри компонента поля ввода.
>
> Компонент получает состояние извне и отображает его. Решение о корректности данных принимает `ViewModel` или другой
> владелец состояния.

### 4.7.4. Иконки и подсказки

Поле ввода может содержать дополнительные элементы.

```kotlin
@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Поиск...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Очистить"
                    )
                }
            }
        }
    )
}
```

`leadingIcon` размещает иконку слева, `trailingIcon` — справа.

`placeholder` отображается, когда поле пусто, и исчезает при вводе текста.

### 4.7.5. Настройки клавиатуры

Для разных типов данных удобно задавать соответствующую клавиатуру.

```kotlin
OutlinedTextField(
    value = phone,
    onValueChange = { phone = it },
    label = { Text("Телефон") },
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Phone,
        autoCapitalize = AutoCapitalize.NONE
    )
)
```

Основные типы клавиатуры:

| Тип клавиатуры          | Назначение           |
|-------------------------|----------------------|
| `KeyboardType.Text`     | Общий текстовый ввод |
| `KeyboardType.Email`    | Ввод email           |
| `KeyboardType.Phone`    | Ввод номера телефона |
| `KeyboardType.Uri`      | Ввод URL             |
| `KeyboardType.Number`   | Числовой ввод        |
| `KeyboardType.Password` | Ввод пароля          |

### 4.7.6. Обработка действий клавиатуры

Для улучшения UX можно обрабатывать нажатие клавиши Enter.

```kotlin
OutlinedTextField(
    value = text,
    onValueChange = { text = it },
    label = { Text("Комментарий") },
    keyboardActions = KeyboardActions(
        onDone = {
            // скрыть клавиатуру или выполнить действие
        }
    )
)
```

Параметр `onDone` срабатывает при нажатии клавиши «Готово» на клавиатуре.

### 4.7.7. Пример формы входа

```kotlin
data class LoginState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null
)

@Composable
fun LoginForm(
    state: LoginState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            isError = state.emailError != null,
            supportingText = {
                state.emailError?.let { Text(it) }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = { Text("Пароль") },
            isError = state.passwordError != null,
            supportingText = {
                state.passwordError?.let { Text(it) }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Войти")
        }
    }
}
```

Форма получает состояние и обработчики извне.

Компонент не валидирует данные и не выполняет запросы — он только отображает текущее состояние и передаёт изменения
вверх.

## 4.8. Checkbox — флажки

`Checkbox` позволяет выбирать несколько независимых вариантов.

Например:

- выбор нескольких категорий;
- согласие с несколькими условиями;
- выбор фильтров;
- настройка нескольких параметров.

Простой пример:

```kotlin
@Composable
fun AgreementCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange
    )
}
```

Основные параметры:

- `checked` — текущее состояние;
- `onCheckedChange` — обработчик изменения;
- `enabled` — доступность взаимодействия;
- `colors` — настройка цветов.

### Checkbox с текстовой подписью

```kotlin
@Composable
fun AgreementRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.toggleable(
            value = checked,
            role = Role.Checkbox,
            onValueChange = onCheckedChange
        )
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null
        )

        Text("Я принимаю условия использования")
    }
}
```

В этом примере вся строка является интерактивной.

Сам `Checkbox` не имеет собственного обработчика, поскольку взаимодействие обрабатывает родительский `Row`.

Это также помогает предоставить корректную семантику для средств доступности.

### TriStateCheckbox

Для некоторых сценариев существуют три состояния:

- не выбран;
- выбран частично;
- выбран полностью.

Например, родительский флажок "Выбрать всё" может показывать частичное выделение, когда выбрана только часть дочерних
элементов.

Для этого предназначен `TriStateCheckbox`.

## 4.9. Switch — переключатель

`Switch` используется для изменения состояния бинарной настройки.

Например:

- включение уведомлений;
- включение тёмной темы;
- разрешение автоматического обновления;
- активация определённой функции.

Пример:

```kotlin
@Composable
fun NotificationsSwitch(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit
) {
    Switch(
        checked = enabled,
        onCheckedChange = onEnabledChange
    )
}
```

В отличие от `Checkbox`, `Switch` обычно отражает настройку, которая может изменяться непосредственно и сразу влиять на
поведение приложения.

### Switch с подписью

```kotlin
@Composable
fun NotificationsSetting(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = enabled,
                role = Role.Switch,
                onValueChange = onEnabledChange
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Уведомления")

        Switch(
            checked = enabled,
            onCheckedChange = null
        )
    }
}
```

### Checkbox или Switch?

Выбор зависит от смысла взаимодействия.

`Checkbox` используется преимущественно для выбора одного или нескольких вариантов.

`Switch` используется преимущественно для немедленного включения или отключения функции.

Нельзя выбирать компонент только потому, что он визуально больше нравится.

## 4.10. RadioButton — выбор одного варианта

`RadioButton` используется тогда, когда пользователь должен выбрать только один вариант из нескольких.

Например:

- способ доставки;
- способ оплаты;
- режим отображения;
- категория пользователя.

Модель вариантов:

```kotlin
enum class ThemeOption {
    LIGHT,
    DARK,
    SYSTEM
}
```

Компонент:

```kotlin
@Composable
fun ThemeOptionItem(
    title: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onSelect,
                role = Role.RadioButton
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )

        Text(title)
    }
}
```

Здесь `selected` определяет выбранный вариант.

Вызов `onSelect` передаёт событие владельцу состояния.

Для группы вариантов `ViewModel` должна хранить одно выбранное значение, а не отдельные независимые Boolean-переменные
для каждой кнопки.

Например:

```kotlin
data class AppearanceUiState(
    val selectedTheme: ThemeOption =
        ThemeOption.SYSTEM
)
```

При выборе нового значения `ViewModel` публикует обновлённое состояние.

## 4.11. Slider — выбор значения из диапазона

`Slider` позволяет пользователю выбрать числовое значение в определённом диапазоне.

Применяется для:

- громкости;
- яркости;
- размера шрифта;
- рейтинга;
- ценового фильтра;
- других количественных параметров.

Простейший пример:

```kotlin
@Composable
fun VolumeSlider(
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = 0f..100f
    )
}
```

Основные параметры:

- `value` — текущее значение;
- `onValueChange` — изменение значения;
- `valueRange` — диапазон;
- `steps` — количество промежуточных делений;
- `enabled` — доступность управления.

### Slider с фиксированными шагами

```kotlin
Slider(
    value = rating,
    onValueChange = onRatingChange,
    valueRange = 0f..10f,
    steps = 9
)
```

В данном случае значение может выбираться с шагом в `1` единицу.

Например: `0`, `1`, `2`, `3` и так далее до `10`.

### Как передавать значение в ViewModel

```kotlin
fun onVolumeChanged(value: Float) {
    _uiState.update {
        it.copy(volume = value)
    }
}
```

Для некоторых сценариев частое обновление состояния может быть нежелательным. Например, когда изменение ползунка должно
приводить к дорогой операции.

В таком случае можно разделить:

- текущее значение при перемещении;
- окончательно подтверждённое значение.

Для этого у `Slider` предусмотрен `callback` `onValueChangeFinished`.

## 4.12. ProgressIndicator — индикаторы загрузки

Приложение должно информировать пользователя о длительных операциях.

Например:

- получение данных с сервера;
- отправка формы;
- загрузка изображения;
- обработка файла;
- выполнение фоновой операции.

Для этого используются индикаторы прогресса.

Основные типы:

- `CircularProgressIndicator`;
- `LinearProgressIndicator`.

### 4.12.1. Неопределённый прогресс

Используется, когда приложение знает, что операция выполняется, но не знает точного процента завершения.

```kotlin
@Composable
fun LoadingIndicator() {
    CircularProgressIndicator()
}
```

Например, это удобно во время ожидания ответа сервера.

### 4.12.2. Определённый прогресс

Если известен процент завершения, можно передавать значение от 0 до 1.

Современная форма API **Material 3**:

```kotlin
@Composable
fun UploadProgress(
    progress: Float
) {
    LinearProgressIndicator(
        progress = { progress.coerceIn(0f, 1f) },
        modifier = Modifier.fillMaxWidth()
    )
}
```

Например:

- `0f` — операция не началась;
- `0.5f` — выполнена наполовину;
- `1f` — операция завершена.

### Индикатор в архитектуре MVVM

`ViewModel` может публиковать состояние:

```kotlin
data class UploadUiState(
    val isUploading: Boolean = false,
    val progress: Float = 0f
)
```

Экран отображает индикатор в соответствии с этим состоянием:

```kotlin
@Composable
fun UploadContent(
    state: UploadUiState
) {
    if (state.isUploading) {
        LinearProgressIndicator(
            progress = {
                state.progress.coerceIn(0f, 1f)
            }
        )
    }
}
```

Composable-функция не должна самостоятельно определять, завершилась ли загрузка. Источником истины является состояние
приложения.

## 4.13. Связь UI-компонентов с MVVM

Теперь объединим изученные компоненты с архитектурой, которую мы разобрали в первой главе.

> [!IMPORTANT] **Главное правило:**
>
> UI-компоненты получают данные и передают события. `ViewModel` отвечает за изменение состояния.

Рассмотрим пример экрана настроек.

### Шаг 1. Модель состояния

```kotlin
data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val marketingEnabled: Boolean = false,
    val selectedTheme: ThemeOption =
        ThemeOption.SYSTEM
)
```

### Шаг 2. ViewModel

```kotlin
class SettingsViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow(SettingsUiState())

    val uiState =
        _uiState.asStateFlow()

    fun onNotificationsChanged(
        enabled: Boolean
    ) {
        _uiState.update {
            it.copy(
                notificationsEnabled = enabled
            )
        }
    }

    fun onMarketingChanged(
        enabled: Boolean
    ) {
        _uiState.update {
            it.copy(
                marketingEnabled = enabled
            )
        }
    }

    fun onThemeChanged(
        theme: ThemeOption
    ) {
        _uiState.update {
            it.copy(
                selectedTheme = theme
            )
        }
    }
}
```

`ViewModel` хранит актуальное состояние и предоставляет обработчики событий.

### Шаг 3. Composable-компонент экрана

```kotlin
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onNotificationsChanged: (Boolean) -> Unit,
    onMarketingChanged: (Boolean) -> Unit,
    onThemeChanged: (ThemeOption) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {
        NotificationsSetting(
            enabled = state.notificationsEnabled,
            onEnabledChange = onNotificationsChanged
        )

        AgreementRow(
            checked = state.marketingEnabled,
            onCheckedChange = onMarketingChanged
        )

        Text(
            text = "Тема приложения",
            style = MaterialTheme.typography.titleMedium
        )

        ThemeOptionItem(
            title = "Светлая",
            selected =
                state.selectedTheme == ThemeOption.LIGHT,
            onSelect = {
                onThemeChanged(ThemeOption.LIGHT)
            }
        )

        ThemeOptionItem(
            title = "Тёмная",
            selected =
                state.selectedTheme == ThemeOption.DARK,
            onSelect = {
                onThemeChanged(ThemeOption.DARK)
            }
        )

        ThemeOptionItem(
            title = "Системная",
            selected =
                state.selectedTheme == ThemeOption.SYSTEM,
            onSelect = {
                onThemeChanged(ThemeOption.SYSTEM)
            }
        )
    }
}
```

Здесь `SettingsScreen` ничего не знает о `ViewModel`.

Он получает только состояние и обработчики.

### Шаг 4. Связываем экран с ViewModel

На **Android** это можно сделать на уровне `Route`:

```kotlin
@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = viewModel()
) {
    val state by viewModel.uiState
        .collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onNotificationsChanged =
            viewModel::onNotificationsChanged,
        onMarketingChanged =
            viewModel::onMarketingChanged,
        onThemeChanged =
            viewModel::onThemeChanged
    )
}
```

Здесь `SettingsRoute` является интеграционной границей между **Android** UI и представлением.

Сам `SettingsScreen` и его дочерние компоненты не требуют **Android** `ViewModel` и могут использоваться в общем коде
**Compose Multiplatform**.

## 4.14. Доступность UI-компонентов

**Доступность** (Accessibility) — важная часть разработки интерфейсов.

Приложением должны быть удобно пользоваться не только при обычном сенсорном взаимодействии, но и с помощью
вспомогательных технологий.

**Compose** и **Material 3** предоставляют встроенную поддержку семантики компонентов.

**Семантика** — дополнительные сведения, описывающие назначение элемента, его состояние и доступные действия.

Например:

```kotlin
Icon(
    imageVector = Icons.Default.Delete,
    contentDescription = "Удалить"
)
```

Описание позволяет вспомогательным технологиям передать пользователю смысл иконки.

### Основные рекомендации

1. **Не оставляйте интерактивные иконки без описания.**

   Для функциональной иконки необходимо указать назначение действия.

2. **Не добавляйте избыточные описания.**

   Декоративные изображения могут иметь `contentDescription = null`.

3. **Используйте готовые Material-компоненты.**

   Они уже предоставляют встроенную семантику для стандартных ролей взаимодействия.

4. **Не делайте область нажатия слишком маленькой.**

   Для интерактивных элементов необходимо учитывать удобство сенсорного взаимодействия. **Material**-компоненты
   автоматически предусматривают минимальные интерактивные области в соответствующих сценариях.

5. **Обязательно подписывайте поля ввода.**

   Не следует полагаться только на `placeholder`, поскольку он исчезает при вводе текста. Используйте `label` и понятное
   назначение поля.

6. **Отображайте состояние, а не только цвет.**

   Например, нельзя обозначать ошибку исключительно красным цветом. Необходимо также показывать текстовое сообщение.

4.15. Использование компонентов в Compose Multiplatform

Большая часть изученных компонентов не привязана к Android API и может использоваться в общем коде.

Например:

Text;

Button;

Card;

Row;

Column;

Box;

Checkbox;

Switch;

RadioButton;

Slider;

Image.

При переносе приложения важно учитывать несколько аспектов.

1. Использовать общий UI API

Не следует помещать Android-специфичные элементы в компоненты, которые должны работать на нескольких платформах.

2. Отделять получение ресурсов

Вместо прямого обращения к R.drawable в общем коде используйте Compose Multiplatform Resources.

3. Проверять доступность API на всех платформах

Даже если библиотека использует Kotlin и Compose, необходимо проверить, поддерживает ли она нужные платформенные targets.

4. Изолировать платформенные возможности

Работа с системной камерой, разрешениями, файловой системой и другими возможностями устройства должна проходить через соответствующие абстракции.

5. Разделять общий и платформенный слой

Общий UI должен получать подготовленные данные и события, а платформенный слой — предоставлять необходимые системные возможности.

Именно такую структуру будем последовательно развивать в практическом проекте учебника.

## 4.16. Типичные ошибки при работе с UI-компонентами

#### Ошибка 1. Использовать Text для каждого фрагмента форматированного текста.

В некоторых случаях правильнее использовать `AnnotatedString`.

#### Ошибка 2. Создавать собственную кнопку вместо Material Button без необходимости.

Это увеличивает объём кода и ответственность разработчика за доступность и корректное взаимодействие.

#### Ошибка 3. Использовать Icon вместо IconButton для интерактивного элемента.

Иконка сама по себе не является кнопкой.

#### Ошибка 4. Не передавать contentDescription изображениям и иконкам.

Это ухудшает доступность, если элемент имеет самостоятельное информационное значение.

#### Ошибка 5. Выполнять бизнес-логику внутри UI-компонента.

Например, выполнять покупку товара непосредственно в обработчике компонента. Лучше передать событие в `ViewModel`.

#### Ошибка 6. Использовать отдельные Boolean-переменные для взаимоисключающих RadioButton.

Для такой группы лучше хранить одно выбранное значение.

#### Ошибка 7. Смешивать состояние поля ввода и состояние всего экрана.

`TextFieldState` и `UiState` — разные механизмы с разными задачами.

#### Ошибка 8. Использовать placeholder вместо label.

`Placeholder` не заменяет полноценное название поля.

#### Ошибка 9. Использовать цвет как единственный способ передать состояние.

Необходимо предусматривать текстовое или семантическое описание.

#### Ошибка 10. Смешивать получение ресурсов с логикой визуального компонента.

Повторно используемый компонент должен получать необходимые данные через параметры.