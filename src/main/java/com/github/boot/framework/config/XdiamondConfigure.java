package com.github.boot.framework.config;

import io.github.xdiamond.client.XDiamondConfig;
import io.github.xdiamond.client.annotation.AllKeyListener;
import io.github.xdiamond.client.event.ConfigEvent;
import io.github.xdiamond.client.event.EventType;
import io.github.xdiamond.client.spring.XDiamondConfigFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Xdiamond Config
 *
 * @see //github.com/hengyunabc/xdiamond
 * @author chenjianhui
 * @create 2018/05/09
 **/
@Configuration
public class XdiamondConfigure {

    private static final Logger LOGGER = LoggerFactory.getLogger(XdiamondConfigure.class);

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public XDiamondConfigFactoryBean configFactoryBean(){
        String serverHost = System.getProperty("xdiamond.server.host");
        if(serverHost == null){
            throw new RuntimeException("XDiamond配置缺失");
        }
        String serverPort = System.getProperty("xdiamond.server.port");
        if(serverPort == null){
            throw new RuntimeException("XDiamond配置缺失");
        }
        String secretKey = System.getProperty("xdiamond.project.secretKey");
        if(secretKey == null){
            throw new RuntimeException("XDiamond配置缺失");
        }
        String profile = System.getProperty("xdiamond.project.profile");
        if(profile == null){
            throw new RuntimeException("XDiamond配置缺失");
        }
        String version = System.getProperty("xdiamond.project.version");
        if(version == null){
            throw new RuntimeException("XDiamond配置缺失");
        }
        String groupId = System.getProperty("xdiamond.project.groupId");
        if(groupId == null){
            throw new RuntimeException("XDiamond配置缺失");
        }
        String artifactId = System.getProperty("xdiamond.project.artifactId");
        if(artifactId == null){
            throw new RuntimeException("XDiamond配置缺失");
        }
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>> 配置环境: " + profile);
        XDiamondConfigFactoryBean configFactoryBean = new XDiamondConfigFactoryBean();
        configFactoryBean.setServerHost(serverHost);
        configFactoryBean.setServerPort(serverPort);
        configFactoryBean.setGroupId(groupId);
        configFactoryBean.setArtifactId(artifactId);
        configFactoryBean.setProfile(profile);
        configFactoryBean.setVersion(version);
        configFactoryBean.setSecretKey(secretKey);
        return configFactoryBean;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public PropertyPlaceholderConfigurer placeholderConfigurer() throws Exception {
        PropertyPlaceholderConfigurer placeholderConfigurer = new PropertyPlaceholderConfigurer();
        placeholderConfigurer.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
        placeholderConfigurer.setIgnoreResourceNotFound(true);
        XDiamondConfig config = configFactoryBean().getObject();
        placeholderConfigurer.setProperties(config.getProperties());
        return placeholderConfigurer;
    }

    @Bean
    public ConfigChangeListener configChangeListener(){
        return new ConfigChangeListener();
    }

    /**
     * 系统变量改变监听器
     * @author cjh
     * @date 2017/3/8
     */
    private static class ConfigChangeListener implements ApplicationContextAware{

        private final static Logger LOGGER = LoggerFactory.getLogger(ConfigChangeListener.class);

        private ApplicationContext applicationContext;

        /**
         * 监听系统配置变化
         * @param event
         */
        @AllKeyListener
        public void onChange(ConfigEvent event){
            if(event.getEventType() == EventType.UPDATE){
                LOGGER.info("update " + event.getKey() + "="  + event.getValue());
                applicationContext.publishEvent(new ConfigChangeEvent(event));
            }
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }

    }

    /**
     * 配置变动事件
     */
    public static class ConfigChangeEvent extends ApplicationEvent {

        private String configKey;

        private String configValue;

        public ConfigChangeEvent(ConfigEvent event) {
            super(event);
            this.configKey = event.getKey();
            this.configValue = event.getValue();
        }

        public String getConfigKey() {
            return configKey;
        }

        public void setConfigKey(String configKey) {
            this.configKey = configKey;
        }

        public String getConfigValue() {
            return configValue;
        }

        public void setConfigValue(String configValue) {
            this.configValue = configValue;
        }
    }


}
