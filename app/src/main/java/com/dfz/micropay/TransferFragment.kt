package com.dfz.micropay

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_transfer.*


class TransferFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transfer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trans_button.setOnClickListener({
            val moneyAmount = money.text.toString()
            if (!moneyAmount.isEmpty()) {
                val intent = Intent(activity, TransferActivity::class.java)
//                Toast.makeText(activity, "please input money$moneyAmount", Toast.LENGTH_SHORT).show()
                intent.putExtra("money", moneyAmount)
                startActivity(intent)
            } else {
                Toast.makeText(activity, "please input money", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = TransferFragment().apply {}
    }
}
