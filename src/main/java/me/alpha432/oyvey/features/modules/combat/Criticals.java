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
        // Kiểm tra nếu là gói tin tấn công
        if (event.getPacket() instanceof ServerboundInteractPacket packet) {
            // Sử dụng Accessor hoặc Reflection nếu Action không public (tùy thuộc vào version Minecraft)
            if (packet.getActionType() == ServerboundInteractPacket.ActionType.ATTACK) {
                Entity entity = mc.level.getEntity(packet.getEntityId());

                // Điều kiện cơ bản để thực hiện Crit
                if (entity == null 
                        || entity instanceof EndCrystal 
                        || !mc.player.onGround() 
                        || mc.player.isInWater() 
                        || mc.player.isInLava()
                        || !(entity instanceof LivingEntity)) return;

                // GRIM BYPASS: Sử dụng các bước nhảy packet cực nhỏ
                // Grim thường kiểm tra "Ground Spoof", nên ta gửi tọa độ Y thay đổi nhẹ
                double x = mc.player.getX();
                double y = mc.player.getY();
                double z = mc.player.getZ();

                // Gửi 3 packet nhỏ để đánh lừa server về việc người chơi đang ở trên không
                mc.player.connection.send(new ServerboundMovePlayerPacket.Pos(x, y + 0.000001, z, false));
                mc.player.connection.send(new ServerboundMovePlayerPacket.Pos(x, y + 0.0000001, z, false));
                mc.player.connection.send(new ServerboundMovePlayerPacket.Pos(x, y, z, false));

                // Hiển thị hiệu ứng crit cục bộ (Client-side)
                mc.player.crit(entity);
            }
        }
    }

    @Override
    public String getDisplayInfo() {
        return "Grim";
    }
}
