package cn.wizzer.modules.models.losys;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;

import cn.wizzer.common.base.Model;
@Table("lo_taobao_orders")
public class Lo_taobao_orders extends Model implements Serializable {
	private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;
    
    @Column
    @Comment("日期")
    @ColDefine(type = ColType.DATETIME, width = 32)
    private String orderDate;
    
    @Column
    @Comment("旺旺号")
    @ColDefine(type = ColType.VARCHAR, width = 30)
    private String account;
    
    @Column
    @Comment("收件人")
    @ColDefine(type = ColType.VARCHAR, width = 30)
    private String recipient;
    
    @Column
    @Comment("固话")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String fixedTelephone;
    
    @Column
    @Comment("收件手机")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String mobilePhone;
    
    @Column
    @Comment("收件详细地址")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String address;
    
    @Column
    @Comment("托寄物型号")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String mailingModel;
    
    @Column
    @Comment("尺寸（长宽高）")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String size;
    
    @Column
    @Comment("托寄物件数")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String quantity;
    
    @Column
    @Comment("颜色")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String color;
    
    @Column
    @Comment("选择发货物流")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String Logistics;
}
