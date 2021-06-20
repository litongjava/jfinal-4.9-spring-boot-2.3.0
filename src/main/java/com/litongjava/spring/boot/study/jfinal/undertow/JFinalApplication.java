package com.litongjava.spring.boot.study.jfinal.undertow;

import java.util.HashSet;

import org.springframework.web.SpringServletContainerInitializer;

import com.jfinal.server.undertow.UndertowServer;
import com.litongjava.spring.boot.study.jfinal.undertow.config.AppConfig;
import com.litongjava.spring.boot.study.jfinal.undertow.config.BootInitializer;

import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import io.undertow.servlet.util.ImmediateInstanceFactory;

/**
 * @author create by ping-e-lee on 2021年6月18日 下午12:37:29
 * @version 1.0
 * @desc
 */
public class JFinalApplication {
  public static void main(String[] args) {
    long start = System.currentTimeMillis();
    // 启动
    // UndertowServer.start(AppConfig.class, 80, true);
    UndertowServer.create(AppConfig.class, "undertow.properties").configWeb(builder -> {
      //添加SpringServletContainerInitializer到undertow
      SpringServletContainerInitializer initializer = new SpringServletContainerInitializer();
      InstanceFactory<SpringServletContainerInitializer> instanceFactory = new ImmediateInstanceFactory<>(initializer);
      HashSet<Class<?>> hashSet = new HashSet<Class<?>>();
      hashSet.add(BootInitializer.class);
      ServletContainerInitializerInfo sciInfo = new ServletContainerInitializerInfo(SpringServletContainerInitializer.class, instanceFactory,
          hashSet);
      builder.getDeploymentInfo().addServletContainerInitializers(sciInfo);
    }).start();

    long end = System.currentTimeMillis();
    System.out.println((end - start) + "ms");
  }
}