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
import cn.wizzer.modules.models.losys.Lo_overlength_pricesetting;
import cn.wizzer.modules.models.sys.Sys_menu;
import cn.wizzer.modules.models.sys.Sys_unit;
import cn.wizzer.modules.services.losys.LosysAreaPriceService;
import cn.wizzer.modules.services.losys.LosysAreaService;
import cn.wizzer.modules.services.losys.LosysGroupPricesettingService;
import cn.wizzer.modules.services.losys.LosysInsurancePricesettingService;
import cn.wizzer.modules.services.losys.LosysInsuranceService;
import cn.wizzer.modules.services.losys.LosysLogisticsService;
import cn.wizzer.modules.services.losys.LosysOverLengthPricesettingService;
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
    @Inject
    private LosysOverLengthPricesettingService overLengthService;
    @Inject
    private LosysGroupPricesettingService groupPricesettingService;
    @Inject
    private LosysAreaPriceService areaPriceService;

    /**
     * 访问运费查询模块首页
     */
    @At("")
    @Ok("beetl:/platform/losys/freight/index.html")
    @RequiresAuthentication
    public Object index(String  logisticsId,String last, String width, String height, String weight, String insurance, HttpServletRequest req) {
		req.setAttribute("logisticsId", "");
    	return logisticsService.dao().query("lo_logistics", Cnd.where("delFlag", "=", "0"));
    }
    
    /**
     * 访问运费查询模块首页
     */
    @At("/index/?/?/?/?/?/?")
    @Ok("beetl:/platform/losys/freight/tree.html")
    @RequiresAuthentication
    public Object data(String  logisticsId,String last, String width, String height, String weight, String insurance, HttpServletRequest req) {
    		 String sqlString = "select a.id, a.pid,a.`name`,a.path,a.hasChild from lo_area_price p INNER JOIN lo_area a on (p.areaId=a.id) WHERE p.logisticsId=@logisticsId";
    		 Sql sql = Sqls.create(sqlString);
    		sql.params().set("logisticsId", logisticsId);
    		List<Record> records = areaPriceService.list(sql);
    		
            List<Map<String, Object>> tree = new ArrayList<>();
            for (Record area : records) {
                Map<String, Object> obj = new HashMap<>();
                obj.put("id", area.get("id"));
                obj.put("text", area.get("name"));
                obj.put("parent", "".equals(Strings.sNull(area.get("pid"))) ? "#" : area.get("pid"));
                tree.add(obj);
            }
            req.setAttribute("area", Json.toJson(tree));
            req.setAttribute("logisticsId", logisticsId);
            req.setAttribute("last", last.equals("null")? "" : last);
            req.setAttribute("width", width.equals("null")? "" : width);
            req.setAttribute("height", height.equals("null")? "" : height);
            req.setAttribute("weight", weight.equals("null")? "" : weight);
            req.setAttribute("insurance", insurance.equals("null")? "" : insurance);
		
    	return logisticsService.dao().query("lo_logistics", Cnd.where("delFlag", "=", "0"));
    }
    
    /**
     * 保价计算
     * @param insurance
     * @param logistics
     * @return
     */
