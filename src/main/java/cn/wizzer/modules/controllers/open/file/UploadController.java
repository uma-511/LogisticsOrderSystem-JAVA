package cn.wizzer.modules.controllers.open.file;

import cn.wizzer.common.base.Globals;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.util.DateUtil;
import cn.wizzer.modules.models.losys.Lo_factory_dataImport;
import cn.wizzer.modules.services.losys.LoFactoryDataimportService;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.nutz.integration.json4excel.J4E;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import com.mysql.jdbc.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by Wizzer on 2016/7/5.
 */
@IocBean
@At("/open/file/upload")
public class UploadController extends NutDao {
    private static final Log log = Logs.get();
    
    @Inject
    private LoFactoryDataimportService dataimportService;

    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:imageUpload"})
    @POST
    @At
    @Ok("json")
    @RequiresAuthentication
    //AdaptorErrorContext必须是最后一个参数
    public Object image(@Param("Filedata") TempFile tf, HttpServletRequest req, AdaptorErrorContext err) {
        try {
            if (err != null && err.getAdaptorErr() != null) {
                return NutMap.NEW().addv("code", 1).addv("msg", "文件不合法");
            } else if (tf == null) {
                return Result.error("空文件");
            } else {
                String p = Globals.AppRoot;
                String f = Globals.AppUploadPath + "/image/" + DateUtil.format(new Date(), "yyyyMMdd") + "/" + R.UU32() + tf.getSubmittedFileName().substring(tf.getSubmittedFileName().indexOf("."));
                Files.write(new File(p + f), tf.getInputStream());
                return Result.success("上传成功", Globals.AppBase+f);
            }
        } catch (Exception e) {
            return Result.error("系统错误");
        } catch (Throwable e) {
            return Result.error("图片格式错误");
        }
    }
    
    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:upLoadExeclFile"})
    @POST
    @At
    @Ok("json")
    @RequiresAuthentication
    //AdaptorErrorContext必须是最后一个参数
    public Object upLoadExeclFile(@Param("Filedata") TempFile tf, HttpServletRequest req, AdaptorErrorContext err) {
    	try {
    		if (err != null && err.getAdaptorErr() != null) {
    			return NutMap.NEW().addv("code", 1).addv("msg", "文件不合法");
    		} else if (tf == null) {
    			return Result.error("空文件");
    		} else {
    			String nameSuffix = tf.getSubmittedFileName().substring(tf.getSubmittedFileName().indexOf(".") + 1);
    			if (!nameSuffix.equals("xsl") && !nameSuffix.equals("xlsx")) return Result.error("请上传Excel文件");

    			List<Lo_factory_dataImport> dataImports = J4E.fromExcel(tf.getInputStream(), Lo_factory_dataImport.class, null);
    			for (Lo_factory_dataImport lo_factory_dataImport : dataImports) {
    				Lo_factory_dataImport dataImport = dataimportService.fetch(Cnd.where("logisticsNo", "=", lo_factory_dataImport.getLogisticsNo()));
    				String status = lo_factory_dataImport.getStatus();
    				
    				if (status.equals("")&&(dataImport!=null&&StringUtils.isNullOrEmpty(dataImport.getStatus()))||dataImport==null) {
    					status = "未发货";
    				}else{
    					status=dataImport.getStatus();
    				}
    				
    				if (dataImport != null) {
    					dataimportService.update(Chain.make("date", lo_factory_dataImport.getDate())
    											 .add("tbName", lo_factory_dataImport.getTbName())
    											 .add("factory", lo_factory_dataImport.getFactory())
    											 .add("addressee", lo_factory_dataImport.getAddressee())
    											 .add("phone", lo_factory_dataImport.getPhone())
    											 .add("receivingAddress", lo_factory_dataImport.getReceivingAddress())
    											 .add("objectContent", lo_factory_dataImport.getObjectContent())
    											 .add("objectNumber", lo_factory_dataImport.getObjectNumber())
    											 .add("logisticsCompany", lo_factory_dataImport.getLogisticsCompany())
    											 .add("logisticsNo", lo_factory_dataImport.getLogisticsNo())
    											 .add("money", lo_factory_dataImport.getMoney())
    											 .add("status", status)
    											 .add("remarks", lo_factory_dataImport.getRemarks()), Cnd.where("id", "=", dataImport.getId()));
    				} else {
    					lo_factory_dataImport.setStatus(status);
    					dataimportService.insert(lo_factory_dataImport);
    				}
				}
    			return Result.success("导入成功");
    		}
    	} catch (Exception e) {
    		return Result.error("系统错误");
    	} catch (Throwable e) {
    		return Result.error("图片格式错误");
    	}
    }
}
