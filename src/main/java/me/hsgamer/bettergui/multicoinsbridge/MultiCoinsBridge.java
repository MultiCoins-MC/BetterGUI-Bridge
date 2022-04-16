package me.hsgamer.bettergui.multicoinsbridge;

import me.hsgamer.multicoins.MultiCoins;
import me.hsgamer.multicoins.object.CoinEntry;
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
        return getHolder(holderName)
                .map(holder -> holder.getOrCreateEntry(uuid))
                .map(CoinEntry::getBalance);
    }

    public static boolean give(UUID uuid, String holderName, double amount) {
        return getHolder(holderName)
                .map(holder -> holder.getOrCreateEntry(uuid))
                .map(entry -> entry.giveBalance(amount))
                .orElse(false);
    }

    public static boolean has(UUID uuid, CoinHolder holder, double amount) {
        return holder.getOrCreateEntry(uuid).getBalance() >= amount;
    }

    public static boolean take(UUID uuid, CoinHolder holder, double amount) {
        return holder.getOrCreateEntry(uuid).takeBalance(amount);
    }
}
