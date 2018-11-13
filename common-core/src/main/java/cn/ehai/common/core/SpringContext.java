package cn.ehai.common.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * applicatioContext 类
 * @author 18834
 *
 */
@Configuration
public class SpringContext implements ApplicationContextAware{
	
	private static ApplicationContext  application;

	private static final Logger log = LoggerFactory.getLogger(SpringContext.class);


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.application = applicationContext;
		log.info("setApplicationContext");
	}

	public static ApplicationContext getApplicationContext() {
		return application;
	}
	
	
}
