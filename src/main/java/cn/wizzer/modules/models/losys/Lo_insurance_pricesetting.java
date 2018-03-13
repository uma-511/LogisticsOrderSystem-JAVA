package cn.wizzer.modules.models.losys;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import cn.wizzer.common.base.Model;
@Table(value = "lo_insurance_pricesetting")
@Comment("保价管理设置")
public class Lo_insurance_pricesetting extends Model implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    private int id;

	@Column
	@Comment("保价ID")
	@ColDefine(type = ColType.VARCHAR, width = 32)
	private String insuranceId;
	
	@Column
	@Comment("运算符   大于：> 小于：< 等于：= 小于等于：<= 大于等于：>=")
	@ColDefine(type = ColType.VARCHAR, width = 4)
	private String operator;

	@Column
	@Comment("保价")
	@ColDefine(type = ColType.VARCHAR, width = 10)
	private String insurance;
	
	@Column
	@Comment("收费类型  1：固定金额， 2：百分比")
	@ColDefine(type = ColType.VARCHAR, width = 1)
	private String type;

	@Column
	@Comment("收费")
	@ColDefine(type = ColType.VARCHAR, width = 10)
	private String value;
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getInsuranceId() {
		return insuranceId;
	}

	public void setInsuranceId(String insuranceId) {
		this.insuranceId = insuranceId;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getInsurance() {
		return insurance;
	}

	public void setInsurance(String insurance) {
		this.insurance = insurance;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
