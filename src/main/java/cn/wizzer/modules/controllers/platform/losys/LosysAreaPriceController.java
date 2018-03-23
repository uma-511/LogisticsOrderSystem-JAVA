package cn.wizzer.modules.controllers.platform.losys;


import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.losys.Lo_area;
import cn.wizzer.modules.models.losys.Lo_area_price;
import cn.wizzer.modules.models.losys.Lo_logistics;
import cn.wizzer.modules.models.sys.Sys_menu;
import cn.wizzer.modules.models.sys.Sys_unit;
import cn.wizzer.modules.services.losys.LosysAreaPriceService;
import cn.wizzer.modules.services.losys.LosysAreaService;
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
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.WhaleAdaptor;
import org.nutz.mvc.annotation.*;
import com.mysql.fabric.xmlrpc.base.Data;




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

    /**
     * 首页
     */
    @At("")
    @Ok("beetl:/platform/losys/areaPrice/index.html")
    @RequiresAuthentication
    public Object index(HttpServletRequest req) {
    	List<Lo_area> list = areaService.query(Cnd.where("1", "=", "1"));
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Lo_area area : list) {
            Map<String, Object> obj = new HashMap<>();
            obj.put("id", area.getId());
            obj.put("text", area.getName());
            obj.put("parent", "".equals(Strings.sNull(area.getPid())) ? "#" : area.getPid());
            tree.add(obj);
        }
        req.setAttribute("area", Json.toJson(tree));
        req.setAttribute("logisticsId", "");
    	return logisticsService.dao().query("lo_logistics", Cnd.where("delFlag", "=", "0"));
    }
    
    /**
     * 查询数据
     */
    
    @At("/data/?")
    @Ok("beetl:/platform/losys/areaPrice/index.html")
    @RequiresAuthentication
    public Object data(String logisticsId, HttpServletRequest req) {
    	List<Lo_area> list = areaService.query(Cnd.where("1", "=", "1"));
    	List<Lo_area_price> area_prices = areaPriceService.query(Cnd.where("logisticsId", "=", logisticsId));
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Lo_area area : list) {
            Map<String, Object> obj = new HashMap<>();
            obj.put("id", area.getId());
            obj.put("text", area.getName());
            obj.put("parent", "".equals(Strings.sNull(area.getPid())) ? "#" : area.getPid());
            for (Lo_area_price  area_price : area_prices) {
				if (area_price.getAreaId().equals(area.getId())) {
					obj.put("state", NutMap.NEW().addv("selected", true));
					break;
				}else {
					obj.put("state", NutMap.NEW().addv("selected", false));
				}
			}
            tree.add(obj);
        }
        req.setAttribute("area", Json.toJson(tree));
        req.setAttribute("logisticsId", logisticsId);
        return logisticsService.dao().query("lo_logistics", Cnd.where("delFlag", "=", "0"));
    }
    
}
