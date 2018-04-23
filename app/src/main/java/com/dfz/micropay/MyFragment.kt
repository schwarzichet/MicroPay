package com.dfz.micropay


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_my.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import java.net.SocketTimeoutException


/**
 * A simple [Fragment] subclass.
 *
 */
class MyFragment : Fragment() {
    //    private var mNum: Int = 0
    private var username = ""
    private var password = ""
    private var mTcpClient: TcpClient? = null


    companion object {
        private const val TAG = "MyFragment"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        username = arguments!!.getString("username")
        password = arguments!!.getString("password")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)


        return inflater.inflate(R.layout.fragment_my, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usernameTextView.text = username
        updateBalance()

    }

    private fun updateBalance() {
        var balanceValue: String?
        if (mTcpClient == null) {
//            mTcpClient = TcpClient(object : TcpClient.OnMessageReceived {
//                override fun messageReceived(message: String) {
//                    Log.d(TAG, "receive tcp data [$message] ")
//                    balanceValue = message
//                }
//            })
            mTcpClient = TcpClient.instance
        }

        val message = "BALANCE\n"

        async(UI) {
            try {
                balanceValueTextView.text = bg {
                    mTcpClient!!.connect()
                    mTcpClient!!.sendMessage(message)
                    Log.d(TAG, "message sent")
                    var response = mTcpClient!!.read()
                    val balance = response!!.removePrefix("BALANCE_REPLY%").removeSuffix("\n")

                    Log.d(TAG, "balance response1: $response")
                    response = mTcpClient!!.read()
                    Log.d(TAG, "balance response2: $response")
                    balance

                }.await()

            } catch (e: SocketTimeoutException) {
                Toast.makeText(activity, "Time out", Toast.LENGTH_SHORT).show()
            }
//            mTcpClient!!.stopClient()
        }
    }
}