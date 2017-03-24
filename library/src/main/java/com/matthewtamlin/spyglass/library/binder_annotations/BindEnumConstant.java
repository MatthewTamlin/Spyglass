package com.matthewtamlin.spyglass.library.binder_annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BindEnumConstant {
	int annotationId();

	Class<? extends Enum> enumClass();

	int ordinal();
}