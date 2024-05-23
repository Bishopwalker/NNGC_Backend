package com.northernneckgarbage.nngc;
import com.northernneckgarbage.nngc.entity.HelloJob;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.quartz.JobBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {
    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(HelloJob.class)
                .withIdentity("helloJob", "group1")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger trigger(JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("helloTrigger", "group1")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(45)
                        .repeatForever())
                .build();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactory(Trigger trigger, JobDetail jobDetail) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setJobDetails(jobDetail);
        schedulerFactory.setTriggers(trigger);
        return schedulerFactory;
    }
}
