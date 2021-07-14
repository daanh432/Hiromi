package nl.daanh.hiromi.helpers;

import nl.daanh.hiromi.models.commandcontext.IBaseCommandContext;

public class MessageFormatting {
    public static String currencyFormat(IBaseCommandContext ctx, int amount) {
        return ctx.getConfiguration().getDatabaseManager().getCurrency(ctx.getGuild()).replace("%", String.valueOf(amount));
    }
}
