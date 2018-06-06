package cn.wizzer.modules.services.losys;

import cn.wizzer.common.base.Globals;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.base.Service;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.losys.Lo_taobao_factory;
import cn.wizzer.modules.models.losys.Lo_taobao_order;
import cn.wizzer.modules.models.losys.Lo_taobao_orders;
import cn.wizzer.modules.models.sys.Sys_api;
import cn.wizzer.modules.models.sys.Sys_user;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.*;
import java.security.Key;
import java.util.Date;
import java.util.List;

/**
 * Created by wizzer on 2016/8/11.
 */
@IocBean(args = { "refer:dao" })
public class LosysTaobaoOrderService extends Service<Lo_taobao_orders> {
	private static final Log log = Logs.get();

	@Inject
	LosysTaobaoOrderService taobaoOrderService;
	@Inject
	LosysOrderService orderService;

	public LosysTaobaoOrderService(Dao dao) {
		super(dao);
	}

	public Sql getMessageList(String col, String dir, int beginTime, int endTime, String status, String name,
			String pay) {
		// TODO Auto-generated method stub
		Subject subject = SecurityUtils.getSubject();
		Sys_user user = (Sys_user) subject.getPrincipal();
		String sqlstr = "select t.*,t.id as orderid,o.orderStatus,o.factoryId from lo_taobao_orders t inner JOIN lo_orders o on t.id=o.tbId where 1=1 ";
		if (!status.equals("-1")) {
			sqlstr += "and o.orderStatus=@status ";
		}
		if (beginTime != 0) {
			sqlstr += "and t.orderDate>@beginTime ";
		}
		if (endTime != 0) {
			sqlstr += "and t.orderDate<@endTime ";
		}
		if (user.getAccountType() == 1) {
			sqlstr += "and o.taobaoId=@userid ";
		}
		if (!name.equals("-1")) {
			sqlstr += "and o.taobaoId=@name ";
		}
		if (!pay.equals("-1")) {
			sqlstr += "and o.payStatus=@pay ";
		}
		Sql sql = Sqls.create(sqlstr);
		sql.params().set("status", status);
		sql.params().set("beginTime", beginTime);
		sql.params().set("endTime", endTime);
		sql.params().set("userid", user.getId());
		sql.params().set("name", name);
		sql.params().set("pay", pay);
		return sql.setCallback(Sqls.callback.records());
	}

	public Sql getMessageList(int beginTime, int endTime, String status, String name, String pay) {
		// TODO Auto-generated method stub
		Subject subject = SecurityUtils.getSubject();
		Sys_user user = (Sys_user) subject.getPrincipal();
		String sqlstr = "select t.*,t.id as orderid,o.orderStatus from lo_taobao_orders t INNER JOIN lo_orders o ON t.id=o.tbId where 1=1 ";
		if (!status.equals("-1")) {
			sqlstr += "and o.orderStatus=@status ";
		}
		if (beginTime != 0) {
			sqlstr += "and t.orderDate>@beginTime ";
		}
		if (endTime != 0) {
			sqlstr += "and t.orderDate<@endTime ";
		}
		if (!user.getLoginname().equals("superadmin")) {
			sqlstr += "and o.factoryId=@factoryid";
		}
		if (!name.equals("-1")) {
			sqlstr += "and o.taobaoId=@name ";
		}
		if (!pay.equals("-1")) {
			sqlstr += "and o.payStatus=@pay ";
		}
		Sql sql = Sqls.create(sqlstr);
		sql.params().set("status", status);
		sql.params().set("beginTime", beginTime);
		sql.params().set("endTime", endTime);
		sql.params().set("name", name);
		sql.params().set("pay", pay);
		sql.params().set("factoryid", user.getId());
		return sql.setCallback(Sqls.callback.records());
	}
	
	public List<Lo_taobao_orders> getMessageListExport(String id,int beginTime, int endTime, String status, String name, String pay) {
		// TODO Auto-generated method stub
		Subject subject = SecurityUtils.getSubject();
		Sys_user user = (Sys_user) subject.getPrincipal();
		String sqlstr = "select t.*,t.id as orderid,o.orderStatus from lo_taobao_orders t INNER JOIN lo_orders o ON t.id=o.tbId where 1=1 ";
		if (!id.isEmpty()) {
			sqlstr += "and t.id=@id ";
		}
		if (!status.equals("-1")) {
			sqlstr += "and o.orderStatus=@status ";
		}
		if (beginTime != 0) {
			sqlstr += "and t.orderDate>@beginTime ";
		}
		if (endTime != 0) {
			sqlstr += "and t.orderDate<@endTime ";
		}
		if (user.getAccountType() == 1) {
			sqlstr += "and o.taobaoId=@userid ";
		}
		if (!name.equals("-1")) {
			sqlstr += "and o.taobaoId=@name ";
		}
		if (!pay.equals("-1")) {
			sqlstr += "and o.payStatus=@pay ";
		}
		Sql sql = Sqls.create(sqlstr);
		sql.params().set("id", id);
		sql.params().set("status", status);
		sql.params().set("beginTime", beginTime);
		sql.params().set("endTime", endTime);
		sql.params().set("userid", user.getId());
		sql.params().set("name", name);
		sql.params().set("pay", pay);
		Entity<Lo_taobao_orders> entityOrder = dao().getEntity(Lo_taobao_orders.class);
        sql.setEntity(entityOrder);
		sql.setCallback(Sqls.callback.entities());
		this.dao().execute(sql);
		List<Lo_taobao_orders> records = sql.getList(Lo_taobao_orders.class);
		return records;
	}
	
	public List<Lo_taobao_orders> getListExport(String id,int beginTime, int endTime, String status, String name, String pay) {
		// TODO Auto-generated method stub
		Subject subject = SecurityUtils.getSubject();
		Sys_user user = (Sys_user) subject.getPrincipal();
		String sqlstr = "select t.*,t.id as orderid,o.orderStatus from lo_taobao_orders t INNER JOIN lo_orders o ON t.id=o.tbId where 1=1 ";
		if (!id.isEmpty()) {
			sqlstr += "and t.id=@id ";
		}
		if (!status.equals("-1")) {
			sqlstr += "and o.orderStatus=@status ";
		}
		if (beginTime != 0) {
			sqlstr += "and t.orderDate>@beginTime ";
		}
		if (endTime != 0) {
			sqlstr += "and t.orderDate<@endTime ";
		}
		if (!user.getLoginname().equals("superadmin")) {
			sqlstr += "and o.factoryId=@factoryid";
		}
		if (!name.equals("-1")) {
			sqlstr += "and o.taobaoId=@name ";
		}
		if (!pay.equals("-1")) {
			sqlstr += "and o.payStatus=@pay ";
		}
		Sql sql = Sqls.create(sqlstr);
		sql.params().set("status", status);
		sql.params().set("beginTime", beginTime);
		sql.params().set("endTime", endTime);
		sql.params().set("name", name);
		sql.params().set("pay", pay);
		sql.params().set("factoryid", user.getId());
		Entity<Lo_taobao_orders> entityOrder = dao().getEntity(Lo_taobao_orders.class);
        sql.setEntity(entityOrder);
		sql.setCallback(Sqls.callback.entities());
		this.dao().execute(sql);
		List<Lo_taobao_orders> records = sql.getList(Lo_taobao_orders.class);
		return records;
	}

}
