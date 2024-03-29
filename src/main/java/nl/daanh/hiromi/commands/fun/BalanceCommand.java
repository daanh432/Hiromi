package nl.daanh.hiromi.commands.fun;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import nl.daanh.hiromi.database.IDatabaseManager;
import nl.daanh.hiromi.models.commandcontext.IGenericCommandContext;
import nl.daanh.hiromi.models.commands.IGenericCommand;
import nl.daanh.hiromi.models.commands.annotations.CommandCategory;
import nl.daanh.hiromi.models.commands.annotations.CommandInvoke;
import nl.daanh.hiromi.models.commands.annotations.SelfPermission;
import nl.daanh.hiromi.utils.MessageFormatting;

@CommandInvoke("bal")
@CommandInvoke("balance")
@CommandCategory(CommandCategory.CATEGORY.FUN)
@SelfPermission(Permission.MESSAGE_WRITE)
public class BalanceCommand implements IGenericCommand {
    @Override
    public void handle(IGenericCommandContext ctx) {
        Member member = ctx.getMember();
        IDatabaseManager databaseManager = ctx.getConfiguration().getDatabaseManager();

        long bankAmount = databaseManager.getBankAmount(member);
        long cashAmount = databaseManager.getCashAmount(member);

        ctx.reply(String.format("Your current bank account balance is: ``%s`` and your current wallet balance is: ``%s``",
                MessageFormatting.currencyFormat(ctx, bankAmount),
                MessageFormatting.currencyFormat(ctx, cashAmount)));
    }

    @Override
    public CommandData getCommandDefinition() {
        return new CommandData("balance", "Displays your current bank and wallet balance");
    }
}
