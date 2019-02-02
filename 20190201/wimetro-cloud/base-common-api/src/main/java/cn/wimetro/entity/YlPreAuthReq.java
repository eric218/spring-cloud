package cn.wimetro.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangwei
 * @since 2019-01-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("YL_PRE_AUTH_REQ")
@ApiModel(value="YlPreAuthReq对象", description="")
public class YlPreAuthReq extends Model<YlPreAuthReq> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "EVENT_NO", type = IdType.AUTO)
    private String eventNo;

    @TableField("SAVE_TIME")
    private Date saveTime;

    @TableField("DEAL_TYPE")
    private String dealType;

    @TableField("B2_AC_NO")
    private String b2AcNo;

    @TableField("B3_PROCOD")
    private String b3Procod;

    @TableField("B4_AMT")
    private String b4Amt;

    @TableField("B11_SEQNO")
    private String b11Seqno;

    @TableField("B12_TXNTM")
    private String b12Txntm;

    @TableField("B13_TXNDT")
    private String b13Txndt;

    @TableField("B14_EXPDATE")
    private String b14Expdate;

    @TableField("B15_ACC_DT")
    private String b15AccDt;

    @TableField("B37_SREFNO")
    private String b37Srefno;

    @TableField("B38_AUTCOD_REC")
    private String b38AutcodRec;

    @TableField("B39_RESPCOD_REC")
    private String b39RespcodRec;

    @TableField("B41_TERM_NO")
    private String b41TermNo;

    @TableField("B42_MERCT_CODE")
    private String b42MerctCode;

    @TableField("B44_REC_ORG")
    private String b44RecOrg;

    @TableField("B55_IC")
    private String b55Ic;

    @TableField("B60_US1_MSG_ID")
    private String b60Us1MsgId;

    @TableField("B60_US2_MSG_BTH")
    private String b60Us2MsgBth;

    @TableField("B60_US3_NET_INFO_NO")
    private String b60Us3NetInfoNo;

    @TableField("B62_REC_ORG")
    private String b62RecOrg;

    @TableField("B63_US1_MSG_ID")
    private String b63Us1MsgId;

    @TableField("REC_FLAG")
    private String recFlag;

    @TableField("REQ_TIME")
    private Date reqTime;

    @TableField("REQ_FINISH_TIME")
    private Date reqFinishTime;

    @TableField("REQ_FLAG")
    private String reqFlag;

    @TableField("SPLIT_TIME")
    private Date splitTime;

    @TableField("B61_US1_ORI_BTH")
    private String b61Us1OriBth;

    @TableField("B61_US2_ORI_WAT")
    private String b61Us2OriWat;

    @TableField("B61_US3_ORI_DATE")
    private String b61Us3OriDate;

    @TableField("ORG_EVENT_NO")
    private String orgEventNo;

    @TableField("MATCH_FLAG")
    private String matchFlag;

    @TableField("MATCH_TIME")
    private Date matchTime;


    @Override
    protected Serializable pkVal() {
        return this.eventNo;
    }

}
