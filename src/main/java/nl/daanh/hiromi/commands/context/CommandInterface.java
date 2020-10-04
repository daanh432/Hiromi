package nl.daanh.hiromi.commands.context;

import nl.daanh.hiromi.commands.annotations.SelfPermission;
import nl.daanh.hiromi.commands.annotations.UserPermission;

public interface CommandInterface {
    void handle(CommandContext ctx);

    default boolean checkPermissions(CommandContext ctx) {
        for (UserPermission annotation : this.getClass().getAnnotationsByType(UserPermission.class)) {
            if (!ctx.getMember().hasPermission(annotation.value())) {
                if (!annotation.errorMessage().equalsIgnoreCase("")) {
                    ctx.getChannel().sendMessage(annotation.errorMessage()).queue();
                    return false;
                }
                ctx.getChannel().sendMessage(String.format("You don't have the permission ``%s``", annotation.value())).queue();
                return false;
            }
        }

        for (SelfPermission annotation : this.getClass().getAnnotationsByType(SelfPermission.class)) {
            if (!ctx.getSelfMember().hasPermission(annotation.value())) {
                if (!annotation.errorMessage().equalsIgnoreCase("")) {
                    ctx.getChannel().sendMessage(annotation.errorMessage()).queue();
                    return false;
                }
                ctx.getChannel().sendMessage(String.format("Oops. It looks like I don't have the permission ``%s``\nAsk the server owner to grant me these privileges.", annotation.value())).queue();
                return false;
            }
        }

        return true;
    }
}
