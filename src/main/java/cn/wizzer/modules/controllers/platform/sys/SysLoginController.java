package cn.wizzer.modules.controllers.platform.sys;

import cn.apiclub.captcha.Captcha;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.util.StringUtil;
import cn.wizzer.common.services.log.SLogService;
import cn.wizzer.common.shiro.exception.EmptyCaptchaException;
import cn.wizzer.common.shiro.exception.IncorrectCaptchaException;
import cn.wizzer.common.shiro.filter.AuthenticationFilter;
import cn.wizzer.modules.controllers.open.util.RandomCode;
import cn.wizzer.modules.controllers.open.util.SMSSend;
import cn.wizzer.modules.models.losys.Lo_orders;
import cn.wizzer.modules.models.sys.Sys_log;
import cn.wizzer.modules.models.sys.Sys_role;
import cn.wizzer.modules.models.sys.Sys_unit;
import cn.wizzer.modules.models.sys.Sys_user;
import cn.wizzer.modules.services.losys.LosysOrderService;
import cn.wizzer.modules.services.sys.SysRoleService;
import cn.wizzer.modules.services.sys.SysUserService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.WhaleAdaptor;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Created by wizzer on 2016/6/22.
 */
@IocBean // 声明为Ioc容器中的一个Bean
@At("/platform/login") // 整个模块的路径前缀
@Ok("json:{locked:'password|createAt',ignoreNull:true}") // 忽略password和createAt属性,忽略空属性的json输出
public class SysLoginController {
	private static final Log log = Logs.get();
	@Inject
	SysUserService userService;
	@Inject
	SLogService sLogService;
	@Inject
	SysRoleService roleService;
	@Inject
	LosysOrderService orderService;

