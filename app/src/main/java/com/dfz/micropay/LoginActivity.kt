package com.dfz.micropay

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import java.net.SocketTimeoutException

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    private var mTcpClient: TcpClient? = null
    private var logInSuccess = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        logInButton.setOnClickListener { attemptLogin() }
        showProgress(false)

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        val username = usernameTextView.text.toString()
        val password = passwordTextView.text.toString()
        showProgress(true)


        if (mTcpClient == null) {
//            mTcpClient = TcpClient(object : TcpClient.OnMessageReceived {
//                override fun messageReceived(message: String) {
//                    Log.d(TAG, "receive tcp data [$message] ")
//
//                    if (message == "LOGIN_ON") {
//                        Log.d(TAG, "log in success")
//                        logInSuccess = true
//                    }
//
//
//                }
//
//
//            })
            mTcpClient = TcpClient.instance
        }

        val message = "LOGIN%$username%$password\n"

        async(UI) {
            try {
                bg {
                    mTcpClient!!.connect()
                    mTcpClient!!.sendMessage(message)
                    Log.d(TAG, "message sent")
                    val response = mTcpClient!!.read()
                    if (response == "LOGIN_ON") {
                        Log.d(TAG, "log in success")
                        logInSuccess = true
                    }else{
                        Log.d(TAG, "log in error $response")

                    }
//                    mTcpClient!!.logInResult
                }.await()
                if (logInSuccess) {
                    Toast.makeText(this@LoginActivity, "log in success", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("username", username)
                    intent.putExtra("LogInPassword", password)
                    startActivity(intent)
                } else {
                    mPasswordLayout.error = "wrong password"
                }


            } catch (e: SocketTimeoutException) {
                Toast.makeText(this@LoginActivity, "Time out", Toast.LENGTH_SHORT).show()
            }
//            mTcpClient!!.stopClient()
            showProgress(false)
        }
    }


    private fun showProgress(show: Boolean) {
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)
        login_progress.visibility = (if (show) View.VISIBLE else View.GONE)
        login_progress.animate().setDuration(shortAnimTime.toLong()).alpha(
                (if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                login_progress.visibility = if (show) View.VISIBLE else View.GONE
            }
        })
    }


    companion object {
        private const val TAG = "LoginActivity"
    }
}
