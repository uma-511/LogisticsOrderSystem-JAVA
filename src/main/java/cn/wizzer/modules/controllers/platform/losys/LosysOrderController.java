package cn.wizzer.modules.controllers.platform.losys;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.losys.Lo_orders;
import cn.wizzer.modules.models.losys.Lo_taobao_orders;
import cn.wizzer.modules.models.sys.Sys_user;
import cn.wizzer.modules.services.losys.LosysOrderService;
import cn.wizzer.modules.services.losys.LosysTaobaoFactoryService;
import cn.wizzer.modules.services.losys.LosysTaobaoOrderService;
import cn.wizzer.modules.services.sys.SysMenuService;
import cn.wizzer.modules.services.sys.SysUnitService;
import cn.wizzer.modules.services.sys.SysUserService;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.*;
import org.nutz.integration.json4excel.J4E;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.util.Disks;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.WhaleAdaptor;
import org.nutz.mvc.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by wizzer on 2016/6/23.
 */
@IocBean
@At("/platform/losys/order")
@Filters({@By(type = PrivateFilter.class)})
public class LosysOrderController {
    private static final Log log = Logs.get();
    @Inject
    SysUserService userService;
    @Inject
    SysMenuService menuService;
    @Inject
    SysUnitService unitService;
    @Inject
    LosysTaobaoFactoryService factoryService;
    @Inject
    LosysTaobaoOrderService taobaoOrderService;
    @Inject
    LosysOrderService orderService;
    
    @At("")
    @Ok("beetl:/platform/losys/order/index.html")
    @RequiresAuthentication
    public void index() {

    }
    
    @At
    @Ok("beetl:/platform/losys/order/add.html")
    @RequiresAuthentication
    public void add() {
    	
    }

    @At
    @Ok("json")
    @SLog(tag = "新建订单", msg = "")
    public Object addDo(@Param("..") Lo_taobao_orders orders, HttpServletRequest req,HttpSession session) {
		try {
			Subject subject = SecurityUtils.getSubject();
			Sys_user user = (Sys_user) subject.getPrincipal();

			orders.setOrderDate(System.currentTimeMillis());
			taobaoOrderService.insert(orders);
			Lo_orders order = new Lo_orders();
			order.setTbId(orders.getId());
			order.setTaobaoId(user.getId());
			orderService.insert(order);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    /**
     * 淘宝订单管理列表
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
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
        return taobaoOrderService.data(length, start, draw, order, columns, cnd, null);
    }
    
    @At("/exportFile")
	@Ok("json")
	public Object exportFile(HttpServletRequest req) throws FileNotFoundException, IOException {
    	// 第一步，查询数据得到一个数据集合
    	List<Lo_taobao_orders> taobao = taobaoOrderService.dao().query(Lo_taobao_orders.class,null);
    	 
    	// 第二步，使用j4e将数据输出到指定文件或输出流中
    	try (OutputStream out = new FileOutputStream(Files.createFileIfNoExists2("C:/淘宝订单.xls"))) {
    	    J4E.toExcel(out, taobao, null);  
    	}
		return Result.success("导出C盘成功");

	}
    
    
    @At("/importFile")
	@Ok("json")
	@AdaptBy(type = WhaleAdaptor.class)
	public Object importFile(@Param("file")File file) {
		try {
			Subject subject = SecurityUtils.getSubject();
			Sys_user user = (Sys_user) subject.getPrincipal();
			// 第一步，查询数据得到一个数据集合
			// 第一步，使用j4e解析excel文件获得数据集合
			InputStream in = Files.findFileAsStream(Disks.absolute(file.getPath()));
			List<Lo_taobao_orders> people = J4E.fromExcel(in, Lo_taobao_orders.class, null);
			// 第二步，插入数据到数据库
			taobaoOrderService.dao().clear(Lo_taobao_orders.class);
			taobaoOrderService.dao().insert(people);
			List<Lo_taobao_orders> taobao = taobaoOrderService.dao().query(Lo_taobao_orders.class, null);
			orderService.dao().clear(Lo_orders.class);
			for (Lo_taobao_orders taobaoOrder : taobao) {
				Lo_orders orders = new Lo_orders();
				orders.setTbId(taobaoOrder.getId());
				orders.setTaobaoId(user.getId());
				orderService.insert(orders);
			}
			return Result.success("导入成功");
		} catch (Exception e) {
			return Result.error("system.error");
		}

	}
    
}