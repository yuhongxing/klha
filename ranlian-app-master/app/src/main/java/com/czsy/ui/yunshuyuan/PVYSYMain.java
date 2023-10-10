package com.czsy.ui.yunshuyuan;


import android.content.Context;
import android.util.Log;
import android.view.View;

import com.czsy.CZBackTask;
import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.android.R;
import com.czsy.bean.CongzhuangzhanBean;
import com.czsy.bean.GangPingBean;
import com.czsy.bean.LoginUser;
import com.czsy.bean.MenDianBean;
import com.czsy.ui.*;
import com.czsy.ui.changzhan.PVCZUpdateGangping;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.app.MyLog;
import mylib.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// yunshuyuan
public class PVYSYMain extends AbsPVBase implements View.OnClickListener {

    private List<CongzhuangzhanBean> czz_list = new ArrayList<>();

    public PVYSYMain(MainActivity a) {
        super(a);
    }

    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(ctx, R.layout.pv_yunshuyuan_main, null);

        for (int id : new int[]{
                R.id.btn_md_rk,
                R.id.btn_md_ck,
                R.id.btn_cz_rk,
                R.id.btn_cz_ck,
                R.id.btn_order,
                R.id.xiugaigangping_container,
                R.id.mykucun_container,
        }) {
            mMainView.findViewById(id).setOnClickListener(this);
        }
    }

    private void md_ck(final List<GangPingBean> list, final long md_id) { // 门店出库
        act.showProgress();
        BackTask.post(new CZBackTask(act) {
            @Override
            protected void parseResult(JSONObject jdata) throws Exception {

            }

            @Override
            protected String getInputParam() throws Exception {
                JSONObject j = new JSONObject();
                JSONArray ja = new JSONArray();
                for (GangPingBean gp : list) {
                    ja.put(gp.id);
                }
                j.put("idList", ja);
                j.put("gongYingZhanId", md_id);
                return j.toString();
            }

            @Override
            protected String getURL() {
                return "gangPingLiuZhuan/menDianFaSong";
            }

            @Override
            protected void runFront2() {
                Utils.toast(R.string.tip_op_ok);
                // 显示汇总信息
                List<String> s = Constant.gpHuiZongInfoNew(list, false);
//                PVSimpleText pv = new PVSimpleText(act, s);
                CompletedKt pv = new CompletedKt(act,"供应站出库",s);
                act.getPVC().replace(pv);
            }
        });
    }

    private void cz_ck(final List<GangPingBean> list) {
        act.showProgress();
        BackTask.post(new CZBackTask(act) {
            @Override
            protected void parseResult(JSONObject jdata) throws Exception {

            }

            @Override
            protected String getInputParam() throws Exception {
                JSONObject j = new JSONObject();
                JSONArray ja = new JSONArray();
                for (GangPingBean gp : list) {
                    ja.put(gp.id);
                }
                j.put("idList", ja);

                return j.toString();
            }

            @Override
            protected String getURL() {
                return "gangPingLiuZhuan/changZhanChuKu";
            }

            @Override
            protected void runFront2() {
                Utils.toast(R.string.tip_op_ok);
                // 显示汇总信息
                List<String> s = Constant.gpHuiZongInfoNew(list, false);
                CompletedKt pv = new CompletedKt(act,"充装站出库",s);
                act.getPVC().replace(pv);
            }
        });
    }

    private void cz_rk(final List<GangPingBean> list, final long md_id) {
        act.showProgress();
        BackTask.post(new CZBackTask(act) {
            @Override
            protected void parseResult(JSONObject jdata) throws Exception {

            }

            @Override
            protected String getInputParam() throws Exception {
                JSONObject j = new JSONObject();
                JSONArray ja = new JSONArray();
                for (GangPingBean gp : list) {
                    ja.put(gp.id);
                }
                j.put("idList", ja);
                j.put("changZhanId", md_id);
                return j.toString();
            }

            @Override
            protected String getURL() {
                return "gangPingLiuZhuan/changZhanRuKu";
            }

            @Override
            protected void runFront2() {
                Utils.toast(R.string.tip_op_ok);
                // 显示汇总信息
                List<String> s = Constant.gpHuiZongInfoNew(list, false);
                CompletedKt pv = new CompletedKt(act,"充装站入库",s);
                act.getPVC().replace(pv);
            }
        });
    }

    private void md_rk(final List<GangPingBean> list, final long md_id) { // 门店入库
        act.showProgress();
        BackTask.post(new CZBackTask(act) {
            @Override
            protected void parseResult(JSONObject jdata) throws Exception {

            }

            @Override
            protected String getInputParam() throws Exception {
                JSONObject j = new JSONObject();
                JSONArray ja = new JSONArray();
                for (GangPingBean gp : list) {
                    ja.put(gp.id);
                }
                j.put("idList", ja);
                j.put("gongYingZhanId", md_id);
                return j.toString();
            }

            @Override
            protected String getURL() {
                return "gangPingLiuZhuan/menDianJieShou";
            }

            @Override
            protected void runFront2() {
                Utils.toast(R.string.tip_op_ok);
                // 显示汇总信息
                List<String> s = Constant.gpHuiZongInfoNew(list, false);
                CompletedKt pv = new CompletedKt(act,"供应站入库",s);
                act.getPVC().replace(pv);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.btn_order == id) { // 我的运输单
            PVMyYunShuDan pv = new PVMyYunShuDan(act);
            act.getPVC().push(pv);
        } else if (R.id.btn_setting == id) {
            PVSetting pv = new PVSetting(act,0);
            act.getPVC().push(pv);
        } else if (R.id.btn_md_rk == id) {
            // 门店入库，选择门店
            act.getPVC().push(new AbsPVSeleItem(act, "供应站", "gangPingLiuZhuan/menDianList") {

                @Override
                protected void onOk(final MenDianBean mb,List<MenDianBean> list) {
                    // id & name 是选中的门店
                    act.getPVC().replace(new AbsYSYPVScan(act) {

                        @Override
                        public String checkGanPing(GangPingBean gp) {
                            if (gp.status != GangPingBean.gp_status_using) {
                                return "气瓶状态： " + gp.getStatusName();
                            }
                            if (gp.changZhanId <= 0) {
                                return "无归属充装站";
                            }
                            LoginUser lu = LoginUser.get();
                            if (gp.yunShuYuanId != lu.id
                                    || gp.zuiHouWeiZhi != Constant.zhihouweizhi_ysy) {
                                return "此气瓶不在库存";
                            }
                            if (gp.isEmpty()) {
                                return ("此气瓶是空瓶");
                            }
                            return super.checkGanPing(gp);
                        }

                        @Override
                        protected void onGP(List<GangPingBean> list) {
                            md_rk(list, mb.id);
                        }
                    });
                }
            });
        } else if (R.id.btn_md_ck == id) {
            // 门店出库，选择门店
            act.getPVC().push(new AbsPVSeleItem(act, "供应站", "gangPingLiuZhuan/menDianList") {

                @Override
                protected void onOk(final MenDianBean mb,List<MenDianBean> list) {
                    // id & name 是选中的门店
                    act.getPVC().replace(new AbsYSYPVScan(act) {

                        @Override
                        public String checkGanPing(GangPingBean gp) {
                            if (gp.gongYingZhanId != mb.danWeiId
                                    || gp.zuiHouWeiZhi != Constant.zhihouweizhi_md) {
//                                return act.getString(R.string.err_gp_not_org);
                                return "此气瓶不在供应站";
                            }
                            if (!gp.isEmpty()) {
                                Utils.toastLONG("此瓶为重瓶");
                            }
                            if (gp.changZhanId <= 0) {
                                return "无归属充装站";
                            }

                            return super.checkGanPing(gp);
                        }

                        @Override
                        protected void onGP(List<GangPingBean> list) {
                            md_ck(list, mb.id);
                        }
                    });
                }
            });
        } else if (R.id.btn_cz_rk == id) {

            act.getPVC().push(new AbsPVSeleItem(act, "充装站", "gangPingLiuZhuan/changZhanList") {
                @Override
                protected void onOk(final MenDianBean mb, final List<MenDianBean> list) {
                    act.getPVC().push(
                            new AbsYSYPVScan(act) {

                                @Override
                                public String checkGanPing(GangPingBean gp) {
                                    if (gp.status != GangPingBean.gp_status_using) {
                                        return "气瓶状态: " + gp.getStatusName();
                                    }
                                    LoginUser lu = LoginUser.get();
                                    if (gp.yunShuYuanId != lu.id
                                            || gp.zuiHouWeiZhi != Constant.zhihouweizhi_ysy) {
                                        return "此气瓶不在库存";
//                                return act.getString(R.string.err_gp_bad_user);
                                    }
                                    if (!gp.isEmpty()) {
//                                Utils.toast(R.string.err_gp_not_empty);
                                        Utils.toastLONG("此瓶为重瓶");
                                    }
                                    if (gp.changZhanId <= 0) {
                                        Utils.toastLONG("无归属充装站");
                                    }


                                    for (MenDianBean bean : list) {
                                        if (bean.danWeiId == gp.changZhanId) {
                                            Log.d("xing", "(bean.getDanWeiId()) == gp.changZhanId) ==>" + gp.changZhanId);
                                            return null;
                                        }
                                    }

                                    return super.checkGanPing(gp);
                                }

                                @Override
                                protected void onGP(List<GangPingBean> list) {
                                    cz_rk(list, mb.id);
                                }
                            }
                    );
                }
            });

//            selectC();

        } else if (R.id.btn_cz_ck == id) {
            // 场站出库，直接扫瓶
            selectC();
            act.getPVC().push(
                    new AbsYSYPVScan(act) {

                        @Override
                        public String checkGanPing(GangPingBean gp) {
                            if (gp.status != GangPingBean.gp_status_using) {
                                return "气瓶状态: " + gp.getStatusName();
                            }
                            if (gp.zuiHouWeiZhi != Constant.zhihouweizhi_cz) {
                                return ("此气瓶不在充装站");
                            }
//                            LoginUser lu = LoginUser.get();
//                            if (lu.orgId != gp.changZhanId) {
//                                return "此钢瓶不属于充装站";
//                            }

                            if (gp.isEmpty()) {
                                return "此气瓶是空瓶";
                            }


                            for (CongzhuangzhanBean bean : czz_list) {
                                if (Long.parseLong(bean.getDanWeiId()) == gp.changZhanId) {
                                    Log.d("xing", "(bean.getDanWeiId()) == gp.changZhanId) ==>" + gp.changZhanId);
                                    return null;
                                }
                            }

                            return super.checkGanPing(gp);
                        }

                        @Override
                        protected void onGP(List<GangPingBean> list) {
                            cz_ck(list);
                        }
                    }
            );
        } else if (R.id.mykucun_container == id) { // 我的库存
            act.getPVC().push(new PVMyKunCun2(act));

        }else if (R.id.xiugaigangping_container == id) { // 修改钢瓶信息
            act.getPVC().push(new PVCZUpdateGangping(act));
        }
    }

    //    //获取场站ID
    private void selectC() {
        act.showProgress();
        BackTask.post(new BackFrontTask() {
            @Override
            public void runFront() {
                act.hideProgress();
                if (act.isFinishing()) {
                    return;
                }
//                List<String> s = new LinkedList<>();
//                for (CongzhuangzhanBean yj : czz_list) {
//                    s.add(yj.getMingCheng());
//                }
            }

            String err_msg;

            @Override
            public void runBack() {
                try {
                    JSONObject ret = CZNetUtils.postCZHttp(
                            "gangPingLiuZhuan/changZhanList", null);
                    JSONArray result = ret.isNull("result") ? null : ret.optJSONArray("result");
                    czz_list.clear();
                    for (int i = 0; i < result.length(); i++) {
                        czz_list.add(
                                Constant.gson.fromJson(result.getJSONObject(i).toString(),
                                        CongzhuangzhanBean.class));
                    }

                } catch (Exception e) {
                    MyLog.LOGE(e);
                    if (null == err_msg) {
                        err_msg = act.getString(R.string.tip_common_err);
                    }
                }

            }
        });

    }

}

