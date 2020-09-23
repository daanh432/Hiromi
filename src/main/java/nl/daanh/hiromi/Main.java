package nl.daanh.hiromi;

import nl.daanh.hiromi.commands.PingCommand;
import nl.daanh.hiromi.commands.annotations.CommandCategory;

public class Main {
    public static void main(String[] args) {
        PingCommand pingCommand = new PingCommand();
        CommandCategory annotation = pingCommand.getClass().getAnnotation(CommandCategory.class);
        System.out.println(annotation.value());
    }
}
