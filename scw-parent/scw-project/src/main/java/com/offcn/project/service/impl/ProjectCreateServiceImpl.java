package com.offcn.project.service.impl;

import com.alibaba.fastjson.JSON;
import com.offcn.project.contants.ProjectConstant;
import com.offcn.project.service.ProjectCreateService;
import com.offcn.project.vo.req.ProjectRedisStorageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectCreateServiceImpl implements ProjectCreateService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    /*
    * 获取令牌
    * */
    public String initCreateProject(Integer memberId) {
        //创建令牌
        String tocken = UUID.randomUUID().toString().replace("-", "");
        //创建临时对象
        ProjectRedisStorageVo initVo =new ProjectRedisStorageVo();
        initVo .setMemberid(memberId);
        //临时对象转换成字符串
        String jsonString = JSON.toJSONString(initVo);
        //存入redis中
        stringRedisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX+tocken,jsonString);
       //返回令牌
        return tocken;
    }
}
