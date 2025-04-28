//package io.hd.springquartz.config;
//
//import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
//import org.springframework.context.annotation.Bean;
//
//
//@Configuration
//public class QuartzAutowiringConfig {
//
//    @Bean
//    public SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer(AutowiringSpringBeanJobFactory autowiringSpringBeanJobFactory) {
//        return schedulerFactoryBean -> schedulerFactoryBean.setJobFactory(autowiringSpringBeanJobFactory);
//    }
//
//    @Bean
//    public AutowiringSpringBeanJobFactory autowiringSpringBeanJobFactory() {
//        return new AutowiringSpringBeanJobFactory();
//    }
//
//}
