jfinal-undertow整合spring-boot  

## 1.1.背景描述

[背景描述]
使用jfinal开发有一个非常大的便利性就是热加载即修改java文件后保存后即可生效,但是spring-boot却不行,能不能让spring-boot向jfinal一样修改java文件后保存后即可生效呢?

[jfinal-undertow]
jfinal-undertow使用波总对undertow容器二开的一个undertow容器,添加了一些新的功能,例如hotswap热加载
hotswap热加载代码实现地址
https://gitee.com/jfinal/jfinal-undertow/tree/master/src/main/java/com/jfinal/server/undertow/hotswap

[思路]
spring-boot默认使用的servlet容器是tomcat,但是spring-boot也支持jetty,undertow,我的思路是让spring-boot使用jfinal-undertow作为sevlet容器,jfinal-undertow具有热加载的功能

## 1.2.创建spring-boot工程

首先创建一个spring-boot工程,工程名jfinal-4.9-spring-boot-2.3.0,使用undertow作为web容器

### 1.2.1.pom.xml配置如下
```
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>2.3.0.RELEASE</version>
</parent>

<build>
  <plugins>
    <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
    </plugin>
  </plugins>
</build>
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
      <!--排除tomcat依赖 -->
      <exclusion>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-tomcat</artifactId>
      </exclusion>
    </exclusions>
  </dependency>
  <!-- 使用undertow -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-undertow</artifactId>
  </dependency>

  <dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
  </dependency>
</dependencies>

```
### 1.2.2.启动类
```
package com.litongjava.spring.boot.study.jfinal.undertow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author create by ping-e-lee on 2021年6月18日 上午9:30:32
 * @version 1.0
 * @desc
 */
 @SpringBootApplication
 public class BootApplication {
    public static void main(String[] args) {
    long start = System.currentTimeMillis();
    SpringApplication.run(BootApplication.class, args);
    long end = System.currentTimeMillis();
    System.out.println((end - start) + "ms");
    }
 }
```
### 1.2.3.controller
编写一个spring的controller

```
package com.litongjava.spring.boot.study.jfinal.undertow.contorller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author create by ping-e-lee on 2021年6月18日 上午9:31:52 
 * @version 1.0 
 * @desc
 */
  @RestController
  @RequestMapping()
  public class HelloController {
    @Autowired
    private ApplicationContext applicationContext;
    @RequestMapping
    public String hello() {
    //拼接成下面的样式返回
    StringBuffer stringBuffer = new StringBuffer();
    String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
    stringBuffer.append("@Import({");
    for (String beanDefinitionName : beanDefinitionNames) {
      if(beanDefinitionName.endsWith("AutoConfiguration")){
        stringBuffer.append(beanDefinitionName+".class,\r\n");
      }
    }
    stringBuffer.append("})");

    return stringBuffer.toString();
    }

  @RequestMapping("test")
  public String test() {
    return "Test12";
  } 
}
```



### 1.2.4.logback配置文件
添加logback.xml文件,内容如下
```
<?xml version="1.0" encoding="UTF-8" ?>
<configuration debug="false"
  xmlns="http://ch.qos.logback/xml/ns/logback" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://ch.qos.logback/xml/ns/logback https://raw.githubusercontent.com/enricopulatzo/logback-XSD/master/src/main/xsd/logback.xsd">
  <!--定义日志文件的存储地址 勿在 LogBack 的配置中使用相对路径 -->
  <property name="LOG_HOME" value="logs" />
  <!-- 控制台输出 -->
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
      <!--格式化输出：%d表示日期,%-5level：日志级别从左显示6个字符宽度,%m：日志消息，%n是换行符 -->
      <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-6level%logger{0}.%M:%L - %m%n</pattern>
    </encoder>
  </appender>

  <!-- 按照每天生成日志文件 -->
  <appender name="FILE"
    class="ch.qos.logback.core.rolling.RollingFileAppender">
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      <!--日志文件输出的文件名 -->
      <fileNamePattern>${LOG_HOME}/project-name-%d{yyyy-MM-dd}.log</fileNamePattern>
      <!--日志文件保留天数 -->
      <maxHistory>120</maxHistory>
    </rollingPolicy>
    <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
      <!--格式化输出：%d表示日期,%-6level：日志级别从左显示6个字符宽度,%m：日志消息，%n是换行符 -->
      <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-6level%logger{0}.%M:%L - %m%n</pattern>
    </encoder>
    <!--日志文件最大的大小 -->
    <triggeringPolicy
      class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
      <maxFileSize>10MB</maxFileSize>
    </triggeringPolicy>
  </appender>
  <!-- 日志输出级别 和输出源 -->
  <root level="info">
    <appender-ref ref="STDOUT" />
    <appender-ref ref="FILE" />
  </root>
</configuration>
```
### 1.2.5.启动spring-boot项目
启动项目

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.3.0.RELEASE)

