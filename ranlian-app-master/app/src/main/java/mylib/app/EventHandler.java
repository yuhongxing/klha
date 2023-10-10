package mylib.app;

import android.content.res.Configuration;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mylib.utils.Global;

public class EventHandler {
    public final static String MYTAG = MyLog.DEBUG ? "事件处理 -> " : "";

    protected List<Class<? extends Enum<?>>> getEventClass() {
        List<Class<? extends Enum<?>>> ret = new LinkedList<Class<? extends Enum<?>>>();
        ret.add(Events.class);
        return ret;
    }

    private static Map<Enum<?>, Method> sMethods;

    static {
        sMethods = new HashMap<Enum<?>, Method>();
        EventHandler evtHandler = null;
        try {
            Class<? extends EventHandler> handlerClaz = AndroidApp.sInst.getEventHandlerClass();
            if (handlerClaz != null) {
                evtHandler = handlerClaz.newInstance();
                List<Class<? extends Enum<?>>> evtClasses = evtHandler.getEventClass();

                loop:
                for (Method m : handlerClaz.getMethods()) { // all methods
                    String name = m.getName();
                    if (name.length() < 2 || name.charAt(0) != 'o' || name.charAt(1) != 'n') {

                        continue;
                    }
                    for (Class<? extends Enum<?>> claz : evtClasses) {
                        Enum<?>[] enums = claz.getEnumConstants();

                        for (Enum<?> e : enums) {
                            String fname = e.name();
                            if (fname.equals(name)) {

                                try {
                                    sMethods.put(e, m);
                                    //if (MyLog.DEBUG) {
                                    //	MyLog.LOGD(MYTAG + "加入event方法: " + userAccount);
                                    //}
                                    continue loop;
                                } catch (Exception ex) {
                                    if (MyLog.DEBUG) {
                                        MyLog.LOGW(ex);
                                    }
                                }
                            } // if (fname.equals(userAccount))
                        } // for (Enum<?> e : enums)
                    } // for (Class<? extends Enum<?>>
                } // for (Method m :
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final static Map<Enum<?>, List<WeakReference<EventHandler>>> handlers = new HashMap<Enum<?>, List<WeakReference<EventHandler>>>();

    private static void dumpHandlers() {

    }

    public static void addEventHandler(Enum<?>[] evts, EventHandler handler) {
        synchronized (handlers) {
            if (null == handler || evts == null)
                return;

            for (Enum<?> evt : evts) {
                List<WeakReference<EventHandler>> list = handlers.get(evt);
                if (list == null) {
                    list = new LinkedList<WeakReference<EventHandler>>();
                    handlers.put(evt, list);
                }
                // check dup
                boolean added = false;
                Iterator<WeakReference<EventHandler>> iter = list.iterator();
                while (iter.hasNext()) {
                    WeakReference<EventHandler> ref = iter.next();
                    if (ref == null || ref.get() == null) {
                        iter.remove();
                    } else {
                        if (ref.get() == handler) {
                            added = true;
                            break;
                        }
                    }
                } // while
                if (!added) {
                    list.add(new WeakReference<EventHandler>(handler));
                }
            }

            dumpHandlers();
        }
    }

    public static void removeEventHandler(Enum<?>[] evts, EventHandler handler) {
        if (handler == null) {
            return;
        }
        synchronized (handlers) {
            out:
            for (Enum<?> evt : evts) {
                List<WeakReference<EventHandler>> list = handlers.get(evt);
                if (list == null) {
                    continue;
                }
                Iterator<WeakReference<EventHandler>> iter = list.iterator();
                while (iter.hasNext()) {
                    WeakReference<EventHandler> ref = iter.next();
                    if (ref == null || ref.get() == null) {
                        iter.remove();
                    } else if (ref.get() == handler) {
                        iter.remove();
                        break out;
                    }
                } // while
            } // for

            dumpHandlers();
        }
    }

    public static boolean notifyEvent(Enum<?> evt, Object... paras) {
        synchronized (handlers) {

            List<WeakReference<EventHandler>> set = handlers.get(evt);
            if (set == null) {
                return false;
            }
            boolean handled = false;
            Iterator<WeakReference<EventHandler>> iter = set.iterator();
            while (iter.hasNext()) {
                WeakReference<EventHandler> ref = iter.next();
                final EventHandler h = ref == null ? null : ref.get();
                if (h == null) {
                    iter.remove();
                } else {
                    handled |= EventHandler.doNotify(evt, h, paras);
                }
            } // while
            return handled;
        }
    }

    private static boolean doNotify(final Enum<?> evt, final EventHandler handler, final Object... args) {
        synchronized (sMethods) {
            final Method method = sMethods.get(evt);
            if (method == null) {
                MyLog.LOGW("no such callback: " + evt.toString());
                return false;
            }
            // do all callback in main thread
            AndroidApp.sHandler.post(new Runnable() {

                @Override
                public void run() {
                    try {
                        long t1 = System.currentTimeMillis();
                        {
                            method.invoke(handler, args);
                        }
                        long t2 = System.currentTimeMillis();
                        if (t2 - t1 > Global.EVT_TIMEOUT) {
                            MyLog.LOGW(String.format("[evt: %d]: %s", (t2 - t1), evt.name()));
                        }
                    } catch (Throwable t) {
                        MyLog.LOGE(String.format("Call %s err", evt.toString()), t);
                        throw new RuntimeException(t);
                    }
                }

            });
            return true;
        }
    }

    public static enum Events {
        onLocationChanged, onLocationError,
        onToggleScreen, onConnChanged,
    }

    /////************************** callback methods
    public void onHomePressed() {
    }

    public void onConfigurationChanged(Configuration old, Configuration newC) {
    }

    public void onModulesChanged() {
    }

    public void onSendMail(int mailid, boolean ok) {
    }

    public void onLocationChanged(double lng, double lat) {
    }

    public void onLocationError(String errString) {
    }

    public void onToggleScreen(boolean on) {
    }

    public void onConnChanged() {
    }


}
