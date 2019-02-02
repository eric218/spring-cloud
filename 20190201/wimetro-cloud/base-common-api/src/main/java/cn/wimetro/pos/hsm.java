package cn.wimetro.pos;

import java.net.Socket;
import java.util.Arrays;

/**
 * 加密机模块
 */
public class hsm {
	private String host; // 加密机IP地址
	private int port; 	 // 加密机端口
	private Socket sock;

	hsm(Socket inSock) {
		sock = inSock;
	}
	
	//获取版本号
	public void getVer()
	{	
		byte[] verCMD ={0x00};
		
		try
		{
			if(sock != null)
			{
				System.out.println("HSM get VER:");
				sock.getOutputStream().write(verCMD);
				byte[] retData=new byte[32];
				sock.getInputStream().read(retData);
				
				printHexString(retData);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//获取本地主密钥状态
	public void getLMKSts()
	{	
		byte[] err ={0x00,0x01};
		byte[] verCMD ={0x01};
		
		try
		{
			if(sock != null)
			{
				System.out.println("HSM get LMK:");
				sock.getOutputStream().write(verCMD);
				byte[] retData=new byte[32];
				sock.getInputStream().read(retData);
				printHexString(retData);

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void setZMK(byte[] zmk1,byte[] zmkchk1,byte[] zmk2,byte[] zmkchk2,byte[] mnPWD)
	{
		try 
		{

			byte[] cmdData = new byte[512];
			byte[] cmd = {0x22,
				0x00,0x01
			};
			System.arraycopy(cmd,0,cmdData,0,3);
			System.arraycopy(zmk1,0,cmdData,3,16);
			System.arraycopy(zmkchk1,0,cmdData,19,4);
			System.arraycopy(zmk2,0,cmdData,23,16);
			System.arraycopy(zmkchk2,0,cmdData,39,4);
			System.arraycopy(mnPWD,0,cmdData,43,8);
			
			System.out.println("===HSM set ZMK:");
			
			byte[] senddata = new byte[51];
			System.arraycopy(cmdData,0,senddata,0,51);
			
			printHexString(senddata);
			sock.getOutputStream().write(senddata);
			byte[] retData=new byte[32];
			sock.getInputStream().read(retData);
			printHexString(retData);
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	//测试接口 不使用
	public void setSecondMK(byte[] zmk1,byte[] zmkchk1,byte[] zmk2,byte[] zmkchk2,byte[] mnPWD)
	{
		try 
		{

			byte[] cmdData = new byte[512];
			byte[] cmd = {0x22,
				0x00,0x01
			};
			System.arraycopy(cmd,0,cmdData,0,3);
			System.arraycopy(zmk1,0,cmdData,3,16);
			System.arraycopy(zmkchk1,0,cmdData,19,4);
			System.arraycopy(zmk2,0,cmdData,23,16);
			System.arraycopy(zmkchk2,0,cmdData,39,4);
			System.arraycopy(mnPWD,0,cmdData,43,8);
			
			System.out.println("===HSM set SecondZMK:");
			
			byte[] senddata = new byte[51];
			System.arraycopy(cmdData,0,senddata,0,51);
			
			printHexString(senddata);
			sock.getOutputStream().write(senddata);
			byte[] retData=new byte[32];
			sock.getInputStream().read(retData);
			printHexString(retData);
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void printHexString(byte[] b)
	{
		for(int i=0;i<b.length;i++)
		{
			String hex = Integer.toHexString(b[i]&0xFF);		
			if(hex.length()==1)
			{
					hex='0'+hex;
				}
//			System.out.print("0x");
//			System.out.print(hex.toUpperCase());
//			System.out.print(" ");
		}
		//System.out.print("\n");
	}
	
	//国密计算MAC
	public byte[] calcSMMAC(byte[] MACK, byte[] data)
	{
		byte[] err ={0x45,0x2F};
		try {

			byte[] cmdData = new byte[512];
			byte[] cmd = {(byte)0xA8,0x03,
				0x07,
				0x01,
				0x00,0x01,
				0x10,
				0x02
			};
			
			//System.out.print("计算数据:");
			printHexString(data);

			byte[] dataln = new byte[2];
			dataln[0] = (byte)(data.length/256);
			dataln[1] = (byte)(data.length%256);
			
			System.arraycopy(cmd,0,cmdData,0,8);
			//MACK 
			System.arraycopy(MACK,0,cmdData,8,16);
			//初始向量
			byte[] tmp = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
			System.arraycopy(tmp,0,cmdData,24,16);
			//计算MAC数据长度
			System.arraycopy(dataln,0,cmdData,40,2);
			//计算MAC数据
			int len = ((dataln[0]&0xFF)<<8) + (dataln[1]&0xFF);
			System.arraycopy(data,0,cmdData,42,len);
			
			byte[] senddata = new byte[len+42];
			System.arraycopy(cmdData,0,senddata,0,len+42);
			
			//System.out.println("计算MAC发送数据:");
			//printHexString(senddata);
			sock.getOutputStream().write(senddata);
			byte[] retData=new byte[32];
			sock.getInputStream().read(retData);
			//System.out.println("计算MAC返回数据:");
			printHexString(retData);
			byte[] tempMac = new byte[8];
			
			System.arraycopy(retData,1,tempMac,0,8);
			//System.out.println("SM计算MAC结果:");
			printHexString(tempMac);
			return tempMac;
		}
		catch(Exception e)
		{
				e.printStackTrace();
		}
		return err;
	}
	
	//生成mac，测试分组8字节，地铁业务16字节，
	public byte[] generateSMMac(byte[] wKEY, byte[] data)
	{	
		byte[] err ={0x45,0x2F};
		try
		{
			int datalen = data.length;
			datalen += (16 - datalen % 16) % 16;
			byte[] srcdata = new byte[datalen];
			System.arraycopy(data, 0, srcdata, 0, data.length);
			for (int i = data.length; i < datalen; i++)
			{
				srcdata[i] = 0x00;
			}

			int pos = 0;
			byte[] oper1 = new byte[16];
			System.arraycopy(srcdata,0,oper1,0,16);
			pos+=16;
			while(pos<datalen)
			{
				byte[] oper2 = new byte[16];
				System.arraycopy(srcdata,pos,oper2,0,16);
				pos+=16;
				byte[] t = bytesXOR(oper1,oper2);
				oper1 = t;
			}
		
			byte[] resultBlock = ISOF.Bytes_HexStr(oper1).getBytes();
			//System.out.println("SM计算MAC 异或结果:");
			printHexString(resultBlock);
			
			byte[] front16 = new byte[16];
			System.arraycopy(resultBlock,0,front16,0,16);
			
			byte[] end16 = new byte[16];
			System.arraycopy(resultBlock,16,end16,0,16);
			
			byte[] desfront16 = encDataSM(wKEY, front16);
			
			if(desfront16[0] == 0x45 && desfront16[1] == 0x2F)
			{
				System.out.println("generate返回异常:");
				return err;	
			}
			byte[] resultXOR = bytesXOR(desfront16,end16);
			byte[] buff = encDataSM(wKEY, resultXOR);

			byte[] tempData = new byte[16];
			System.arraycopy(ISOF.Bytes_HexStr(buff).getBytes(),0,tempData,0,16);

			return tempData;
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return err;
	}
	
	//磁道加密 复用MACK 0xA8 08
	/*
	public byte[] encDataSM(byte[] wKEY, byte[] data)
	{
		
		byte[] err ={0x45,0x2F};
		try {		

			byte[] cmdData = new byte[256];
			byte[] cmd = {(byte)0xA8,0x08,//指令
				0x01,
				0x00,0x01,
				0x07,//SM4
				0x00,//加密
				0x00,//ECB
				0x01,
				0x07,//密钥密文的解密算法标志  07 SM4
				0x00,//密钥解密模式
				0x10
			};
			
			//System.out.println("SM计算MAC 异或结果:");
			
			int cmdLen = cmd.length;
			
			System.arraycopy(cmd,0,cmdData,0,cmdLen);
			//MACK 
			System.arraycopy(wKEY,0,cmdData,cmdLen,16);
			cmdLen += 16;
			
			byte[] dataln = new byte[2];
			dataln[0] = (byte)(data.length/256);
			dataln[1] = (byte)(data.length%256);
			System.arraycopy(dataln,0,cmdData,cmdLen,2);
			cmdLen += 2;
			
			int len = ((dataln[0]&0xFF)<<8) + (dataln[1]&0xFF);
			System.arraycopy(data,0,cmdData,cmdLen,len);
			cmdLen += len;
			
			byte[] senddata = new byte[cmdLen];

			System.arraycopy(cmdData,0,senddata,0,cmdLen);

			//System.out.println("SM计算MAC发发送数据:"+cmdLen);
			//printHexString(senddata);
			
			sock.getOutputStream().write(senddata);
			
			
			try
			{
				Thread.sleep(100);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			byte[] retData=new byte[32];
			sock.getInputStream().read(retData);
			//System.out.println("SM计算MAC返回结果:");
			//printHexString(retData);
			
			
			if(retData[0] == 0x41)
			{
				byte[] tempLen = new byte[2];
				System.arraycopy(retData,1,tempLen,0,2);
				len = ((tempLen[0]&0xFF)<<8) + (tempLen[1]&0xFF);
				byte[] tempData = new byte[len];
				System.arraycopy(retData,3,tempData,0,len);
				
				//encSM结果
				//System.out.println("encSM结果:");
				//printHexString(tempData);
				
				return tempData;
			}

		}
		catch(Exception e)
		{
				System.out.printf("===================================");
				e.printStackTrace();
		}
		return err;
	}	*/


	//磁道加密 复用MACK 0xA8 08
	public byte[] encDataSM(byte[] wKEY, byte[] data)
	{
		
		byte[] err ={0x45,0x2F};
		try {		

			byte[] cmdData = new byte[256];
			byte[] cmd = {(byte)0xA8,0x08,//指令
				0x01,
				0x00,0x01,
				0x07,//SM4
				0x00,//加密
				0x00,//ECB
				0x01,
				0x07,//密钥密文的解密算法标志  07 SM4
				0x00,//密钥解密模式
				0x10
			};
			
			//System.out.println("SM计算MAC 异或结果:");
			
			int cmdLen = cmd.length;
			
			System.arraycopy(cmd,0,cmdData,0,cmdLen);
			//MACK 
			System.arraycopy(wKEY,0,cmdData,cmdLen,16);
			cmdLen += 16;
			
			byte[] dataln = new byte[2];
			dataln[0] = (byte)(data.length/256);
			dataln[1] = (byte)(data.length%256);
			System.arraycopy(dataln,0,cmdData,cmdLen,2);
			cmdLen += 2;
			
			int len = ((dataln[0]&0xFF)<<8) + (dataln[1]&0xFF);
			System.arraycopy(data,0,cmdData,cmdLen,len);
			cmdLen += len;
			
			byte[] senddata = new byte[cmdLen];

			System.arraycopy(cmdData,0,senddata,0,cmdLen);

			//System.out.println("SM计算MAC发发送数据:"+cmdLen);
			//printHexString(senddata);
			
			sock.getOutputStream().write(senddata);
			
			
			try
			{
				Thread.sleep(100);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			byte[] retData=new byte[32];
			sock.getInputStream().read(retData);
			//System.out.println("SM计算MAC返回结果:");
			//printHexString(retData);
			
			
			if(retData[0] == 0x41)
			{
				byte[] tempLen = new byte[2];
				System.arraycopy(retData,1,tempLen,0,2);
				len = ((tempLen[0]&0xFF)<<8) + (tempLen[1]&0xFF);
				byte[] tempData = new byte[len];
				System.arraycopy(retData,3,tempData,0,len);
				
				//encSM结果
				//System.out.println("encSM结果:");
				//printHexString(tempData);
				
				return tempData;
			}

		}
		catch(Exception e)
		{
				System.out.printf("===================================");
				e.printStackTrace();
		}
		return err;
	}	
	
	//校验checkvalue
	public boolean calCheckvalueSM(byte[] wKEY,byte[] checkValue)
	{
		try
		{
			byte[] calData = new byte[16];
			int i = 0;
			for(i=0;i<calData.length;i++)
			{
					calData[i] = 0x00;
			}
			
			byte[] retData = encDataSM(wKEY, calData);
			byte[] checkMAC = new byte[4];
			System.arraycopy(retData,0,checkMAC,0,4);
			
			if(Arrays.equals(checkMAC,checkValue))
			{
				return true;
			}

		}
		catch(Exception e)
		{
				e.printStackTrace();
		}
		return false;
	}
	
	//校验MAC SM
	public boolean checkMacSM(byte[] data, byte[] inMac)
	{
		//System.out.println("###***checkMac***###");
		//System.out.println(ISOF.Bytes_HexStr(data));
		//System.out.println(ISOF.Bytes_HexStr(ISOF.gMACK));
		byte[] calcMac = generateSMMac(ISOF.gMACK, data);
		byte[] calMacf8 = new byte[8];
		System.arraycopy(calcMac,0,calMacf8,0,8);
		//System.out.println("###***checkMac in[calMacf8]***###");
		//System.out.println(new String(calMacf8));
		if(!Arrays.equals(inMac,calMacf8))
		{
			//System.out.println("###***checkMac in[gMACK]***###");
			//System.out.println(new String(calcMac));
			if(ISOF.calSec())//窗口期内
			{
				byte[] calcMac1 = generateSMMac(ISOF.gMACK_bak, data);
				System.arraycopy(calcMac1,0,calMacf8,0,8);
				//System.out.println("###***checkMac in[gMACK_bak]***###");
				//System.out.println(new String(calMacf8));
				//System.out.println("###***checkMac in[inMac]***###");
				//System.out.println(new String(inMac));
				
				if(!Arrays.equals(inMac,calMacf8))
				{
					return false;
				}
			}
			else
			{
				return false;	
			}
		}
		
		return true;
	}

	
	//磁道加密 复用MACK 0x71
	public byte[] encDATA(byte[] wKEY, byte[] data)
	{
		
		byte[] err ={0x45,0x2F};
		try {		

			byte[] cmdData = new byte[256];
			byte[] cmd = {0x71,//指令
				0x00,0x10,//索引0x10为测试环境主密钥
			};
			System.arraycopy(cmd,0,cmdData,0,3);
			//MACK 
			System.arraycopy(wKEY,0,cmdData,3,16);
			//初始向量
			byte[] tmp = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
			System.arraycopy(tmp,0,cmdData,19,8);
			cmdData[27] = 0x01;//加解密标识
			cmdData[28] = 0x00;//0x01,//第0位为0 ECB 为1 CBC，第一位为0 3DES 1 DES 算法标识
			
			byte[] dataln = new byte[2];
			dataln[0] = (byte)(data.length/256);
			dataln[1] = (byte)(data.length%256);
			System.arraycopy(dataln,0,cmdData,29,2);
			int len = ((dataln[0]&0xFF)<<8) + (dataln[1]&0xFF);
			System.arraycopy(data,0,cmdData,31,len);
			
			byte[] senddata = new byte[len+31];

			System.arraycopy(cmdData,0,senddata,0,len+31);

			sock.getOutputStream().write(senddata);
			
			try
			{
				Thread.sleep(100);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			byte[] retData=new byte[32];
			sock.getInputStream().read(retData);
			
			if(retData[0] == 0x41)
			{
				byte[] tempLen = new byte[2];
				System.arraycopy(retData,1,tempLen,0,2);
				len = ((tempLen[0]&0xFF)<<8) + (tempLen[1]&0xFF);
				byte[] tempData = new byte[len];
				System.arraycopy(retData,3,tempData,0,len);
				
				return tempData;
			}

		}
		catch(Exception e)
		{
				System.out.printf("===================================");
				e.printStackTrace();
		}
		return err;
	}	
	
	//计算MAC
	public byte[] calcMAC(byte[] MACK, byte[] data)
	{
		byte[] err ={0x45,0x2F};
		try {

			byte[] cmdData = new byte[512];
			byte[] cmd = {0x04,0x10,
				0x01,
				0x00,0x01,
				0x10,
				0x01,
			};
			//System.out.print("计算数据:");
			printHexString(data);

			byte[] dataln = new byte[2];
			dataln[0] = (byte)(data.length/256);
			dataln[1] = (byte)(data.length%256);
			
			System.arraycopy(cmd,0,cmdData,0,7);
			//MACK 
			System.arraycopy(MACK,0,cmdData,7,16);
			//初始向量
			byte[] tmp = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
			System.arraycopy(tmp,0,cmdData,23,8);
			//计算MAC数据长度
			System.arraycopy(dataln,0,cmdData,31,2);
			//计算MAC数据
			int len = ((dataln[0]&0xFF)<<8) + (dataln[1]&0xFF);

			System.arraycopy(data,0,cmdData,33,len);
			
			byte[] senddata = new byte[len+33];
			System.arraycopy(cmdData,0,senddata,0,len+33);
			
			sock.getOutputStream().write(senddata);
			byte[] retData=new byte[32];
			sock.getInputStream().read(retData);
			printHexString(retData);
			byte[] tempMac = new byte[8];
			System.arraycopy(retData,1,tempMac,0,8);
			
			return tempMac;
		}
		catch(Exception e)
		{
				e.printStackTrace();
		}
		return err;
	}
	
	//校验checkvalue
	public boolean calCheckvalue(byte[] wKEY,byte[] checkValue)
	{
		try
		{
			//计算密钥的checkvalue 0x0423
			byte[] cmdData =new byte[21];
			byte[] cmd ={	0x04,0x23,
										0x00,0x10,//索引
										0x10}; 
			byte[] retData=new byte[32];
										
			System.arraycopy(cmd,0,cmdData,0,5);
			System.arraycopy(wKEY,0,cmdData,5,16);
			
			sock.getOutputStream().write(cmdData);
			sock.getInputStream().read(retData);
			if(retData[0] == 0x41)
			{
				byte[] retValue = new byte[4];
				System.arraycopy(retData,1,retValue,0,4);
				if(Arrays.equals(retValue,checkValue))
				{
					return true;
				}
			}
			else
			{
				//失败 记录错误log
			}
		}
		catch(Exception e)
		{
				e.printStackTrace();
		}
		return false;
	}

	//生成mac，测试分组8字节，地铁业务16字节，
	public byte[] generateMac(byte[] wKEY, byte[] data)
	{	
		byte[] err ={0x45,0x2F};
		try
		{
			int datalen = data.length;
			datalen += (8 - datalen % 8) % 8;
			byte[] srcdata = new byte[datalen];
			System.arraycopy(data, 0, srcdata, 0, data.length);
			for (int i = data.length; i < datalen; i++)
			{
				srcdata[i] = 0x00;
			}

			int pos = 0;
			byte[] oper1 = new byte[8];
			System.arraycopy(srcdata,0,oper1,0,8);
			pos+=8;
			while(pos<datalen)
			{
				byte[] oper2 = new byte[8];
				System.arraycopy(srcdata,pos,oper2,0,8);
				pos+=8;
				byte[] t = bytesXOR(oper1,oper2);
				oper1 = t;
			}
		
			byte[] resultBlock = ISOF.Bytes_HexStr(oper1).getBytes();

			
			byte[] front8 = new byte[8];
			System.arraycopy(resultBlock,0,front8,0,8);
			
			byte[] end8 = new byte[8];
			System.arraycopy(resultBlock,8,end8,0,8);
			
			byte[] dlen = {0x00,0x08};
			byte[] desfront8 = encDATA(wKEY, front8);

			
			byte[] resultXOR = bytesXOR(desfront8,end8);
			byte[] buff = encDATA(wKEY, resultXOR);

			byte[] tempData = new byte[8];
			System.arraycopy(ISOF.Bytes_HexStr(buff).getBytes(),0,tempData,0,8);
			
			return tempData;
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return err;
	}
	
	public boolean checkMac(byte[] data, byte[] inMac)
	{
		//System.out.println("###***checkMac***###");
		//System.out.println(ISOF.Bytes_HexStr(data));
		//System.out.println(ISOF.Bytes_HexStr(ISOF.gMACK_bak));
		byte[] calcMac = generateSMMac(ISOF.gMACK, data);
		if(!Arrays.equals(inMac,calcMac))
		{
			System.out.println("###***checkMac in[gMACK]***###");
			System.out.println(new String(calcMac));
			if(ISOF.calSec())//窗口期内
			{
				byte[] calcMac1 = generateSMMac(ISOF.gMACK_bak, data);
				System.out.println("###***checkMac in[gMACK_bak]***###");
				System.out.println(new String(calcMac1));
				System.out.println("###***checkMac in[inMac]***###");
				System.out.println(new String(inMac));
				
				if(!Arrays.equals(inMac,calcMac1))
				{
					return false;
				}
			}
			else
			{
				return false;	
			}
		}
		
		return true;
	}
	
	private byte[] bytesXOR(byte[] inOper1,byte[] inOper2)
	{
		int len = inOper1.length;
		if(len != inOper2.length)
		{
				return null;
		}
		byte[] result = new byte[len];
		for(int i =0; i<len;i++)
		{
			result[i] = (byte)((inOper1[i]&0xFF)^(inOper2[i]&0xFF));		
		}
		
		return result;
	}

}
