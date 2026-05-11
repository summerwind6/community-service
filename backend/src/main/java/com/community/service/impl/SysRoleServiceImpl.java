package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.po.SysPermission;
import com.community.entity.po.SysRole;
import com.community.entity.po.SysRolePerm;
import com.community.mapper.SysPermissionMapper;
import com.community.mapper.SysRoleMapper;
import com.community.mapper.SysRolePermMapper;
import com.community.service.SysRoleService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Resource
    private SysRolePermMapper sysRolePermMapper;

    @Resource
    private SysPermissionMapper sysPermissionMapper;

    @Override
    public List<SysRole> getAllRoles() {
        return list();
    }

    @Override
    public SysRole getRoleById(Long id) {
        return getById(id);
    }

    @Override
    public SysRole addRole(SysRole role) {
        save(role);
        return role;
    }

    @Override
    public SysRole updateRole(SysRole role) {
        updateById(role);
        return role;
    }

    @Override
    public void assignPermissions(Long roleId, List<Long> permIds) {
        LambdaQueryWrapper<SysRolePerm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePerm::getRoleId, roleId);
        sysRolePermMapper.delete(wrapper);

        for (Long permId : permIds) {
            SysRolePerm rolePerm = new SysRolePerm();
            rolePerm.setRoleId(roleId);
            rolePerm.setPermId(permId);
            sysRolePermMapper.insert(rolePerm);
        }
    }

    @Override
    public List<SysPermission> getPermissionsByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRolePerm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePerm::getRoleId, roleId);
        List<SysRolePerm> rolePerms = sysRolePermMapper.selectList(wrapper);
        List<Long> permIds = rolePerms.stream().map(SysRolePerm::getPermId).toList();
        if (permIds.isEmpty()) {
            return List.of();
        }
        return sysPermissionMapper.selectBatchIds(permIds);
    }
}
