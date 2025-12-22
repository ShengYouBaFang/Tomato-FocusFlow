# Focus Flow - Kotlin 开发设计文档

## 一、项目概述

Focus Flow 是一款基于 Kotlin 开发的专注番茄钟 + 白噪音播放器应用，采用现代 Android 开发架构和最佳实践。

### 核心特性
- ⏱️ 番茄钟倒计时（支持自定义时长：25/45/60 分钟）
- 🎵 多种白噪音播放（雨声、林涛、海浪、咖啡厅、篝火）
- 🔄 后台运行保活（前台 Service + WorkManager）
- 🎨 自定义倒计时动画 View（圆形进度条、颜色渐变）
- 🔔 通知栏快捷控制（播放/暂停/停止）
- 📊 专注历史记录统计
### 质量要求
- 基础功能完整，逻辑通顺，无明显闪退（Crash）。
- 工作量饱满（Activity/Fragment数量不少于5个，涉及Room数据库操作）。
- 代码规范（命名规范、注释清晰）。
- 适当使用高级特性（如Jetpack组件、MVVM架构、自定义View、NDK等）酌情加分。
- UI/UX设计美观，交互流畅（非原生丑陋界面）。
- 重要：UI定制：应用必须包含一套独特的主题色和LOGO，启动页需展示开发者姓名“王宁皓”，学号“202305100111”。
  
  
### 技术栈

**开发语言**：Kotlin 2.0.21

**架构模式**：MVVM + Repository

**核心依赖库**：

**UI 层**：
- Material Design 3（1.13.0）- 提供现代化 UI 组件
- ConstraintLayout（2.2.1）- 复杂布局支持
- ViewBinding - 类型安全的视图绑定
- RecyclerView - 历史记录列表展示

**数据层**：
- Room（2.6.1）- 本地数据库，存储专注历史
- DataStore - 存储用户偏好设置

**异步处理**：
- Kotlin Coroutines - 协程支持
- Flow - 响应式数据流

**后台服务**：
- Foreground Service - 前台服务保持倒计时运行
- WorkManager - 后台任务调度
- MediaPlayer + AudioFocusRequest - 音频播放管理

**依赖注入**：
- ViewModel + LiveData - MVVM 架构核心
- Fragment + Navigation - 页面导航

**测试**：
- JUnit 4 - 单元测试
- Espresso - UI 自动化测试


## 二、项目配置

### 2.1 基础配置

```kotlin
// build.gradle.kts (Project)
minSdk = 30              // 支持 Android 11+
targetSdk = 36           // 最新 API
compileSdk = 36
jvmTarget = "11"         // Java 11
```

### 2.2 应用信息

- **应用 ID**：`com.wangninghao.a202305100111.endtest01_tomato_focusflow`
- **版本号**：1.0（versionCode: 1）
- **开发者**：王宁皓
- **学号**：202305100111

### 2.3 权限配置

```xml
<!-- AndroidManifest.xml 需添加 -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
```

### 2.4 必须添加的依赖

**需在 `libs.versions.toml` 和 `build.gradle.kts` 中添加：**

```toml
[versions]
room = "2.6.1"
lifecycle = "2.8.7"
navigation = "2.8.5"
workManager = "2.9.1"
datastore = "1.1.1"

[libraries]
# Room 数据库
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

# ViewModel & LiveData
androidx-lifecycle-viewmodel-ktx = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-ktx", version.ref = "lifecycle" }
androidx-lifecycle-livedata-ktx = { group = "androidx.lifecycle", name = "lifecycle-livedata-ktx", version.ref = "lifecycle" }

# Navigation
androidx-navigation-fragment-ktx = { group = "androidx.navigation", name = "navigation-fragment-ktx", version.ref = "navigation" }
androidx-navigation-ui-ktx = { group = "androidx.navigation", name = "navigation-ui-ktx", version.ref = "navigation" }

# WorkManager
androidx-work-runtime-ktx = { group = "androidx.work", name = "work-runtime-ktx", version.ref = "workManager" }

# DataStore
androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }

# ViewBinding
androidx-fragment-ktx = { group = "androidx.fragment", name = "fragment-ktx", version = "1.8.7" }
```



## 三、技术架构设计

### 3.1 整体架构

采用 **MVVM（Model-View-ViewModel）** 架构 + **Repository 模式**：

