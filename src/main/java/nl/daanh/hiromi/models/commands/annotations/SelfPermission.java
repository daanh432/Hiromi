package nl.daanh.hiromi.models.commands.annotations;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(SelfPermissions.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface SelfPermission {
    Permission value();

    String errorMessage() default ("");
}
