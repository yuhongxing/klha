package com.czsy.ui.sqg

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import com.czsy.CZBackTask
import com.czsy.Constant
import com.czsy.ReturnRunable
import com.czsy.android.R
import com.czsy.android.databinding.PvSqgQitaBinding
import com.czsy.bean.CommonOrderBean
import com.czsy.bean.CommonOrderBean.GangPingInfo
import com.czsy.bean.GangPingBean
import com.czsy.bean.LoginUser
import com.czsy.bean.WeiXiuBean
import com.czsy.ui.AbsPVBase
import com.czsy.ui.AbsPVScanEmpty
import com.czsy.ui.MainActivity
import com.czsy.ui.PVMyKunCun2
import com.czsy.ui.sqg.huishou.PVZheJiuDan2
import com.czsy.ui.sqg.order.PVClientOrder_Client
import com.czsy.ui.sqg.order.PVClientOrder_Empty
import com.czsy.ui.yunshuyuan.CompletedKt
import mylib.app.BackTask
import mylib.app.LocationService
import mylib.utils.Utils
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class PvQita(a: MainActivity, num_tuiping: String, num_baoxiu: String, num_huishou: String) : AbsPVBase(a), View.OnClickListener {

    lateinit var binding: PvSqgQitaBinding
    private val num_tuiping = num_tuiping
    private val num_baoxiu = num_baoxiu
    private val num_huishou = num_huishou

    override fun createMainView(ctx: Context?) {


        binding = PvSqgQitaBinding.inflate(LayoutInflater.from(act))
        mMainView = binding.root

        binding.include.ivBack.setOnClickListener {
            act.main_pvc.pop()
        }
        binding.tvNumBaoXiu.text = " ${num_baoxiu} "
        binding.tvNumHuiShouDan.text = " ${num_huishou} "
        binding.tvNumTuiPing.text = " ${num_tuiping} "

        binding.include.tvTitle.text = "其他"
        binding.tuipingContainer.setOnClickListener(this)
        binding.weixiuContainer.setOnClickListener(this)
        binding.zhejiuContainer.setOnClickListener(this)
        binding.hisOrderSearchContainer.setOnClickListener(this)
        binding.rukuContainer.setOnClickListener(this)
        binding.chukuContainer.setOnClickListener(this)
        binding.clientContainer.setOnClickListener(this)
        binding.mykucunContainer.setOnClickListener(this)
        binding.myGangpingContainer.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when (v) {
            binding.tuipingContainer -> {
                // 退瓶单
                if (true) {
                    act.pvc.push(PVCommonOrderList(act, false, Constant.dingdan_type_tuipin)) //new PVTuiPingList(act, false));
                } else {
                    // 直接新建
                    act.pvc.push(object : PVClientOrder_Client(act, null) {
                        override fun onClickOk() {
                            if (!client_pv.validClient()) {
                                return
                            }
                            val cb = client_pv.client_bean
                            val b = CommonOrderBean()
                            b.fromClientBean(cb)
                            b.dingDanLeiXing = Constant.dingdan_type_tuipin
                            act.pvc.replace(PVClientOrder_Empty(act, b))
                        }
                    })
                }
            }
            binding.weixiuContainer -> {
                // 维修
                if (true) {
                    act.pvc.push(PVWeiXiuList(act, false))
                } else {
                    act.pvc.push(object : PVClientOrder_Client(act, null) {
                        override fun onClickOk() {
                            if (!client_pv.validClient()) {
                                return
                            }
                            act.pvc.pop()
                            val cb = client_pv.client_bean
                            val b = WeiXiuBean()
                            b.keHuId = cb.id
                            b.diZhi = cb.diZhi
                            b.keHuMing = cb.userName
                            PVWeiXiuList.doWeiXiuDan(act, b, false, null)
                        }
                    })
                }
            }
            binding.zhejiuContainer -> {
                // 折旧
                if (true) {
                    act.pvc.push(PVCommonOrderList(act, false, Constant.dingdan_type_zhejiu))
                } else { // 直接新建
                    act.pvc.push(object : PVClientOrder_Client(act, null) {
                        override fun onClickOk() {
                            if (!client_pv.validClient()) {
                                return
                            }
                            val cb = client_pv.client_bean
                            val b = CommonOrderBean()
                            b.fromClientBean(cb)
                            b.dingDanLeiXing = Constant.dingdan_type_zhejiu
                            act.pvc.replace(PVZheJiuDan2(act, b))
                        }
                    })
                }

            }
            binding.hisOrderSearchContainer -> {
                // 历史单查询
                act.pvc.push(PVOrderSearch(act))
            }
            binding.rukuContainer, binding.chukuContainer -> {
                // 钢瓶领取 || 钢瓶回库
                val is_huiku = binding.rukuContainer !== v
                act.pvc.push(object : AbsPVScanEmpty(act, null, false) {
                    override fun createMainView(ctx: Context) {
                        super.createMainView(ctx)
                        btn_next.visibility = View.GONE
                        btn_clear.visibility = View.GONE
                        tv_title.setText(if (is_huiku) R.string.tip_chuku else R.string.tip_ruku)
                        btn_no_empty.setText(if (is_huiku) R.string.tip_chuku else R.string.tip_ruku)
                        btn_no_empty.setTextColor(Color.WHITE)
                        btn_no_empty.setBackgroundResource(R.drawable.sel_btn_blue)
                    }

                    override fun checkGanPing(gp: GangPingBean): String {
                        val lu = LoginUser.get()
                        if (is_huiku) {
                            /**
                             * (1)钢瓶不在供应站不可入库（提示：钢瓶不在库存）
                             * (2)钢瓶超期、报废不可入库（提示：此瓶超期未检、此瓶已报废）
                             * (3)钢瓶为空瓶不可入库（提示：此钢瓶是空瓶）
                             */
                            if (gp.peiSongYuanId != lu.id
                                    || gp.zuiHouWeiZhi != Constant.zhihouweizhi_psy) {
                                return "气瓶不在库存"
                            } else if (gp.status != GangPingBean.gp_status_using) {
                                return "气瓶状态: " + gp.getStatusName()
                            } else if (!gp.isEmpty) {
                                Utils.toastLONG("此气瓶是重瓶")
                            }
                        } else {
                            // (1)钢瓶不在配送员不可回库（提示：钢瓶不在库存）
                            if (gp.gongYingZhanId != lu.orgId
                                    || gp.zuiHouWeiZhi != Constant.zhihouweizhi_md) {
                                return "气瓶不在库存"
                            } else if (gp.status != GangPingBean.gp_status_using) {
                                return "气瓶状态: " + gp.getStatusName()
                            } else if (gp.isEmpty) {
                                return "此气瓶是空瓶"
                            }
                        }
//                        return super.checkGanPing(gp)
                        return ""
                    }

                    override fun onClickScanHeavy() {
                        // throw new RuntimeException();
                    }

                    private fun doIt() {
                        act.showProgress()
                        BackTask.post(object : CZBackTask(act) {
                            @Throws(Exception::class)
                            override fun parseResult(jdata: JSONObject) {
                                // TODO:
                            }

                            @Throws(Exception::class)
                            override fun getInputParam(): String {
                                val lu = LoginUser.get()
                                val j = JSONObject()
                                val pos = LocationService.myGP
                                if (pos != null) {
                                    j.put("lat", pos[1])
                                    j.put("lng", pos[0])
                                }
                                j.put("changZhanId", lu.orgId)
                                val ja = JSONArray()
                                for (info in gp_id_list) {
                                    ja.put(info.gp_bean.id)
                                }
                                for (info in nfc_gp_list) {
                                    ja.put(info.gp_bean.id)
                                }
                                run { j.put("idList", ja) }
                                return j.toString()
                            }

                            override fun getURL(): String {
                                return if (is_huiku) "gangPing/peiSongGangPingHuiKu" else "gangPing/peiSongGangPingChuKu"
                            }

                            override fun runFront2() {
                                Utils.toast(R.string.tip_op_ok)
                                val list: MutableList<GangPingInfo> = LinkedList()
                                list.addAll(gp_id_list)
                                list.addAll(nfc_gp_list)
                                val s = Constant.gpHuiZongInfoNew(list, false)
                                act.pvc.replace(CompletedKt(act, if (is_huiku) "气瓶回库" else "气瓶领取", s))
                            }
                        })
                    }

                    override fun onClickNoEmpty() {
                        val size = gp_id_list.size + nfc_gp_list.size
                        if (size == 0) {
                            Utils.toastLONG("没有输入气瓶")
                            return
                        }
                        // 出库或入库
                        getIdFromServer(object : ReturnRunable<Boolean?>() {
                            override fun run() {
                                if (!ret!!) {
                                    return
                                }
                                val ab = AlertDialog.Builder(act)
                                ab.setTitle(R.string.app_name)
                                ab.setMessage("输入了" + size + "个气瓶，确定提交？")
                                ab.setPositiveButton(android.R.string.ok) { dialogInterface, i ->
                                    dialogInterface.dismiss()
                                    doIt()
                                }
                                ab.setNegativeButton(android.R.string.cancel, null)
                                ab.show()
                            }
                        })
                    }
                })
            }
            binding.clientContainer -> {
                // 充值
                val client: PVClientOrder_Client = object : PVClientOrder_Client(act, null) {
                    override fun onClickOk() {
                        if (!client_pv.validClient()) {
                            return
                        }
                        val cb = client_pv.client_bean
                        act.pvc.replace(PVCharge(act, cb))
                    }
                }
                act.pvc.push(client)
            }
            binding.mykucunContainer -> {
                // 我的库存
                act.pvc.push(PVMyKunCun2(act))
            }
            binding.myGangpingContainer ->{
                act.pvc.push(PVNeedGangPing(act))
            }
        }

    }
}