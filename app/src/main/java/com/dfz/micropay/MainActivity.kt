package com.dfz.micropay

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord.createMime
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.dfz.micropay.dummy.DummyContent
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity(), NfcAdapter.CreateNdefMessageCallback, TradeRecordFragment.OnListFragmentInteractionListener {
    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    lateinit var mNfcAdapter: NfcAdapter
    val MESSAGE_SENT = 1

    companion object {
        const val NUM_ITEMS = 3

        class MyPageAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
            private val tabTitles = arrayOf("我", "转账", "查询")

            override fun getPageTitle(position: Int): CharSequence? {
                return tabTitles[position]
            }

            override fun getItem(position: Int): Fragment {
                return MyFragment.newInstance(position)
            }

            override fun getCount(): Int {
                return NUM_ITEMS
            }
        }

        class MyFragment : Fragment() {
            private var mNum: Int = 0

            companion object {
                fun newInstance(num: Int): Fragment {
                    return when (num) {
                        0 -> LogInFragment.newInstance("test1", "test2")
                        2 -> TradeRecordFragment.newInstance(1)
                        else -> {
                            val f = MyFragment()
                            val args = Bundle()
                            args.putInt("num", num)
                            f.arguments = args
                            f
                        }
                    }

                }
            }

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                mNum = if (arguments != null) arguments!!.getInt("num") else 1
            }

            override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//                return super.onCreateView(inflater, container, savedInstanceState)
                val v = inflater.inflate(R.layout.fragment_test, container, false)
                val tv = v.findViewById<TextView>(R.id.testText)
                tv.text = "Fragment #$mNum"
                return v
            }

        }
    }

    private lateinit var mPageAdapter: MyPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mPageAdapter = MyPageAdapter(supportFragmentManager)

        pager.adapter = mPageAdapter

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show()
            finish()
            return
        } else {
            Toast.makeText(this, "NFC is available", Toast.LENGTH_LONG).show()
        }
        mNfcAdapter.setNdefPushMessageCallback(this, this)
        fab.setOnClickListener { view ->
            //            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//            mNfcAdapter.setNdefPushMessageCallback(this, this)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun createNdefMessage(p0: NfcEvent?): NdefMessage {
        val text = ("Beam me up, Android\n\n" + "Beam Time: " + System.currentTimeMillis())

        return NdefMessage(arrayOf(
                createMime("application/vnd.micropay.dfz", text.toByteArray())
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
//        textView1.text = String(msg.records[0].payload)

    }


}


