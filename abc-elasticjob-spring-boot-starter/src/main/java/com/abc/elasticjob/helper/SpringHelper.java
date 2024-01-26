package com.abc.elasticjob.helper;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * SpringHelper
 *
 * @Description SpringHelper
 * @Author abc
 * @Date 24/01/26 16:11
 * @Version 1.0
 */
public class SpringHelper implements ApplicationContextAware {

    /**
     * 应用上下文
     */
    private static ApplicationContext APPLICATION_CONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        APPLICATION_CONTEXT = applicationContext;
    }

    /**
     * 获取指定的bean实例
     *
     * @param beanName bean名称
     * @return 对应的bean实例
     */
    public static <T> T getBean(String beanName) {
        if (APPLICATION_CONTEXT.containsBean(beanName)) {
            return (T) APPLICATION_CONTEXT.getBean(beanName);
        }
        return null;
    }

    /**
     * 获取指定的bean实例
     *
     * @param className class名称
     * @return 对应的bean实例
     */
    public static <T> T getBean(Class<T> className) {
        return APPLICATION_CONTEXT.getBean(className);
    }

    /**
     * 获取指定基类的所有子类
     *
     * @param baseType 基类型
     * @return 所有子类
     */
    public static <T> Map<String, T> getBeansByType(Class<T> baseType) {
        return APPLICATION_CONTEXT.getBeansOfType(baseType);
    }

    public static ApplicationContext getApplicationContext() {
        return APPLICATION_CONTEXT;
    }
}
