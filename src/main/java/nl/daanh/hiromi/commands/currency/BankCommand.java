package nl.daanh.hiromi.commands.currency;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import nl.daanh.hiromi.database.IDatabaseManager;
import nl.daanh.hiromi.helpers.MessageFormatting;
import nl.daanh.hiromi.models.commandcontext.ICommandContext;
import nl.daanh.hiromi.models.commandcontext.ISlashCommandContext;
import nl.daanh.hiromi.models.commands.ICommand;
import nl.daanh.hiromi.models.commands.ISlashCommand;
import nl.daanh.hiromi.models.commands.annotations.CommandCategory;
import nl.daanh.hiromi.models.commands.annotations.CommandInvoke;
import nl.daanh.hiromi.models.commands.annotations.SelfPermission;

import java.util.List;

@CommandInvoke("bank")
@CommandCategory(CommandCategory.CATEGORY.CURRENCY)
@SelfPermission(Permission.MESSAGE_WRITE)
public class BankCommand implements ICommand, ISlashCommand {
    @Override
    public void handle(ICommandContext ctx) {
        List<String> args = ctx.getArgs();
        Guild guild = ctx.getGuild();
        Member member = ctx.getMember();
        IDatabaseManager databaseManager = ctx.getConfiguration().getDatabaseManager();

        if (args.size() == 2) {
            try {
                int amount = Integer.parseInt(args.get(1));
                int bankAmount = databaseManager.getBankAmount(member);
                int cashAmount = databaseManager.getCashAmount(member);

                if (args.get(0).equalsIgnoreCase("take")) {

                    if (amount > bankAmount || amount <= 0) {
                        ctx.reply("I'm afraid that you don't have that much money stored in your bank account.").queue();
                        return;
                    }

                    bankAmount -= amount;
                    cashAmount += amount;

                    databaseManager.setBankAmount(member, bankAmount);
                    databaseManager.setCashAmount(member, cashAmount);
                    ctx.reply(String.format("Withdrawn %s from your bank account. New balance for your wallet: %s, new balance for your bank account: %s",
                            MessageFormatting.currencyFormat(ctx, amount),
                            MessageFormatting.currencyFormat(ctx, cashAmount),
                            MessageFormatting.currencyFormat(ctx, bankAmount)
                    )).queue();
                    return;
                } else if (args.get(0).equalsIgnoreCase("put")) {
                    if (amount > cashAmount || amount <= 0) {
                        ctx.reply("I'm afraid that you don't have that much money in your wallet.").queue();
                        return;
                    }

                    cashAmount -= amount;
                    bankAmount += amount;

                    databaseManager.setCashAmount(member, cashAmount);
                    databaseManager.setBankAmount(member, bankAmount);
                    ctx.reply(String.format("Deposited %s to your bank account. New balance for your bank account: %s, new balance for your wallet: %s",
                            MessageFormatting.currencyFormat(ctx, amount),
                            MessageFormatting.currencyFormat(ctx, bankAmount),
                            MessageFormatting.currencyFormat(ctx, cashAmount)
                    )).queue();
                    return;
                }

            } catch (NumberFormatException exception) {
                ctx.reply("Please specify a valid numeric amount.").queue();
                return;
            }
        }

        ctx.reply(String.format("Please specify if you want to take money from you bank account or put money in it. %sbank <take|put> <amount>", databaseManager.getPrefix(guild))).queue();
    }

    @Override
    public void handle(ISlashCommandContext ctx) {
        SlashCommandEvent event = ctx.getEvent();
        Member member = ctx.getMember();
        IDatabaseManager databaseManager = ctx.getConfiguration().getDatabaseManager();

        OptionMapping takeOption = event.getOption("take");
        OptionMapping putOption = event.getOption("put");

        try {
            int bankAmount = databaseManager.getBankAmount(member);
            int cashAmount = databaseManager.getCashAmount(member);

            if (takeOption != null) {
                int amount = Integer.parseInt(takeOption.getAsString());
                if (amount > bankAmount || amount <= 0) {
                    ctx.reply("I'm afraid that you don't have that much money stored in your bank account.").setEphemeral(true).queue();
                    return;
                }

                bankAmount -= amount;
                cashAmount += amount;

                databaseManager.setBankAmount(member, bankAmount);
                databaseManager.setCashAmount(member, cashAmount);
                ctx.reply(
                        String.format("Withdrawn %s from your bank account. New balance for your wallet: %s, new balance for your bank account: %s",
                                MessageFormatting.currencyFormat(ctx, amount),
                                MessageFormatting.currencyFormat(ctx, cashAmount),
                                MessageFormatting.currencyFormat(ctx, bankAmount)
                        )).setEphemeral(true).queue();
                return;
            } else if (putOption != null) {
                int amount = Integer.parseInt(putOption.getAsString());
                if (amount > cashAmount || amount <= 0) {
                    ctx.reply("I'm afraid that you don't have that much money in your wallet.").setEphemeral(true).queue();
                    return;
                }

                cashAmount -= amount;
                bankAmount += amount;

                databaseManager.setCashAmount(member, cashAmount);
                databaseManager.setBankAmount(member, bankAmount);
                ctx.reply(
                        String.format("Deposited %s to your bank account. New balance for your bank account: %s, new balance for your wallet: %s",
                                MessageFormatting.currencyFormat(ctx, amount),
                                MessageFormatting.currencyFormat(ctx, bankAmount),
                                MessageFormatting.currencyFormat(ctx, cashAmount)
                        )).setEphemeral(true)
                        .queue();
                return;
            }
        } catch (NumberFormatException exception) {
            ctx.reply("Please specify a valid numeric amount.").setEphemeral(true).queue();
            return;
        }

        ctx.reply("Please specify if you want to take money from you bank account or put money in it.").setEphemeral(true).queue();
    }

    @Override
    public CommandData getCommandDefinition() {
        return new CommandData("bank", "Allows you to store or take money from your bank account")
                .addOption(OptionType.INTEGER, "take", "Give a numeric value of the amount you want to take from your bank account", false)
                .addOption(OptionType.INTEGER, "put", "Give a numeric value of the amount you want to store in your bank account", false);
    }
}
