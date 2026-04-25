package com.xarhabia.progression;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.*;

public class XpService {

    public static int getXpForKill(LivingEntity entity) {

        //Mobs hostiles
        if(entity instanceof ZombieEntity) return 10;
        if(entity instanceof CreeperEntity) return 15;
        if(entity instanceof EndermanEntity) return 30;
        if(entity instanceof SlimeEntity) return 8;
        if(entity instanceof SkeletonEntity) return 15;
        if(entity instanceof BlazeEntity) return 20;
        if(entity instanceof ElderGuardianEntity) return 30;
        if(entity instanceof GhastEntity) return 20;
        if(entity instanceof IllagerEntity) return 15;
        if(entity instanceof PhantomEntity) return 8;
        if(entity instanceof PiglinEntity) return 18;
        if(entity instanceof RavagerEntity) return 35;
        if(entity instanceof WitherSkeletonEntity) return 30;
        if(entity instanceof ZoglinEntity) return 35;
        if(entity instanceof SpiderEntity) return 10;

        //bosses
        if(entity instanceof EnderDragonEntity) return 1000;
        if(entity instanceof WardenEntity) return 2000;
        if(entity instanceof WitherEntity) return 500;
        if(entity instanceof GuardianEntity) return 300;

        return 5;
    }
}
