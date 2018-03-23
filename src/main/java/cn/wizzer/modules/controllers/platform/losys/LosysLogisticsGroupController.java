package cn.wizzer.modules.controllers.platform.losys;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.losys.Lo_area;
import cn.wizzer.modules.models.losys.Lo_group_pricesetting;
import cn.wizzer.modules.models.losys.Lo_logistics;
import cn.wizzer.modules.models.losys.Lo_logistics_group;
import cn.wizzer.modules.models.losys.Lo_logistics_pricesetting;
import cn.wizzer.modules.models.sys.Sys_unit;
import cn.wizzer.modules.services.losys.LosysGroupPricesettingService;
import cn.wizzer.modules.services.losys.LosysLogisticsGroupService;
import cn.wizzer.modules.services.losys.LosysLogisticsPricesettingService;
import cn.wizzer.modules.services.losys.LosysLogisticsService;
import cn.wizzer.modules.services.sys.SysMenuService;
import cn.wizzer.modules.services.sys.SysUnitService;
import cn.wizzer.modules.services.sys.SysUserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.WhaleAdaptor;
import org.nutz.mvc.annotation.*;
import com.mysql.fabric.xmlrpc.base.Data;




@IocBean
@At("/platform/losys/logisticsGroup")
@Filters({@By(type = PrivateFilter.class)})
public class LosysLogisticsGroupController {
    private static final Log log = Logs.get();
    @Inject
    private LosysLogisticsGroupService logisticsGroupService;
    @Inject
    private LosysLogisticsService logisticsService;
    @Inject
    private LosysGroupPricesettingService groupPriceService;
    @Inject
    private LosysLogisticsPricesettingService logisticsPriceService;

    /**
     * 访问物流公司模块首页
     */
    @At("")
    @Ok("beetl:/platform/losys/logisticsGroup/index.html")
    @RequiresAuthentication
    public void index() {
    
    }
    
