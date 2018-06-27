package cn.wizzer.modules.models.losys;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.Name;
import org.nutz.integration.json4excel.annotation.J4EName;

public class Lo_taobao_kyorder implements Serializable {
	private static final long serialVersionUID = 1L;
    
    @J4EName("序号")
    private String orderid;
    
    @J4EName("收件公司")
    private String company;
    
    @J4EName("收件地址")
    private String address;
    
    @J4EName("收件人名")
    private String recipient;
    
    @J4EName("收方座机")
    private String fixedTelephone;
    
    @J4EName("收件手机")
    private String mobilePhone;
    
    @J4EName("寄件公司")
    private String mailCompany;
    
    @J4EName("寄件地址")
    private String mailAddress;
    
    @J4EName("寄件人名")
    private String mailRecipient;
    
    @J4EName("寄件手机")
    private String mailPhone;
    
    @J4EName("寄方座机")
    private String mailTelephone;
    
    @J4EName("服务方式")
    private String service;
    
    @J4EName("付款方式")
    private String Pay;
    
    @J4EName("托寄物")
    private String mailing;
    
    @J4EName("件数")
    private String quantity;
    
    @J4EName("重量")
    private String weight;
    
    @J4EName("付款账号")
    private String payAccount;
    
    @J4EName("签回单")
    private String receipt;
    
    @J4EName("产品编号")
    private String productNumber;
    
    @J4EName("代收货款")
    private String collectionGoods;
    
    @J4EName("声明价值")
    private String declarations;
    
    @J4EName("寄件备注")
    private String remark;
    
    @J4EName("京东预约时间")
    private String appointmentTime;
    
    @J4EName("京东预约编号")
    private String appointmentNum;
    
    @J4EName("入库号")
    private String WarehousingNum;
    
    @J4EName("代理公司")
    private String agencyCompany;
    
    @J4EName("尺寸1")
    private String sizeOne;
    @J4EName("尺寸2")
    private String sizeTwo;
    @J4EName("尺寸3")
    private String sizeThree;
    @J4EName("尺寸4")
    private String sizeFour;
    @J4EName("尺寸5")
    private String sizeFive;
    @J4EName("尺寸6")
    private String sizeSix;
    @J4EName("尺寸7")
    private String sizeSeven;
    @J4EName("尺寸8")
    private String sizeEight;
    
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
	public String getMailCompany() {
		return mailCompany;
	}
	public void setMailCompany(String mailCompany) {
		this.mailCompany = mailCompany;
	}
	public String getMailAddress() {
		return mailAddress;
	}
	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}
	public String getMailRecipient() {
		return mailRecipient;
	}
	public void setMailRecipient(String mailRecipient) {
		this.mailRecipient = mailRecipient;
	}
	public String getMailPhone() {
		return mailPhone;
	}
	public void setMailPhone(String mailPhone) {
		this.mailPhone = mailPhone;
	}
	public String getMailTelephone() {
		return mailTelephone;
	}
	public void setMailTelephone(String mailTelephone) {
		this.mailTelephone = mailTelephone;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getPay() {
		return Pay;
	}
	public void setPay(String pay) {
		Pay = pay;
	}
	public String getMailing() {
		return mailing;
	}
	public void setMailing(String mailing) {
		this.mailing = mailing;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getPayAccount() {
		return payAccount;
	}
	public void setPayAccount(String payAccount) {
		this.payAccount = payAccount;
	}
	public String getReceipt() {
		return receipt;
	}
	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}
	public String getProductNumber() {
		return productNumber;
	}
	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}
	public String getCollectionGoods() {
		return collectionGoods;
	}
	public void setCollectionGoods(String collectionGoods) {
		this.collectionGoods = collectionGoods;
	}
	public String getDeclarations() {
		return declarations;
	}
	public void setDeclarations(String declarations) {
		this.declarations = declarations;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getAppointmentTime() {
		return appointmentTime;
	}
	public void setAppointmentTime(String appointmentTime) {
		this.appointmentTime = appointmentTime;
	}
	public String getAppointmentNum() {
		return appointmentNum;
	}
	public void setAppointmentNum(String appointmentNum) {
		this.appointmentNum = appointmentNum;
	}
	public String getWarehousingNum() {
		return WarehousingNum;
	}
	public void setWarehousingNum(String warehousingNum) {
		WarehousingNum = warehousingNum;
	}
	public String getAgencyCompany() {
		return agencyCompany;
	}
	public void setAgencyCompany(String agencyCompany) {
		this.agencyCompany = agencyCompany;
	}
	public String getSizeOne() {
		return sizeOne;
	}
	public void setSizeOne(String sizeOne) {
		this.sizeOne = sizeOne;
	}
	public String getSizeTwo() {
		return sizeTwo;
	}
	public void setSizeTwo(String sizeTwo) {
		this.sizeTwo = sizeTwo;
	}
	public String getSizeThree() {
		return sizeThree;
	}
	public void setSizeThree(String sizeThree) {
		this.sizeThree = sizeThree;
	}
	public String getSizeFour() {
		return sizeFour;
	}
	public void setSizeFour(String sizeFour) {
		this.sizeFour = sizeFour;
	}
	public String getSizeFive() {
		return sizeFive;
	}
	public void setSizeFive(String sizeFive) {
		this.sizeFive = sizeFive;
	}
	public String getSizeSix() {
		return sizeSix;
	}
	public void setSizeSix(String sizeSix) {
		this.sizeSix = sizeSix;
	}
	public String getSizeSeven() {
		return sizeSeven;
	}
	public void setSizeSeven(String sizeSeven) {
		this.sizeSeven = sizeSeven;
	}
	public String getSizeEight() {
		return sizeEight;
	}
	public void setSizeEight(String sizeEight) {
		this.sizeEight = sizeEight;
	}

	
    
    
}