//    @At("")
//    @Ok("beetl:/platform/losys/freight/add.html")
//    @RequiresAuthentication
    public double insurance(String insurance, String logistics) {
		try {
			int num = Integer.parseInt(insurance);
			String expression = "";
			List<Lo_insurance> insurances = insuranceService.query(Cnd.where("logisticsId", "=", logistics));
			for(Lo_insurance ins:insurances){
				List<Lo_insurance_pricesetting> list = insurancePricesettingService.query(Cnd.where("insuranceId", "=", ins.getId()));
				for (Lo_insurance_pricesetting price : list) {
					int cost = Integer.parseInt(price.getInsurance());
					double value = Double.parseDouble(price.getValue());
					//判断不同保价范围的收费标准
					if (price.getOperator().equals(">") && num > cost) {
						expression = String.valueOf(num * value);
					} else if (price.getOperator().equals("<") && num < cost) {
						expression = String.valueOf(num * value);
					} else if (price.getOperator().equals(">=") && num >= cost) {
						expression = String.valueOf(num * value);
					} else if (price.getOperator().equals("<=") && num <= cost) {
						expression = String.valueOf(num * value);
					} else {
						expression = String.valueOf(num * value);
					}
				}
			}
			double result = Calculator.conversion(expression);
			System.out.println(expression + " = " + result);
			return result;
		} catch (Exception e) {
			return 0;
		}

	}
    
    /**
     * 总价
     */
    @At("/select")
    @Ok("json")
    @RequiresAuthentication
    public Object countMoney(String last,String width,String height,String weight,String logistics,String insurance) {
    	double baojia = insurance(insurance,logistics);
    	double chaoChang = overLengthPrice(Double.parseDouble(last), Double.parseDouble(width), Double.parseDouble(height), Double.parseDouble(weight),logistics);
    	double freight = freight(last,width,height,weight,logistics);
    	return Calculator.conversion(baojia + "+" + chaoChang + "+" + freight);
    }
    
    /**
     * 运费计算
     * @param last
     * @param width
     * @param height
     * @param weight
     * @param logistics
     * @return
     */
    @RequiresAuthentication
	public double freight(String last,String width,String height,String weight,String logistics) {//100
    	try{
    		//last="10";width="10";height="10";weight="30";
        	double wei=Double.parseDouble(weight);
    		Lo_logistics company=logisticsService.fetch(logistics);
    		List<Lo_group_pricesetting> list=groupPricesettingService.query();
    		for(Lo_group_pricesetting heft:list){
    			int cost=Integer.parseInt(heft.getWeight());
    			// 判断不同重量的计费方法
    			if(heft.getOperator().equals(">") && wei>cost){
    				return calculation(last,width,height,company.getSize(),heft.getPrice(),company.getCompare(),heft.getWeight(),heft.getMin());
    			}else if(heft.getOperator().equals("<") && wei<cost){
    				return calculation(last,width,height,company.getSize(),heft.getPrice(),company.getCompare(),heft.getWeight(),heft.getMin());
    			}else if(heft.getOperator().equals(">=") && wei>=cost){
    				return calculation(last,width,height,company.getSize(),heft.getPrice(),company.getCompare(),heft.getWeight(),heft.getMin());
    			}else if(heft.getOperator().equals("<=") && wei<=cost){
    				return calculation(last,width,height,company.getSize(),heft.getPrice(),company.getCompare(),heft.getWeight(),heft.getMin());
    			}else{
    				return calculation(last,width,height,company.getSize(),heft.getPrice(),company.getCompare(),heft.getWeight(),heft.getMin());
    			}
    		}
    		return 0;
    	}catch (Exception e) {
    		return 0;
    	}
	}
    /**
     * 计算步骤
     * @param last
     * @param width
     * @param height
     * @param size
     * @param price
     * @param compare
     * @param weight
     * @param min
     * @return
     */
    public double calculation(String last, String width, String height, String size, String price, String compare, String weight, String min){
    	try{
    		String expression="";
    		String rgex="";
    		expression =last+"*"+width+"*"+height+"/"+size+"*"+price+""+compare;
    		if(min.isEmpty()){
    			rgex=weight+"*"+price+""+compare;
    		}else{
    			rgex=min+""+compare;
    		}
    		double result = Calculator.conversion(expression);
    		double rgexs = Calculator.conversion(rgex);
    		System.out.println(expression + " = " + result);
    		System.out.println(rgex + " = " + rgexs);
    		if(result>rgexs){
    			return result;
    		}else{
    			return rgexs;
    		}
    	}catch (Exception e) {
    		return 0;
    	}
    }
    
    
    /**
     * 超长计算
     * @param longs 长
     * @param height 宽 
     * @param width 高
     * @param weight 重量
     * @param logisticsId
     * @return 
     */
	public double overLengthPrice(double longs, double height, double width, double weight, String logisticsId) {
		double money = 0;
		try {
			int overLengthCount =0;
			Lo_logistics logistics = logisticsService.fetch(logisticsId);
			//该公司是否收超长费用
			if(logistics.getCalType().equals("2")) {
				//统计超长边数
				if(longs > Double.parseDouble(logistics.getValue())) {
					overLengthCount += 1;
				}else if (height > Double.parseDouble(logistics.getValue())) {
					overLengthCount += 1;
				}else if (width > Double.parseDouble(logistics.getValue())) {
					overLengthCount += 1;
				}
				//比较是否收取超长费用
				if(overLengthCount >= Integer.parseInt(logistics.getQuantity())) {
					Lo_overlength_pricesetting overlength = overLengthService.fetch(Cnd.where("logisticsId", "=", logisticsId));
					//参考值是否为0，0代表统一金额/统一百分比收费，不为0 比较参考值大小 
					if(overlength.getCalKey().equals("0")) {
						//是否固定金额
						if(overlength.getType().equals("1")) {
							money = Double.parseDouble(overlength.getCalValue());
						}else {
						//百分比收费	
							money = weight * (Double.parseDouble(overlength.getCalValue())/100);
						}
					}else {
						if (overlength.getOperator().equals(">")) {
							if (weight > Double.parseDouble(overlength.getCalKey())) {								
									//是否固定金额
									if(overlength.getType().equals("1")) {
										money = Double.parseDouble(overlength.getCalValue());
									}else {
									//百分比收费	
										money = weight * (Double.parseDouble(overlength.getCalValue())/100);
									}
							}else {
								//是否固定金额
								if(overlength.getType().equals("1")) {
									money = Double.parseDouble(overlength.getCalValue());
								}else {
								//百分比收费	
									money = weight * (Double.parseDouble(overlength.getCalValue())/100);
								}
							}

						}else if (overlength.getOperator().equals("<")) {
							if (weight < Double.parseDouble(overlength.getCalKey())) {								
								//是否固定金额
								if(overlength.getType().equals("1")) {
									money = Double.parseDouble(overlength.getCalValue());
								}else {
								//百分比收费	
									money = weight * (Double.parseDouble(overlength.getCalValue())/100);
								}
							}else {
								//是否固定金额
								if(overlength.getType().equals("1")) {
									money = Double.parseDouble(overlength.getCalValue());
								}else {
								//百分比收费	
									money = weight * (Double.parseDouble(overlength.getCalValue())/100);
								}
							}	
						}else if (overlength.getOperator().equals("=")) {
							if (weight == Double.parseDouble(overlength.getCalKey())) {								
								//是否固定金额
								if(overlength.getType().equals("1")) {
									money = Double.parseDouble(overlength.getCalValue());
								}else {
								//百分比收费	
									money = weight * (Double.parseDouble(overlength.getCalValue())/100);
								}
							}else {
								//是否固定金额
								if(overlength.getType().equals("1")) {
									money = Double.parseDouble(overlength.getCalValue());
								}else {
								//百分比收费	
									money = weight * (Double.parseDouble(overlength.getCalValue())/100);
								}
							}
						}else if (overlength.getOperator().equals(">=")) {
							if (weight >= Double.parseDouble(overlength.getCalKey())) {								
								//是否固定金额
								if(overlength.getType().equals("1")) {
									money = Double.parseDouble(overlength.getCalValue());
								}else {
								//百分比收费	
									money = weight * (Double.parseDouble(overlength.getCalValue())/100);
								}
							}else {
								//是否固定金额
								if(overlength.getType().equals("1")) {
									money = Double.parseDouble(overlength.getCalValue());
								}else {
								//百分比收费	
									money = weight * (Double.parseDouble(overlength.getCalValue())/100);
								}
							}
						}else if (overlength.getOperator().equals("<=")) {
							if (weight <= Double.parseDouble(overlength.getCalKey())) {								
								//是否固定金额
								if(overlength.getType().equals("1")) {
									money = Double.parseDouble(overlength.getCalValue());
								}else {
								//百分比收费	
									money = weight * (Double.parseDouble(overlength.getCalValue())/100);
								}
							}else {
								//是否固定金额
								if(overlength.getType().equals("1")) {
									money = Double.parseDouble(overlength.getCalValue());
								}else {
								//百分比收费	
									money = weight * (Double.parseDouble(overlength.getCalValue())/100);
								}
							}
						}
					}	
				}else {
					return 0;
				}
			}else {
				return 0;
			}
		} catch (Exception e) {
			
		}
		return money;
	}
    
}
