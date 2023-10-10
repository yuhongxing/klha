package com.czsy.ui.sqg.order

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.czsy.CZBackTask
import com.czsy.CZNetUtils
import com.czsy.Constant
import com.czsy.android.R
import com.czsy.bean.Address2Bean
import com.czsy.bean.ClientBean
import com.czsy.bean.IDNameBean
import com.czsy.ui.AbsPVNFC
import com.czsy.ui.AddressDialog
import com.czsy.ui.AddressDialog.OnAddressOK
import com.czsy.ui.MainActivity
import com.google.gson.Gson
import mylib.app.BackTask
import mylib.utils.CameraUtils
import mylib.utils.FileUtils
import mylib.utils.ImageUtils
import mylib.utils.Utils
import org.json.JSONObject

class NewClient(a: MainActivity) : AbsPVNFC(a), View.OnClickListener {

    var btn_reset: Button? = null
    var btn_ok: Button? = null
    var iv_add: ImageView? = null
    var iv_back: ImageView? = null
    var et_name: EditText? = null
    var et_phone: EditText? = null
    var et_mingcheng: EditText? = null
    var et_address: TextView? = null
    var sp_type: Spinner? = null
    var sp_type1: Spinner? = null
    var spinner_jiedao: Spinner? = null
    var mingcheng_container: RelativeLayout? = null
    var type_container: RelativeLayout? = null
    var pic_container: RelativeLayout? = null
    private var cb_qianfei: CheckBox? = null

    private var phone: String = ""
    private var name1: String = ""

    private var new_client_address: Address2Bean? = null

    var jieDaoIdLists = mutableListOf<IDNameBean>()     // 街道id

    var bmp: Bitmap? = null
    private var pending_pic_file: String? = null

    constructor(a: MainActivity, phone: String, name: String) : this(a) {
        this.phone = phone
        this.name1 = name
    }


