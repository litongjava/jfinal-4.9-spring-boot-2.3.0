package com.litongjava.spring.boot.study.jfinal.undertow.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.Action;
import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;

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