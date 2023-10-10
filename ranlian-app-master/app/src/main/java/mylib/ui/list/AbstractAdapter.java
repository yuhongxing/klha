
package mylib.ui.list;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.czsy.TheApp;

public abstract class AbstractAdapter<T> extends BaseAdapter {

    protected List<T> mData;

    public void add(T data) {
        if (mData == null) {
            mData = new LinkedList<T>();
        }
        mData.add(data);
        notifyDataSetChanged();
    }

    public void addAll(List<T> data) {
        if (mData == null) {
            mData = new LinkedList<T>();
        }
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setData(List<T> data) {
//        mData = data;
        if (mData == null) {
            mData = new LinkedList<T>();
        }
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();

    }

    public void setData_shangpin(List<T> data) {
        mData = data;
        notifyDataSetChanged();

    }

    public List<T> getData() {
        return mData;
    }

    @Override
    public int getCount() {
        List<T> data = getData();
        return data == null ? 0 : data.size();
    }

    @Override
    public T getItem(int position) {
        List<T> data = getData();
        if (null == data) {
            return null;
        }
        if (position < 0 || position >= data.size()) {
            return null;
        }
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

}
