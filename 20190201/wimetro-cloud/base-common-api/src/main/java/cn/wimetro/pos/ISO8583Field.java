package cn.wimetro.pos;

/**
 *   ISO8583单一数据域对象
 * 注: 为方便统一处理, 在本方案中将报文头等数据, 也当成特殊的域,
 *     与标准的数据域采用相同的方法和逻辑进行处理
 */
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
@Slf4j
public class ISO8583Field
{
	private int    iRQRS;       //交易包用途, eUSE_RQ-请求包, eUSE_RS-响应包
	private int    iFieldIdx;   //域的编号
	private int    iMaxLen;     //域的最大数据长度
	private int    iLenType;    //数据域数据的长度类型, eFIXED-定长, eLVAR-1字节变长, eLLVAR-2字节边长, eLLLVAR-3字节边长
	private int    iBCDZip;     //数据域数据,是否需要执行压缩, eBCD_N-不需要压缩, eBCD_Y-需要压缩
	private int    iBCDFill;    //数据域数据压缩时,是左靠还是右靠, eBCDF_N-不补齐, eBCDF_L-左靠右补齐, eBCDF_R-右靠左补齐
	private int    iFillFlag;   //数据域数据,是否需要填充, eFILL_N-不填充, eFILL_L-左填充, eFILL_R-右填充
	private byte   bFillChar;   //填充字符

	private int    iMapFlag;    //该域是否在报文中存在, eMAP_N-不存在, eMAP_Y-存在
	private byte   bData1[];    //组包前/解包后的原始数据
	private byte   bData2[];    //组包后/解包前的数据

	/**
	 *   构造函数, 执行域的初始化
	 * @param piRQRS,  域的用途, 作为请求包还是响应包
	 */
	ISO8583Field(int piRQRS)
	{
		iRQRS     = piRQRS;
	}

	/**
	 *   设置数据域
	 * @param  piFieldIdx,   域的编号
	 * @param  piMaxLen,     域的最大数据长度
	 * @param  piLenType,    域的长度类型
	 * @param  piFillFlag,   域的数据填充方向
	 * @param  piBCDZip,     域的是否要压缩
	 * @param  piBCDFill,    域的数据在压缩时, 长度不足时, 前补0还是后补0
	 */
	public void setAttr(int piFieldIdx, int piMaxLen, int piLenType, int piFillFlag, byte pbFillChar, int piBCDZip, int piBCDFill)
	{
		iFieldIdx = piFieldIdx;
		iMaxLen   = piMaxLen;
		iLenType  = piLenType;
		iFillFlag = piFillFlag;
		bFillChar = pbFillChar;
		iBCDZip   = piBCDZip;
		iBCDFill  = piBCDFill;
	}

	/**
	 *   将指定的byte数组, 设置给当前域
	 * @param  piFormatFlag, eFORMAT_N-psData未格式化的数据, eFORMAT_Y-psData已格式化的数据
	 * @param  pbArr,        用于赋值的数据
	 * @return 结果, 0-成功, <0失败
	 */
	public int set(int piFormatFlag, byte pbArr[])
	{
		if( ISOF.FORMAT_N == piFormatFlag )   //未格式化的数据,原始数据(组包用)
		{
			bData1 = new byte[pbArr.length];
			
			System.arraycopy(pbArr,0,bData1,0,pbArr.length);
			if( bData1.length > 0 ) 
			{
				iMapFlag = ISOF.MAP_Y;  //设置数据之后,就置域标志为存在
			}
		}
		else  //已格式化的数据(解包用)
		{
			bData2 = new byte[pbArr.length];
			System.arraycopy(pbArr,0,bData2,0,pbArr.length);
		}

		return 0;
	}

	/**
	 *   获取域指定的数据
	 * @param  piFormatFlag, eFORMAT_N未格式化的数据, eFORMAT_Y已格式化的数据
	 * @return 数据的字符串形式
	 */
	public byte[] get(int piFormatFlag)
	{
		if( ISOF.FORMAT_N == piFormatFlag )
		{
			return bData1;
		}
		else
		{
			return bData2;
		}
	}

