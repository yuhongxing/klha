
package mylib.app;

import android.os.AsyncTask;
import android.util.Log;

import mylib.utils.Global;

import java.lang.ref.WeakReference;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class BackTask implements BackFrontTask {
    //static private BackFrontAsyncTask sAsyncTask = new BackFrontAsyncTask();

    @Override
    public void runFront() {
    }

    abstract public void runBack();

    public static void post(final BackFrontTask task) {
        if (task == null) {
            return;
        }
        BackFrontAsyncTask t = new BackFrontAsyncTask();
//		t.execute();
        t.executeOnExecutor(
                BackFrontAsyncTask.threadPool,
                new WeakReference<BackFrontTask>(task));
    }

    private static class BackFrontAsyncTask
            extends AsyncTask<WeakReference<BackFrontTask>, Integer, WeakReference<BackFrontTask>[]> {

        final static Executor threadPool = new ThreadPoolExecutor(4, 16,
                5, TimeUnit.MINUTES,
                new ArrayBlockingQueue<Runnable>(100));

        BackFrontTask task = null;

        @Override
        protected void onPostExecute(WeakReference<BackFrontTask>[] result) {
            if (result == null) {
                return;
            }

//            BackFrontTask task = result.get();
            if (task != null) {
                long t1 = System.currentTimeMillis();
                task.runFront();
                long t2 = System.currentTimeMillis();

                if (MyLog.DEBUG && t2 - t1 > Global.EVT_TIMEOUT) {
                    MyLog.LOGW(String.format("[front: %d] %s", (t2 - t1), task.toString()));
                }
            }

//					Log.d("NetWork","onPostExecute  result数量-->"+result.length);
//			for (WeakReference<BackFrontTask> task_ref : result) {
//				BackFrontTask task = task_ref == null ? null : task_ref.get();
//				cut++;
//				Log.d("NetWork","onPostExecute  cut-->"+cut);
//				if (task == null) {
//					Log.d("NetWork","onPostExecute  task == null"+cut);
//					continue;
//				}
//				long t1 = System.currentTimeMillis();
//				task.runFront();
//				long t2 = System.currentTimeMillis();
//				if (MyLog.DEBUG && t2 - t1 > Global.EVT_TIMEOUT) {
//					MyLog.LOGW(String.format("[front: %d] %s", (t2 - t1), task.toString()));
//				}
//			}
        }

        @Override
        protected WeakReference<BackFrontTask>[] doInBackground(WeakReference<BackFrontTask>... params) {

            if (params == null) {
                return null;
            }
            for (WeakReference<BackFrontTask> task_ref : params) {
                task = task_ref == null ? null : task_ref.get();

//                Log.d("NetWork", "doInBackground  cut-->" + cut);
                if (task == null) {
//                    Log.d("NetWork", "doInBackground  task == null" + cut);
                    continue;
                }
                long t1 = System.currentTimeMillis();
                task.runBack();
                long t2 = System.currentTimeMillis();
                if (MyLog.DEBUG) {
                    MyLog.LOGD(String.format("[back: %d] %s", (t2 - t1), task.toString()));
                }
            }
//            task = params[0].get();
            return params;
        }

    }
}
