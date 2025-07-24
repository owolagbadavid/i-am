package me.oreos.iam.services.utils;

import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.oreos.iam.Dtos.AuthorizationRequestDto;
import me.oreos.iam.annotation.CustomAuthorize;
import me.oreos.iam.annotation.ResourceId;
import me.oreos.iam.services.AuthService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
@RequiredArgsConstructor
public class CustomAuthorizeAspect {

    private final AuthService authorizationService;
    private final HttpServletRequest request;

    // Match methods annotated with @CustomAuthorize OR methods within a class
    // annotated with @CustomAuthorize
    @Around("(execution(* me.oreos..*(..)) || execution(* org.wakanda..*(..))) && " +
            "(@annotation(me.oreos.iam.annotation.CustomAuthorize) || " +
            "@within(me.oreos.iam.annotation.CustomAuthorize) || " +
            "@target(me.oreos.iam.annotation.CustomAuthorize))")
    // @Before("@annotation(me.oreos.iam.annotation.CustomAuthorize)")
    public Object before(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = joinPoint.getTarget().getClass();

        // Try to get annotation from method first, then from class
        CustomAuthorize annotation = method.getAnnotation(CustomAuthorize.class);
        if (annotation == null) {
            annotation = targetClass.getAnnotation(CustomAuthorize.class);
        }

        if (annotation == null) {
            // Shouldn't happen due to the pointcut, but defensive programming
            return joinPoint.proceed();
        }

        // Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        // CustomAuthorize annotation = method.getAnnotation(CustomAuthorize.class);

        // if (annotation == null) {
        // Class<?> targetClass = joinPoint.getTarget().getClass();
        // annotation = targetClass.getAnnotation(CustomAuthorize.class);
        // }

        // if (annotation == null) {
        // return; // Skip if not found
        // }

        Object[] args = joinPoint.getArgs();
        int resourceId = 0; // default value if no @ResourceId param is found

        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            for (Annotation paramAnnotation : parameters[i].getAnnotations()) {
                if (paramAnnotation instanceof ResourceId) {
                    Object value = args[i];
                    try {
                        resourceId = Integer.parseInt(value.toString());
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("@ResourceId value is not a valid integer: " + value);
                    }
                    break;
                }
            }
        }

        AuthorizationRequestDto dto = new AuthorizationRequestDto();
        dto.authToken = extractTokenFromHeader();
        dto.action = annotation.action();
        dto.resourceType = annotation.resourceType();
        dto.resourceId = resourceId;

        try {

            authorizationService.authorize(dto);
        } catch (Exception e) {
            return Helper.errorHandler(e);
        }

        return joinPoint.proceed();
    }

    private String extractTokenFromHeader() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Missing or invalid Authorization header");
    }
}
