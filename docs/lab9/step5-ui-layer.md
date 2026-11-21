# Шаг 5. UI-слой: компоненты и навигация

UI-слой (lab7) — это всё, что пользователь видит и с чем взаимодействует. Мы используем **Compose Multiplatform** — декларативный фреймворк для создания UI, который работает на всех платформах.

UI-слой состоит из:
1. **Компонентов** — переиспользуемые элементы (TextInput, DateInput, ReferenceDropdown).
2. **Навигации** — переходы между экранами (Route, Router).
3. **Экранов** — полные страницы приложения.
4. **Темы** — цветовая схема.
5. **Утилит** — вспомогательные функции.

---

## Шаг 5.1. Главный компонент App

**Файл:** `shared/src/commonMain/kotlin/.../ui/App.kt`

```kotlin
package ru.example.docmanager.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import ru.example.docmanager.ui.router.Router
import ru.example.docmanager.ui.screens.ConnectionScreen

@Composable
fun App() {
    val isConnected = remember { mutableStateOf(false) }
    if (isConnected.value) {
        Router()
    } else {
        ConnectionScreen(
            onConnect = { isConnected.value = true }
        )
    }
}
```

### Как это работает?

1. При запуске показывается `ConnectionScreen` — экран подключения к БД.
2. После успешного подключения `isConnected` становится `true`, и показывается `Router` — основной экран с навигацией.
3. `remember { }` сохраняет состояние между пересборками Compose.

---

## Шаг 5.2. Навигация: Route

**Файл:** `shared/src/commonMain/kotlin/.../ui/router/Route.kt`

```kotlin
package ru.example.docmanager.ui.router

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import docmanager.shared.generated.resources.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import ru.example.docmanager.domain.document.DocumentType
import ru.example.docmanager.domain.reference.ReferenceType

@Serializable
sealed interface Route : NavKey {
    @Contextual
    val stringResource: StringResource
    @Contextual
    val icon: ImageVector

    @Serializable
    data object Start : Route {
        @Contextual override val stringResource = Res.string.route_home
        @Contextual override val icon = Icons.Rounded.Home
    }

    @Serializable
    data object DocumentTypes : Route {
        @Contextual override val stringResource = Res.string.route_document_types
        @Contextual override val icon = Icons.Rounded.Description
    }

    @Serializable
    data object References : Route {
        @Contextual override val stringResource = Res.string.route_references
        @Contextual override val icon = Icons.AutoMirrored.Rounded.ViewList
    }

    @Serializable
    data object Settings : Route {
        @Contextual override val stringResource = Res.string.route_settings
        @Contextual override val icon = Icons.Rounded.Settings
    }

    @Serializable
    data class Documents(
        val type: DocumentType,
        @Contextual override val stringResource: StringResource = Res.string.route_documents,
        @Contextual override val icon: ImageVector = Icons.Rounded.Description
    ) : Route

    @Serializable
    data class Form(
        val type: DocumentType,
        val document: Document? = null,
        @Contextual override val stringResource: StringResource = Res.string.route_form,
        @Contextual override val icon: ImageVector = Icons.Rounded.Person
    ) : Route

    @Serializable
    data class Reference(
        val type: ReferenceType,
        @Contextual override val stringResource: StringResource = Res.string.route_reference,
        @Contextual override val icon: ImageVector = Icons.Rounded.Person
    ) : Route
}
```

### Почему `sealed interface`?

- **Исчерпывающий список.** Компилятор знает все возможные маршруты.
- **`@Serializable`** — навигация должна сериализовать маршруты для сохранения состояния.
- **`@Contextual`** — для полей, которые не имеют встроенного сериализатора (`StringResource`, `ImageVector`).

### Типы маршрутов

| Маршрут | Описание |
|---|---|
| `Start` | Главная страница со статистикой |
| `DocumentTypes` | Список типов документов |
| `References` | Список справочников |
| `Settings` | Настройки проекта |
| `Documents(type)` | Список документов конкретного типа |
| `Form(type, document)` | Форма создания/редактирования |
| `Reference(type)` | Справочник конкретного типа |

---

## Шаг 5.3. Навигация: Router

**Файл:** `shared/src/commonMain/kotlin/.../ui/router/Router.kt`

