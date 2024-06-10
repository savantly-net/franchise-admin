package net.savantly.nexus.flow.dom.flowNode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.core.annotation.AliasFor;

@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    String description() default "";
}
