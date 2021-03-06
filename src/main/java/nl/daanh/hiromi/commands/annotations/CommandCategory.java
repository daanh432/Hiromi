package nl.daanh.hiromi.commands.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandCategory {
    CATEGORY value();

    enum CATEGORY {
        FUN,
        MUSIC,
        MODERATION,
        OTHER
    }
}
