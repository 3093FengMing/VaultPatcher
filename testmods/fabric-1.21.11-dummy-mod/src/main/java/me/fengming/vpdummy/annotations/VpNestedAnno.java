package me.fengming.vpdummy.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface VpNestedAnno {
    String value() default "";

    String[] names() default {};

    VpLeafAnno child() default @VpLeafAnno;
}
