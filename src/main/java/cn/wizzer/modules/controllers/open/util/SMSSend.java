package cn.wizzer.modules.controllers.open.util;

import com.beust.jcommander.internal.Maps;
import org.nutz.http.Http;

import java.util.Map;

public class SMSSend {

    public static void sendSMS(String phone,String content){
        Map<String,Object> qrmap = Maps.newHashMap();
        qrmap.put("account","losys");
        qrmap.put("password","losys123654");
        qrmap.put("mobile", phone);
        qrmap.put("content", content);
        qrmap.put("action", "send");
        Http.post("dx.ipyy.net/smsJson.aspx", qrmap, 10000);
    }
}