2021-06-20 16:32:23.147 INFO  BootApplication.logStarting:55 - Starting BootApplication on DESKTOP-FAUAFH1 with PID 11692 (E:\dev_workspace\eclipse-jee-2019-12\jfinal-4.9-spring-boot-2.3.0\target\classes started by Administrator in E:\dev_workspace\eclipse-jee-2019-12\jfinal-4.9-spring-boot-2.3.0)
2021-06-20 16:32:23.150 INFO  BootApplication.logStartupProfileInfo:651 - No active profile set, falling back to default profiles: default
2021-06-20 16:32:24.209 WARN  jsr.handleDeployment:68 - UT026010: Buffer pool was not set on WebSocketDeploymentInfo, the default pool will be used
2021-06-20 16:32:24.231 INFO  servlet.log:364 - Initializing Spring embedded WebApplicationContext
2021-06-20 16:32:24.232 INFO  ContextLoader.prepareWebApplicationContext:284 - Root WebApplicationContext: initialization completed in 1043 ms
2021-06-20 16:32:24.371 INFO  ThreadPoolTaskExecutor.initialize:181 - Initializing ExecutorService 'applicationTaskExecutor'
2021-06-20 16:32:24.480 INFO  undertow.start:117 - starting server: Undertow - 2.1.0.Final
2021-06-20 16:32:24.489 INFO  xnio.<clinit>:95 - XNIO version 3.8.0.Final
2021-06-20 16:32:24.499 INFO  nio.<clinit>:59 - XNIO NIO Implementation Version 3.8.0.Final
2021-06-20 16:32:24.555 INFO  threads.<clinit>:52 - JBoss Threads version 3.1.0.Final
2021-06-20 16:32:24.593 INFO  UndertowWebServer.start:133 - Undertow started on port(s) 8080 (http)
2021-06-20 16:32:24.600 INFO  BootApplication.logStarted:61 - Started BootApplication in 1.719 seconds (JVM running for 2.125)
2043ms
```



访问测试

## 1.3.改造为spring项目

### 1.3.1.spring-boot-starter-undertow依赖

```
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-dependencies</artifactId>
      <version>2.3.0.RELEASE</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
<dependencies>
  <dependency>
    <groupId>io.undertow</groupId>
    <artifactId>undertow-core</artifactId>
    <scope>compile</scope>
  </dependency>
  <dependency>
    <groupId>io.undertow</groupId>
    <artifactId>undertow-servlet</artifactId>
    <scope>compile</scope>
    <exclusions>
      <exclusion>
        <artifactId>jboss-servlet-api_4.0_spec</artifactId>
        <groupId>org.jboss.spec.javax.servlet</groupId>
      </exclusion>
      <exclusion>
        <artifactId>jboss-annotations-api_1.2_spec</artifactId>
        <groupId>org.jboss.spec.javax.annotation</groupId>
      </exclusion>
    </exclusions>
  </dependency>
  <dependency>
    <groupId>io.undertow</groupId>
    <artifactId>undertow-websockets-jsr</artifactId>
    <scope>compile</scope>
    <exclusions>
      <exclusion>
        <artifactId>jboss-servlet-api_4.0_spec</artifactId>
        <groupId>org.jboss.spec.javax.servlet</groupId>
      </exclusion>
      <exclusion>
        <artifactId>jboss-annotations-api_1.2_spec</artifactId>
        <groupId>org.jboss.spec.javax.annotation</groupId>
      </exclusion>
    </exclusions>
  </dependency>
  <dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <scope>compile</scope>
  </dependency>
  <dependency>
    <groupId>org.glassfish</groupId>
    <artifactId>jakarta.el</artifactId>
    <scope>compile</scope>
  </dependency>
