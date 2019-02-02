package cn.wimetro.pos;


import cn.wimetro.constants.YlConstants;

/**
 *   预授权完成交易对象
 */

public class TradeAuthFi
{
	//输入参数
	public  byte[] bIn_CardNo=new byte[0];       //[02]卡号
	public  byte[] bIn_Money=new byte[0];        //[04]交易金额
	public  byte[] bIn_TradeNo=new byte[0];      //[11]闸机交易流水号
	public  byte[] bIn_ExpDate=new byte[0];      //[14]卡有效期
	public  byte[] bIn_InStat=new byte[0];       //[16]进站站点
	public  byte[] bIn_InDT=new byte[0];         //[17]进站日期时间
	public  byte[] bIn_OutStat=new byte[0];      //[18]出站站点
	public  byte[] bIn_OutDT=new byte[0];        //[19]出站日期时间
	public  byte[] bIn_CardIdx=new byte[0];      //[23]卡序列号
	public  byte[] bIn_Mag2=new byte[0];         //[35]二磁道数据
	public  byte[] bIn_AIRC=new byte[0];         //[38]授权标识应答码[从原交易的'38'获取]
	public  byte[] bIn_BatNo=new byte[0];        //[60.2]批次号
	public  byte[] bIn_Old_BatNo=new byte[0];    //[61.1]原交易批次号[从原交易的'60.2'获取]
	public  byte[] bIn_Old_TradeNo=new byte[0];  //[61.2]原交易流水号[从原交易的'11'获取]
	public  byte[] bIn_Old_TradeDT=new byte[0];  //[61.3]原交易日期[从原交易的'13'获取]

	//输出参数[使用get方法,指定域编号,获取域的值, 特殊子域使用下面的变量获取]
	public  byte[] bOut_Money=new byte[12];       //[62.1]实际扣款金额
	public  byte[] bOut_CCCCode=new byte[3];     //[63.1]返回的信用卡公司代码

	private ISO8583 trade_rq;    //发送包处理对象
	private ISO8583 trade_rs;    //接收包处理对象

	public TradeAuthFi()
	{
		trade_rq = new ISO8583(ISOF.USE_RQ);
		trade_rs = new ISO8583(ISOF.USE_RS);
	}

	/**
	 *   组包
	 */
	public PKGResult seal()
	{
		//61域 原批次号 流水号 交易日期
		String s=(new String(bIn_Old_BatNo))+(new String(bIn_Old_TradeNo))+(new String(bIn_Old_TradeDT));
		byte[] bIn_Ori=s.getBytes();
		byte[] bBatNo2= (new String("20"+(new String(bIn_BatNo)+"000"))).getBytes();
		byte[] tmpMacMap={0, 0, 0, 0, 0, 0, 0, 0};

		//替换二磁道数据的"="
		for(int i=0; i<bIn_Mag2.length; i++)
		{
			if( bIn_Mag2[i] == (byte)61 ) 
			{
				bIn_Mag2[i] = (byte)68;
			}
		}

		//报文头
		trade_rq.set(1, ISOF.gTPDU.getBytes());
		trade_rq.set(2, ISOF.gHEAD.getBytes());
		trade_rq.set(3, (new String("0200")).getBytes());
		trade_rq.set(4, tmpMacMap);   //要先置一个临时位图,以得到占位标志,其实际值在组包时自动处理

		////数据域
		trade_rq.setField(2 , bIn_CardNo);     //002:主账号(卡号),【根据实际填写】
		trade_rq.setField(3 , YlConstants.UNION_PAY_AUTH_FRI.getBytes());    //003:交易处理码,固定值'000000'
		trade_rq.setField(4 , bIn_Money);      //004:交易金额,按照地铁最高单程票价格填写,以分为单位,【根据实际填写】
		trade_rq.setField(11, bIn_TradeNo);    //011:闸机交易流水号,【根据实际填写】
		trade_rq.setField(14, bIn_ExpDate);    //014:卡有效期,格式'YYMM',【根据实际填写】
		trade_rq.setField(16, bIn_InStat);     //016:进站站点,【可选】,【根据实际填写】
		trade_rq.setField(17, bIn_InDT);       //017:进站时间,格式'YYYYMMDDHHMMSS',【可选】,【根据实际填写】
		trade_rq.setField(18, bIn_OutStat);    //018:出站站点,【可选】,【根据实际填写】
		trade_rq.setField(19, bIn_OutDT);      //019:出站时间,格式'YYYYMMDDHHMMSS',【可选】,【根据实际填写】
		trade_rq.setField(22, (new String("072")).getBytes());       //022:服务点输入方式码,固定值'072'
		trade_rq.setField(23, bIn_CardIdx);    //023:卡片序列号,【可选】,【根据实际填写】
		trade_rq.setField(25, (new String("06")).getBytes());        //025:服务点条件码,固定值'06'
		trade_rq.setField(35, bIn_Mag2);       //035:二磁道数据,【可选】,【根据实际填写】
		trade_rq.setField(38, bIn_AIRC);       //038:授权标识应答码,原始预授权的38域获取
		trade_rq.setField(41, ISOF.gTerminal_no.getBytes());  //041:终端编号,【根据实际填写】
		trade_rq.setField(42, ISOF.gMerct_no.getBytes());     //042:商户代码,【根据实际填写】
		trade_rq.setField(43, ISOF.gSys_no.getBytes());       //043:地铁前置系统标识,【根据实际填写】
		trade_rq.setField(49, ISOF.gMoney_code.getBytes());   //049:交易货币代码,固定值'156'
		//trade_rq.setField(53, ISOF.gSecurety);     //053:安全控制信息,固定值'061'
		trade_rq.setField(60, bBatNo2);        //060:自定义数据
		trade_rq.setField(61, bIn_Ori);        //061:原始信息
		trade_rq.setField(64, tmpMacMap);      //064:MAC,需要计算MAC域的包,要先置一个临时位图,以得到占位标识, 其实际值在组包的时候计算附加到包后
		trade_rq.ConditionProc();

		//组包
		return trade_rq.seal();
	}

	/**
	 *   解包
	 * @param pbPkg,         需要解包的数据, 从TPDU-MAC的数据, 不包括长度
	 */
	public PKGResult unseal(byte pbPkg[])
	{
		PKGResult res;

		res = trade_rs.unseal( pbPkg );
		if( res.iResult < 0 ) 
		{
			return res;
		}

		//置需要给出的特殊数据,需要定义对应的变量来存储
		//1:解62域的实际扣款金额
		byte b[] = trade_rs.getField(62);
		
		if( b != null )
		{
			System.arraycopy(b,0,bOut_Money,0,12);
		}
		//2:解63域的国际信用卡公司代码
		byte b1[] = trade_rs.getField(63);
		if( b1 != null )
		{
			System.arraycopy(b1,0,bOut_CCCCode,0,3);
		}

		return res;
	}

	/**
	
	 *   取得解包后的数据
	 */
	public byte[] getField(int piFieldIdx)
	{
		return trade_rs.getField( piFieldIdx );
	}
}
