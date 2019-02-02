package cn.wimetro.unit;


import cn.wimetro.constants.YlConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author brofe
 * @function 日期处理工具类
 * @date 2008-2-20
 */
public class DateUtil {

    private static Calendar cale = Calendar.getInstance();

    private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * 获得服务器当前日期，以格式为：YYYYMMDDHHMMSS的日期字符串形式返回
     * @author wangwei
     * @date  2019/01/08
     * @return String
     **/
    public static String getDateTimeNew() {
        try {
            cale = Calendar.getInstance();
            return sdf3.format(cale.getTime());
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获得服务器当前日期，以格式为：YYYYMMDDHHMMSS的日期字符串形式返回
     * @author wangwei
     * @date  2019/01/08
     * @return Date
     **/
    public static Date getDateFromString(String dateString) {
        Date date = null;
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            date = sdf3.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获得服务器当前日期，以格式为：YYYYMMDDHHMMSS的日期字符串形式返回
     * @author wangwei
     * @date  2019/01/08
     * @return String
     **/
    public static String getStringFromDate(Date date) {
        String dateString = null;
        SimpleDateFormat sdf4 = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            dateString = sdf4.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString;
    }


    /**
     * 获得服务器当前日期，以格式为：YYYYMMDDHHMMSS的日期字符串形式返回
     * @author wangwei
     * @date  2019/01/08
     * @return Date
     **/
    public static Date getOperationDate(Date dealDate){
        //武汉地铁的运营日是04:00到T+1 的04:00，匹配按照运营日匹配。
        //交易时间：2018-11-28 04:00:00——2018-11-29 04:00:00  对应的运营日2018-11-28
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf4 = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = sdf4.format(dealDate);
        String dateTinm ="";
        Date date = null;
        int  time = Integer.parseInt(dateString.substring(8,14));
        if(time>= YlConstants.OPERATE_TIME){
            dateTinm = dateString.substring(0,8);
        }else{
            dateTinm = Integer.parseInt(dateString.substring(0,8))-1 +"";
        }
        try {
            date = sdf3.parse(dateTinm);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
        }

        return date;
    }

    /**
     * 获得服务器当前日期，以格式为：YYYYMMDDHHMMSS的日期字符串形式返回
     * @author wangwei
     * @date  2019/01/08
     * @return Date
     **/
    public static long getMinutesBetween(Date d1 ,Date d2){
        long diff = d1.getTime() - d2.getTime();
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long min = diff % nd % nh / nm;
        return min;
    }
}
