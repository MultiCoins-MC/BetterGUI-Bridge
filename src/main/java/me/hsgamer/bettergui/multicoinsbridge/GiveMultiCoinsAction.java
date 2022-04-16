package me.hsgamer.bettergui.multicoinsbridge;

import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.lib.core.expression.ExpressionUtils;
import me.hsgamer.bettergui.lib.taskchain.TaskChain;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class GiveMultiCoinsAction extends BaseAction {
    public GiveMultiCoinsAction(String string) {
        super(string);
    }

    @Override
    public void addToTaskChain(UUID uuid, TaskChain<?> taskChain) {
        String parsed = getReplacedString(uuid);
        String[] split = parsed.split(" ", 2);
        if (split.length != 2) {
            Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> player.sendMessage(ChatColor.RED + "Error: Invalid format. Please inform the staff."));
            return;
        }
        String holderName = split[0].trim();
        String rawAmount = split[1].trim();
        double amount = 0;
        if (Validate.isValidPositiveNumber(rawAmount)) {
            amount = Double.parseDouble(rawAmount);
        } else if (ExpressionUtils.isValidExpression(rawAmount)) {
            amount = Objects.requireNonNull(ExpressionUtils.getResult(parsed)).doubleValue();
        } else {
            Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> MessageUtils.sendMessage(player, MessageConfig.INVALID_NUMBER.getValue().replace("{input}", parsed)));
        }

        if (amount > 0) {
            double finalAmount = amount;
            taskChain.sync(() -> {
                if (!MultiCoinsBridge.give(uuid, holderName, finalAmount)) {
                    Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> player.sendMessage(ChatColor.RED + "Error: the transaction couldn't be executed. Please inform the staff."));
                }
            });
        }
    }
}
