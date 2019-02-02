package cn.wimetro.pos;

/**

 *   ISO8583交易包对象
 * 注1: 为了方便统一处理,
 *        将0域作为占位域, 无需处理
 *        将数组1,2,3,4分别设置为TPDU、Header、Msgid、Mainmap等4个报文头域
 *        将数组5-68作为1-64域
 * 注2: 此类作为所有具体交易的"基类", 是基于每个域在不同的报文中域的一级格式一致的前提下,
 *        否则, 需要将fields域的初始化配置提到具体的交易对象中去执行
 */

import lombok.extern.slf4j.Slf4j;

import java.net.Socket;

@Slf4j
public class ISO8583
{
	private ISO8583Field fields[];
	/**
	
	 *   构造函数, 执行域对象的初始化
	 * @param piRQRS,  对象的用途
	 */
	ISO8583(int piRQRS)
	{
		//初始化头和域的数据对象
		fields     = new ISO8583Field[ISOF.iFields+5];  //64个域+4个报文头域+1个占位域
		for(int i=0; i<(ISOF.iFields+5); i++)
		{
			fields[i] = new ISO8583Field( piRQRS );    //为每个域创建一个域对象
		}

    	/* 以下执行域的初始化配置 */
		//占位域
		fields[0+  0].setAttr(0+  0,  10, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_Y, ISOF.BCDF_L);  //占位,便于后续下标对应处理
    	//报文头
		fields[1+  0].setAttr(1+  0,  10, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_Y, ISOF.BCDF_L);  //TPDU, 定长10字节, 左靠BCD
		fields[2+  0].setAttr(2+  0,  12, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_Y, ISOF.BCDF_L);  //Header, 定长12字节, 左靠BCD
		fields[3+  0].setAttr(3+  0,   4, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_Y, ISOF.BCDF_L);  //Msgid, 定长4字节, 左靠BCD
		fields[4+  0].setAttr(4+  0,   8, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_N, ISOF.BCDF_N);  //Mainmap, 定长8字节 byte

		//以下为数据域	  
		fields[4+  2].setAttr(4+  2,  19, ISOF.LLVAR , ISOF.FILL_N,  (byte)0 , ISOF.BCD_Y, ISOF.BCDF_L);    //002:主账号域, 2字节变长, 最大19字节,左靠BCD
		fields[4+  3].setAttr(4+  3,   6, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_Y, ISOF.BCDF_L);    //003:交易处理码, 定长6字节,左靠BCD
		fields[4+  4].setAttr(4+  4,  12, ISOF.FIXED , ISOF.FILL_L,  (byte)48, ISOF.BCD_Y, ISOF.BCDF_R);    //004:交易金额, 定长12字节,右靠BCD

		fields[4+  7].setAttr(4+  7,  10, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_Y, ISOF.BCDF_L);    //007:交易日期时间

		fields[4+ 11].setAttr(4+ 11,   6, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_Y, ISOF.BCDF_L);    //011:受卡方系统跟踪号, 定长6字节, 左靠BCD
		fields[4+ 12].setAttr(4+ 12,   6, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_Y, ISOF.BCDF_L);    //012:受卡方所在地时间, 定长6字节, 左靠BCD
		fields[4+ 13].setAttr(4+ 13,   4, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_Y, ISOF.BCDF_L);    //013:受卡方所在地日期, 定长4字节, 左靠BCD
		fields[4+ 14].setAttr(4+ 14,   4, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_Y, ISOF.BCDF_L);    //014:卡有效期, 定长4字节, 左靠BCD
		fields[4+ 15].setAttr(4+ 15,   4, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_Y, ISOF.BCDF_L);    //015:清算日期, 定长4字节, 左靠BCD
		fields[4+ 16].setAttr(4+ 16,  50, ISOF.LLVAR , ISOF.FILL_N,  (byte)0 , ISOF.BCD_N, ISOF.BCDF_N);    //016:进站站点, 2字节变长, 最大50字节
		fields[4+ 17].setAttr(4+ 17,  14, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_Y, ISOF.BCDF_L);    //017:进站时间, 定长14字节, 左靠BCD
		fields[4+ 18].setAttr(4+ 18,  50, ISOF.LLVAR , ISOF.FILL_N,  (byte)0 , ISOF.BCD_N, ISOF.BCDF_N);    //018:出站站点, 2字节变长, 最大50字节
		fields[4+ 19].setAttr(4+ 19,  14, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_Y, ISOF.BCDF_L);    //019:出站时间, 定长14字节, 左靠BCD

		fields[4+ 22].setAttr(4+ 22,   3, ISOF.FIXED , ISOF.FILL_L,  (byte)0 , ISOF.BCD_Y, ISOF.BCDF_L);    //022:服务点输入方式码, 定长3字节,左靠BCD
		fields[4+ 23].setAttr(4+ 23,   3, ISOF.FIXED , ISOF.FILL_R,  (byte)48, ISOF.BCD_Y, ISOF.BCDF_R);    //023:卡序列号, 定长3字节,右靠BCD

		fields[4+ 25].setAttr(4+ 25,   2, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_Y, ISOF.BCDF_L);    //025:服务点条件码, 定长2字节, 左靠BCD

		fields[4+ 32].setAttr(4+ 32,  11, ISOF.LLVAR , ISOF.FILL_N,  (byte)0 , ISOF.BCD_Y, ISOF.BCDF_L);    //032:受理方标识码, 2字节变长, 最大11字节, 左靠BCD

		fields[4+ 35].setAttr(4+ 35,  37, ISOF.LLVAR , ISOF.FILL_N,  (byte)0 , ISOF.BCD_Y, ISOF.BCDF_L);    //035:2磁道数据, 2字节变长, 最大37字节, 左靠BCD

		fields[4+ 37].setAttr(4+ 37,  12, ISOF.FIXED , ISOF.FILL_R,  (byte)32, ISOF.BCD_N, ISOF.BCDF_N);    //037:检索参考号, 定长12字节
		fields[4+ 38].setAttr(4+ 38,   6, ISOF.FIXED , ISOF.FILL_R,  (byte)32, ISOF.BCD_N, ISOF.BCDF_N);    //038:授权标识应答码, 定长6字节
		fields[4+ 39].setAttr(4+ 39,   2, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_N, ISOF.BCDF_N);    //039:应答码, 定长2字节


		fields[4+ 41].setAttr(4+ 41,   8, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_N, ISOF.BCDF_N);    //041:受卡机终端标识码, 定长8字节
		fields[4+ 42].setAttr(4+ 42,  15, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_N, ISOF.BCDF_N);    //042:受卡方标识码
		fields[4+ 43].setAttr(4+ 43,   8, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_N, ISOF.BCDF_N);    //043:受卡方系统标识
		fields[4+ 44].setAttr(4+ 44,  25, ISOF.LLVAR , ISOF.FILL_N,  (byte)0 , ISOF.BCD_N, ISOF.BCDF_N);    //044:附加响应数据, 2字节变长, 最大25字节

		fields[4+ 49].setAttr(4+ 49,   3, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_N, ISOF.BCDF_N);    //049:交易货币代码, 定长3字节

		fields[4+ 53].setAttr(4+ 53,  16, ISOF.FIXED , ISOF.FILL_R,  (byte)48, ISOF.BCD_Y, ISOF.BCDF_L);    //053:安全控制信息, 定长16字节, 最大16字节, 左靠BCD
		fields[4+ 55].setAttr(4+ 55, 255, ISOF.LLLVAR, ISOF.FILL_N,  (byte)0 , ISOF.BCD_N, ISOF.BCDF_N);    //055:IC卡数据域, 3字节变长, 最大255字节
		fields[4+ 59].setAttr(4+ 59, 999, ISOF.LLLVAR, ISOF.FILL_N,  (byte)0 , ISOF.BCD_N, ISOF.BCDF_N);    //自定义域, 2字节变长, 最大999字节
		fields[4+ 60].setAttr(4+ 60,  17, ISOF.LLLVAR, ISOF.FILL_N,  (byte)0 , ISOF.BCD_Y, ISOF.BCDF_L);    //060:自定义域, 3字节变长, 最大13字节, 左靠BCD

		fields[4+ 61].setAttr(4+ 61,  29, ISOF.LLLVAR, ISOF.FILL_N,  (byte)0 , ISOF.BCD_Y, ISOF.BCDF_L);    //061:原始信息域, 3字节变长, 最大29字节, 左靠BCD
		fields[4+ 62].setAttr(4+ 62,  72, ISOF.LLLVAR, ISOF.FILL_N,  (byte)0 , ISOF.BCD_N, ISOF.BCDF_N);    //062:自定义域, 3字节变长, 最大512字节
		fields[4+ 63].setAttr(4+ 63,  63, ISOF.LLLVAR, ISOF.FILL_N,  (byte)0 , ISOF.BCD_N, ISOF.BCDF_N);    //063:自定义域, 3字节变长, 最大163字节
		fields[4+ 64].setAttr(4+ 64,   8, ISOF.FIXED , ISOF.FILL_N,  (byte)0 , ISOF.BCD_N, ISOF.BCDF_N);    //064:mac域, 定长8字节 byte
	}

