package cn.wizzer.modules.controllers.platform.losys;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.losys.Lo_logistics;
import cn.wizzer.modules.models.sys.Sys_unit;
import cn.wizzer.modules.services.losys.LosysLogisticsService;
import cn.wizzer.modules.services.sys.SysMenuService;
import cn.wizzer.modules.services.sys.SysUnitService;
import cn.wizzer.modules.services.sys.SysUserService;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.*;
import org.nutz.dao.Chain;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.WhaleAdaptor;
import org.nutz.mvc.annotation.*;
import com.mysql.fabric.xmlrpc.base.Data;




@IocBean
@At("/platform/losys/logistics")
@Filters({@By(type = PrivateFilter.class)})
public class LosysLogisticsController {
    private static final Log log = Logs.get();
    @Inject
    private LosysLogisticsService logisticsService;

    /**
     * 访问物流公司模块首页
     */
    @At("")
    @Ok("beetl:/platform/losys/logistics/index.html")
    @RequiresAuthentication
    public void index() {
    
    }
    
    /**
     * 访问物流公司模块添加物流公司页面
     */
    @At
    @Ok("beetl:/platform/losys/logistics/add.html")
    @RequiresAuthentication
    public void add() {
    	
    }

    /**
     * 访问物流公司模块修改物流公司页面
     */
    @At("/edit/?")
    @Ok("beetl:/platform/losys/logistics/edit.html")
    @RequiresPermissions("logistics.manage.edit")
    public Object edit(String id,HttpServletRequest req) {
    	Lo_logistics logistics = logisticsService.fetch(id);
    	req.setAttribute("logistics", logistics);
    	return logistics;
    }
    
    /**
     * 添加物流公司
     */
	@At
    @Ok("json")
    @RequiresPermissions("logistics.manage.add")
    @SLog(tag = "添加物流公司", msg = "商品名称:${args[0].name}")
	@AdaptBy(type = WhaleAdaptor.class)
    public Object addDo(@Param("..") Lo_logistics logistics, HttpServletRequest req) {
        try {
        	logistics.setOpAt((int)System.currentTimeMillis());
        	logistics.setDelFlag(false);
            logisticsService.insert(logistics);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }
    
    
    
    /**
     * 删除物流公司 
     */
    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("logistics.manage.delete")
    @SLog(tag = "删除物流公司", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String oneId, @Param("ids") String[] ids, HttpServletRequest req) {
    	try {
        	if (ids != null && ids.length > 0) {
            	for (String string : ids) {
            		//logisticsService.delete(string);
            		logisticsService.dao().update(Lo_logistics.class, Chain.make("delFlag", true), Cnd.where("id","=",string));
            	}
                req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
            } else {
            	//logisticsService.delete(oneId);
            	logisticsService.dao().update(Lo_logistics.class, Chain.make("delFlag", true), Cnd.where("id","=",oneId));
                req.setAttribute("id", oneId);
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }
    
	/**
	 * 修改物流公司
	 */
    @At
    @Ok("json")
    @RequiresPermissions("logistics.manage.edit")
    @SLog(tag = "修改物流公司", msg = "商品名称:${args[0].name}")
	@AdaptBy(type = WhaleAdaptor.class)
    public Object editDo(@Param("..") Lo_logistics logistics, HttpServletRequest req) {	
        try {
        	logisticsService.update(logistics);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }
    
    /**
     * 查询数据
     */
    
    @At
    @Ok("json:full")
    @RequiresAuthentication
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
        cnd.and("delFlag", "=", '0');
        return logisticsService.data(length, start, draw, order, columns, cnd, null);
        
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
