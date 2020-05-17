package de.thedodo24.commonPackage.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleSettings {

    String name() default "module";
    String commandGroup() default "adventuria-module";

}

