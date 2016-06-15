package com.github.uliss3s.strutsannotationplugin.annotations;

import com.github.uliss3s.strutsannotationplugin.parameters.ActionScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {
	String name() default "";
	ActionScope scope();
	String parameter() default "";
	String path();
	Forward[] forwards() default {};
}