package cn.wizzer.modules.services.losys;


import cn.wizzer.common.base.Service;
import cn.wizzer.modules.models.losys.Lo_group_pricesetting;
import cn.wizzer.modules.models.losys.Lo_insurance_pricesetting;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;



/**
 * Created by wizzer on 2016/8/11.
 */
@IocBean(args = {"refer:dao"})
public class LosysInsurancePricesettingService extends Service<Lo_insurance_pricesetting> {
    private static final Log log = Logs.get();

    public LosysInsurancePricesettingService(Dao dao) {
        super(dao);
    }

	public Sql getMessageList(String logisticsid) {
		String sqlstr = "select a.* from lo_insurance_pricesetting a INNER JOIN lo_insurance b on a.insuranceId=b.id where 1=1 ";
		if (!logisticsid.isEmpty()) {
			sqlstr += "and b.logisticsId=@logisticsid ";
		}
		Sql sql = Sqls.create(sqlstr);
		sql.params().set("logisticsid", logisticsid);
		return sql.setCallback(Sqls.callback.records());
	}
    
}
