package com.dfz.micropay

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_transfer.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg

class TransferActivity : AppCompatActivity(), NfcAdapter.CreateNdefMessageCallback {

    private var moneyAmount: String? = null
    private var username: String? = null
    private var mTcpClient = TcpClient.instance
    private var password: String? = null

    private var mNfcAdapter: NfcAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)


        username = intent.getStringExtra("username")
        moneyAmount = intent.getStringExtra("money")
        password = intent.getStringExtra("payerPassword")

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show()
            finish()
            return
        } else {
            Toast.makeText(this, "NFC is available", Toast.LENGTH_LONG).show()
        }
        mNfcAdapter!!.setNdefPushMessageCallback(this, this)
        transferTextView.text = "Transfering..."
        async(UI){
            bg{
                val response = mTcpClient.read()
                val responseArray = response!!.split("%")
                if (responseArray[0]=="EXPENSE"){
                    Toast.makeText(this@TransferActivity, "Transfer Success", Toast.LENGTH_LONG ).show()
                    transferTextView.text = "Transfer Success"
                }
            }
        }
    }

    override fun createNdefMessage(p0: NfcEvent?): NdefMessage {
        val text = "$username%$password%$moneyAmount"
        return NdefMessage(arrayOf(
                NdefRecord.createMime("pay/vnd.micropay.dfz", text.toByteArray())
        ))
    }


}
