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
@Table(value = "lo_insurance")
@Comment("保价管理")
public class Lo_insurance extends Model implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column
	@Name
	@Comment("ID")
	@ColDefine(type = ColType.VARCHAR, width = 32)
	@Prev(els = { @EL("uuid()") })
	private String id;

	@Column
	@Comment("物流公司id")
	@ColDefine(type = ColType.VARCHAR, width = 32)
	private String logisticsId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLogisticsId() {
		return logisticsId;
	}

	public void setLogisticsId(String logisticsId) {
		this.logisticsId = logisticsId;
	}
}
