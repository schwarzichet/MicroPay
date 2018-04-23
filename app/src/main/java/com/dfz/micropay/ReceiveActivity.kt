package com.dfz.micropay

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_receive.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import java.net.SocketTimeoutException

class ReceiveActivity : AppCompatActivity() {

    private var mTcpClient: TcpClient? = null
    private var username: String? = null


    companion object {
        const val TAG = "ReceiveActivity"
    }

    private lateinit var mNfcAdapter: NfcAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive)
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show()
            finish()
            return
        } else {
            Toast.makeText(this, "NFC is available", Toast.LENGTH_LONG).show()
        }
        mNfcAdapter.setNdefPushMessage(null, this)

//        showProgress(false)
        Log.d(TAG, "textview is ${receive_textView.text}")

        username = intent.getStringExtra("username")
    }


    override fun onResume() {
        super.onResume()
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            processIntent(intent)
        }
    }

    private fun processIntent(intent: Intent?) {
        val rawMsgs = intent?.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        val msg = rawMsgs?.get(0) as NdefMessage
        receive_textView.text = "waiting..."
        Log.d(TAG, "textview is ${receive_textView.text}")
        val seq = String(msg.records[0].payload).splitToSequence("%")
        val payer = seq.first()
        val payerPassword = seq.elementAt(1)
        val money = seq.last()

        sendServerMessage(payer, payerPassword, money)
    }

    private fun sendServerMessage(payer: String, payerPassword: String, money: String) {
        showProgress(true)

        if (mTcpClient == null) {
//            mTcpClient = TcpClient(object : TcpClient.OnMessageReceived {
//                override fun messageReceived(message: String) {
//                    Log.d(TAG, "receive tcp data $message ")
//                    //                if (result) {
////                    Toast.makeText(this@ReceiveActivity, "transfer success", Toast.LENGTH_SHORT).show()
////
////                } else {
////                    Toast.makeText(this@ReceiveActivity, "transfer fail", Toast.LENGTH_SHORT).show()
////                }
//                }
//            })
            mTcpClient = TcpClient.instance
        }

        val transferMessage = "TRANSFER%$payer%$payerPassword%$username%$money\n"
        async(UI) {
            try {
                val response = bg {
                    mTcpClient!!.connect()
                    mTcpClient!!.sendMessage(transferMessage)
                    Log.d(TAG, "message sent")
                    val response = mTcpClient!!.read()

                    response
//                    mTcpClient!!.logInResult
                }.await()
                Log.d(TAG, "transfer response: [$response]")
                when (response) {
                    "TRANSFER_ERROR%PASSWORD_INCORRECT" -> {
                        Toast.makeText(this@ReceiveActivity, "password incorrect", Toast.LENGTH_SHORT).show()
                        receive_textView.text = "wrong password"
                    }
                    "TRANSFER_SUCCESS" -> {
                        mTcpClient!!.read()
                        Toast.makeText(this@ReceiveActivity, "success", Toast.LENGTH_SHORT).show()
                        receive_textView.text = "get ${username} from ${payer}"
                    }

                    else -> {
                        Toast.makeText(this@ReceiveActivity, "strange error", Toast.LENGTH_SHORT).show()
                        receive_textView.text = "strange error"
                    }
                }


            } catch (e: SocketTimeoutException) {
                Toast.makeText(this@ReceiveActivity, "Time out", Toast.LENGTH_SHORT).show()
            }

        }
        showProgress(false)

    }


    private fun showProgress(show: Boolean) {

        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)
        mReceiveProgressBar.visibility = (if (show) View.VISIBLE else View.GONE)
        mReceiveProgressBar.animate().setDuration(shortAnimTime.toLong()).alpha(
                (if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mReceiveProgressBar.visibility = if (show) View.VISIBLE else View.GONE
            }
        })
    }


    public override fun onNewIntent(intent: Intent) {
        setIntent(intent)
    }
}
