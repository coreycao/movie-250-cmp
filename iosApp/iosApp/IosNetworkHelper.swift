//
//  NetworkMonitor.swift
//  iosApp
//
//  Created by Yeung Cho on 2025/8/3.
//

import Foundation
import Network
import ComposeApp

class IosNetworkHelper : NetworkHelper {
    private let monitor: NWPathMonitor = NWPathMonitor()

    func registerListener(onNetworkAvailable: @escaping () -> Void, onNetworkLost: @escaping () -> Void) {
        monitor.pathUpdateHandler = { path in
            if path.status == .satisfied {
                onNetworkAvailable()
            } else {
                onNetworkLost()
            }
        }
        monitor.start(queue: DispatchQueue.global(qos: .background))
    }

    func unregisterListener() {
        monitor.cancel()
    }
}
