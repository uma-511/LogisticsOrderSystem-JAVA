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
@Table("lo_logistics")
@Comment("物流公司")
public class Lo_logistics extends Model implements Serializable {
	private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;
    
    @Column
    @Comment("公司名称")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String name;
    
    @Column
    @Comment("体积变量 ")
    @ColDefine(type = ColType.VARCHAR, width = 10)
    private String size;
    
    @Column
    @Comment("比较变量 *0.75或者+25")
    @ColDefine(type = ColType.VARCHAR, width = 10)
    private String compare;
    
    @Column
    @Comment("公式")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String formula;
    
	@Column
	@Comment("超长值 单位：cm")
	@ColDefine(type = ColType.VARCHAR, width = 10)
	private String value;
	
	@Column
	@Comment("超长数量")
	@ColDefine(type = ColType.VARCHAR, width = 2)
	private String quantity;
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getCalType() {
		return calType;
	}

	public void setCalType(String calType) {
		this.calType = calType;
	}

	@Column
	@Comment("超长收费计算参考 1：重量， 2：超长（单纯超长）")
	@ColDefine(type = ColType.VARCHAR, width = 2)
	private String calType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getCompare() {
		return compare;
	}

	public void setCompare(String compare) {
		this.compare = compare;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
