package cn.wizzer.modules.controllers.platform.losys;

import cn.apiclub.captcha.filter.image.TextureFilter;
import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.losys.Lo_area;
import cn.wizzer.modules.models.losys.Lo_group_pricesetting;
import cn.wizzer.modules.models.losys.Lo_insurance;
import cn.wizzer.modules.models.losys.Lo_insurance_pricesetting;
import cn.wizzer.modules.models.losys.Lo_logistics;
import cn.wizzer.modules.models.sys.Sys_menu;
import cn.wizzer.modules.models.sys.Sys_unit;
import cn.wizzer.modules.services.losys.LosysAreaService;
import cn.wizzer.modules.services.losys.LosysGroupPricesettingService;
import cn.wizzer.modules.services.losys.LosysInsurancePricesettingService;
import cn.wizzer.modules.services.losys.LosysInsuranceService;
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
 * 保价管理
 * @author 
 *
 */

@IocBean
@At("/platform/losys/insurance")
@Filters({@By(type = PrivateFilter.class)})
public class LosysInsurancePricesettingController {
    private static final Log log = Logs.get();
    @Inject
    LosysLogisticsService logisticsService;
    @Inject
    LosysInsuranceService insuranceService;
    @Inject
    private LosysInsurancePricesettingService insurancePricesettingService;

    /**
     * 访问保价管理模块首页
     */
    @At("")
    @Ok("beetl:/platform/losys/insurance/index.html")
    @RequiresAuthentication
    public Object index(HttpServletRequest req) {
    	return logisticsService.query();
    }
    
    /**
     * 访问保价管理模块添加保价页面
     */
    @At
    @Ok("beetl:/platform/losys/insurance/add.html")
    @RequiresAuthentication
    public Object add() {
    	return logisticsService.query();
    }

    /**
     * 访问保价管理模块修改保价页面
     */
    @At("/edit/?/?")
    @Ok("beetl:/platform/losys/insurance/edit.html")
    @RequiresPermissions("support.price.edit")
    public Object edit(String insuranceid,String id,HttpServletRequest req) {
    	List<Lo_insurance> list=insuranceService.query(Cnd.where("id", "=", insuranceid));
    	for(Lo_insurance data:list){
    		List<Lo_logistics> logistics=logisticsService.query(Cnd.where("id", "=", data.getLogisticsId()));
    		for(Lo_logistics lo:logistics){
    			req.setAttribute("logistics", lo);
    		}
    		req.setAttribute("logisticsList", logisticsService.query());
    	}
    	return insurancePricesettingService.fetch(id);
    }
    
    /**
     * 添加保价
     */
	@At
    @Ok("json")
    @RequiresPermissions("support.price.add")
    @SLog(tag = "添加保价", msg = "价格保价id:${args[0].id}")
	@AdaptBy(type = WhaleAdaptor.class)
    public Object addDo(@Param("..") Lo_insurance_pricesetting insurance_pricesetting, HttpServletRequest req) {
        try {
        	Lo_insurance insurance =new Lo_insurance();
        	insurance.setLogisticsId(insurance_pricesetting.getInsuranceId());
        	insuranceService.insert(insurance);
        	List<Lo_insurance> list=insuranceService.query(Cnd.where("logisticsId", "=", insurance_pricesetting.getInsuranceId()));
        	for(Lo_insurance data:list){
        		insurance_pricesetting.setOpAt((int)System.currentTimeMillis());
        		insurance_pricesetting.setInsuranceId(data.getId());
        		insurancePricesettingService.insert(insurance_pricesetting);
        	}
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
    @RequiresPermissions("support.price.delete")
    public Object delete(String oneId, @Param("ids") String[] ids, HttpServletRequest req) {
    	try {
        	if (ids != null && ids.length > 0) {
            	for (String string : ids) {
            		/*groupPricesettingService.delete(string);*/
            		insurancePricesettingService.dao().clear("lo_insurance_pricesetting", Cnd.where("id", "=", string));
            	}
                req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
            } else {
            	/*groupPricesettingService.delete(oneId);*/
            	insurancePricesettingService.dao().clear("lo_insurance_pricesetting", Cnd.where("id", "=", oneId));
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
    @RequiresPermissions("support.price.edit")
    @SLog(tag = "修改保价", msg = "保价:${args[0].id}")
    public Object editDo(@Param("..") Lo_insurance_pricesetting insurance_pricesetting, HttpServletRequest req) {	
        try {
        	List<Lo_insurance> list=insuranceService.query(Cnd.where("logisticsId", "=", insurance_pricesetting.getInsuranceId()));
        	for(Lo_insurance data:list){
        		insurance_pricesetting.setOpAt((int)System.currentTimeMillis());
        		insurance_pricesetting.setInsuranceId(data.getId());
        		insurancePricesettingService.update(insurance_pricesetting);
        	}
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
    public Object data(@Param("logisticsid") String logisticsid,@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        List<Lo_insurance> list=insuranceService.query();
        if(list.isEmpty()){
        	Lo_insurance insurance =new Lo_insurance();
        	insurance.setLogisticsId(logisticsid);
        	insuranceService.insert(insurance);
        }
        Sql sql = insurancePricesettingService.getMessageList(logisticsid);
		return insurancePricesettingService.data(length, start, draw, sql, sql);
        
    }
    
    
}
