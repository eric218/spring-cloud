package cn.wimetro.pos;

public class TradeResetKey
{
	public  byte[] bIn_Date={0};      		//输入时间MMDDhhmmss
	public  byte[] bIn_TradeNo={0};      	//输入的闸机交易流水号
	public  byte[] bIn_retCode={0}; 		//响应码

	//输出参数[使用get方法,指定域编号,获取域的值]
	private ISO8583 trade_rq;    //发送包处理对象
	private ISO8583 trade_rs;    //接收包处理对象

	public TradeResetKey()
	{
		trade_rq = new ISO8583(ISOF.USE_RQ);
		trade_rs = new ISO8583(ISOF.USE_RS);
	}

	/**
	 *   组包
	 */
	 
	public PKGResult seal()
	{
		byte[] field_60= (new String("00"+"000000"+"101")).getBytes();
		byte tmpMacMap[]={0, 0, 0, 0, 0, 0, 0, 0};

		//报文头
		trade_rq.set(1, ISOF.gTPDU.getBytes());
		trade_rq.set(2, ISOF.gHEAD.getBytes());
		trade_rq.set(3, (new String("0810")).getBytes());
		trade_rq.set(4, tmpMacMap);   //要先置一个临时位图,以得到占位标志,其实际值在组包时自动处理

		////数据域
		trade_rq.setField(7, bIn_Date);        //07
		trade_rq.setField(11, bIn_TradeNo);    //011:闸机交易流水号,【根据实际填写】
		trade_rq.setField(39, bIn_retCode);    //39
		trade_rq.setField(43, ISOF.gSys_no.getBytes());   //43
		trade_rq.setField(60, field_60);       //60
		trade_rq.setField(64, tmpMacMap);      //64
		
		//trade_rq.ConditionProc();

		//组包
		return trade_rq.seal();
	}
	
	
	
	/**
	 *   解包 打印输出信息设置, 1-打印域反格式化后的数据, 2-打印域的原始数据(未反格式化, 不包括变长长度), 3-都打印
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