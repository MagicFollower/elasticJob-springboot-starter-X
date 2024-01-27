package org.example.job;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TestJob1 implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {
        // DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        // System.out.printf(">>>>>>>>|%s|-111-|定时任务执行中|TestJob1.doBusiness...%n", df.format(LocalDateTime.now()));
        // switch (shardingContext.getShardingItem()) {
        //     case 0:
        //         System.out.println("分片0");
        //         break;
        //     case 1:
        //         System.out.println("分片1");
        //         break;
        // }

        String shardingParameter = shardingContext.getShardingParameter();
        switch (shardingParameter) {
            case "Beijing":
                System.out.println("-Beijing...");
                break;
            case "Shanghai":
                System.out.println("--Shanghai...");
                break;
            case "Guangzhou":
                System.out.println("---Guangzhou...");
                break;
        }
    }
}
