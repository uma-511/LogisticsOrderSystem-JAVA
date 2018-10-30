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
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.Iterator;
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
					   @Param("logisticsNo") String logisticsNo, 
					   @Param("date1") String date1, 
					   @Param("date2") String date2, 
					   @Param("inputDeliverGoods") String inputDeliverGoods, 
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
		
		// 淘宝身份，只查询自己店铺的数据信息
		if (role.getCode().equals("taobao")) {
			cnd.and("tbName", "=", user.getShopname());
		}
		
		// 条件 淘宝名
		if (!Strings.isBlank(tbName)) {
			cnd.and("tbName", "like", "%" + tbName + "%");
		}
		
		// 条件 淘宝名
		if (!Strings.isBlank(factory)) {
			cnd.and("factory", "like", "%" + factory + "%");
		}
		
		// 条件 发货弹出层自动输入订单号
		/**
		 * 注明：若发货按钮弹出层输入不为空的情况，其余条件作废，以弹出层条件作为唯一条件
		 * identification 用于标明是否弹框发货成功，状态为 1 时，发货成功
		 */
		int identification = 0;
		if (!Strings.isBlank(inputDeliverGoods)) {
//			Lo_factory_dataImport dataImport = loFactoryDataimportService.fetch();
//			dataImport.u
			int upDataCount = loFactoryDataimportService.update(Chain.make("status", "已发货"), Cnd.where("logisticsNo", "=", inputDeliverGoods));
			if (upDataCount >= 1) {
				identification = 1;
			}
			cnd.and("logisticsNo", "like", "%" + inputDeliverGoods + "%");
		} else {
			
			// 条件 订单号
			if (!Strings.isBlank(logisticsNo)) {
				cnd.and("logisticsNo", "like", "%" + logisticsNo + "%");
			}
			
			// 条件 收货地址
			if (!Strings.isBlank(addressee)) {
				cnd.and("addressee", "like", "%" + addressee + "%");
			}
			
			// 条件 收件人手机号
			if (!Strings.isBlank(phone)) {
				cnd.and("phone", "like", "%" + phone + "%");
			}
			
			// 条件 物品内容
			if (!Strings.isBlank(objectContent)) {
				cnd.and("objectContent", "like", "%" + objectContent + "%");
			}
			
			// 条件 日期
			if (!Strings.isBlank(date1) && !Strings.isBlank(date2)) {
				cnd.and("STR_TO_DATE(`date`,\"%Y-%m-%d\") BETWEEN '" + date1 + "'", "and", date2);
			}
		}
		
		NutMap map = loFactoryDataimportService.data(length, start, draw, order, columns, cnd, null);
		map.addv("identification", identification);
    	return map;
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
