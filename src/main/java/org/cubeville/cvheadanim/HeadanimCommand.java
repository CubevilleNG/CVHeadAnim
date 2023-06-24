package org.cubeville.cvheadanim;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import org.cubeville.commons.commands.CommandParameterDouble;
import org.cubeville.commons.commands.CommandParameterListDouble;
import org.cubeville.commons.commands.CommandParameterWorld;
import org.cubeville.commons.commands.CommandParameterInteger;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandResponse;

import org.cubeville.cvloadouts.CVLoadouts;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;

@SuppressWarnings("unchecked")
public class HeadanimCommand extends BaseCommand
{
    CVLoadouts cvLoadouts;
    
    HeadanimCommand(CVLoadouts cvLoadouts) {
        super("");
        addBaseParameter(new CommandParameterString());
        addBaseParameter(new CommandParameterInteger());
        addBaseParameter(new CommandParameterWorld());
        addBaseParameter(new CommandParameterListDouble(5, ","));
        addBaseParameter(new CommandParameterInteger());
        addParameter("rotate", true, new CommandParameterDouble());
        addParameter("yoffset", true, new CommandParameterDouble());
        addParameter("offset", true, new CommandParameterDouble());
        setSilentConsole();
        this.cvLoadouts = cvLoadouts;
    }

    public CommandResponse execute(CommandSender commandSender, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters)
        throws CommandExecutionException
    {
        String loadoutName = (String) baseParameters.get(0);
        int loadoutSlot = (Integer) baseParameters.get(1);
        World world = (World) baseParameters.get(2);
        List<Double> locationPar = (List<Double>) baseParameters.get(3);
        int duration = (Integer) baseParameters.get(4);

        Location location = new Location(world, locationPar.get(0), locationPar.get(1), locationPar.get(2), (float) (double) locationPar.get(3), (float) (double) locationPar.get(4));
        location.setY(location.getY() - 0.7);
        if(parameters.containsKey("yoffset"))
            location.setY(location.getY() + (double) parameters.get("yoffset"));
        if(parameters.containsKey("offset")) {
            Vector dir = location.getDirection();
            dir.multiply((double) parameters.get("offset"));
            location.add(dir);
        }
        if(parameters.containsKey("rotate"))
            location.setYaw(location.getYaw() + (float) (double) parameters.get("rotate"));

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.a("id", "minecraft:armor_stand");
        nbt.a("Small", (byte) 1);
        nbt.a("NoGravity", (byte) 1);
        nbt.a("Invisible", (byte) 1);
        WorldServer ws = ((CraftWorld)world).getHandle();
        Optional<Entity> entity = EntityTypes.a(nbt, ws);
        entity.get().b(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        ws.tryAddFreshEntityWithPassengers(entity.get(), CreatureSpawnEvent.SpawnReason.CUSTOM);
        ArmorStand stand = (ArmorStand)(entity.get().getBukkitEntity());
        stand.setHelmet(cvLoadouts.getLoadoutItem(loadoutName, loadoutSlot));

        new BukkitRunnable() {
            public void run() {
                stand.remove();
            }
        }.runTaskLater(CVHeadAnim.getInstance(), duration);

        return new CommandResponse("Done.");
    }
}
