package com.dfz.micropay

import android.util.Log
import java.io.*
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class TcpClient private constructor() {

    companion object {
        const val SERVER_IP = "47.100.162.109" //server IP address
        const val SERVER_PORT = 8186
        private const val TAG = "TcpClient"
        // message to send to the server
        private var mServerMessage: String? = null
        // sends message received notifications

        val instance: TcpClient by lazy { TcpClient() }

    }


    var connected: Boolean = false
    lateinit var socket: Socket
    //    var mMessageListener: OnMessageReceived? = null
    // while this is true, the server will continue running
    private var mRun = false
    // used to send messages
    private var mBufferOut: PrintWriter? = null
    // used to read messages from the server
    private var mBufferIn: BufferedReader? = null


    fun sendMessage(message: String) {
        if (mBufferOut != null) {
            Log.d(TAG, "Sending: $message")
            mBufferOut!!.println(message)
            mBufferOut!!.flush()
        } else {
            Log.d(TAG, "BufferOut is null")
        }
    }

    fun stopClient() {
        mRun = false
        if (mBufferOut != null) {
            mBufferOut!!.flush()
            mBufferOut!!.close()
        }
//        mMessageListener = null
        mBufferIn = null
        mBufferOut = null
        mServerMessage = null
        connected = false
    }

    interface OnMessageReceived {
        fun messageReceived(message: String)
    }


    fun read(): String? {
        while (mRun) {
            mServerMessage = mBufferIn!!.readLine()
//            if (mServerMessage != null && mMessageListener != null) {
////                mMessageListener!!.messageReceived(mServerMessage!!)
////                break
//            }
            if (mServerMessage != null) {
                return mServerMessage
            }
        }
        return null
    }

    fun connect() {

        mRun = true

        //here you must put your computer's IP address.
        val serverAddr = InetAddress.getByName(SERVER_IP)
        //create a socket to make the connection with the server
        socket = Socket()
        if (!connected) {
            socket.connect(InetSocketAddress(serverAddr, SERVER_PORT), 1000)
            //sends the message to the server
            mBufferOut = PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true)
            //receives the message which the server sends back
            mBufferIn = BufferedReader(InputStreamReader(socket.getInputStream()))
            connected = true
            Log.d(TAG, "TCP Client" + "C: Connected")

        }
    }


}