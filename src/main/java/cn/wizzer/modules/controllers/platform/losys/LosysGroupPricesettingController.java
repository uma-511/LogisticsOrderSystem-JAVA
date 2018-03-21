package cn.wizzer.modules.controllers.platform.losys;

import cn.apiclub.captcha.filter.image.TextureFilter;
import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.losys.Lo_area;
import cn.wizzer.modules.models.losys.Lo_group_pricesetting;
import cn.wizzer.modules.models.losys.Lo_logistics;
import cn.wizzer.modules.models.sys.Sys_menu;
import cn.wizzer.modules.models.sys.Sys_unit;
import cn.wizzer.modules.services.losys.LosysAreaService;
import cn.wizzer.modules.services.losys.LosysGroupPricesettingService;
import cn.wizzer.modules.services.losys.LosysLogisticsService;
import cn.wizzer.modules.services.sys.SysMenuService;
import cn.wizzer.modules.services.sys.SysUnitService;
import cn.wizzer.modules.services.sys.SysUserService;
import oracle.net.aso.a;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


/**
 * 分组价格管理
 * @author Lenovo
 *
 */

@IocBean
@At("/platform/losys/GroupPricesetting")
@Filters({@By(type = PrivateFilter.class)})
public class LosysGroupPricesettingController {
    private static final Log log = Logs.get();
    @Inject
    private LosysGroupPricesettingService groupPricesettingService;

    /**
     * 访问价格分组管理模块首页
     */
    @At("")
    @Ok("beetl:/platform/losys/GroupPricesetting/index.html")
    @RequiresAuthentication
    public void index(HttpServletRequest req) {
    	
    	List<Lo_group_pricesetting> group_pricesettings = groupPricesettingService.query(Cnd.where("1", "=", "1").asc("opAt"));
    
    	req.setAttribute("list", group_pricesettings);
    }
    
    /**
     * 访问价格分组管理模块添加价格分组页面
     */
    @At
    @Ok("beetl:/platform/losys/GroupPricesetting/add.html")
    @RequiresAuthentication
    public void add() {
    	
    }

    /**
     * 访问价格分组管理模块价格分组页面
     */
    @At("/edit/?")
    @Ok("beetl:/platform/losys/GroupPricesetting/edit.html")
    @RequiresPermissions("groupPricesetting.manage.edit")
    public Object edit(String id,HttpServletRequest req) {
    	Record group_pricesetting = groupPricesettingService.dao().fetch("lo_group_pricesetting", Cnd.where("id", "=", id));
    	req.setAttribute("group_pricesetting", group_pricesetting);
    	return group_pricesetting;
    }
    
    /**
     * 添加价格分组
     */
	@At
    @Ok("json")
    @RequiresPermissions("groupPricesetting.manage.add")
    @SLog(tag = "添加价格分组", msg = "价格分组名id:${args[0].id}")
	@AdaptBy(type = WhaleAdaptor.class)
    public Object addDo(@Param("..") Lo_group_pricesetting group_pricesetting, HttpServletRequest req) {
        try {
        	group_pricesetting.setOpAt((int)System.currentTimeMillis());
            groupPricesettingService.insert(group_pricesetting);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }
    
    
    
    /**
     * 删除  价格分组
     */
    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("groupPricesetting.manage.delete")
    public Object delete(String oneId, @Param("ids") String[] ids, HttpServletRequest req) {
    	try {
        	if (ids != null && ids.length > 0) {
            	for (String string : ids) {
            		/*groupPricesettingService.delete(string);*/
            		groupPricesettingService.dao().clear("lo_group_pricesetting", Cnd.where("id", "=", string));
            	}
                req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
            } else {
            	/*groupPricesettingService.delete(oneId);*/
            	groupPricesettingService.dao().clear("lo_group_pricesetting", Cnd.where("id", "=", oneId));
                req.setAttribute("id", oneId);
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }
    
	/**
	 * 修改价格分组
	 */
    @At
    @Ok("json")
    @RequiresPermissions("groupPricesetting.manage.edit")
    @SLog(tag = "修改价格分组", msg = "价格分组:${args[0].id}")
    public Object editDo(@Param("..") Lo_group_pricesetting group_pricesetting, HttpServletRequest req) {	
        try {
        	groupPricesettingService.update(group_pricesetting);
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
        return groupPricesettingService.data(length, start, draw, order, columns, cnd, null);
        
    }
}