	@At("")
	@Ok("re")
	@Filters
	public String login() {
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			return "redirect:/platform/home";
		} else {
			return "beetl:/platform/sys/login.html";
		}
	}

	@At("/noPermission")
	@Ok("beetl:/platform/sys/noPermission.html")
	@Filters
	public void noPermission() {

	}

	@At("/enroll")
	@Ok("beetl:/platform/sys/enroll.html")
	@Filters
	public void enroll() {

	}
	
	@At("/forgetPwd")
	@Ok("beetl:/platform/sys/forgetPwd.html")
	@Filters
	public void forgetPwd() {

	}

	/**
	 * 切换样式，对登陆用户有效
	 *
	 * @param theme
	 * @param req
	 * @RequiresUser 记住我有效
	 * @RequiresAuthentication 就算记住我也需要重新验证身份
	 */
	@At("/theme")
	@RequiresAuthentication
	public void theme(@Param("loginTheme") String theme, HttpServletRequest req) {
		if (!Strings.isEmpty(theme)) {
			Subject subject = SecurityUtils.getSubject();
			if (subject != null) {
				Sys_user user = (Sys_user) subject.getPrincipal();
				user.setLoginTheme(theme);
				userService.update(Chain.make("loginTheme", theme), Cnd.where("id", "=", user.getId()));
			}
		}
	}

	/**
	 * 切换布局，对登陆用户有效
	 *
	 * @param p
	 * @param v
	 * @param req
	 * @RequiresUser 记住我有效
	 * @RequiresAuthentication 就算记住我也需要重新验证身份
	 */
	@At("/layout")
	@RequiresAuthentication
	public void layout(@Param("p") String p, @Param("v") boolean v, HttpServletRequest req) {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			Sys_user user = (Sys_user) subject.getPrincipal();
			if ("sidebar".equals(p)) {
				userService.update(Chain.make("loginSidebar", v), Cnd.where("id", "=", user.getId()));
				user.setLoginSidebar(v);
			} else if ("boxed".equals(p)) {
				userService.update(Chain.make("loginBoxed", v), Cnd.where("id", "=", user.getId()));
				user.setLoginBoxed(v);
			} else if ("scroll".equals(p)) {
				userService.update(Chain.make("loginScroll", v), Cnd.where("id", "=", user.getId()));
				user.setLoginScroll(v);
			}
		}

	}

	/**
	 * 注册新用户
	 * 
	 * @param user
	 * @return
	 */
	@At("/bind")
	@Ok("json:full")
	@AdaptBy(type = WhaleAdaptor.class)
	public Object bind(@Param("..") Sys_user user, HttpServletRequest req) {
		try {
			RandomNumberGenerator rng = new SecureRandomNumberGenerator();
			String salt = rng.nextBytes().toBase64();
			String hashedPasswordBase64 = new Sha256Hash(user.getPassword(), salt, 1024).toBase64();
			user.setSalt(salt);
			user.setPassword(hashedPasswordBase64);
			user.setLoginPjax(true);
			user.setLoginCount(0);
			user.setLoginAt(0);
			user.setNickname(user.getLoginname());
			user.setStatus(2);
			user = userService.insert(user);
			if (user.getAccountType() == 1) {
				List<Sys_role> role = roleService.query(Cnd.where("name", "=", "淘宝方"));
				for (Sys_role roleid : role) {
					userService.insert("sys_user_role",
							org.nutz.dao.Chain.make("roleId", roleid.getId()).add("userId", user.getId()));
				}
			} else {
				List<Sys_role> role = roleService.query(Cnd.where("name", "=", "工厂方"));
				for (Sys_role roleid : role) {
					userService.insert("sys_user_role",
							org.nutz.dao.Chain.make("roleId", roleid.getId()).add("userId", user.getId()));
				}
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
	}

	/**
	 * 验证用户名是否存在
	 * 
	 * @param theme
	 * @param req
	 */
	@At("/validLoginname")
	@Ok("json:full")
	@AdaptBy(type = WhaleAdaptor.class)
	public Object validLoginname(@Param("loginname") String loginname, HttpServletRequest req) {
		if (!Strings.isEmpty(loginname)) {
			Subject subject = SecurityUtils.getSubject();
			if (subject != null) {
				List<Sys_user> lname = userService.query(Cnd.where("loginname", "=", loginname));
				if (lname.isEmpty()) {
					return Result.success("system.success");
				} else {
					return Result.error("system.error");
				}
			}
		}
		return req;
	}

	/**
	 * 验证手机号码是否存在
	 * @param phone
	 * @param req
	 * @return
	 */
	@At("/validPhone")
	@Ok("json:full")
	@AdaptBy(type = WhaleAdaptor.class)
	public Object validPhone(@Param("phone") String phone, HttpServletRequest req) {
		if (!Strings.isEmpty(phone)) {
			Subject subject = SecurityUtils.getSubject();
			if (subject != null) {
				List<Sys_user> lphone = userService.query(Cnd.where("phone", "=", phone));
				if (lphone.isEmpty()) {
					return Result.success("system.success");
				} else {
					return Result.error("system.error");
				}
			}
		}
		return req;
	}
	
	/**
	 * 验证淘宝/工厂名称是否存在
	 * 
	 * @param theme
	 * @param req
	 */
	@At("/validShopname")
	@Ok("json:full")
	@AdaptBy(type = WhaleAdaptor.class)
	public Object validShopname(@Param("shopname") String shopname, HttpServletRequest req) {
		if (!Strings.isEmpty(shopname)) {
			Subject subject = SecurityUtils.getSubject();
			if (subject != null) {
				List<Sys_user> sname = userService.query(Cnd.where("shopname", "=", shopname));
				if (sname.isEmpty()) {
					return Result.success("system.success");
				} else {
					return Result.error("system.error");
				}
			}
		}
		return req;
	}

	/**
	 * 发送验证码
	 *
	 * @param phone
	 * @return
	 */
	@At("/sendVerifyCode")
	@Ok("json:full")
	@AdaptBy(type = WhaleAdaptor.class)
	public Object sendVerifyCode(String phone) {
		Sys_user user = userService.fetch(Cnd.where("phone", "=", phone));
		int time=(int) (System.currentTimeMillis() / 1000);
		if (user == null) {
			return Result.error(-1,"此手机号码未注册过！");
		}
//		if (user != null && time - user.getOpAt() < 60) {
//			return Result.error(-2,"操作过于频繁，请稍后！");
//		}
		String code = RandomCode.genIntCode(6);
		String content = "短信验证码：" + code + "，验证码有效时间为5分钟！【台盟物流供应链】";
		SMSSend.sendSMS(phone, content);
		user.setCode(code);
		user.setOpAt(time);
		user.setPhone(phone);
		userService.updateIgnoreNull(user);
		return Result.success(code,"发送成功！");
	}
	
	/**
	 * 验证验证码
	 *
	 * @param phone
	 * @param code
	 * @return
	 */
	@At("/verifyCode")
	@Ok("json:full")
	@AdaptBy(type = WhaleAdaptor.class)
	public Object verifyCode(@Param("phone") String phone, @Param("code") String code) {
		Sys_user user = userService.fetch(Cnd.where("phone", "=", phone));
//		if (user == null) {
//			return "验证码失效，请重新发送！";
//		}
		if (!StringUtils.equals(user.getCode(), code)) {
			return Result.error(1,"验证码验证错误！");
		}
		if (user.getOpAt() - (int) (System.currentTimeMillis() / 1000)>300) {
			return Result.error(2,"验证码已失效，请重新发送！");
		}
		return Result.success("验证码验证正确！");
	}
	
	@At
	@Ok("json:full")
	@AdaptBy(type = WhaleAdaptor.class)
    public Object doChangePassword(@Param("phone") String phone, @Param("newPwd") String newPassword, HttpServletRequest req) {
		    Sys_user user = userService.fetch(Cnd.where("phone", "=", phone));
            RandomNumberGenerator rng = new SecureRandomNumberGenerator();
            String salt = rng.nextBytes().toBase64();
            String hashedPasswordBase64 = new Sha256Hash(newPassword, salt, 1024).toBase64();
            user.setSalt(salt);
            user.setPassword(hashedPasswordBase64);
            userService.update(Chain.make("salt", salt).add("password", hashedPasswordBase64), Cnd.where("id", "=", user.getId()));
            return Result.success("修改成功");
    }
	
	/**
	 * 登陆验证
	 *
	 * @param token
	 * @param req
	 * @return
	 */
	@At("/doLogin")
	@Ok("json")
	@Filters(@By(type = AuthenticationFilter.class))
	public Object doLogin(@Attr("loginToken") AuthenticationToken token, HttpServletRequest req, HttpSession session) {
		int errCount = 0;
		try {
			// 输错三次显示验证码窗口
			errCount = NumberUtils
					.toInt(Strings.sNull(SecurityUtils.getSubject().getSession(true).getAttribute("errCount")));
			Subject subject = SecurityUtils.getSubject();
			ThreadContext.bind(subject);
			subject.login(token);
			Sys_user user = (Sys_user) subject.getPrincipal();
			if (user.getStatus() == 1) {

				int count = user.getLoginCount() == null ? 0 : user.getLoginCount();
				userService.update(Chain.make("loginIp", user.getLoginIp())
						.add("loginAt", (int) (System.currentTimeMillis() / 1000)).add("loginCount", count + 1)
						.add("isOnline", true), Cnd.where("id", "=", user.getId()));
				Sys_log sysLog = new Sys_log();
				sysLog.setType("info");
				sysLog.setTag("用户登陆");
				sysLog.setSrc(this.getClass().getName() + "#doLogin");
				sysLog.setMsg("成功登录系统！");
				sysLog.setIp(StringUtil.getRemoteAddr());
				sysLog.setOpBy(user.getId());
				sysLog.setOpAt((int) (System.currentTimeMillis() / 1000));
				sysLog.setNickname(user.getNickname());
				sLogService.async(sysLog);
				return Result.success("login.success");
			} else if (user.getStatus() == 0) {
				SecurityUtils.getSubject().getSession(true).setAttribute("errCount", errCount);
				subject.logout();
				return Result.error(5, "login.error.not");
			} else {
				SecurityUtils.getSubject().getSession(true).setAttribute("errCount", errCount);
				subject.logout();
				return Result.error(5, "login.error.verify");
			}
		} catch (IncorrectCaptchaException e) {
			// 自定义的验证码错误异常
			return Result.error(1, "login.error.captcha");
		} catch (EmptyCaptchaException e) {
			// 验证码为空
			return Result.error(2, "login.error.captcha");
		} catch (LockedAccountException e) {
			return Result.error(3, "login.error.locked");
		} catch (UnknownAccountException e) {
			errCount++;
			SecurityUtils.getSubject().getSession(true).setAttribute("errCount", errCount);
			return Result.error(4, "login.error.user");
		} catch (AuthenticationException e) {
			errCount++;
			SecurityUtils.getSubject().getSession(true).setAttribute("errCount", errCount);
			return Result.error(5, "login.error.user");
		} catch (Exception e) {
			errCount++;
			SecurityUtils.getSubject().getSession(true).setAttribute("errCount", errCount);
			return Result.error(6, "login.error.system");
		}
	}

	/**
	 * 退出系统
	 */
	@At
	@Ok(">>:/platform/login")
	public void logout(HttpSession session) {
		try {
			Subject currentUser = SecurityUtils.getSubject();
			Sys_user user = (Sys_user) currentUser.getPrincipal();
			currentUser.logout();
			Sys_log sysLog = new Sys_log();
			sysLog.setType("info");
			sysLog.setTag("用户登出");
			sysLog.setSrc(this.getClass().getName() + "#logout");
			sysLog.setMsg("成功退出系统！");
			sysLog.setIp(StringUtil.getRemoteAddr());
			sysLog.setOpBy(user.getId());
			sysLog.setOpAt((int) (System.currentTimeMillis() / 1000));
			sysLog.setNickname(user.getNickname());
			sLogService.async(sysLog);
			userService.update(Chain.make("isOnline", false), Cnd.where("id", "=", user.getId()));
		} catch (SessionException ise) {
			log.debug("Encountered session exception during logout.  This can generally safely be ignored.", ise);
		} catch (Exception e) {
			log.debug("Logout error", e);
		}
	}

	@At("/captcha")
	@Ok("raw:png")
	public BufferedImage next(HttpSession session, @Param("w") int w, @Param("h") int h) {
		if (w * h < 1) { // 长或宽为0?重置为默认长宽.
			w = 200;
			h = 60;
		}
		Captcha captcha = new Captcha.Builder(w, h).addText()
				// .addBackground(new GradiatedBackgroundProducer())
				// .addNoise(new StraightLineNoiseProducer()).addBorder()
				// .gimp(new FishEyeGimpyRenderer())
				.build();
		String text = captcha.getAnswer();
		session.setAttribute("captcha", text);
		return captcha.getImage();
	}

	/**
	 * 订单状态改变推送提示消息
	 * 
	 * @param req
	 * @return
	 */
	@At("/orderStatus")
	@Ok("json:full")
	@AdaptBy(type = WhaleAdaptor.class)
	public Object orderStatus(HttpServletRequest req) {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			Sys_user user = (Sys_user) subject.getPrincipal();
			Sql sql = Sqls.create("select * from lo_orders where not (userId like @userId)");
			sql.params().set("userId", "%" + user.getId() + "%");
			List<Record> orders = orderService.list(sql);
			if (!orders.isEmpty()) {
				for (Record order : orders) {
					if (order.getString("userId").equals("")) {
						orderService.update(Chain.make("userId", user.getId()),
								Cnd.where("id", "=", order.getString("id")));
					} else {
						orderService.update(Chain.make("userId", user.getId() + "," + order.getString("userId")),
								Cnd.where("id", "=", order.getString("id")));
					}
				}
				return Result.success("system.order");
			} else {
				return Result.success("system.success");
			}
		}
		return Result.success("system.success");
	}

}
