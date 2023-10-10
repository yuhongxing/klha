package com.czsy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.czsy.android.BuildConfig;
import com.czsy.android.R;
import com.czsy.bean.CommonOrderBean;
import com.czsy.bean.GangPingBean;
import com.czsy.bean.IDNameBean;
import com.czsy.bean.IGPInfo;
import com.google.gson.Gson;

import mylib.app.BackTask;
import mylib.app.BaseActivity;
import mylib.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class Constant {

    public final static boolean DO_TEST = BuildConfig.DEBUG;

    public final static long _1day = (1000 * 60 * 60 * 24);
    public final static int GPS_INTERVAL = (60); // gps location update, in sec


    public static final int wx_pay_process = 1;
    public static final int wx_pay_ok = 2;
    public static final int wx_pay_err = 3;

    /**
     * 1-->厂站员工
     * 2-->运输员
     * 3-->配送员
     * 4-->巡检员
     * 5-->维修员
     */
    public final static int role_min = 1;
    public final static int role_max = 7;
    public final static int role_chang_zhan = 1; // 场站
    public final static int role_yun_shu_yuan = 2; // 运输员
    public final static int role_song_qi_gong = 3; // 送气工人
    public final static int role_xun_jian = 4; //巡检员
    public final static int role_wei_xiu = 5; //维修员
    public final static int role_gyz_zhanzhang = 6; //供应站-站长
    public final static int role_cz_zhanzhang = 7; //充装站-站长


    public final static String login_name = "login_name";


    //最后位置,1(厂站)，2（运输员），3（配送站），4（配送员），5（客户）
    public final static int zhihouweizhi_cz = 1;
    public final static int zhihouweizhi_ysy = 2;
    public final static int zhihouweizhi_md = 3;
    public final static int zhihouweizhi_psy = 4;
    public final static int zhihouweizhi_kh = 5;

    public interface GPChecker {
        String checkGanPing(GangPingBean gp);
    }

    //    YI_FEN_PEI(new Byte("2"), "已分配"),
//    DAI_FU_KUAN(new Byte("4"), "待付款"),
//    FINISH(new Byte("6"), "完成"),
    public final static int status_yifenpei = 2;
    public final static int status_daifukuan = 4;
    public final static int status_done = 6;

    // 1（购气订单）、2（商品订单）、3（退瓶单）、4（回收单）折旧 , 5(充值卡）
    public final static int dingdan_type_gouqi = 1;
    public final static int dingdan_type_shangpin = 2;
    public final static int dingdan_type_tuipin = 3;
    public final static int dingdan_type_zhejiu = 4;
    public final static int dingdan_type_czk = 5;
    public final static int dingdan_type_baoxiu = 6;
    public final static int dingdan_type_anjian = 7;


    //public final static String dingdan_tuiping_dan = "退瓶单";
    //public final static String dingdan_wupin_dan = "物品订单";

    public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getDateString(long time) {
        return sdf.format(new Date(time));
    }

    public static boolean sameString(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return false;
        }
        return TextUtils.equals(s1, s2);
    }

    public static boolean hasPermission(Context act, String p) {
        int perm = PermissionChecker.checkSelfPermission(act, p);
        return perm == 0;
    }

    public static Pair<String, String> getTimeDuration(long cur_date, long t2) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(cur_date);
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);
        String d1 = String.format(String.format("%4d-%02d-%02d", y, m + 1, d));

        c.setTimeInMillis(t2); //cur_date + (1000 * 60 * 60 * 24) + 100 * 60);
        y = c.get(Calendar.YEAR);
        m = c.get(Calendar.MONTH);
        d = c.get(Calendar.DAY_OF_MONTH);
        String d2 = String.format(String.format("%4d-%02d-%02d", y, m + 1, d));

        return new Pair<>(d1, d2);
    }

    // the global gson
    public final static Gson gson = new Gson();

    public static void deltePics(List<String> pic) {
        if (pic == null) {
            return;
        }
        for (String f : pic) {
            if (f == null) {
                continue;
            }
            File ff = new File(f);
            ff.delete();
        }
    }

    //获取钢瓶规格
    public static void getLX_GE(
            List<IDNameBean> ge
    ) throws Exception {
        // 获取钢瓶规格
        JSONObject j_lx = CZNetUtils.postCZHttp("gangPingPriceRule/getRuleParam", "{}");
        JSONArray ja = j_lx.getJSONObject("result").getJSONArray("guiGes");
        for (int i = 0; i < ja.length(); i++) {
            IDNameBean gp = Constant.gson.fromJson(ja.getJSONObject(i).toString(),
                    IDNameBean.class);
            ge.add(gp);
        }
    }

    //获取钢瓶规格 和 气体类型
    public static void getLX_GE(
            List<IDNameBean> lx,
            List<IDNameBean> ge
    ) throws Exception {
        // 获取钢瓶类型
        JSONObject j_qt = CZNetUtils.getCZHttp("gangPing/ranQiZhongLei");
        JSONArray ja_qt = j_qt.getJSONArray("result");
        for (int i = 0; i < ja_qt.length(); i++) {
            IDNameBean gp = Constant.gson.fromJson(ja_qt.getJSONObject(i).toString(),
                    IDNameBean.class);
            lx.add(gp);
        }

        // 获取钢瓶规格
        JSONObject j_lx = CZNetUtils.postCZHttp("gangPingPriceRule/getRuleParam", "{}");
        JSONArray ja = j_lx.getJSONObject("result").getJSONArray("guiGes");
        for (int i = 0; i < ja.length(); i++) {
            IDNameBean gp = Constant.gson.fromJson(ja.getJSONObject(i).toString(),
                    IDNameBean.class);
            ge.add(gp);
        }
    }

    public static void getIdFromServer(
            final BaseActivity act,
            final CommonOrderBean.GangPingInfo gp_info,
            final GPChecker checker,
            final ReturnRunable<Boolean> call_back) {
        if (gp_info == null || gp_info.gp_bean == null
                || (TextUtils.isEmpty(gp_info.gp_bean.xinPianHao) &&
                TextUtils.isEmpty(gp_info.gp_bean.gangPingHao))) {
            throw new IllegalArgumentException();
        }
        act.showProgress();

        // xin pian hao -> gp info
        call_back.ret = false;
        BackTask.post(new CZBackTask(act) {
            @Override
            protected void parseResult(JSONObject ret) throws Exception {
                JSONArray result = ret.getJSONArray("result");

                if (result.length() == 0) {
                    call_back.msg = act.getString(R.string.tip_gp_not_found);
                    call_back.ret = false;
                } else {
                    GangPingBean gp = Constant.gson.fromJson(
                            result.getJSONObject(0).toString(),
                            GangPingBean.class);
                    String err_info = checker == null ? null : checker.checkGanPing(gp);
                    if (TextUtils.isEmpty(err_info)) { // ok
                        gp_info.gp_bean = gp;
                        gp_info.jingZhong = gp.jingZhong;
                        call_back.msg = null;
                        call_back.ret = true;
                    } else if (err_info.equals(TheApp.sInst.getString(R.string.err_gp_not_user))) {
                        gp_info.gp_bean = gp;
                        gp_info.jingZhong = gp.jingZhong;
                        call_back.msg = TheApp.sInst.getString(R.string.err_gp_not_user);
                        call_back.ret = true;
                    } else {
                        call_back.msg = err_info;
                        call_back.ret = false;
                    }
                }
            }

            // in:  [ { gangping_id: <string 钢瓶号>, is_nfc: <bool 钢瓶号是否nfc？还是钢印号> } , ... ]
            //out: [ {id:<long 此钢瓶的id号>,gangping_id: <string 钢瓶号>, is_nfc: <bool 钢瓶号是否nfc？还是钢印号>}, ... ]

            @Override
            protected String getInputParam() throws Exception {
                JSONArray ja = new JSONArray();
                final GangPingBean b = gp_info.gp_bean;
                JSONObject jo = new JSONObject();
                ja.put(jo);
                if (!TextUtils.isEmpty(b.xinPianHao)) {
                    jo.put("xinPianHao", b.xinPianHao);
                } else {
                    jo.put("gangPingHao", b.gangPingHao);
                }
                return ja.toString();
            }

            @Override
            protected String getURL() {
                return "gangPing/piLiangChaXun";
            }

            @Override
            protected void runFront2() {
                act.hideProgress();
                if (!call_back.ret) {
                    Utils.toastLONG(call_back.msg);
                } else {

                    if (call_back.msg != null && call_back.msg.equals(TheApp.sInst.getString(R.string.err_gp_not_user))) {

                        final AlertDialog.Builder normalDialog =
                                new AlertDialog.Builder(act);
                        normalDialog.setTitle(call_back.msg);
                        normalDialog.setMessage("是否添加？");
                        normalDialog.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        call_back.run();
                                    }
                                });
                        normalDialog.setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        // 显示
                        normalDialog.show();
                    } else {

                        call_back.run();
                    }

                }
            }
        });
    }

    public static String[] getLouCeng() {
        return new String[]{
                "1楼", "2楼", "3楼", "4楼", "5楼", "6楼", // "7楼", "8楼", "9楼", "10楼",
        };
    }

    public static String gpHuiZongInfo(Collection<? extends IGPInfo> list,
                                       boolean has_jine) {
        // 显示汇总信息
        if (list == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        Map<String, Integer> map = new HashMap<>();
        Map<String, Double> map_jine = new HashMap<>();
        for (IGPInfo gp : list) {
            String guiGeName = gp.getGuiGeName();
            if (guiGeName == null) {
                continue;
            }
            Integer i = map.get(guiGeName);
            if (i == null) {
                map.put(guiGeName, 1);
                if (has_jine) {
                    map_jine.put(guiGeName, gp.getJine());
                }
            } else {
                map.put(guiGeName, i + 1);
                if (has_jine) {
                    double d = map_jine.get(guiGeName);
                    map_jine.put(guiGeName, d + gp.getJine());
                }
            }
        }

        for (String key : map.keySet()) {
            sb.append(key + " : " + map.get(key));
            if (has_jine) {
                sb.append(String.format("  ; 金额： %.2f", map_jine.get(key)));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public static List<String> gpHuiZongInfoNew(Collection<? extends IGPInfo> list,
                                                boolean has_jine) {
        // 显示汇总信息
        if (list == null) {
            return null;
        }
        String sb = "";
        Map<String, Integer> map = new HashMap<>();
        Map<String, Double> map_jine = new HashMap<>();
        for (IGPInfo gp : list) {
            String guiGeName = gp.getGuiGeName();
            if (guiGeName == null) {
                continue;
            }
            Integer i = map.get(guiGeName);
            if (i == null) {
                map.put(guiGeName, 1);
                if (has_jine) {
                    map_jine.put(guiGeName, gp.getJine());
                }
            } else {
                map.put(guiGeName, i + 1);
                if (has_jine) {
                    double d = map_jine.get(guiGeName);
                    map_jine.put(guiGeName, d + gp.getJine());
                }
            }
        }

        List<String> mlist = new ArrayList<>();
        for (String key : map.keySet()) {
            sb = "规格：" + key + " ； 数量：" + map.get(key);
            if (has_jine) {
                sb = sb + " ； 金额：" + map_jine.get(key);
            }
            mlist.add(sb);
        }
        return mlist;
    }
}
