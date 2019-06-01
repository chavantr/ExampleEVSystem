package com.mywings.emergencyvehicle.process

import android.os.AsyncTask
import org.json.JSONObject

class LoginAsync : AsyncTask<JSONObject, Void, String?>() {

    private val httpConnectionUtil = HttpConnectionUtil()
    private lateinit var onLoginListener: OnLoginListener

    override fun doInBackground(vararg param: JSONObject?): String? {
        return httpConnectionUtil.requestPost(EmerConstants.URL + EmerConstants.LOGIN, param[0])
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        onLoginListener.onLoginSuccess(result)
    }

    fun setOnLoginListener(onLoginListener: OnLoginListener, request: JSONObject) {
        this.onLoginListener = onLoginListener
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request)
    }


}