	/**
	 *   将指定的byte数组, 设置给当前域
	 * @param  piFieldIdx,   设置数据的域编号
	 * @param  pFieldData,        用于赋值的数据
	 * @return 结果, 0-成功, <0失败
	 */
	public int set(int piFieldIdx, byte pFieldData[])
	{
		return fields[piFieldIdx].set(ISOF.FORMAT_N, pFieldData);
	}
	
	public int setField(int piFieldIdx, byte pFieldData[])
	{
		return fields[piFieldIdx+4].set(ISOF.FORMAT_N, pFieldData);
	}

	/**
	 *   取得指定域组包前/解包后的值
	 * @param  piFieldIdx,   设置数据的域编号
	 * @return 取到的数据
	 */
	public byte[] get(int piFieldIdx)
	{
		return fields[piFieldIdx].get( ISOF.FORMAT_N );
	}
	
	public byte[] getY(int piFieldIdx)
	{
		return fields[piFieldIdx].get( ISOF.FORMAT_Y );
	}
	public byte[] getField(int piFieldIdx)
	{
		return fields[piFieldIdx+4].get( ISOF.FORMAT_N );
	}
	
	public byte[] getFieldY(int piFieldIdx)
	{
		return fields[piFieldIdx+4].get( ISOF.FORMAT_Y );
	}
	/**
	 *   取位图
	 */
	public byte[] getBitmap()
	{
		return fields[4].get(ISOF.FORMAT_Y);
	}
	

	
	/**
	 *   格式化当前域的数据
	 */
	public void formatField()
	{
		//格式化每一个域
		for(int i=0; i<(ISOF.iFields+5); i++)
		{
			
			//if( i == (4+11) ) //35域磁道加密
			//{
			//	fields[i].format11();
			//}
			
			fields[i].format();
			
			if( i == (4+35) ) //35域磁道加密
			{
				if(fields[i].getMapFlag() == ISOF.MAP_Y)  //若不存在35域，则53域也不能存在
				{
					fields[i].format35();
				}
				else
				{
					fields[4+53].setMapFlag(ISOF.MAP_N);
				}
			}
		}
	}

