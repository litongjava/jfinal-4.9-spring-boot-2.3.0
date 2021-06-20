package com.litongjava.spring.boot.study.jfinal.undertow.config;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.template.Engine;

/**
 * @author create by ping-e-lee on 2021年6月18日 下午12:37:49 
 * @version 1.0 
 * @desc
 */
public class AppConfig extends JFinalConfig {

  public void configConstant(Constants me) {
  }

  public void configRoute(Routes me) {
  }

  public void configEngine(Engine me) {
  }

  public void configPlugin(Plugins me) {
  }

  public void configInterceptor(Interceptors me) {
  }

  public void configHandler(Handlers me) {
    me.add(new SpringMVCHandler());
  }
}