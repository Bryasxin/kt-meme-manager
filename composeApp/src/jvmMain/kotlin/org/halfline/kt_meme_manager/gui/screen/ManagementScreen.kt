package org.halfline.kt_meme_manager.gui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.halfline.kt_meme_manager.core.MemeViewModel
import org.halfline.kt_meme_manager.gui.component.AddMemeDrawer
import org.halfline.kt_meme_manager.gui.component.MemeItem

@Composable
fun ManagementScreen(viewModel: MemeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val memes = viewModel.memes
    var searchQuery by remember { mutableStateOf("") }
    var viewType by remember { mutableStateOf(ViewType.GRID) }
    var sortBy by remember { mutableStateOf(SortOption.NAME) }
    var showAddMemeDrawer by remember { mutableStateOf(false) }
    var memeToEdit by remember { mutableStateOf<org.halfline.kt_meme_manager.core.Meme?>(null) }

    val filteredMemes = remember(memes, searchQuery, sortBy) {
        val result = if (searchQuery.isBlank()) {
            memes
        } else {
            memes.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.collection.contains(searchQuery, ignoreCase = true)
            }
        }

        when (sortBy) {
            SortOption.NAME -> result.sortedBy { it.name.lowercase() }
            SortOption.COLLECTION -> result.sortedBy { it.collection.lowercase() }
            SortOption.DATE -> result.sortedByDescending { it.createdAt }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 顶部工具栏
        ManagementToolbar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            viewType = viewType,
            onViewTypeChange = { viewType = it },
            sortBy = sortBy,
            onSortByChange = { sortBy = it }
        )

        if (filteredMemes.isEmpty()) {
            EmptyMemesView(hasAnyMemes = memes.isNotEmpty(), searchQuery = searchQuery)
        } else {
            when (viewType) {
                ViewType.GRID -> MemeGrid(filteredMemes, viewModel) { meme ->
                    memeToEdit = meme
                    showAddMemeDrawer = true
                }

                ViewType.LIST -> MemeList(filteredMemes, viewModel) { meme ->
                    memeToEdit = meme
                    showAddMemeDrawer = true
                }
            }
        }
    }

    if (showAddMemeDrawer) {
        AddMemeDrawer(
            selectedFile = null,
            onFileSelected = {},
            onDismiss = {
                showAddMemeDrawer = false
                memeToEdit = null
            },
            onUpload = { _, _ -> },
            memeToEdit = memeToEdit,
            onUpdate = { id, name, collection ->
                viewModel.updateMeme(id, name, collection)
                showAddMemeDrawer = false
                memeToEdit = null
            }
        )
    }
}

@Composable
private fun ManagementToolbar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    viewType: ViewType,
    onViewTypeChange: (ViewType) -> Unit,
    sortBy: SortOption,
    onSortByChange: (SortOption) -> Unit
) {
    var expandedSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Search memes") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 排序按钮
            Box {
                OutlinedButton(
                    onClick = { expandedSortMenu = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "Sort",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (sortBy) {
                                SortOption.NAME -> "Name"
                                SortOption.COLLECTION -> "Collection"
                                SortOption.DATE -> "Date"
                            },
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                DropdownMenu(
                    expanded = expandedSortMenu,
                    onDismissRequest = { expandedSortMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Sort by Name") },
                        onClick = {
                            onSortByChange(SortOption.NAME)
                            expandedSortMenu = false
                        },
                        leadingIcon = {
                            if (sortBy == SortOption.NAME) {
                                Icon(Icons.Default.Check, contentDescription = "Selected")
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Sort by Collection") },
                        onClick = {
                            onSortByChange(SortOption.COLLECTION)
                            expandedSortMenu = false
                        },
                        leadingIcon = {
                            if (sortBy == SortOption.COLLECTION) {
                                Icon(Icons.Default.Check, contentDescription = "Selected")
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Sort by Date") },
                        onClick = {
                            onSortByChange(SortOption.DATE)
                            expandedSortMenu = false
                        },
                        leadingIcon = {
                            if (sortBy == SortOption.DATE) {
                                Icon(Icons.Default.Check, contentDescription = "Selected")
                            }
                        }
                    )
                }
            }

            // 视图切换按钮
            OutlinedButton(
                onClick = {
                    onViewTypeChange(
                        if (viewType == ViewType.GRID) ViewType.LIST else ViewType.GRID
                    )
                }
            ) {
                Icon(
                    imageVector = if (viewType == ViewType.GRID) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                    contentDescription = if (viewType == ViewType.GRID) "Switch to list view" else "Switch to grid view",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyMemesView(hasAnyMemes: Boolean, searchQuery: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = if (hasAnyMemes && searchQuery.isNotBlank()) Icons.Default.SearchOff else Icons.Default.AddPhotoAlternate,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (hasAnyMemes && searchQuery.isNotBlank()) {
                    "No memes match your search"
                } else {
                    "No memes yet"
                },
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = if (hasAnyMemes && searchQuery.isNotBlank()) {
                    "Try adjusting your search terms"
                } else {
                    "Click the + button to add your first meme"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun MemeGrid(
    memes: List<org.halfline.kt_meme_manager.core.Meme>,
    viewModel: MemeViewModel,
    onEditMeme: (org.halfline.kt_meme_manager.core.Meme) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(memes) { meme ->
            MemeItem(
                meme = meme,
                onEdit = { onEditMeme(meme) },
                onDelete = {
                    viewModel.removeMeme(meme.id)
                }
            )
        }
    }
}

@Composable
private fun MemeList(
    memes: List<org.halfline.kt_meme_manager.core.Meme>,
    viewModel: MemeViewModel,
    onEditMeme: (org.halfline.kt_meme_manager.core.Meme) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(memes) { meme ->
            MemeItem(
                meme = meme,
                onEdit = { onEditMeme(meme) },
                onDelete = {
                    viewModel.removeMeme(meme.id)
                }
            )
        }
    }
}

enum class ViewType {
    GRID, LIST
}

enum class SortOption {
    NAME, COLLECTION, DATE
}