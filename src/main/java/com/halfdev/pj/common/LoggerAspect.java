package com.halfdev.pj.common;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class LoggerAspect {

	static final Logger logger = LoggerFactory.getLogger(LoggerAspect.class);

	   @Before("execution(* com.springbook.board.*.submit(..))")
	   public void before(JoinPoint joinPoint) {
		   HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		   logger.info("REQUEST URI : {}", request.getRequestURI());
	   }
}
