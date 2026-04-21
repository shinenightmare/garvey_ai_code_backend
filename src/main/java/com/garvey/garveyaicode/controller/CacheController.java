package com.garvey.garveyaicode.controller;

import com.garvey.garveyaicode.common.response.BaseResponse;
import com.garvey.garveyaicode.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/cache")
public class CacheController {

    @Autowired
    private RedisUtil redisUtil;

    @PostMapping("/removeByKey")
    public void removeAll(@RequestParam long id) {
        String key = "user:" + id;
        redisUtil.del(key);
    }
}
