package com.dfz.micropay

import android.graphics.Color
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
import kotlinx.android.synthetic.main.fragment_traderecord.view.*
import kotlinx.android.synthetic.main.fragment_traderecord_list.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import java.net.SocketTimeoutException

class TradeRecordFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var mTcpClient: TcpClient? = null
    private var transferRecord: List<Record> = ArrayList()

    private lateinit var username: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString("username")
        }
    }

    override fun onRefresh() {
        Toast.makeText(activity, "refresh", Toast.LENGTH_SHORT).show()

        if (mTcpClient == null) {
            mTcpClient = TcpClient.instance

        }

        val message = "RECORD\n"
        async(UI) {
            try {
                transferRecord = bg {
                    mTcpClient!!.connect()
                    mTcpClient!!.sendMessage(message)
                    Log.d(TAG, "message sent")

                    val message = mTcpClient!!.read()
                    val responseArray = message!!.split("%")
                    val recordArray: MutableList<Record> = ArrayList()
                    if (responseArray[0] == "RECORD_REPLY") {
                        var i = 1
                        while (i < responseArray.size) {
                            val payer = responseArray[i]
                            val payee = responseArray[i + 1]
                            val time = responseArray[i + 2]
                            val money = responseArray[i + 3]
                            i += 4
                            recordArray.add(Record(payer, payee, time, money))
                        }
                        val finishMessage = mTcpClient!!.read()
                        if (finishMessage == "RECORD_REPLY_FINISHED") {
                            Log.d(TAG, "record finish")
                        } else {
                            Log.e(TAG, "wrong record format$finishMessage")
                        }
                    } else {
                        Log.d(TAG, "wrong record format$responseArray")
                    }
//                    mTcpClient!!.logInResult
                    recordArray
                }.await()
                list.adapter = MyTradeRecordRecyclerViewAdapter(transferRecord, username)

            } catch (e: SocketTimeoutException) {
                Toast.makeText(activity, "Time out", Toast.LENGTH_SHORT).show()
            }

        }
        swiperefresh.isRefreshing = false

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_traderecord_list, container, false)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set the adapter
        if (list is RecyclerView) {
            with(list) {

                layoutManager = LinearLayoutManager(context)
                Log.d(TAG, "about to at recyclerView adapter")
                if (adapter == null) {
                    Log.d(TAG, "adapter is null")
                    adapter = MyTradeRecordRecyclerViewAdapter(transferRecord, username)
                }
            }
        } else {
            Log.d(TAG, "not recycleView")
        }

        swiperefresh.setOnRefreshListener(this)
        onRefresh()
    }

    companion object {
        private const val columnCount = 1

        private const val TAG = "TradeRecordFragment"


        private const val ARG_COLUMN_COUNT = "column-count"


        @JvmStatic
        fun newInstance(name: String) = TradeRecordFragment().apply {
            arguments = Bundle().apply {
                putString("username", name)
            }
        }


        class MyTradeRecordRecyclerViewAdapter(
                private val mValues: List<Record>, private val username:String)
            : RecyclerView.Adapter<MyTradeRecordRecyclerViewAdapter.ViewHolder>() {


            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.fragment_traderecord, parent, false)
                return ViewHolder(view)
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val item = mValues[position]
//                holder.mIdView.text = item.id
//                holder.mContentView.text = item.content
//                holder.mIdView.text = position.toString()
//                holder.mContentView.text = "${item.time} ${item.payer}->${item.payee} ${item.money}"
//                holder.mIdView.text = item.time
//                holder.mContentView.text = "${item.payer}->${item.payee} ${item.money}"
                holder.timeView.text = item.time
                holder.objectView.text = if (item.payer==username) item.payee else item.payer
                holder.moneyView.text = if (item.payer==username) "-${item.money}" else "+${item.money}"
                holder.moneyView.setTextColor(if (item.payer!=username) Color.RED else Color.BLACK)


                with(holder.mView) {
                    tag = item
                }
            }

            override fun getItemCount(): Int = mValues.size

            inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
                val timeView: TextView = mView.time
                val objectView: TextView = mView.`object`
                val moneyView: TextView = mView.money

//                override fun toString(): String {
//                    return super.toString() + " '" + timeView.text + "'"
//                }
            }
        }
    }
}
