package cn.wizzer.modules.models.losys;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import cn.wizzer.common.base.Model;
@Table(value = "lo_area_price")
@Comment("区域价格")
public class Lo_area_price extends Model implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private int id;
	
	@Column
	@Comment("物流公司id")
	@ColDefine(type = ColType.VARCHAR, width = 32)
	private String logisticsId;
	
	@Column
    @Comment("区域id")
    @ColDefine(type = ColType.INT)
    private String areaId;
	
	@Column
	@Comment("分组Id")
	@ColDefine(type = ColType.VARCHAR, width = 32)
	private String groupId;
	
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

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

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
}
