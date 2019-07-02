package com.tchristofferson.portablecontainers.core.tileentities;

import com.tchristofferson.portablecontainers.core.ContainerInfo;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventoryBrewer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.BrewingStandFuelEvent;
import org.bukkit.inventory.Inventory;

public class EntityBrewingStand extends TileEntityBrewingStand implements IEntityContainer {

    private final ContainerInfo containerInfo;
    private NonNullList<ItemStack> items;
    private int lastTick;
    private Player owner;
    private Item k;

    public EntityBrewingStand(ContainerInfo containerInfo, Player owner) {
        super();
        this.containerInfo = containerInfo;
        this.items = NonNullList.a(5, ItemStack.a);
        this.lastTick = MinecraftServer.currentTick;
        this.owner = owner;
    }

    @Override
    public EntityTypes getType() {
        return EntityTypes.BREWING_STAND;
    }

    @Override
    public void tick() {
        ItemStack itemstack = this.items.get(4);
        if (this.fuelLevel <= 0 && itemstack.getItem() == Items.BLAZE_POWDER) {
            BrewingStandFuelEvent event = new BrewingStandFuelEvent(this.world.getWorld().getBlockAt(this.position.getX(), this.position.getY(), this.position.getZ()), CraftItemStack.asCraftMirror(itemstack), 20);
            this.world.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) return;

            this.fuelLevel = event.getFuelPower();
            if (this.fuelLevel > 0 && event.isConsuming()) {
                itemstack.subtract(1);
            }
        }

        boolean flag = this.h();
        boolean flag1 = this.brewTime > 0;
        ItemStack itemstack1 = this.items.get(3);
        int elapsedTicks = MinecraftServer.currentTick - this.lastTick;
        this.lastTick = MinecraftServer.currentTick;
        if (flag1) {
            this.brewTime -= elapsedTicks;
            boolean flag2 = this.brewTime <= 0;
            if (flag2 && flag) {
                this.s();
            } else if (!flag) {
                this.brewTime = 0;
            } else if (this.k != itemstack1.getItem()) {
                this.brewTime = 0;
            }
        } else if (flag && this.fuelLevel > 0) {
            --this.fuelLevel;
            this.brewTime = 400;
            this.k = itemstack1.getItem();
        }

        if (this.getViewers().isEmpty() && this.fuelLevel == 0 && (items.get(0) == ItemStack.a || items.get(0).isEmpty()) &&
                (items.get(1) == ItemStack.a || items.get(0).isEmpty()) && (items.get(2) == ItemStack.a || items.get(2).isEmpty()) &&
                (items.get(3) == ItemStack.a || items.get(3).isEmpty()) && (items.get(4) == ItemStack.a || items.get(4).isEmpty())) {
            containerInfo.setEntityBrewingStand(null);
        }
    }

    @Override
    public boolean isNotEmpty() {
        for (ItemStack itemStack : this.items) {
            if (!itemStack.isEmpty()) return false;
        }

        return true;
    }

    @Override
    public void load(NBTTagCompound nbtTagCompound) {
        super.load(nbtTagCompound);
        this.items = NonNullList.a(items.size(), ItemStack.a);
        ContainerUtil.b(nbtTagCompound, this.items);
        this.brewTime = nbtTagCompound.getShort("BrewTime");
        this.fuelLevel = nbtTagCompound.getByte("Fuel");
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbtTagCompound) {
        super.save(nbtTagCompound);
        nbtTagCompound.setShort("BrewTime", (short) this.brewTime);
        ContainerUtil.a(nbtTagCompound, this.items);
        nbtTagCompound.setByte("Fuel", (byte) this.fuelLevel);
        return nbtTagCompound;
    }

    @Override
    public ItemStack getItem(int i) {
        return i >= 0 && i < this.items.size() ? this.items.get(i) : ItemStack.a;
    }

    @Override
    public ItemStack splitStack(int i, int j) {
        return ContainerUtil.a(this.items, i, j);
    }

    @Override
    public ItemStack splitWithoutUpdate(int i) {
        return ContainerUtil.a(this.items, i);
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        if (i >= 0 && i < this.items.size()) this.items.set(i, itemstack);
    }

    @Override
    public void clear() {
        this.items.clear();
    }

    private boolean h() {
        ItemStack itemstack = this.items.get(3);
        if (itemstack.isEmpty()) {
            return false;
        } else if (!PotionBrewer.a(itemstack)) {
            return false;
        } else {
            for(int i = 0; i < 3; ++i) {
                ItemStack itemstack1 = this.items.get(i);
                if (!itemstack1.isEmpty() && PotionBrewer.a(itemstack1, itemstack)) {
                    return true;
                }
            }

            return false;
        }
    }

    private void s() {
        ItemStack itemstack = this.items.get(3);
        BrewEvent event = new BrewEvent(world.getWorld().getBlockAt(this.position.getX(), this.position.getY(), this.position.getZ()), new CraftInventoryBrewer(this), this.fuelLevel);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        for(int i = 0; i < 3; ++i) {
            this.items.set(i, PotionBrewer.d(itemstack, this.items.get(i)));
        }

        itemstack.subtract(1);
        if (itemstack.getItem().o()) {
            ItemStack itemstack1 = new ItemStack(itemstack.getItem().n());
            if (itemstack.isEmpty()) {
                itemstack = itemstack1;
            } else if (!this.world.isClientSide) {
                Inventory inventory = this.owner.getInventory();
                CraftItemStack craftItemStack = CraftItemStack.asCraftMirror(itemstack1);
                if (inventory.firstEmpty() != -1 || inventory.first(craftItemStack) != -1) {
                    inventory.addItem(craftItemStack);
                } else {
                    owner.getWorld().dropItem(owner.getLocation(), craftItemStack);
                }
            }
        }

        this.items.set(3, itemstack);
    }
}
