package cn.wizzer.modules.models.losys;

import cn.wizzer.common.base.Model;
import lombok.Getter;
import lombok.Setter;
import org.nutz.dao.entity.annotation.*;
import org.nutz.integration.json4excel.J4EColumnType;
import org.nutz.integration.json4excel.annotation.J4EDateFormat;
import org.nutz.integration.json4excel.annotation.J4EDefine;
import org.nutz.integration.json4excel.annotation.J4EFormat;
import org.nutz.integration.json4excel.annotation.J4EIgnore;
import org.nutz.integration.json4excel.annotation.J4EName;

import java.io.Serializable;

@J4EName("Sheet1")
@Table(value="lo_factory_dataImport")
@Comment("数据导入")
@Getter
@Setter
public class Lo_factory_dataImport extends Model implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column
    @Comment("ID")
    @Name
    @Prev(els = { @EL("uuid()") })
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String id;
    
    @J4EName("日期")
    @Column
    @Comment("日期")
    @J4EIgnore
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String date;

    @J4EName("淘宝店名")
    @Column
    @Comment("淘宝店名")
    @ColDefine(type = ColType.VARCHAR, width = 64)
    private String tbName;

    @J4EName("工厂")
    @Column
    @Comment("工厂")
    @ColDefine(type = ColType.VARCHAR, width = 64)
    private String factory;
    
    @J4EName("收件人")
    @Column
    @Comment("收件人")
    @ColDefine(type = ColType.VARCHAR, width = 64)
    private String addressee;
    
    @J4EName("收件手机")
    @Column
    @Comment("收件手机")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String phone;
    
    @J4EName("收件地址")
    @Column
    @Comment("收件地址")
    @ColDefine(type = ColType.VARCHAR, width = 258)
    private String receivingAddress;

    @J4EName("托寄物内容")
    @Column
    @Comment("物件内容")
    @ColDefine(type = ColType.VARCHAR, width = 258)
    private String objectContent;

    @J4EName("托寄物数量")
    @Column
    @Comment("物件数量")
    @ColDefine(type = ColType.INT)
    private Integer objectNumber;
    
    @J4EName("物流公司")
    @Column
    @Comment("物流公司")
    @ColDefine(type = ColType.VARCHAR, width = 64)
    private String logisticsCompany;

    @J4EName("物流单号")
    @Column
    @Comment("物流单号")
    @ColDefine(type = ColType.VARCHAR, width = 64)
    private String logisticsNo;
    
    @J4EName("费用")
    @Column
    @Comment("费用")
    @ColDefine(type = ColType.VARCHAR, width = 64)
    private String money;

    @J4EName("订单状态")
    @Column
    @Comment("订单状态")
    @Default("未发货")
    @ColDefine(type = ColType.VARCHAR, width = 64)
    private String status;

    @J4EName("备注")
    @Column
    @Comment("备注")
    @ColDefine(type = ColType.VARCHAR, width = 256)
    private String remarks;
}
