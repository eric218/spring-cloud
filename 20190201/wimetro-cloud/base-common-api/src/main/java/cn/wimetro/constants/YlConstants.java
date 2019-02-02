package cn.wimetro.constants;

import java.math.BigDecimal;


/**
 *
 * @author wangwei
 * @date  2019-02-02
 **/
public class YlConstants {

	/**
	 * 联机标识 联机
	 */
	public static String YL_RETURN_00="00";

	/**
	 * 联机标识 联机
	 */
	public static int YL_FIELD_39= 39;

	/**
	 * 联机标识 联机
	 */
	public static String IS_ONLINE_YES="1";

	/**
	 * 联机标识 脱机
	 */
	public static String IS_ONLINE_NO="0";

	/**
	 * 闸机开门码 成功
	 */
	public static String DOOR_CDOD_SUCCESSFUL = "0000";

	/**
	 * 闸机开门码  失败
	 */
	public static String DOOR_CDOD_ERROR = "9999";

	/** 交易类型-进闸:26	(进闸:26 出闸:27)  */
	public static final String DEAL_TYPE_IN = "26";
	/** 交易类型-出闸:27	(进闸:26 出闸:27)  */
	public static final String DEAL_TYPE_OUT = "27";
	/** 交易类型-出闸:BOM补登 17  */
	public static final String DEAL_TYPE_BOM = "17";

	/**
	 * 银联交易码  预授权请求
	 */
	public static String UNION_PAY_AUTH_PRE = "030000";

	/**
	 * 银联交易码  预授权完成
	 */
	public static String UNION_PAY_AUTH_FRI = "000000";

	/**
	 * 银联交易码  预授权撤销请求
	 */
	public static String UNION_PAY_AUTH_PRE_CANCE = "200000";

	/**
	 * IAFC  预授权申请
	 */
	public static String IAFC_PAY_AUTH_PRE= "10";

	/**
	 * IAFC  预授权撤销
	 */
	public static String IAFC_PAY_AUTH_PRE_CANCLE = "11";

	/**
	 * IAFC  预授权完成
	 */
	public static String IAFC_PAY_AUTH_FRI = "20";

	/**
	 * IAFC  预授权撤销
	 */
	public static String IAFC_PAY_AUTH_FRI_CANCEL = "25";


	/**
	 * 组包错误
	 */
	public static String SEAL_ERROR = "-2";

	/**
	 * 银联39域返回码
	 */
	public static String BANK_39_RETURN_CDOD_SUCCESSFUL = "00";

	/**
	 * 银联请求标识 成功
	 */
	public static String YL_REQ_FLAG_SUCCESS="0";

	/**
	 * 银联请求标识 失败
	 */
	public static String YL_REQ_FLAG_ERROR="1";

	/**
	 * 银联请求标识 超时
	 */
	public static String YL_REQ_FLAG_OVER_TIME="2";

	/**
	 * 银联请求标识 处理中
	 */
	public static String YL_REQ_FLAG_IN_PROCESS="9";


	/**
	 * 地铁运营时间 凌晨4点
	 */
	public static int OPERATE_TIME = 40000;
	/**
	 * 预授权记录状态 原始交易
	 */
	public static String REC_FLAG_INIT="0";
	/**
	 * 预授权记录状态 被撤销
	 */
	public static String REC_FLAG_CANCLE="1";

	/**
	 * 预授权记录状态 闪卡放行过
	 */
	public static String REC_FLAG_IS_IN_AGAIN="3";

	/**
	 * 预授权完成记录状态 原始交易
	 */
	public static String REC_FLAG_FINISH_INIT="0";
	/**
	 * 预授权完成记录状态 被瑞或
	 */
	public static String REC_FLAG_FINISH_BACK="1";

	/**
	 * 匹配标识 BOM补登初始状态
	 */
	public static String MATCH_FLAG_BOM_DEFAULT="20";
	/**
	 * 匹配标识 默认状态
	 */
	public static String MATCH_FLAG_DEFAULT="00";
	/**
	 * 匹配标识 未匹配
	 */
	public static String MATCH_FLAG_NO="01";

	/**
	 * 匹配标识 匹配成功
	 */
	public static String MATCH_FLAG_SUCCESS="02";
	/**
	 * 匹配标识 撤销初始状态
	 */
	public static String MATCH_FLAG_CANCEL_DEFAULT="30";
	/**
	 * 匹配标识 撤销
	 */
	public static String MATCH_FLAG_CANCEL="03";
	/**
	 * 匹配标识 进站单边
	 */
	public static String MATCH_FLAG_IN_SIDE="13";

	/**
	 * 匹配数据来源 进站数据
	 */
	public static String MATCH_SRC_IN="0";

	/**
	 * 匹配数据来源 更新数据
	 */
	public static String MATCH_SRC_UPDATE="1";

	/**
	 * 是否超时 是
	 */
	public static String IS_OVER_TIME_YES = "0";

	/**
	 * 是否超时 否
	 */
	public static String IS_OVER_TIME_NO = "1";

	/**
	 * 超时时间
	 */
	public static int OVER_TIME = 180;

	/**
	 * 超时罚金
	 */
	public static BigDecimal OVER_FEE = new BigDecimal(11);

	/**
	 * 白名单 是
	 */
	public static String IS_WHITE_YES = "0";

	/**
	 * 白名单 否
	 */
	public static String IS_WHITE_NO = "1";
	/**
	 * 匹配结果返回给读头
	 */
	public static String READER_FLAG_YES = "0";
	/**
	 * 匹配结果未返回给读头
	 */
	public static String READER_FLAG_NO = "1";
	/**
	 * 银联赔付标记 赔付
	 */
	public static String YL_PAY_FLAG_YES= "1";
	/**
	 * 银联赔付标记 不赔付
	 */
	public static String YL_PAY_FLAG_NO= "0";

	/**
	 * 异常种类 超过三天ODA出站
	 */
	public static String EXCEPL_TYPE_OVERTIME="1";
	/**
	 * 异常种类 三天内ODA出站，并且无进站匹配
	 */
	public static String EXCEPL_TYPE_INTIME="0";
	/**
	 * 异常种类 进站单边
	 */
	public static String EXCEPL_TYPE_ONLY_IN="2";
	/**
	 *  BOM进站更新
	 */
	public static String BOM_UPDATE_IN="1";
	/**
	 *  BOM出站更新
	 */
	public static String BOM_UPDATE_OUT="2";
	
}