```kotlin
package ru.example.docmanager.ui.router

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ru.example.docmanager.ui.screens.*
import ru.example.docmanager.ui.screens.forms.FormScreen

@Composable
fun Router(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(
        configuration = routerConfig,
        Route.Start  // Начальный маршрут
    )

    val currentRoute = backStack.last() as Route

    fun onBack() {
        if (backStack.size > 1) backStack.removeLastOrNull()
    }

    fun navigateTo(route: Route, navigationType: NavigationType = NavigationType.PUSH) {
        when (navigationType) {
            NavigationType.PUSH -> Unit
            NavigationType.RESET -> backStack.clear()
            NavigationType.REPLACE -> backStack.removeLastOrNull()
        }
        backStack.add(route)
    }

    RouterContent(
        currentRoute = currentRoute,
        backStack = backStack,
        onBack = ::onBack,
        navigateTo = ::navigateTo,
        modifier = modifier,
    )
}

@Composable
private fun RouterContent(
    currentRoute: Route,
    backStack: NavBackStack<NavKey>,
    onBack: () -> Unit,
    navigateTo: (Route, NavigationType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val bottomBarRoutes = listOf(Route.Start, Route.DocumentTypes, Route.References)
    val showBottomBar = bottomBarRoutes.contains(currentRoute)

    Box(modifier = modifier.background(MaterialTheme.colorScheme.surfaceDim).fillMaxSize()) {
        RouterNavDisplay(
            backStack = backStack,
            navigateTo = navigateTo,
            onBack = onBack,
            modifier = if (showBottomBar) Modifier.fillMaxSize().padding(bottom = 80.dp)
            else Modifier.fillMaxSize()
        )
        RouterBottomBar(
            currentRoute = currentRoute,
            routes = bottomBarRoutes,
            navigateTo = navigateTo,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun RouterNavDisplay(
    backStack: NavBackStack<NavKey>,
    navigateTo: (Route, NavigationType) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canGoBack = backStack.size > 1
    NavDisplay(
        backStack = backStack,
        onBack = onBack,
        transitionSpec = navigationTransition,
        popTransitionSpec = popTransition,
        predictivePopTransitionSpec = predictiveTransition,
        modifier = modifier.fillMaxSize(),
        entryProvider = entryProvider {
            entry<Route.Start> {
                StartScreen(navigateTo = { navigateTo(it, NavigationType.PUSH) }, canGoBack = false, onBack = onBack)
            }
            entry<Route.DocumentTypes> {
                DocumentTypesScreen(navigateTo = { navigateTo(it, NavigationType.PUSH) }, canGoBack = false, onBack = onBack)
            }
            entry<Route.References> {
                ReferencesScreen(navigateTo = { navigateTo(it, NavigationType.PUSH) }, canGoBack = false, onBack = onBack)
            }
            entry<Route.Documents> {
                DocumentsScreen(
                    destination = it,
                    navigateTo = { navigateTo(it, NavigationType.PUSH) },
                    viewModel = koinViewModel(key = it.type.name) { parametersOf(it) },
                    canGoBack = canGoBack, onBack = onBack
                )
            }
            entry<Route.Form> {
                FormScreen(destination = it, navigateTo = navigateTo, canGoBack = canGoBack, onBack = onBack)
            }
            entry<Route.Reference> {
                ReferenceScreen(
                    destination = it,
                    viewModel = koinViewModel(key = it.type.name) { parametersOf(it) },
                    canGoBack = canGoBack, onBack = onBack
                )
            }
            entry<Route.Settings> {
                SettingsScreen(canGoBack = canGoBack, onBack = onBack)
            }
        }
    )
}

@Composable
private fun RouterBottomBar(
    currentRoute: Route,
    routes: List<Route>,
    navigateTo: (Route, NavigationType) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = routes.contains(currentRoute),
        modifier = modifier,
        enter = slideInVertically { fullHeight -> fullHeight } + expandVertically(Alignment.Bottom) + fadeIn(),
        exit = slideOutVertically { fullHeight -> fullHeight } + shrinkVertically(Alignment.Bottom) + fadeOut()
    ) {
        NavigationBar {
            routes.forEach { route ->
                NavigationBarItem(
                    selected = currentRoute == route,
                    onClick = { navigateTo(route, NavigationType.RESET) },
                    icon = { Icon(imageVector = route.icon, contentDescription = stringResource(route.stringResource)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                )
            }
        }
    }
}
```

### Как работает навигация?

