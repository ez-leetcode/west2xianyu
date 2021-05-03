package com.west2xianyu.task;


import com.west2xianyu.mapper.GoodsMapper;
import com.west2xianyu.pojo.Goods;
import com.west2xianyu.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

//定时任务，实现商品浏览量刷新，减少mysql负载
@Component
@Slf4j
public class ScheduledTasks {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private GoodsMapper goodsMapper;

    //cron表达式  每到整五分钟执行一次刷新
    @Scheduled(cron = "0 */5 * * * ?")
    public void syncPostViews(){
        //未被移除的商品才会维护
        log.info("开始同步所有商品浏览量");
        Long startTime = System.nanoTime();
        //获取全部的浏览信息
        Map<String,Integer> map = redisUtils.getAllScan();
        for(String key: map.keySet()){
            //原有浏览量
            log.info(key);
            log.info("开始遍历所有的浏览记录");
            Goods goods = goodsMapper.selectById(Long.valueOf(key));
            if(goods == null){
                //商品不存在或已被冻结
                log.info("商品不存在或已被冻结：" + Long.valueOf(key));
                //这里也要记得删掉redis数据库的数据
                redisUtils.delete("scan_" + key);
                continue;
            }
            //新增浏览量
            int inc = map.get(key);
            goods.setScanCounts(goods.getScanCounts() + inc);
            //更新商品浏览量数据
            goodsMapper.updateById(goods);
            //删除redis中对应的数据（为防止缓存雪崩，遍历删除）
            redisUtils.delete("scan_" + key);
            log.info("商品浏览量更新成功，商品：" + key + " 新增浏览量：" + inc);
        }
        Long endTime = System.nanoTime();
        log.info("本次商品访问量同步成功, 总耗时: {}", (endTime - startTime) / 1000000 + "ms");
        log.info("商品浏览量同步成功");
    }

}