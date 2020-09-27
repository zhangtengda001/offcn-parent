package com.offcn.project.controller;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.dycommon.util.OssTemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/project")
@Api(tags = "项目基本模块，文件上传")
public class ProjectInfoController {

    @Autowired
    private OssTemplate ossTemplate;

    @PostMapping("/upload")
    @ApiOperation("上传图片")
    public AppResponse<Map<Object,Object>> upload(@RequestParam("file") MultipartFile[] files)throws Exception{
        Map map=new HashMap();
        List list=new ArrayList();
        if (files!=null && files.length>0){
            for (MultipartFile item : files) {
            if (!item.isEmpty()){
                String upload = ossTemplate.upload(item.getInputStream(), item.getOriginalFilename());
                list.add(upload);
            }
            }
        }
       
             map.put("urls",list);
        log.debug("ossTemplate信息：{},文件上传成功访问路径{}",ossTemplate,list);
        return AppResponse.ok(map);
    }
}
