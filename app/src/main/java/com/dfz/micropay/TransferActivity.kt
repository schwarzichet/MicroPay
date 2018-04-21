package com.dfz.micropay

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_transfer.*

class TransferActivity : AppCompatActivity(), NfcAdapter.CreateNdefMessageCallback {

    private  var moneyAmount:String = "2333"

    lateinit var mNfcAdapter: NfcAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        if (intent==null){
            Toast.makeText(this, "fuck you", Toast.LENGTH_SHORT).show()
        }else{
            moneyAmount = intent.getStringExtra("money")
        }

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show()
            finish()
            return
        } else {
            Toast.makeText(this, "NFC is available", Toast.LENGTH_LONG).show()
        }
//        Thread.
        mNfcAdapter.setNdefPushMessageCallback(this, this)




    }

    override fun createNdefMessage(p0: NfcEvent?): NdefMessage {
//        val text = ("Beam me up, Android\n\n" + "Beam Time: " + System.currentTimeMillis())
        val text = moneyAmount
        return NdefMessage(arrayOf(
                NdefRecord.createMime("pay/vnd.micropay.dfz", text.toByteArray())
        ))
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
        textView1.text = String(msg.records[0].payload)
    }


    public override fun onNewIntent(intent: Intent) {
        setIntent(intent)
    }
}
