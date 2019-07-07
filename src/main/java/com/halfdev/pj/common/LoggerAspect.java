package com.halfdev.pj.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerAspect {

	static final Logger logger = LoggerFactory.getLogger(LoggerAspect.class);

//	   @Before("execution(* com.springbook.board.*.submit(..))")
//	   public void before(JoinPoint joinPoint) {
//		   HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
//		   logger.info("REQUEST URI : {}", request.getRequestURI());
//	   }
}
