package cn.wizzer.modules.controllers.platform.losys;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.common.util.DateUtil;
import cn.wizzer.modules.models.losys.Lo_factory_dataImport;
import cn.wizzer.modules.models.losys.Lo_orders;
import cn.wizzer.modules.models.losys.Lo_taobao_order;
import cn.wizzer.modules.models.losys.Lo_taobao_orders;
import cn.wizzer.modules.models.sys.Sys_role;
import cn.wizzer.modules.models.sys.Sys_user;
import cn.wizzer.modules.models.sys.Sys_user_role;
import cn.wizzer.modules.services.losys.LoFactoryDataimportService;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.integration.json4excel.J4E;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
		} else if (role.getCode().equals("dataManage")) {
			req.setAttribute("isDisplay", "1");
		} else {
			req.setAttribute("isDisplay", "2");
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
    
    @At("/send/?")
    @Ok("json") 
    public Object send(String id ,HttpServletRequest req) {
//    	Subject subject = SecurityUtils.getSubject();
//		Sys_user user = (Sys_user) subject.getPrincipal();
    	Result res=new Result();
		try {
//			Lo_factory_dataImport lfd = loFactoryDataimportService.fetch(Cnd.where("logisticsNo", "=", id).and("tbName", "=", user.getShopname()));
			Lo_factory_dataImport lfd = loFactoryDataimportService.fetch(Cnd.where("logisticsNo", "=", id));
			if(lfd!=null){
				if("已发货".equals(lfd.getStatus())){
					res=Result.error("该订单已经发货");
				}else{
					lfd.setStatus("已发货");
					int sendResult=loFactoryDataimportService.update(lfd);
					if(sendResult>0){
						res=Result.success("发货成功");
					}else{
						res=Result.error("发货失败");
					}
				}
			}else{
				res=Result.error("找不到对应物流单");
			}
			return res;
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }
    
    @At("/seeDataImport")
	@Ok("beetl:/platform/losys/factory/dataImport/seeDataImport.html")
	@RequiresAuthentication
	public void seeDataImport() {

	}
    
	@At("/exportFile")
	@Ok("void")
	public void exportFile(	@Param("tbName") String tbName, 
							@Param("factory") String factory, 
							@Param("addressee") String addressee, 
							@Param("phone") String phone, 
							@Param("objectContent") String objectContent,
							@Param("date1") String date1, 
							@Param("date2") String date2, 
							@Param("logisticsNo") String logisticsNo, 
							HttpServletRequest req, HttpServletResponse resp) {
		try {
			// 登录用户
			Subject subject = SecurityUtils.getSubject();
			Sys_user user = (Sys_user) subject.getPrincipal();
				
			// 登录对应的角色关系
			Sys_user_role user_role = dao.fetch(Sys_user_role.class, Cnd.where("userId", "=", user.getId()));
			
			// 登录的角色
			Sys_role role = dao.fetch(Sys_role.class,  Cnd.where("id", "=", user_role.getRoleId()));
			
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
			
			// 条件 工厂名
			if (!Strings.isBlank(factory)) {
				cnd.and("factory", "like", "%" + factory + "%");
			}
			
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
			
			List<Lo_factory_dataImport> dataImports = loFactoryDataimportService.query(cnd);
			resp.setContentType("application/vnd.ms-excel;charset=utf-8");
			resp.setHeader("Content-Disposition", "attachment;filename=" + new String("订单数据导出.xlsx".getBytes(),"ISO-8859-1"));
			export(resp.getOutputStream(), dataImports);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 创建Excel，并导出
	 *
	 * @author X J B
	 *
	 * @date 2018年11月27日 下午3:33:46
	 *
	 * @param os
	 *
	 */
	private void export(OutputStream os, List<Lo_factory_dataImport> dataImports) {
        //工作簿
		XSSFWorkbook wk = new XSSFWorkbook();
		CellStyle cellStyle = wk.createCellStyle();
		cellStyle.setDataFormat(wk.createDataFormat().getFormat("yyyy-MM-dd"));
//		CreationHelper createHelper = wk.getCreationHelper();
//		cellStyle.setDataFormat( createHelper.createDataFormat().getFormat("yyyy-MM-dd"));
        //创建工作表
        Sheet sheet = wk.createSheet("Sheet1");
        //创建一行,参数指的是： 行的索引=行号-1
        Row row = sheet.createRow(0);
        //列名,表头
        String[] headers = {"序号", "日期", "淘宝店名", "工厂", "收件人", "收件手机", "收件地址", "物件内容", "物件数量", "物流公司", "物流单号", "费用", "订单状态", "备注"};
        for(int i = 0; i < headers.length; i++){
            row.createCell(i).setCellValue(headers[i]);
        }
        //创建单元格， 参数指的是：列的索引，从0开始
        //输出每一条记录
        if(null != dataImports && dataImports.size() > 0){
        	Lo_factory_dataImport dataImport = null;
            for(int i = 1; i<=dataImports.size(); i++){
            	dataImport = dataImports.get(i-1);
            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        		String time= dataImport.getDate()==null?"":sdf.format(dataImport.getDate());
                row = sheet.createRow(i);
                row.createCell(0).setCellValue(i);
//                row.createCell(1).setCellStyle(cellStyle);
                row.createCell(1).setCellValue(time);
                row.createCell(2).setCellValue(dataImport.getTbName()==null?"":dataImport.getTbName());
                row.createCell(3).setCellValue(dataImport.getFactory()==null?"":dataImport.getFactory());
                row.createCell(4).setCellValue(dataImport.getAddressee()==null?"":dataImport.getAddressee());
                row.createCell(5).setCellValue(dataImport.getPhone()==null?"":dataImport.getPhone());       
                row.createCell(6).setCellValue(dataImport.getReceivingAddress()==null?"":dataImport.getReceivingAddress());       
                row.createCell(7).setCellValue(dataImport.getObjectNumber()==null?0:dataImport.getObjectNumber());       
                row.createCell(8).setCellValue(dataImport.getObjectContent()==null?"":dataImport.getObjectContent());       
                row.createCell(9).setCellValue(dataImport.getLogisticsCompany()==null?"":dataImport.getLogisticsCompany());       
                row.createCell(10).setCellValue(dataImport.getLogisticsNo()==null?"":dataImport.getPhone());       
                row.createCell(11).setCellValue(dataImport.getMoney());       
                row.createCell(12).setCellValue(dataImport.getStatus()==null?"":dataImport.getStatus());       
                row.createCell(13).setCellValue(dataImport.getRemarks()==null?"":dataImport.getRemarks());       
            }
        }
        //输出到输出流中
        try {
            wk.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                wk.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
