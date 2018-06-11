package cn.wizzer.modules.controllers.platform.losys;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.integration.json4excel.J4E;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.WhaleAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.common.util.DateUtil;
import cn.wizzer.modules.models.losys.Lo_area;
import cn.wizzer.modules.models.losys.Lo_area_price;
import cn.wizzer.modules.models.losys.Lo_logistics;
import cn.wizzer.modules.models.losys.Lo_logistics_pricesetting;
import cn.wizzer.modules.models.losys.Lo_orders;
import cn.wizzer.modules.models.losys.Lo_taobao_factory;
import cn.wizzer.modules.models.losys.Lo_taobao_order;
import cn.wizzer.modules.models.losys.Lo_taobao_orders;
import cn.wizzer.modules.models.sys.Sys_user;
import cn.wizzer.modules.services.losys.LosysAreaPriceService;
import cn.wizzer.modules.services.losys.LosysAreaService;
import cn.wizzer.modules.services.losys.LosysGroupPricesettingService;
import cn.wizzer.modules.services.losys.LosysLogisticsPricesettingService;
import cn.wizzer.modules.services.losys.LosysLogisticsService;
import cn.wizzer.modules.services.losys.LosysOrderService;
import cn.wizzer.modules.services.losys.LosysTaobaoFactoryService;
import cn.wizzer.modules.services.losys.LosysTaobaoOrderService;
import cn.wizzer.modules.services.sys.SysMenuService;
import cn.wizzer.modules.services.sys.SysUnitService;
import cn.wizzer.modules.services.sys.SysUserService;

/**
 * Created by wizzer on 2016/6/23.
 */
@IocBean
@At("/platform/losys/taobao/order")
@Filters({ @By(type = PrivateFilter.class) })
public class LosysTaobaoOrderController {
	private static final Log log = Logs.get();
	@Inject
	private SysUserService userService;
	@Inject
	private SysMenuService menuService;
	@Inject
	private SysUnitService unitService;
	@Inject
	private LosysTaobaoFactoryService taobaoFactoryService;
	@Inject
	private LosysTaobaoOrderService taobaoOrderService;
	@Inject
	private LosysOrderService orderService;
	@Inject
	private LosysLogisticsService logisticsService;
	@Inject
	private LosysAreaPriceService areaPriceService;
	@Inject
	private LosysAreaService areaService;
	@Inject
	private LosysGroupPricesettingService groupPricesettingService;
	@Inject
	private LosysLogisticsPricesettingService logisticsPricesettingService;
	@Inject
	private LosysFreightController losysFreight;

	@At("")
	@Ok("beetl:/platform/losys/taobao/order/index.html")
	@RequiresAuthentication
	public Object index(HttpServletRequest req) {
		req.setAttribute("today", DateUtil.getDate());
		return userService.query(Cnd.where("accountType", "=", 1));
	}

	@At
	@Ok("beetl:/platform/losys/taobao/order/add.html")
	@RequiresAuthentication
	public void add(HttpServletRequest req) {
		String sqlString = "select a.id, a.pid,a.`name`,a.path,a.hasChild from lo_area a  WHERE a.pid=''";
        Sql sql = Sqls.create(sqlString);
        List<Record> areaOne = areaPriceService.list(sql);
        req.setAttribute("areaOne", areaOne);
		req.setAttribute("logistics", logisticsService.query());
	}
	
	/**
	 * 查询子区域
	 */
	@At
	@Ok("json")
	@RequiresAuthentication
	public Object child(String areaId, String logisticsId, HttpServletRequest req) {
		String sqlString;
		if(logisticsId != null) {
			sqlString = "select a.id, a.pid,a.`name`,a.path,a.hasChild from lo_area_price p INNER JOIN lo_area a on (p.areaId=a.id) WHERE p.logisticsId=@logisticsId and a.pid=@pid";
		}else {
			sqlString = "select a.id, a.pid,a.`name`,a.path,a.hasChild from lo_area a  WHERE a.pid=@pid";
		}
		Sql sql = Sqls.create(sqlString);
		if(logisticsId != null) {
			sql.params().set("logisticsId", logisticsId);
		}
		sql.params().set("pid", areaId);
		List<Record> area = areaPriceService.list(sql);
		req.setAttribute("area", area);
		return area;
	}
	
	
	@At("/detail/?")
	@Ok("beetl:/platform/losys/taobao/order/detail.html")
	@RequiresAuthentication
	public Object detail(String id, HttpServletRequest req) {
		List<Lo_orders> orders = orderService.query(Cnd.where("tbId", "=", id));
		req.setAttribute("orders", orders.get(0));
		return taobaoOrderService.fetch(id);
	}

