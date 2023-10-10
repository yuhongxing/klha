package com.czsy.ui.sqg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.czsy.CZBackTask;
import com.czsy.CZNetUtils;
import com.czsy.android.R;
import com.czsy.bean.AnJianBean;
import com.czsy.bean.AnJianOrderBean;
import com.czsy.bean.ClientBean;
import com.czsy.ui.AbsPVBase;
import com.czsy.ui.MainActivity;
import com.czsy.ui.PVImage;
import com.czsy.ui.fingerpaint.SignDialog;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.app.MyLog;
import mylib.utils.CameraUtils;
import mylib.utils.FileUtils;
import mylib.utils.ImageUtils;
import mylib.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PVAnJianDan extends AbsPVBase implements View.OnClickListener {
    final ClientBean client_bean;
    ImageView iv_sign;
    TextView tv_client_info;
    final boolean can_skip;
    ViewGroup pic_container, main_container;
    final String ding_dan_hao;

    public final AnJianBean anjian_bean = new AnJianBean();
    protected final AnJianOrderBean anjian_order_bean;

    public PVAnJianDan(MainActivity a, ClientBean c, AnJianOrderBean ab) {
        super(a);
        ding_dan_hao = "";
        client_bean = c;
        can_skip = true;
        anjian_order_bean = ab;
    }

    private boolean orderIsDone() {
        return (anjian_order_bean != null
                && anjian_order_bean.status == 4);
    }

    public PVAnJianDan(MainActivity a, ClientBean c, boolean can_skip, String order_id) {
        super(a);
        ding_dan_hao = order_id;
        client_bean = c;
        this.can_skip = can_skip;
        anjian_order_bean = null;
    }

    @Override
    protected void createMainView(Context ctx) {
        initPhotoError();
        mMainView = View.inflate(ctx, R.layout.pv_an_jian_dan, null);
        mMainView.findViewById(R.id.btn_sign).setOnClickListener(this);
        mMainView.findViewById(R.id.btn_dengji).setOnClickListener(this);
        mMainView.findViewById(R.id.iv_back).setOnClickListener(this);
        main_container = mMainView.findViewById(R.id.main_container);
        pic_container = mMainView.findViewById(R.id.pic_container);
        pic_container.findViewById(R.id.iv_add).setOnClickListener(this);

        View v = mMainView.findViewById(R.id.tv_skip);
        if (!can_skip) {
            v.setOnClickListener(this);
        } else {
            v.setVisibility(View.GONE);
        }
        iv_sign = mMainView.findViewById(R.id.iv_sign);
        tv_client_info = mMainView.findViewById(R.id.tv_client_info);
        // update text
        String address = client_bean == null ? null : client_bean.diZhi;
        if (address == null) {
            address = anjian_order_bean == null ? "" : anjian_order_bean.diZhi;
        }
        tv_client_info.setText(String.format("客户信息：%s (%s)\n%s: %s"
                , TextUtils.isEmpty(client_bean.userName) ? "无信息" : client_bean.userName,
                TextUtils.isEmpty(client_bean.telNum) ? "" : client_bean.telNum
                , act.getString(R.string.title_address), address
        ));
        if (orderIsDone()) {
            // order is done , can only view
            mMainView.findViewById(R.id.btn_container).setVisibility(View.GONE);
            mMainView.findViewById(R.id.iv_add).setVisibility(View.GONE);
            updateAnJianQues(anjian_order_bean.anJianWenTiList);
        }
    }

    private void initPhotoError() {
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        final String file = CameraUtils.getPicutre(data, requestCode, resultCode, act, pending_pic_file);
        if (ImageUtils.isImage(file)) {

            BackTask.post(new BackFrontTask() {
                @Override
                public void runFront() {
                    if (bmp == null) {
                        return;
                    }
                    anjian_bean.pics.add(file);
                    ImageView v = (ImageView) View.inflate(act, R.layout.an_jian_dan_item, null);
                    v.setImageBitmap(bmp);
                    v.setTag(bmp);
                    v.setTag(file);
                    pic_container.addView(v, pic_container.getChildAt(0).getLayoutParams());
                    v.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(final View view) {
                            final String file = (String) view.getTag();
                            if (file == null) {
                                return false;
                            }
                            AlertDialog.Builder ab = new AlertDialog.Builder(act);
                            ab.setTitle(R.string.app_name).setMessage(R.string.tip_sur_delete).setNegativeButton(
                                    android.R.string.cancel, null
                            ).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int ii) {
                                    dialogInterface.dismiss();
                                    pic_container.removeView(view);
                                    anjian_bean.pics.remove(file);

                                    File f = new File(file);
                                    f.delete();
                                }
                            });
                            ab.show();
                            return true;
                        }
                    });
                }

                Bitmap bmp;

                @Override
                public void runBack() {
                    bmp = ImageUtils.resizeBitmap(
                            ImageUtils.getFileBmp(file), 100);
                }
            });
            return true;
        } else {
            return super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String pending_pic_file = null;

    protected void onAnJianOk() {
    }

    protected void onAnJianSkip() {
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.iv_add == id) {
            pending_pic_file = FileUtils.getDir(FileUtils.DirType.pic) + System.currentTimeMillis() + ".jpg";
            CameraUtils.doCamera(act, pending_pic_file);
        } else if (R.id.tv_skip == id) {
            onAnJianSkip();
        } else if (R.id.iv_back == id) {
            act.getPVC().pop();
        } else if (R.id.btn_sign == id) {
            final SignDialog d = new SignDialog(act);
            d.setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = view.getId();
                    if (id == R.id.iv_back) {
                        d.dismiss();
                    } else if (R.id.btn_ok == id) {
                        d.dismiss();
                        Bitmap bmp = d.finger_view.getBitmap();
                        iv_sign.setImageBitmap(bmp);
                        iv_sign.setTag(bmp);
                        iv_sign.setVisibility(View.VISIBLE);
                    } else if (R.id.btn_reset == id) {
                        d.finger_view.clear();
                    }
                }
            });
            d.show();
        } else if (R.id.btn_dengji == id) {
            for (AnJianQuesViewHolder vh : anjian_list) {
                boolean checked = false;
                for (CheckBox cb : vh.cbs) {
                    if (cb.isChecked()) {
                        checked = true;
                        break;
                    }
                } // for
                if (!checked) {
                    Utils.toastLONG("请填写所有问题");
                    return;
                }
            } // for
            final Bitmap bmp = (Bitmap) iv_sign.getTag();
            if (bmp == null) {
                Utils.toastLONG("客户未签字");
                return;
            }
            if (pic_container.getChildCount() <= 1) {
                Utils.toastLONG("未拍照");
                return;
            }
            act.showProgress();
            BackTask.post(new CZBackTask(act) {
                @Override
                protected void parseResult(JSONObject jdata) throws Exception {

//                    int code = jdata.getInt("code");
//                    if (code == 200) {
//                        Utils.toastSHORT("登记成功");
//                    }
                }

                @Override
                protected String getInputParam() throws Exception {
                    String bmp_url = CZNetUtils.upload(bmp);
                    JSONArray j_tupian = new JSONArray();

                    StringBuffer ssb = new StringBuffer();
                    for (int i = 1; i < pic_container.getChildCount(); i++) {
                        String file = (String) pic_container.getChildAt(i).getTag();
                        j_tupian.put(CZNetUtils.upload(file));
                    }
                    JSONObject j = new JSONObject();
                    if (anjian_order_bean != null) {
                        j.put("sid", anjian_order_bean.dingDanHao);
                    }
                    j.put("diZhi", client_bean.diZhi);
                    j.put("anJianTuPianList", j_tupian);
                    j.put("anJianQianMing", bmp_url);

                    if (!TextUtils.isEmpty(ding_dan_hao)) {
                        j.put("dingDanHao", ding_dan_hao);
                    }
                    j.put("keHuId", client_bean.id);

                    JSONArray anJianDaAnList = new JSONArray();
                    j.put("daAnList", anJianDaAnList);
                    for (AnJianQuesViewHolder vh : anjian_list) {
                        JSONObject o = new JSONObject();
                        anJianDaAnList.put(o);
                        o.put("wenTi", vh.item.wenTi);

                        JSONArray huida = new JSONArray();
                        o.put("huiDa", huida);
                        for (CheckBox cb : vh.cbs) {
                            if (cb.isChecked()) {
                                //sb.append(cb.getText());
                                huida.put(cb.getText());
                            }
                        } // for
                    } // for
                    return j.toString();
                }

                @Override
                protected String getURL() {
                    return anjian_order_bean == null ? "anJian/create"
                            : "anJian/jieDan";
                }

                @Override
                protected void runFront2() {
                    onAnJianOk();
                }
            });
        }
    }


    final private static class AnJianQuesViewHolder {
        final AnJianOrderBean.QA item;
        final CheckBox[] cbs;
        final TextView tv_title;
        final View root;

        AnJianQuesViewHolder(Context ctx, AnJianOrderBean.QA i) {
            View r = View.inflate(ctx, R.layout.anjian_ques_item, null);
            root = r;
            root.setTag(this);
            item = i;
            cbs = new CheckBox[]{
                    r.findViewById(R.id.cb1),
                    r.findViewById(R.id.cb2),
                    r.findViewById(R.id.cb3),
                    r.findViewById(R.id.cb4),
                    r.findViewById(R.id.cb5),
                    r.findViewById(R.id.cb6),
                    r.findViewById(R.id.cb7),
                    r.findViewById(R.id.cb8),
                    r.findViewById(R.id.cb9),
                    r.findViewById(R.id.cb10),
            };
            for (CheckBox cb : cbs) {
                cb.setTag(this);

            }

            tv_title = r.findViewById(R.id.tv_title);
            tv_title.setText(item.wenTi);
            for (int idx = 0; idx < cbs.length; idx++) {
                if (idx >= item.daAn.size()) {
                    cbs[idx].setVisibility(View.GONE);
                } else {
                    cbs[idx].setVisibility(View.VISIBLE);
                    cbs[idx].setText(item.daAn.get(idx));
                }
            }
        }

    }

    final private List<AnJianQuesViewHolder> anjian_list = new LinkedList<>();

    void updateAnjianPics() {
        if (anjian_order_bean.anJianQianMing == null &&
                anjian_order_bean.anJianTuPian == null) {
            return;
        }
        try {
            List<String> list = new LinkedList<>();
            if (!TextUtils.isEmpty(anjian_order_bean.anJianQianMing)) {
                list.add(anjian_order_bean.anJianQianMing);
            }
            String[] pics = anjian_order_bean.anJianTuPian.split(",");
            list.addAll(Arrays.asList(pics));

            for (String pic : list) {
                if (TextUtils.isEmpty(pic) || ",".equalsIgnoreCase(pic)) {
                    continue;
                }
                ImageView v = (ImageView) View.inflate(act, R.layout.an_jian_dan_item, null);
                final String url = CZNetUtils.svr_host + pic;
                v.setBackgroundResource(R.drawable.bg_blue_rect);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        act.getPVC().push(new PVImage(act, url));
                    }
                });
                pic_container.addView(v, pic_container.getChildAt(0).getLayoutParams());
                Glide.with(act).load(url).into(v);

            }
        } catch (Exception e) {
            MyLog.LOGE(e);
        }
    }

    private void updateAnJianQues(List<AnJianOrderBean.QA> item_list) {
        main_container.removeAllViews();
        anjian_list.clear();
        if (item_list.isEmpty()) {
            return;
        }
        boolean is_done = orderIsDone();
        for (AnJianOrderBean.QA item : item_list) {
            AnJianQuesViewHolder vh = new AnJianQuesViewHolder(act, item);
            main_container.addView(vh.root);
            anjian_list.add(vh);
            if (is_done) {
                vh.root.setEnabled(false);
                for (CheckBox cb : vh.cbs) {
                    cb.setChecked(true);
                    //cb.setEnabled(false);
                    cb.setClickable(false);
                }
            }
        }
        if (is_done) {
            updateAnjianPics();
        }
    }

    @Override
    public void onAttach(boolean firstShow) {
        super.onAttach(firstShow);
        if (!orderIsDone()) {
            act.showProgress();
            BackTask.post(new CZBackTask(act) {
                final List<AnJianOrderBean.QA> item_list = new LinkedList<>();

                @Override
                protected void parseResult(JSONObject jdata) throws Exception {
                    item_list.clear();
                    JSONArray result = jdata.getJSONArray("result");
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject j_obj = result.getJSONObject(i);
                        String title = j_obj.getString("wenTi");
                        JSONArray optionList = j_obj.getJSONArray("optionList");
                        AnJianOrderBean.QA item = new AnJianOrderBean.QA();
                        item.wenTi = title;
                        for (int j = 0; j < optionList.length(); j++) {
                            String daan = optionList.getString(j); //j_obj.optString("daAn" + j);
                            item.daAn.add(daan);
                        }// for
                        if (item.daAn.size() > 0) {
                            item_list.add(item);
                        }
                    }

                }

                @Override
                protected String getInputParam() throws Exception {
                    return "{}";
                }

                @Override
                protected String getURL() {
                    return "anJian/chaKanAnJianBiao";
                }

                @Override
                protected void runFront2() {
                    updateAnJianQues(item_list);
                }
            });
        }
    }
}
