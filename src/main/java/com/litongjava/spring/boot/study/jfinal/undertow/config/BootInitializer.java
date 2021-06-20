package com.litongjava.spring.boot.study.jfinal.undertow.config;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.litongjava.spring.boot.study.jfinal.undertow.BootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BootInitializer extends SpringBootServletInitializer {

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    // 传入SpringBoot应用的主程序
    // 测试回调
    log.info("启动spring-boot");
    return builder.sources(BootApplication.class);
  }
}