</dependencies>
```



### 1.3.2.添加jfinal依赖到spring-boot项目

添加jfinal依赖,jfinal-undertow依赖,和spring-boot-starter-undertow依赖到spring-boot项目
添加后的pom.xml完整依赖如下

```
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
      <!--排除tomcat依赖 -->
      <exclusion>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-tomcat</artifactId>
      </exclusion>
    </exclusions>
  </dependency>
  <!-- 使用undertow -->
  <!-- <dependency> <groupId>org.springframework.boot</groupId> <artifactId>spring-boot-starter-undertow</artifactId> </dependency> -->
  <!-- 添加jfinal依赖和spring-boot-starter-undertow依赖 -->
  <dependency>
    <groupId>com.jfinal</groupId>
    <artifactId>jfinal</artifactId>
    <version>4.9.12</version>
  </dependency>
  <dependency>
    <groupId>com.jfinal</groupId>
    <artifactId>jfinal-undertow</artifactId>
    <version>2.5</version>
  </dependency>
  <dependency>
    <groupId>io.undertow</groupId>
    <artifactId>undertow-core</artifactId>
    <scope>compile</scope>
  </dependency>
  <dependency>
    <groupId>io.undertow</groupId>
    <artifactId>undertow-servlet</artifactId>
    <scope>compile</scope>
    <exclusions>
      <exclusion>
        <artifactId>jboss-servlet-api_4.0_spec</artifactId>
        <groupId>org.jboss.spec.javax.servlet</groupId>
      </exclusion>
      <exclusion>
        <artifactId>jboss-annotations-api_1.2_spec</artifactId>
        <groupId>org.jboss.spec.javax.annotation</groupId>
      </exclusion>
    </exclusions>
  </dependency>
  <dependency>
    <groupId>io.undertow</groupId>
    <artifactId>undertow-websockets-jsr</artifactId>
    <scope>compile</scope>
    <exclusions>
      <exclusion>
        <artifactId>jboss-servlet-api_4.0_spec</artifactId>
        <groupId>org.jboss.spec.javax.servlet</groupId>
      </exclusion>
      <exclusion>
        <artifactId>jboss-annotations-api_1.2_spec</artifactId>
        <groupId>org.jboss.spec.javax.annotation</groupId>
      </exclusion>
    </exclusions>
  </dependency>
  <dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <scope>compile</scope>
  </dependency>
  <dependency>
    <groupId>org.glassfish</groupId>
    <artifactId>jakarta.el</artifactId>
    <scope>compile</scope>
  </dependency>
​```

  <dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
  </dependency>
</dependencies>
```



### 1.3.3.spring-boot的启动方式

spring-boot的启动方式总体可以分为两种
第一种
使用嵌入式servlet容器启动
第二种
石红外部servlet容器自动,主要涉及到的类有

```
javax.servlet.ServletContainerInitializer.onStartup()
org.springframework.web.SpringServletContainerInitializer.onStartup(webAppInitializerClasses.servletContext)
org.springframework.web.WebApplicationInitializer.onStartup()
org.springframework.boot.web.servlet.support.SpringBootServletInitializer.onStartup()
```



我的思路是使用第二种
先启动jfinal-undertow容器
使用ServletContainerInitializer的特性使用spring-boot

### 1.3.4.SpringMVCHandler

SpringMVCHandler的主要作用是找到spring mvc的controller
笔者通过观察jfinal源码了解到
在UndertowServer.configJFinalFilter()配置JFinalFiler拦截"/"
在JFinalFiler.doFilter中转发到ActionHanlder,ActionHanlder处理请求,但是ActionHanlder不会去查找spring-mvc的sevlet
所以我编写了SpringMVCHandler,SpringMVCHandler先查找JFinal的Action,如果找到JFinal的Action则结束方法,JFinalFiler会继续执行向下查找servlet

```
package com.litongjava.spring.boot.study.jfinal.undertow.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.Action;
import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;

import lombok.extern.slf4j.Slf4j;

public class SpringMVCHandler extends Handler {

  public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
    // 1.静态文件返回
    if (target.indexOf('.') != -1) {
      return;
    }

    String[] urlPara = { null };
    Action action = JFinal.me().getAction(target, urlPara);
    
    if (action == null) {
      return;
    }
    // 让actionHanlder执行
    next.handle(target, request, response, isHandled);

  }
}
```



JFinal配置类

```
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
```

### 1.3.5.BootInitializer

BootInitializer链接到SpringBoot的启动类

```
package com.litongjava.spring.boot.study.jfinal.undertow.config;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.litongjava.spring.boot.study.jfinal.undertow.UndertowApplication;

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
```



### 1.3.6.JFinal启动类

JFinal启动类将BootInitializer添加到undertow-server

```
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
```

### 1.3.7.undertow.properties

```
undertow.properties开启开发模式
undertow.devMode=true
```



