package cn.wizzer.modules.services.losys;


import cn.wizzer.common.base.Service;
import cn.wizzer.modules.models.losys.Lo_area;
import cn.wizzer.modules.models.losys.Lo_logistics_pricesetting;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;



/**
 * Created by wizzer on 2016/8/11.
 */
@IocBean(args = {"refer:dao"})
public class LosysLogisticsPricesettingService extends Service<Lo_logistics_pricesetting> {
    private static final Log log = Logs.get();

    public LosysLogisticsPricesettingService(Dao dao) {
        super(dao);
    }
    
}
