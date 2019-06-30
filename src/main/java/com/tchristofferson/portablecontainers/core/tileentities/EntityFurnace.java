package com.tchristofferson.portablecontainers.core.tileentities;

import com.tchristofferson.portablecontainers.core.TickManager;
import com.tchristofferson.portablecontainers.events.PortableFurnaceBurnEvent;
import com.tchristofferson.portablecontainers.events.PortableFurnaceSmeltEvent;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.event.inventory.FurnaceBurnEvent;

public class EntityFurnace extends TileEntityFurnace {

    final TickManager tickManager;

    public EntityFurnace(TickManager tickManager) {
        super(TileEntityTypes.FURNACE, Recipes.SMELTING);
        this.tickManager = tickManager;
    }

    protected EntityFurnace(TickManager tickManager, TileEntityTypes<?> tileentitytypes, Recipes<? extends RecipeCooking> recipes) {
        super(tileentitytypes, recipes);
        this.tickManager = tickManager;
    }

    @Override
    public void tick() {
        if (this.isBurning()) {
            --this.burnTime;
        }

        if (!this.world.isClientSide) {
            ItemStack itemstack = this.items.get(1);
            if (this.isBurning() || !itemstack.isEmpty() && !this.items.get(0).isEmpty()) {
                IRecipe irecipe = this.world.getCraftingManager().craft(this.c, this, this.world).orElse(null);
                if (!this.isBurning() && this.canBurn(irecipe)) {
                    CraftItemStack fuel = CraftItemStack.asCraftMirror(itemstack);
                    FurnaceBurnEvent furnaceBurnEvent = new FurnaceBurnEvent(CraftBlock.at(this.world, this.position), fuel, this.fuelTime(itemstack));
                    this.world.getServer().getPluginManager().callEvent(furnaceBurnEvent);
                    if (furnaceBurnEvent.isCancelled()) return;

                    this.burnTime = furnaceBurnEvent.getBurnTime();
                    this.b.setProperty(1, this.burnTime);
                    if (this.isBurning() && furnaceBurnEvent.isBurning()) {
                        if (!itemstack.isEmpty()) {
                            Item item = itemstack.getItem();
                            itemstack.subtract(1);
                            if (itemstack.isEmpty()) {
                                Item item1 = item.n();
                                this.items.set(1, item1 == null ? ItemStack.a : new ItemStack(item1));
                            }
                        }
                    }
                }

                if (this.isBurning() && this.canBurn(irecipe)) {
                    ++this.cookTime;
                    if (this.cookTime == this.cookTimeTotal) {
                        this.cookTime = 0;
                        this.cookTimeTotal = this.getRecipeCookingTime();
                        this.burn(irecipe);
                    }
                } else {
                    this.cookTime = 0;
                }
            } else if (!this.isBurning() && this.cookTime > 0) {
                this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
            }
        }

        if (this.getViewers().isEmpty() && !isBurning() && (this.getItem(0) == ItemStack.a || this.getItem(0).isEmpty()) &&
                (this.getItem(1) == ItemStack.a || this.getItem(1).isEmpty()) && (this.getItem(2) == ItemStack.a || this.getItem(2).isEmpty())) {
            setTileEntityInTickerNull();
        }
    }

    protected void setTileEntityInTickerNull() {
        tickManager.setEntityFurnace(null);
        System.out.println("SET TICK MANAGER FURNACE TO NULL");
    }

    private boolean isBurning() {
        return this.burnTime > 0;
    }

    private void burn(IRecipe<?> irecipe) {
        if (irecipe != null && this.canBurn(irecipe)) {
            ItemStack itemstack = this.items.get(0);
            ItemStack itemstack1 = irecipe.c();
            ItemStack itemstack2 = this.items.get(2);
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
                    this.items.set(2, itemstack1.cloneItemStack());
                } else {
                    if (!CraftItemStack.asCraftMirror(itemstack2).isSimilar(result)) {
                        return;
                    }

                    itemstack2.add(itemstack1.getCount());
                }
            }

            if (!this.world.isClientSide) {
                this.a(irecipe);
            }

            if (itemstack.getItem() == Blocks.WET_SPONGE.getItem() && !this.items.get(1).isEmpty() && this.items.get(1).getItem() == Items.BUCKET) {
                this.items.set(1, new ItemStack(Items.WATER_BUCKET));
            }

            itemstack.subtract(1);
        }

    }

    @Override
    protected IChatBaseComponent getContainerName() {
        return new ChatMessage("container.furnace");
    }

    @Override
    protected Container createContainer(int var0, PlayerInventory var1) {
        return new ContainerFurnaceFurnace(var0, var1, this, this.b);
    }
}
