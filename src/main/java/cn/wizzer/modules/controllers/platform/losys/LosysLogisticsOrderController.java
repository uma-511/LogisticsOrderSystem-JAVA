package cn.wizzer.modules.controllers.platform.losys;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.common.util.DateUtil;
import cn.wizzer.modules.models.losys.Lo_logistics;
import cn.wizzer.modules.models.losys.Lo_orders;
import cn.wizzer.modules.models.losys.Lo_taobao_factory;
import cn.wizzer.modules.models.losys.Lo_taobao_kyorder;
import cn.wizzer.modules.models.losys.Lo_taobao_sforder;
import cn.wizzer.modules.models.losys.Lo_taobao_orders;
import cn.wizzer.modules.models.sys.Sys_user;
import cn.wizzer.modules.services.losys.LosysAreaPriceService;
import cn.wizzer.modules.services.losys.LosysLogisticsService;
import cn.wizzer.modules.services.losys.LosysOrderService;
import cn.wizzer.modules.services.losys.LosysTaobaoFactoryService;
import cn.wizzer.modules.services.losys.LosysTaobaoOrderService;
import cn.wizzer.modules.services.sys.SysMenuService;
import cn.wizzer.modules.services.sys.SysUnitService;
import cn.wizzer.modules.services.sys.SysUserService;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.*;
import org.nutz.dao.Chain;
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
import org.nutz.mvc.annotation.*;

import com.mysql.jdbc.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wizzer on 2016/6/23.
 */
@IocBean
@At("/platform/losys/logistics/order")
@Filters({ @By(type = PrivateFilter.class) })
public class LosysLogisticsOrderController {
	private static final Log log = Logs.get();
	@Inject
	SysUserService userService;
	@Inject
	SysMenuService menuService;
	@Inject
	SysUnitService unitService;
	@Inject
	LosysTaobaoFactoryService taobaoFactoryService;
	@Inject
	LosysTaobaoOrderService taobaoOrderService;
	@Inject
	LosysOrderService orderService;
	@Inject
    private LosysLogisticsService logisticsService;
	@Inject
	private LosysAreaPriceService areaPriceService;

	@At("")
	@Ok("beetl:/platform/losys/logistics/order/index.html")
	@RequiresAuthentication
	public Object index(HttpServletRequest req) {
		req.setAttribute("today", DateUtil.getDate());
		req.setAttribute("logistics", logisticsService.query());
		return userService.query(Cnd.where("accountType", "=", 1));
	}

	@At
	@Ok("beetl:/platform/losys/logistics/order/add.html")
	@RequiresAuthentication
	public void add(HttpServletRequest req) {
		String sqlString = "select a.id, a.pid,a.`name`,a.path,a.hasChild from lo_area a  WHERE a.pid=''";
        Sql sql = Sqls.create(sqlString);
        List<Record> areaOne = areaPriceService.list(sql);
        req.setAttribute("areaOne", areaOne);
		req.setAttribute("logistics", logisticsService.query());
	}

	@At("/detail/?")
	@Ok("beetl:/platform/losys/logistics/order/detail.html")
	@RequiresAuthentication
	public Object detail(String id, HttpServletRequest req) {
		List<Lo_orders> orders = orderService.query(Cnd.where("tbId", "=", id));
		List<Lo_logistics> logistics=logisticsService.query();
		req.setAttribute("orders", orders.get(0));
		req.setAttribute("logistics", logistics);
		return taobaoOrderService.fetch(id);
	}
	
