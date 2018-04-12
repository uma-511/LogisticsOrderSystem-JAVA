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
@Table("lo_orders")
public class Lo_orders extends Model implements Serializable {
	 private static final long serialVersionUID = 1L;
	    @Column
	    @Name
	    @Comment("ID")
	    @ColDefine(type = ColType.VARCHAR, width = 32)
	    @Prev(els = {@EL("uuid()")})
	    private String id;
	    
	    @Column
	    @Comment("淘宝店id")
	    @ColDefine(type = ColType.VARCHAR, width = 32)
	    private String taobaoId;
	    
	    @Column
	    @Comment("工厂id")
	    @ColDefine(type = ColType.VARCHAR, width = 32)
	    private String factoryId;
	    
	    @Column
	    @Comment("淘宝单号")
	    @ColDefine(type = ColType.VARCHAR, width = 32)
	    private String tbId;
	    
	    @Column
	    @Comment("发布时间")
	    @ColDefine(type = ColType.DATETIME, width = 32)
	    private String publishTime;
	    
	    @Column
	    @Comment("确认时间")
	    @ColDefine(type = ColType.DATETIME, width = 32)
	    private String confirmTime;
	    
	    @Column
	    @Comment("待揽件时间")
	    @ColDefine(type = ColType.DATETIME, width = 32)
	    private String waitTime;
	    
		@Column
	    @Comment("待揽件时间")
	    @ColDefine(type = ColType.DATETIME, width = 32)
	    private String packageTime;
	    
	    @Column
	    @Comment("关闭时间")
	    @ColDefine(type = ColType.DATETIME, width = 32)
	    private String closeTime;
	    
	    @Column
	    @Comment("运单号")
	    @ColDefine(type = ColType.VARCHAR, width = 32)
	    private String expNum;

	    @Column
	    @Comment("运费")
	    @ColDefine(type = ColType.FLOAT, width = 32)
	    private String freight;
	    
	    @Column
	    @Comment("备注")
	    @ColDefine(type = ColType.VARCHAR, width = 200)
	    private String remark;
	    
	    @Column
	    @Comment("揽件图片")
	    @ColDefine(type = ColType.VARCHAR, width = 200)
	    private String packagePhoto;
	    
	    @Column
	    @Comment("订单状态 0:未发布 1:已发布 2:已确认（有货） 3:已确认（无货） 4:待揽件 5:已揽件 6:已关闭")
	    @ColDefine(type = ColType.INT)
	    private int orderStatus;
	    
	    @Column
	    @Comment("支付状态 0:未支付 1:已支付")
	    @ColDefine(type = ColType.INT)
	    private int payStatus;
	    
	    @Column
	    @Comment("订单改变的用户id")
	    @ColDefine(type = ColType.VARCHAR, width = 500)
	    private String userId;
	    
	    public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getTaobaoId() {
			return taobaoId;
		}

		public void setTaobaoId(String taobaoId) {
			this.taobaoId = taobaoId;
		}

		public String getFactoryId() {
			return factoryId;
		}

		public void setFactoryId(String factoryId) {
			this.factoryId = factoryId;
		}

		public String getTbId() {
			return tbId;
		}

		public void setTbId(String tbId) {
			this.tbId = tbId;
		}

		public String getPublishTime() {
			return publishTime;
		}

		public void setPublishTime(String publishTime) {
			this.publishTime = publishTime;
		}

		public String getConfirmTime() {
			return confirmTime;
		}

		public void setConfirmTime(String confirmTime) {
			this.confirmTime = confirmTime;
		}

		public String getWaitTime() {
			return waitTime;
		}

		public void setWaitTime(String waitTime) {
			this.waitTime = waitTime;
		}

		public String getPackageTime() {
			return packageTime;
		}

		public void setPackageTime(String packageTime) {
			this.packageTime = packageTime;
		}

		public String getCloseTime() {
			return closeTime;
		}

		public void setCloseTime(String closeTime) {
			this.closeTime = closeTime;
		}

		public String getExpNum() {
			return expNum;
		}

		public void setExpNum(String expNum) {
			this.expNum = expNum;
		}

		public String getFreight() {
			return freight;
		}

		public void setFreight(String freight) {
			this.freight = freight;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public String getPackagePhoto() {
			return packagePhoto;
		}

		public void setPackagePhoto(String packagePhoto) {
			this.packagePhoto = packagePhoto;
		}

		public int getOrderStatus() {
			return orderStatus;
		}

		public void setOrderStatus(int orderStatus) {
			this.orderStatus = orderStatus;
		}

		public int getPayStatus() {
			return payStatus;
		}

		public void setPayStatus(int payStatus) {
			this.payStatus = payStatus;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}
		
}
