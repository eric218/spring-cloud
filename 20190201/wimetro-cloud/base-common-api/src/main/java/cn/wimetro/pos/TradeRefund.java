package cn.wimetro.pos;

/**

 *   退货交易对象
 */

public class TradeRefund
{
	//输入参数
	public  byte[] bIn_CardNo=new byte[0];       //[02]输入的卡号
	public  byte[] bIn_Money=new byte[0];        //[04]输入的交易金额
	public  byte[] bIn_TradeNo=new byte[0];      //[11]输入的闸机交易流水号
	public  byte[] bIn_ExpDate=new byte[0];      //[14]输入的卡有效期
	public  byte[] bIn_CardIdx=new byte[0];      //[23]输入的卡序列号
	public  byte[] bIn_Mag2=new byte[0];         //[35]输入的二磁道数据
	public  byte[] bIn_RRN=new byte[0];          //[37]输入的检索参考号[从原交易的'38'获取]
	public  byte[] bIn_AIRC=new byte[0];         //[38]输入的授权标识应答码[从原交易的'38'获取]
	public  byte[] bIn_BatNo=new byte[0];        //[60.2]输入的批次号
	public  byte[] bIn_Old_BatNo=new byte[0];    //[61.1]输入的原交易批次号[从原交易的'60.2'获取]
	public  byte[] bIn_Old_TradeNo=new byte[0];  //[61.2]输入的原交易流水号[从原交易的'11'获取]
	public  byte[] bIn_Old_TradeDT=new byte[0];  //[61.3]输入的原交易日期[从原交易的'13'获取]
	public  byte[] bIn_CCCCode=new byte[0];      //[63.1]输入的信用卡公司代码

	//输出参数[使用get方法,指定域编号,获取域的值, 特殊子域使用下面的变量获取]

	private ISO8583 trade_rq;    //发送包处理对象
	private ISO8583 trade_rs;    //接收包处理对象

	public TradeRefund()
	{
		trade_rq = new ISO8583(ISOF.USE_RQ);
		trade_rs = new ISO8583(ISOF.USE_RS);
	}

	/**
 
	 *   组包打印输出信息设置, 1-打印域未格式化之前的数据, 2-打印格式化之后的数据, 3-都打印
	 */
	public PKGResult seal()
	{
		String s=(new String(bIn_Old_BatNo))+(new String(bIn_Old_TradeNo))+(new String(bIn_Old_TradeDT));
		byte[] bIn_Ori=s.getBytes();
		byte[] bBatNo2= (new String("25"+(new String(bIn_BatNo)+"000"))).getBytes();
		byte[] tmpMacMap={0, 0, 0, 0, 0, 0, 0, 0};

		//替换二磁道数据的"="
		for(int i=0; i<bIn_Mag2.length; i++)
		{
			if( bIn_Mag2[i] == (byte)61 ) {
				bIn_Mag2[i] = (byte)68;
			}
		}

		//报文头
		trade_rq.set(1, ISOF.gTPDU.getBytes());
		trade_rq.set(2, ISOF.gHEAD.getBytes());
		trade_rq.set(3, (new String("0220")).getBytes());
		trade_rq.set(4, tmpMacMap);   //要先置一个临时位图,以得到占位标志,其实际值在组包时自动处理

		////数据域
		trade_rq.setField(2 , bIn_CardNo);     //002:主账号(卡号),【根据实际填写】
		trade_rq.setField(3 , (new String("200000")).getBytes());    //003:交易处理码,固定值'000000'
		trade_rq.setField(4 , bIn_Money);      //004:交易金额,按照地铁最高单程票价格填写,以分为单位,【根据实际填写】
		trade_rq.setField(11, bIn_TradeNo);    //011:闸机交易流水号,【根据实际填写】
		trade_rq.setField(14, bIn_ExpDate);    //014:卡有效期,格式'YYMM',【根据实际填写】
		trade_rq.setField(22, (new String("072")).getBytes());       //022:服务点输入方式码,固定值'072'
		//trade_rq.setField(23, bIn_CardIdx);    //023:卡片序列号,【可选】,【根据实际填写】
		trade_rq.setField(25, (new String("00")).getBytes());        //025:服务点条件码,固定值'06'
		//trade_rq.setField(35, bIn_Mag2);       //035:二磁道数据,【可选】,【根据实际填写】
		trade_rq.setField(37, bIn_RRN);        //037:检索参考号,【根据实际填写】
		trade_rq.setField(38, bIn_AIRC);       //038:授权标识应答码,【根据实际填写】
		trade_rq.setField(41, ISOF.gTerminal_no.getBytes());  //041:终端编号,【根据实际填写】
		trade_rq.setField(42, ISOF.gMerct_no.getBytes());     //042:商户代码,【根据实际填写】
		trade_rq.setField(43, ISOF.gSys_no.getBytes());       //043:地铁前置系统标识,【根据实际填写】
		trade_rq.setField(49, ISOF.gMoney_code.getBytes());   //049:交易货币代码,固定值'156'
		//trade_rq.setField(53, ISOF.gSecurety);     //053:安全控制信息,固定值'061'
		trade_rq.setField(60, bBatNo2);        //060:自定义数据
		trade_rq.setField(61, bIn_Ori);        //061:原始信息
		//trade_rq.setField(62, );             //062:自定义数据
		trade_rq.setField(63, bIn_CCCCode);    //063:自定义数据
		trade_rq.setField(64, tmpMacMap);      //064:MAC,需要计算MAC域的包,要先置一个临时位图,以得到占位标识, 其实际值在组包的时候计算附加到包后
		trade_rq.conditionProc();

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
		if( res.iResult < 0 ) {
			return res;
		}

		//置需要给出的特殊数据,需要定义对应的变量来存储

		return res;
	}

	/**
 
	 *   取得解包后的数据
	 */
	public byte[] get(int piFieldIdx)
	{
		return trade_rs.getField( piFieldIdx );
	}
}
