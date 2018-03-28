package cn.wizzer.modules.controllers.platform.losys;

import cn.apiclub.captcha.filter.image.TextureFilter;
import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.common.util.Calculator;
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
 * 运费查询
 * @author 
 *
 */

@IocBean
@At("/platform/losys/freight")
@Filters({@By(type = PrivateFilter.class)})
public class LosysFreightController {
    private static final Log log = Logs.get();
    @Inject
    LosysLogisticsService logisticsService;
    @Inject
    LosysInsuranceService insuranceService;
    @Inject
    private LosysInsurancePricesettingService insurancePricesettingService;

//    /**
//     * 访问运费查询模块首页
//     */
//    @At("")
//    @Ok("beetl:/platform/losys/freight/index.html")
//    @RequiresAuthentication
//    public void index(HttpServletRequest req) {
//    	
//    }
    /**
     * 保价计算
     * @param insurance
     * @param logistics
     * @return
     */
    @At("")
    @Ok("beetl:/platform/losys/freight/add.html")
    @RequiresAuthentication
	public Object insurance(String insurance,String logistics) {//100
    	insurance="90";
		int num=Integer.parseInt(insurance);
		String expression="";
		List<Lo_insurance_pricesetting> list=insurancePricesettingService.query();
		for(Lo_insurance_pricesetting price:list){
			int cost=Integer.parseInt(price.getInsurance());
			double value=Double.parseDouble(price.getValue());
			if(price.getOperator().equals(">") && num>cost){
				expression =String.valueOf(num*value);
			}else if(price.getOperator().equals("<") && num<cost){
				expression =String.valueOf(num*value);
			}else if(price.getOperator().equals(">=") && num>=cost){
				expression =String.valueOf(num*value);
			}else if(price.getOperator().equals("<=") && num<=cost){
				expression =String.valueOf(num*value);
			}
		}
		double result = Calculator.conversion(expression);
		System.out.println(expression + " = " + result);
		return result;

	}
    
}
