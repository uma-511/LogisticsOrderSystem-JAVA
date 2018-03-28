package cn.wizzer.modules.controllers.platform.losys;

import cn.apiclub.captcha.filter.image.TextureFilter;
import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.losys.Lo_overlength_pricesetting;
import cn.wizzer.modules.services.losys.LosysLogisticsService;
import cn.wizzer.modules.services.losys.LosysOverLengthPricesettingService;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.*;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;


import org.nutz.log.Log;
import org.nutz.log.Logs;

import org.nutz.mvc.annotation.*;





@IocBean
@At("/platform/losys/overLength")
@Filters({@By(type = PrivateFilter.class)})
public class LosysOverLengthController {
    private static final Log log = Logs.get();
    @Inject
    private LosysOverLengthPricesettingService overLengthService;
    @Inject
    private LosysLogisticsService logisticsService;
    
    
    /**
     * 首页
     */
    @At("")
    @Ok("beetl:/platform/losys/overLength/index.html")
    @RequiresAuthentication
    public void index(HttpServletRequest req) {
    	
    }

    /**
     * 添加页面
     */
    @At
    @Ok("beetl:/platform/losys/overLength/add.html")
    @RequiresAuthentication
    public Object add() {
    	return logisticsService.dao().query("lo_logistics", Cnd.where("delFlag", "=", "0"));
    }

    /**
     * 修改页面
     */
    @At("/edit/?")
    @Ok("beetl:/platform/losys/overLength/edit.html")
    public Object edit(String id,HttpServletRequest req) {
    	/*Lo_overlength_pricesetting overLength = overLengthService.dao().fetch(Lo_overlength_pricesetting.class, Cnd.where("id", "=", id));*/
    	Sql sql = Sqls.create("SELECT o.id, l.`name`, o.type, o.operator,o.calKey,o.calValue, o.logisticsId from lo_overlength_pricesetting o LEFT JOIN lo_logistics l  ON(o.logisticsId = l.id) where o.id =" + id);
    	List<Record> overLength = overLengthService.list(sql);
    	return overLength.get(0);
    }
    
    
    /**
     * 添加
     */
	@At
    @Ok("json")
    public Object addDo(@Param("..") Lo_overlength_pricesetting overlength, HttpServletRequest req) {
        try {
        	List<Record> overLengths = overLengthService.dao().query("lo_overlength_pricesetting", Cnd.where("logisticsId", "=", overlength.getLogisticsId()));
        	if (overLengths.size()>=1) {
        		 return Result.error(500, "该公司已存在超长价格，无法继续添加");
			}else {
				overLengthService.insert(overlength);
			}
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }
    
    
    
    /**
     * 删除
     */
    @At({"/delete/?", "/delete"})
    @Ok("json")
    public Object delete(String oneId, @Param("ids") String[] ids, HttpServletRequest req) {
    	try {
        	if (ids != null && ids.length > 0) {
            	for (String string : ids) {
            		overLengthService.clear(Cnd.where("id", "=", string));
            	}
                req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
            } else {
            	/*overLengthService.delete(oneId);*/
            	overLengthService.clear(Cnd.where("id", "=", oneId));
                req.setAttribute("id", oneId);
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }
    
	/**
	 * 修改
	 */
    @At
    @Ok("json")
    public Object editDo(@Param("..") Lo_overlength_pricesetting overlength, HttpServletRequest req) {	
        try {
        	overLengthService.update(overlength);
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
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Sql countSql = Sqls.create("SELECT o.id, l.`name`, o.type, o.operator,o.calKey,o.calValue from lo_overlength_pricesetting o LEFT JOIN lo_logistics l  ON(o.logisticsId = l.id) ");
        return overLengthService.data(length, start, draw, countSql, countSql);
        
    }
    
}
