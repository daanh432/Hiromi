package nl.daanh.hiromi.commands.currency;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import nl.daanh.hiromi.database.IDatabaseManager;
import nl.daanh.hiromi.models.commandcontext.ICommandContext;
import nl.daanh.hiromi.models.commandcontext.ISlashCommandContext;
import nl.daanh.hiromi.models.commands.ICommand;
import nl.daanh.hiromi.models.commands.ISlashCommand;
import nl.daanh.hiromi.models.commands.annotations.CommandCategory;
import nl.daanh.hiromi.models.commands.annotations.CommandInvoke;
import nl.daanh.hiromi.models.commands.annotations.SelfPermission;
import nl.daanh.hiromi.utils.MessageFormatting;

@CommandInvoke("bal")
@CommandInvoke("balance")
@CommandCategory(CommandCategory.CATEGORY.CURRENCY)
@SelfPermission(Permission.MESSAGE_WRITE)
public class BalanceCommand implements ICommand, ISlashCommand {
    @Override
    public void handle(ICommandContext ctx) {
        Member member = ctx.getMember();
        IDatabaseManager databaseManager = ctx.getConfiguration().getDatabaseManager();

        int bankAmount = databaseManager.getBankAmount(member);
        int cashAmount = databaseManager.getCashAmount(member);

        ctx.reply(String.format("Your current bank account balance is: ``%s`` and your current wallet balance is: ``%s``",
                MessageFormatting.currencyFormat(ctx, bankAmount),
                MessageFormatting.currencyFormat(ctx, cashAmount))
        ).queue();
    }

    @Override
    public void handle(ISlashCommandContext ctx) {
        Member member = ctx.getMember();
        IDatabaseManager databaseManager = ctx.getConfiguration().getDatabaseManager();

        int bankAmount = databaseManager.getBankAmount(member);
        int cashAmount = databaseManager.getCashAmount(member);

        ctx.reply(String.format("Your current bank account balance is: ``%s`` and your current wallet balance is: ``%s``",
                        MessageFormatting.currencyFormat(ctx, bankAmount),
                        MessageFormatting.currencyFormat(ctx, cashAmount))
                ).setEphemeral(true)
                .queue();
    }

    @Override
    public CommandData getCommandDefinition() {
        return new CommandData("balance", "Displays your current bank and wallet balance");
    }
}
