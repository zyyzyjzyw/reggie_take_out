package com.tedu.java.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tedu.java.pojo.User;
import com.tedu.java.service.UserService;
import com.tedu.java.utils.R;
import com.tedu.java.utils.SMSUtils;
import com.tedu.java.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author： zyy
 * @date： 2022/12/28 16:19
 * @description： TODO
 * @version: 1.0
 * @描述：
 **/
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        // 获取手机号
        String phone = map.get("phone").toString();
        // 获取验证码
        String code = map.get("code").toString();
        // 从session中获取保存的验证码
        // 进行验证码的比对
        //String CodeSession = session.getAttribute(phone).toString();
        // 从redis中取出数据
        String CodeSession = (String )redisTemplate.opsForValue().get(phone);
        if(CodeSession!=null&&CodeSession.equals(code)){
            // 如果能够比对成功,说明登陆成功
            // 判断当前手机号对应的用户是否为新用户,如果是新用户就自动完成注册
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(lambdaQueryWrapper);
            if(user==null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            // 如果用户登录成功，则删除存储在redis中的验证码
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登陆失败");
    }

    /**
     * 发送手机验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        // 获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            // 生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code= {}",code);
            // 调用阿里云提供的短信服务API完成发送短信
            SMSUtils.sendMessage("遥遥科技","SMS_262570127",phone,code);
            // 需要将生成的验证码保存到Session中
            //session.setAttribute(phone,code);
            // 将生成的验证码保存到redis中，并且设置有效期为5分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.success("手机验证码发送成功");
        }
        return R.error("短信发送失败");
    }

    /**
     * 用户退出
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}
