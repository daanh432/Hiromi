package nl.daanh.hiromi.commands.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(CommandInvokes.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInvoke {
    String value();
}
