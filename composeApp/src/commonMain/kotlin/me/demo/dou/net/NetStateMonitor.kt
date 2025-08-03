package me.demo.dou.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn

/**
 * @author Yeung
 * @date 2025/8/3
 */

interface NetworkHelper {
    fun registerListener(onNetworkAvailable: () -> Unit, onNetworkLost: () -> Unit)
    fun unregisterListener()
}

class NetStateMonitor(private val helper: NetworkHelper) {
    val networkStatus: Flow<NetworkStatus> = callbackFlow {
        helper.registerListener(
            onNetworkAvailable = {
                trySend(NetworkStatus.Connected)
            },
            onNetworkLost = {
                trySend(NetworkStatus.Disconnected)
            }
        )

        awaitClose {
            helper.unregisterListener()
        }
    }.distinctUntilChanged().flowOn(Dispatchers.IO)
}

sealed class NetworkStatus {
    data object Connected : NetworkStatus()
    data object Disconnected : NetworkStatus()
}
