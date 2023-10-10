package com.czsy.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.czsy.CZBackTask;
import com.czsy.CZNetUtils;
import com.czsy.Constant;
import com.czsy.TheApp;
import com.czsy.android.R;
import com.czsy.bean.IDNameBean;
import com.czsy.bean.LoginUser;
import com.czsy.bean.PageInfoBean;
import com.czsy.push.MessageBean;
import com.czsy.push.PushBean;
import com.czsy.push.PushManager;
import com.czsy.ui.gongyingzhan.GYZZhanZhangMain;
import com.czsy.ui.sqg.PVMainSQG;
import com.czsy.ui.sqg.PVSQGApp;
import com.czsy.ui.sqg.PVSQGApp1;
import com.czsy.ui.sqg.PVWeiXiuList;
import com.czsy.ui.weixiuyuan.PVWeixiuMain;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mylib.app.BackFrontTask;
import mylib.app.BackTask;
import mylib.ui.list.AbstractAdapter;
import mylib.utils.FileUtils;
import mylib.utils.Utils;

public class PVPushMsg extends AbsPVBase implements AdapterView.OnItemClickListener, View.OnClickListener {

    final TextView tv_msg_cnt;
    TextView tv_right;
    final AbsPVBase pv_abs; //消息操作分类： sqg-->送气工的；wxy-->维修员; 供应站站长

//    private List<MessageBean> datas = new ArrayList<>();

    protected PageInfoBean page_info;
    int _totalItemCount;// 总数量；
    int _lastVisibleItem;// 最后一个可见的item；

    public PVPushMsg(MainActivity a, AbsPVBase sqg, TextView tv_msg_cnt) {
        super(a);
        this.tv_msg_cnt = tv_msg_cnt;
        this.pv_abs = sqg;
    }


    protected int getNextPage() {
        return page_info == null ? 1 : page_info.curPage + 1;
    }

