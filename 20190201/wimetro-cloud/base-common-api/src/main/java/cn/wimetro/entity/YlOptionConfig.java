package cn.wimetro.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
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
@TableName("YL_OPTION_CONFIG")
@ApiModel(value="YlOptionConfig对象", description="")
public class YlOptionConfig extends Model<YlOptionConfig> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private BigDecimal id;

    @TableField("CODE_GROUP")
    private String codeGroup;

    @TableField("CODE_CODE")
    private String codeCode;

    @TableField("CODE_VALUE")
    private String codeValue;

    @TableField("CODE_COMMENT")
    private String codeComment;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
