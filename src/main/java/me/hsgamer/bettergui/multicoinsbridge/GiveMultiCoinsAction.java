package me.hsgamer.bettergui.multicoinsbridge;

import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.task.element.TaskProcess;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class GiveMultiCoinsAction extends BaseAction {
    private final String holderName;

    public GiveMultiCoinsAction(ActionBuilder.Input input) {
        super(input);
        this.holderName = input.option;
    }

    @Override
    public void accept(UUID uuid, TaskProcess process) {
        String parsed = getReplacedString(uuid);
        Optional<Double> optionalAmount = Validate.getNumber(parsed).map(BigDecimal::doubleValue);
        if (!optionalAmount.isPresent()) {
            Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> player.sendMessage(ChatColor.RED + "Invalid amount: " + parsed));
            process.next();
            return;
        }
        double amount = optionalAmount.get();
        if (amount > 0) {
            Scheduler.current().sync().runTask(() -> {
                if (!MultiCoinsBridge.give(uuid, holderName, amount)) {
                    Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> player.sendMessage(ChatColor.RED + "Error: the transaction couldn't be executed. Please inform the staff."));
                }
                process.next();
            });
        } else {
            process.next();
        }
    }
}
