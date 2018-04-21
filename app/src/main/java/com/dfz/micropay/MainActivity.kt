package com.dfz.micropay

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_test.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val NUM_ITEMS = 4

        class MyPageAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
            private val tabTitles = arrayOf("我", "转账", "收款", "查询")

            override fun getPageTitle(position: Int): CharSequence? {
                return tabTitles[position]
            }

            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> LogInFragment.newInstance()
                    1 -> TransferFragment.newInstance()
                    2 -> ReceiveFragment.newInstance()
                    3 -> TradeRecordFragment.newInstance()
                    else -> {
                        val f = MyFragment()
                        val args = Bundle()
                        args.putInt("num", position)
                        f.arguments = args
                        f
                    }
                }
            }

            override fun getCount(): Int {
                return NUM_ITEMS
            }
        }

        class MyFragment : Fragment() {
            private var mNum: Int = 0


            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

                mNum = if (arguments != null) arguments!!.getInt("num") else 1


            }

            override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
                super.onCreateView(inflater, container, savedInstanceState)


                return inflater.inflate(R.layout.fragment_test, container, false)
            }

            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)
                button.setOnClickListener {
                    val intent = Intent(activity, ReceiveActivity::class.java)
                    startActivity(intent)
                }
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


