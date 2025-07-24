package me.oreos.iam.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomAuthorize {
    String action();
    String resourceType();
}
