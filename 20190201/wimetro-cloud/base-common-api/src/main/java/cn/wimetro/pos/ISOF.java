package cn.wimetro.pos;

/**

 *   ISO8583工具类, 定义了相关的常量和公共方法
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ISOF
{
	//测试用密钥及时间
	public  static byte[] gMACK;
	public  static byte[] gMACK_bak;
	public  static String gDateTime;


	public  static String gHSMIP;
	public  static String gHSMPORT;
	public  static String gTPDU;
	public  static String gHEAD;
	//041:终端编号,【根据实际填写】
	public  static String gTerminal_no;
	//042:商户代码,【根据实际填写】
	public  static String gMerct_no;
	//043:地铁前置系统标识,【根据实际填写】
	public  static String gSys_no;
	//049:交易货币代码,固定值'156'
	public  static String gMoney_code;



	//域的个数
	public static final int iFields=64;

	//交易包用途
	public static final int USE_RQ=1;   //请求包
	public static final int USE_RS=1;   //响应包

	//域存在标志(只对1-128域有用)
	public static final int MAP_N=0;    //域不存在
	public static final int MAP_Y=1;    //域存在

	//长度类型定义
	public static final int FIXED=0;    //定长类型
	public static final int LVAR=1;     //1字节变长
	public static final int LLVAR=2;    //2字节变长
	public static final int LLLVAR=3;   //3字节变长

	//打印数据类型定义
	public static final int HEXSTR=1;   //十六进制字符串形式
	public static final int ASCSTR=2;   //ASCII字符串形式

	//ASC/BCD
	public static final int BCD_N=1;    //不压缩
	public static final int BCD_Y=2;    //压缩

	//BCD补齐方向
	public static final int BCDF_N=0;   //不补齐
	public static final int BCDF_L=1;   //左靠BCD
	public static final int BCDF_R=2;   //右靠BCD

	//填充
	public static final int FILL_N=0;   //不填充
	public static final int FILL_L=1;   //左边填充
	public static final int FILL_R=2;   //右边填充

	//数据是否格式化
	public static final int FORMAT_N=0; //未格式化的数据
	public static final int FORMAT_Y=1; //已格式化的数据

	//打印时是否每2个HexStr打一个空格
	public static final int SPACE_N=0;  //不打印分割空格
	public static final int SPACE_Y=1;  //要打印分割空格

	//错误代码
	public static final int ERR_DATA_TOO_LEN    = -101;   //数据超长错误
	public static final int ERR_VAR_NULL        = -102;   //变量为空
	public static final int ERR_DATA_TOO_SHORT  = -103;   //数据长度不足
	public static final int ERR_MAC             = -104;   //MAC校验错
	public static final int ERR_MAC_EXCEPTION   = -105;   //MAC计算异常
	public static final int ERR_CHECKVALUE		= -106;		//checkvalue校验错
	public static final int ERR_EXCHANGEKEY		= -107;		//重置密钥窗口期内

	private static Logger log = LoggerFactory.getLogger(ISOF.class);


	/**
	 * 修改key，其他参数默认不修改
	 */
	public static void updateKey(byte[] mKEY)
	{
		log.info("***updateKey begin ***");
		log.info(ISOF.Bytes_HexStr(mKEY));
		try
		{
			System.arraycopy(gMACK,0,gMACK_bak,0,16);
			System.arraycopy(mKEY,0,gMACK,0,16);

			SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");
			gDateTime = dt.format(new Date());

			log.info("***updateKey successful ***");
			
		}
		catch(Exception e)	
		{
				e.printStackTrace();
		}
	}
	
	/**
	 * 窗口期180秒
	 */
	 public static boolean calSec()
	 {
	 		SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");
	 		Calendar cal = Calendar.getInstance();

	 		SimpleDateFormat dtend = new SimpleDateFormat("yyyyMMddHHmmss");
			String endTime = dtend.format(new Date());
			
	 		long time1 = 0;
	 		long time2 = 0;
	 		
	 		try
	 		{
	 			cal.setTime(dt.parse(gDateTime));	
	 			time1 = cal.getTimeInMillis();
	 			cal.setTime(dt.parse(endTime));	
	 			time2 = cal.getTimeInMillis();
	 			
	 		}
	 		catch(Exception e)
	 		{
	 			e.printStackTrace();	
	 		}
	 		
	 		long betweenSecs = (time2 - time1)/1000;
	 		System.out.println("时间差:");
	 		System.out.println(String.valueOf(gDateTime));	
	 		System.out.println(String.valueOf(endTime));	
	 		System.out.println(String.valueOf(betweenSecs));
	 		
	 		if(betweenSecs <= 180)//窗口期时间
	 		{
	 			return true;
	 		}
	 		
	 		return false;
	 }
	
	/**
	 * 修改其他参数，暂不实现
	 */
	public static void updateEnv()
	{
		try
		{
			
		}
		catch(Exception e)	
		{
				e.printStackTrace();
		}
	}
	 
	private static byte charToByte(char c)
	{
		return (byte) "0123456789ABCDEF".indexOf(c);
    }

	/**
	 *   将Byte数组, 转换成十六进制字符串
	 * @param  src, 需要转换的Byte数组
	 * @return 转换后得到的十六进制字符串
	 */
	public static String Bytes_HexStr(byte src[])
	{
       StringBuilder stringBuilder = new StringBuilder("");
       if (src == null || src.length <= 0) 
       {
      	  return null;
       }

       for (int i = 0; i < src.length; i++)
       {
          int v = src[i] & 0xFF;
          String hv = Integer.toHexString(v);
          if (hv.length() < 2)
          {
        	 stringBuilder.append(0);
          }

          stringBuilder.append(hv);
       }
       return stringBuilder.toString().toUpperCase();
	}

	/*
	 *   将成十六进制字符串, 转换Byte数组
	 * @param  hexString, 需要转换的十六进制字符串
	 * @param  piBCDFill, 长度不足时, 前补0还是后补0
	 * @return 转换后得到的Byte数组
	 */
    public static byte[] HexStr_Bytes(String hexString, int piBCDFill)
    {
        if (hexString == null || ("").equals(hexString))
        {
      	   return null;
        }

		String hexStr="";
		if( (hexString.length()%2) == 1 )
		{
			if( ISOF.BCDF_L == piBCDFill )
			{
				hexStr = hexString+"0";    //左靠BCD
			}
			if( ISOF.BCDF_R == piBCDFill )
			{
				hexStr = "0"+hexString;    //右靠BCD
			}
		}
		else
		{
			hexStr = hexString;
		}

        hexStr = hexStr.toUpperCase();
        int length = hexStr.length() / 2;
        char[] hexChars = hexStr.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++)
        {
           int pos = i * 2;
           d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
}
