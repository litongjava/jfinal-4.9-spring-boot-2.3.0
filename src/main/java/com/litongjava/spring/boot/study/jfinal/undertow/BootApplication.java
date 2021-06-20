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
