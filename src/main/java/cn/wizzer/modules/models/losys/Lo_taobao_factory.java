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
@Table("lo_taobao_factory")
public class Lo_taobao_factory extends Model implements Serializable {
	 private static final long serialVersionUID = 1L;
	    @Column
	    @Name
	    @ColDefine(type = ColType.VARCHAR, width = 32)
	    @Prev(els = {@EL("uuid()")})
	    private String id;
	    
	    @Column
	    @Comment("淘宝店id")
	    @ColDefine(type = ColType.VARCHAR, width = 32)
	    private String taobaoid;
	    
		@Column
	    @Comment("工厂id")
	    @ColDefine(type = ColType.VARCHAR, width = 32)
	    private String factoryid;
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getTaobaoid() {
			return taobaoid;
		}

		public void setTaobaoid(String taobaoid) {
			this.taobaoid = taobaoid;
		}

		public String getFactoryid() {
			return factoryid;
		}

		public void setFactoryid(String factoryid) {
			this.factoryid = factoryid;
		}
}