	@At
	@Ok("json")
	@SLog(tag = "修改用户", msg = "")
	@AdaptBy(type = WhaleAdaptor.class)
	public Object editDo(@Param("..") Lo_taobao_orders tOrders, @Param("..") Lo_orders orders, HttpServletRequest req) {
		try {
			Subject subject = SecurityUtils.getSubject();
			if (subject != null) {
				Sys_user user = (Sys_user) subject.getPrincipal();
				tOrders.setOpBy(Strings.sNull(req.getAttribute("uid")));
				tOrders.setOpAt((int) (System.currentTimeMillis() / 1000));
				tOrders.setOrderDate((int) (System.currentTimeMillis() / 1000));
				taobaoOrderService.updateIgnoreNull(tOrders);
				orderService.update(Chain.make("expNum", orders.getExpNum()).add("packagePhoto", orders.getPackagePhoto()).add("orderStatus", 5)
						.add("userId",user.getId()).add("payStatus",orders.getPayStatus()).add("freight", orders.getFreight()),
						Cnd.where("tbId", "=", tOrders.getId()));
				return Result.success("system.success");
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
	}

	@At
	@Ok("json")
	@SLog(tag = "新建订单", msg = "")
	public Object addDo(@Param("..") Lo_taobao_orders orders, HttpServletRequest req, HttpSession session) {
		try {
			Subject subject = SecurityUtils.getSubject();
			if (subject != null) {
				Lo_area area=areaService.fetch(orders.getAddress());
				Lo_logistics logistics=logisticsService.fetch(orders.getLogistics());
				
				Sys_user user = (Sys_user) subject.getPrincipal();
				Lo_orders order = new Lo_orders();
				if(!orders.getSize().equals("") && user.getAccountType()==0){
					String[] size=orders.getSize().toString().split("\\*");
					String last="";
					String width="";
					String height="";
					for (String s : size) {
						if(width!=""){
							height=s;
							break;
						}
						if(last!=""){
							width=s;
							continue;
						}
						last=s;
					}
					double freight= losysFreight.freight(last, width, height, "70", orders.getLogistics(), orders.getAddress());
					if(freight == -2.0){
						return Result.error("system.area");
					}
					if(freight == -1.0){
						return Result.error("system.size");
					}
					order.setFreight(String.valueOf(freight));
				}else{
					String min=minfreight(orders.getLogistics(),orders.getAddress());
					if(min!=null){
						order.setFreight(min);
					}else{
						order.setFreight("0");
					}
				}
				orders.setOrderDate((int)(System.currentTimeMillis() / 1000));
				orders.setLogistics(logistics.getName());
				orders.setAddress(area.getName());
				
				taobaoOrderService.insert(orders);
				order.setTbId(orders.getId());
				order.setTaobaoId(user.getId());
				List<Sys_user> factoryId=userService.query(Cnd.where("accountType", "=", 2));
				String factoryid="";
				if(!factoryId.isEmpty()){
					for(Sys_user factory:factoryId){
						factoryid+=factory.getId()+",";
					}
					String str = factoryid;
					str=str.substring(0, str.length()-1);
					order.setUserId(user.getId()+','+str);
				}else{
					order.setUserId(user.getId());
				}
				orderService.insert(order);
				return Result.success("system.success");
			}
			return Result.error("system.error");
		} catch (Exception e) {
			System.out.println(e);
			return Result.error("system.error");
		}
	}

	/**
	 * 查询低消
	 * @param logistics
	 * @param areaId
	 * @return
	 */
	public String minfreight(String logistics,String areaId){
		List<Lo_area_price> areas = areaPriceService
				.query(Cnd.where("logisticsId", "=", logistics).and("areaId", "=", areaId));
		if (areas.size()>0) {
			for (Lo_area_price areaPrice : areas) {
				List<Lo_logistics_pricesetting> groups = logisticsPricesettingService
						.query(Cnd.where("logisticsGroupId", "=", areaPrice.getGroupId()));
				for (Lo_logistics_pricesetting pricesetting : groups) {
					Record heft = groupPricesettingService.dao().fetch("lo_group_pricesetting",
							Cnd.where("id", "=", pricesetting.getPricesettingId()));
					if (heft != null) {
						return heft.getString("min");
					}
				}
			}
			
		}
		return null;
	}
	/**
	 * 指派工厂
	 * 
	 * @param id
	 * @param req
	 */
	@At("/appoint/?")
	@Ok("beetl:/platform/losys/taobao/order/appoint.html")
	@RequiresAuthentication
	public void factory(String ids, HttpServletRequest req) {
		String[] tbid=ids.split(",");
		Subject subject = SecurityUtils.getSubject();
		StringBuilder sb = new StringBuilder();
		if (subject != null) {
			for (String s : tbid) {
				Sys_user user = (Sys_user) subject.getPrincipal();
				List<Lo_taobao_factory> common = taobaoFactoryService.query(Cnd.where("taobaoid", "=", user.getId()));
				List<NutMap> factory = new ArrayList<>();
				if (!common.isEmpty()) {
					for (Lo_taobao_factory communal : common) {
						NutMap map = new NutMap();
						List<Sys_user> list = userService.query(Cnd.where("id", "=", communal.getFactoryid()));
						for (Sys_user user2 : list) {
							map.put("id", user2.getId());
							map.put("text", user2.getLoginname());
							map.put("icon", "");
							map.put("data", "");
							List<Lo_orders> order = orderService.query(Cnd.where("tbId", "=", s));
							if (order.get(0).getFactoryId() != null) {
								if (order.get(0).getFactoryId().equals(user2.getId())) {
									map.put("state", NutMap.NEW().addv("selected", true));
								}
							}
							factory.add(map);
						}
					}
				}
			    sb.append(s).append(",");
			    req.setAttribute("user", Json.toJson(factory));
			}
			req.setAttribute("ids", sb.toString());
		}
	}

	@At
	@Ok("json")
	public Object editFactoryDo(@Param("factoryid") String factoryid, @Param("tbId") String[] tbid,
			HttpServletRequest req) {
		try {
			Subject subject = SecurityUtils.getSubject();
    		if (subject != null) {
    			for (String s : tbid) {
    				Sys_user user = (Sys_user) subject.getPrincipal();
    				List<Lo_orders> userid=orderService.query(Cnd.where("tbId", "=", s));
    				String userId=userid.get(0).getUserId().replace(factoryid, "");
    				orderService.update(Chain.make("factoryId", factoryid).add("orderStatus", 1).add("userId", userId), Cnd.where("tbId", "=", s));
    			}
    		}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
	}

	/**
	 * 淘宝订单管理列表
	 * 
	 * @param loginname
	 * @param nickname
	 * @param length
	 * @param start
	 * @param draw
	 * @param order
	 * @param columns
	 * @return
	 */
	@At
	@Ok("json:{locked:'password|salt',ignoreNull:false}") // 忽略password和createAt属性,忽略空属性的json输出
	@RequiresAuthentication
	public Object data(@Param("beginDate") String beginDate, @Param("endDate") String endDate,
			@Param("status") String status, @Param("name") String name, @Param("pay") String pay,
			@Param("length") int length, @Param("start") int start, @Param("draw") int draw,
			@Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		int beginTime = 0;
		int endTime = 0;
		String tableName = Times.format("yyyyMM", new Date());
		if (Strings.isNotBlank(beginDate)) {
			tableName = Times.format("yyyyMM", Times.D(beginDate + " 00:00:00"));
			beginTime = DateUtil.getTime(beginDate + " 00:00:00");
		}
		if (Strings.isNotBlank(endDate)) {
			endTime = DateUtil.getTime(endDate + " 23:59:59");
		}
		String col = "orderDate";// 默认
		String dir = "asc";
		if (order != null && order.size() > 0) {
			for (DataTableOrder orders : order) {
				DataTableColumn c = columns.get(orders.getColumn());
				col = Sqls.escapeSqlFieldValue(c.getData()).toString();
				dir = orders.getDir();
			}
		}
		Sql sql = taobaoOrderService.getMessageList(col, dir, beginTime, endTime, status, name, pay);
		return taobaoOrderService.data(length, start, draw, sql, sql);
	}

	@At("/exportFile/?/?/?/?/?")
	@Ok("void")
	public void exportFile(String beginDate,String endDate,String status,String name,String pay,HttpServletRequest req, HttpServletResponse resp) throws FileNotFoundException, IOException {
		try {
			// 第一步，查询数据得到一个数据集合
			Subject subject = SecurityUtils.getSubject();
			if (subject != null) {
				Sys_user user = (Sys_user) subject.getPrincipal();
				String filename = URLEncoder.encode("淘宝订单.xls", "UTF-8");
				resp.addHeader("content-type", "application/shlnd.ms-excel;charset=utf-8");
				resp.addHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
				OutputStream out = resp.getOutputStream();
				List<Lo_taobao_orders> taobao = null;
				int beginTime = 0;
				int endTime = 0;
				String tableName = Times.format("yyyyMM", new Date());
				if (Strings.isNotBlank(beginDate)) {
					tableName = Times.format("yyyyMM", Times.D(beginDate + " 00:00:00"));
					beginTime = DateUtil.getTime(beginDate + " 00:00:00");
				}
				if (Strings.isNotBlank(endDate)) {
					endTime = DateUtil.getTime(endDate + " 23:59:59");
				}
				List<Lo_taobao_order> ordersData=new ArrayList<Lo_taobao_order>();
				if (user.getLoginname().equals("superadmin")) {
					taobao = taobaoOrderService.getMessageListExport("", beginTime, endTime, status, name, pay);
					ordersData=exportData(out, taobao);
					J4E.toExcel(out, ordersData, null);
				} else {
					List<Lo_orders> orders = orderService.query(Cnd.where("taobaoId", "=", user.getId()));
					for (Lo_orders order : orders) {
						taobao = taobaoOrderService.getMessageListExport(order.getTbId(), beginTime, endTime, status, name, pay);
						List<Lo_taobao_order> orders2 = exportData(out, taobao);
						System.out.println(orders2);
						for (Lo_taobao_order lo_taobao_order : orders2) {
							ordersData.add(lo_taobao_order);
						}
					}
					J4E.toExcel(out, ordersData, null);
				}
				// poi
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	public List<Lo_taobao_order> exportData(OutputStream out, List<Lo_taobao_orders> taobao){
    	List<Lo_taobao_order> orderData = new ArrayList<Lo_taobao_order>();
    	for(Lo_taobao_orders date:taobao){
    		Lo_taobao_order r =new Lo_taobao_order();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
			String orderDate=sdf.format(new Date(Long.valueOf(date.getOrderDate()+"000")));
			r.setId(date.getId());
			r.setAccount(date.getAccount());
			r.setFileDate(orderDate);
			r.setRecipient(date.getRecipient());
			r.setFixedTelephone(date.getFixedTelephone());
			r.setMobilePhone(date.getMobilePhone());
			r.setAddress(date.getAddress());
			r.setMailingModel(date.getMailingModel());
			r.setSize(date.getSize());
			r.setLogistics(date.getLogistics());
			r.setQuantity(date.getQuantity());
			r.setColor(date.getColor());
			orderData.add(r);
		}
		return orderData;
    	
    }
	@At("/importFile")
	@Ok("json")
	@AdaptBy(type = WhaleAdaptor.class)
	public Object importFile(@Param("file") File file) {
		try {
			Subject subject = SecurityUtils.getSubject();
			Sys_user user = (Sys_user) subject.getPrincipal();
			// 第一步，查询数据得到一个数据集合
			// 第一步，使用j4e解析excel文件获得数据集合
			InputStream in = Files.findFileAsStream(Disks.absolute(file.getPath()));
			List<Lo_taobao_orders> people = J4E.fromExcel(in, Lo_taobao_orders.class, null);
			// 第二步，插入数据到数据库
			for (Lo_taobao_orders orders : people) {
				orders.setOrderDate((int)(System.currentTimeMillis() / 1000));
				taobaoOrderService.dao().insert(orders);
				Lo_orders order = new Lo_orders();
				order.setTbId(orders.getId());
				order.setTaobaoId(user.getId());
				List<Sys_user> factoryId=userService.query(Cnd.where("accountType", "=", 2));
				String factoryid="";
				if(!factoryId.isEmpty()){
					for(Sys_user factory:factoryId){
						factoryid+=factory.getId()+",";
					}
					String str = factoryid;
					str=str.substring(0, str.length()-1);
					order.setUserId(user.getId()+','+str);
				}else{
					order.setUserId(user.getId());
				}
				orderService.insert(order);
			}
			return Result.success("导入成功");
		} catch (Exception e) {
			return Result.error("system.error");
		}

	}

}
