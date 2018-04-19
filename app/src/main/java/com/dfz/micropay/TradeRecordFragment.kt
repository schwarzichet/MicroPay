package com.dfz.micropay

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.dfz.micropay.dummy.DummyContent
import kotlinx.android.synthetic.main.fragment_traderecord_list.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [TradeRecordFragment.OnListFragmentInteractionListener] interface.
 */
class TradeRecordFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {


    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null

    private val TAG = "TradeRecordFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            //            columnCount = it.getInt(ARG_COLUMN_COUNT)
            columnCount = 1
        }
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
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                Log.d(TAG, "about to at recyclerView adapter")
                adapter = MyTradeRecordRecyclerViewAdapter(DummyContent.ITEMS, listener)
            }
        } else {
            Log.d(TAG, "not recycleView")
        }

        val mSwipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swiperefresh)
        mSwipeRefreshLayout.setOnRefreshListener(this)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: DummyContent.DummyItem?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                TradeRecordFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
