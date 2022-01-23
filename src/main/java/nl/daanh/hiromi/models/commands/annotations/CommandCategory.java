package nl.daanh.hiromi.models.commands.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandCategory {
    CATEGORY value();

    enum CATEGORY {
        OTHER(1),
        FUN(2),
        MUSIC(4),
        MODERATION(8),
        PERSONALITY(16),
        LEVELING(32),
        EMOJI(64);

        private final int mask;

        CATEGORY(int mask) {
            this.mask = mask;
        }

        public int getMask() {
            return mask;
        }
    }
}
