package lilypuree.decorative_blocks.events;

import lilypuree.decorative_blocks.Constants;
import lilypuree.decorative_blocks.blocks.BrazierBlock;
import lilypuree.decorative_blocks.core.Callbacks;
import lilypuree.decorative_blocks.core.Registration;
import lilypuree.decorative_blocks.DecorativeBlocksCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        int result = Callbacks.onRightClick(event.getLevel(), event.getEntity(), event.getHand(), event.getItemStack(), event.getPos());
        if (result == -2) {
            event.setCanceled(true);
        } else if (result == -1) {
            event.setUseBlock(Event.Result.DENY);
        } else if (result == 1) {
            event.setUseBlock(Event.Result.ALLOW);
        }
    }
    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event){
        if (Callbacks.onLeftClick(event.getLevel(), event.getPos())){
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public static void onEntityDamage(LivingDamageEvent event) {
        if (event.getSource() == event.getEntity().damageSources().fall()) {
            LivingEntity entity = event.getEntity();
            BlockPos pos = entity.blockPosition();
            Level world = entity.getCommandSenderWorld();
            if (world.getFluidState(pos).getType() == Registration.STILL_THATCH) {
                event.setAmount(event.getAmount() * 0.2f);
            }
        }
    }

    @SubscribeEvent
    public static void onProjectileCollisionEvent(ProjectileImpactEvent event) {
        Projectile potion = event.getProjectile();
        Level world = potion.getCommandSenderWorld();
        BlockPos pos = potion.blockPosition();
        BlockState state = world.getBlockState(pos);
        if (world.isClientSide) return;
        if (potion instanceof ThrownPotion && PotionUtils.getPotion(((ThrownPotion) potion).getItem()) == Potions.WATER) {
            if ((state.getBlock() instanceof BrazierBlock) && state.getValue(BrazierBlock.LIT)) {
                world.setBlockAndUpdate(pos, state.setValue(BrazierBlock.LIT, Boolean.FALSE));
                world.playSound((Player) null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.8F, 1.0F);
            }
        }
    }
}
