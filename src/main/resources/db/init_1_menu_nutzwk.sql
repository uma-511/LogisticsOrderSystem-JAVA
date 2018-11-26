
/* updateSysMenu1 */
INSERT INTO `losys`.`sys_menu` (`id`, `parentId`, `path`, `name`, `aliasName`, `type`, `href`, `target`, `icon`, `isShow`, `disabled`, `permission`, `note`, `location`, `hasChildren`, `opBy`, `opAt`, `delFlag`) VALUES ('8ae0e5decd8c47ce8f885626e4636789', '', '0004', '淘宝管理', NULL, 'menu', '', '', '', '1', '0', 'taobao', NULL, '57', '1', 'cd4618704b5f4e4784295d10395ad0cb', '1520503156', '0');
/* updateSysMenu2 */
INSERT INTO `losys`.`sys_menu` (`id`, `parentId`, `path`, `name`, `aliasName`, `type`, `href`, `target`, `icon`, `isShow`, `disabled`, `permission`, `note`, `location`, `hasChildren`, `opBy`, `opAt`, `delFlag`) VALUES ('1e4415c59d79485da4d576fb0db6da3a', '8ae0e5decd8c47ce8f885626e4636789', '00040001', '淘宝管理', NULL, 'menu', '', '', '', '1', '0', 'taobao.number', NULL, '58', '1', 'cd4618704b5f4e4784295d10395ad0cb', '1520503169', '0');
/* updateSysMenu3 */
INSERT INTO `losys`.`sys_menu` (`id`, `parentId`, `path`, `name`, `aliasName`, `type`, `href`, `target`, `icon`, `isShow`, `disabled`, `permission`, `note`, `location`, `hasChildren`, `opBy`, `opAt`, `delFlag`) VALUES ('066c308370204a2c9fddf4a77618551a', '1e4415c59d79485da4d576fb0db6da3a', '000400010001', '账号管理', NULL, 'menu', '/platform/losys/taobao', '', '', '1', '0', 'taobao.number.account', NULL, '59', '0', 'cd4618704b5f4e4784295d10395ad0cb', '1520503211', '0');
/* updateSysMenu4 */
INSERT INTO `sys_menu` VALUES ('bb05021392cf40968eb6c8a7de80faca', '1e4415c59d79485da4d576fb0db6da3a', '000400010002', '数据查看', null, 'menu', '/platform/losys/factory/dataImport', '', '', '1', '0', 'see.dataImport', null, '60', '0', '36a8e470f1bb4b659eb13f9150345680', '1540802564', '0');


/* insertTeamtalkMenu9 */ 
INSERT INTO sys_role_menu(roleId,menuId) SELECT (select id from sys_role where name='淘宝方') as roleid,id as menuid FROM sys_menu WHERE 
FIND_IN_SET(id, (select GROUP_CONCAT(id) as id from sys_menu where path like ('0004') or path like ('000400010002%') or path like ('00040001')))

/* insertTeamtalkMenu10
INSERT INTO sys_role_menu(roleId,menuId) SELECT (select id from sys_role where name='工厂方') as roleid,id as menuid FROM sys_menu WHERE 
FIND_IN_SET(id, (select GROUP_CONCAT(id) as id from sys_menu where path like ('0005') or path like ('000500010002%') or path like ('00050001')))
 */ 
/* insertTeamtalkMenu11 */ 
INSERT INTO sys_role_menu(roleId,menuId) SELECT (select id from sys_role where name='淘宝方') as roleid,id as menuid FROM sys_menu WHERE 
FIND_IN_SET(id, (select GROUP_CONCAT(id) as id from sys_menu where path like ('0011') or path like ('001100010001%') or path like ('00110001')))
/* insertTeamtalkMenu12
INSERT INTO sys_role_menu(roleId,menuId) SELECT (select id from sys_role where name='工厂方') as roleid,id as menuid FROM sys_menu WHERE 
FIND_IN_SET(id, (select GROUP_CONCAT(id) as id from sys_menu where path like ('0011') or path like ('001100010001%') or path like ('00110001')))
 */ 
/* insertTeamtalkMenu13
INSERT INTO sys_role_menu(roleId,menuId) SELECT (select id from sys_role where name='物流方') as roleid,id as menuid FROM sys_menu WHERE 
FIND_IN_SET(id, (select GROUP_CONCAT(id) as id from sys_menu where path like ('0006') or path like ('000600010002%') or path like ('00060001')))
 */ 
/* insertTeamtalkMenu14 */ 
insert into sys_role_menu (`roleId`, `menuId`) VALUES ((select id from sys_role where `code` = 'dataManage'), 'bb05021392cf40968eb6c8a7de80faca');
/* insertTeamtalkMenu15 */ 
insert into sys_role_menu (`roleId`, `menuId`) VALUES ((select id from sys_role where `code` = 'dataManage'), '1e4415c59d79485da4d576fb0db6da3a');
/* insertTeamtalkMenu16 */ 
insert into sys_role_menu (`roleId`, `menuId`) VALUES ((select id from sys_role where `code` = 'dataManage'), '8ae0e5decd8c47ce8f885626e4636789');