	/**
	 *   设置当前当前将交易包中, 哪些数据域存在
	 */
	public void setBitmap()
	{
		byte b1[]={0, 0, 0, 0, 0, 0, 0, 0};
		byte b[]={0};
		for(int i=0; i<64; i++)
		{
			if( fields[i+5].getMapFlag() == ISOF.MAP_Y )
			{
				int k1 = i / 8;   //位存在的byte字节(0-7)
				int k2 = i % 8;   //位在byte内的顺序(0-7)
				b[0] = (byte)(1 << (7-k2));
				b1[k1] = (byte)(b[0] | b1[k1]) ;
			}
		}
		fields[4+  0].set(ISOF.FORMAT_Y, b1);
	}

	/**
	 *  测试接口 private
	 */
	private void print(int piFieldIdx, int piHEXorASC, int piFormatFlag, int piSpaceFlag)
	{
		fields[piFieldIdx].print(piHEXorASC, piFormatFlag, piSpaceFlag);
	}
	
	/**
	 *  提示信息
	 */
	public void printField()
	{
		if(true)
		{
			log.debug("seal format:");
			print(1+0 , ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);																													
			print(2+0 , ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);																													
			print(3+0 , ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);																													
			print(4+0 , ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);																													
      																													
			print(4+2 , ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);																													
			print(4+3 , ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);																													
			print(4+4 , ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);
			print(4+7 , ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);
			print(4+11, ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);
			print(4+12, ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);
			print(4+13, ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);
			print(4+14, ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);																													
			print(4+15, ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);
			print(4+16, ISOF.ASCSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);																													
			print(4+17, ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);																													
			print(4+18, ISOF.ASCSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);																													
			print(4+19, ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);																													
			print(4+22, ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);																													
			print(4+23, ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);																													
			print(4+25, ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);																													
			print(4+35, ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);
			print(4+37, ISOF.ASCSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);
			print(4+38, ISOF.ASCSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);
			print(4+39, ISOF.ASCSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);
			print(4+41, ISOF.ASCSTR, ISOF.FORMAT_Y, ISOF.SPACE_N);																													
			print(4+42, ISOF.ASCSTR, ISOF.FORMAT_Y, ISOF.SPACE_N);																													
			print(4+43, ISOF.ASCSTR, ISOF.FORMAT_Y, ISOF.SPACE_N);																													
			print(4+44, ISOF.ASCSTR, ISOF.FORMAT_Y, ISOF.SPACE_N);
			print(4+49, ISOF.ASCSTR, ISOF.FORMAT_Y, ISOF.SPACE_N);																													
			print(4+53, ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);																													
			print(4+55, ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);																													
			print(4+60, ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);
			print(4+61, ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);
			print(4+62, ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);
			print(4+63, ISOF.ASCSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);
			print(4+64, ISOF.HEXSTR, ISOF.FORMAT_Y, ISOF.SPACE_Y);
		}
	}