```
┌─────────────────────────────────────────────────────────┐
│                    UI Layer (View)                      │
│  Activity/Fragment + ViewBinding + CustomView           │
│  - SplashActivity（启动页）                              │
│  - MainActivity（主容器）                                 │
│  - TimerFragment（倒计时页面）                            │
│  - WhiteNoiseFragment（白噪音页面）                       │
│  - HistoryFragment（历史记录）                            │
│  - SettingsFragment（设置页面）                           │
│  - CustomTimerView（自定义圆形进度条）                    │
└─────────────────────────────────────────────────────────┘
                           ↓ ↑ (LiveData/Flow)
┌─────────────────────────────────────────────────────────┐
│                 ViewModel Layer                         │
│  - TimerViewModel（倒计时逻辑）                           │
│  - WhiteNoiseViewModel（音频控制）                        │
│  - HistoryViewModel（历史数据）                           │
└─────────────────────────────────────────────────────────┘
                           ↓ ↑
┌─────────────────────────────────────────────────────────┐
│                 Repository Layer                        │
│  - FocusRecordRepository（数据统一管理）                  │
│  - PreferencesRepository（设置管理）                      │
└─────────────────────────────────────────────────────────┘
                           ↓ ↑
┌─────────────────────────────────────────────────────────┐
│              Data Source Layer (Model)                  │
│  - Room Database（本地数据库）                            │
│  - DataStore（偏好设置）                                  │
│  - MediaPlayer（音频资源）                                │
└─────────────────────────────────────────────────────────┘
                           ↓ ↑
┌─────────────────────────────────────────────────────────┐
│                   Service Layer                         │
│  - TimerService（前台服务，倒计时后台运行）                │
│  - NotificationHelper（通知栏管理）                       │
└─────────────────────────────────────────────────────────┘
```

### 3.2 核心模块划分

#### **📦 1. UI 模块（ui/）**

**Activity（2个）**：
- `SplashActivity`：启动页，展示 Logo + 开发者信息（王宁皓 202305100111）
- `MainActivity`：主容器，使用 BottomNavigationView + NavHostFragment

**Fragment（5个）**：
- `TimerFragment`：倒计时主界面，自定义圆形进度条 + 时长选择（25/45/60分钟）
- `WhiteNoiseFragment`：白噪音列表（雨声、林涛、海浪、咖啡厅、篝火）
- `HistoryFragment`：专注历史记录（RecyclerView + 日期统计）
- `SettingsFragment`：设置页面（通知开关、音量调节、主题切换）
- `AboutFragment`：关于页面（开发者信息、版本号）

**CustomView（1个）**：
- `CircularTimerView`：自定义圆形倒计时进度条（Canvas 绘制 + 颜色渐变动画）

#### **📦 2. ViewModel 模块（viewmodel/）**

- `TimerViewModel`：管理倒计时状态（运行/暂停/停止）、剩余时间、与 Service 通信
- `WhiteNoiseViewModel`：音频播放状态、音量控制、音频切换
- `HistoryViewModel`：从 Repository 获取历史数据、统计总时长

#### **📦 3. Repository 模块（data/repository/）**

- `FocusRecordRepository`：统一管理专注记录的增删查改
- `PreferencesRepository`：管理用户设置（默认时长、音量、主题）

#### **📦 4. 数据库模块（data/database/）**

**Entity**：
```kotlin
@Entity(tableName = "focus_records")
data class FocusRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val duration: Int,           // 设定时长（分钟）
    val actualDuration: Int,     // 实际完成时长（秒）
    val startTime: Long,         // 开始时间戳
    val endTime: Long,           // 结束时间戳
    val isCompleted: Boolean,    // 是否完整完成
    val whiteNoise: String?      // 使用的白噪音类型
)
```

**DAO**：
```kotlin
@Dao
interface FocusRecordDao {
    @Insert suspend fun insert(record: FocusRecord)
    @Query("SELECT * FROM focus_records ORDER BY startTime DESC")
    fun getAllRecords(): Flow<List<FocusRecord>>
    @Query("SELECT SUM(actualDuration) FROM focus_records WHERE isCompleted = 1")
    suspend fun getTotalFocusTime(): Int
}
```

**Database**：
```kotlin
@Database(entities = [FocusRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun focusRecordDao(): FocusRecordDao
}
```

#### **📦 5. Service 模块（service/）**

