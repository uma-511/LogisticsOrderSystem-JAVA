package cn.wizzer.modules.controllers.platform.losys;


import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.modules.models.losys.Lo_area;
import cn.wizzer.modules.models.losys.Lo_area_price;
import cn.wizzer.modules.models.losys.Lo_group_pricesetting;
import cn.wizzer.modules.services.losys.LosysAreaPriceService;
import cn.wizzer.modules.services.losys.LosysAreaService;
import cn.wizzer.modules.services.losys.LosysGroupPricesettingService;
import cn.wizzer.modules.services.losys.LosysLogisticsPricesettingService;
import cn.wizzer.modules.services.losys.LosysLogisticsService;

import java.util.ArrayList;
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

import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import org.nutz.mvc.annotation.*;





@IocBean
@At("/platform/losys/areaPrice")
@Filters({@By(type = PrivateFilter.class)})
public class LosysAreaPriceController {
    private static final Log log = Logs.get();
    @Inject
    private LosysAreaPriceService areaPriceService;
    @Inject
    private LosysLogisticsService logisticsService;
    @Inject
    private LosysAreaService areaService;
    @Inject
    private LosysGroupPricesettingService groupPriceService;
    @Inject
    private LosysLogisticsPricesettingService logisticsPricesettingService;

    /**
     * 首页
     */
    @At({"","/data/?"})
    @Ok("beetl:/platform/losys/areaPrice/index.html")
    public Object index(String logisticsId ,HttpServletRequest req) {
    	List<Record> logistics = logisticsService.dao().query("lo_logistics", Cnd.where("delFlag", "=", "0"));
//    	Sql sql = Sqls.create("SELECT * FROM lo_area WHERE pid = ''");
//    	Sql sql = Sqls.create("SELECT * FROM lo_area");
//    	List<Record> areas = areaService.list(sql);
//    	for (Record record : areas) {
//    		Sql sql2 = Sqls.create("SELECT g.`name` FROM lo_area_price p INNER JOIN lo_logistics_group  g on(p.logisticsId=g.logisticsId) WHERE p.areaId =@areaId and g.logisticsId =@logisticsId AND p.groupId = g.id");
//    		if (logisticsId == null) {
//    			sql2.params().set("logisticsId", logistics.get(0).get("id"));
//    			logisticsId="";
//			}else {
//				sql2.params().set("logisticsId", logisticsId);
//				
//			}
//    		sql2.params().set("areaId", record.getString("id"));
//        	List<Record> prices = areaService.list(sql2);
//        	String prString = "";
//        	String priceName = "";
//        	for (Record record2 : prices) {
//        			prString += record2.getString("name") + ",";
//			}
//        	if(!prString.equals("")){
//        		priceName=prString.substring(0,prString.length()-1);
//        	}
//        	record.set("price", priceName);
//		}
    	if (logisticsId == null) {
			logisticsId=logistics.get(0).getString("id");
    	}
    	List<Record> areas = areaService.getAllAreaPrice(logisticsId);
    	req.setAttribute("list", areas);
    	req.setAttribute("logistics", logistics);
    	req.setAttribute("logisticsId", logisticsId);
    	return req;
    }
    
    
    @At("/child/?/?")
    @Ok("beetl:/platform/losys/areaPrice/child.html")
    @RequiresAuthentication
    public Object child(String id ,String logisticsId, HttpServletRequest req) {
    	List<Record> logistics = logisticsService.dao().query("lo_logistics", Cnd.where("delFlag", "=", "0"));
    	Sql sql = Sqls.create("SELECT * FROM lo_area WHERE pid = @pid" );
    	sql.setParam("pid", id);
    	List<Record> areas = areaService.list(sql);
    	for (Record record : areas) {
    		Sql sql2 = Sqls.create("SELECT g.`name` FROM lo_area_price p INNER JOIN lo_logistics_group  g on(p.logisticsId=g.logisticsId) WHERE p.areaId =@areaId AND p.groupId = g.id where g.logisticsId =@logisticsId ");
    		if (logisticsId == null) {
    			sql2.params().set("logisticsId", logistics.get(0).get("id"));
			}else {
				sql2.params().set("logisticsId", logisticsId);
			}
    		sql2.params().set("areaId", record.getString("id"));
        	List<Record> prices = areaService.list(sql2);
        	String prString = "";
        	String priceName = "";
        	for (Record record2 : prices) {
        			prString += record2.getString("name") + ",";
			}
        	if(!prString.equals("")){
        		priceName=prString.substring(0,prString.length()-1);
        	}
        	record.set("price", priceName);
		}
    	req.setAttribute("list", areas);
    	req.setAttribute("logistics", logistics);
    	return req;
    }
    
    /**
     * 访问设置价格页面
     */
    @At("/setPrice/?/?")
    @Ok("beetl:/platform/losys/areaPrice/setPrice.html")
    public void editPrice(String ids,String logisticsId, HttpServletRequest req) {
    	String[] id = ids.split(",");
//    	SELECT p.id,g.`name` FROM lo_logistics_pricesetting p INNER JOIN lo_logistics_group g on(p.logisticsGroupId=g.id) WHERE g.logisticsId = 'bfcbc6791d8f41d08db561c68a91c183'
    	List<Lo_area_price> list2 = null;
    	if (id.length == 1) {
    		list2 = areaPriceService.query(Cnd.where("logisticsId", "=", logisticsId).and("areaId", "=", ids).asc("opAt"));
		}else {
			list2 = areaPriceService.query(Cnd.where("logisticsId", "=", logisticsId).asc("opAt"));
		}
    	Sql sql = Sqls.create("SELECT p.id,g.`name`,p.logisticsGroupId FROM lo_logistics_pricesetting p INNER JOIN lo_logistics_group g on(p.logisticsGroupId=g.id) WHERE g.logisticsId = @logisticsId GROUP BY g.`name`");
    	sql.params().set("logisticsId", logisticsId);
    	List<Record> list = logisticsPricesettingService.list(sql);
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Record group : list) {
            Map<String, Object> obj = new HashMap<>();
            obj.put("id", group.get("logisticsGroupId"));
            obj.put("text", group.get("name"));
            
            if (id.length == 1) {
            	if (list2.size()>0) {
    				for (Lo_area_price map : list2) {
    					if (map.getGroupId().equals(String.valueOf(group.get("id")))) {
    						obj.put("state", NutMap.NEW().addv("selected", true));
    					}
    				}
    			}
			}
            tree.add(obj);
        }
        req.setAttribute("price", Json.toJson(tree));
        req.setAttribute("logisticsId", logisticsId);
        req.setAttribute("ids", ids);
    }
    
    /**
     * 设置价格
     */
	@At
    @Ok("json")
    public Object editPriceDo(@Param("ids") String ids,@Param("logisticsId") String logisticsId, @Param("areaIds") String areaIds, HttpServletRequest req) {
        try {
        	String[] priceId = ids.split(",");
        	String[] areaId = areaIds.split(",");
        	Lo_area_price areaPrice = new Lo_area_price();
        	for (String string : areaId) {
				areaPriceService.clear(Cnd.where("logisticsId", "=", logisticsId).and("areaId", "=", string));
				for (String p : priceId) {
	        		areaPrice.setAreaId(string);
	        		areaPrice.setGroupId(p);
	        		areaPrice.setLogisticsId(logisticsId);
					areaPriceService.insert(areaPrice);
				}
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }
}