1. **`rememberNavBackStack`** — создаёт стек навигации. Начальный маршрут — `Route.Start`.
2. **`navigateTo()`** — добавляет маршрут в стек. `PUSH` — просто добавляет, `RESET` — очищает стек и добавляет, `REPLACE` — удаляет текущий и добавляет новый.
3. **`NavDisplay`** — отображает текущий экран из стека с анимациями переходов.
4. **`RouterBottomBar`** — нижняя панель навигации, видна только на основных экранах (Start, DocumentTypes, References).

---

## Шаг 5.4. Переиспользуемые компоненты

### TextInput

**Файл:** `shared/src/commonMain/kotlin/.../ui/components/TextInput.kt`

```kotlin
package ru.example.docmanager.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.component_input_required
import org.jetbrains.compose.resources.stringResource

@Composable
fun TextInput(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    required: Boolean = false,
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        enabled = enabled,
        onValueChange = onValueChange,
        label = { Text(label) },
        supportingText = if (required) {
            { Text(stringResource(Res.string.component_input_required)) }
        } else null,
        singleLine = singleLine,
        shape = RoundedCornerShape(16.dp),
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        modifier = modifier
    )
}
```

### DateInput

**Файл:** `shared/src/commonMain/kotlin/.../ui/components/DateInput.kt`

```kotlin
package ru.example.docmanager.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.component_input_required
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.ui.utils.*

@Composable
fun DateInput(
    value: LocalDate,
    label: String,
    onValueChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    var textValue by remember(value) { mutableStateOf(value.toRussianString()) }
    val currentInitialValue = remember(value) { value.toEpochMillisUtc() }
    val showDialog = rememberSaveable { mutableStateOf(false) }

    if (enabled) {
        ModalDatePicker(
            showDialog = showDialog.value,
            onDismissRequest = { showDialog.value = false },
            onValueChange = { selectedDate ->
                selectedDate?.toLocalDateFromUtc()?.let { onValueChange(it) }
            },
            initialValue = currentInitialValue
        )
    }

    OutlinedTextField(
        value = textValue,
        enabled = enabled,
        onValueChange = { rawInput ->
            textValue = rawInput
            rawInput.parseRussianOrNull()
                ?: runCatching { LocalDate.parse(rawInput) }.getOrNull()
                ?.let { onValueChange(it) }
        },
        label = { Text(label) },
        supportingText = if (required) {
            { Text(stringResource(Res.string.component_input_required)) }
        } else null,
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        trailingIcon = {
            if (enabled) {
                IconButton(onClick = { showDialog.value = true }) {
                    Icon(Icons.Rounded.DateRange, contentDescription = null)
                }
            }
        },
        visualTransformation = visualTransformation,
        modifier = modifier
    )
}
```

### ReferenceDropdown

**Файл:** `shared/src/commonMain/kotlin/.../ui/components/ReferenceDropdown.kt`

```kotlin
package ru.example.docmanager.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.component_dropdown_empty
import docmanager.shared.generated.resources.component_dropdown_selected
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.domain.reference.Reference

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T : Reference> ReferenceDropdown(
    value: T?,
    items: List<T>,
    itemName: (T?) -> String,
    onValueChange: (T?) -> Unit,
    modifier: Modifier = Modifier,
    itemSupportingText: ((T?) -> String)? = null,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
) {
    var expanded by remember { mutableStateOf(false) }
    var draft by remember(value) { mutableStateOf(itemName(value)) }

    LaunchedEffect(value) {
        if (!expanded) draft = itemName(value)
    }

    val filteredItems = remember(items, draft) {
        if (draft.isBlank()) items
        else items.filter { itemName(it).contains(draft, ignoreCase = true) }
    }

    fun selectItem(item: T) {
        draft = itemName(item)
        onValueChange(item)
        expanded = false
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { shouldExpand ->
            if (!enabled) return@ExposedDropdownMenuBox
            expanded = shouldExpand
            if (shouldExpand) draft = itemName(value)
        },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = draft,
            onValueChange = { newValue ->
                draft = newValue
                expanded = true
            },
            modifier = Modifier.fillMaxWidth().menuAnchor(
                type = ExposedDropdownMenuAnchorType.PrimaryEditable,
                enabled = enabled,
            ),
            enabled = enabled,
            label = label?.let { { Text(it) } },
            placeholder = placeholder?.let { { Text(it) } },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            trailingIcon = {
                val rotationState by animateFloatAsState(
                    targetValue = if (expanded) 180f else 0f, label = "DropdownArrowRotation"
                )
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.size(20.dp).graphicsLayer(rotationZ = rotationState)
                )
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions(onDone = {
                val exactItem = items.firstOrNull { itemName(it) == draft }
                if (exactItem != null) { onValueChange(exactItem); draft = itemName(exactItem); expanded = false }
                else { draft = itemName(value); expanded = false }
            }),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                onValueChange(items.firstOrNull { itemName(it) == draft })
                draft = itemName(value)
                expanded = false
            },
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            modifier = Modifier.exposedDropdownSize().animateContentSize(animationSpec = tween(200)),
        ) {
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(180)) + expandVertically(tween(220)),
                exit = fadeOut(tween(120)) + shrinkVertically(tween(180)),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
                ) {
                    filteredItems.forEachIndexed { index, item ->
                        val selected = item == value
                        SegmentedListItem(
                            selected = selected,
                            shapes = ListItemDefaults.segmentedShapes(index = index, count = filteredItems.size),
                            colors = ListItemDefaults.segmentedColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                            onClick = { selectItem(item) },
                            supportingContent = itemSupportingText?.let { { Text(it(item)) } },
                            trailingContent = {
                                AnimatedVisibility(visible = selected,
                                    enter = fadeIn(tween(160)) + expandVertically(tween(160)),
                                    exit = fadeOut(tween(100)) + shrinkVertically(tween(100))) {
                                    Icon(Icons.Rounded.Check, contentDescription = stringResource(Res.string.component_dropdown_selected),
                                        modifier = Modifier.padding(start = 12.dp).size(20.dp))
                                }
                            }
                        ) { Text(itemName(item)) }
                    }
                    if (filteredItems.isEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainer).padding(16.dp)) {
                            Text(stringResource(Res.string.component_dropdown_empty),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }
}
```