- `TimerService`：前台服务，后台运行倒计时，使用 CountDownTimer
- `NotificationHelper`：创建通知栏，显示剩余时间 + 播放/暂停/停止按钮

#### **📦 6. 音频模块（audio/）**

- `WhiteNoisePlayer`：封装 MediaPlayer，管理音频播放、循环、音量
- 白噪音资源（res/raw/）：
  - `rain.mp3`（雨声）
  - `forest.mp3`（林涛）
  - `ocean.mp3`（海浪）
  - `cafe.mp3`（咖啡厅）
  - `fire.mp3`（篝火）

#### **📦 7. 工具类模块（utils/）**

- `TimeFormatter`：时间格式化工具（秒 → "25:00" 格式）
- `NotificationChannelHelper`：创建通知渠道（Android 8.0+）


## 四、核心技术实现

### 4.1 自定义倒计时 View（CircularTimerView）

**功能**：圆形进度条 + 时间文本 + 颜色渐变动画

**技术要点**：
- 使用 `Canvas.drawArc()` 绘制圆环进度
- `ValueAnimator` 实现颜色从绿色 → 黄色 → 红色渐变
- 监听触摸事件实现拖拽调整时长

**核心代码结构**：
```kotlin
class CircularTimerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {
    
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    var progress: Float = 1f  // 0-1 进度
        set(value) {
            field = value
            updateColor()
            invalidate()
        }
    
    private fun updateColor() {
        // 根据进度改变颜色：绿 → 黄 → 红
        val color = when {
            progress > 0.5f -> Color.rgb(76, 175, 80)   // 绿色
            progress > 0.2f -> Color.rgb(255, 193, 7)   // 黄色
            else -> Color.rgb(244, 67, 54)              // 红色
        }
        progressPaint.color = color
    }
    
    override fun onDraw(canvas: Canvas) {
        // 绘制底层灰色圆环
        // 绘制进度圆环
        // 绘制中心时间文字
    }
}
```

### 4.2 前台 Service 实现（TimerService）

**功能**：后台保活倒计时，退出应用仍在运行

**技术要点**：
- 使用 `startForeground()` 提升优先级
- `CountDownTimer` 精准倒计时
- 通过 `LocalBroadcastManager` 与 UI 通信
- 倒计时结束播放提示音 + 震动

**核心代码结构**：
```kotlin
class TimerService : Service() {
    
    private var countDownTimer: CountDownTimer? = null
    private var remainingTime: Long = 0
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTimer(intent.getLongExtra(EXTRA_DURATION, 25 * 60 * 1000))
            ACTION_PAUSE -> pauseTimer()
            ACTION_STOP -> stopTimer()
        }
        return START_STICKY
    }
    
    private fun startTimer(duration: Long) {
        // 创建前台通知
        val notification = NotificationHelper.createTimerNotification(this, duration)
        startForeground(NOTIFICATION_ID, notification)
        
        // 启动倒计时
        countDownTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                // 更新通知
                // 广播给 UI
            }
            
            override fun onFinish() {
                // 播放提示音
                // 保存记录到数据库
                // 显示完成通知
            }
        }.start()
    }
}
```

### 4.3 白噪音播放（WhiteNoisePlayer）

**技术要点**：
- `MediaPlayer` 循环播放
- `AudioFocusRequest` 请求音频焦点，避免与其他应用冲突
- 支持音量独立调节

**核心代码结构**：
```kotlin
class WhiteNoisePlayer(private val context: Context) {
    
    private var mediaPlayer: MediaPlayer? = null
    private val audioManager = context.getSystemService(AudioManager::class.java)
    
    fun play(@RawRes audioRes: Int, volume: Float = 0.5f) {
        requestAudioFocus()
        
        mediaPlayer = MediaPlayer.create(context, audioRes).apply {
            isLooping = true
            setVolume(volume, volume)
            start()
        }
    }
    
    private fun requestAudioFocus() {
        val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .build()
        audioManager.requestAudioFocus(request)
    }
}
```

### 4.4 通知栏控制（NotificationHelper）

**功能**：显示倒计时进度 + 快捷按钮（播放/暂停/停止）

**技术要点**：
- 创建 `NotificationChannel`（Android 8.0+）
- 使用 `PendingIntent` 绑定 Service 操作
- 动态更新通知内容

