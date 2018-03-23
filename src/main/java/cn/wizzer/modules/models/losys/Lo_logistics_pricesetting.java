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

@Table(value = "lo_logistics_pricesetting")
@Comment("公司分组与价格分组中间表")
public class Lo_logistics_pricesetting extends Model implements Serializable {
	private static final long serialVersionUID = 1L;	
    @Id
    private int id;
    
	@Column
	@Comment("价格分组Id")
	@ColDefine(type = ColType.VARCHAR, width = 32)
	private String pricesettingId;

	@Column
	@Comment("公司分组Id")
	@ColDefine(type = ColType.VARCHAR, width = 32)
	private String logisticsGroupId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPricesettingId() {
		return pricesettingId;
	}

	public void setPricesettingId(String pricesettingId) {
		this.pricesettingId = pricesettingId;
	}

	public String getLogisticsGroupId() {
		return logisticsGroupId;
	}

	public void setLogisticsGroupId(String logisticsGroupId) {
		this.logisticsGroupId = logisticsGroupId;
	}

	
}
