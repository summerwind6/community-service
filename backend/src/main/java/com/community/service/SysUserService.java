package com.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.entity.dto.PageDTO;
import com.community.entity.dto.UserDTO;
import com.community.entity.po.SysUser;
import com.community.entity.vo.LoginVO;
import com.community.entity.vo.PageVO;
import com.community.entity.vo.UserVO;

public interface SysUserService extends IService<SysUser> {

    LoginVO login(String username, String password);

    void register(UserDTO userDTO, Long roleId);

    UserVO getUserInfo(Long id);

    SysUser getUserByUsername(String username);

    UserVO getUserInfoByUsername(String username);

    UserVO updateUser(UserDTO userDTO);

    PageVO<UserVO> getUserList(PageDTO pageDTO);

    void changePassword(Long userId, String oldPassword, String newPassword);

    UserVO updateProfile(Long userId, UserDTO userDTO);
}
