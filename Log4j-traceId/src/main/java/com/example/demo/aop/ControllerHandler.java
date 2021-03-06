package com.example.demo.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.demo.exception.DemoRuntimeException;
import com.example.demo.response.ResponseCode;
import com.example.demo.response.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
@Slf4j
@Aspect
public class ControllerHandler {

    private final static String TRACE_ID = "TRACE_ID";

    @Autowired
    HttpServletRequest request;

    @Pointcut("execution(public * com.example.demo.controller..*.*(..)) ")
    public void recordLog() {

    }

    public void setTraceId(){
        if (request != null && request.getHeader("Trace-Id") != null) {
            MDC.put(TRACE_ID, request.getHeader("Trace-Id"));
        }else{
            MDC.put(TRACE_ID, UUID.randomUUID().toString());
        }
    }

    @Around("recordLog()")
    public Object record(ProceedingJoinPoint joinPoint) throws Throwable {

        setTraceId();

        String classType = joinPoint.getTarget().getClass().getName();
        Class<?> clazz = Class.forName(classType);
        String clazzSimpleName = clazz.getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] parameters = joinPoint.getArgs();
        Map<String, Object> parameterMap = new LinkedHashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            String parameterName = parameterNames[i];
            Object parameter = parameters[i];
            parameterMap.put(parameterName, parameter);
        }

        String parametersJsonString = JSON.toJSONString(parameterMap, SerializerFeature.WriteMapNullValue);
        log.info("{}#{} args:{}", clazzSimpleName, methodName, parametersJsonString);

        Object response;

        try {
            response = joinPoint.proceed(joinPoint.getArgs());
        } catch (Exception e) {
            log.error("{}#{}, exception:{}:", clazzSimpleName, methodName, e.getClass().getSimpleName(), e);

            ResponseData<Object> res = ResponseData.failure(ResponseCode.INTERNAL_ERROR);

            if (e instanceof IllegalArgumentException || e instanceof NullPointerException) {
                res.setCode(ResponseCode.BAD_REQUEST.getCode());
                res.setMessage(e.getMessage());
            }else if (e instanceof DemoRuntimeException) {
                res.setMessage(e.getMessage());
            }
            response = res;
        }

        if (response instanceof ResponseData) {
            ((ResponseData) response).setTraceId(MDC.get(TRACE_ID));
        }

        String resultJsonString = JSON.toJSONString(response, SerializerFeature.WriteMapNullValue,
                SerializerFeature.DisableCircularReferenceDetect);
        log.info("{}#{} response:{}", clazzSimpleName, methodName, resultJsonString);

        return response;
    }

}
