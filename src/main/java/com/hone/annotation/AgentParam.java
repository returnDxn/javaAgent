package com.hone.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author H-one
 * @Date 2024/5/30 17:32
 * @Version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(ElementType.PARAMETER)
public @interface AgentParam {
    String value();

}
