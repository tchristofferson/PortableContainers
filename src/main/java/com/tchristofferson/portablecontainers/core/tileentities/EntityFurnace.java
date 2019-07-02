package com.tchristofferson.portablecontainers.core.tileentities;

import com.tchristofferson.portablecontainers.core.ContainerInfo;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;

public class EntityFurnace extends TileEntityFurnace implements IEntityContainer {

    final ContainerInfo containerInfo;

    public EntityFurnace(ContainerInfo containerInfo) {
        super(TileEntityTypes.FURNACE, Recipes.SMELTING);
        this.containerInfo = containerInfo;
    }

    protected EntityFurnace(ContainerInfo containerInfo, TileEntityTypes<?> tileentitytypes, Recipes<? extends RecipeCooking> recipes) {
        super(tileentitytypes, recipes);
        this.containerInfo = containerInfo;
    }

    @Override
    public EntityTypes getType() {
        return EntityTypes.FURNACE;
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
            setTileEntityInContainerInfoNull();
        }
    }

    protected void setTileEntityInContainerInfoNull() {
        containerInfo.setEntityFurnace(null);
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
            FurnaceSmeltEvent furnaceSmeltEvent = new FurnaceSmeltEvent(this.world.getWorld().getBlockAt(this.position.getX(), this.position.getY(), this.position.getZ()), source, result);
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

    public int getProperty(int i) {
        return this.b.getProperty(i);
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
