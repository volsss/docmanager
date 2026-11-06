/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License v3.0.
 *
 * Distributed WITHOUT ANY WARRANTY. See the GNU General Public License
 * for more details: <https://gnu.org>.
 */

package ru.example.docmanager.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ListAlt
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import docmanager.shared.generated.resources.*
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ru.example.docmanager.ui.components.LoadingBox
import ru.example.docmanager.ui.router.Route
import ru.example.docmanager.ui.utils.adaptiveWidth
import ru.example.docmanager.ui.utils.applyAdaptive
import ru.example.docmanager.ui.utils.toRussianString
import ru.example.docmanager.ui.viewmodel.start.StartUiState
import ru.example.docmanager.ui.viewmodel.start.StartViewModel

@Composable
fun StartScreen(
    navigateTo: (Route) -> Unit,
    canGoBack: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StartViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colorScheme.surface,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    AnimatedVisibility(
                        visible = canGoBack,
                        enter = expandHorizontally() + fadeIn(),
                        exit = shrinkHorizontally() + fadeOut()
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(Res.string.component_back_button)
                            )
                        }
                    }
                },
                title = {
                    Text(
                        text = stringResource(Res.string.route_home),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    FilledTonalIconButton(
                        onClick = { navigateTo(Route.Settings) },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = stringResource(Res.string.route_settings)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface,
                    titleContentColor = colorScheme.onSurface,
                    navigationIconContentColor = colorScheme.onSurface,
                    actionIconContentColor = colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        StartContent(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@Composable
private fun StartContent(
    state: StartUiState,
    modifier: Modifier = Modifier
) {
    if (state.isLoading) {
        LoadingBox(modifier = modifier)
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .adaptiveWidth(max = 1240.dp)
                .fillMaxSize()
                .applyAdaptive(
                    whenWide = Modifier.padding(horizontal = 28.dp),
                    whenNotWide = Modifier.padding(horizontal = 16.dp)
                ),
            contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            item(key = "workspace-overview") {
                HeroSection(
                    state = state,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item(key = "overview-heading") {
                SectionHeading()
            }
            item(key = "statistics") {
                StatsGrid(state = state)
            }
        }
    }
}

@Composable
private fun HeroSection(
    state: StartUiState,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val projectName = state.projectName.ifBlank { stringResource(Res.string.start_project_untitled) }
    val heroShape = RoundedCornerShape(
        topStart = 36.dp,
        topEnd = 36.dp,
        bottomEnd = 36.dp,
        bottomStart = 12.dp
    )

    Surface(
        modifier = modifier
            .heightIn(min = 252.dp)
            .clip(heroShape)
            .animateContentSize(),
        shape = heroShape,
        color = colorScheme.primaryContainer,
        contentColor = colorScheme.onPrimaryContainer
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 54.dp, y = (-76).dp)
                    .size(218.dp)
                    .background(
                        color = colorScheme.secondary.copy(alpha = 0.16f),
                        shape = CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 38.dp, y = 52.dp)
                    .size(144.dp)
                    .background(
                        color = colorScheme.tertiary.copy(alpha = 0.18f),
                        shape = CircleShape
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp, vertical = 25.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .background(colorScheme.primary, CircleShape)
                    )
                    Text(
                        text = stringResource(Res.string.start_hero_eyebrow),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorScheme.primary
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(11.dp)
                    ) {
                        Text(
                            text = projectName,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = stringResource(Res.string.start_hero_tagline),
                            style = MaterialTheme.typography.bodyLarge,
                            color = colorScheme.onPrimaryContainer.copy(alpha = 0.78f)
                        )
                    }
                    ProjectArtwork()
                }
                ProjectMetadata(state = state)
                CreationDate(date = state.creationDate)
            }
        }
    }
}

@Composable
private fun ProjectArtwork() {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier.size(96.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .size(66.dp)
                .rotate(13f)
                .offset(x = 10.dp, y = 5.dp),
            shape = RoundedCornerShape(21.dp),
            color = colorScheme.tertiaryContainer
        ) {}
        Surface(
            modifier = Modifier
                .size(66.dp)
                .rotate(-10f)
                .offset(x = (-6).dp, y = 2.dp),
            shape = RoundedCornerShape(21.dp),
            color = colorScheme.secondaryContainer
        ) {}
        Surface(
            modifier = Modifier.size(66.dp),
            shape = RoundedCornerShape(21.dp),
            color = colorScheme.primary,
            contentColor = colorScheme.onPrimary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.Description,
                    contentDescription = null,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}

@Composable
private fun ProjectMetadata(state: StartUiState) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (state.version.isNotBlank()) {
            InfoChip(
                icon = Icons.Rounded.Tag,
                label = stringResource(Res.string.start_project_version, state.version)
            )
        }
        if (state.author.isNotBlank()) {
            InfoChip(
                icon = Icons.Rounded.Person,
                label = state.author
            )
        }
    }
}

