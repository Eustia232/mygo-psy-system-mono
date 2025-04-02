package com.mygo.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mygo.constant.RedisConstant;
import com.mygo.domain.dto.AdminLoginDTO;
import com.mygo.domain.dto.AdminRegisterDTO;
import com.mygo.domain.dto.ResetPasswordDTO;
import com.mygo.domain.dto.UserDTO;
import com.mygo.domain.entity.Admin;
import com.mygo.domain.vo.AdminLoginVO;
import com.mygo.exception.BadRequestException;
import com.mygo.mapper.AdminMapper;
import com.mygo.result.Result;
import com.mygo.service.AdminService;
import com.mygo.utils.AdminContext;
import com.mygo.utils.JwtTool;
import com.mygo.utils.MailUtils;
import com.mygo.utils.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AdminServiceImpl implements AdminService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AdminMapper adminMapper;

    private final JwtTool jwtTool;

    private final StringRedisTemplate stringRedisTemplate;

    private final MailUtils mailUtils;

    @Autowired
    public AdminServiceImpl(AdminMapper adminMapper, JwtTool jwtTool, StringRedisTemplate stringRedisTemplate,
                            MailUtils mailUtils) {
        this.adminMapper = adminMapper;
        this.jwtTool = jwtTool;
        this.stringRedisTemplate = stringRedisTemplate;
        this.mailUtils = mailUtils;
    }

    /**
     * 根据用户名和密码登陆,如果顺利登陆,返回一个token
     *
     * @param adminLoginDTO 登陆DTO
     * @return token
     */
    @Override
    public AdminLoginVO login(AdminLoginDTO adminLoginDTO) throws JsonProcessingException {
        //1.根据用户名查找是否存在该用户
        Admin admin = adminMapper.getAdminByName(adminLoginDTO.getName());
        if (admin == null) {
            throw new BadRequestException("用户不存在");
        }
        //2.判断密码是否正确
        if (!PasswordEncoder.matches(admin.getPassword(), adminLoginDTO.getPassword())) {
            throw new BadRequestException("密码错误");
        }
        //3.生成JWT令牌
        String jwt = jwtTool.createJWT(admin.getId());
        //4.将JWT保存在redis中
        //这里不使用hash,因为要分别设置过期时间
        stringRedisTemplate.opsForValue()
                .set(RedisConstant.ADMIN_JWT_KEY + admin.getId(), "");
        //5.设置过期时间
        stringRedisTemplate.expire(
                RedisConstant.ADMIN_JWT_KEY + admin.getId(), RedisConstant.JWT_EXPIRE, RedisConstant.JWT_EXPIRE_UNIT);
        //6.返回token
        return new AdminLoginVO(jwt, admin.getRole());
    }

    @Override
    public void register(AdminRegisterDTO adminRegisterDTO) {
        adminMapper.addAdmin(adminRegisterDTO.getName(), adminRegisterDTO.getPassword(), adminRegisterDTO.getEmail());
    }

    @Override
    public String sendEmail(String name) {
        //1.通过用户名查询邮件
        String email = adminMapper.getEmailByName(name);
        if (email == null) {
            throw new BadRequestException("用户名不存在");
        }
        //2.生成6为随机数字
        String num = RandomUtil.randomNumbers(6);
        //3.发送邮件
        String subject = "验证码";
        String text = "你的验证码为：" + num;
        mailUtils.sendMail(email, subject, text);
        //4.把数据存在redis中
        stringRedisTemplate.opsForValue()
                .set(RedisConstant.VERIFY_KEY +
                        name, num, RedisConstant.VERIFY_EXPIRE, RedisConstant.VERIFY_EXPIRE_UNIT);
        //5.返回邮箱（用于前端展示）
        return email;
    }

    @Override
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        String name = resetPasswordDTO.getName();
        //1.从redis读取验证码
        String verificationCode = stringRedisTemplate.opsForValue()
                .get(RedisConstant.VERIFY_KEY + name);
        //2.判断验证码是否正确
        if (!Objects.equals(verificationCode, resetPasswordDTO.getVerificationCode())) {
            throw new BadRequestException("验证码错误");
        }
        //3.修改密码

    }

    @Override
    public Result<UserDTO> getUserInfo() {
        long userId = AdminContext.getUser();
        UserDTO userDTO = adminMapper.getUserDTOById(userId);
        return Result.success(userDTO);
    }
}
