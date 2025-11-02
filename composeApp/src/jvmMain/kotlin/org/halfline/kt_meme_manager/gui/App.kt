package org.halfline.kt_meme_manager.gui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.halfline.kt_meme_manager.core.MemeViewModel
import org.halfline.kt_meme_manager.gui.component.AddMemeDrawer
import org.halfline.kt_meme_manager.gui.component.AppTopAppBar
import org.halfline.kt_meme_manager.gui.component.SideNavigation
import org.halfline.kt_meme_manager.gui.screen.HomeScreen
import org.halfline.kt_meme_manager.gui.screen.ManagementScreen
import org.halfline.kt_meme_manager.gui.screen.SettingsScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    var selectedItem by remember { mutableStateOf(0) }
    var showUploadDrawer by remember { mutableStateOf(false) }
    var selectedFile by remember { mutableStateOf<File?>(null) }
    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<MemeViewModel>()

    MaterialTheme {
        Scaffold(topBar = {
            AppTopAppBar()
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showUploadDrawer = true
                    selectedFile = null
                }) {
                Icon(Icons.Default.Add, contentDescription = "Add Meme")
            }
        }) { paddingValues ->
            Row(
                modifier = Modifier.padding(paddingValues).fillMaxSize()
            ) {
                // 侧边栏导航
                SideNavigation(
                    selectedItem = selectedItem, onItemSelected = { selectedItem = it })

                // 主内容区域
                Box(modifier = Modifier.fillMaxSize()) {
                    when (selectedItem) {
                        0 -> HomeScreen()
                        1 -> ManagementScreen()
                        2 -> SettingsScreen()
                    }
                }
            }

            // 上传抽屉
            if (showUploadDrawer) {
                AddMemeDrawer(selectedFile = selectedFile, onFileSelected = { selectedFile = it }, onDismiss = {
                    showUploadDrawer = false
                    selectedFile = null
                }, onUpload = { memeName, collection ->
                    selectedFile?.let { file ->
                        viewModel.saveMeme(file, memeName, collection)
                    }
                    showUploadDrawer = false
                    selectedFile = null
                })
            }
        }
    }
}