package com.offcn.project.controller;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.project.service.ProjectCreateService;
import com.offcn.project.vo.BaseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Api(tags = "项目的基本操作（创建、保存、项目信息获取、文件上传等）")
@RequestMapping("/project")
public class ProjectCreateController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProjectCreateService projectCreateService;

    @ApiOperation("项目发起第1步-阅读同意协议")
    @PostMapping("/init")
    public AppResponse<String> init(BaseVo vo){
        //获取令牌
        String accessToken = vo.getAccessToken();
        //根据令牌获取项目Id
        String memberid = stringRedisTemplate.opsForValue().get(accessToken);
        //判断
        if (StringUtils.isEmpty(memberid)){
            return AppResponse.fail("无权限");
        }

        int parseInt = Integer.parseInt(memberid);

        String createProject = projectCreateService.initCreateProject(parseInt);

        return AppResponse.ok(createProject);
    }
}
