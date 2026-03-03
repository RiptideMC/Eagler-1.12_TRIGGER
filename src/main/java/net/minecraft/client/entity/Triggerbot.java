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
        // LOBBY SAFETY: Don't swing if in a menu or in Adventure/Spectator mode
        if (mc.currentScreen != null) return;
        if (mc.player.isSpectator() || mc.playerController.getCurrentGameType().isAdventure()) return;

        // Check if looking at an entity
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
            if (mc.objectMouseOver.entityHit instanceof EntityPlayer) {
                EntityPlayer target = (EntityPlayer) mc.objectMouseOver.entityHit;

                // FILTERS: Don't hit invisible players, teammates, or NPCs (not in tablist)
                if (target.isInvisible() || mc.player.isOnSameTeam(target)) return;
                if (mc.getConnection().getPlayerInfo(target.getUniqueID()) == null) return;

                long now = System.currentTimeMillis();
                if (now - lastAttack >= nextDelay) {
                    
                    // CRITICAL HIT & MACING: If jumping, wait until falling to hit
                    boolean isFalling = mc.player.fallDistance > 0.1F && !mc.player.onGround && !mc.player.isOnLadder() && !mc.player.isInWater();
                    if (mc.gameSettings.keyBindJump.isKeyDown() && !isFalling) return;

                    // Execute Attack
                    mc.playerController.attackEntity(mc.player, target);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    
                    // Set random delay (0.2s to 0.5s)
                    lastAttack = now;
                    nextDelay = 200 + rand.nextInt(301); 
                }
            }
        }
    }
}
