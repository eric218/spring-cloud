package cn.wimetro.unit;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class NumberUtil {
	
	
	public static String getTradeNo(int tradeNo) {
        //得到一个NumberFormat的实例
        NumberFormat nf = NumberFormat.getInstance();
        //设置是否使用分组
        nf.setGroupingUsed(false);
        //设置最大整数位数
        nf.setMaximumIntegerDigits(6);
        //设置最小整数位数    
        nf.setMinimumIntegerDigits(6);
        
        //输出测试语句
       return nf.format(tradeNo);
    }

    public static String getEightString(BigDecimal dealAmount) {

        long amount = dealAmount.multiply(new BigDecimal(100)).intValue();
        //得到一个NumberFormat的实例
        NumberFormat nf = NumberFormat.getInstance();
        //设置是否使用分组
        nf.setGroupingUsed(false);
        //设置最大整数位数
        nf.setMaximumIntegerDigits(8);
        //设置最小整数位数
        nf.setMinimumIntegerDigits(8);

        //输出测试语句
        return nf.format(amount);
    }

    /**
     * 以0补全
     *
     * @param amount 整数
     * @param digit 几位补全
     * @return
     */
    public static String getNumStringWithZero(int amount, int digit) {
        //得到一个NumberFormat的实例
        NumberFormat nf = NumberFormat.getInstance();
        //设置是否使用分组
        nf.setGroupingUsed(false);
        //设置最大整数位数
        nf.setMaximumIntegerDigits(digit);
        //设置最小整数位数
        nf.setMinimumIntegerDigits(digit);

        //输出测试语句
        return nf.format(amount);
    }

    //乘以100
    public static String countYLMoney(BigDecimal num){
        BigDecimal b1 = num;
        b1 = b1.multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_UP);
        return b1.intValue()+"";
    }

    //除以100
    public static BigDecimal getYLMoneyToNum(String num){
        BigDecimal b1 = new BigDecimal(num);
        b1 = b1.divide(new BigDecimal(100));
        return b1;
    }

//	public static void main(String[] args) {
//		String date = DateUtil.getDateTimeNew().substring(4,8);
//		System.out.println(date);
//        String s = getTradeNo(1233333);
//        System.out.println(s);
//
//        BigDecimal n1 = new BigDecimal("0510");
////        n1 = n1.multiply(new BigDecimal(100));
////        n1 = n1.subtract(new BigDecimal(100));
//        System.out.println(n1.divide(new BigDecimal(100)));
//	}

}
