package com.dfz.micropay

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {
    override fun onDestroy() {
        super.onDestroy()
        TcpClient.instance.stopClient()
    }

    companion object {
        lateinit var username: String
        lateinit var logInPassword: String
        const val NUM_ITEMS = 4

        class MyPageAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

            private val tabTitles = arrayOf("我", "转账", "收款", "查询")

            override fun getPageTitle(position: Int): CharSequence? {
                return tabTitles[position]
            }

            override fun getItem(position: Int): Fragment {
                val args = Bundle()
                args.putString("username", username)
                args.putString("password", logInPassword)
                return when (position) {
                    0 -> {
                        val f = MyFragment()
                        f.arguments = args
                        f
                    }
                    1 -> TransferFragment.newInstance(username)
                    2 -> ReceiveFragment.newInstance(username)
                    3 -> TradeRecordFragment.newInstance(username)
                    else -> {
                        val f = MyFragment()
                        f.arguments = args
                        f
                    }
                }
            }
            override fun getCount(): Int {
                return NUM_ITEMS
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

        username = intent.getStringExtra("username")
        logInPassword = intent.getStringExtra("LogInPassword")

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


}