**核心代码结构**：
```kotlin
object NotificationHelper {
    
    fun createTimerNotification(context: Context, remainingTime: Long): Notification {
        val pauseIntent = PendingIntent.getService(
            context, 0,
            Intent(context, TimerService::class.java).setAction(ACTION_PAUSE),
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle("专注中")
            .setContentText("剩余 ${TimeFormatter.format(remainingTime)}")
            .addAction(R.drawable.ic_pause, "暂停", pauseIntent)
            .setOngoing(true)
            .build()
    }
}
```

### 4.5 数据统计（HistoryViewModel）

**功能**：展示每日专注时长、总时长统计、完成率

**技术要点**：
- Room + Flow 实现响应式数据更新
- 按日期分组统计
- RecyclerView + DiffUtil 高效列表更新

**核心代码结构**：
```kotlin
class HistoryViewModel(private val repository: FocusRecordRepository) : ViewModel() {
    
    val allRecords: LiveData<List<FocusRecord>> = repository.getAllRecords().asLiveData()
    
    val totalFocusTime: LiveData<Int> = liveData {
        emit(repository.getTotalFocusTime())
    }
    
    // 按日期分组
    val groupedRecords: LiveData<Map<String, List<FocusRecord>>> = allRecords.map { records ->
        records.groupBy { record ->
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Date(record.startTime))
        }
    }
}
```

# 五、开发实施步骤

### 第一阶段：基础框架搭建（1-2 天）

#### **任务 1.1：配置项目依赖**
- ✅ 在 `libs.versions.toml` 中添加 Room、Navigation、WorkManager 等依赖
- ✅ 在 `build.gradle.kts` 中启用 ViewBinding 和 kapt
- ✅ 添加必要权限到 `AndroidManifest.xml`

#### **任务 1.2：创建项目包结构**
```
com.wangninghao.a202305100111.endtest01_tomato_focusflow/
├── ui/
│   ├── activity/
│   │   ├── SplashActivity.kt
│   │   └── MainActivity.kt
│   ├── fragment/
│   │   ├── TimerFragment.kt
│   │   ├── WhiteNoiseFragment.kt
│   │   ├── HistoryFragment.kt
│   │   ├── SettingsFragment.kt
│   │   └── AboutFragment.kt
│   └── customview/
│       └── CircularTimerView.kt
├── viewmodel/
│   ├── TimerViewModel.kt
│   ├── WhiteNoiseViewModel.kt
│   └── HistoryViewModel.kt
├── data/
│   ├── database/
│   │   ├── AppDatabase.kt
│   │   ├── FocusRecord.kt
│   │   └── FocusRecordDao.kt
│   └── repository/
│       ├── FocusRecordRepository.kt
│       └── PreferencesRepository.kt
├── service/
│   ├── TimerService.kt
│   └── NotificationHelper.kt
├── audio/
│   └── WhiteNoisePlayer.kt
└── utils/
    ├── TimeFormatter.kt
    └── Constants.kt
```

#### **任务 1.3：设计主题色和 Logo**
- 🎨 定义主题色（Primary: #6200EE，Secondary: #03DAC5）
- 🎨 设计应用 Logo（番茄 + 时钟元素）
- 🎨 创建启动页背景（渐变色 + 开发者信息）

#### **任务 1.4：创建 SplashActivity**
- 展示 Logo 和开发者信息（王宁皓 202305100111）
- 延迟 2 秒后跳转到 MainActivity
- 使用主题色渐变背景

#### **任务 1.5：搭建 MainActivity 框架**
- 使用 BottomNavigationView + NavHostFragment
- 创建导航图（nav_graph.xml）
- 连接 5 个 Fragment（Timer、WhiteNoise、History、Settings、About）

---

### 第二阶段：Service 实现（2-3 天）

#### **任务 2.1：实现 TimerService 基础功能**
- ✅ 创建前台服务，使用 `startForeground()`
- ✅ 实现 CountDownTimer 倒计时逻辑
- ✅ 支持 START/PAUSE/RESUME/STOP 四种操作
- ✅ 通过 BroadcastReceiver 与 UI 通信

#### **任务 2.2：实现 NotificationHelper**
- ✅ 创建通知渠道（CHANNEL_ID: "timer_channel"）
- ✅ 显示倒计时进度通知（每秒更新）
- ✅ 添加通知操作按钮（播放/暂停/停止）
- ✅ 倒计时结束显示完成通知 + 震动

