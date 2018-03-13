package cn.wizzer.modules.models.losys;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Table;

import cn.wizzer.common.base.Model;
@Table(value="lo_area")
@PK("id")
@Comment("区域")
public class Lo_area extends Model implements Serializable {
	private static final long serialVersionUID = 1L;

    @Column
    @Comment("ID")
    @ColDefine(type = ColType.INT, auto=true)
    private String id;
    
    @Column
    @Comment("父级ID")
    @ColDefine(type = ColType.INT)
    private String pid;
    
    @Column
    @Comment("区域名称")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String name;
    
    @Column
    @Comment("路径 所有父级id以-隔开")
    @ColDefine(type = ColType.VARCHAR, width = 30)
    private String path;
    
    @Column
    @Comment("是否存在子级  存在：1 不存在：0")
    @ColDefine(type = ColType.INT)
    private String hasChild;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getHasChild() {
		return hasChild;
	}

	public void setHasChild(String hasChild) {
		this.hasChild = hasChild;
	}
}
