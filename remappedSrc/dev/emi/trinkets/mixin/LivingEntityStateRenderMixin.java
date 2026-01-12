package dev.emi.trinkets.mixin;

import dev.emi.trinkets.TrinketEntityRenderState;
import dev.emi.trinkets.api.SlotReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderState.class)
public class LivingEntityStateRenderMixin implements TrinketEntityRenderState {

    @Unique
    private List<Tuple<ItemStack, SlotReference>> trinketsState = new ArrayList<>();

    @Override
    public void trinkets$setState(List<Tuple<ItemStack, SlotReference>> items) {
        this.trinketsState = items;
    }

    @Override
    public List<Tuple<ItemStack, SlotReference>> trinkets$getState() {
        return this.trinketsState;
    }
}
