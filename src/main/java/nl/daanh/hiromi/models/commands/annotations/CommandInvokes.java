package nl.daanh.hiromi.models.commands.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInvokes {
    CommandInvoke[] value();
}
