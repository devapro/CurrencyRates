package pro.devapp.network

import java.net.InetAddress
import java.net.UnknownHostException

class ConnectionStateUtil {
    fun isInternetAvailable(): Boolean {
        try {
            val address: InetAddress = InetAddress.getByName("www.google.com")
            return address.toString() != ""
        } catch (e: UnknownHostException) {
            // Log error
        }
        return false
    }
}