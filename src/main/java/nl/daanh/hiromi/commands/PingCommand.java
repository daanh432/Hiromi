package nl.daanh.hiromi.commands;

import nl.daanh.hiromi.commands.annotations.CommandCategory;
import nl.daanh.hiromi.commands.context.CommandContextInterface;
import nl.daanh.hiromi.commands.context.CommandInterface;

@CommandCategory(CommandCategory.CATEGORY.FUN)
public class PingCommand implements CommandInterface {
    @Override
    public void handle(CommandContextInterface ctx) {

    }
}
