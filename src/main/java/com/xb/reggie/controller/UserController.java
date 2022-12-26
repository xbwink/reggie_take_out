package com.xb.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.xb.reggie.common.R;
import com.xb.reggie.entity.User;
import com.xb.reggie.service.UserService;
import com.xb.reggie.utils.SMSUtils;
import com.xb.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author xb
 * @create 2022-12-14 10:40
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送验证码短信
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();

        if(StringUtils.isNotEmpty(phone)){
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);

            //调用阿里云提供的短信服务API完成发送短信
            //SMSUtils.sendMessage("瑞吉外卖","SMS_264201183",phone,code);


            //将验证码保存到session
            //session.setAttribute(phone,code);

            //将生成的验证码缓存放到Redis中,并设置有效期5分钟
            redisTemplate.opsForValue().set(phone,code,5L, TimeUnit.MINUTES);

            return R.success("手机验证码短信发送成功");
        }

        return R.error("短信发送失败");
    }

    /**
     * 用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        //获取用户手机号
        String phone = map.get("phone").toString();
        //获取页面提交的验证码信息
        String code = map.get("code").toString();
        //从Redis中获取保存的验证码
        Object codeInRedis = redisTemplate.opsForValue().get(phone);

        //比对验证码是否正确
        if(code != null && code.equals(codeInRedis)){
            //查询用户是否存在,如果不存在则创建用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if(user == null){
                //创建用户
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }

            //如果然后登录成功，删除Redis中缓存的验证码
            redisTemplate.delete(phone);

            //保存用户到session
            session.setAttribute("user",user.getId());

            //将用户返回给前端页面
            return R.success(user);
        }

        return R.error("登录失败");
    }

    /**
     * 用户退出
     * @return
     */
    @PostMapping("/loginout")
    public R<String> loginOut(HttpSession httpSession){
        //清除session中的用户id
        httpSession.removeAttribute("user");
        //返回结果
        return R.success("用户退出成功");
    }

}