    /**
     * 访问物流公司价格分组模块添加物流公司页面
     */
    @At
    @Ok("beetl:/platform/losys/logisticsGroup/add.html")
    @RequiresAuthentication
    public void add(HttpServletRequest req) {
    	
    /*	Cnd cnd = Cnd.NEW();
        cnd.and("delFlag", "=", '0');
        
        List<Record> logistics = logisticsService.dao().query("lo_logistics", Cnd.where("delFlag", "=", '0'));
    	req.setAttribute("logistics", Json.toJson(logistics));*/
    	
    	List<Lo_logistics> list = logisticsService.query(Cnd.where("delFlag", "=", "0").asc("opAt"));
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Lo_logistics logistics : list) {
            Map<String, Object> obj = new HashMap<>();
            obj.put("id", logistics.getId());
            obj.put("text", logistics.getName());
            tree.add(obj);
        }
        req.setAttribute("logistics", Json.toJson(tree));
    	 	
    }

    /**
     * 修改分组页面
     */
    @At("/edit/?")
    @Ok("beetl:/platform/losys/logisticsGroup/editGroup.html")
    @RequiresPermissions("logisticsGroup.manage.edit")
    public void edit(String id,HttpServletRequest req) {
    	List<Lo_logistics> list = logisticsService.query(Cnd.where("delFlag", "=", "0").asc("opAt"));
    	Lo_logistics_group group = logisticsGroupService.fetch(id);
    	
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Lo_logistics logistics : list) {
            Map<String, Object> obj = new HashMap<>();
            obj.put("id", logistics.getId());
            obj.put("text", logistics.getName());
            if (logistics.getId().equals(group.getLogisticsId())) {
            	obj.put("state", NutMap.NEW().addv("selected", true));
			}
            tree.add(obj);
        }
        req.setAttribute("logistics", Json.toJson(tree));
        req.setAttribute("group", group);
    }
    
    /**
     * 访问物流公司价格分组模块修改价格页面
     */
    @At("/editPrice/?/?/?")
    @Ok("beetl:/platform/losys/logisticsGroup/editPrice.html")
    public void editPrice(String logistics,String groupName, String groupId, HttpServletRequest req) {
    	
    	List<Lo_logistics_pricesetting> list2 = logisticsPriceService.query(Cnd.where("logisticsGroupId", "=", groupId).asc("opAt"));
    	List<Lo_group_pricesetting> list = groupPriceService.query(Cnd.where("1", "=", "1").asc("opAt"));
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Lo_group_pricesetting group : list) {
            Map<String, Object> obj = new HashMap<>();
            obj.put("id", group.getId());
            obj.put("text", "运算符："+ group.getOperator() + "  重量：" + group.getWeight() + "  价钱：" + group.getPrice() + "  低消：" + group.getMin());
            if (list2.size()>0) {
				for (Lo_logistics_pricesetting map : list2) {
					if (map.getPricesettingId().equals(String.valueOf(group.getId()))) {
						obj.put("state", NutMap.NEW().addv("selected", true));
					}
				}
			}
            tree.add(obj);
        }
        req.setAttribute("price", Json.toJson(tree));
        req.setAttribute("logistics", logistics);
        req.setAttribute("groupName", groupName);
        req.setAttribute("groupId", groupId);
    }
    
    /**
     * 访问物流公司价格分组模块设置价格页面
     */
    @At("/setPrice/?/?")
    @Ok("beetl:/platform/losys/logisticsGroup/setPrice.html")
    public void setPrice(String logistics,String groupName, HttpServletRequest req) {
    	
    	List<Lo_group_pricesetting> list = groupPriceService.query(Cnd.where("1", "=", "1").asc("opAt"));
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Lo_group_pricesetting group : list) {
            Map<String, Object> obj = new HashMap<>();
            obj.put("id", group.getId());
            obj.put("text", "运算符："+ group.getOperator() + "  重量：" + group.getWeight() + "  价钱：" + group.getPrice() + "  低消：" + group.getMin());
            tree.add(obj);
        }
        req.setAttribute("price", Json.toJson(tree));
        req.setAttribute("logistics", logistics);
        req.setAttribute("groupName", groupName);
    }
    
    /**
     * 公司分组设置价格
     */
	@At
    @Ok("json")
    public Object setPriceDo(@Param("ids") String ids,@Param("groupName") String groupName, @Param("logistics") String logistics, HttpServletRequest req) {
        try {
        	/*Lo_logistics records = logisticsService.dao().fetch(Lo_logistics.class, Cnd.where("name", "=", logistics));*/
        	Lo_logistics_group logisticsGroup = new Lo_logistics_group();
        	String groupid = getUUID();
        	logisticsGroup.setId(groupid);
        	logisticsGroup.setLogisticsId(logistics);
        	logisticsGroup.setName(groupName);
        	logisticsGroupService.insert(logisticsGroup);
        	String[] id = ids.split(",");
        	for (String string : id) {
        		Lo_logistics_pricesetting pricesetting = new Lo_logistics_pricesetting();
        		pricesetting.setLogisticsGroupId(groupid);
        		pricesetting.setPricesettingId(string);
        		logisticsPriceService.insert(pricesetting);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }
    
	/**
     * 公司分组设置价格
     */
	@At
    @Ok("json")
    public Object editPriceDo(@Param("ids") String ids,@Param("groupName") String groupName, @Param("logistics") String logistics, @Param("groupId") String groupId, HttpServletRequest req) {
        try {
        	/*Lo_logistics records = logisticsService.dao().fetch(Lo_logistics.class, Cnd.where("name", "=", logistics));*/
        	Lo_logistics_group logisticsGroup = logisticsGroupService.fetch(groupId);
        	logisticsGroup.setLogisticsId(logistics);
        	logisticsGroup.setName(groupName);
        	logisticsGroupService.update(logisticsGroup);
        	String[] id = ids.split(",");
        	
        	logisticsPriceService.clear(Cnd.where("logisticsGroupId", "=", groupId));
        	for (String string : id) {
        		Lo_logistics_pricesetting pricesetting = new Lo_logistics_pricesetting();
        		pricesetting.setLogisticsGroupId(groupId);
        		pricesetting.setPricesettingId(string);
        		logisticsPriceService.insert(pricesetting);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }
	
    /**
     * 添加物流公司价格分组
     */
	@At
    @Ok("json")
    @RequiresPermissions("logisticsGroup.manage.add")
    public Object addDo(@Param("ids") String ids,@Param("name") String name, HttpServletRequest req) {
        try {
        	String[] id = ids.split(",");
        	String uuid = getUUID();
        	for (String string : id) {
				Lo_logistics_group group = new Lo_logistics_group();
				group.setName(name);
				group.setLogisticsId(string);
				logisticsGroupService.insert(group);
			}
        	/*group.setOpAt((int)System.currentTimeMillis());
        	group.setDelFlag(false);
        	logisticsGroupService.insert(group);*/
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }
    
    
    
    /**
     * 删除物流公司价格分组
     */
    @At({"/delete/?", "/delete"})
    @Ok("json")
    @RequiresPermissions("logisticsGroup.manage.delete")
    @SLog(tag = "删除物流公司价格分组", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String oneId, @Param("ids") String[] ids, HttpServletRequest req) {
    	try {
        	if (ids != null && ids.length > 0) {
            	for (String string : ids) {
            		logisticsGroupService.dao().clear("lo_logistics_group", Cnd.where("id", "=", string));
            		logisticsPriceService.dao().clear("lo_logistics_pricesetting",Cnd.where("logisticsGroupId", "=", string));
            		/*logisticsGroupService.dao().update(Lo_logistics.class, Chain.make("delFlag", true), Cnd.where("id","=",string));*/
//            		logisticsGroupService.dao().d
            	}
                req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
            } else {
//            	logisticsGroupService.delete(oneId);
            	logisticsGroupService.dao().clear("lo_logistics_group", Cnd.where("id", "=", oneId));
            	logisticsPriceService.dao().clear("lo_logistics_pricesetting",Cnd.where("logisticsGroupId", "=", oneId));
            	/*logisticsGroupService.dao().update(Lo_logistics.class, Chain.make("delFlag", true), Cnd.where("id","=",oneId));*/
                req.setAttribute("id", oneId);
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }
    
	/**
	 * 修改物流公司价格分组
	 */
    @At
    @Ok("json")
    @RequiresPermissions("logisticsGroup.manage.edit")
    public Object editDo(@Param("name") String name, @Param("oldName") String oldName, HttpServletRequest req) {	
        try {
        	//logisticsGroupService.update(group);
        	logisticsGroupService.dao().update(Lo_logistics_group.class, Chain.make("name", name), Cnd.where("name", "=" ,oldName ));
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
//        cnd.and("delFlag", "=", '0');
//        return logisticsGroupService.data(length, start, draw, order, columns, cnd, null);
        Sql countSql = Sqls.create("SELECT g.id, g.logisticsId, g.`name`, l.`name` as logisticsName from lo_logistics_group g,lo_logistics l WHERE  l.id = g.logisticsId");
        return logisticsGroupService.data(length, start, draw, countSql, countSql);
        
    }
    
    
    public  String getUUID(){
        UUID uuid=UUID.randomUUID();
        String str = uuid.toString(); 
        String uuidStr=str.replace("-", "");
        return uuidStr;
      }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
