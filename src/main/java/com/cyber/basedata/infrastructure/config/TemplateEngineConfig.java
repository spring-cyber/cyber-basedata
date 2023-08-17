package com.cyber.basedata.infrastructure.config;

import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: lijianming
 * @Description:
 * @Date: Created in 18:43 2023/8/16
 * @Modified By: 模板配置
 */
@Configuration
public class TemplateEngineConfig {

    @Bean("templateEngine")
    public TemplateEngine getTemplate() {
        return TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
    }

}
