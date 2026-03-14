package me.fengming.vpdummy.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface VpComplexAnno {
    String value() default "";

    String[] labels() default {};

    VpNestedAnno nested() default @VpNestedAnno;

    VpLeafAnno[] leafArray() default {};
}
