/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.flys.grim

import me.zywl.fdpclient.FDPClient
import me.zywl.fdpclient.event.PacketEvent
import me.zywl.fdpclient.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.flys.FlyMode
import net.ccbluex.liquidbounce.ui.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.hud.element.elements.NotifyType
import net.minecraft.network.play.server.S27PacketExplosion


class GrimDamage : FlyMode("GrimDamage") {
    private var velocitypacket = false

    override fun onEnable() {
        if(mc.isSingleplayer) {
            FDPClient.hud.addNotification(Notification("GrimDamage", "Damage GrimAC Fly (TNT ONLY IS A TEST!!)", NotifyType.ERROR))
            fly.state = false
        }
        velocitypacket = false
    }
    override fun onUpdate(event: UpdateEvent) {
        if(mc.isSingleplayer) return

        if (velocitypacket){
            mc.thePlayer.setPositionAndRotation(mc.thePlayer.posX+50, mc.thePlayer.posY, mc.thePlayer.posZ+50, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)
            velocitypacket = false
        }
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is S27PacketExplosion ) {
            velocitypacket = true
        }
    }
}
