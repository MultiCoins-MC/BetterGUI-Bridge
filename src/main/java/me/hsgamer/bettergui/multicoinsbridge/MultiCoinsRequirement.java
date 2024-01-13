package me.hsgamer.bettergui.multicoinsbridge;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.requirement.TakableRequirement;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.Validate;
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
    private static final String HOLDER_KEY = "holder";
    private static final String AMOUNT_KEY = "amount";

    public MultiCoinsRequirement(RequirementBuilder.Input input) {
        super(input);
    }

    @Override
    protected boolean getDefaultTake() {
        return true;
    }

    @Override
    protected Object getDefaultValue() {
        return new HashMap<String, Object>() {{
            put(HOLDER_KEY, "coin");
            put(AMOUNT_KEY, 0D);
        }};
    }

    @Override
    protected MultiCoinsRequest convert(Object value, UUID uuid) {
        Optional<Player> optionalPlayer = Optional.ofNullable(Bukkit.getPlayer(uuid));
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            CoinHolder holder = Optional.ofNullable(map.get(HOLDER_KEY))
                    .map(String::valueOf)
                    .map(s -> StringReplacerApplier.replace(s, uuid, this))
                    .flatMap(MultiCoinsBridge::getHolder)
                    .orElseGet(() -> {
                        optionalPlayer.ifPresent(player -> MessageUtils.sendMessage(player, "&cThe coin holder &6" + map.get(HOLDER_KEY) + " &cis not found"));
                        return null;
                    });
            double amount = Optional.ofNullable(map.get(AMOUNT_KEY))
                    .map(String::valueOf)
                    .map(s -> StringReplacerApplier.replace(s, uuid, this))
                    .flatMap(Validate::getNumber)
                    .map(BigDecimal::doubleValue)
                    .orElseGet(() -> {
                        optionalPlayer.ifPresent(player -> MessageUtils.sendMessage(player, BetterGUI.getInstance().getMessageConfig().getInvalidNumber(String.valueOf(map.get(AMOUNT_KEY)))));
                        return 0D;
                    });
            return new MultiCoinsRequest(holder, amount);
        } else {
            String parsed = StringReplacerApplier.replace(String.valueOf(value), uuid, this);
            String[] split = parsed.split(" ", 2);
            if (split.length != 2) {
                return null;
            }
            CoinHolder holder = MultiCoinsBridge.getHolder(split[0].trim()).orElseGet(() -> {
                optionalPlayer.ifPresent(player -> MessageUtils.sendMessage(player, "&cThe coin holder &6" + split[0].trim() + " &cis not found"));
                return null;
            });
            double amount = Validate.getNumber(split[1].trim())
                    .map(BigDecimal::doubleValue)
                    .orElseGet(() -> {
                        optionalPlayer.ifPresent(player -> MessageUtils.sendMessage(player, BetterGUI.getInstance().getMessageConfig().getInvalidNumber(parsed)));
                        return 0D;
                    });
            return new MultiCoinsRequest(holder, amount);
        }
    }

    @Override
    protected Result checkConverted(UUID uuid, MultiCoinsRequest value) {
        if (
                value == null
                        || value.amount <= 0
                        || value.holder == null
                        || !MultiCoinsBridge.has(uuid, value.holder, value.amount)
        ) {
            return Result.fail();
        } else {
            return successConditional((uuid1, process) -> Scheduler.current().sync().runTask(() -> {
                if (!MultiCoinsBridge.take(uuid1, value.holder, value.amount)) {
                    Optional.ofNullable(Bukkit.getPlayer(uuid1)).ifPresent(player -> player.sendMessage(ChatColor.RED + "Error: the transaction couldn't be executed. Please inform the staff."));
                }
                process.next();
            }));
        }
    }
}
