package me.fengming.vpdummy.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface VpLeafAnno {
    String value() default "";

    String[] tags() default {};
}