    override fun createMainView(ctx: Context?) {
        mMainView = View.inflate(act, R.layout.new_client, null)
        CameraUtils.initPhotoError()

        mingcheng_container = mMainView.findViewById(R.id.mingcheng_container)
        type_container = mMainView.findViewById(R.id.type_container)
        pic_container = mMainView.findViewById(R.id.pic_container)
        et_name = mMainView.findViewById(R.id.et_name)
        et_mingcheng = mMainView.findViewById(R.id.et_mingcheng)
        iv_add = mMainView.findViewById(R.id.iv_add)
        et_phone = mMainView.findViewById(R.id.et_phone)
        et_address = mMainView.findViewById(R.id.et_address)
        sp_type = mMainView.findViewById(R.id.sp_type)
        sp_type1 = mMainView.findViewById(R.id.sp_type1)
        spinner_jiedao = mMainView.findViewById(R.id.spinner_jiedao)
        cb_qianfei = mMainView.findViewById(R.id.cb_qianfei)
        iv_back = mMainView.findViewById(R.id.iv_back)
        btn_reset = mMainView.findViewById(R.id.btn_reset)
        btn_ok = mMainView.findViewById(R.id.btn_ok)
        btn_ok?.setOnClickListener(this)
        btn_reset?.setOnClickListener(this)
        iv_back?.setOnClickListener(this)
        et_address?.setOnClickListener(this)
        iv_add?.setOnClickListener(this)
        //商业用户
        sp_type?.setAdapter(ArrayAdapter(ctx!!, R.layout.gp_id_item,
                ClientBean.clientTypes.toTypedArray()))
        sp_type?.setSelection(2)
        sp_type?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val typeId = ClientBean.clientTypes.get(position).id
                if (typeId.toInt() == 1 || typeId.toInt() == 2) {
                    mingcheng_container?.visibility = View.VISIBLE
                    pic_container?.visibility = View.VISIBLE
                } else {
                    mingcheng_container?.visibility = View.GONE
                    pic_container?.visibility = View.GONE
                }

                if (typeId.toInt() == 2) {
                    type_container?.visibility = View.VISIBLE
                } else {
                    type_container?.visibility = View.GONE
                }
            }

        }

        //商业用户--类型
        sp_type1?.setAdapter(ArrayAdapter(ctx!!, R.layout.gp_id_item,
                ClientBean.clientShangYeTypes.toTypedArray()))

        et_phone?.setText(phone)
        et_name?.setText(name1)

    }

    private fun updateClientInfo() {
        et_phone!!.setText(null)
        et_name!!.setText(null)
        et_address!!.text = null
        et_mingcheng?.text = null
        iv_add?.setImageResource(android.R.drawable.ic_input_add)
        pending_pic_file = null
        bmp = null
        new_client_address = null
    }

    override fun onClick(v: View?) {
        when (v) {
            et_address -> {
                val ad: AddressDialog? = AddressDialog(act, null,
                        OnAddressOK { ad ->
                            et_address!!.text = ad.addr.toString()
                            new_client_address = ad.addr
                            val dizhi = ad.addr.toString()

                        })
                ad?.show()
            }
            iv_back -> {
                act.main_pvc.pop()
            }
            btn_reset -> updateClientInfo()
            btn_ok -> {
                subClient()
            }
            iv_add -> {
                pending_pic_file = FileUtils.getDir(FileUtils.DirType.pic) + System.currentTimeMillis() + ".jpg"
                CameraUtils.doCamera(act, pending_pic_file)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {

        val file = CameraUtils.getPicutre(data, requestCode, resultCode, act, pending_pic_file)
        if (ImageUtils.isImage(file)) {
            val _bmp = ImageUtils.getFileBmp(file)
            if (_bmp != null) {
                bmp = _bmp
                iv_add?.setImageBitmap(_bmp)
            }
            return true
        } else {
            return super.onActivityResult(requestCode, resultCode, data)

        }
    }

    fun subClient() {
        act.showProgress()

        val phone = et_phone!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(phone) || phone.length < 6) {
            Utils.toastLONG("请填写电话")
            act.hideProgress()
            return
        }

        val name = et_name!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(name)) {
            Utils.toastLONG("请填写姓名")
            act.hideProgress()
            return
        }

        if (new_client_address == null) {
            Utils.toastLONG("请填写地址")
            act.hideProgress()
            return
        }

        val mingcheng = et_mingcheng!!.text.toString().trim { it <= ' ' }
        var isHasPic = false
        val jiedaoId = spinner_jiedao!!.selectedItem as IDNameBean
        val shangYeType = sp_type1!!.selectedItem as IDNameBean
        val type = sp_type!!.selectedItem as IDNameBean
        if (type.id.toInt() == 1 || type.id.toInt() == 2) {
            isHasPic = true
        }
        if (isHasPic) {
            if (TextUtils.isEmpty(mingcheng)) {
                Utils.toastSHORT("请填写单位名称")
                act.hideProgress()
                return
            }
            if (bmp == null) {
                Utils.toastSHORT("请添加门头照片")
                act.hideProgress()
                return
            }
        }

        BackTask.post(object : CZBackTask(act) {
            @Throws(Exception::class)
            override fun parseResult(jdata: JSONObject) {

            }

            @Throws(Exception::class)
            override fun getInputParam(): String {
                var bmp_url: String = ""
                if (isHasPic) {
                    Utils.toastLONG("正在提交请稍后...")
                    bmp_url = CZNetUtils.upload(bmp)

                }

                val j = JSONObject()
                j.put("shengFen", new_client_address!!.provinceName)
                j.put("quXian", new_client_address!!.countyName)
                j.put("cun", new_client_address!!.villageName)
                j.put("xiangZhen", new_client_address!!.townName)
                j.put("diShi", new_client_address!!.cityName)
                j.put("diZhi", new_client_address!!.detailAddress)

                j.put("keHuXingMing", name)
                j.put("telNum", phone)
                j.put("leiXing", type.id)
                j.put("jieDaoId", jiedaoId.id)

                if (type.id.toInt() == 2) {//商业用户
                    j.put("shangYeLeiXing", shangYeType.id)
                }

                if (isHasPic) {//需要门头照
                    j.put("picture", bmp_url)
                    j.put("menDianMingCheng", mingcheng)
                }

                return j.toString()
            }

            override fun getURL(): String {
                return "keHu/peiSongYuanChuangJianKeHu"
            }

            override fun runFront2() {
                act.hideProgress()
                Utils.toastLONG("新建用户成功！")
                act.main_pvc.pop()

            }
        })
    }

    override fun onAttach(firstShow: Boolean) {
        super.onAttach(firstShow)

        BackTask.post(object : CZBackTask(act) {
            override fun getURL(): String {

                return "keHu/queryAllStreet"
            }

            override fun runFront2() {

                spinner_jiedao?.setAdapter(ArrayAdapter(act, R.layout.gp_id_item, jieDaoIdLists))

            }

            override fun getInputParam(): String {
                return ""
            }

            override fun parseResult(jdata: JSONObject?) {

                var result = jdata?.getJSONArray("result")
//                    Log.d("xing",""+result?.length())
                if (result?.length()!! > 0) {

                    for (i in 1..result.length()) {
                        var bean = Constant.gson.fromJson(result[i-1].toString(), IDNameBean::class.java)
                        jieDaoIdLists.add(bean)
                    }

                }
            }

        })

    }
}