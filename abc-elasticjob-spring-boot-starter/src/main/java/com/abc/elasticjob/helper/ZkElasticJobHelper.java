package com.abc.elasticjob.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ElasticJob;
import org.apache.shardingsphere.elasticjob.lite.internal.schedule.JobScheduleController;
import org.apache.shardingsphere.elasticjob.lite.internal.schedule.JobScheduler;
import org.apache.shardingsphere.elasticjob.lite.spring.boot.job.ElasticJobConfigurationProperties;
import org.apache.shardingsphere.elasticjob.lite.spring.boot.job.ElasticJobProperties;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;

import javax.annotation.PostConstruct;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Map;

/**
 * ElasticJobHelper
 * <pre>
 * 1.功能介绍：
 *   1.1 获取注册的所有定时任务名称
 *   1.2 检测任务是否存在
 *   1.3 单次触发
 *   1.2 启停
 * 2.配置示例：
 * {@code
 * elasticjob:
 *   jobs:
 *     TestJob1:
 *       description: 测试任务1
 *       # https://cron.qqe2.com/
 *       cron: 0/1 * * * * ?
 *       elasticJobClass: org.example.job.TestJob1
 *       # 分片，一般情况下设置为1
 *       shardingTotalCount: 1
 *       # 失效转移
 *       failover: true
 *       # 错过重新执行
 *       misfire: true
 *       # 是否覆盖已存在作业
 *       overwrite: true
 *       # 禁止启动(true禁止，false开启)
 *       disabled: true
 *     TestJob2:
 *       description: 测试任务2
 *       # https://cron.qqe2.com/
 *       cron: 0/5 * * * * ?
 *       elasticJobClass: org.example.job.TestJob2
 *       # 分片，一般情况下设置为1
 *       shardingTotalCount: 1
 *       # 失效转移
 *       failover: true
 *       # 错过重新执行
 *       misfire: true
 *       # 是否覆盖已存在作业
 *       overwrite: true
 *       # 禁止启动(true禁止，false开启)
 *       disabled: true
 *   regCenter:
 *     namespace: XX-JOB
 *     serverLists: localhost:2181
 *     max-retries: 3
 *     base-sleep-time-milliseconds: 1000
 *     max-sleep-time-milliseconds: 3000
 *     session-timeout-milliseconds: 6000
 *     connection-timeout-milliseconds: 6000
 * }
 * </pre>
 *
 * @Description ElasticJobHelper
 * @Author abc
 * @Date 24/01/26 14:41
 * @Version 1.0
 */
@Slf4j
@RequiredArgsConstructor
public class ZkElasticJobHelper {
    private final ZookeeperRegistryCenter zookeeperRegistryCenter;
    private final ElasticJobProperties elasticJobProperties;
    private Map<String, ElasticJobConfigurationProperties> jobNameToJobConfigurationPropertiesMap;

    @PostConstruct
    public void init() {
        jobNameToJobConfigurationPropertiesMap = elasticJobProperties.getJobs();
    }

    /**
     * 根据定时任务名称检测定时任务是否注册
     *
     * @return 已注册的所有定时任务名
     */
    public boolean checkExists(String jobName) {
        return jobNameToJobConfigurationPropertiesMap.containsKey(jobName);
    }

    /**
     * 获取已注册的所有定时任务名
     *
     * @return 已注册的所有定时任务名
     */
    public Collection<String> getJobNames() {
        return jobNameToJobConfigurationPropertiesMap.keySet();
    }

    /**
     * 出发执行一次
     *
     * @param jobName 定时任务名称
     * @throws RuntimeException RuntimeException
     */
    public void triggerJob(String jobName) throws RuntimeException {
        if (!checkExists(jobName)) {
            throw new RuntimeException(String.format("定时任务(%s)不存在!", jobName));
        }
        ElasticJobConfigurationProperties jobConfigurationProperties = jobNameToJobConfigurationPropertiesMap.get(jobName);
        jobConfigurationProperties.setDisabled(true);
        getJobScheduleController(jobName, jobConfigurationProperties).triggerJob();
        log.info(">>>>>>>>|定时任务{}|已触发!", jobName);
    }

    public void pauseJob(String jobName) {
        if (!checkExists(jobName)) {
            throw new RuntimeException(String.format("定时任务(%s)不存在!", jobName));
        }
        ElasticJobConfigurationProperties jobConfigurationProperties = elasticJobProperties.getJobs().get(jobName);
        getJobScheduleController(jobName, jobConfigurationProperties).pauseJob();
        log.info(">>>>>>>>|定时任务{}|已暂停!", jobName);
    }

    /**
     * 重启定时任务
     *
     * @param jobName 定时任务名称
     * @throws RuntimeException RuntimeException
     */
    public void rescheduleJob(String jobName) throws RuntimeException {
        rescheduleJob(jobName, null, ZoneId.systemDefault());
    }

    /**
     * 重启定时任务(重新设置CRON)
     *
     * @param jobName 定时任务名称
     * @throws RuntimeException RuntimeException
     */
    public void rescheduleJob(String jobName, String newCron) throws RuntimeException {
        rescheduleJob(jobName, newCron, ZoneId.systemDefault());
    }

    /**
     * 重启定时任务(重新设置CRON)
     * <pre>
     * 1.当前方法不会对CRON进行额外校验
     * </pre>
     *
     * @param jobName 定时任务名称
     * @throws RuntimeException RuntimeException
     */
    public void rescheduleJob(String jobName, String newCron, ZoneId zoneId) throws RuntimeException {
        if (newCron != null && !newCron.isEmpty()) {
            log.info(">>>>>>>>|定时任务:{},新CRON:{}|开始重启...", jobName, newCron);
        } else {
            log.info(">>>>>>>>|定时任务:{}|开始重启...", jobName);
        }
        if (!checkExists(jobName)) {
            throw new RuntimeException(String.format("定时任务(%s)不存在!", jobName));
        }
        ElasticJobConfigurationProperties jobConfigurationProperties = elasticJobProperties.getJobs().get(jobName);
        jobConfigurationProperties.setDisabled(false);
        if (newCron != null && !newCron.isEmpty()) {
            jobConfigurationProperties.setCron(newCron);
        }
        getJobScheduleController(jobName, jobConfigurationProperties).resumeJob();
        if (newCron != null && !newCron.isEmpty()) {
            log.info(">>>>>>>>|定时任务:{},新CRON:{}|重启完成!", jobName, newCron);
        } else {
            log.info(">>>>>>>>|定时任务:{}|重启完成!", jobName);
        }
    }

    /**
     * 获取指定任务的调度控制器
     *
     * @param jobName                    任务名
     * @param jobConfigurationProperties ElasticJobConfigurationProperties实例
     * @return JobScheduleController
     */
    private JobScheduleController getJobScheduleController(String jobName, ElasticJobConfigurationProperties jobConfigurationProperties) {
        ElasticJob currentJob = SpringHelper.getBean(jobConfigurationProperties.getElasticJobClass());
        JobScheduler jobScheduler = new JobScheduler(zookeeperRegistryCenter, currentJob, jobConfigurationProperties.toJobConfiguration(jobName));
        return jobScheduler.getJobScheduleController();
    }

}