#### **任务 2.3：实现 WhiteNoisePlayer**
- ✅ 封装 MediaPlayer 播放逻辑
- ✅ 实现循环播放、音量控制、暂停/恢复
- ✅ 请求音频焦点（AudioFocusRequest）
- ✅ 准备白噪音资源文件（5 种音效，放入 res/raw/）

#### **任务 2.4：Service 生命周期管理**
- ✅ 处理服务被系统杀死后的恢复（START_STICKY）
- ✅ 使用 WakeLock 防止休眠
- ✅ 绑定 WorkManager 实现后台保活

---

### 第三阶段：UI 开发（2-3 天）

#### **任务 3.1：实现 TimerFragment**
- ✅ 集成 CircularTimerView 自定义进度条
- ✅ 添加时长选择按钮（25/45/60 分钟）
- ✅ 添加开始/暂停/停止按钮
- ✅ 监听 Service 广播更新 UI

#### **任务 3.2：实现 CircularTimerView**
- ✅ 使用 Canvas 绘制圆形进度条
- ✅ 根据剩余时间改变颜色（绿 → 黄 → 红）
- ✅ 中心显示剩余时间（"25:00" 格式）
- ✅ 添加触摸拖拽调整时长功能（可选）

#### **任务 3.3：实现 WhiteNoiseFragment**
- ✅ 使用 RecyclerView 展示 5 种白噪音列表
- ✅ 每项显示图标 + 名称 + 播放状态
- ✅ 点击切换播放/暂停
- ✅ 添加音量调节 SeekBar

#### **任务 3.4：实现 HistoryFragment**
- ✅ 使用 RecyclerView 展示历史记录
- ✅ 按日期分组显示（今天、昨天、更早）
- ✅ 顶部显示总专注时长统计卡片
- ✅ 支持删除记录（左滑删除）

#### **任务 3.5：实现 SettingsFragment**
- ✅ 通知开关（SwitchPreference）
- ✅ 默认时长选择（ListPreference）
- ✅ 主题切换（浅色/深色/跟随系统）
- ✅ 清除所有历史记录按钮

#### **任务 3.6：实现 AboutFragment**
- ✅ 显示应用 Logo 和名称
  - 使用 ImageView 展示应用图标
  - TextView 显示 "Focus Flow" 应用名称
- ✅ 显示版本号
  - 通过 PackageManager 获取并显示当前应用版本（如 v1.0）
- ✅ 显示开发者信息
  - 显示开发者姓名：王宁皓
  - 显示学号：202305100111
- ✅ 添加 GitHub 链接（可选）
  - 使用 Linkify 或 ClickableSpan 实现可点击链接
  - 跳转到项目的 GitHub 仓库页面

---

### 第四阶段：数据持久化（1-2 天）

#### **任务 4.1：创建 Room 数据库**
- ✅ 定义 `FocusRecord` Entity（包含时长、时间戳、完成状态等）
- ✅ 创建 `FocusRecordDao`（增删查改 + 统计查询）
- ✅ 创建 `AppDatabase` 单例

#### **任务 4.2：实现 Repository 层**
- ✅ `FocusRecordRepository`：封装数据库操作
- ✅ `PreferencesRepository`：使用 DataStore 存储设置

#### **任务 4.3：实现 ViewModel**
- ✅ `TimerViewModel`：管理倒计时状态、与 Service 通信
- ✅ `HistoryViewModel`：获取历史数据、统计总时长
- ✅ `WhiteNoiseViewModel`：管理音频播放状态

#### **任务 4.4：数据绑定到 UI**
- ✅ 使用 LiveData 观察数据变化
- ✅ 倒计时结束自动保存记录到数据库
- ✅ HistoryFragment 实时显示最新数据

---

### 第五阶段：优化与测试（1-2 天）

#### **任务 5.1：性能优化**
- ✅ 使用 DiffUtil 优化 RecyclerView 刷新
- ✅ 图片资源压缩（WebP 格式）
- ✅ 避免内存泄漏（使用 LeakCanary 检测）
- ✅ Service 退出时释放资源

#### **任务 5.2：UI/UX 优化**
- ✅ 添加页面切换动画
- ✅ 按钮点击涟漪效果
- ✅ 适配深色模式
- ✅ 适配不同屏幕尺寸（使用 ConstraintLayout）

