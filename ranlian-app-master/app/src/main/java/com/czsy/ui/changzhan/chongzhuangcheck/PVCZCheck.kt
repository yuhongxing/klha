package com.czsy.ui.changzhan.chongzhuangcheck

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import com.czsy.android.R
import com.czsy.ui.MainActivity
import com.czsy.ui.changzhan.AbsPVCZPiLiangKt
import mylib.utils.Utils


class PVCZCheck(a: MainActivity, _isNext: Boolean) : AbsPVCZPiLiangKt(a, _isNext) {

    override fun getMainViewRes(): Int {
        return R.layout.cz_abs_piliang
    }

    override fun createMainView(ctx: Context?) {
        super.createMainView(ctx)
//        tv_fields1?.text = "钢瓶编号"
        tv_title?.text = "充装检查"
    }

    override fun subData(list: List<Long>) {

        if (list.size == 0){
            Utils.toastSHORT("未扫描气瓶")
            return
        }

        var yourChoice = 0
        val items = arrayOf("充装-前", "充装-后")
        val singleChoiceDialog: AlertDialog.Builder = AlertDialog.Builder(act)
        singleChoiceDialog.setTitle("请选择检测项")
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(items, 0,
                DialogInterface.OnClickListener { dialog, which -> yourChoice = which })
        singleChoiceDialog.setPositiveButton("确定",
                DialogInterface.OnClickListener { dialog, which ->

                    act.pvc.push(PVCZSeleteTipSub(act, yourChoice + 1,list))
                })
        singleChoiceDialog.show()
    }
}