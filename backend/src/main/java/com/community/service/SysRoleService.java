package com.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.entity.po.SysPermission;
import com.community.entity.po.SysRole;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {

    List<SysRole> getAllRoles();

    SysRole getRoleById(Long id);

    SysRole addRole(SysRole role);

    SysRole updateRole(SysRole role);

    void assignPermissions(Long roleId, List<Long> permIds);

    List<SysPermission> getPermissionsByRoleId(Long roleId);
}
