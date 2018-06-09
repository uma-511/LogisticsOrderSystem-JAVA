package cn.wizzer.modules.models.losys;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.integration.json4excel.annotation.J4EIgnore;
import org.nutz.integration.json4excel.annotation.J4EName;

import cn.wizzer.common.base.Model;
@Table("lo_taobao_orders")
public class Lo_taobao_orders extends Model implements Serializable {
	private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @J4EName("订单ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;
    
    @J4EIgnore
    @Column
    @Comment("日期")
    @ColDefine(type = ColType.INT, width = 32)
    private long orderDate;
    
    @Column
    @Comment("买家会员名")
    @J4EName("买家会员名")
    @ColDefine(type = ColType.VARCHAR, width = 30)
    private String account;
    
    @Column
    @Comment("收货人姓名")
    @J4EName("收货人姓名")
    @ColDefine(type = ColType.VARCHAR, width = 30)
    private String recipient;
    
    @Column
    @Comment("联系电话")
    @J4EName("联系电话")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String fixedTelephone;
    
    @Column
    @Comment("联系手机")
    @J4EName("联系手机")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String mobilePhone;
    
    @Column
    @Comment("收货地址")
    @J4EName("收货地址")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String address;
    
    @Column
    @Comment("托寄物型号")
    @J4EName("托寄物型号")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String mailingModel;
    
    @Column
    @Comment("尺寸（长宽高）")
    @J4EName("尺寸（长宽高）")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String size;
    
    @Column
    @Comment("宝贝总数量")
    @J4EName("宝贝总数量")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String quantity;
    
    @Column
    @Comment("颜色")
    @J4EName("颜色")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String color;
    
    @Column
    @Comment("选择发货物流")
    @J4EName("选择发货物流")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String Logistics;
    
    @Column
    @Comment("备注")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String remark;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(long orderDate) {
		this.orderDate = orderDate;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getFixedTelephone() {
		return fixedTelephone;
	}

	public void setFixedTelephone(String fixedTelephone) {
		this.fixedTelephone = fixedTelephone;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMailingModel() {
		return mailingModel;
	}

	public void setMailingModel(String mailingModel) {
		this.mailingModel = mailingModel;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getLogistics() {
		return Logistics;
	}

	public void setLogistics(String logistics) {
		Logistics = logistics;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
    
    
}