	/**
	 *   条件域处理
	 */
	public void conditionProc()
	{
		for(int i=0; i<(ISOF.iFields+5); i++)
		{
			fields[i].conditionProc();
		}
	}

	/**
	 *   组包,完成从TPDU到63域之间所有域的组包处理
	 */
	public PKGResult seal()
	{
		byte b1[]={0};
		byte b2[]={0};
		int ln=0;
		int ln2=0;
		PKGResult res=new PKGResult();

		formatField();   	//格式化所有域数据
		setBitmap();   		//置位图
	
		
		for(int i=0; i<((ISOF.iFields+5)-1); i++)   ///将已格式化的域数据组合
		{
			if( fields[i].getMapFlag() == ISOF.MAP_Y )
			{
				if( ln > 0 )
				{
					b1 = new byte[ln];
					System.arraycopy(b2,0,b1,0,ln);
				}
				byte b[]=fields[i].get( ISOF.FORMAT_Y );

				b2 = new byte[ln + b.length];
				
				System.arraycopy(b1,0,b2,0,ln);

				System.arraycopy(b,0,b2,ln,b.length);

				ln = ln + b.length;
			}
		}
		byte[] bitMap = getBitmap();
		//System.out.println("组包bitmap：");
		//System.out.println(ISOF.Bytes_HexStr(bitMap));

		try
		{
			if((bitMap != null)&&(bitMap[7]&0x01) == 0x01)
			{
				byte dataMac[]=new byte[b2.length-11];
				System.arraycopy(b2,11,dataMac,0,b2.length-11);
				//计算MAC
				try
				{
					Socket sock = new Socket(ISOF.gHSMIP, Integer.parseInt(ISOF.gHSMPORT));
					hsm tHsm = new hsm(sock);
					byte[] bMac = tHsm.generateSMMac(ISOF.gMACK, dataMac);
					//System.out.print("数据生成的bMac为：");
					//System.out.println(ISOF.Bytes_HexStr(bMac));
					byte[] field64 = new byte[8];
					System.arraycopy(bMac,0,field64,0,8);
					setField(64,field64);
					//组合mac
					byte bSealPkg2[]=new byte[b2.length+8];
					System.arraycopy(b2,0,bSealPkg2,0,b2.length);
					System.arraycopy(bMac,0,bSealPkg2,b2.length,8);
					b2 = bSealPkg2;
					
					sock.close();
				}
				catch(Exception e)
				{
					res.iResult = ISOF.ERR_MAC_EXCEPTION;
					return res;
				}
			}
		}
		catch(Exception e)
		{
			res.iResult = ISOF.ERR_DATA_TOO_SHORT;
			return res;
		}
		res.iResult = 0;
		res.bResult = b2;
		
		printField();
		return res;
	}

