package me.hsgamer.bettergui.multicoinsbridge;

import me.hsgamer.bettergui.api.addon.BetterGUIAddon;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.lib.core.variable.VariableManager;

public final class Main extends BetterGUIAddon {
    @Override
    public void onEnable() {
        MultiCoinsBridge.setupPlugin();
        ActionBuilder.INSTANCE.register(GiveMultiCoinsAction::new, "give-multicoins", "give-mc", "give-coins");
        RequirementBuilder.INSTANCE.register(MultiCoinsRequirement::new, "multicoins", "mc", "coins");
        VariableManager.register("multicoins", (original, uuid) -> {
            if (original.length() < 2) return null;
            String currency = original.substring(2);
            return MultiCoinsBridge.get(uuid, currency).map(String::valueOf).orElse("");
        });
    }
}
