
/* updateSysMenu1 */
INSERT INTO `losys`.`sys_menu` (`id`, `parentId`, `path`, `name`, `aliasName`, `type`, `href`, `target`, `icon`, `isShow`, `disabled`, `permission`, `note`, `location`, `hasChildren`, `opBy`, `opAt`, `delFlag`) VALUES ('8ae0e5decd8c47ce8f885626e4636789', '', '0004', '淘宝管理', NULL, 'menu', '', '', '', '1', '0', 'taobao', NULL, '57', '1', 'cd4618704b5f4e4784295d10395ad0cb', '1520503156', '0');
/* updateSysMenu2 */
INSERT INTO `losys`.`sys_menu` (`id`, `parentId`, `path`, `name`, `aliasName`, `type`, `href`, `target`, `icon`, `isShow`, `disabled`, `permission`, `note`, `location`, `hasChildren`, `opBy`, `opAt`, `delFlag`) VALUES ('1e4415c59d79485da4d576fb0db6da3a', '8ae0e5decd8c47ce8f885626e4636789', '00040001', '淘宝管理', NULL, 'menu', '', '', '', '1', '0', 'taobao.number', NULL, '58', '1', 'cd4618704b5f4e4784295d10395ad0cb', '1520503169', '0');
/* updateSysMenu3 */
INSERT INTO `losys`.`sys_menu` (`id`, `parentId`, `path`, `name`, `aliasName`, `type`, `href`, `target`, `icon`, `isShow`, `disabled`, `permission`, `note`, `location`, `hasChildren`, `opBy`, `opAt`, `delFlag`) VALUES ('066c308370204a2c9fddf4a77618551a', '1e4415c59d79485da4d576fb0db6da3a', '000400010001', '账号管理', NULL, 'menu', '/platform/losys/taobao', '', '', '1', '0', 'taobao.number.account', NULL, '59', '0', 'cd4618704b5f4e4784295d10395ad0cb', '1520503211', '0');
/* updateSysMenu4 */
INSERT INTO `losys`.`sys_menu` (`id`, `parentId`, `path`, `name`, `aliasName`, `type`, `href`, `target`, `icon`, `isShow`, `disabled`, `permission`, `note`, `location`, `hasChildren`, `opBy`, `opAt`, `delFlag`) VALUES ('7fd50fec15084290b300d9aebfdc4c25', '1e4415c59d79485da4d576fb0db6da3a', '000400010002', '订单管理', NULL, 'menu', '/platform/losys/taobao/order', '', '', '1', '0', 'taobao.number.order', NULL, '63', '0', 'e09744c4a2fd45e1a04db0efd958f748', '1520926697', '0');

/* updateSysMenu5 */
INSERT INTO `losys`.`sys_menu` (`id`, `parentId`, `path`, `name`, `aliasName`, `type`, `href`, `target`, `icon`, `isShow`, `disabled`, `permission`, `note`, `location`, `hasChildren`, `opBy`, `opAt`, `delFlag`) VALUES ('925a7a4a99584f3d8eda3f54634c0724', '', '0005', '工厂管理', NULL, 'menu', '', '', '', '1', '0', 'factory', NULL, '60', '1', '0ae59b2184ac48dc8780936f47aeb5b8', '1520579637', '0');
/* updateSysMenu6 */
INSERT INTO `losys`.`sys_menu` (`id`, `parentId`, `path`, `name`, `aliasName`, `type`, `href`, `target`, `icon`, `isShow`, `disabled`, `permission`, `note`, `location`, `hasChildren`, `opBy`, `opAt`, `delFlag`) VALUES ('f7776e48db9443b5935d276ac30d84a7', '925a7a4a99584f3d8eda3f54634c0724', '00050001', '工厂管理', NULL, 'menu', '', '', '', '1', '0', 'factory.company', NULL, '61', '1', '0ae59b2184ac48dc8780936f47aeb5b8', '1520579696', '0');
/* updateSysMenu7 */
INSERT INTO `losys`.`sys_menu` (`id`, `parentId`, `path`, `name`, `aliasName`, `type`, `href`, `target`, `icon`, `isShow`, `disabled`, `permission`, `note`, `location`, `hasChildren`, `opBy`, `opAt`, `delFlag`) VALUES ('0dc24576cc4145b3bb3271334fe07409', 'f7776e48db9443b5935d276ac30d84a7', '000500010001', '账号管理', NULL, 'menu', '/platform/losys/factory', '', '', '1', '0', 'factory.company.number', NULL, '62', '0', '0ae59b2184ac48dc8780936f47aeb5b8', '1520579877', '0');
/* updateSysMenu8 */
INSERT INTO `losys`.`sys_menu` (`id`, `parentId`, `path`, `name`, `aliasName`, `type`, `href`, `target`, `icon`, `isShow`, `disabled`, `permission`, `note`, `location`, `hasChildren`, `opBy`, `opAt`, `delFlag`) VALUES ('ee4ca73f94f44606a1aa78b51f83d9cb', 'f7776e48db9443b5935d276ac30d84a7', '000500010002', '订单管理', NULL, 'menu', '/platform/losys/factory/order', '', '', '1', '0', 'factory.company.order', NULL, '64', '0', 'e9356d3fbb5447f6a40c40e0aa9bd5dd', '1521185587', '0');


/* insertTeamtalkMenu9 */ 
INSERT INTO sys_role_menu(roleId,menuId) SELECT (select id from sys_role where name='淘宝方') as roleid,id as menuid FROM sys_menu WHERE 
FIND_IN_SET(id, (select GROUP_CONCAT(id) as id from sys_menu where path like ('0004') or path like ('000400010002%') or path like ('00040001')))
/* insertTeamtalkMenu10 */ 
INSERT INTO sys_role_menu(roleId,menuId) SELECT (select id from sys_role where name='工厂方') as roleid,id as menuid FROM sys_menu WHERE 
FIND_IN_SET(id, (select GROUP_CONCAT(id) as id from sys_menu where path like ('0005') or path like ('000500010002%') or path like ('00050001')))