package cn.wizzer.modules.services.losys;

import cn.wizzer.common.base.Globals;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.base.Service;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.losys.Lo_taobao_factory;
import cn.wizzer.modules.models.losys.Lo_taobao_orders;
import cn.wizzer.modules.models.sys.Sys_api;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
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
@IocBean(args = {"refer:dao"})
public class LosysTaobaoOrderService extends Service<Lo_taobao_orders> {
    private static final Log log = Logs.get();
    
    @Inject
    LosysTaobaoOrderService taobaoOrderService;
    @Inject
    LosysOrderService orderService;

    public LosysTaobaoOrderService(Dao dao) {
        super(dao);
    }

	public Sql getMessageList(String col, String dir) {
		// TODO Auto-generated method stub
				Sql sql=Sqls.create("select t.*,t.id as orderid,o.orderStatus from lo_taobao_orders t inner JOIN lo_orders o on t.id=o.tbId order by " + col + " "+ dir);
		return sql.setCallback(Sqls.callback.str());
	}

}