	/**
	 *   格式化当前域的数据
	 * @return 结果, 0-成功, <0失败
	 */
	public int format()
	{
		if( bData1 == null ) 
		{
			return ISOF.ERR_VAR_NULL;
		}
		if( iMapFlag == ISOF.MAP_N ) 
		{
			return 0;   //该域不存在时, 不需要进行格式化处理
		}
		
		int  ln = bData1.length;        //未填充前的实际数据长度

		//1:首先检查是否需要填充
		byte b1[]={0};
		if( ISOF.FILL_N == iFillFlag ) 
		{
			b1 = bData1;   //不填充
		}
		if( ISOF.FILL_L == iFillFlag )        //左填充
		{
			b1 = new byte[iMaxLen];             //申请最大数据长度数组
			for(int i=0; i<iMaxLen; i++)        //置初值为填充字符
			{
				b1[i] = bFillChar;
			}

			if( ln>iMaxLen ) 
			{
					return ISOF.ERR_DATA_TOO_LEN;   //数据超长错误
			}
			System.arraycopy(bData1,0,b1,iMaxLen-ln,ln);
		}
		if( ISOF.FILL_R == iFillFlag )        //右填充
		{
			b1 = new byte[iMaxLen];             //申请最大数据长度数组
			for(int i=0; i<iMaxLen; i++)        //置初值为填充字符
			{
				b1[i] = bFillChar;
			}

			if( ln>iMaxLen ) 
			{
				return ISOF.ERR_DATA_TOO_LEN;    	//数据超长错误
			}
			System.arraycopy(bData1,0,b1,0,ln);
		}
		ln = b1.length;    //重新取长度值,填充后,长度可能变化

		//2:检查是否BCD压缩
		byte b2[]={0};
		if( ISOF.BCD_Y == iBCDZip )   //BCD压缩
		{
			b2 = ISOF.HexStr_Bytes( new String(b1), iBCDFill );
		}
		else   //不压缩
		{
			b2 = b1;
		}

		//3:检查是否变长
		byte b3[]={0};
		if( ISOF.FIXED == iLenType ) 
		{
			b3 = b2;   //定长
		}
		if( ( ISOF.LVAR  == iLenType ) || ( ISOF.LLVAR  == iLenType ) )  //1字节和2字节变长
		{
			b3 = new byte[b2.length+1];
			String s="";
			s = s.format("%02d", ln);
			byte btmp[]=ISOF.HexStr_Bytes(s, ISOF.BCDF_R);
			b3[0] = btmp[0];
			System.arraycopy(b2,0,b3,1,b2.length);
			//for( int i=0; i<b2.length; i++ )
			//{
			//	b3[i+1] = b2[i];
			//}
		}
		if( ISOF.LLLVAR == iLenType )  //3字节变长
		{
			b3 = new byte[b2.length+2];
			String s="";
			s = s.format("%04d", ln);
			byte btmp[]=ISOF.HexStr_Bytes(s, ISOF.BCDF_R);
			b3[0] = btmp[0];
			b3[1] = btmp[1];
			
			System.arraycopy(b2,0,b3,2,b2.length);
			//for( int i=0; i<b2.length; i++ )
			//	b3[i+2] = b2[i];
		}

		bData2 = b3;
		
		return 0;
	}
	
	public String format11()
	{
//			byte[] bData1 = {0x30,0x30,0x30,0x31,0x32,0x33};
			
			String strTrace = new String(bData1);
			int iTrace = Integer.parseInt(strTrace);
			
			if(iTrace <999999)
			{
				iTrace += 1;	
			}
			
			strTrace = Integer.toString(iTrace);
			//System.out.println("===STRTRACE:"+strTrace);
			byte[] newTrace =  strTrace.getBytes();
			byte[] tmpTrace = {0x30,0x30,0x30,0x30,0x30,0x30};
			if(newTrace.length < 6)
			{
					System.arraycopy(newTrace,0,tmpTrace,6-newTrace.length,newTrace.length);
					strTrace = new String(tmpTrace);
					bData1 = tmpTrace;
			}
			else
			{
					bData1 = newTrace;
			}
			
			return strTrace;	
	}

	public void format35()
	{
		if( bData2 == null )
		{
			return;
		}
		byte[] formatData = new byte[bData2.length-1];
		System.arraycopy(bData2,1,formatData,0,bData2.length-1);
		String strLen = ISOF.Bytes_HexStr(bData2);
		String strLen2= strLen.substring(0,2);
		int lenth = Integer.parseInt(strLen2);

		int ln=formatData.length;
		byte[] encMAGData = new byte[16];

		if(ln-1<16)
		{
			System.arraycopy(formatData,0,encMAGData,0,formatData.length-1);
			int i =0;
			for(i=formatData.length-1;i<16;i++)
			{
				encMAGData[i] = (byte)0xFF;
				lenth += 2;
			}
		}
		else
		{
			System.arraycopy(formatData,ln-16-1,encMAGData,0,16);
		}
		//加密
		try
		{
			byte encResult[];
			Socket sock = new Socket(ISOF.gHSMIP, Integer.parseInt(ISOF.gHSMPORT));
			hsm tHsm = new hsm(sock);
			encResult= tHsm.encDataSM(ISOF.gMACK, encMAGData);
			sock.close();
			//替换
			if(ln-1<16)
			{
				byte[] temp = new byte[18];

				String s="";
				s = s.format("%02d", lenth);
				byte btmp[]=ISOF.HexStr_Bytes(s, ISOF.BCDF_R);
				temp[0] = btmp[0];

				System.arraycopy(encResult,0,temp,1,16);
				temp[17] = bData2[ln];

				bData2 = temp;
			}
			else
			{
				System.arraycopy(encResult,0,bData2,ln-16,16);
			}

		}
		catch(Exception e)
		{
		}
	}
	
//	public void format35()
//	{
//		if( bData2 == null ) return;
//		int ln=bData2.length;
//		byte b[]=new byte[8];
//
//		System.arraycopy(bData2,ln-9,b,0,8);
//		//for(int i=0; i<8; i++)
//		//	b[i] = bData2[ln-9+i];
//		//加密
//		try
//		{
//			byte b2[];
//			Socket sock = new Socket(ISOF.gHSMIP, Integer.parseInt(ISOF.gHSMPORT));
//			hsm tHsm = new hsm(sock);
//			b2= tHsm.encDataSM(ISOF.gMACK, bData2);
//			sock.close();
//			//替换
//			System.arraycopy(b2,0,bData2,ln-8-1,8);
//		}
//		catch(Exception e)
//		{
//		}
//	}

