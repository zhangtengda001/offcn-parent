package com.offcn.user.controller;


import com.offcn.dycommon.response.AppResponse;
import com.offcn.user.component.SmsTemplate;
import com.offcn.user.pojo.TMember;
import com.offcn.user.service.UserService;
import com.offcn.user.vo.req.UserRegistVo;
import com.offcn.user.vo.resp.UserRespVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@Api(tags = "用户登录/注册模块")
@Slf4j
@RequestMapping("/user")
public class UserLoginController {

    @Autowired
    private SmsTemplate smsTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @ApiOperation("获取注册的验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phoneNo", value = "手机号", required = true)
    })//@ApiImplicitParams：描述所有参数；@ApiImplicitParam描述某个参数
    @PostMapping("/sendCode")
    public AppResponse<Object> sendCode(String phoneNo) {

        //1、生成验证码保存到服务器，准备用户提交上来进行对比
        String code = UUID.randomUUID().toString().substring(0, 4);

        //2、保存验证码和手机号的对应关系,设置验证码过期时间
        redisTemplate.opsForValue().set(phoneNo, code, 60*5, TimeUnit.MINUTES);

        //3、短信发送构造参数
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phoneNo);
        querys.put("param", "code:" + code);
        querys.put("tpl_id", "TP1711063");//短信模板

        //4、发送短信
        String sendCode = smsTemplate.sendCode(querys);

        if (sendCode.equals("") || sendCode.equals("fail")) {

            //短信失败
            return AppResponse.fail("短信发送失败");
        }
        return AppResponse.ok(sendCode);
    }


    @ApiOperation("用户注册")
    @PostMapping("/regist")
    public AppResponse<Object> regist(UserRegistVo registVo) {
        //从redis中获取验证码
        String code = (String)redisTemplate.opsForValue().get(registVo.getLoginacct());
        //判断是否不为空
        if (!StringUtils.isEmpty(code)){
            boolean b = code.equalsIgnoreCase(registVo.getCode());
            if (b){
                TMember member=new TMember();
                BeanUtils.copyProperties(registVo,member);

                try {

                    userService.registerUser(member);
                    log.debug("用户信息注册成功：{}", member.getLoginacct());
                    //保存成功后删除redis中信息
                    redisTemplate.delete(registVo.getLoginacct());
                    return AppResponse.ok("成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("用户信息注册失败：{}", member.getLoginacct());
                    return AppResponse.fail(e.getMessage());
                }
            }else{
                return AppResponse.fail("验证码有误");
            }
        }else {
            return AppResponse.fail("验证码过期，请从新获取");
        }
    }

    @ApiOperation("用户登录")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "username" ,value ="用户名" ,required = true),
            @ApiImplicitParam(name = "password",value = "密码" ,required = true)
    })
    @PostMapping("/login")
    public AppResponse<UserRespVo>  login(String username ,String password){
        TMember member = userService.login(username, password);
        if (member==null){
            AppResponse<UserRespVo> fail=AppResponse.fail(null);
            fail.setMsg("用户名或密码错误");
            return fail;
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        UserRespVo vo=new UserRespVo();
        BeanUtils.copyProperties(member,vo);

        vo.setAccessToken(token);
        redisTemplate.opsForValue().set(token,member.getId()+"",2,TimeUnit.HOURS);
        return AppResponse.ok(vo);


    }
}
