package cn.wimetro.pos;

/**

 *   ISO8583工具类, 定义了相关的常量和公共方法
 */

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ISOFTemp
{
	//测试用密钥及时间
	private static final String keyfileName = "./8583_keystore";
	public static byte[] gMACK=new String("ABB9799F38257D8F543F7C7595EF5264").getBytes();
	public static byte[] gMACK_bak=new String("ABB9799F38257D8F543F7C7595EF5264").getBytes();
	public static String gDateTime = "20181116154658";
	
	//交易用流水号、批次号
	private static final String transfileName = "./8583_transfile";
	public static String gTrace = "000001";
	public static String gBatch = "000001";
	
	//8583报文固定参数
	private static final String configfileName = "./8583_config";
//	public static String gHSMIP		="172.16.100.112";
//	public static String gHSMPORT	="6666";
//	public static String gTPDU		="6005040000";
//	public static String gHEAD		="603200320000";
//	public static String gTerminal_no = "00000001";		//041:终端编号,【根据实际填写】
//	public static String gMerct_no 		= "102420170110001";  //042:商户代码,【根据实际填写】
//	public static String gSys_no		 	= "M0000607";			//043:地铁前置系统标识,【根据实际填写】
//	public static String gMoney_code 	= "156";  			//049:交易货币代码,固定值'156'

	public static String gHSMIP		="172.16.100.57";
	public static String gHSMPORT	="6666";
	public static String gTPDU		="6005210000";
	//public static String gHEAD		="603200320000";
	public static String gHEAD		="000000000000";
	public static String gTerminal_no = "00000001";		//041:终端编号,【根据实际填写】
	public static String gMerct_no 		= "102320211020011";  //042:商户代码,【根据实际填写】
	public static String gSys_no		 	= "M0000607";			//043:地铁前置系统标识,【根据实际填写】
	public static String gMoney_code 	= "156";  			//049:交易货币代码,固定值'156'

	public static final byte[] gSecurety = new String("061").getBytes();    	//053:安全控制信息,固定值'061'


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
	public static final int BCDF_LF=3;

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

	/**
	 * 初始化配置文件，文件存在时读数据，不存在时创建默认值
	 */
	public static void envInit()
	{
		try
		{
			String encoding = "UTF-8";
			File envFile = new File(configfileName);
			if(envFile.isFile()&&envFile.exists())
			{
				InputStreamReader inptReader = new  InputStreamReader(new FileInputStream(envFile),encoding);
				BufferedReader bufferReader = new BufferedReader(inptReader);
				String lineTxt = null;
				int i =0;
				gHSMIP 				= bufferReader.readLine();
		  		gHSMPORT 			= bufferReader.readLine();
		  		gTPDU 				= bufferReader.readLine();
		  		gHEAD 				= bufferReader.readLine();
		  		gTerminal_no 		= bufferReader.readLine();
				gMerct_no 			= bufferReader.readLine(); 
				gSys_no 			= bufferReader.readLine();
				gMoney_code 		= bufferReader.readLine();
				/*System.out.println("初始化参数：");
				System.out.println(gHSMIP);
				System.out.println(gHSMPORT);
				System.out.println(gTPDU);
				System.out.println(gHEAD);
				System.out.println(gTerminal_no);
				System.out.println(gMerct_no);
				System.out.println(gSys_no);
				System.out.println(gMoney_code);*/
				bufferReader.close();
			}
			else
			{
				try
				{
					envFile.createNewFile();
					FileWriter fw  = new FileWriter(envFile.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					
					bw.write(gHSMIP);
					bw.newLine();
					
					bw.write(gHSMPORT);
					bw.newLine();
					
					bw.write(gTPDU);
					bw.newLine();
					
					bw.write(gHEAD);
					bw.newLine();
					
					bw.write(gTerminal_no);
					bw.newLine();
					
					bw.write(gMerct_no);
					bw.newLine();
					
					bw.write(gSys_no);
					bw.newLine();
					
					bw.write(gMoney_code);
					bw.newLine();
					
					bw.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();	
				}
			}
		}
		catch(Exception e)
		{
				e.printStackTrace();	
		}	
		
		try
		{
			String encoding = "UTF-8";
			File keyFile = new File(keyfileName);
			if(keyFile.isFile()&&keyFile.exists())
			{
				InputStreamReader confReader = new  InputStreamReader(new FileInputStream(keyFile),encoding);
				BufferedReader bufferReader = new BufferedReader(confReader);
				String lineTxt = null;
				int i =0;
				gMACK = HexStr_Bytes(bufferReader.readLine(),0);
				gMACK_bak = HexStr_Bytes(bufferReader.readLine(),0);
				gDateTime = bufferReader.readLine();
				System.out.println("初始化key：");
				//System.out.println(Bytes_HexStr(gMACK));
				//System.out.println(Bytes_HexStr(gMACK_bak));
				//System.out.println(gDateTime);

				bufferReader.close();
			}
			else
			{
				try
				{
					keyFile.createNewFile();
					FileWriter fw  = new FileWriter(keyFile.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
									
					bw.write(new String(gMACK));
					bw.newLine();
					             
					bw.write(new String(gMACK_bak));
					bw.newLine();
					
					bw.write(gDateTime);
					bw.newLine();
					
					bw.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();	
				}
			}
		}
		catch(Exception e)
		{
				e.printStackTrace();	
		}
		
		//流水号 批次号
		try
		{
			String encoding = "UTF-8";
			File transFile = new File(transfileName);
			if(transFile.isFile()&&transFile.exists())
			{
				InputStreamReader transReader = new  InputStreamReader(new FileInputStream(transFile),encoding);
				BufferedReader transBufferReader = new BufferedReader(transReader);
				String lineTxt = null;
				int i =0;
				gTrace = transBufferReader.readLine();;
				gBatch = transBufferReader.readLine();;
				System.out.println("初始化交易数据：");


				transBufferReader.close();
			}
			else
			{
				try
				{
					transFile.createNewFile();
					FileWriter fw  = new FileWriter(transFile.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
									
					bw.write(gTrace);
					bw.newLine();
					             
					bw.write(gBatch);
					bw.newLine();

					bw.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();	
				}
			}
		}
		catch(Exception e)
		{
				e.printStackTrace();	
		}	
	} 
	
	public static void updateTrace(String strTrace)
	{
		String encoding = "UTF-8";
		File transFile = new File(transfileName);
		try
		{
			InputStreamReader inptReader = new  InputStreamReader(new FileInputStream(transFile),encoding);
			BufferedReader bufferReader = new BufferedReader(inptReader);
			String lineTxt = null;
			int i =0;
	  	gTrace = bufferReader.readLine();
	  	gBatch = bufferReader.readLine();
			bufferReader.close();
			
			FileWriter fw  = new FileWriter(transFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			gTrace = strTrace;
			bw.write(gTrace);
			bw.newLine();
			bw.write(gBatch);
			bw.newLine();
			
			bw.close();
			
		}
		catch(Exception e)	
		{
				e.printStackTrace();
		}
	}
	
	public static void updateBatch(String strBatch)
	{
		String encoding = "UTF-8";
		File transFile = new File(transfileName);
		try
		{
			InputStreamReader inptReader = new  InputStreamReader(new FileInputStream(transFile),encoding);
			BufferedReader bufferReader = new BufferedReader(inptReader);
			String lineTxt = null;
			int i =0;
	  	gTrace = bufferReader.readLine();
	  	gBatch = bufferReader.readLine();
			bufferReader.close();
			
			FileWriter fw  = new FileWriter(transFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(gTrace);
			bw.newLine();
			
			gBatch = strBatch;
			bw.write(gBatch);
			bw.newLine();
			
			bw.close();
			
		}
		catch(Exception e)	
		{
				e.printStackTrace();
		}
	}
	/**
	 * 修改key，其他参数默认不修改
	 */
	public static void updateKey(byte[] mKEY)
	{
		System.out.println("***updateKey***");
		System.out.println(ISOFTemp.Bytes_HexStr(mKEY));
		String encoding = "UTF-8";
		File keyFile = new File(keyfileName);
		try
		{
			InputStreamReader inptReader = new  InputStreamReader(new FileInputStream(keyFile),encoding);
			BufferedReader bufferReader = new BufferedReader(inptReader);
			String lineTxt = null;
			int i =0;
		  	gMACK = HexStr_Bytes(bufferReader.readLine(),0); 
		  	gMACK_bak = HexStr_Bytes(bufferReader.readLine(),0); 
		  	gDateTime = bufferReader.readLine();
			bufferReader.close();
			System.out.println(ISOFTemp.Bytes_HexStr(gMACK));
			System.out.println(ISOFTemp.Bytes_HexStr(gMACK_bak));
			
			FileWriter fw  = new FileWriter(keyFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			System.arraycopy(gMACK,0,gMACK_bak,0,16);	
			System.arraycopy(mKEY,0,gMACK,0,16);
			bw.write(Bytes_HexStr(gMACK));
			bw.newLine();
			bw.write(Bytes_HexStr(gMACK_bak));
			bw.newLine();
			
			SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss");
			gDateTime = dt.format(new Date());
			bw.write(gDateTime);
			
			bw.close();
			
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
	 		
	 		long between_secs = (time2 - time1)/1000;
	 		System.out.println("时间差:");
	 		System.out.println(String.valueOf(gDateTime));	
	 		System.out.println(String.valueOf(endTime));	
	 		System.out.println(String.valueOf(between_secs));	
	 		
	 		if(between_secs <= 180)//窗口期时间
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
        if (hexString == null || hexString.equals("")) 
        {
      	   return null;
        }

		String hexStr="";
		if( (hexString.length()%2) == 1 )
		{
			if( ISOFTemp.BCDF_L == piBCDFill )
			{
				hexStr = hexString+"0";    //左靠BCD
			}
			if( ISOFTemp.BCDF_LF == piBCDFill )
			{
				hexStr = hexString+"F";    //左靠BCD
			}
			if( ISOFTemp.BCDF_R == piBCDFill )
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