	/**
	 *   解指定域的数据
	 * @param piStart,  数据开始的位置
	 * @param pbPkg,    数据包
	 */
	public int unformat(int piStart, byte pbPkg[])
	{
		//1:判断与是否存在
		if( iMapFlag == ISOF.MAP_N ) return 0;   //域不存在
		//判断是否定长类型
		if( ISOF.FIXED == iLenType )  //如果是定长类型
		{
			//判断域是否压缩
			if( ISOF.BCD_N == iBCDZip )  //不压缩
			{
				if( (piStart+iMaxLen) > pbPkg.length ) 
				{
					return ISOF.ERR_DATA_TOO_SHORT;
				}
				bData1 = new byte[iMaxLen];
				bData2 = new byte[iMaxLen];
				
				System.arraycopy(pbPkg,piStart,bData1,0,iMaxLen);
				System.arraycopy(pbPkg,piStart,bData2,0,iMaxLen);

				//去掉右边的填充字符
				if( iFillFlag == ISOF.FILL_R )
				{
					for(int i=bData1.length-1; 0<=i; i--)
					{
						if( bData1[i] != bFillChar )
						{
							byte b[];
							b = new byte[i+1];
							for(int j=0; j<=i; j++)
								b[j] = bData1[j];
							bData1 = b;
							break;
						}
					}
				}

				return iMaxLen;
			}
			else   //压缩
			{
				if( (piStart+(iMaxLen+1)/2) > pbPkg.length ) 
				{
					return ISOF.ERR_DATA_TOO_SHORT;
				}
				bData2 = new byte[(iMaxLen+1)/2];
				for(int i=0; i<((iMaxLen+1)/2); i++)
				{
					bData2[i] = pbPkg[piStart+i];
				}
				String s=ISOF.Bytes_HexStr(bData2);
				byte btmp[]=s.getBytes();
				bData1 = new byte[iMaxLen];
				if( ISOF.BCDF_R == iBCDFill )  //右靠
				{
					int ioffset=0;
					if( (iMaxLen%2) > 0 ) 
					{
						ioffset=1;
					}
					System.arraycopy(btmp,ioffset,bData1,0,iMaxLen);
					//for(int i=0; i<iMaxLen; i++)
					//	bData1[i] = btmp[ioffset+i];
				}
				else  //左靠
				{
					System.arraycopy(btmp,0,bData1,0,iMaxLen);
					//for(int i=0; i<iMaxLen; i++)
					//	bData1[i] = btmp[i];
				}

				//去掉右边的填充字符
				if( iFillFlag == ISOF.FILL_R )
				{
					for(int i=bData1.length-1; 0<=i; i--)
					{
						if( bData1[i] != bFillChar )
						{
							byte b[];
							b = new byte[i+1];
							System.arraycopy(bData1,0,b,0,i);
							//for(int j=0; j<=i; j++)
							//	b[j] = bData1[j];
							bData1 = b;
							break;
						}
					}
				}

				return (iMaxLen+1)/2;
			}
		}
		else     //变长数据
		{
			int ln1=0;
			int ln2=0;
			if( (ISOF.LVAR == iLenType) || (ISOF.LLVAR == iLenType) )  //如果是1/2字节变长
			{
				ln1 = 1;
				//首先判断数据是否足够
				if( (piStart+ln1) > pbPkg.length ) 
				{
					return ISOF.ERR_DATA_TOO_SHORT;
				}

				//取一字节长度
				byte btmp[]={0};
				btmp[0] = pbPkg[piStart+0];
				ln2 = Integer.parseInt( ISOF.Bytes_HexStr(btmp) );  //取得实际数据长度
			}
			else   //3字节变长
			{
				ln1 = 2;
				//判断数据是否足够
				if( (piStart+ln1) > pbPkg.length ) 
				{
					return ISOF.ERR_DATA_TOO_SHORT;
				}

				//取一字节长度
				byte btmp[]={0, 0};
				btmp[0] = pbPkg[piStart+0];
				btmp[1] = pbPkg[piStart+1];
				ln2 = Integer.parseInt( ISOF.Bytes_HexStr(btmp) );  //取得实际数据长度
			}

			//判断域是否压缩
			if( ISOF.BCD_N == iBCDZip )  //不压缩
			{
				if( (piStart+ln1+ln2) > pbPkg.length ) 
				{
					return ISOF.ERR_DATA_TOO_SHORT;
				}
				bData1 = new byte[ln2];
				bData2 = new byte[ln2];
				System.arraycopy(pbPkg,piStart+ln1,bData1,0,ln2);
				System.arraycopy(pbPkg,piStart+ln1,bData2,0,ln2);
				//for(int i=0; i<ln2; i++)
				//{
				//	bData1[i] = pbPkg[piStart+ln1+i];
				//	bData2[i] = pbPkg[piStart+ln1+i];
				//}

				return ln1+ln2;
			}
			else   //压缩
			{
				if( (piStart+ln1+(ln2+1)/2) > pbPkg.length )
				{
					return ISOF.ERR_DATA_TOO_SHORT;
				}
				bData2 = new byte[(ln2+1)/2];
				System.arraycopy(pbPkg,piStart+ln1,bData2,0,((ln2+1)/2));
				//for(int i=0; i<((ln2+1)/2); i++)
				//{
				//	bData2[i] = pbPkg[piStart+ln1+i];
				//}
				String s=ISOF.Bytes_HexStr(bData2);
				bData1 = s.getBytes();
				if( ln2 < bData1.length )  //取实际数据长度
				{
					byte btmp[] = new byte[ln2];
					System.arraycopy(bData1,0,btmp,0,ln2);
					//for(int i=0; i<ln2; i++)
					//	btmp[i] = bData1[i];
					bData1 = btmp;
				}
				return ln1+(ln2+1)/2;
			}
		}
	}