	/**
	 *   解包
	 * @param pbPkg,   需要解包的数据, 从TPDU——MAC的数据, 不包括长度
	 */
	public PKGResult unseal(byte[] pbPkg)
	{
		int iRet=0;
		int iStart=0;

		PKGResult res=new PKGResult();

		//重置密钥报文需要用新密钥校验MAC，解包后校验MAC
		//1:置报文头的域为存在
		fields[1].setMapFlag( ISOF.MAP_Y );
		fields[2].setMapFlag( ISOF.MAP_Y );
		fields[3].setMapFlag( ISOF.MAP_Y );
		fields[4].setMapFlag( ISOF.MAP_Y );
		
		try
		{
			//2:解位图
			byte bMap[]={0, 0, 0, 0, 0, 0, 0, 0};
			System.arraycopy(pbPkg, 5+6+2, bMap, 0, 8);
			
			byte b[]={0};
			for(int i=0; i<64; i++)
			{
				int k1 = i / 8;
				int k2 = i % 8;
				b[0] = (byte)(1 << (7-k2));
				if( (b[0] & bMap[k1]) != 0 )
				{
					fields[5+i].setMapFlag(ISOF.MAP_Y);
				}
			}
	
			//3:解每个域
			iStart = 0;
			try
			{
				for(int i=0; i<(ISOF.iFields+5); i++)
				{
					iRet = fields[i].unformat(iStart, pbPkg);
					if( iRet < 0 )
					{
						res.iResult = ISOF.ERR_DATA_TOO_SHORT;
						return res;
					}
					iStart = iStart + iRet;
				}
			}
			catch(Exception e)
			{
				res.iResult = ISOF.ERR_DATA_TOO_SHORT;
				return res;
			}
			
			//密钥重置交易先验checkvalue()
		
			byte[] f62 = getField(62);
			byte[] macK = new byte[16];
			byte[] macKcheck = new byte[4];
			
			String f603 = ISOF.Bytes_HexStr(getFieldY(60)).substring(8,11);
			if(("101").equals(f603) && ("0800").equals(ISOF.Bytes_HexStr(getY(3))))
			{
				System.arraycopy(f62,0,macK,0,16);
				//测试环境格式
				//System.arraycopy(f62,0,macK,0,8);
				//System.arraycopy(f62,0,macK,8,8);
				System.arraycopy(f62,16,macKcheck,0,4);
				
				try
				{
					Socket sock = new Socket(ISOF.gHSMIP, Integer.parseInt(ISOF.gHSMPORT));
					hsm tHsm = new hsm(sock);
					if(tHsm.calCheckvalueSM(macK,macKcheck))
					{
						ISOF.updateKey(macK);
						System.out.println("[updateKey Success]");
					}
					else
					{						
						System.out.println("***B0校验CHeckValue不通过***");
						res.iResult = ISOF.ERR_CHECKVALUE;
					}
					sock.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			//4:校验mac是否正确
			try
			{
				//System.out.printf("***11111准备计算MAC***[%02x] [%02x]\n", pbPkg[20],pbPkg[20]& 0x01);
				//System.out.println( ISOF.Bytes_HexStr( pbPkg ) );
				if((pbPkg[20]& 0x01) == 0x01)//位图MAC存在校验MAC
				{
					byte macData[]= new byte[pbPkg.length - 11 - 8];
					System.arraycopy(pbPkg,11,macData,0,pbPkg.length- 11 - 8);
					//计算MAC
					try
					{
						Socket sock = new Socket(ISOF.gHSMIP, Integer.parseInt(ISOF.gHSMPORT));
						hsm tHsm = new hsm(sock);
						byte[] retMac = new byte[8];
						System.arraycopy(pbPkg,(pbPkg.length-8),retMac,0,8);
						if(false == tHsm.checkMacSM(macData,retMac))
						{
							System.out.println("***A0 校验MAC不通过***");
							res.iResult = ISOF.ERR_MAC_EXCEPTION;
							return res;	
						}
						//System.out.println("***校验MAC通过***");
						sock.close();
					}
					catch(Exception e)
					{
						res.iResult = ISOF.ERR_MAC_EXCEPTION;
						return res;
					}
				}
			}
			catch(Exception e)
			{
				res.iResult = ISOF.ERR_DATA_TOO_SHORT;
				return res;
			}	
		}
		catch(Exception e)
		{
			res.iResult = ISOF.ERR_DATA_TOO_SHORT;
			return res;
		}	
		printField();
		res.iResult = 0;
		return res;
	}
}
