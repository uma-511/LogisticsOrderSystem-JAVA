package cn.wizzer.modules.models.sys;

import cn.wizzer.common.base.Model;
import lombok.Getter;
import lombok.Setter;

import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by wizzer on 2016/8/11.
 */
@Table("sys_user_role")
@Getter
@Setter
public class Sys_user_role extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Comment("userId")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String userId;

    @Column
    @Comment("roleId")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String roleId;

}