    private ListView list_view;
    private AbstractAdapter<MessageBean> adapter = new AbstractAdapter<MessageBean>() {
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            ViewHolder vh;
            if (view == null) {
                view = View.inflate(viewGroup.getContext(), R.layout.item_pushmsg, null);
                vh = new ViewHolder(view);
            } else {
                vh = (ViewHolder) view.getTag();
            }
            MessageBean data = adapter.getItem(i);
            bindItem(i, data, vh);
            return vh.root;

        }
    };

    @Override
    protected void createMainView(Context ctx) {
        mMainView = View.inflate(act, R.layout.pv_pushmsg, null);
        tv_right = mMainView.findViewById(R.id.tv_right);
        tv_right.setOnClickListener(this);
        list_view = mMainView.findViewById(R.id.list_view);
        list_view.setEmptyView(mMainView.findViewById(R.id.img_empty));
        list_view.setAdapter(adapter);
        list_view.setOnItemClickListener(this);

//        list_view.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
////      totalItemCount == lastVisibleItem相等时说明滑到了底部
//                if (_totalItemCount == _lastVisibleItem && scrollState == SCROLL_STATE_IDLE) {
//                    Log.d("xing", "滑到了底部");
//
//                    reload();
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                _lastVisibleItem = firstVisibleItem + visibleItemCount;
//                _totalItemCount = totalItemCount;
//            }
//        });

    }

    public void reload() {

//        act.showProgress();
        BackTask.post(new CZBackTask(act) {
            List<MessageBean> datas = new ArrayList<>();
            int unread = 0;

            @Override
            protected void parseResult(JSONObject jdata) throws Exception {
                JSONArray result = jdata.getJSONArray("result");
//                page_info = Constant.gson.fromJson(
//                        jdata.getJSONObject("pageVO").toString(), PageInfoBean.class);

//                if (result.length() == 0) {
//                    Utils.toastSHORT("没有更多了");
//                } else {

                    for (int i = 0; i < result.length(); i++) {
                        MessageBean gp = Constant.gson.fromJson(result.getJSONObject(i).toString(),
                                MessageBean.class);
                        datas.add(gp);

                        if (!gp.isRead) {
                            unread++;
                        }
                    }

//                }

            }

            @Override
            protected String getInputParam() throws Exception {
                return null;
            }

            @Override
            protected String getURL() {
                return "notSendMsg/chaXun/80/1";
//                return "notSendMsg/chaXun/10/" + getNextPage();
            }

            @Override
            protected void runFront2() {
                if (unread <= 0) {
                    tv_msg_cnt.setVisibility(View.GONE);
                    tv_right.setVisibility(View.GONE);

                } else {
                    tv_msg_cnt.setVisibility(View.VISIBLE);

                    tv_right.setVisibility(View.VISIBLE);

                    tv_msg_cnt.setText(unread > 99 ? "99+" : unread < 10 ? " " + unread + " " : String.valueOf(unread));
                }
                adapter.setData(datas);
            }
        });

    }

    /**
     * @param isWeixiu
     */
    public void reload(final boolean isWeixiu) {

        BackTask.post(new CZBackTask(act) {
            List<MessageBean> datas = new ArrayList<>();

            @Override
            protected void parseResult(JSONObject jdata) throws Exception {
                JSONArray result = jdata.getJSONArray("result");
                for (int i = 0; i < result.length(); i++) {
                    MessageBean gp = Constant.gson.fromJson(result.getJSONObject(i).toString(),
                            MessageBean.class);
                    datas.add(gp);
                }

            }

            @Override
            protected String getInputParam() throws Exception {
                return null;
            }

            @Override
            protected String getURL() {
                return "notSendMsg/chaXun/100/1";
            }

            @Override
            protected void runFront2() {
                int unread = datas.size();
                if (unread <= 0) {
                    tv_msg_cnt.setVisibility(View.GONE);
                    tv_right.setVisibility(View.GONE);

                } else {
                    tv_msg_cnt.setVisibility(View.VISIBLE);

                    tv_right.setVisibility(View.VISIBLE);

                    tv_msg_cnt.setText(unread > 99 ? "99+" : String.valueOf(unread));
                }

                if (isWeixiu) {
                    tv_msg_cnt.setVisibility(View.GONE);
                }
                adapter.setData(datas);
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MessageBean pb = adapter.getItem(i);
        Log.d("xing", "类型：" + pb.getTypeString() + pb.dingDanType);

        if (pv_abs instanceof PVSQGApp1) {//配送员
            PushManager.get().readMsg(pb, (PVSQGApp1) pv_abs, this);
            Log.d("类型", "PVSQGApp");
        } else if (pv_abs instanceof PVWeixiuMain) {//维修员
            Log.d("类型", "PVWeiXiuList");
            PushManager.get().readMsg(pb, pv_abs, this);
            Log.d("类型", "PVWeiXiuList");
        } else if (pv_abs instanceof GYZZhanZhangMain) {//供应站站长
            PushManager.get().readMsg(pb, pv_abs, this);
            Log.d("类型", "GYZZhanZhangMain");
        }

        PushManager.get().UpdataMsgStatus(pb.id, pb.dingDanHao, 0);
    }

    @Override
    public void onClick(View view) {
        if (view == tv_right) {
            PushManager.get().UpdataMsgStatus(0, "0", 1);
//            if (page_info == null) {
//                page_info = new PageInfoBean();
//                page_info.curPage = 0;
//                page_info.pageSize = 10;
//            }
//            page_info.curPage = 0;
            reload();

        }
    }

    @Override
    public void onAttach(boolean firstShow) {
        Log.d("xing", "show pushMsg11");
        super.onAttach(firstShow);
//        if (page_info == null) {
//            page_info = new PageInfoBean();
//            page_info.curPage = 0;
//            page_info.pageSize = 10;
//        }
//        page_info.curPage = 0;
        reload();
    }

    private void bindItem(int i, MessageBean bean, ViewHolder viewHolder) {

        if (bean.type == 2){
            viewHolder.icon_cuidan.setVisibility(View.VISIBLE);
        }else {
            viewHolder.icon_cuidan.setVisibility(View.GONE);
        }

        viewHolder.tv_type.setText(bean.getTypeString());
        viewHolder.tv_time.setText(bean.chuangJianShiJian);
        viewHolder.tv_order.setText("订单编号:" + bean.dingDanHao);
        if (bean.isRead) {
            viewHolder.tv_status.setText("已读");
            viewHolder.tv_status.setTextColor(Color.parseColor("#FFBFBFBF"));
        } else {
            viewHolder.tv_status.setText("未读");
            viewHolder.tv_status.setTextColor(Color.parseColor("#FF238CFC"));
        }

    }

    class ViewHolder {
        public View root;
        TextView tv_type, tv_time, tv_order, tv_status;
        ImageView icon_cuidan;

        public ViewHolder(View root) {
            this.root = root;
            tv_type = root.findViewById(R.id.tv_type);
            tv_time = root.findViewById(R.id.tv_time);
            tv_order = root.findViewById(R.id.tv_order);
            tv_status = root.findViewById(R.id.tv_status);
            icon_cuidan = root.findViewById(R.id.icon_cuidan);
            root.setTag(this);
        }
    }
}