> [!NOTE]
> **Почему обобщённый `<T : Reference>`?** Один и тот же компонент работает для любого типа справочника: Organization, Individual, Product, Supplier. Параметр `itemName` определяет, какое поле отображать.

---

## Шаг 5.5. Утилиты

### Форматирование дат

**Файл:** `shared/src/commonMain/kotlin/.../ui/utils/DateUtils.kt`

```kotlin
package ru.example.docmanager.ui.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

val DATE_FORMAT = LocalDate.Format {
    day(); char('.'); monthNumber(); char('.'); year()
}

fun String.parseRussianOrNull(): LocalDate? = runCatching { DATE_FORMAT.parse(this) }.getOrNull()
fun LocalDate.toEpochMillisUtc(): Long = this.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
fun Long.toLocalDateFromUtc(): LocalDate = Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date
fun LocalDate.toRussianString(): String = DATE_FORMAT.format(this)
```

### Адаптивность

**Файл:** `shared/src/commonMain/kotlin/.../ui/utils/ModifierUtils.kt`

```kotlin
package ru.example.docmanager.ui.utils

import androidx.compose.foundation.layout.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.adaptiveWidth(max: Dp = 891.dp): Modifier = this.widthIn(max = max).fillMaxWidth()

@Composable
fun Modifier.applyAdaptive(whenWide: Modifier = Modifier, whenNotWide: Modifier = Modifier): Modifier {
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(891)
    return if (isWide) this.then(whenWide) else this.then(whenNotWide)
}
```

---

## Шаг 5.6. Экран формы доверенности

**Файл:** `shared/src/commonMain/kotlin/.../ui/screens/forms/powerofattorney/PowerOfAttorneyScreen.kt`

