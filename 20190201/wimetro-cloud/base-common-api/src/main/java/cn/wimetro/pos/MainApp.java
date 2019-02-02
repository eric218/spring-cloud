package cn.wimetro.pos;


import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

;

public class MainApp {

	public static final String unionIp = "9.234.3.193";
	public static final int unionPort  = 56242;
	
	public static boolean login()
  {
    	//预授权, 置初始数据
		PKGResult res;
		TradeLogin login = new TradeLogin();
		login.bIn_Date     = new String("1129172013").getBytes();    //交易日期时间, 10字节
		login.bIn_TradeNo  = new String("201908").getBytes();        //闸机的交易流水号, 6字节
		login.bIn_BatNo    = new String("000008").getBytes();                   

		//组包
		res = login.seal();
		if( res.iResult < 0 )
		{
			System.out.println("组包发生错误:"+res.iResult);
			return false;
		}
		System.out.println("MainApp login 组包返回数据:");
		System.out.println( ISOF.Bytes_HexStr( res.bResult ) );

		//发送数据
		Socket client = null;
		try{
			//连接服务器
			client = new Socket(unionIp, unionPort);
			//发送数据
			int ln = res.bResult.length;
			byte b[]={0, 0};
			b[0] = (byte)( ln / 256 );
			b[1] = (byte)( ln % 256 );
			client.getOutputStream().write( b );
			client.getOutputStream().write(res.bResult);

			//接收数据
			b[0] = 0;  b[1] = 0;
			client.getInputStream().read( b );
			System.out.println( ISOF.Bytes_HexStr( b ) );
			ln = (b[0]&0xFF)*256 + (b[1]&0xFF);
			byte data[] = new byte[ln];
			client.getInputStream().read( data );

			//System.out.println( ISOF.Bytes_HexStr( data ) );
			res.bResult = data;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally 
		{
			if (client != null) 
			{
				try 
				{
					client.close();
				} 
				catch (IOException e) 
				{
					System.out.println("systemerr:" +e);
				}
			}
		}

		//解包
		res = login.unseal( res.bResult);
		if( res.iResult < 0 )
		{
			System.out.println("解包完成:"+res.iResult);
			return false;
		}
		if((new String(login.getField(39))).equals("00"))
		{
			return true;	
		}

		return false;

  }
	public static void authcon()
  {
    	//预授权, 置初始数据
		PKGResult res;
		TradeAuthFi auth = new TradeAuthFi();
		auth.bIn_CardNo   = (new String("622588782237025")).getBytes();   //卡号
		auth.bIn_Money    = (new String("1432")).getBytes();              //交易金额,以分为单位,单程票最高金额
		auth.bIn_TradeNo  = (new String("201908")).getBytes();            //闸机的交易流水号, 6字节
		auth.bIn_ExpDate  = (new String("2011")).getBytes();              //卡的有效期'YYMM',没有可不填
		//auth.bIn_InStat   = ISOF.HexStr_Bytes("C7C5CDB7", ISOF.BCDF_L);   //进站站点,没有可不填
		//auth.bIn_InDT     = (new String("20181118131438")).getBytes();    //进站时间,没有可不填
		//auth.bIn_OutStat  = ISOF.HexStr_Bytes("B8A3CCEF", ISOF.BCDF_L);   //出站站点,没有可不填
		//auth.bIn_OutDT    = (new String("20181118154532")).getBytes();    //出站时间,没有可不填
		//auth.bIn_IC		= ISOF.HexStr_Bytes("9F26089569997027F76C189F2701809F101307000103A00000010A0100000020000B116BC49F37049E54A8B09F360211C8950500000000009A031601179C01039F02060000000100005F2A02015682027C009F1A0201569F03060000000000019F3303E0E9C89F3501229F1E085465726D696E616C8408A0000003330101019F090200309F4104100012199F63103030303130303030FF00000000000000", ISOF.BCDF_L);
		auth.bIn_CardIdx  = (new String("008")).getBytes();               //卡序列号
		auth.bIn_Mag2     = (new String("1234567890123456789=05082017819991683")).getBytes();   //二磁道数据, 需要将'='替换成'D'
		auth.bIn_AIRC     = (new String("000100")).getBytes();            //授权标识应答码
		auth.bIn_Old_BatNo   = (new String("000012")).getBytes();         //原交易批次号
		auth.bIn_Old_TradeNo   = (new String("000012")).getBytes();       //原交易流水号
		auth.bIn_Old_TradeDT   = (new String("1212")).getBytes();         //原交易日期
		
		auth.bIn_BatNo    = (new String("000008")).getBytes();            //每天更换

		//组包
		res = auth.seal();
		if( res.iResult < 0 )
		{
			System.out.println("组包发生错误:"+res.iResult);
			return;
		}
		System.out.println( ISOF.Bytes_HexStr( res.bResult ) );

		//发送数据
		Socket client = null;
		try{
			//连接服务器
			client = new Socket(unionIp, unionPort);
			//发送数据
			int ln = res.bResult.length;
			byte b[]={0, 0};
			b[0] = (byte)( ln / 256 );
			b[1] = (byte)( ln % 256 );
			client.getOutputStream().write( b );
			client.getOutputStream().write(res.bResult);

			//接收数据
			b[0] = 0;  b[1] = 0;
			client.getInputStream().read( b );
			//System.out.println( ISOF.Bytes_HexStr( b ) );
			ln = (b[0]&0xFF)*256 + (b[1]&0xFF);
			byte data[] = new byte[ln];
			client.getInputStream().read( data );
			//System.out.println( ISOF.Bytes_HexStr( data ) );
			res.bResult = data;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally 
		{
			if (client != null) 
			{
				try 
				{
					client.close();
				} 
				catch (IOException e) 
				{
					System.out.println("systemerr:" +e);
				}
			}
		}

		System.out.println("");
		System.out.println("=================================");
		System.out.println("");
		//解包
		res = auth.unseal( res.bResult);
		if( res.iResult < 0 )
		{
			System.out.println("解包完成:"+res.iResult);
			return;
		}
		System.out.println("");
  }
  public static void auth()
  {
    	//预授权, 置初始数据
		PKGResult res;
		TradeAuth auth = new TradeAuth();
		auth.bIn_CardNo   = (new String("6224242200000052")).getBytes();   //卡号
		auth.bIn_Money    = (new String("200")).getBytes();              //交易金额,以分为单位,单程票最高金额
		auth.bIn_TradeNo  = (new String("000013")).getBytes();            //闸机的交易流水号, 6字节
		auth.bIn_ExpDate  = (new String("2801")).getBytes();              //卡的有效期'YYMM',没有可不填
		auth.bIn_InStat   = (new String("wuhanzhdt")).getBytes();    			  //进站站点,没有可不填
		auth.bIn_InDT     = (new String("20181118131438")).getBytes();    //进站时间,没有可不填
		auth.bIn_OutStat  = (new String("wuhanzhdt")).getBytes();  			  //出站站点,没有可不填
		auth.bIn_OutDT    = (new String("20181118154532")).getBytes();    //出站时间,没有可不填
		auth.bIn_IC		  = ISOF.HexStr_Bytes("9F260818FD1E978C7D04CB9F2701809F101307011703A00000010A0100000885178A1F82999F37049E0A8D6B9F36022A00950500000000009A031812039C01009F02060000000000015F2A02015682027C009F1A0201569F03060000000000009F3303E0E1C89F3501229F1E0830303431323336309F410400000001", ISOF.BCDF_L);
		auth.bIn_CardIdx  = (new String("001")).getBytes();               //卡序列号
		auth.bIn_Mag2     = (new String("6224242200000052D280120142990310010F")).getBytes();   //二磁道数据, 需要将'='替换成'D'
		auth.bIn_BatNo    = (new String("000001")).getBytes();            //每天更换

		//组包
		res = auth.seal();
		if( res.iResult < 0 )
		{
			System.out.println("组包发生错误:"+res.iResult);
			return;
		}
		System.out.println("MainApp组包返回数据:"+res.iResult);
		System.out.println( ISOF.Bytes_HexStr( res.bResult ) );

		//发送数据
		Socket client = null;
		try{
			//连接服务器
			client = new Socket(unionIp, unionPort);
			//发送数据
			int ln = res.bResult.length;
			byte b[]={0, 0};
			b[0] = (byte)( ln / 256 );
			b[1] = (byte)( ln % 256 );
			client.getOutputStream().write( b );
			client.getOutputStream().write(res.bResult);

			//接收数据
			b[0] = 0;  b[1] = 0;
			client.getInputStream().read( b );
			System.out.println( ISOF.Bytes_HexStr( b ) );
			ln = (b[0]&0xFF)*256 + (b[1]&0xFF);
			byte data[] = new byte[ln];
			client.getInputStream().read( data );
			//System.out.println( ISOF.Bytes_HexStr( data ) );
			res.bResult = data;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally 
		{
			if (client != null) 
			{
				try 
				{
					client.close();
				} 
				catch (IOException e) 
				{
					System.out.println("systemerr:" +e);
				}
			}
		}
		//解包
		res = auth.unseal( res.bResult);
		if( res.iResult < 0 )
		{
			System.out.println("解包完成:"+res.iResult);
			return;
		}
		System.out.println("");

		//取指定返回域数据, 并打印
		//byte b[] = auth.get(4+64);
		//if( b != null )
		//	System.out.println( ISOF.Bytes_HexStr( b ) );
		//else
		//	System.out.println("field not exists");
  }
  public static void authFi()
  {
    	//预授权完成, 置初始数据
		PKGResult res;
		TradeAuthFi authFi = new TradeAuthFi();
		
		authFi.bIn_CardNo      = (new String("6224242200000052")).getBytes();   //2  卡号
		authFi.bIn_Money       = (new String("200")).getBytes();              //4  交易金额,以分为单位,单程票最高金额
		authFi.bIn_TradeNo     = (new String("000002")).getBytes();            //11 闸机的交易流水号, 6字节
		authFi.bIn_ExpDate     = (new String("2801")).getBytes();              //14 卡的有效期'YYMM',没有可不填
		authFi.bIn_InStat      = (new String("wuhanzhdt")).getBytes();    			  //进站站点,没有可不填
		authFi.bIn_InDT        = (new String("20181118131438")).getBytes();    //进站时间,没有可不填
		authFi.bIn_OutStat     = (new String("wuhanzhdt")).getBytes();  			  //出站站点,没有可不填
		authFi.bIn_OutDT       = (new String("20181118154532")).getBytes();    //出站时间,没有可不填
		authFi.bIn_CardIdx     = (new String("008")).getBytes();               //23 卡序列号
		authFi.bIn_Mag2        = (new String("6224242200000052D280120142990310010F")).getBytes();   //35 二磁道数据, 需要将'='替换成'D'
		authFi.bIn_AIRC        = (new String("010206")).getBytes();            //38 授权应答码
		authFi.bIn_BatNo       = (new String("000002")).getBytes();            //60.2 批次号，每天更换
		authFi.bIn_Old_BatNo   = (new String("000001")).getBytes();            //61.1 原批次号
		authFi.bIn_Old_TradeNo = (new String("000002")).getBytes();            //61.2 原交易流水号
		authFi.bIn_Old_TradeDT = (new String("1204")).getBytes();              //61.3 原交易日期

		//组包
		res = authFi.seal();
		if( res.iResult < 0 )
		{
			System.out.println("组包发生错误:"+res.iResult);
			return;
		}
		System.out.println( ISOF.Bytes_HexStr( res.bResult ) );

		//发送数据
		Socket client = null;
		try{
			//连接服务器
			client = new Socket(unionIp, unionPort);
			//发送数据
			int ln = res.bResult.length;
			byte b[]={0, 0};
			b[0] = (byte)( ln / 256 );
			b[1] = (byte)( ln % 256 );
			client.getOutputStream().write( b );
			client.getOutputStream().write(res.bResult);

			//接收数据
			b[0] = 0;  b[1] = 0;
			client.getInputStream().read( b );
			//System.out.println( ISOF.Bytes_HexStr( b ) );
			ln = (b[0]&0xFF)*256 + (b[1]&0xFF);
			byte data[] = new byte[ln];
			client.getInputStream().read( data );
			//System.out.println( ISOF.Bytes_HexStr( data ) );
			res.bResult = data;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally 
		{
			if (client != null) 
			{
				try 
				{
					client.close();
				} 
				catch (IOException e)
				 {
					System.out.println("systemerr:" +e);
				}
			}
		}
		//解包
		res = authFi.unseal( res.bResult);
		if( res.iResult < 0 )
		{
			System.out.println("解包完成:"+res.iResult);
			return;
		}
		System.out.println("");

		//取指定返回域数据, 并打印
		//byte b[] = auth.get(4+64);
		//if( b != null )
		//	System.out.println( ISOF.Bytes_HexStr( b ) );
		//else
		//	System.out.println("field not exists");
  }
  
  public static void authRefund()
  {
    	//退货, 置初始数据
		PKGResult res;
		TradeRefund authRefund = new TradeRefund();
		
		authRefund.bIn_CardNo      = (new String("6224242200000052")).getBytes();   //2  卡号
		authRefund.bIn_Money       = (new String("200")).getBytes();              //4  交易金额,以分为单位,单程票最高金额
		authRefund.bIn_TradeNo     = (new String("000017")).getBytes();            //11 闸机的交易流水号, 6字节
		authRefund.bIn_ExpDate     = (new String("2801")).getBytes();              //14 卡的有效期'YYMM',没有可不填
		//authRefund.bIn_InStat      = ISOF.HexStr_Bytes("C7C5CDB7", ISOF.BCDF_L);   //16 进站站点,没有可不填
		//authRefund.bIn_InDT        = (new String("20181118131438")).getBytes();    //17 进站时间,没有可不填
		//authRefund.bIn_OutStat     = ISOF.HexStr_Bytes("B8A3CCEF", ISOF.BCDF_L);   //18 出站站点,没有可不填
		//authRefund.bIn_OutDT       = (new String("20181118154532")).getBytes();    //19 出站时间,没有可不填
		//authRefund.bIn_CardIdx     = (new String("008")).getBytes();               //23 卡序列号
		//authRefund.bIn_Mag2        = (new String("1234567890123456789=05082017819991683")).getBytes();   //35 二磁道数据, 需要将'='替换成'D'
		authRefund.bIn_RRN         = (new String("123456123456")).getBytes();      //37 检索参考号,预授权完成时获取
		authRefund.bIn_AIRC        = (new String("010206")).getBytes();            //38 授权应答码【选填】
		authRefund.bIn_BatNo       = (new String("000002")).getBytes();            //60.2 批次号，每天更换
		authRefund.bIn_Old_BatNo   = (new String("000002")).getBytes();            //61.1 原批次号
		authRefund.bIn_Old_TradeNo = (new String("000016")).getBytes();            //61.2 原POS流水号
		authRefund.bIn_Old_TradeDT = (new String("1204")).getBytes();              //61.3 原交易日期【原交易应答13域】
		authRefund.bIn_CCCCode     = (new String("000")).getBytes();               //63.1 国际信用卡公司代码【同原交易，若无填全零】

		//组包
		res = authRefund.seal();
		if( res.iResult < 0 )
		{
			System.out.println("组包发生错误:"+res.iResult);
			return;
		}
		System.out.println( ISOF.Bytes_HexStr( res.bResult ) );

		//发送数据
		Socket client = null;
		try{
			//连接服务器
			client = new Socket(unionIp, unionPort);
			//发送数据
			int ln = res.bResult.length;
			byte b[]={0, 0};
			b[0] = (byte)( ln / 256 );
			b[1] = (byte)( ln % 256 );
			client.getOutputStream().write( b );
			client.getOutputStream().write(res.bResult);

			//接收数据
			b[0] = 0;  b[1] = 0;
			client.getInputStream().read( b );
			//System.out.println( ISOF.Bytes_HexStr( b ) );
			ln = (b[0]&0xFF)*256 + (b[1]&0xFF);
			byte data[] = new byte[ln];
			client.getInputStream().read( data );
			//System.out.println( ISOF.Bytes_HexStr( data ) );
			res.bResult = data;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally 
		{
			if (client != null) 
			{
				try 
				{
					client.close();
				} 
				catch (IOException e) 
				{
					System.out.println("systemerr:" +e);
				}
			}
		}
		//解包
		res = authRefund.unseal( res.bResult);
		if( res.iResult < 0 )
		{
			System.out.println("解包完成:"+res.iResult);
			return;
		}
		System.out.println("");

		//取指定返回域数据, 并打印
		//byte b[] = auth.get(4+64);
		//if( b != null )
		//	System.out.println( ISOF.Bytes_HexStr( b ) );
		//else
		//	System.out.println("field not exists");
  }

  /**
  *		交易结果查询
  */
  public static void authResult()
  {
		PKGResult res;
		TradeResult authResult = new TradeResult();
		
		authResult.bIn_CardNo      = (new String("6224242200000052")).getBytes();  //02  卡号
		authResult.bIn_Money       = (new String("200")).getBytes();               //04  交易金额,以分为单位,单程票最高金额
		authResult.bIn_TradeNo     = (new String("000017")).getBytes();            //11 闸机的交易流水号, 6字节
		authResult.bIn_BatType     = (new String("01")).getBytes();				   //[60.1]交易类型码          预授权01 预授权完成02 预授权撤销03              退货04 
		authResult.bIn_BatNo       = (new String("000002")).getBytes();            //60.2 批次号，每天更换
		authResult.bIn_Old_BatNo   = (new String("000002")).getBytes();            //61.1 原批次号
		authResult.bIn_Old_TradeNo = (new String("000016")).getBytes();            //61.2 原POS流水号
		authResult.bIn_Old_TradeDT = (new String("1204")).getBytes();              //61.3 原交易日期【原交易应答13域】

		//组包
		res = authResult.seal();
		if( res.iResult < 0 )
		{
			System.out.println("组包发生错误:"+res.iResult);
			return;
		}
		System.out.println( ISOF.Bytes_HexStr( res.bResult ) );

		//发送数据
		Socket client = null;
		try{
			//连接服务器
			client = new Socket(unionIp, unionPort);
			//发送数据
			int ln = res.bResult.length;
			byte b[]={0, 0};
			b[0] = (byte)( ln / 256 );
			b[1] = (byte)( ln % 256 );
			client.getOutputStream().write( b );
			client.getOutputStream().write(res.bResult);

			//接收数据
			b[0] = 0;  b[1] = 0;
			client.getInputStream().read( b );
			//System.out.println( ISOF.Bytes_HexStr( b ) );
			ln = (b[0]&0xFF)*256 + (b[1]&0xFF);
			byte data[] = new byte[ln];
			client.getInputStream().read( data );
			//System.out.println( ISOF.Bytes_HexStr( data ) );
			res.bResult = data;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally 
		{
			if (client != null) 
			{
				try 
				{
					client.close();
				} 
				catch (IOException e) 
				{
					System.out.println("systemerr:" +e);
				}
			}
		}
		//解包
		res = authResult.unseal( res.bResult);
		if( res.iResult < 0 )
		{
			System.out.println("解包完成:"+res.iResult);
			return;
		}
		System.out.println("");
  }
  
  public static void authFiRe()
  {
    	//预授权, 置初始数据
		PKGResult res;
		TradeAuthFiRe authFR = new TradeAuthFiRe();
		
		authFR.bIn_CardNo      = (new String("622588782237025")).getBytes();   //2  卡号
		authFR.bIn_Money       = (new String("1432")).getBytes();              //4  交易金额,以分为单位,单程票最高金额
		authFR.bIn_TradeNo     = (new String("000021")).getBytes();            //11 闸机的交易流水号, 6字节
		authFR.bIn_ExpDate     = (new String("2801")).getBytes();              //14 卡的有效期'YYMM',没有可不填
		//authFR.bIn_InStat      = ISOF.HexStr_Bytes("C7C5CDB7", ISOF.eBCDF_L);  //16 进站站点,没有可不填
		//authFR.bIn_InDT        = (new String("20181118131438")).getBytes();    //17 进站时间,没有可不填
		//authFR.bIn_OutStat     = ISOF.HexStr_Bytes("B8A3CCEF", ISOF.eBCDF_L);  //18 出站站点,没有可不填
		//authFR.bIn_OutDT       = (new String("20181118154532")).getBytes();    //19 出站时间,没有可不填
		//authFR.bIn_CardIdx     = (new String("008")).getBytes();               //23 卡序列号
		//authFR.bIn_Mag2        = (new String("1234567890123456789=05082017819991683")).getBytes();   //35 二磁道数据, 需要将'='替换成'D'
		authFR.bIn_RRN         = (new String("173035422000")).getBytes();      //37 检索参考号
		authFR.bIn_AIRC        = (new String("005767")).getBytes();            //38 授权应答码
		authFR.bIn_BatNo       = (new String("000003")).getBytes();            //60.2 批次号，每天更换
		authFR.bIn_Old_BatNo   = (new String("000002")).getBytes();            //61.1 原批次号
		authFR.bIn_Old_TradeNo = (new String("000017")).getBytes();            //61.2 原交易流水号
		authFR.bIn_Old_TradeDT = (new String("1204")).getBytes();              //61.3 原交易日期

		//组包
		res = authFR.seal();
		if( res.iResult < 0 )
		{
			System.out.println("组包发生错误:"+res.iResult);
			return;
		}
		System.out.println( ISOF.Bytes_HexStr( res.bResult ) );

		//发送数据
		Socket client = null;
		try{
			//连接服务器
			client = new Socket(unionIp, unionPort);
			//发送数据
			int ln = res.bResult.length;
			byte b[]={0, 0};
			b[0] = (byte)( ln / 256 );
			b[1] = (byte)( ln % 256 );
			client.getOutputStream().write( b );
			client.getOutputStream().write(res.bResult);

			//接收数据
			b[0] = 0;  b[1] = 0;
			client.getInputStream().read( b );
			//System.out.println( ISOF.Bytes_HexStr( b ) );
			ln = (b[0]&0xFF)*256 + (b[1]&0xFF);
			byte data[] = new byte[ln];
			client.getInputStream().read( data );
			//System.out.println( ISOF.Bytes_HexStr( data ) );
			res.bResult = data;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally 
		{
			if (client != null) 
			{
				try 
				{
					client.close();
				} 
				catch (IOException e) {
					System.out.println("systemerr:" +e);
				}
			}
		}
		//解包
		res = authFR.unseal( res.bResult);
		if( res.iResult < 0 )
		{
			System.out.println("解包完成:"+res.iResult);
			return;
		}
		System.out.println("");

  }
  
   public static void authRe()
  {
    	//预授权撤销, 置初始数据
		PKGResult res;
		TradeAuthRe authR = new TradeAuthRe();
		
		authR.bIn_CardNo      = (new String("6224242200000052")).getBytes();   //2  卡号
		authR.bIn_Money       = (new String("200")).getBytes();              //4  交易金额,以分为单位,单程票最高金额
		authR.bIn_TradeNo     = (new String("000013")).getBytes();            //11 闸机的交易流水号, 6字节
		authR.bIn_ExpDate     = (new String("2801")).getBytes();              //14 卡的有效期'YYMM',没有可不填
		//authR.bIn_InStat      = ISOF.HexStr_Bytes("C7C5CDB7", ISOF.eBCDF_L);  //16 进站站点,没有可不填
		//authR.bIn_InDT        = (new String("20181118131438")).getBytes();    //17 进站时间,没有可不填
		//authR.bIn_OutStat     = ISOF.HexStr_Bytes("B8A3CCEF", ISOF.eBCDF_L);  //18 出站站点,没有可不填
		//authR.bIn_OutDT       = (new String("20181118154532")).getBytes();    //19 出站时间,没有可不填
		//authR.bIn_CardIdx     = (new String("008")).getBytes();               //23 卡序列号
		//authR.bIn_Mag2        = (new String("1234567890123456789=05082017819991683")).getBytes();   //35 二磁道数据, 需要将'='替换成'D'
		authR.bIn_AIRC        = (new String("010206")).getBytes();            //38 授权应答码
		authR.bIn_BatNo       = (new String("000002")).getBytes();            //60.2 批次号，每天更换
		authR.bIn_Old_BatNo   = (new String("000002")).getBytes();            //61.1 原批次号
		authR.bIn_Old_TradeNo = (new String("000010")).getBytes();            //61.2 原POS流水号
		authR.bIn_Old_TradeDT = (new String("1204")).getBytes();              //61.3 原交易日期

		//组包
		res = authR.seal();
		if( res.iResult < 0 )
		{
			System.out.println("组包发生错误:"+res.iResult);
			return;
		}
		System.out.println( ISOF.Bytes_HexStr( res.bResult ) );

		//发送数据
		Socket client = null;
		try{
			//连接服务器
			client = new Socket(unionIp, unionPort);
			//发送数据
			int ln = res.bResult.length;
			byte b[]={0, 0};
			b[0] = (byte)( ln / 256 );
			b[1] = (byte)( ln % 256 );
			client.getOutputStream().write( b );
			client.getOutputStream().write(res.bResult);

			//接收数据
			b[0] = 0;  b[1] = 0;
			client.getInputStream().read( b );
			//System.out.println( ISOF.Bytes_HexStr( b ) );
			ln = (b[0]&0xFF)*256 + (b[1]&0xFF);
			byte data[] = new byte[ln];
			client.getInputStream().read( data );
			//System.out.println( ISOF.Bytes_HexStr( data ) );
			res.bResult = data;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally 
		{
			if (client != null) 
			{
				try
				{
					client.close();
				} 
				catch (IOException e) 
				{
					System.out.println("systemerr:" +e);
				}
			}
		}
		//解包
		res = authR.unseal( res.bResult);
		if( res.iResult < 0 )
		{
			System.out.println("解包完成:"+res.iResult);
			return;
		}
		System.out.println("");

  }
  
  public static PKGResult resetKey(byte[] pkgData)
  {
  	PKGResult res = null;
  	byte[] retCode;
		TradeResetKey setKey = new TradeResetKey();
		
		try
		{
	  		//解包
			res = setKey.unseal( pkgData);

			retCode = (new String("00")).getBytes();
			if( res.iResult < 0 )
			{
				setKey.bIn_retCode  = (new String("B0")).getBytes();;            	//闸机的交易流水号, 6字节
				System.out.println("解包完成:"+res.iResult);
				if(res.iResult == ISOF.ERR_MAC)
				{
					retCode = (new String("A0")).getBytes();		
				}
				else if(res.iResult == ISOF.ERR_CHECKVALUE)
				{
					retCode = (new String("B0")).getBytes();		
				}
				else
				{
					retCode = (new String("00")).getBytes();
					//retCode = (new String("B1")).getBytes();	
				}
			}
			
			//getField里参数是N，取不到值改Y
			setKey.bIn_Date  	= setKey.getField(7);			      			//输入时间MMDDhhmmss
			setKey.bIn_TradeNo  = setKey.getField(11);				  			//闸机的交易流水号, 6字节
			setKey.bIn_retCode  = retCode;				  						//响应码
		
	
			//组包
			res = setKey.seal();
			if( res.iResult < 0 )
			{
				System.out.println("组包发生错误:"+res.iResult);
				return res;
			}
			System.out.println("组包返回数据:");
			System.out.println( ISOF.Bytes_HexStr( res.bResult ) );
			
			return res;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return res;
  }
  public static void performanceTest(final int t_Thread,final int t_Loop)
  {
		//byte[] testPKG = ISOF.HexStr_Bytes("60000000896032003200000800003800010AC000152019081107361122082035290031313037333632303139303830303132333435363738313233343536373839303132333435001100001017101000202C1B3F16130462EE0000000000000000C6B92C473036314444464445", ISOF.BCDF_L);//D
		final byte[] threadTestPKG = ISOF.HexStr_Bytes("60000000896032003200000800003800010AC000152019081107361122082035290031313037333632303139303830303132333435363738313233343536373839303132333435001100001017101000202C1B3F16130462EE0000000000000000C6B92C473036314444464445", ISOF.BCDF_L);//D
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		long time1 = System.currentTimeMillis();
		//System.out.println("开始时间:"+String.valueOf(time1));
		try
		{
			for(int i=0;i<t_Thread;i++)
			{
				final int index = i;
				try
				{
					Thread.sleep(5);
				}	
				catch(Exception e)
				{
					e.printStackTrace();
				}
			
				cachedThreadPool.execute(new Runnable(){
					public void run()
					{
						int j=0;
						try
						{
							Socket sock = new Socket(ISOF.gHSMIP, Integer.parseInt(ISOF.gHSMPORT));
							hsm tHsm = new hsm(sock);
							while(j<t_Loop)
							{
								j++;
								tHsm.generateMac(ISOF.gMACK, threadTestPKG);
								try
								{
									Thread.sleep(50);
								}
								catch(Exception e)
								{
									e.printStackTrace();
								}
							}
							sock.close();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				});
			}
			cachedThreadPool.shutdown();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	    //auth();
	    while(true)
	    {
	    	if(cachedThreadPool.isTerminated())
	    	{
	    	
	 			long time2 = System.currentTimeMillis();
				//System.out.println("结束时间:"+String.valueOf(time2));	
				long between_s = time2 - time1;
		 		System.out.printf("%d(%d*%d)次时间差:",t_Thread*t_Loop,t_Thread,t_Loop);
		 		System.out.println(String.valueOf(between_s/1000));	
	
				break;
			}
			
	    }
 }
  public static void hsmTest()
  {
		try
		{
			Socket sock = new Socket(ISOF.gHSMIP, Integer.parseInt(ISOF.gHSMPORT));
			hsm tHsm = new hsm(sock);
			
			tHsm.getVer();
			System.out.println("--------------next--------------");
			tHsm.getLMKSts();
			System.out.println("--------------next--------------");
			byte[] zmk1 = ISOF.HexStr_Bytes("0102030405060708090A0B0C0D0E0F00", ISOF.BCDF_L);
			byte[] zmkchk1 = ISOF.HexStr_Bytes("42338E8E", ISOF.BCDF_L);
			byte[] zmk2 = ISOF.HexStr_Bytes("00000000000000000000000000000000", ISOF.BCDF_L);
			byte[] zmkchk2 = ISOF.HexStr_Bytes("8CA64DE9", ISOF.BCDF_L);
			byte[] mnPWD = ISOF.HexStr_Bytes("0000000000000000", ISOF.BCDF_L);
			tHsm.setZMK( zmk1, zmkchk1, zmk2, zmkchk2,mnPWD);
			System.out.println("--------------next--------------generateSMMac");
			final byte[] threadTestPKG = ISOF.HexStr_Bytes("60000000896032003200000800003800010AC000152019081107361122082035290031313037333632303139303830303132333435363738313233343536373839303132333435001100001017101000202C1B3F16130462EE0000000000000000C6B92C473036314444464445", ISOF.BCDF_L);//D
			tHsm.generateSMMac(ISOF.gMACK, threadTestPKG);
			System.out.println("--------------next--------------");
			sock.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
  }
		
	
	public static void transTest()
	{
		login();
		
		//重置密钥需要传入CUPA发送的充值请求包
		//resetKey(testPKG);
		
		auth();
		
		authFi();
		
		authRefund();
		
		authFiRe();
		
		authRe();
	}
	
	public void setField55()
	{
		byte[] field55 = new byte[512];
		//Tag十六进制 + Length十六进制 + value 
		//9F26 //9F26 = '08  51616C29FE898EE0'
		//9F27  //9F27 = '01  80'
		//9F10  //9F10 = '13  07000103A00000010A01000000200053BC31B1'
		//9F37  //9F37 = '04  C36EC4EA'
		//9F36  //9F36 = '02  11CA'
		//95    //95 = '05  0000000000'
		//9A    //9A = '03  160117'
		//9C    //9C = '01  03'
		//9F02  //9F02 = '06  000000012000'
		//5F2A  //5F2A = '02  0156'
		//82    //82 = '02  7C00'
		//9F1A  //9F1A = '02  0156'
		//9F03  //9F03 = '06  000000000001'
		//9F33  //9F33 = '03  E0E9C8'
		//9F34  //9F35 = '01  22'
		//9F35  //9F1E = '08  5465726D696E616C'
		//9F1E  //84 = '08  A000000333010101'
		//84    //9F09 = '02  0030'
		//9F09  //9F41 = '04  10001221'
		//9F41  //9F63 = '10  3030303130303030FF00000000000000'
		//9F63 
	}
	
//  public static void main(String []args)
//  {
//	//ISOF.envInit();
//	if(args[0].equals("1")){
//	  login();
//	}else if(args[0].equals("2")){
//	  auth();
//	}else if(args[0].equals("3")){
//	  authFi();
//	}else if(args[0].equals("4")){
//	  authRe();
//	}else if(args[0].equals("5")){
//	  authRefund();
//	}else if(args[0].equals("6")){
//	  performanceTestCUP();
//	}else if(args[0].equals("7")){
//	  authResult();
//	}else if(args[0].equals("8")){
//	  hsmTest();
//	}
//
//  }

  public static void performanceTestCUP()
  {
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		long time1 = System.currentTimeMillis();
		System.out.println("开始时间:"+String.valueOf(time1));
		try
		{
			for(int i=0;i<100;i++)
			{
				final int index = i;
				try
				{
					Thread.sleep(5);
				}	
				catch(Exception e)
				{
					e.printStackTrace();
				}
			
				cachedThreadPool.execute(new Runnable(){
					public void run()
					{
						login();
					}
				});
			}
			cachedThreadPool.shutdown();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	    //auth();
	    while(true)
	    {
	    	if(cachedThreadPool.isTerminated())
	    	{
	    	
	 			long time2 = System.currentTimeMillis();
				//System.out.println("结束时间:"+String.valueOf(time2));	
				long between_s = time2 - time1;
		 		//System.out.printf("%d(%d*%d)次时间差:",t_Thread*t_Loop,t_Thread,t_Loop);
		 		//System.out.println(String.valueOf(between_s/1000));	
	
				break;
			}
			
	    }
 	}
  
}

