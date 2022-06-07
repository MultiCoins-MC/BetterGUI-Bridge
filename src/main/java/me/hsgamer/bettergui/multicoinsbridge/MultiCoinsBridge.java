package me.hsgamer.bettergui.multicoinsbridge;

import me.hsgamer.multicoins.MultiCoins;
import me.hsgamer.multicoins.object.CoinHolder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.UUID;

public final class MultiCoinsBridge {
    private static MultiCoins multiCoins;

    private MultiCoinsBridge() {
        // EMPTY
    }

    public static void setupPlugin() {
        multiCoins = JavaPlugin.getPlugin(MultiCoins.class);
    }

    public static Optional<CoinHolder> getHolder(String holderName) {
        return multiCoins.getCoinManager().getHolder(holderName);
    }

    public static Optional<Double> get(UUID uuid, String holderName) {
        return getHolder(holderName).map(holder -> holder.getBalance(uuid));
    }

    public static boolean give(UUID uuid, String holderName, double amount) {
        return getHolder(holderName)
                .map(holder -> holder.giveBalance(uuid, amount))
                .orElse(false);
    }

    public static boolean has(UUID uuid, CoinHolder holder, double amount) {
        return holder.getBalance(uuid) >= amount;
    }

    public static boolean take(UUID uuid, CoinHolder holder, double amount) {
        return holder.takeBalance(uuid, amount);
    }
}
