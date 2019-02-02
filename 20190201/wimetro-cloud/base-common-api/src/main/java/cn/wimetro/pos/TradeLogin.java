package cn.wimetro.pos;

public class TradeLogin
{
	//输入参数 地铁是 7 11 43 60.1.2.3
	public  byte[] bIn_Date={0};      		//输入时间MMDDhhmmss
	public  byte[] bIn_TradeNo={0};      	//输入的闸机交易流水号
	public  byte[] bIn_BatNo={0};        	//输入的批次号 默认0
	public  byte[] field_62={0};
	//输出参数[使用get方法,指定域编号,获取域的值]


	private ISO8583 trade_rq;    //发送包处理对象
	private ISO8583 trade_rs;    //接收包处理对象

	public TradeLogin()
	{
		trade_rq = new ISO8583(ISOF.USE_RQ);
		trade_rs = new ISO8583(ISOF.USE_RS);
	}

	/**
	
	 *   组包 打印输出信息设置, 1-打印域未格式化之前的数据, 2-打印格式化之后的数据, 3-都打印
	 * @return PKGResult,  iResult存放错误代码, bResult存放组包结果
	 */
	 
	public PKGResult seal()
	{
		byte[] field_60= (new String("00"+"000000"+"101")).getBytes();
		byte tmpMacMap[]={0, 0, 0, 0, 0, 0, 0, 0};

		//报文头
		trade_rq.set(1, ISOF.gTPDU.getBytes());
		trade_rq.set(2, ISOF.gHEAD.getBytes());
		trade_rq.set(3, (new String("0820")).getBytes());
		trade_rq.set(4, tmpMacMap);   //要先置一个临时位图,以得到占位标志,其实际值在组包时自动处理

		////数据域
		trade_rq.setField(7,  bIn_Date);       	//007:交易日期时间,【根据实际填写】
		trade_rq.setField(11, bIn_TradeNo);    		//011:闸机交易流水号,【根据实际填写】
		//trade_rq.setField(41, ISOF.gTerminal_no.getBytes());    //041
		//trade_rq.setField(42, ISOF.gMerct_no.getBytes());    	//042
		trade_rq.setField(43, ISOF.gSys_no.getBytes());   //043:输入的地铁系统ID,【根据实际填写】
		trade_rq.setField(60, field_60);       //060:商户代码,【根据实际填写】
		//trade_rq.setField(62, field_62);
		//trade_rq.setField(63, new String("001").getBytes());
		
		//trade_rq.ConditionProc();

		//组包
		return trade_rq.seal();
	}


	public static void printHexString(byte[] b)
	{
		for(int i=0;i<b.length;i++)
		{
			String hex = Integer.toHexString(b[i]&0xFF);		
			if(hex.length()==1)
			{
					hex='0'+hex;
			}
			System.out.print("0x");
			System.out.print(hex.toUpperCase());
			System.out.print(" ");
		}
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
