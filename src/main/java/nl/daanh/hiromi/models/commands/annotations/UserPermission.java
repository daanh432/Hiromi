package nl.daanh.hiromi.models.commands.annotations;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(UserPermissions.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserPermission {
    Permission value();

    String errorMessage() default ("");
}
