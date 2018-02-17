package com.example.demo.commons;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface XMLProjection {
	public String CDATA() default "";
	public boolean decodeCDATA() default false; 
	/**
	 * If it exists CDATA, this value valid must be a sub-path from CDATA expression
	 * @return Projection string. Default value: / (root element)
	 */
	public String value() default "/";
}