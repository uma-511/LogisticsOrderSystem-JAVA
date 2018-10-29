package cn.wizzer.modules.services.losys;

import cn.wizzer.common.base.Service;
import cn.wizzer.modules.models.losys.Lo_factory_dataImport;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
@IocBean(args = {"refer:dao"})
public class LoFactoryDataimportService extends Service<Lo_factory_dataImport> {
	private static final Log log = Logs.get();

    public LoFactoryDataimportService(Dao dao) {
    	super(dao);
    }
}

