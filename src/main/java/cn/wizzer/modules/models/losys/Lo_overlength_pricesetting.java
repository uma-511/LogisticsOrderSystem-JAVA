package cn.wizzer.modules.models.losys;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Table;

import cn.wizzer.common.base.Model;
@Table(value = "lo_overlength_pricesetting")
@PK("id")
@Comment("超长价格设置")
public class Lo_overlength_pricesetting extends Model implements Serializable {
	private static final long serialVersionUID = 1L;

    @Column
    @Comment("ID")
    @ColDefine(type = ColType.INT, auto=true)
    private int id;

	@Column
	@Comment("物流公司ID")
	@ColDefine(type = ColType.VARCHAR, width = 32)
	private String logisticsId;
	
	@Column
	@Comment("收费类型  1：固定金额， 2：百分比")
	@ColDefine(type = ColType.VARCHAR, width = 1)
	private String type;
	
	@Column
	@Comment("运算符   大于：> 小于：< 等于：= 小于等于：<= 大于等于：>=")
	@ColDefine(type = ColType.VARCHAR, width = 4)
	private String operator;
	
	@Column
	@Comment("参考值  当key=0时，代表统一金额收费/统一百分比收费")
	@ColDefine(type = ColType.VARCHAR, width = 4)
	private String calKey;
	
	@Column
	@Comment("计算值")
	@ColDefine(type = ColType.VARCHAR, width = 4)
	private String calValue;
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogisticsId() {
		return logisticsId;
	}

	public void setLogisticsId(String logisticsId) {
		this.logisticsId = logisticsId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getCalKey() {
		return calKey;
	}

	public void setCalKey(String calKey) {
		this.calKey = calKey;
	}

	public String getCalValue() {
		return calValue;
	}

	public void setCalValue(String calValue) {
		this.calValue = calValue;
	}
}
