package com.dfz.micropay


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_receive.*


/**
 * A simple [Fragment] subclass.
 * Use the [ReceiveFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ReceiveFragment : Fragment() {
    private var username: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString("username")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_receive, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        receive_button.setOnClickListener {
            val intent = Intent(activity, ReceiveActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(name: String) = ReceiveFragment().apply {
            arguments = Bundle().apply {
                putString("username", name)
            }
        }
    }
}