### 1.3.8.启动测试

启动JFinalApplication

```
Starting JFinal 4.9.12 -> http://0.0.0.0:80
Info: jfinal-undertow 2.5, undertow 2.1.0.Final, jvm 1.8.0_121
2021-06-20 17:04:50.092 INFO  servlet.log:364 - 1 Spring WebApplicationInitializers detected on classpath
2021-06-20 17:04:50.174 INFO  BootInitializer.configure:17 - 启动spring-boot

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.3.0.RELEASE)

2021-06-20 17:04:50.447 INFO  BootInitializer.logStarting:55 - Starting BootInitializer on DESKTOP-FAUAFH1 with PID 4716 (E:\dev_workspace\java\java-study\java-ee-sutdy\java-ee-spring-boot-2.3.0-study\ee-spring-boot-2.3.0-jfinal-undertow\target\classes started by Administrator in E:\dev_workspace\java\java-study\java-ee-sutdy\java-ee-spring-boot-2.3.0-study\ee-spring-boot-2.3.0-jfinal-undertow)
2021-06-20 17:04:50.448 INFO  BootInitializer.logStartupProfileInfo:651 - No active profile set, falling back to default profiles: default
2021-06-20 17:04:51.049 INFO  servlet.log:364 - Initializing Spring embedded WebApplicationContext
2021-06-20 17:04:51.050 INFO  ContextLoader.prepareWebApplicationContext:284 - Root WebApplicationContext: initialization completed in 560 ms
2021-06-20 17:04:51.298 INFO  ThreadPoolTaskExecutor.initialize:181 - Initializing ExecutorService 'applicationTaskExecutor'
2021-06-20 17:04:51.414 INFO  BootInitializer.logStarted:61 - Started BootInitializer in 1.237 seconds (JVM running for 1.809)
2021-06-20 17:04:51.479 INFO  undertow.start:117 - starting server: Undertow - 2.1.0.Final
2021-06-20 17:04:51.487 INFO  xnio.<clinit>:95 - XNIO version 3.8.0.Final
2021-06-20 17:04:51.495 INFO  nio.<clinit>:59 - XNIO NIO Implementation Version 3.8.0.Final
2021-06-20 17:04:51.904 INFO  threads.<clinit>:52 - JBoss Threads version 3.1.0.Final
Starting Complete in 2.1 seconds. Welcome To The JFinal World (^_^)

2235ms
```

使用Eclipse修改spring-boot的controller

```
  @RequestMapping("test")
  public String test() {
    return "Test123";
  }
```

修改之后立即生效

````Loading changes ......
`Loading changes ......
2021-06-20 17:05:26.430 INFO  undertow.stop:252 - stopping server: Undertow - 2.1.0.Final
2021-06-20 17:05:26.447 INFO  servlet.log:364 - 1 Spring WebApplicationInitializers detected on classpath
2021-06-20 17:05:26.452 INFO  BootInitializer.configure:17 - 启动spring-boot

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.3.0.RELEASE)

2021-06-20 17:05:26.474 INFO  BootInitializer.logStarting:55 - Starting BootInitializer on DESKTOP-FAUAFH1 with PID 4716 (E:\dev_workspace\java\java-study\java-ee-sutdy\java-ee-spring-boot-2.3.0-study\ee-spring-boot-2.3.0-jfinal-undertow\target\classes started by Administrator in E:\dev_workspace\java\java-study\java-ee-sutdy\java-ee-spring-boot-2.3.0-study\ee-spring-boot-2.3.0-jfinal-undertow)
2021-06-20 17:05:26.475 INFO  BootInitializer.logStartupProfileInfo:651 - No active profile set, falling back to default profiles: default
2021-06-20 17:05:26.668 INFO  servlet.log:364 - Initializing Spring embedded WebApplicationContext
2021-06-20 17:05:26.669 INFO  ContextLoader.prepareWebApplicationContext:284 - Root WebApplicationContext: initialization completed in 192 ms
2021-06-20 17:05:26.724 INFO  ThreadPoolTaskExecutor.initialize:181 - Initializing ExecutorService 'applicationTaskExecutor'
2021-06-20 17:05:26.768 INFO  BootInitializer.logStarted:61 - Started BootInitializer in 0.315 seconds (JVM running for 37.163)
2021-06-20 17:05:26.818 INFO  undertow.start:117 - starting server: Undertow - 2.1.0.Final
Loading complete in 0.4 seconds (^_^)
```

访问测试

http://127.0.0.1/test





