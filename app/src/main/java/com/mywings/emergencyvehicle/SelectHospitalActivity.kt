package com.mywings.emergencyvehicle

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.mywings.emergencyvehicle.joint.HospitalAdapter
import com.mywings.emergencyvehicle.joint.OnHospitalSelectListener
import com.mywings.emergencyvehicle.models.Hospital
import com.mywings.emergencyvehicle.models.UserInfoHolder
import com.mywings.emergencyvehicle.process.GetHospitalAsync
import com.mywings.emergencyvehicle.process.OnHospitalListener
import com.mywings.emergencyvehicle.process.ProgressDialogUtil
import kotlinx.android.synthetic.main.activity_select_hospital.*

class SelectHospitalActivity : AppCompatActivity(), OnHospitalSelectListener, OnHospitalListener {

    private lateinit var hospitalAdapter: HospitalAdapter

    private lateinit var progressDialogUtil: ProgressDialogUtil


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_hospital)
        progressDialogUtil = ProgressDialogUtil(this)
        lstHospitals.layoutManager = LinearLayoutManager(this)
        init()
    }


    private fun init() {
        progressDialogUtil.show()
        val getHospitalAsync = GetHospitalAsync()
        getHospitalAsync.setOnHospitalListener(this)
    }

    override fun onHospitalSuccess(result: List<Hospital>?) {
        progressDialogUtil.hide()
        if (result!!.isNotEmpty()) {
            hospitalAdapter = HospitalAdapter(result)
            hospitalAdapter.setOnHospitalListener(this)
            lstHospitals.adapter = hospitalAdapter
        }
    }

    override fun onHospitalSelected(selected: Hospital?) {
        UserInfoHolder.getInstance().hospital = selected
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }
}
