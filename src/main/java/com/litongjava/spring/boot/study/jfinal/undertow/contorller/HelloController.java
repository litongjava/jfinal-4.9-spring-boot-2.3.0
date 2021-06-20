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
    /*
    @Import({
      AopAutoConfiguration.class,

    })
     */
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
    return "Test123";
  }
  
}
