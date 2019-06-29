package com.tchristofferson.portablecontainers.core.tileentities;

import com.tchristofferson.portablecontainers.core.TickManager;
import com.tchristofferson.portablecontainers.events.PortableFurnaceBurnEvent;
import com.tchristofferson.portablecontainers.events.PortableFurnaceSmeltEvent;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;

import java.lang.reflect.Field;

public class EntityFurnace extends TileEntityFurnaceFurnace {

    final TickManager tickManager;

    public EntityFurnace(TickManager tickManager) {
        super();
        this.tickManager = tickManager;
    }

    @Override
    public void tick() {
        if (isBurning()) this.burnTime--;
        World world = this.getWorld();

        ItemStack itemStack1 = this.getItem(1);

        if (isBurning() || !this.getItem(0).isEmpty() && !itemStack1.isEmpty()) {
            IRecipe recipe = world.getCraftingManager().craft(Recipes.SMELTING, this, world).orElse(null);

            if (!isBurning() && canBurn(recipe)) {
                CraftItemStack fuel = CraftItemStack.asCraftMirror(itemStack1);
                PortableFurnaceBurnEvent furnaceBurnEvent = new PortableFurnaceBurnEvent(fuel, fuelTime(itemStack1));
                Bukkit.getPluginManager().callEvent(furnaceBurnEvent);

                if (furnaceBurnEvent.isCancelled()) return;

                this.burnTime = furnaceBurnEvent.getBurnTime();

                Field ticksForCurrentFuelField = null;
                try {
                    ticksForCurrentFuelField = TileEntityFurnace.class.getDeclaredField("ticksForCurrentFuel");
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }

                ticksForCurrentFuelField.setAccessible(true);

                try {
                    ticksForCurrentFuelField.set(this, this.burnTime);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                if (isBurning() && furnaceBurnEvent.isBurning()) {
                    if (!itemStack1.isEmpty()) {
                        Item item = itemStack1.getItem();
                        itemStack1.subtract(1);

                        if (itemStack1.isEmpty()) {
                            Item item1 = item.n();
                            items.set(1, item1 == null ? ItemStack.a : new ItemStack(item1));
                        }
                    }
                }
            }

            if (isBurning() && canBurn(recipe)) {
                this.cookTime++;

                if (this.cookTime == this.cookTimeTotal) {
                    this.cookTime = 0;
                    this.cookTimeTotal = world.getCraftingManager().craft(Recipes.SMELTING, this, world).map(RecipeCooking::e).orElse(200);
                    burn(recipe, items);
                }
            } else {
                this.cookTime = 0;
            }
        } else if (!isBurning() && this.cookTime > 0) {
            this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
        }

        if (this.getViewers().isEmpty() && !isBurning() && (this.getItem(0) == ItemStack.a || this.getItem(0).isEmpty()) &&
                (this.getItem(1) == ItemStack.a || this.getItem(1).isEmpty()) && (this.getItem(2) == ItemStack.a || this.getItem(2).isEmpty())) {
            setTileEntityInTickerNull();
        }
    }

    protected void setTileEntityInTickerNull() {
        tickManager.setEntityFurnace(null);
    }

    private boolean isBurning() {
        return this.burnTime > 0;
    }

    private void burn(IRecipe<?> irecipe, NonNullList<ItemStack> items) {
        if (irecipe != null && this.canBurn(irecipe)) {
            ItemStack itemstack = this.getItem(0);
            ItemStack itemstack1 = irecipe.c();
            ItemStack itemstack2 = this.getItem(2);
            CraftItemStack source = CraftItemStack.asCraftMirror(itemstack);
            org.bukkit.inventory.ItemStack result = CraftItemStack.asBukkitCopy(itemstack1);
            PortableFurnaceSmeltEvent furnaceSmeltEvent = new PortableFurnaceSmeltEvent(source, result);
            Bukkit.getPluginManager().callEvent(furnaceSmeltEvent);
            if (furnaceSmeltEvent.isCancelled()) {
                return;
            }

            result = furnaceSmeltEvent.getResult();
            itemstack1 = CraftItemStack.asNMSCopy(result);
            if (!itemstack1.isEmpty()) {
                if (itemstack2.isEmpty()) {
                    items.set(2, itemstack1.cloneItemStack());
                } else {
                    if (!CraftItemStack.asCraftMirror(itemstack2).isSimilar(result)) {
                        return;
                    }

                    itemstack2.add(itemstack1.getCount());
                }
            }

            if (!this.getWorld().isClientSide) {
                this.a(irecipe);
            }

            if (itemstack.getItem() == Blocks.WET_SPONGE.getItem() && !(this.getItem(1)).isEmpty() && (this.getItem(1)).getItem() == Items.BUCKET) {
                items.set(1, new ItemStack(Items.WATER_BUCKET));
            }

            itemstack.subtract(1);
        }

    }

}
