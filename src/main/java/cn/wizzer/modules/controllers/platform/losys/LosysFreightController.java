package cn.wizzer.modules.controllers.platform.losys;

import cn.apiclub.captcha.filter.image.TextureFilter;
import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.common.util.Calculator;
import cn.wizzer.modules.models.losys.Lo_area;
import cn.wizzer.modules.models.losys.Lo_area_price;
import cn.wizzer.modules.models.losys.Lo_group_pricesetting;
import cn.wizzer.modules.models.losys.Lo_insurance;
import cn.wizzer.modules.models.losys.Lo_insurance_pricesetting;
import cn.wizzer.modules.models.losys.Lo_logistics;
import cn.wizzer.modules.models.losys.Lo_logistics_group;
import cn.wizzer.modules.models.losys.Lo_logistics_pricesetting;
import cn.wizzer.modules.models.losys.Lo_overlength_pricesetting;
import cn.wizzer.modules.models.sys.Sys_menu;
import cn.wizzer.modules.models.sys.Sys_unit;
import cn.wizzer.modules.services.losys.LosysAreaPriceService;
import cn.wizzer.modules.services.losys.LosysAreaService;
import cn.wizzer.modules.services.losys.LosysGroupPricesettingService;
import cn.wizzer.modules.services.losys.LosysInsurancePricesettingService;
import cn.wizzer.modules.services.losys.LosysInsuranceService;
import cn.wizzer.modules.services.losys.LosysLogisticsGroupService;
import cn.wizzer.modules.services.losys.LosysLogisticsPricesettingService;
import cn.wizzer.modules.services.losys.LosysLogisticsService;
import cn.wizzer.modules.services.losys.LosysOverLengthPricesettingService;
import cn.wizzer.modules.services.sys.SysMenuService;
import cn.wizzer.modules.services.sys.SysUnitService;
import cn.wizzer.modules.services.sys.SysUserService;
import oracle.net.aso.a;

import java.math.BigDecimal;
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
 * 
 * @author
 *
 */

