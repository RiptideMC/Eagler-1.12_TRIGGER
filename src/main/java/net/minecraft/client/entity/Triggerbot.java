package net.minecraft.client.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import java.util.Random;

public class Triggerbot {
    private static long lastAttack = 0;
    private static long nextDelay = 0;
    private static final Random rand = new Random();

    public static void onTick(Minecraft mc) {
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
            if (mc.objectMouseOver.entityHit instanceof EntityPlayer) {
                EntityPlayer target = (EntityPlayer) mc.objectMouseOver.entityHit;

                // Filters
                if (target.isInvisible() || mc.player.isOnSameTeam(target)) return;
                if (mc.getConnection().getPlayerInfo(target.getUniqueID()) == null) return; // NPC Check

                long now = System.currentTimeMillis();
                if (now - lastAttack >= nextDelay) {
                    // Critical Hit & Macing logic
                    boolean isFalling = mc.player.fallDistance > 0.1F && !mc.player.onGround && !mc.player.isInWater();
                    
                    if (mc.gameSettings.keyBindJump.isKeyDown() && !isFalling) return;

                    mc.playerController.attackEntity(mc.player, target);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    
                    lastAttack = now;
                    nextDelay = 200 + rand.nextInt(300); // 0.2s - 0.5s delay
                }
            }
        }
    }
}
