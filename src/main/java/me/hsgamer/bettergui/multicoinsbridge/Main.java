package me.hsgamer.bettergui.multicoinsbridge;

import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.expansion.common.Expansion;
import me.hsgamer.hscore.variable.VariableBundle;

public final class Main implements Expansion {
    private final VariableBundle variableBundle = new VariableBundle();

    @Override
    public void onEnable() {
        MultiCoinsBridge.setupPlugin();
        ActionBuilder.INSTANCE.register(GiveMultiCoinsAction::new, "give-multicoins", "give-mc", "give-coins");
        RequirementBuilder.INSTANCE.register(MultiCoinsRequirement::new, "multicoins", "mc", "coins");
        variableBundle.register("multicoins_", StringReplacer.of((original, uuid) -> MultiCoinsBridge.get(uuid, original).map(String::valueOf).orElse("")));
    }

    @Override
    public void onDisable() {
        variableBundle.unregisterAll();
    }
}
