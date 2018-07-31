package cn.wizzer.modules.services.losys;


import cn.wizzer.common.base.Service;
import cn.wizzer.modules.models.losys.Lo_area;

import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;



/**
 * Created by wizzer on 2016/8/11.
 */
@IocBean(args = {"refer:dao"})
public class LosysAreaService extends Service<Lo_area> {
    private static final Log log = Logs.get();

    public LosysAreaService(Dao dao) {
        super(dao);
    }
    
    public List<Record> getAllAreaPrice(String logisticsId){
    	Sql sql=Sqls.create("SELECT la.*,ifnull(llg.`name`,'') as price FROM lo_area la left join "
    			+ " (select * from lo_area_price where logisticsId = @logisticsId ) as lap on la.id=lap.areaId "+
 "LEFT JOIN lo_logistics_group llg on lap.logisticsId=llg.logisticsId AND lap.groupId = llg.id order by la.id");
    	sql.setParam("logisticsId", logisticsId);
    	sql.setCallback(Sqls.callback.entities());
    	sql.setEntity(dao().getEntity(Record.class));
    	dao().execute(sql);
    	List<Record> list=sql.getList(Record.class);
		return list;
    }
}