#### **任务 5.3：功能测试**
- ✅ 倒计时在后台运行是否正常
- ✅ 退出应用后通知是否持续显示
- ✅ 白噪音播放是否流畅
- ✅ 数据库增删查改是否正确
- ✅ 横屏切换是否保持状态

#### **任务 5.4：异常处理**
- ✅ 音频文件加载失败提示
- ✅ 数据库操作异常处理
- ✅ 网络权限检查（如需在线音频）
- ✅ 通知权限被拒绝的降级方案

#### **任务 5.5：编写测试用例**
- ✅ 单元测试：TimeFormatter、Repository 逻辑
- ✅ UI 测试：使用 Espresso 测试倒计时流程

#### **任务 5.6：代码规范检查**
- ✅ 添加 KDoc 注释
- ✅ 统一命名规范（驼峰命名、语义化）
- ✅ 移除 TODO 和调试代码
- ✅ 提交前代码格式化（Ctrl+Alt+L）

---

## 六、评分标准对应

| 评分项 | 实现方式 | 预期得分 |
|--------|---------|----------|
| **基础功能完整** | 番茄钟倒计时 + 白噪音 + 历史记录 + 设置 | ✅ |
| **Activity/Fragment 数量** | 2 个 Activity + 5 个 Fragment | ✅ 满足 |
| **数据库操作** | Room 数据库存储专注记录 | ✅ 满足 |
| **代码规范** | KDoc 注释 + 规范命名 + MVVM 架构 | ✅ 满足 |
| **高级特性** | 自定义 View + 前台 Service + WorkManager + MVVM | ✅ 加分项 |
| **UI/UX 设计** | Material Design 3 + 自定义主题 + 动画效果 | ✅ 加分项 |
| **独特主题和 Logo** | 自定义配色 + 原创 Logo | ✅ 满足 |
| **启动页开发者信息** | SplashActivity 展示"王宁皓 202305100111" | ✅ 满足 |

---

## 七、开发注意事项

### 7.1 必须实现的核心功能
1. ✅ **倒计时后台运行**：退出应用仍在倒计时，通知栏显示剩余时间
2. ✅ **白噪音循环播放**：支持 5 种音效切换，独立音量控制
3. ✅ **历史记录持久化**：使用 Room 数据库存储，支持统计总时长
4. ✅ **通知栏控制**：快捷操作按钮（播放/暂停/停止）
5. ✅ **自定义圆形进度条**：颜色随时间变化，Canvas 绘制

### 7.2 可选优化功能
- ⭐ 震动反馈（倒计时结束时）
- ⭐ 统计图表（每周专注时长趋势）
- ⭐ 番茄钟周期提醒（25 分钟工作 + 5 分钟休息）
- ⭐ 小部件（Widget）显示倒计时
- ⭐ 多语言支持（中英文）

### 7.3 常见问题解决

**Q1：Service 被系统杀死怎么办？**
- A：使用 `START_STICKY` + WorkManager 双重保活

**Q2：MediaPlayer 播放卡顿？**
- A：在子线程预加载音频，使用 `prepareAsync()`

**Q3：通知栏按钮点击无效？**
- A：检查 PendingIntent 的 flags 是否设置为 `FLAG_IMMUTABLE`（Android 12+）

**Q4：横屏切换数据丢失？**
- A：使用 ViewModel 保存数据，避免在 Activity 中存储状态

---

## 八、开发时间规划

| 阶段 | 任务 | 预计时间 |
|------|------|----------|
| 第一阶段 | 基础框架搭建 + 主题设计 | 1-2 天 |
| 第二阶段 | Service + 音频播放实现 | 2-3 天 |
| 第三阶段 | UI 开发（6 个页面） | 2-3 天 |
| 第四阶段 | 数据库 + ViewModel 绑定 | 1-2 天 |
| 第五阶段 | 优化测试 + Bug 修复 | 1-2 天 |
| **总计** | | **7-12 天** |

---

## 九、最终交付清单

- ✅ 完整 APK 文件（可安装运行）
- ✅ 源代码（包含完整注释）
- ✅ 开发文档（本 README）
- ✅ 测试报告（功能测试截图）
- ✅ 演示视频（可选）

---

**开发者**：王宁皓  
**学号**：202305100111  
**最后更新**：2025-12-22