```kotlin
package ru.example.docmanager.ui.screens.forms.powerofattorney

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import docmanager.shared.generated.resources.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.example.docmanager.data.reference.*
import ru.example.docmanager.domain.document.Document
import ru.example.docmanager.domain.document.header.HeaderFieldType
import ru.example.docmanager.ui.components.*
import ru.example.docmanager.ui.router.*
import ru.example.docmanager.ui.utils.*
import ru.example.docmanager.ui.viewmodel.forms.FormEvent
import ru.example.docmanager.ui.viewmodel.forms.powerofattorney.*
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun PowerOfAttorneyScreen(
    document: Document?,
    navigateTo: (Route, NavigationType) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PowerOfAttorneyViewModel = koinInject()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val formState by viewModel.uiState.collectAsStateWithLifecycle()

    fun showSnackbar(message: String) = scope.launch { snackbarHostState.showSnackbar(message) }

    // Обработка событий от ViewModel
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is FormEvent.Status -> showSnackbar(event.message)
                is FormEvent.Error -> showSnackbar(event.message)
                is FormEvent.DocumentSaved<*> -> navigateTo(
                    Route.Form(formState.type, event.document), NavigationType.REPLACE)
                FormEvent.DocumentDeleted -> navigateTo(
                    Route.Documents(formState.type), NavigationType.REPLACE)
                else -> Unit
            }
        }
    }

    // Инициализация формы
    LaunchedEffect(document) { viewModel.onInit(document = document) }

    if (formState.isLoading) { LoadingBox(modifier = modifier); return }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { AppSnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(Res.string.form_fab)) },
                icon = { Icon(Icons.Rounded.Add, contentDescription = stringResource(Res.string.form_fab)) },
                onClick = {
                    viewModel.addBodyItem()
                    scope.launch { delay(280.milliseconds); scrollState.animateScrollTo(scrollState.maxValue) }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
                .verticalScroll(scrollState).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.adaptiveWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                // Кнопки действий
                PowerOfAttorneyActions(
                    isEditing = formState.isEditing,
                    onSaveDocument = { viewModel.onSaveDocument() },
                    onSaveAsNewDocument = { viewModel.onSaveAsNewDocument() },
                    onDeleteDocument = { viewModel.onDeleteDocument() },
                    onExportDocument = { viewModel.onExportToFile() },
                    onPrintDocument = { viewModel.onPrintDocument() }
                )
                // Секция шапки
                PowerOfAttorneyHeaderSection(
                    formState = formState,
                    onNumberChanged = viewModel::onNumberChanged,
                    onDischargeDateChanged = viewModel::onDischargeDateChanged,
                    onEndDateChanged = viewModel::onEndDateChanged,
                    onOrganizationChanged = viewModel::onOrganizationChanged,
                    onIndividualChanged = viewModel::onIndividualChanged,
                    onSupplierChanged = viewModel::onSupplierChanged,
                    onSupplierAgreementChanged = viewModel::onSupplierAgreementChanged,
                )
                // Секция тела
                PowerOfAttorneyBodySection(
                    formState = formState,
                    onUpdateBodyItem = viewModel::updateBodyItem,
                    onRemoveBodyItem = viewModel::removeBodyItem,
                )
            }
        }
    }
}

@Composable
private fun PowerOfAttorneySection(label: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val isExpanded = remember { mutableStateOf(true) }
    Card(modifier = modifier.fillMaxWidth().animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Row(modifier = Modifier.fillMaxWidth().clickable { isExpanded.value = !isExpanded.value }.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = label, style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.weight(1f))
            Icon(imageVector = if (isExpanded.value) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                contentDescription = null, modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface)
        }
        if (isExpanded.value) content()
    }
}

@Composable
private fun PowerOfAttorneyHeaderSection(
    formState: PowerOfAttorneyUiState,
    onNumberChanged: (Int) -> Unit,
    onDischargeDateChanged: (LocalDate) -> Unit,
    onEndDateChanged: (LocalDate) -> Unit,
    onOrganizationChanged: (Organization?) -> Unit,
    onIndividualChanged: (Individual?) -> Unit,
    onSupplierChanged: (Supplier?) -> Unit,
    onSupplierAgreementChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    PowerOfAttorneySection(label = stringResource(Res.string.form_section_header), modifier = modifier) {
        Column(modifier = modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            IntInput(value = formState.number, onValueChange = onNumberChanged,
                label = stringResource(HeaderFieldType.NUMBER.stringResource), modifier = Modifier.fillMaxWidth())
            DateInput(value = formState.dischargeDate, onValueChange = onDischargeDateChanged,
                label = stringResource(HeaderFieldType.DISCHARGE_DATE.stringResource), modifier = Modifier.fillMaxWidth())
            DateInput(value = formState.endDate, onValueChange = onEndDateChanged,
                label = stringResource(HeaderFieldType.END_DATE.stringResource), modifier = Modifier.fillMaxWidth())
            ReferenceDropdown(value = formState.organization, items = formState.organizations,
                itemName = { it?.name?.ifBlank { "ID: ${it.id}" } ?: "" },
                onValueChange = onOrganizationChanged,
                label = stringResource(Res.string.form_field_organization), modifier = Modifier.fillMaxWidth())
            ReferenceDropdown(value = formState.individual, items = formState.individuals,
                itemName = { it?.name?.ifBlank { "ID: ${it.id}" } ?: "" },
                itemSupportingText = { it?.job ?: "" },
                onValueChange = onIndividualChanged,
                label = stringResource(Res.string.form_field_individual), modifier = Modifier.fillMaxWidth())
            ReferenceDropdown(value = formState.supplier, items = formState.suppliers,
                itemName = { it?.name?.ifBlank { "ID: ${it.id}" } ?: "" },
                onValueChange = onSupplierChanged,
                label = stringResource(Res.string.form_field_supplier), modifier = Modifier.fillMaxWidth())
            TextInput(value = formState.supplierAgreement, onValueChange = onSupplierAgreementChanged,
                label = stringResource(HeaderFieldType.SUPPLIER_AGREEMENT.stringResource), modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun PowerOfAttorneyBodySection(
    formState: PowerOfAttorneyUiState,
    onUpdateBodyItem: (index: Int, item: PowerOfAttorneyBodyItemState) -> Unit,
    onRemoveBodyItem: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    PowerOfAttorneySection(label = stringResource(Res.string.form_section_body), modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize().applyAdaptive(
            whenNotWide = Modifier.horizontalScroll(rememberScrollState()))
            .padding(16.dp).animateContentSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            formState.bodyItems.forEachIndexed { index, item ->
                BodyItemRow(index = index, item = item, products = formState.products,
                    onItemChange = { updated -> onUpdateBodyItem(index, updated) },
                    onDelete = { onRemoveBodyItem(index) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BodyItemRow(
    index: Int, item: PowerOfAttorneyBodyItemState, products: List<Product>,
    onItemChange: (PowerOfAttorneyBodyItemState) -> Unit, onDelete: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(42.dp).clip(MaterialShapes.Slanted.toShape())
            .background(MaterialTheme.colorScheme.secondary), contentAlignment = Alignment.Center) {
            Text(text = "${index + 1}", color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
        ReferenceDropdown(value = item.product, items = products,
            itemName = { it?.name?.ifBlank { "ID: ${it.id}" } ?: "" },
            onValueChange = { onItemChange(item.copy(product = it)) },
            label = stringResource(Res.string.form_field_product),
            modifier = Modifier.applyAdaptive(whenWide = Modifier.weight(2f), whenNotWide = Modifier.requiredWidth(300.dp)))
        TextInput(value = item.unit, onValueChange = { onItemChange(item.copy(unit = it)) },
            label = stringResource(Res.string.body_field_unit), singleLine = true,
            modifier = Modifier.applyAdaptive(whenWide = Modifier.weight(1f), whenNotWide = Modifier.widthIn(min = 150.dp)))
        TextInput(value = item.count, onValueChange = { onItemChange(item.copy(count = it)) },
            label = stringResource(Res.string.body_field_count), singleLine = true,
            modifier = Modifier.applyAdaptive(whenWide = Modifier.weight(1.5f), whenNotWide = Modifier.widthIn(min = 300.dp)))
        FilledTonalIconButton(onClick = onDelete) {
            Icon(Icons.Rounded.Delete, contentDescription = stringResource(Res.string.component_delete_row))
        }
    }
}

@Composable
private fun PowerOfAttorneyActions(
    isEditing: Boolean, onSaveDocument: () -> Unit, onSaveAsNewDocument: () -> Unit,
    onDeleteDocument: () -> Unit, onExportDocument: () -> Unit, onPrintDocument: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(modifier = modifier.fillMaxWidth().animateContentSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically) {
        Button(onClick = onSaveDocument) { Text(stringResource(Res.string.form_button_save)) }
        if (isEditing) {
            OutlinedButton(onClick = onSaveAsNewDocument) { Text(stringResource(Res.string.form_button_save_as_new)) }
            OutlinedButton(onClick = onDeleteDocument) { Text(stringResource(Res.string.form_button_delete)) }
        }
        Button(onClick = onExportDocument) { Text(stringResource(Res.string.form_button_export)) }
        Button(onClick = onPrintDocument) { Text(stringResource(Res.string.form_button_print)) }
    }
}
```

> [!IMPORTANT]
> **Ключевые паттерны:**
> - `collectAsStateWithLifecycle()` — подписка на StateFlow, которая автоматически приостанавливается, когда экран не виден.
> - `LaunchedEffect` — запускает корутину при изменении параметров.
> - `koinInject()` — получает ViewModel из Koin.
> - `Scaffold` — базовая структура экрана с topBar, bottomBar, FAB и snackbar.

---

**Следующий шаг:** [Шаг 6. ViewModel и состояние](step6-viewmodel.md) — логика формы, StateFlow, обработка событий.