@Composable
private fun InfoChip(
    icon: ImageVector,
    label: String
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        shape = RoundedCornerShape(50),
        color = colorScheme.onPrimaryContainer.copy(alpha = 0.08f),
        contentColor = colorScheme.onPrimaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CreationDate(date: LocalDate?) {
    if (date == null) return

    Row(
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.CalendarToday,
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f)
        )
        Text(
            text = stringResource(
                Res.string.start_project_created_on,
                date.toRussianString()
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f)
        )
    }
}

@Composable
private fun SectionHeading() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = stringResource(Res.string.start_section_overview),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private data class StatItem(
    val title: String,
    val value: Int,
    val icon: ImageVector,
    val containerColor: Color,
    val contentColor: Color,
    val shape: Shape
)

@Composable
private fun StatsGrid(state: StartUiState) {
    val colorScheme = MaterialTheme.colorScheme
    val stats = listOf(
        StatItem(
            title = stringResource(Res.string.start_stat_document_types),
            value = state.documentTypesCount,
            icon = Icons.Rounded.Category,
            containerColor = colorScheme.primaryContainer,
            contentColor = colorScheme.onPrimaryContainer,
            shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp, bottomEnd = 12.dp, bottomStart = 34.dp)
        ),
        StatItem(
            title = stringResource(Res.string.start_stat_documents),
            value = state.documentsCount,
            icon = Icons.Rounded.Description,
            containerColor = colorScheme.secondaryContainer,
            contentColor = colorScheme.onSecondaryContainer,
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 34.dp, bottomEnd = 34.dp, bottomStart = 34.dp)
        ),
        StatItem(
            title = stringResource(Res.string.start_stat_reference_types),
            value = state.referenceTypesCount,
            icon = Icons.Rounded.DynamicFeed,
            containerColor = colorScheme.tertiaryContainer,
            contentColor = colorScheme.onTertiaryContainer,
            shape = RoundedCornerShape(topStart = 34.dp, topEnd = 12.dp, bottomEnd = 34.dp, bottomStart = 34.dp)
        ),
        StatItem(
            title = stringResource(Res.string.start_stat_references),
            value = state.referencesCount,
            icon = Icons.AutoMirrored.Rounded.ListAlt,
            containerColor = colorScheme.surfaceVariant,
            contentColor = colorScheme.onSurfaceVariant,
            shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp, bottomEnd = 34.dp, bottomStart = 12.dp)
        )
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stats.forEach { stat ->
            StatCard(
                stat = stat,
                modifier = Modifier.applyAdaptive(
                    whenWide = Modifier.weight(1f),
                    whenNotWide = Modifier.fillMaxWidth(0.47f)
                )
            )
        }
    }
}

@Composable
private fun StatCard(
    stat: StatItem,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.heightIn(min = 190.dp),
        shape = stat.shape,
        color = stat.containerColor,
        contentColor = stat.contentColor
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-34).dp)
                    .size(112.dp)
                    .background(
                        color = stat.contentColor.copy(alpha = 0.07f),
                        shape = CircleShape
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 190.dp)
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.start_stat_total),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = stat.contentColor.copy(alpha = 0.72f)
                    )
                    Surface(
                        shape = RoundedCornerShape(15.dp),
                        color = stat.contentColor.copy(alpha = 0.10f),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = stat.icon,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(
                        text = stat.value.toString(),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = stat.contentColor,
                        maxLines = 1
                    )
                    Text(
                        text = stat.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = stat.contentColor.copy(alpha = 0.82f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}