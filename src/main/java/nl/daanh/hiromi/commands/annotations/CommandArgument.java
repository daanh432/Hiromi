package nl.daanh.hiromi.commands.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CommandArguments.class)
public @interface CommandArgument {
    String value();

    TYPE type() default TYPE.STRING;

    enum TYPE {
        INTEGER,
        STRING,
        MEMBER
    }
}
