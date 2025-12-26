## 开发目标
### Focus Flow - Kotlin 开发设计文档

### 一、项目概述

Focus Flow 是一款基于 Kotlin 开发的专注番茄钟 + 白噪音播放器应用，采用现代 Android 开发架构和最佳实践。

### 核心特性
- 番茄钟倒计时（支持自定义时长或者10/15/30 分钟快捷定时）
- 多种白噪音播放（雨声、林涛、海浪、夏夜、篝火）
- 后台运行保活（前台 Service + WorkManager）
- 优质倒计时动画 View（圆形进度条、颜色渐变）
- 专注时长历史记录统计模块
### 质量要求
- 基础功能完整，逻辑通顺，无明显闪退（Crash）。
- 工作量饱满（Activity/Fragment数量不少于5个，涉及Room数据库操作）。
- 代码规范（命名规范、注释清晰）。
- 适当使用高级特性（如Jetpack组件、MVVM架构、自定义View、NDK等）酌情加分。
- UI/UX设计美观，交互流畅（非原生丑陋界面）。
- 重要：UI定制：应用必须包含一套独特的主题色和LOGO.

### 整体思路
1. 启动页面展示开发者姓名学号以及开发者的圆形头像图标，点击按钮后进入主界面
2. 主界面包含倒计时展示，白噪音播放启动、暂停、切换按钮，需展示当前白噪音的名称和圆形图标，并随按钮同步切换，白噪音播放过程中圆形图标旋转，白噪音循环播放
3. 主界面底部包含菜单栏，点击切换界面
4. 白噪音选择界面
    * 列出可选的白噪音模式，包括名称及圆形图标
    * 有播放按钮可进行试听，选择按钮将其选择为主界面的播放音频
5. 专注时长历史记录页
    * 记录用户最近完成的倒计时情况，包括专注时间，开始时间和选择的白噪音模式，要求倒计时从开始到完整结束才会加入到历史记录。
    * 配置数据统计按钮，转到数据统计页面，用于展示用户专注时间最长的一次记录，并图形化用户的专注历史记录，每产生一次专注记录更新一次
6. 倒计时设置界面
   * 可由主界面转到倒计时设置界面，并可以设置x时x分的倒计时，并添加快捷倒计时按钮，实现10/15/30的倒计时。
7. 软件版本展示页面，展示开发者相关信息并展示软件版本号。
### 技术栈

#### 核心框架
- **Kotlin** - 主要开发语言
- **Android Jetpack** - 现代Android开发组件集
  - **ViewModel** - 生命周期感知的数据管理
  - **LiveData** - 可观察的数据持有者
  - **Room** - SQLite数据库ORM框架
  - **Navigation** - Fragment导航管理
  - **WorkManager** - 后台任务调度
  - **ViewBinding** - 视图绑定

#### UI框架
- **Material Design 3** - UI组件库
- **ConstraintLayout** - 布局管理
- **RecyclerView** - 列表展示
- **自定义View** - 圆形进度条、旋转动画

#### 媒体播放
- **MediaPlayer** - 音频播放
- **Foreground Service** - 前台服务保活

#### 数据可视化
- **MPAndroidChart** - 图表绘制（专注历史统计）

#### 协程
- **Kotlin Coroutines** - 异步编程
- **Flow** - 响应式数据流
## 三、技术架构设计

### 3.1 整体架构

采用 **MVVM (Model-View-ViewModel)** 架构模式：

```
┌─────────────────────────────────────────────────────┐
│                    View Layer                        │
│  (Activity/Fragment + XML Layout + Custom Views)    │
└───────────────────┬─────────────────────────────────┘
                    │ 观察 LiveData/Flow
                    │ 调用 ViewModel 方法
┌───────────────────▼─────────────────────────────────┐
│                 ViewModel Layer                      │
│   (持有业务逻辑，暴露LiveData，调用Repository)       │
└───────────────────┬─────────────────────────────────┘
                    │ 调用数据仓库
┌───────────────────▼─────────────────────────────────┐
│                Repository Layer                      │
│     (统一数据访问接口，协调本地和远程数据)           │
└───────┬───────────────────────┬─────────────────────┘
        │                       │
┌───────▼─────────┐    ┌────────▼──────────┐
│  Local Data     │    │  Media Player     │
│  (Room DB)      │    │  (MediaPlayer +   │
│                 │    │   Service)        │
└─────────────────┘    └───────────────────┘
```

### 3.2 核心模块划分

#### 1. UI模块 (ui/)
```
ui/
├── splash/
│   └── SplashActivity.kt                 # 启动页（展示开发者信息）
├── main/
│   └── MainActivity.kt                   # 主Activity（容器）
├── home/
│   ├── HomeFragment.kt                   # 主界面Fragment
│   ├── HomeViewModel.kt
│   └── view/
│       └── CircularProgressView.kt       # 自定义圆形进度条
├── timer/
│   ├── TimerSettingFragment.kt           # 倒计时设置Fragment
│   └── TimerSettingViewModel.kt
├── sound/
│   ├── SoundSelectionFragment.kt         # 白噪音选择Fragment
│   ├── SoundSelectionViewModel.kt
│   └── adapter/
│       └── SoundAdapter.kt               # RecyclerView适配器
├── history/
│   ├── HistoryFragment.kt                # 历史记录Fragment
│   ├── HistoryViewModel.kt
│   └── adapter/
│       └── HistoryAdapter.kt
├── statistics/
│   ├── StatisticsFragment.kt             # 数据统计Fragment
│   └── StatisticsViewModel.kt
└── about/
    └── AboutFragment.kt                  # 关于页面Fragment
```

#### 2. 数据模块 (data/)
```
data/
├── model/
│   ├── FocusSession.kt                   # 专注记录实体
│   └── WhiteNoise.kt                     # 白噪音模型
├── local/
│   ├── AppDatabase.kt                    # Room数据库
│   ├── dao/
│   │   └── FocusSessionDao.kt            # DAO接口
│   └── entity/
│       └── FocusSessionEntity.kt         # 数据库实体
└── repository/
    ├── FocusRepository.kt                # 专注记录仓库
    └── WhiteNoiseRepository.kt           # 白噪音数据仓库
```

#### 3. 服务模块 (service/)
```
service/
├── TimerService.kt                       # 倒计时前台服务
├── MediaPlayerManager.kt                 # 媒体播放管理器
└── NotificationHelper.kt                 # 通知辅助类
```

#### 4. 工具模块 (util/)
```
util/
├── Constants.kt                          # 常量定义
├── PreferenceHelper.kt                   # SharedPreferences封装
├── TimeFormatter.kt                      # 时间格式化工具
└── AnimationUtil.kt                      # 动画工具类
```




