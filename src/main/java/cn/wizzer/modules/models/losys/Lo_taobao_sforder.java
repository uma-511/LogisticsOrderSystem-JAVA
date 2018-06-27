package cn.wizzer.modules.models.losys;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.Name;
import org.nutz.integration.json4excel.annotation.J4EName;

public class Lo_taobao_sforder implements Serializable {
	private static final long serialVersionUID = 1L;
    @Name
    @J4EName("用户订单号")
    private String id;
    
    @J4EName("收件公司")
    private String company;
    
    @J4EName("收件人")
    private String recipient;
    
    @J4EName("收件电话")
    private String fixedTelephone;
    
    @J4EName("收件手机")
    private String mobilePhone;
    
    @J4EName("收件详细地址")
    private String address;
    
    @J4EName("托寄物内容")
    private String mailingContent;
    
    @J4EName("托寄物数量")
    private String quantity;
    
    @J4EName("包裹重量")
    private String pageWeight;

    @J4EName("寄方备注")
    private String remark;
    
    @J4EName("运费付款方式")
    private String pay;
    
    @J4EName("业务类型")
    private String type;
    
    @J4EName("件数")
    private String number;
    
    @J4EName("代收金额")
    private String amount;
     
    @J4EName("保价金额")
    private String insured;
    
    @J4EName("标准化包装")
    private String standardized;
    
    @J4EName("个性化包装")
    private String personalized;
    
    @J4EName("签回单")
    private String receipt;
    
    @J4EName("自取件")
    private String self;
    
    @J4EName("易碎件")
    private String breakable;
    
    @J4EName("电子验收")
    private String electronic;
    
    @J4EName("超长超重服务费")
    private String servicFee;
    
    @J4EName("是否定时派送")
    private String dispatch;
    
    @J4EName("派送日期")
    private String dispatchDate;
    
    @J4EName("派送时段")
    private String interval;
    
    @J4EName("扩展字段")
    private String field;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
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

	public String getMailingContent() {
		return mailingContent;
	}

	public void setMailingContent(String mailingContent) {
		this.mailingContent = mailingContent;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getPageWeight() {
		return pageWeight;
	}

	public void setPageWeight(String pageWeight) {
		this.pageWeight = pageWeight;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getInsured() {
		return insured;
	}

	public void setInsured(String insured) {
		this.insured = insured;
	}

	public String getStandardized() {
		return standardized;
	}

	public void setStandardized(String standardized) {
		this.standardized = standardized;
	}

	public String getPersonalized() {
		return personalized;
	}

	public void setPersonalized(String personalized) {
		this.personalized = personalized;
	}

	public String getReceipt() {
		return receipt;
	}

	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}

	public String getSelf() {
		return self;
	}

	public void setSelf(String self) {
		this.self = self;
	}

	public String getBreakable() {
		return breakable;
	}

	public void setBreakable(String breakable) {
		this.breakable = breakable;
	}

	public String getElectronic() {
		return electronic;
	}

	public void setElectronic(String electronic) {
		this.electronic = electronic;
	}

	public String getServicFee() {
		return servicFee;
	}

	public void setServicFee(String servicFee) {
		this.servicFee = servicFee;
	}

	public String getDispatch() {
		return dispatch;
	}

	public void setDispatch(String dispatch) {
		this.dispatch = dispatch;
	}

	public String getDispatchDate() {
		return dispatchDate;
	}

	public void setDispatchDate(String dispatchDate) {
		this.dispatchDate = dispatchDate;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	
    
    
}
