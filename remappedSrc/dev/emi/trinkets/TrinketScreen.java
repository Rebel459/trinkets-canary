package dev.emi.trinkets;

import dev.emi.trinkets.api.SlotGroup;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;

public interface TrinketScreen {

	public TrinketPlayerScreenHandler trinkets$getHandler();

	public Rect2i trinkets$getGroupRect(SlotGroup group);

	public Slot trinkets$getFocusedSlot();
	
	public int trinkets$getX();
	
	public int trinkets$getY();

	public default boolean trinkets$isRecipeBookOpen() {
		return false;
	}

	public default void trinkets$updateTrinketSlots() {
	}
}
