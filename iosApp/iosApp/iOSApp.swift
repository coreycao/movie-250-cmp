import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        Koin_iosKt.doInitKoinIos(
            appComponent: IosApplicationComponent(
                networkHelper: IosNetworkHelper()
            )
        )
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}