@IocBean
@At("/platform/losys/freight")
@Filters({ @By(type = PrivateFilter.class) })
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
	@Inject
	LosysLogisticsGroupService logisticsGroupService;
	@Inject
	LosysLogisticsPricesettingService logisticsPricesettingService;

	/**
     * 访问运费查询模块首页
     */
    @At({"","/index/?/?/?/?/?/?"})
    @Ok("beetl:/platform/losys/freight/index.html")
    @RequiresAuthentication
    public Object index(String  logisticsId, String last, String width, String height, String weight, String insurance, HttpServletRequest req) {
        if (logisticsId == null) {
            req.setAttribute("logisticsId", "");
        }else {
            req.setAttribute("logisticsId", logisticsId);
            req.setAttribute("last", !last.equals("null")?last:null);
            req.setAttribute("width", !width.equals("null")?width:null);
            req.setAttribute("height", !height.equals("null")?height:null);
            req.setAttribute("weight", !weight.equals("null")?weight:null);
            req.setAttribute("insurance", !insurance.equals("null")?insurance:null);
            
        }
        List<Record> records = logisticsService.dao().query("lo_logistics", Cnd.where("delFlag", "=", "0"));
        
        if (records.size()>0) {
            String sqlString = "select a.id, a.pid,a.`name`,a.path,a.hasChild from lo_area_price p INNER JOIN lo_area a on (p.areaId=a.id) WHERE p.logisticsId=@logisticsId and a.pid=''";
            Sql sql = Sqls.create(sqlString);
            if(logisticsId == null) {
                sql.params().set("logisticsId", records.get(0).get("id"));
            }else {
                sql.params().set("logisticsId", logisticsId);
            }
            List<Record> areaOne = areaPriceService.list(sql);
            req.setAttribute("areaOne", areaOne);
            if (areaOne.size()>0) {
				String sqlString2 = "select a.id, a.pid,a.`name`,a.path,a.hasChild from lo_area_price p INNER JOIN lo_area a on (p.areaId=a.id) WHERE p.logisticsId=@logisticsId and a.pid=@pid";
				Sql sql2 = Sqls.create(sqlString2);
				if(logisticsId == null) {
	                sql2.params().set("logisticsId", records.get(0).get("id"));
	            }else {
	                sql2.params().set("logisticsId", logisticsId);
	            }
				sql2.params().set("pid", areaOne.get(0).get("id"));
				List<Record> areaTwo = areaPriceService.list(sql2);
				req.setAttribute("areaTwo", areaTwo);
				if (areaTwo.size() > 0) {
					String sqlString3 = "select a.id, a.pid,a.`name`,a.path,a.hasChild from lo_area_price p INNER JOIN lo_area a on (p.areaId=a.id) WHERE p.logisticsId=@logisticsId and a.pid=@pid";
					Sql sql3 = Sqls.create(sqlString3);
					if(logisticsId == null) {
		                sql3.params().set("logisticsId", records.get(0).get("id"));
		            }else {
		                sql3.params().set("logisticsId", logisticsId);
		            }
					sql3.params().set("pid", areaTwo.get(0).get("id"));
					List<Record> areaThree = areaPriceService.list(sql3);
					req.setAttribute("areaThree", areaThree);

				}
			}
		}

		return records;
	}

	/**
	 * 查询子区域
	 */
	@At
	@Ok("json")
	@RequiresAuthentication
	public Object child(String areaId, String logisticsId, HttpServletRequest req) {
		String sqlString = "select a.id, a.pid,a.`name`,a.path,a.hasChild from lo_area_price p INNER JOIN lo_area a on (p.areaId=a.id) WHERE p.logisticsId=@logisticsId and a.pid=@pid";
		Sql sql = Sqls.create(sqlString);
		sql.params().set("logisticsId", logisticsId);
		sql.params().set("pid", areaId);
		List<Record> area = areaPriceService.list(sql);
		req.setAttribute("area", area);
		return area;
	}

	/**
	 * 顺丰保价计算
	 * 
	 * @param insurance
	 * @param logistics
	 * @return
	 */
	// @At("")
	// @Ok("beetl:/platform/losys/freight/add.html")
	// @RequiresAuthentication
	public double insurance(String insurance, String logistics) {
		try {
			int num = Integer.parseInt(insurance);
			int costOne = 0;
			double valueOne = 0;
			int costTwo = 0;
			double valueTwo = 0;
			double money = 0;
			String type="";
			Lo_logistics company = logisticsService.fetch(logistics);
			List<Lo_insurance> insurances = insuranceService.query(Cnd.where("logisticsId", "=", logistics));
			for (Lo_insurance ins : insurances) {
				List<Lo_insurance_pricesetting> list = insurancePricesettingService
						.query(Cnd.where("insuranceId", "=", ins.getId()));
				for (Lo_insurance_pricesetting price : list) {
					int cost = Integer.parseInt(price.getInsurance());
					double value = Double.parseDouble(price.getValue());
					// 判断不同的物流公司的收费标准
					if (company.getName().equals("顺丰")) {
						// 判断不同保价范围的收费标准
						if (price.getOperator().equals(">")) {
							type=price.getType();
							if (money >= 2) {
								valueTwo = value;
							} else {
								money = value;
							}
							costTwo = cost;
						} else if (price.getOperator().equals("<=")) {
							costOne = cost;
							valueOne = value;
						}
						if (costOne != 0 && costTwo != 0) {
							if (costOne != costTwo) {
								if (num > costOne && num <= costTwo) {
									return support(price.getType(), num, money);
								}
								if (num <= costOne) {
									return support(price.getType(), num, valueOne);
								}
								if (num > costTwo) {
									return support(type, num, valueTwo);
								}
							} else {
								continue;
							}
						} else {
							continue;
						}
					} else {
						// 判断不同保价范围的收费标准
						if (price.getOperator().equals(">")) {
							return support(price.getType(), num, value);
						} else if (price.getOperator().equals("<=")) {
							return support(price.getType(), num, value);
						}
					}
				}
			}
		} catch (Exception e) {
			return -1;
		}
		return -1;

	}

	/**
	 * 判断保价的是固定金额还是百分百，然后返回保价
	 * 
	 * @param string
	 * @param num
	 * @param value
	 * @return
	 */
	public double support(String string, int num, double value) {
		String expression = "";
		if (string.equals("1")) {
			expression = String.valueOf(value);
		} else {
			expression = String.valueOf(num * value);
		}
		double result = Calculator.conversion(expression);
		System.out.println(expression + " = " + result);
		return result;
	}

	/**
	 * 总价
	 */
	@At("/select")
	@Ok("json")
	@RequiresAuthentication
	public Object countMoney(String last, String width, String height, String weight, String logistics,
			String insurance, String areaId) {
		double baojia = insurance(insurance, logistics);
		double chaoChang = overLengthPrice(Double.parseDouble(last), Double.parseDouble(width),
				Double.parseDouble(height), Double.parseDouble(weight), logistics);
		double freight = freight(last, width, height, weight, logistics, areaId);
		if (baojia == -1.0 || chaoChang == -1.0 || freight == -1.0) {
			return "请检测保价，超长价格，价格等设置";
		}
		BigDecimal b = new BigDecimal(Calculator.conversion(baojia + "+" + chaoChang + "+" + freight));
		double money = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return money;
	}

	/**
	 * 运费计算
	 * 
	 * @param last
	 * @param width
	 * @param height
	 * @param weight
	 * @param logistics
	 * @return
	 */
	@RequiresAuthentication
	public double freight(String last, String width, String height, String weight, String logistics, String areaId) {// 100
		try {
			double wei = Double.parseDouble(weight);
			Lo_logistics company = logisticsService.fetch(logistics);
			List<Lo_area_price> areas = areaPriceService
					.query(Cnd.where("logisticsId", "=", logistics).and("areaId", "=", areaId));
			for (Lo_area_price areaPrice : areas) {
				List<Lo_logistics_pricesetting> groups = logisticsPricesettingService
						.query(Cnd.where("logisticsGroupId", "=", areaPrice.getGroupId()));
				for (Lo_logistics_pricesetting pricesetting : groups) {
					Record heft = groupPricesettingService.dao().fetch("lo_group_pricesetting",
							Cnd.where("id", "=", pricesetting.getPricesettingId()));
					if (heft != null) {
						int cost = Integer.parseInt(heft.getString("weight"));
						// 判断不同重量的计费方法
						if (heft.getString("operator").equals(">") && wei > cost) {
							return calculation(company.getFormula(), last, width, height, company.getSize(),
									heft.getString("price"), company.getCompare(), heft.getString("weight"),
									heft.getString("min"));
						} else if (heft.getString("operator").equals("<") && wei < cost) {
							return calculation(company.getFormula(), last, width, height, company.getSize(),
									heft.getString("price"), company.getCompare(), heft.getString("weight"),
									heft.getString("min"));
						} else if (heft.getString("operator").equals(">=") && wei >= cost) {
							return calculation(company.getFormula(), last, width, height, company.getSize(),
									heft.getString("price"), company.getCompare(), heft.getString("weight"),
									heft.getString("min"));
						} else if (heft.getString("operator").equals("<=") && wei <= cost) {
							return calculation(company.getFormula(), last, width, height, company.getSize(),
									heft.getString("price"), company.getCompare(), heft.getString("weight"),
									heft.getString("min"));
						} else {
							return calculation(company.getFormula(), last, width, height, company.getSize(),
									heft.getString("price"), company.getCompare(), heft.getString("weight"),
									heft.getString("min"));
						}
					}
				}
			}
			return -1;
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * 运费计算步骤
	 * 
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
	public double calculation(String formula, String last, String width, String height, String size, String price,
			String compare, String weight, String min) {
		try {
			String rgex = "";
			formula = formula.replaceAll("last", last);
			formula = formula.replaceAll("width", width);
			formula = formula.replaceAll("height", height);
			formula = formula.replaceAll("size", size);
			formula = formula.replaceAll("price", price);
			formula = formula.replaceAll("compare", compare);
			System.out.println(formula);
			if (min.isEmpty()) {
				rgex = weight + "*" + price + "" + compare;
			} else {
				rgex = min + "" + compare;
			}
			double result = Calculator.conversion(formula);
			double rgexs = Calculator.conversion(rgex);
			System.out.println(formula + " = " + result);
			System.out.println(rgex + " = " + rgexs);
			if (result > rgexs) {
				return result;
			} else {
				return rgexs;
			}
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 超长计算
	 * 
	 * @param longs
	 *            长
	 * @param height
	 *            宽
	 * @param width
	 *            高
	 * @param weight
	 *            重量
	 * @param logisticsId
	 * @return
	 */
	public double overLengthPrice(double longs, double height, double width, double weight, String logisticsId) {
		double money = 0;
		try {
			int overLengthCount = 0;
			Lo_logistics logistics = logisticsService.fetch(logisticsId);
			// 该公司是否收超长费用
			if (logistics.getCalType().equals("2")) {
				// 统计超长边数
				if (longs > Double.parseDouble(logistics.getValue())) {
					overLengthCount += 1;
				}
				if (height > Double.parseDouble(logistics.getValue())) {
					overLengthCount += 1;
				}
				if (width > Double.parseDouble(logistics.getValue())) {
					overLengthCount += 1;
				}
				// 比较是否收取超长费用
				if (overLengthCount >= Integer.parseInt(logistics.getQuantity())) {
					List<Lo_overlength_pricesetting> overlengths = overLengthService
							.query(Cnd.where("logisticsId", "=", logisticsId));

					for (Lo_overlength_pricesetting overlength : overlengths) {
						// 参考值是否为0，0代表统一金额/统一百分比收费，不为0 比较参考值大小
						if (overlength.getCalKey().equals("0")) {
							// 是否固定金额
							if (overlength.getType().equals("1")) {
								money = Double.parseDouble(overlength.getCalValue());
							} else {
								// 百分比收费
								money = weight * (Double.parseDouble(overlength.getCalValue()) / 100);
							}
						} else {
							if (overlength.getOperator().equals(">")) {
								if (weight > Double.parseDouble(overlength.getCalKey())) {
									// 是否固定金额
									if (overlength.getType().equals("1")) {
										money = Double.parseDouble(overlength.getCalValue());
									} else {
										// 百分比收费
										money = weight * (Double.parseDouble(overlength.getCalValue()) / 100);
									}
								}
							} else if (overlength.getOperator().equals("<")) {
								if (weight < Double.parseDouble(overlength.getCalKey())) {
									// 是否固定金额
									if (overlength.getType().equals("1")) {
										money = Double.parseDouble(overlength.getCalValue());
									} else {
										// 百分比收费
										money = weight * (Double.parseDouble(overlength.getCalValue()) / 100);
									}
								}
							} else if (overlength.getOperator().equals("=")) {
								if (weight == Double.parseDouble(overlength.getCalKey())) {
									// 是否固定金额
									if (overlength.getType().equals("1")) {
										money = Double.parseDouble(overlength.getCalValue());
									} else {
										// 百分比收费
										money = weight * (Double.parseDouble(overlength.getCalValue()) / 100);
									}
								}
							} else if (overlength.getOperator().equals(">=")) {
								if (weight >= Double.parseDouble(overlength.getCalKey())) {
									// 是否固定金额
									if (overlength.getType().equals("1")) {
										money = Double.parseDouble(overlength.getCalValue());
									} else {
										// 百分比收费
										money = weight * (Double.parseDouble(overlength.getCalValue()) / 100);
									}
								}
							} else if (overlength.getOperator().equals("<=")) {
								if (weight <= Double.parseDouble(overlength.getCalKey())) {
									// 是否固定金额
									if (overlength.getType().equals("1")) {
										money = Double.parseDouble(overlength.getCalValue());
									} else {
										// 百分比收费
										money = weight * (Double.parseDouble(overlength.getCalValue()) / 100);
									}
								}
							}
						}
					}
				} else {
					return 0;
				}
			} else {
				return 0;
			}
		} catch (Exception e) {

		}
		return money;
	}

}
