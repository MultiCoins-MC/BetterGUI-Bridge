package me.hsgamer.bettergui.multicoinsbridge;

import me.hsgamer.multicoins.object.CoinHolder;

public class MultiCoinsRequest {
    public final CoinHolder holder;
    public final double amount;

    public MultiCoinsRequest(CoinHolder holder, double amount) {
        this.holder = holder;
        this.amount = amount;
    }
}
