# movie-250-cmp

An KMP App with shared UI that just show the Douban top 250 movie list.

A playground to practice KMP releated tech.

## Libraries

- Koin for dependency injection
- Ktor for network
- Coil for image loading
- kermit for logging
- Room for database

## Use Koin in KMP Project

在 KMP 项目中使用 Koin 管理依赖注入，最理想的情况当然是在 common 中声明并统一管理所有 module，并且提供一个共用的初始化入口供 iOS / Android 双端统一使用。

但是某些类的构造函数中可能会有平台相关的参数，这种情况下就需要在各个平台的 module 中进行依赖注入，典型的例子是 database 的初始化。

在 Android 平台上，database 的初始化需要 Context 参数，而在 iOS 平台上则不需要。

为了解决该问题，可以使用如下方案：

1. **数据库的实例化在各自平台内部进行，通过 expect / actual 机制暴露公共方法到 common 层，以便 common 层可以获取到平台相关的 module。**

    ```Kotlin
    // android module provide platform specific module
    // composeApp/androidMain
    actual fun platformModule(): Module {
        return module {
            single { createDatabase(androidContext()) }
        }
    }

    // iOS module provide platform specific module
    // composeApp/iosMain
    actual fun platformModule(): Module {
        return module {
            single { createDatabase() }
        }
    }

    // manage all koin modules in common module
    // composeApp/commonMain
    expect fun platformModule(): Module

    fun sharedModules(): List<Module> = listOf(
        commonModule(),
        platformModule()
    )
    ```

2. **koin 的启动在各自平台的 App 的初始化入口执行，并且在 Android 平台上传入 Context 等平台相关参数。**

    ```Kotlin

    // init koin in Android App
    // composeApp/androidMain
    fun initKoinAndroid(koinAppDeclaration: KoinAppDeclaration) {
        startKoin {
            koinAppDeclaration()
            modules(sharedModules())
        }
    }

    // composeApp/androidMain
    class App : Application() {
        override fun onCreate() {
            super.onCreate()
            initKoinAndroid {
                androidContext(this@App)
            }
        }
    }
    ```

    ```Swift
    // init koin in iOS App

    // composeApp/iosMain
    fun initKoinIos() {
        startKoin {
            modules(sharedModules())
        }
    }

    // ./iosApp
    struct iOSApp: App {
        init() {
            Koin_iosKt.doInitKoinIos()
        }
    }
    ```

以上方案达到了如下目标：

- 在 commonMain 中统一管理所有 koin 管理的 module
- startKoin 在 Android 和 iOS 的应用启动入口处分别初始化，以传入平台相关的参数，如 context.

### further more

既然 iOS 端的 koin 初始化已经放在 swift 项目中，那么在初始化时，当然也可以在这里直接注入使用 swift 实现的组件。

例如，此项目中在 iOS 端使用 swift 实现了一个 IosNetworkHelper 用以监听网络状态。该 Helper 类实现了 NetworkHelper(commonMain) 接口，并且在 koin 初始化时注入了 koin 的管理体系中

```Swift
init() {
        Koin_iosKt.doInitKoinIos(
            appComponent: IosApplicationComponent(
                networkHelper: IosNetworkHelper()
            )
        )
    }
```

```IosApplicationComponent```是一个位于 ```composeApp/iosMain```中的类，使用它将那些 Swift 注入的依赖包装起来统一管理，而后再通过 ```platformModule``` 将该 module 暴露给 ```composeApp/common```.

```Kotlin
actual fun platformModule(): Module {
    return module {
        single { createDatabase() }
        single<NetworkHelper> { get<IosApplicationComponent>().networkHelper }
    }
}
```

至此，便实现了将 swift 的能力提供 share 给共用模块的能力。
