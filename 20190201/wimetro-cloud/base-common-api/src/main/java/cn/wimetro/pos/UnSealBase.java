package cn.wimetro.pos;

public class UnSealBase {
    private ISO8583 trade_rs;
    public UnSealBase()
    {
        trade_rs = new ISO8583(ISOF.USE_RS);
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
    public String getBodyField(int piFieldIdx)
    {
        byte[] field = trade_rs.getField( piFieldIdx );
        if(field!=null){
            return  new String(field);
        }else{
            return  null;
        }
    }

    /**

     *   取得解包后的头
     */
    public String getHeadField(int piFieldIdx)
    {
        byte[] field = trade_rs.get( piFieldIdx );
        if(field!=null){
            return  new String(field);
        }else{
            return  null;
        }
    }
}
