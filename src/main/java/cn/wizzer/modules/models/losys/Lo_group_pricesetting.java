package cn.wizzer.modules.models.losys;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;

import cn.wizzer.common.base.Model;

@Table(value = "lo_group_pricesetting")
@Comment("分组价格")
public class Lo_group_pricesetting extends Model implements Serializable {
	private static final long serialVersionUID = 1L;	
    @Id
    private int id;
    
	@Column
	@Comment("分组Id")
	@ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
	private String groupId;

	@Column
	@Comment("运算符   大于：> 小于：< 等于：= 小于等于：<= 大于等于：>= 最高：max 最低：min")
	@ColDefine(type = ColType.VARCHAR, width = 4)
	private String operator;

	@Column
	@Comment("重量 单位：kg")
	@ColDefine(type = ColType.VARCHAR, width = 10)
	private String weight;

	@Column
	@Comment("价格")
	@ColDefine(type = ColType.VARCHAR, width = 10)
	private String price;

	@Column
	@Comment("低消")
	@ColDefine(type = ColType.VARCHAR, width = 10)
	private String min;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}
}
