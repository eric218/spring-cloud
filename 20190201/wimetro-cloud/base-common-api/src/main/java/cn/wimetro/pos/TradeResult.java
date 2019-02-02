package cn.wimetro.pos;

public class TradeResult
{
	//输入参数
	public  byte[] bIn_CardNo=new byte[0];       //[02]输入的卡号
	public  byte[] bIn_Money=new byte[0];        //[04]输入的交易金额
	public  byte[] bIn_TradeNo=new byte[0];      //[11]输入的闸机交易流水号
	public  byte[] bIn_BatType=new byte[0];      //[60.1]交易类型码          预授权01 预授权完成02 预授权撤销03                退货04 
	public  byte[] bIn_BatNo=new byte[0];        //[60.2]批次号
	public  byte[] bIn_Old_BatNo=new byte[0];    //[61.1]输入的原交易批次号[从原交易的'60.2'获取]
	public  byte[] bIn_Old_TradeNo=new byte[0];  //[61.2]输入的原交易流水号[从原交易的'11'获取]
	public  byte[] bIn_Old_TradeDT=new byte[0];  //[61.3]输入的原交易日期[从原交易的'13'获取]
	//输出参数[使用get方法,指定域编号,获取域的值]


	private ISO8583 trade_rq;    //发送包处理对象
	private ISO8583 trade_rs;    //接收包处理对象

	public TradeResult()
	{
		trade_rq = new ISO8583(ISOF.USE_RQ);
		trade_rs = new ISO8583(ISOF.USE_RS);
	}

	/**
	
	 *   组包
	 * @return PKGResult,  iResult存放错误代码, bResult存放组包结果
	 */
	public PKGResult seal()
	{
		byte[] field_60 = (new String(bIn_BatType) + new String(bIn_BatNo) + "000").getBytes();
		byte[] field_61 = (new String(bIn_Old_BatNo) + new String(bIn_Old_TradeNo) + new String(bIn_Old_TradeDT)).getBytes();
		byte tmpMacMap[]={0, 0, 0, 0, 0, 0, 0, 0};

		//报文头
		trade_rq.set(1, ISOF.gTPDU.getBytes());
		trade_rq.set(2, ISOF.gHEAD.getBytes());
		trade_rq.set(3, (new String("0240")).getBytes());
		trade_rq.set(4, tmpMacMap);   //要先置一个临时位图,以得到占位标志,其实际值在组包时自动处理

		////数据域
		trade_rq.setField(2 , bIn_CardNo);     						//002:主账号(卡号),【根据实际填写】
		trade_rq.setField(3 , (new String("310000")).getBytes());   //003:交易处理码,固定值'310000'
		trade_rq.setField(4 , bIn_Money);      						//004:交易金额,按照地铁最高单程票价格填写,以分为单位,【根据实际填写】
		trade_rq.setField(11, bIn_TradeNo);    						//011:闸机交易流水号,【根据实际填写】
		trade_rq.setField(25, (new String("00")).getBytes());   	//025:服务点条件码,固定值'00'
		trade_rq.setField(41, ISOF.gTerminal_no.getBytes());  		//041:终端编号,【根据实际填写】
		trade_rq.setField(42, ISOF.gMerct_no.getBytes());    		//042:商户代码,【根据实际填写】
		trade_rq.setField(43, ISOF.gSys_no.getBytes());      		//043:地铁前置系统标识,【根据实际填写】
		trade_rq.setField(60, field_60);        					//060:自定义数据
		trade_rq.setField(61, field_61);        					//061:原始信息
		trade_rq.setField(64, tmpMacMap);      						//064:MAC,需要计算MAC域的包,要先置一个临时位图,以得到占位标识, 其实际值在组包的时候计算附加到包后
		trade_rq.conditionProc();

		//组包
		return trade_rq.seal();
	}
	
	
	/**
	
	 *   解包
	 * @param pbPkg,         需要解包的数据, 从TPDU-MAC的数据, 不包括长度
	 * @return PKGResult,   iResult存放错误代码
	 */
	public PKGResult unseal(byte pbPkg[])
	{
		PKGResult res;

		res = trade_rs.unseal( pbPkg );
		
		//置需要给出的特殊数据,需要定义对应的变量来存储
		
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
