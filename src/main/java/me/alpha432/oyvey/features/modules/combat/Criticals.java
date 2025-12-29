package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.event.system.Subscribe;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;

public class Criticals extends Module {
    public Criticals() {
        super("Criticals", "Bypass Grim AC critical hits", Category.COMBAT);
    }

    @Subscribe
    private void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof ServerboundInteractPacket packet) {
            // Sửa lỗi 1: Truy cập action và entityId theo cấu trúc Fabric chuẩn
            // Chúng ta ép kiểu để kiểm tra nếu đây là hành động ATTACK
            if (packet.getActionType() == ServerboundInteractPacket.ActionType.ATTACK) {
                // Lấy entityId trực tiếp từ packet (Intermediary mappings)
                int entityId = packet.getEntityId();
                Entity entity = mc.level.getEntity(entityId);

                if (entity == null 
                        || entity instanceof EndCrystal 
                        || !mc.player.onGround() 
                        || mc.player.isInWater() 
                        || !(entity instanceof LivingEntity)) return;

                double x = mc.player.getX();
                double y = mc.player.getY();
                double z = mc.player.getZ();

                // Sửa lỗi 2: Thêm tham số thứ 5 cho constructor (thường là boolean onGround)
                // Cấu trúc: Pos(x, y, z, horizontalCollision, onGround)
                mc.player.connection.send(new ServerboundMovePlayerPacket.Pos(x, y + 0.000001, z, false, false));
                mc.player.connection.send(new ServerboundMovePlayerPacket.Pos(x, y + 0.0000001, z, false, false));
                mc.player.connection.send(new ServerboundMovePlayerPacket.Pos(x, y, z, false, false));

                mc.player.crit(entity);
            }
        }
    }

    @Override
    public String getDisplayInfo() {
        return "Grim";
    }
}
