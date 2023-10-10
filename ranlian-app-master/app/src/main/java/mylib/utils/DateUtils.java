package mylib.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static boolean shijian(String time1, String time2) {
        Date dateStart, dateEnd;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");

        try {
            dateStart = dateFormat.parse(time1);
            dateEnd = dateFormat.parse(time2);
            if (dateEnd.getTime() > dateStart.getTime()) {
                return true;
            } else {
                Utils.toastSHORT("上次检验日期必须大于生产日期");
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();

            Utils.toastSHORT("数据格式有误！");
            return false;
        }
    }

    public static boolean shijian1(String time1, String time2) {
        Date dateStart, dateEnd;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            dateStart = dateFormat.parse(time1);
            dateEnd = dateFormat.parse(time2);
            if (dateEnd.getTime() >= dateStart.getTime()) {
                return true;
            } else {
                Utils.toastSHORT("报废日期必须大于等于有效日期");
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();

            Utils.toastSHORT("数据格式有误！");
            return false;
        }
    }

    /**
     * 时间检测-判断选择的时间是否在今天之前（包括今天）
     *
     * @param time 选择的时间
     * @return
     */
    public static boolean shijianPanduan(String time, String format) {
        Date dateSelete;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        try {
            dateSelete = dateFormat.parse(time);
            long timeGetTime = new Date().getTime();

            if (timeGetTime >= dateSelete.getTime()) {
                return true;
            } else {
                if (format.equals("yyyy-MM")) {
                    Utils.toastLONG("选择的时间必须在本月或本月之前");

                } else {
                    Utils.toastLONG("选择的时间必须在今天或今天之前");

                }
                return false;
            }


        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 获取当前时间 例：20220215010203
     *
     * @return
     */
    public static String nowTime() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);

        return str;
    }

}
