package fr.enssat.tpmulticast

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MultiCastAgent(val listener: (String) -> Unit) {

    companion object {
        val PORT = 12345
        val MULTICAST_GROUP = InetAddress.getByName("228.1.2.3")
        var multicastLock: WifiManager.MulticastLock? = null

        fun wifiLock(context: Context) {
            var lock = multicastLock
            if (lock == null) {
                val wifi = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                lock = wifi.createMulticastLock("multicastLock")
                lock.setReferenceCounted(true)
                lock.acquire()
                multicastLock = lock
            }
        }

        fun releaseWifiLock() {
            multicastLock?.release()
            multicastLock = null
        }
    }

    private val TAG = this.javaClass.simpleName
    private val socket = createSocket()
    private val mainThread = object: Executor {
        val handler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            handler.post(command)
        }
    }
    private var loop = true
    private val MAX_SIZE_MESSAGE = 2048

    private fun createSocket(): MulticastSocket {
        val socket = MulticastSocket(PORT)
            socket.joinGroup(MULTICAST_GROUP)
            Log.d(TAG, "create multicast socket:"+ MULTICAST_GROUP + "/" + PORT)
        return socket
    }

    fun startReceiveLoop() {
        Executors.newSingleThreadExecutor().execute {
            while (loop) {
                val data = receive()
                Log.d(TAG, "receiving on multicast: $data")
                mainThread.execute{listener(data)}
            }
        }
    }

        private fun receive(): String {
            val packet = DatagramPacket(ByteArray(MAX_SIZE_MESSAGE), MAX_SIZE_MESSAGE)
            Log.d(TAG, "waiting for multicast datagram")
            socket.receive(packet)
            return String(packet.data, 0, packet.length, StandardCharsets.UTF_8)
        }


    fun stopReceiveLoop() {
        loop = false
        closeSocket()
    }
        private fun closeSocket() {
            Log.d(TAG, "closing multicast socket")
            socket.leaveGroup(MULTICAST_GROUP)
            socket.close()
        }


    fun send(msg: String) {
        Executors.newSingleThreadExecutor().execute {
            val data = msg.toByteArray(StandardCharsets.UTF_8)
            Log.d(TAG, "publishing on multicast: ${String(data)}")
            val packet = DatagramPacket(data, 0, data.size, MULTICAST_GROUP, PORT)
            socket.send(packet)
        }
    }


}