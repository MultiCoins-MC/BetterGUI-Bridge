package me.hsgamer.bettergui.multicoinsbridge;

import me.hsgamer.bettergui.api.requirement.TakableRequirement;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.lib.core.expression.ExpressionUtils;
import me.hsgamer.bettergui.lib.core.variable.VariableManager;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.multicoins.object.CoinHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MultiCoinsRequirement extends TakableRequirement<MultiCoinsRequest> {
    private final Map<UUID, MultiCoinsRequest> checked = new HashMap<>();

    public MultiCoinsRequirement(String name) {
        super(name);
    }

    @Override
    protected boolean getDefaultTake() {
        return true;
    }

    @Override
    protected Object getDefaultValue() {
        return "coin 0";
    }

    @Override
    protected void takeChecked(UUID uuid) {
        MultiCoinsRequest request = checked.remove(uuid);
        if (!MultiCoinsBridge.take(uuid, request.holder, request.amount)) {
            Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> player.sendMessage(ChatColor.RED + "Error: the transaction couldn't be executed. Please inform the staff."));
        }
    }

    @Override
    public MultiCoinsRequest getParsedValue(UUID uuid) {
        String parsed = VariableManager.setVariables(String.valueOf(value).trim(), uuid);
        String[] split = parsed.split(" ", 2);
        if (split.length != 2) {
            return null;
        }
        Optional<Player> optional = Optional.ofNullable(Bukkit.getPlayer(uuid));
        double amount = Optional.ofNullable(ExpressionUtils.getResult(parsed)).map(BigDecimal::doubleValue).orElseGet(() -> {
            optional.ifPresent(player -> MessageUtils.sendMessage(player, MessageConfig.INVALID_NUMBER.getValue().replace("{input}", parsed)));
            return 0D;
        });
        CoinHolder holder = MultiCoinsBridge.getHolder(split[0].trim()).orElseGet(() -> {
            optional.ifPresent(player -> MessageUtils.sendMessage(player, "&cThe coin holder &6" + split[0].trim() + " &cis not found"));
            return null;
        });
        return new MultiCoinsRequest(holder, amount);
    }

    @Override
    public boolean check(UUID uuid) {
        MultiCoinsRequest request = getParsedValue(uuid);
        if (request.amount > 0 && request.holder != null && !MultiCoinsBridge.has(uuid, request.holder, request.amount)) {
            return false;
        } else {
            checked.put(uuid, request);
            return true;
        }
    }
}
