package nl.daanh.hiromi.models.commands;

import nl.daanh.hiromi.models.commandcontext.IBaseCommandContext;
import nl.daanh.hiromi.models.commands.annotations.SelfPermission;
import nl.daanh.hiromi.models.commands.annotations.UserPermission;

public interface IBaseCommand {
    default boolean checkPermissions(IBaseCommandContext ctx) {
        for (UserPermission annotation : this.getClass().getAnnotationsByType(UserPermission.class)) {
            if (!ctx.getMember().hasPermission(annotation.value())) {
                if (!annotation.errorMessage().equalsIgnoreCase("")) {
                    ctx.replyInstant(annotation.errorMessage());
                    return false;
                }
                ctx.replyInstant(String.format("You don't have the permission ``%s``", annotation.value()));
                return false;
            }
        }

        for (SelfPermission annotation : this.getClass().getAnnotationsByType(SelfPermission.class)) {
            if (!ctx.getSelfMember().hasPermission(annotation.value())) {
                if (!annotation.errorMessage().equalsIgnoreCase("")) {
                    ctx.replyInstant(annotation.errorMessage());
                    return false;
                }
                ctx.replyInstant(String.format("Oops. It looks like I don't have the permission ``%s``\nAsk the server owner to grant me these privileges.", annotation.value()));
                return false;
            }
        }

        return true;
    }
}
