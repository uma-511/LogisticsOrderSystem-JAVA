package cn.wizzer.modules.controllers.platform.losys;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.losys.Lo_factory_dataImport;
import cn.wizzer.modules.models.sys.Sys_role;
import cn.wizzer.modules.models.sys.Sys_user;
import cn.wizzer.modules.models.sys.Sys_user_role;
import cn.wizzer.modules.services.losys.LoFactoryDataimportService;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.http.Request;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@IocBean
@At("/platform/losys/factory/dataImport")
@Filters({@By(type = PrivateFilter.class)})
public class LoFactoryDataimportController {
	private static final Log log = Logs.get();
	@Inject
	private LoFactoryDataimportService loFactoryDataimportService;
	
	@Inject
	private Dao dao;
	
	@At("")
	@Ok("beetl:/platform/losys/factory/dataImport/index.html")
	@RequiresAuthentication
	public void index(HttpServletRequest req) {
		// 登录用户
		Subject subject = SecurityUtils.getSubject();
		Sys_user user = (Sys_user) subject.getPrincipal();
			
		// 登录对应的角色关系
		Sys_user_role user_role = dao.fetch(Sys_user_role.class, Cnd.where("userId", "=", user.getId()));
		
		// 登录的角色
		Sys_role role = dao.fetch(Sys_role.class,  Cnd.where("id", "=", user_role.getRoleId()));
		
		if (role.getCode().equals("taobao")) {
			req.setAttribute("isDisplay", "0");
		} else {
			req.setAttribute("isDisplay", "1");
		}
	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("factory") String factory, 
					   @Param("addressee") String addressee, 
					   @Param("phone") String phone, 
					   @Param("objectContent") String objectContent, 
					   @Param("tbName") String tbName, 
					   @Param("length") int length, 
					   @Param("start") int start, 
					   @Param("draw") int draw, 
					   @Param("::order") List<DataTableOrder> order, 
					   @Param("::columns") List<DataTableColumn> columns) {
		
		// 登录用户
		Subject subject = SecurityUtils.getSubject();
		Sys_user user = (Sys_user) subject.getPrincipal();
		if (user == null) return Result.error("用户错误");
			
		// 登录对应的角色关系
		Sys_user_role user_role = dao.fetch(Sys_user_role.class, Cnd.where("userId", "=", user.getId()));
		if (user_role == null) return Result.error("用户角色错误");
		
		// 登录的角色
		Sys_role role = dao.fetch(Sys_role.class,  Cnd.where("id", "=", user_role.getRoleId()));
		if (role == null) return Result.error("角色错误");
		
		// 组装条件
		Cnd cnd = Cnd.NEW();
		if (role.getCode().equals("taobao")) {
			cnd.and("tbName", "=", user.getShopname());	
		}
		
		if (!Strings.isBlank(factory)) {
			cnd.and("factory", "like", "%" + factory + "%");
		}
		
		if (!Strings.isBlank(addressee)) {
			cnd.and("addressee", "like", "%" + addressee + "%");
		}
		
		if (!Strings.isBlank(phone)) {
			cnd.and("phone", "like", "%" + phone + "%");
		}
		
		if (!Strings.isBlank(objectContent)) {
			cnd.and("objectContent", "like", "%" + objectContent + "%");
		}
		
    	return loFactoryDataimportService.data(length, start, draw, order, columns, cnd, null);
    }

    @At
    @Ok("beetl:/platform/losys/factory/dataImport/add.html")
    @RequiresAuthentication
    public void add() {

    }

    @At
    @Ok("json")
    @SLog(tag = "新建数据导入", msg = "")
    public Object addDo(@Param("..") Lo_factory_dataImport loFactoryDataimport, HttpServletRequest req) {
		try {
			loFactoryDataimportService.insert(loFactoryDataimport);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/losys/factory/dataImport/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {
		return loFactoryDataimportService.fetch(id);
    }

    @At
    @Ok("json")
    @SLog(tag = "修改数据导入", msg = "ID:${args[0].id}")
    public Object editDo(@Param("..") Lo_factory_dataImport loFactoryDataimport, HttpServletRequest req) {
		try {

			loFactoryDataimport.setOpAt((int) (System.currentTimeMillis() / 1000));
			loFactoryDataimportService.updateIgnoreNull(loFactoryDataimport);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At({"/delete","/delete/?"})
    @Ok("json")
    @SLog(tag = "删除数据导入", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				loFactoryDataimportService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				loFactoryDataimportService.delete(id);
    			req.setAttribute("id", id);
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At("/detail/?")
    @Ok("beetl:/platform/losys/factory/dataImport/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			return loFactoryDataimportService.fetch(id);

		}
		return null;
    }
    
    @At("/seeDataImport")
	@Ok("beetl:/platform/losys/factory/dataImport/seeDataImport.html")
	@RequiresAuthentication
	public void seeDataImport() {

	}

}
