package com.garvey.garveyaicode.scheduler;

import com.garvey.garveyaicode.scheduler.job.HealthJob;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

@Configuration
public class HealthJobConfig {

    @Bean
    public JobDetailFactoryBean testJobBean(){
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(HealthJob.class);
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setName("HealthJob");
        return jobDetailFactoryBean;
    }

    @Bean
    public CronTriggerFactoryBean myCronTriggerFactoryBean() {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();

        // 关联 JobDetail（从 JobDetailFactoryBean 获取）
        factoryBean.setJobDetail(testJobBean().getObject());

        // 设置 Trigger 唯一标识
        factoryBean.setName("myFactoryBeanTrigger");
        factoryBean.setGroup("myFactoryBeanTriggerGroup");

        // 设置 Cron 表达式（每分钟执行一次）
        factoryBean.setCronExpression("0 * * * * ?");

        // 设置触发器优先级（数值越大优先级越高，默认 5）
        factoryBean.setPriority(5);

        return factoryBean;
    }
}
