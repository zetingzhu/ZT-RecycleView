# 可折叠RecyclerView列表实现

这是一个使用RecyclerView实现的可折叠列表项目，支持分组展示和动画效果。

## 功能特性

- ✅ 可折叠的分组列表
- ✅ 平滑的展开/收起动画
- ✅ 图标旋转动画效果
- ✅ 点击事件处理
- ✅ 现代化的Material Design界面

## 项目结构

```
src/main/java/com/trade/zt_rv_group_list/
├── MainActivity.kt                 # 主Activity
├── adapter/
│   └── ExpandableAdapter.kt       # 可折叠适配器
└── model/
    └── GroupItem.kt               # 数据模型

src/main/res/
├── layout/
│   ├── activity_main.xml          # 主布局
│   ├── item_group.xml             # 分组项布局
│   └── item_child.xml             # 子项布局
└── drawable/
    ├── ic_expand_more.xml         # 展开图标
    └── ic_expand_less.xml         # 收起图标
```

## 核心组件说明

### 1. 数据模型 (GroupItem.kt)
- `GroupItem`: 分组数据模型，包含标题、子项列表和展开状态
- `ChildItem`: 子项数据模型，包含标题和描述
- `DisplayItem`: 用于RecyclerView显示的封装类

### 2. 适配器 (ExpandableAdapter.kt)
- 支持两种ViewType：GROUP和CHILD
- 实现展开/收起动画效果
- 提供点击事件回调接口

### 3. 主要功能
- **展开/收起**: 点击分组项可以展开或收起子项
- **动画效果**: 图标旋转动画和列表项插入/删除动画
- **事件处理**: 分组和子项的点击事件处理

## 使用方法

1. 创建数据源：
```kotlin
val groupItems = mutableListOf(
    GroupItem(
        id = 1,
        title = "分组1",
        children = mutableListOf(
            ChildItem(1, "子项1", "描述1"),
            ChildItem(2, "子项2", "描述2")
        )
    )
)
```

2. 设置适配器：
```kotlin
val adapter = ExpandableAdapter(groupItems)
adapter.onGroupClickListener = { groupItem, position ->
    // 处理分组点击事件
}
adapter.onChildClickListener = { childDisplayItem, position ->
    // 处理子项点击事件
}
recyclerView.adapter = adapter
```

## 自定义扩展

### 添加新的数据类型
可以通过扩展`DisplayItem`密封类来添加新的列表项类型。

### 自定义动画
可以在`ExpandableAdapter`中修改动画效果，如改变动画时长、插值器等。

### 样式定制
可以修改布局文件中的样式，如颜色、字体大小、间距等。

## 依赖项

项目使用了以下主要依赖：
- `androidx.recyclerview:recyclerview:1.3.2`
- `androidx.cardview:cardview:1.0.0`
- `androidx.appcompat:appcompat:1.7.1`
- `com.google.android.material:material:1.13.0`

## 运行项目

1. 确保Android Studio已安装
2. 打开项目
3. 同步Gradle依赖
4. 运行到设备或模拟器

项目将显示一个包含多个分组的可折叠列表，点击分组标题可以展开或收起子项。