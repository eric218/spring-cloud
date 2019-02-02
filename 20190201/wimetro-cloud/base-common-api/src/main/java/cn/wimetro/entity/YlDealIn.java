package cn.wimetro.entity;

import java.math.BigDecimal;
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
 * @since 2019-01-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("YL_DEAL_IN")
@ApiModel(value="YlDealIn对象", description="")
public class YlDealIn extends Model<YlDealIn> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "EVENT_NO", type = IdType.AUTO)
    private String eventNo;

    @TableField("SAVE_TIME")
    private Date saveTime;

    @TableField("DEAL_DEVICE_CODE")
    private String dealDeviceCode;

    @TableField("DEAL_SEQ_GROUP_NO")
    private String dealSeqGroupNo;

    @TableField("DEAL_SEQ_NO")
    private String dealSeqNo;

    @TableField("DEAL_STATION")
    private String dealStation;

    @TableField("DEAL_TYPE")
    private String dealType;

    @TableField("MAIN_TYPE")
    private String mainType;

    @TableField("SUB_TYPE")
    private String subType;

    @TableField("AREA_CODE")
    private String areaCode;

    @TableField("SAM_CODE")
    private String samCode;

    @TableField("LOGICAL_CODE")
    private String logicalCode;

    @TableField("READ_COUNT")
    private Integer readCount;

    @TableField("DEAL_AMOUNT")
    private BigDecimal dealAmount;

    @TableField("BALANCE")
    private BigDecimal balance;

    @TableField("DEAL_TIME")
    private Date dealTime;

    @TableField("TAC")
    private String tac;

    @TableField("OPERATION_DATE")
    private Date operationDate;

    @TableField("DEAL_MODE")
    private String dealMode;

    @TableField("REC_FLAG")
    private String recFlag;

    @TableField("MATCH_FLAG")
    private String matchFlag;

    @TableField("MATCH_TIME")
    private Date matchTime;

    @TableField("ACC_FLAG")
    private String accFlag;

    @TableField("ACC_FILE_NAME")
    private String accFileName;

    @TableField("ACC_FILE_TIME")
    private Date accFileTime;

    @TableField("ACC_FILE_LINE_CODE")
    private BigDecimal accFileLineCode;

    @TableField("SPLIT_TIME")
    private Date splitTime;


    @Override
    protected Serializable pkVal() {
        return this.eventNo;
    }

}
