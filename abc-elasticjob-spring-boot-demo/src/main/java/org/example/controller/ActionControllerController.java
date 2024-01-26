package org.example.controller;

import com.abc.elasticjob.helper.ZkElasticJobHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/elasticjob")
@RequiredArgsConstructor
public class ActionControllerController {

    private final ZkElasticJobHelper elasticJobHelper;

    /**
     * springboot配置文件中指定定时任务禁用，此处调用API后开启所有定时任务
     */
    @PostMapping("/startAll")
    public void startAll() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Collection<String> jobNames = elasticJobHelper.getJobNames();
        log.info(">>>>>>>>|{}", gson.toJson(jobNames));
        jobNames.forEach(elasticJobHelper::rescheduleJob);
    }

    /**
     * 暂停所有任务
     */
    @PostMapping("/stopAll")
    public void stopAll() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Collection<String> jobNames = elasticJobHelper.getJobNames();
        log.info(">>>>>>>>|{}", gson.toJson(jobNames));
        jobNames.forEach(elasticJobHelper::pauseJob);
    }

    /**
     * 使用心得Cron重启所有任务
     */
    @PostMapping("/restartWithNewCorn")
    public void restartWithNewCorn() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Collection<String> jobNames = elasticJobHelper.getJobNames();
        log.info(">>>>>>>>|{}", gson.toJson(jobNames));
        jobNames.forEach(jobName -> elasticJobHelper.rescheduleJob(jobName, "0/5 * * * * ?"));
    }
}
