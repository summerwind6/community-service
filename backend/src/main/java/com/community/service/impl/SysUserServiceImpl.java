package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.common.BusinessException;
import com.community.entity.dto.PageDTO;
import com.community.entity.dto.UserDTO;
import com.community.entity.po.SysRole;
import com.community.entity.po.SysUser;
import com.community.entity.po.SysUserRole;
import com.community.entity.vo.LoginVO;
import com.community.entity.vo.PageVO;
import com.community.entity.vo.UserVO;
import com.community.mapper.SysRoleMapper;
import com.community.mapper.SysRolePermMapper;
import com.community.mapper.SysPermissionMapper;
import com.community.mapper.SysUserMapper;
import com.community.mapper.SysUserRoleMapper;
import com.community.entity.po.SysRolePerm;
import com.community.entity.po.SysPermission;
import com.community.service.SysUserService;
import com.community.shiro.PasswordUtil;
import com.community.util.JwtTokenUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRolePermMapper sysRolePermMapper;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Override
    public LoginVO login(String username, String password) {
        SysUser user = getUserByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        String hashedPassword = PasswordUtil.encrypt(password, user.getSalt());

        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, hashedPassword);
        try {
            subject.login(token);
            subject.getSession().setAttribute("username", username);
        } catch (AuthenticationException e) {
            throw new BusinessException("用户名或密码错误");
        }

        String jwt = jwtTokenUtil.generateToken(user.getId(), username);

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(jwt);
        loginVO.setUser(convertToVO(user));
        return loginVO;
    }

    @Override
    public void register(UserDTO userDTO) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, userDTO.getUsername());
        if (count(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(userDTO, user);
        String salt = PasswordUtil.generateSalt();
        user.setSalt(salt);
        user.setPassword(PasswordUtil.encrypt(userDTO.getPassword(), salt));
        user.setStatus(1);
        save(user);

        if (userDTO.getRoleId() != null) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(userDTO.getRoleId());
            sysUserRoleMapper.insert(userRole);
        }
    }

    @Override
    public UserVO getUserInfo(Long id) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(user);
    }

    @Override
    public SysUser getUserByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return getOne(wrapper);
    }

    @Override
    public UserVO updateUser(UserDTO userDTO) {
        SysUser user = getById(userDTO.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        BeanUtils.copyProperties(userDTO, user, "id", "username", "password", "salt");
        updateById(user);
        return convertToVO(user);
    }

    @Override
    public PageVO<UserVO> getUserList(PageDTO pageDTO) {
        Page<SysUser> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        if (pageDTO.getKeyword() != null && !pageDTO.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(SysUser::getUsername, pageDTO.getKeyword())
                .or().like(SysUser::getRealName, pageDTO.getKeyword()));
        }
        if (pageDTO.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, pageDTO.getStatus());
        }

        page(page, wrapper);
        PageVO<UserVO> pageVO = new PageVO<>();
        pageVO.setRecords(page.getRecords().stream().map(this::convertToVO).toList());
        pageVO.setTotal(page.getTotal());
        pageVO.setPageNum((int) page.getCurrent());
        pageVO.setPageSize((int) page.getSize());
        pageVO.setPages((int) page.getPages());

        return pageVO;
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!PasswordUtil.verify(oldPassword, user.getSalt(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(PasswordUtil.encrypt(newPassword, user.getSalt()));
        updateById(user);
    }

    @Override
    public UserVO updateProfile(Long userId, UserDTO userDTO) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        BeanUtils.copyProperties(userDTO, user, "id", "username", "password", "salt", "status");
        updateById(user);
        return convertToVO(user);
    }

    @Override
    public UserVO getUserInfoByUsername(String username) {
        SysUser user = getUserByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(user);
    }

    private UserVO convertToVO(SysUser user) {
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setPassword(null);
        vo.setSalt(null);
        vo.setRoles(Set.of());
        vo.setPermissions(Set.of());

        LambdaQueryWrapper<SysUserRole> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(SysUserRole::getUserId, user.getId());
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(urWrapper);
        if (!userRoles.isEmpty()) {
            List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).toList();
            List<SysRole> roles = sysRoleMapper.selectBatchIds(roleIds);
            if (roles != null && !roles.isEmpty()) {
                vo.setRoles(roles.stream().map(SysRole::getRoleCode).collect(Collectors.toSet()));
            }

            LambdaQueryWrapper<SysRolePerm> rpWrapper = new LambdaQueryWrapper<>();
            rpWrapper.in(SysRolePerm::getRoleId, roleIds);
            List<SysRolePerm> rolePerms = sysRolePermMapper.selectList(rpWrapper);
            if (!rolePerms.isEmpty()) {
                List<Long> permIds = rolePerms.stream().map(SysRolePerm::getPermId).distinct().toList();
                List<SysPermission> perms = sysPermissionMapper.selectBatchIds(permIds);
                if (perms != null && !perms.isEmpty()) {
                    vo.setPermissions(perms.stream()
                            .filter(p -> p.getStatus() != null && p.getStatus() == 1)
                            .map(SysPermission::getPermCode)
                            .collect(Collectors.toSet()));
                }
            }
        }
        return vo;
    }
}
