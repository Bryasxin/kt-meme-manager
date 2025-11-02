# Meme Manager

一个使用 Kotlin 构建的跨平台表情包管理器。

![Screenshot](https://s1.imagehub.cc/images/2025/11/02/c6ac3f860ff831013cd5bafe37cf6b1b.png)

## 功能特性

- 表情包管理：添加、编辑、删除和组织您的表情包
- 集合分类：将表情包按集合进行分组管理
- 快速复制：一键将表情包复制到剪贴板
- 数据一致性检查：自动维护数据完整性
- 本地存储：所有数据安全地存储在本地

## 技术栈

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)

## 安装要求

- JDK 21 或更高版本
- Gradle 8.14.3 或更高版本

## 构建和运行

### 克隆仓库

```bash
git clone https://your-repo-url/KtMemeManager.git
cd KtMemeManager
```

### 运行应用

```bash
./gradlew run
```

### 构建发行版

构建针对您当前平台的可执行文件：

```bash
./gradlew package
```

构建产物将位于 `composeApp/build/compose/binaries/main/app` 目录中。

## 使用说明

1. 启动应用程序后，您将看到主界面，包含三个主要部分：主页、管理和设置。
2. 点击右下角的 “+” 按钮添加新的表情包。
3. 在添加对话框中，选择图像文件并填写名称和集合信息。
4. 在主页或管理页面浏览您的表情包。
5. 将鼠标悬停在任何表情包上以显示操作按钮：复制、编辑和删除。
6. 在设置页面可以执行数据一致性检查。

## 贡献

欢迎提交 Issue 和 Pull Request 来帮助改进这个项目！
