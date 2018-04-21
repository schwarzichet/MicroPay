package com.dfz.micropay

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.dfz.micropay.dummy.DummyContent
import kotlinx.android.synthetic.main.fragment_traderecord.view.*
import kotlinx.android.synthetic.main.fragment_traderecord_list.*

class TradeRecordFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onRefresh() {
//        TODO: add fetch data from server
        Toast.makeText(activity, "refresh", Toast.LENGTH_SHORT).show()
        swiperefresh.isRefreshing = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_traderecord_list, container, false)
        val listView = view.findViewById<RecyclerView>(R.id.list)
        // Set the adapter
        if (listView is RecyclerView) {
            with(listView) {

                layoutManager = LinearLayoutManager(context)
                Log.d(TAG, "about to at recyclerView adapter")
                adapter = MyTradeRecordRecyclerViewAdapter(DummyContent.ITEMS)
            }
        } else {
            Log.d(TAG, "not recycleView")
        }

        val mSwipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swiperefresh)
        mSwipeRefreshLayout.setOnRefreshListener(this)

        return view
    }


    companion object {
        private const val columnCount = 1

        private const val TAG = "TradeRecordFragment"

        private const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance() = TradeRecordFragment().apply {}


        class MyTradeRecordRecyclerViewAdapter(
                private val mValues: List<DummyContent.DummyItem>)
            : RecyclerView.Adapter<MyTradeRecordRecyclerViewAdapter.ViewHolder>() {


            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.fragment_traderecord, parent, false)
                return ViewHolder(view)
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val item = mValues[position]
                holder.mIdView.text = item.id
                holder.mContentView.text = item.content

                with(holder.mView) {
                    tag = item
                }
            }

            override fun getItemCount(): Int = mValues.size

            inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
                val mIdView: TextView = mView.item_number
                val mContentView: TextView = mView.content

                override fun toString(): String {
                    return super.toString() + " '" + mContentView.text + "'"
                }
            }
        }
    }
}
