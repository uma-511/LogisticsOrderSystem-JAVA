package cn.wizzer.modules.controllers.platform.losys;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.losys.Lo_taobao_factory;
import cn.wizzer.modules.models.sys.Sys_api;
import cn.wizzer.modules.models.sys.Sys_menu;
import cn.wizzer.modules.models.sys.Sys_role;
import cn.wizzer.modules.models.sys.Sys_unit;
import cn.wizzer.modules.models.sys.Sys_user;
import cn.wizzer.modules.services.losys.LosysTaobaoFactoryService;
import cn.wizzer.modules.services.sys.SysMenuService;
import cn.wizzer.modules.services.sys.SysUnitService;
import cn.wizzer.modules.services.sys.SysUserService;

import org.apache.commons.lang3.StringUtils;
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
import org.nutz.integration.json4excel.J4E;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wizzer on 2016/6/23.
 */
@IocBean
@At("/platform/losys/taobao")
@Filters({@By(type = PrivateFilter.class)})
public class LosysTaobaoController {
    private static final Log log = Logs.get();
    @Inject
    SysUserService userService;
    @Inject
    SysMenuService menuService;
    @Inject
    SysUnitService unitService;
    @Inject
    LosysTaobaoFactoryService factoryService;
    @At("")
    @Ok("beetl:/platform/losys/taobao/index.html")
    @RequiresAuthentication
    public void index() {

    }

    @At("/edit/?")
    @Ok("beetl:/platform/losys/taobao/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {
        return userService.fetch(id);
    }
    
    @At("/updatePass/?")
    @Ok("beetl:/platform/losys/taobao/updatePass.html")
    @RequiresAuthentication
    public Object updatePass(String id) {
        return userService.fetch(id);
    }
    
    @At("/factory/?")
    @Ok("beetl:/platform/losys/taobao/factory.html")
    @RequiresAuthentication
    public void factory(String id, HttpServletRequest req) {
		List<Sys_user> list = userService.query(Cnd.where("accountType", "=", 2));
		List<NutMap> factory = new ArrayList<>();
		for (Sys_user user : list) {
			NutMap map = new NutMap();
			List<Lo_taobao_factory> factoryid = factoryService.dao().query(Lo_taobao_factory.class,null);
			map.put("id", user.getId());
			map.put("text", user.getLoginname());
			map.put("icon", "");
			map.put("data", "");
			if(!factoryid.isEmpty()){
				for (Lo_taobao_factory taobao : factoryid) {
					if (taobao.getTaobaoid().equals(id)) {
						map.put("state", NutMap.NEW().addv("selected", true));
					} else {
						map.put("state", NutMap.NEW().addv("selected", false));
					}
				}
			}
			factory.add(map);
		}
		req.setAttribute("user", Json.toJson(factory));
		req.setAttribute("id", id);
    }
    
    @At
    @Ok("json")
    public Object editFactoryDo(@Param("factoryIds") String factoryIds, @Param("taobaoid") String taobaoid, HttpServletRequest req) {
        try {
            String[] ids = StringUtils.split(factoryIds, ",");
            factoryService.dao().clear("ls_taobao_factory", Cnd.where("taobaoid", "=", taobaoid));
            for (String s : ids) {
                if (!Strings.isEmpty(s)) {
                	factoryService.insert("ls_taobao_factory", org.nutz.dao.Chain.make("taobaoid", taobaoid).add("factoryid", s));
                }
            }
            Sys_user user = userService.fetch(taobaoid);
            req.setAttribute("name", user.getLoginname());
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At
    @Ok("json")
    @SLog(tag = "修改用户", msg = "用户名:${args[1]}->${args[0].loginname}")
    public Object editDo(@Param("..") Sys_user user, HttpServletRequest req) {
        try {
            user.setOpBy(Strings.sNull(req.getAttribute("uid")));
            user.setOpAt((int) (System.currentTimeMillis() / 1000));
            user.setAccountType(1);
            userService.updateIgnoreNull(user);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }
    
    @At("/audit/?")
    @Ok("beetl:/platform/losys/taobao/audit.html")
    public Object audit(String id, HttpServletRequest request) {
    	Sys_user user = userService.fetch(id);
        return user;
    }

    /**
     * 审核账号
     * @param id
     * @param status
     * @param remark
     * @return
     */
    @At("/doAudit")
    @Ok("json")
    public Object doAudit(@Param("id") String id, @Param("status") int status, @Param("remark") String remark) {
    	 userService.update(Chain.make("status", status), Cnd.where("id", "=", id));
        return Result.success("cw.success");
    }
    
    /**
     * 淘宝账号管理列表
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
    public Object data(@Param("loginname") String loginname, @Param("nickname") String nickname, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
        cnd.and("accountType","=",1);
        if (!Strings.isBlank(loginname))
            cnd.and("loginname", "like", "%" + loginname + "%");
        if (!Strings.isBlank(nickname))
            cnd.and("nickname", "like", "%" + nickname + "%");
        return userService.data(length, start, draw, order, columns, cnd, null);
    }

    @At("/delete/?")
    @Ok("json")
    @SLog(tag = "删除用户", msg = "用户名:${args[1].getAttribute('loginname')}")
    public Object delete(String userId, HttpServletRequest req) {
        try {
            Sys_user user = userService.fetch(userId);
            if ("superadmin".equals(user.getLoginname())) {
                return Result.error("system.not.allow");
            }
            userService.deleteById(userId);
            req.setAttribute("loginname", user.getLoginname());
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }
    
    @At
    @Ok("json")
    @RequiresAuthentication
    public Object tree(@Param("pid") String pid) {
        List<Sys_unit> list = unitService.query(Cnd.where("parentId", "=", Strings.sBlank(pid)).asc("path"));
        List<Map<String, Object>> tree = new ArrayList<>();
        Map<String, Object> obj = new HashMap<>();
        if (Strings.isBlank(pid)) {
            obj.put("id", "root");
            obj.put("text", "所有用户");
            obj.put("children", false);
            tree.add(obj);
        }
        for (Sys_unit unit : list) {
            obj = new HashMap<>();
            obj.put("id", unit.getId());
            obj.put("text", unit.getName());
            obj.put("children", unit.isHasChildren());
            tree.add(obj);
        }
        return tree;
    }
    
    @At
    @Ok("json")
    @RequiresAuthentication
    public Object doChangePassword(@Param("..") Sys_user user,@Param("newPassword") String newPassword, HttpServletRequest req) {
		RandomNumberGenerator rng = new SecureRandomNumberGenerator();
		String salt = rng.nextBytes().toBase64();
		String hashedPasswordBase64 = new Sha256Hash(newPassword, salt, 1024).toBase64();
		user.setSalt(salt);
		user.setPassword(hashedPasswordBase64);
		userService.update(Chain.make("salt", salt).add("password", hashedPasswordBase64),
				Cnd.where("id", "=", user.getId()));
		return Result.success("修改成功");
    }
    
}
