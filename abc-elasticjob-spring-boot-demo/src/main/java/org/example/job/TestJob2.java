package org.example.job;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TestJob2 implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        System.out.printf(">>>>>>>>|%s|-222-|定时任务执行中|TestJob2.doBusiness...%n", df.format(LocalDateTime.now()));
    }
}
