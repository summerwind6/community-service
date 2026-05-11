package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.common.BusinessException;
import com.community.entity.dto.PageDTO;
import com.community.entity.dto.UserDTO;
import com.community.entity.po.SysUser;
import com.community.entity.vo.PageVO;
import com.community.entity.vo.UserVO;
import com.community.mapper.SysUserMapper;
import com.community.service.SysUserService;
import com.community.shiro.PasswordUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public UserVO login(String username, String password) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            throw new BusinessException("用户名或密码错误");
        }

        return getUserInfoByUsername(username);
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

    private UserVO convertToVO(SysUser user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setPassword(null);
        vo.setSalt(null);
        return vo;
    }

    @Override
    public UserVO getUserInfoByUsername(String username) {
        SysUser user = getUserByUsername(username);
        return convertToVO(user);
    }
}
