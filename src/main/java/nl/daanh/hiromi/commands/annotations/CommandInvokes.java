package nl.daanh.hiromi.commands.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInvokes {
    CommandInvoke[] value();
}
