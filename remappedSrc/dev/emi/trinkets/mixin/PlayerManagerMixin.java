package dev.emi.trinkets.mixin;


import com.llamalad7.mixinextras.sugar.Local;
import dev.emi.trinkets.TrinketPlayerScreenHandler;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketSaveData;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.data.EntitySlotLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import dev.emi.trinkets.payload.SyncInventoryPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Syncs slot data to player's client on login
 *
 * @author C4
 */
@Mixin(PlayerList.class)
public abstract class PlayerManagerMixin {

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;initInventoryMenu()V"), method = "placeNewPlayer")
	private void onPlayerConnect(Connection connection, ServerPlayer player, CommonListenerCookie clientData, CallbackInfo ci) {
		EntitySlotLoader.SERVER.sync(player);
		((TrinketPlayerScreenHandler) player.inventoryMenu).trinkets$updateTrinketSlots(false);
		TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> {
			Map<String, TrinketSaveData.Metadata> tag = new HashMap<>();
			Set<TrinketInventory> inventoriesToSend = trinkets.getTrackingUpdates();

			for (TrinketInventory trinketInventory : inventoriesToSend) {
				tag.put(trinketInventory.getSlotType().getId(), trinketInventory.getSyncMetadata());
			}
			ServerPlayNetworking.send(player, new SyncInventoryPayload(player.getId(), Map.of(), tag));
			inventoriesToSend.clear();
		});
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;initInventoryMenu()V"), method = "respawn")
	private void onPlayerRespawn(ServerPlayer player, boolean alive, Entity.RemovalReason removalReason, CallbackInfoReturnable<ServerPlayer> cir, @Local(ordinal = 1) ServerPlayer newServerPlayer) {
		((TrinketPlayerScreenHandler) newServerPlayer.inventoryMenu).trinkets$updateTrinketSlots(true);
	}
}