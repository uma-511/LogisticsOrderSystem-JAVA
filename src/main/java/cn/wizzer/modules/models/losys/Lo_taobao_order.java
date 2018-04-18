package cn.wizzer.modules.models.losys;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.Name;
import org.nutz.integration.json4excel.annotation.J4EName;

public class Lo_taobao_order implements Serializable {
	private static final long serialVersionUID = 1L;
    @Name
    @J4EName("订单ID")
    private String id;
    
    @J4EName("日期")
    private String fileDate;
    
    @J4EName("买家会员名")
    private String account;
    
    @J4EName("收货人姓名")
    private String recipient;
    
    @J4EName("联系电话")
    private String fixedTelephone;
    
    @J4EName("联系手机")
    private String mobilePhone;
    
    @J4EName("收货地址")
    private String address;
    
    @J4EName("托寄物型号")
    private String mailingModel;
    
    @J4EName("尺寸（长宽高）")
    private String size;
    
    @J4EName("宝贝总数量")
    private String quantity;
    
    @J4EName("颜色")
    private String color;
    
    @J4EName("选择发货物流")
    private String Logistics;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getFileDate() {
		return fileDate;
	}

	public void setFileDate(String fileDate) {
		this.fileDate = fileDate;
	}
    
    
}