	@At("/delDo/?")
	@Ok("json")
	@SLog(tag = "删除订单", msg = "")
	public Object delDo(String orderId, HttpServletRequest req) {
		try {
			    taobaoOrderService.clear(Cnd.where("id","=",orderId));
			    orderService.clear(Cnd.where("tbId","=",orderId));
				return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
	}

	/**
	 * 物流订单管理列表
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
			@Param("logisticsid") String logisticsid,
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
		String logistics="";
		if(!logisticsid.equals("-1")){
			logistics=logisticsService.fetch(logisticsid).getName();
		}
		Sql sql = taobaoOrderService.getMessageList(col, dir, beginTime, endTime, status, name, pay,logistics);
		return taobaoOrderService.data(length, start, draw, sql, sql);
	}

	@At("/exportFile/?/?/?/?/?/?")
	@Ok("void")
	public void exportFile(String beginDate,String endDate,String status,String name,String pay,String logisticsid,HttpServletRequest req, HttpServletResponse resp) throws FileNotFoundException, IOException {
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
				String logistics=logisticsService.fetch(logisticsid).getName();
				List<Lo_taobao_sforder> shunforders=new ArrayList<Lo_taobao_sforder>();
				List<Lo_taobao_kyorder> kuayorders=new ArrayList<Lo_taobao_kyorder>();
				if (user.getLoginname().equals("superadmin")) {
					taobao = taobaoOrderService.getMessageListExport("", beginTime, endTime, status, name, pay,logistics);
					if(logistics.equals("顺丰")){
						shunforders=exportShunf(out, taobao);
						J4E.toExcel(out, shunforders, null);
					}else{
						kuayorders=exportKuay(out, taobao);
						J4E.toExcel(out, kuayorders, null);
					}
				} else {
					List<Lo_orders> orders = orderService.query(Cnd.where("taobaoId", "=", user.getId()));
					if(logistics.equals("顺丰")){
						for (Lo_orders order : orders) {
							taobao = taobaoOrderService.getMessageListExport(order.getTbId(), beginTime, endTime, status, name, pay,logistics);
							List<Lo_taobao_sforder> ordersShunf = exportShunf(out, taobao);
							for (Lo_taobao_sforder ordersf : ordersShunf) {
								shunforders.add(ordersf);
							}
						}
						J4E.toExcel(out, shunforders, null);
					}else{
						for (Lo_orders order : orders) {
							taobao = taobaoOrderService.getMessageListExport(order.getTbId(), beginTime, endTime, status, name, pay,logistics);
							List<Lo_taobao_kyorder> ordersKuay = exportKuay(out, taobao);
							for (Lo_taobao_kyorder orderky : ordersKuay) {
								kuayorders.add(orderky);
							}
						}
						J4E.toExcel(out, kuayorders, null);
					}
				}
				// poi
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	/**
	 * 导出顺丰excel
	 * @param out
	 * @param taobao
	 * @return
	 */
	public List<Lo_taobao_sforder> exportShunf(OutputStream out, List<Lo_taobao_orders> taobao){
    	List<Lo_taobao_sforder> orderData = new ArrayList<Lo_taobao_sforder>();
    	for(Lo_taobao_orders date:taobao){
    		Lo_taobao_sforder r =new Lo_taobao_sforder();
			r.setId(date.getId());
			r.setRecipient(date.getRecipient());
			r.setFixedTelephone(date.getFixedTelephone());
			r.setMobilePhone(date.getMobilePhone());
			r.setAddress(date.getAddress());
			r.setQuantity(date.getQuantity());
			orderData.add(r);
		}
		return orderData;
    	
    }
	
	/**
	 * 导出跨越excel
	 * @param out
	 * @param taobao
	 * @return
	 */
	public List<Lo_taobao_kyorder> exportKuay(OutputStream out, List<Lo_taobao_orders> taobao){
    	List<Lo_taobao_kyorder> orderData = new ArrayList<Lo_taobao_kyorder>();
    	for(Lo_taobao_orders date:taobao){
    		Lo_taobao_kyorder r =new Lo_taobao_kyorder();
			r.setRecipient(date.getRecipient());
			r.setFixedTelephone(date.getFixedTelephone());
			r.setMobilePhone(date.getMobilePhone());
			r.setAddress(date.getAddress());
			r.setQuantity(date.getQuantity());
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
				order.setUserId(user.getId());
				orderService.insert(order);
			}
			return Result.success("导入成功");
		} catch (Exception e) {
			return Result.error("system.error");
		}

	}

}
