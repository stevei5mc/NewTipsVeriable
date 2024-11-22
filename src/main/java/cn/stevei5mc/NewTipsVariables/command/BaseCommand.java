package cn.stevei5mc.NewTipsVariables.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.stevei5mc.NewTipsVariables.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author SmallasWater
 */
public abstract class BaseCommand extends Command {

    private final ArrayList<BaseSubCommand> subCommand = new ArrayList<>();
    private final ConcurrentHashMap<String, Integer> subCommands = new ConcurrentHashMap<>();
    protected final Main main = Main.getInstance();

    public BaseCommand(String name, String description) {
        super(name.toLowerCase(), description);
    }

    /**
     * 判断权限
     * @param sender 玩家
     * @return 是否拥有权限
     */
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(this.getPermission());
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(this.hasPermission(sender)) {
            if(args.length > 0) {
                String subCommand = args[0].toLowerCase();
                if (subCommands.containsKey(subCommand)) {
                    BaseSubCommand command = this.subCommand.get(this.subCommands.get(subCommand));
                    if (command.canUser(sender)) {
                        return command.execute(sender, s, args);
                    }else if (sender.isPlayer()) {
                        sender.sendMessage("§c你没有权限使用此命令！");
                    }else {
                        sender.sendMessage("§c请在游戏内使用此命令！");
                    }
                }else {
                    this.sendHelp(sender);
                }
            }else {
                this.sendHelp(sender);
            }
            return true;
        }else {
            sender.sendMessage("§c你没有权限使用此命令！");
        }
        return true;
    }

    /**
     * 发送帮助
     * @param sender 玩家
     * */
    public abstract void sendHelp(CommandSender sender);

    protected void addSubCommand(BaseSubCommand cmd) {
        this.subCommand.add(cmd);
        int commandId = (this.subCommand.size()) - 1;
        this.subCommands.put(cmd.getName().toLowerCase(), commandId);
        for (String alias : cmd.getAliases()) {
            this.subCommands.put(alias.toLowerCase(), commandId);
        }
    }

    protected void loadCommandBase() {
        this.commandParameters.clear();
        for(BaseSubCommand subCommand : this.subCommand) {
            LinkedList<CommandParameter> parameters = new LinkedList<>();
            parameters.add(CommandParameter.newEnum(subCommand.getName(), new String[]{subCommand.getName()}));
            parameters.addAll(Arrays.asList(subCommand.getParameters()));
            this.commandParameters.put(subCommand.getName(), parameters.toArray(new CommandParameter[0]));
        }
    }
}