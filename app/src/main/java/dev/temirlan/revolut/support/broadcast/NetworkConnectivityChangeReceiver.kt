package dev.temirlan.revolut.support.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/6/20.
 */
internal class NetworkConnectivityChangeReceiver(
    private val isConnectedListener: (Boolean) -> Unit
) : BroadcastReceiver() {

    companion object {
        private var isPreviousStateConnected: Boolean? = null
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            val isConnected = activeNetwork != null

            val isFirstReceiveDisconnected = isPreviousStateConnected == null && !isConnected
            val isStateChanged = isPreviousStateConnected != isConnected
            if (isFirstReceiveDisconnected || isStateChanged) {
                isConnectedListener(isConnected)
            }

            isPreviousStateConnected = isConnected
        }
    }
}