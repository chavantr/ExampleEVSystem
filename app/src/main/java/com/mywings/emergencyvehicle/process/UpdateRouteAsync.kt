package com.mywings.emergencyvehicle.process

import android.os.AsyncTask
import org.json.JSONObject

class UpdateRouteAsync : AsyncTask<JSONObject, Void, String>() {

    private val httpConnectionUtil = HttpConnectionUtil()

    private lateinit var onUpdateRouteListener: OnUpdateRouteListener;

    override fun doInBackground(vararg params: JSONObject?): String {
        return httpConnectionUtil.requestGet(
            EmerConstants.URL + EmerConstants.UPDATE_DIRECTION + "?direction=${params[0]!!.getInt(
                "direction"
            )}&id=${params[0]!!.getInt("id")}"
        );
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        onUpdateRouteListener.onUpdateRoute(result!!)
    }

    fun setOnUpdateRouteListener(onUpdateRouteListener: OnUpdateRouteListener, request: JSONObject) {
        this.onUpdateRouteListener = onUpdateRouteListener
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request)
    }

}