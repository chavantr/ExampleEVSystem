package com.mywings.emergencyvehicle.process

import android.os.AsyncTask
import org.json.JSONObject

class UpdateRouteAsync : AsyncTask<JSONObject, Void, String>() {

    private val httpConnectionUtil = HttpConnectionUtil()

    private lateinit var onUpdateRouteListener: OnUpdateRouteListener;

    override fun doInBackground(vararg params: JSONObject?): String {

        return httpConnectionUtil.requestPost("", params[0])

    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        onUpdateRouteListener.onUpdate(result!!)
    }

    fun setOnUpdateRouteListener(onUpdateRouteListener: OnUpdateRouteListener, request: JSONObject) {
        this.onUpdateRouteListener = onUpdateRouteListener
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request)
    }

}