package mylib.utils;

import com.czsy.android.R;
import com.czsy.bean.GangPingBean;
import com.czsy.bean.LoginUser;

/**
 * 厂站角色批量操作时的判断工具
 */
public class CZUtlis {


    /**
     * 判断此钢瓶是否可操作
     *
     * @param gb
     * @param lu
     * @param isNext 是否判断场站id
     * @return
     */
    public static boolean isok(GangPingBean gb, LoginUser lu, boolean isNext) {

        if (isNext) {
            if (gb.status == 1 && gb.changZhanId == lu.orgId && gb.zuiHouWeiZhi == 1) {
                return true;
            }

        } else {
            if (gb.status == 1 && gb.zuiHouWeiZhi == 0 && gb.changZhanId == 0) {//
                return true;
            }
        }

        return false;
    }

    /**
     * 判断此钢瓶的状态
     *
     * @param gb
     * @param lu
     * @return
     */
    public static String toMsg(GangPingBean gb, LoginUser lu, boolean isNext) {


        if (isNext){
            if (gb.changZhanId != lu.orgId) {
                return "气瓶不属于此单位！";
            } else if (gb.zuiHouWeiZhi != 1) {
                return "此气瓶不在场站";
            } else if (gb.status != 1) {
                return gb.getStatusName();
            }
        }else{
            if (gb.changZhanId == 0) {
                return "调入的气瓶必须没有厂站所属";
            }
        }

        return "当前气瓶不能操作";
    }

}