	/**
	 *   返回域的位图标志
	 */
	public int getMapFlag()
	{
		return iMapFlag;
	}

	/**
	 *   设置域存在标志(解包才需要调用此方法)
	 */
	public void setMapFlag(int piMapFlag)
	{
		iMapFlag = piMapFlag;
	}
	
	/**
	 *   按指定格式, 打印当前域的指定信息
	 * @param  piHEXorASC,    打印数据的形式, eHEXSTR-十六进制字符串, eASCSTR-ASC字符串
	 * @param  piFormatFlag,  打印数据的来源, eFORMAT_N-未格式化前的数据(组包前/解包后的数据), eFORMAT_Y-格式化后的数据(组包后/解包前的数据)
	 * @param  piSpaceFlag,   是否每2个字符后打印一个空格
	 */
	public void print(int piHEXorASC, int piFormatFlag, int piSpaceFlag)
	{
		byte b[];
		String sIdx="";

		if( iFieldIdx < 5 )
		{
			sIdx = String.format(" H%d", iFieldIdx);
		}
		else
		{
			sIdx = String.format("%03d", iFieldIdx-4);
		}

		//log.debug("field["+sIdx+"]:");
		if( ISOF.FORMAT_N == piFormatFlag ) {  //未格式化
			b = bData1;
		}
		else
		{
			b = bData2;
		}

		if( b == null )
		{
			//System.out.println("");
			//log.debug("");
			return;
		}

		String s="";
		if( ISOF.ASCSTR == piHEXorASC )  //ASCII打印
		{
			s = new String(b);
		}
		else   //HEX打印
		{
			s = ISOF.Bytes_HexStr(b);
		}

		char[] arr = s.toCharArray();
		for(int i=0; i<arr.length; i++)
		{
			//System.out.print(arr[i]);

			if( ( ISOF.SPACE_Y == piSpaceFlag ) && ((i+1)%2==0) )
			{
				//System.out.print(" ");
				//log.debug(" ");
			}
		}
		log.debug("field["+sIdx+"]:"+s);
		//System.out.println("");
		log.debug("");
	}

	/**
	 *   条件域处理
	 */
	public void ConditionProc()
	{
		if( ISOF.USE_RQ == iRQRS )   //请求包,才执行条件域处理
		{
			//002: 主账号
			//014: 卡有效期 POS能判断时存在
			//016: 进站站点
			//017: 进站时间
			//018: 出站站点
			//019: 出站时间
			//023: 卡序列号
			//035: 二磁道数据
			//055: IC卡数据域
			//059: 自定义域
			//这些条件域为有数据则存在, 由set方法保证位图标志处理

			//053: 安全控制信息

			//035: 二磁道数据, 加密处理
		}
	}
}
