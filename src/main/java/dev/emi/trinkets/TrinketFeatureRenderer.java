package dev.emi.trinkets;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class TrinketFeatureRenderer<T extends LivingEntityRenderState, M extends EntityModel<T>> extends RenderLayer<T, M> {

	public TrinketFeatureRenderer(RenderLayerParent<T, M> context) {
		super(context);
	}

	public static void update(LivingEntity livingEntity, LivingEntityRenderState entityState, float tickDelta, TrinketEntityRenderState state) {
		Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(livingEntity);
		if (component.isEmpty()) {
			state.trinkets$setState(List.of());
		} else {
			List<Tuple<ItemStack, SlotReference>> items = new ArrayList<>();
			component.get().forEach((slotReference, stack) -> items.add(new Tuple<>(stack, slotReference)));
			state.trinkets$setState(items);
		}
	}

	@Override
	public void submit(PoseStack matrices, SubmitNodeCollector queue, int light, T state, float limbAngle, float limbDistance) {
		((TrinketEntityRenderState) state).trinkets$getState().forEach(pair -> {
			TrinketRendererRegistry.getRenderer(pair.getA().getItem()).ifPresent(renderer -> {
				matrices.pushPose();
				renderer.render(pair.getA(), pair.getB(), this.getParentModel(), matrices, queue,
						light, state, limbAngle, limbDistance);
				matrices.popPose();
			});
		});
	}
}
