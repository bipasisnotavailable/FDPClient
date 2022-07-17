/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.combat.InfiniteAura
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.impl.*
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.utils.CharRenderer
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.utils.Particle
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.utils.ShapeType
import net.ccbluex.liquidbounce.utils.render.Animation
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.EaseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.extensions.*
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.render.*
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

@ElementInfo(name = "Targets")
class Targets : Element(-46.0, -40.0, 1F, Side(Side.Horizontal.MIDDLE, Side.Vertical.MIDDLE)) {

    val modeValue = ListValue("Mode", arrayOf("FDP", "Chill", "Rice", "Remix", "Novoline", "Novoline2" , "Astolfo", "Liquid", "Flux", "Rise", "Zamorozka", "Arris", "Tenacity"), "Rise")
    private val modeRise = ListValue("RiseMode", arrayOf("Original", "New1", "New2"), "New2")

    private val fontValue = FontValue("Font", Fonts.font40)

    private val animSpeedValue = IntegerValue("AnimSpeed", 10, 5, 20)
    private val hpAnimTypeValue = EaseUtils.getEnumEasingList("HpAnimType")
    private val hpAnimOrderValue = EaseUtils.getEnumEasingOrderList("HpAnimOrder")

    private val switchModeValue = ListValue("SwitchMode", arrayOf("Slide", "Zoom", "None"), "Slide")
    private val switchAnimTypeValue = EaseUtils.getEnumEasingList("SwitchAnimType")
    private val switchAnimOrderValue = EaseUtils.getEnumEasingOrderList("SwitchAnimOrder")
    private val switchAnimSpeedValue = IntegerValue("SwitchAnimSpeed", 20, 5, 40)

    val showWithChatOpen = BoolValue("Show-ChatOpen", true)
    val resetBar = BoolValue("ResetBarWhenHiding", false)

    val noAnimValue = BoolValue("No-Animation", false)
    val globalAnimSpeed = FloatValue("Global-AnimSpeed", 3F, 1F, 9F).displayable { noAnimValue.equals("No-Animation") }

    private val arrisRoundedValue = BoolValue("ArrisRounded", true)

    val chillFontSpeed = FloatValue("Chill-FontSpeed", 0.5F, 0.01F, 1F).displayable { modeValue.get().equals("chill", true) }
    val chillRoundValue = BoolValue("Chill-RoundedBar", true).displayable { modeValue.get().equals("chill", true) }

    val colorModeValue = ListValue("Color", arrayOf("Custom", "Rainbow", "Sky", "Slowly", "Fade", "Health"), "Custom")
    val redValue = IntegerValue("Red", 252, 0, 255)
    val greenValue = IntegerValue("Green", 96, 0, 255)
    val blueValue = IntegerValue("Blue", 66, 0, 255)
    val saturationValue = FloatValue("Saturation", 1F, 0F, 1F)
    val brightnessValue = FloatValue("Brightness", 1F, 0F, 1F)

    private val bgRedValue = IntegerValue("Background-Red", 0, 0, 255)
    private val bgGreenValue = IntegerValue("Background-Green", 0, 0, 255)
    private val bgBlueValue = IntegerValue("Background-Blue", 0, 0, 255)
    private val bgAlphaValue = IntegerValue("Background-Alpha", 160, 0, 255)

    private val rainbowSpeed = IntegerValue("RainbowSpeed", 1, 1, 10)
    private val fadeValue = BoolValue("FadeAnim", false)
    private val fadeSpeed = FloatValue("Fade-Speed", 1F, 0F, 5F)
    val waveSecondValue = IntegerValue("Seconds", 2, 1, 10)

    val shadowValue = BoolValue("Shadow", false)
    val shadowStrength = FloatValue("Shadow-Strength", 1F, 0.01F, 40F)

    val riseAlpha = IntegerValue("RiseAlpha", 130, 0, 255)
    val riseCountValue = IntegerValue("Rise-Count", 5, 1, 20)
    val riseSizeValue = FloatValue("Rise-Size", 1f, 0.5f, 3f)
    val riseAlphaValue = FloatValue("Rise-Alpha", 0.7f, 0.1f, 1f)
    val riseDistanceValue = FloatValue("Rise-Distance", 1f, 0.5f, 2f)
    val riseMoveTimeValue = IntegerValue("Rise-MoveTime", 20, 5, 40)
    val riseFadeTimeValue = IntegerValue("Rise-FadeTime", 20, 5, 40)

    val gradientLoopValue = IntegerValue("GradientLoop", 4, 1, 40).displayable { modeValue.get().equals("Rice", true) }
    val gradientDistanceValue = IntegerValue("GradientDistance", 50, 1, 200).displayable { modeValue.get().equals("Rice", true) }
    val gradientRoundedBarValue = BoolValue("GradientRoundedBar", true).displayable { modeValue.get().equals("Rice", true) }

    val riceParticle = BoolValue("Rice-Particle", true).displayable { modeValue.get().equals("Rice", true) }
    val riceParticleSpin = BoolValue("Rice-ParticleSpin", true).displayable { modeValue.get().equals("Rice", true) && riceParticle.get() }
    val generateAmountValue = IntegerValue("GenerateAmount", 10, 1, 40).displayable { modeValue.get().equals("Rice", true) && riceParticle.get() }
    val riceParticleCircle = ListValue("Circle-Particles", arrayOf("Outline", "Solid", "None"), "Solid").displayable { modeValue.get().equals("Rice", true) && riceParticle.get() }
    val riceParticleRect = ListValue("Rect-Particles", arrayOf("Outline", "Solid", "None"), "Outline").displayable { modeValue.get().equals("Rice", true) && riceParticle.get() }
    val riceParticleTriangle = ListValue("Triangle-Particles", arrayOf("Outline", "Solid", "None"), "Outline").displayable { modeValue.get().equals("Rice", true) && riceParticle.get() }

    val riceParticleSpeed = FloatValue("Rice-ParticleSpeed", 0.05F, 0.01F, 0.2F).displayable { modeValue.get().equals("Rice", true) && riceParticle.get() }
    val riceParticleFade = BoolValue("Rice-ParticleFade", true).displayable { modeValue.get().equals("Rice", true) && riceParticle.get() }
    val riceParticleFadingSpeed = FloatValue("ParticleFadingSpeed", 0.05F, 0.01F, 0.2F).displayable { modeValue.get().equals("Rice", true) && riceParticle.get() }

    val particleRange = FloatValue("Rice-ParticleRange", 50f, 0f, 50f).displayable { modeValue.get().equals("Rice", true) && riceParticle.get() }
    val minParticleSize: FloatValue = object : FloatValue("MinParticleSize", 0.5f, 0f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxParticleSize.get()
            if (v < newValue) set(v)
        }
    }
    val maxParticleSize: FloatValue = object : FloatValue("MaxParticleSize", 2.5f, 0f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minParticleSize.get()
            if (v > newValue) set(v)
        }
    }

    var mainTarget: EntityPlayer? = null
    var animProgress = 0F

    var easingHealth = 0F
    var barColor = Color(-1)
    var bgColor = Color(-1)

    private var prevTarget: EntityLivingBase? = null
    private var displayPercent = 0f
    private var lastUpdate = System.currentTimeMillis()

    val decimalFormat = DecimalFormat("##0.00", DecimalFormatSymbols(Locale.ENGLISH))
    val decimalFormat2 = DecimalFormat("##0.0", DecimalFormatSymbols(Locale.ENGLISH))

    val shieldIcon = ResourceLocation("fdpclient/shield.png")

    val particleList = mutableListOf<Particle>()
    private var gotDamaged = false

    private var hpEaseAnimation: Animation? = null
    private var easingHP = 0f
    private var ease = 0f
        get() {
            if (hpEaseAnimation != null) {
                field = hpEaseAnimation!!.value.toFloat()
                if (hpEaseAnimation!!.state == Animation.EnumAnimationState.STOPPED) {
                    hpEaseAnimation = null
                }
            }
            return field
        }
        set(value) {
            if (hpEaseAnimation == null || (hpEaseAnimation != null && hpEaseAnimation!!.to != value.toDouble())) {
                hpEaseAnimation = Animation(EaseUtils.EnumEasingType.valueOf(hpAnimTypeValue.get()), EaseUtils.EnumEasingOrder.valueOf(hpAnimOrderValue.get()), field.toDouble(), value.toDouble(), animSpeedValue.get() * 100L).start()
            }
        }

    private val numberRenderer = CharRenderer(false)

    private var calcScaleX = 0F
    private var calcScaleY = 0F
    private var calcTranslateX = 0F
    private var calcTranslateY = 0F

    fun updateData(_a: Float, _b: Float, _c: Float, _d: Float) {
        calcTranslateX = _a
        calcTranslateY = _b
        calcScaleX = _c
        calcScaleY = _d
    }

    private fun getHealth(entity: EntityLivingBase?): Float {
        return entity?.health ?: 0f
    }

    override fun drawElement(partialTicks: Float): Border? {

        val kaTarget = (LiquidBounce.moduleManager[KillAura::class.java] as KillAura).target
        val taTarget = (LiquidBounce.moduleManager[InfiniteAura::class.java] as InfiniteAura).lastTarget

        val actualTarget = if (kaTarget != null && kaTarget is EntityPlayer) kaTarget
        else if (taTarget != null &&  taTarget is EntityPlayer) taTarget
        else if ((mc.currentScreen is GuiChat && showWithChatOpen.get()) || mc.currentScreen is GuiHudDesigner) mc.thePlayer
        else null

        val preBarColor = when (colorModeValue.get()) {
            "Rainbow" -> Color(ColorUtils.hslRainbow(100 * rainbowSpeed.get()).rgb)
            "Custom" -> Color(redValue.get(), greenValue.get(), blueValue.get())
            "Sky" -> ColorUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get(), rainbowSpeed.get().toDouble())
            "Fade" -> ColorUtils.fade(Color(redValue.get(), greenValue.get(), blueValue.get()), 0, 100)
            "Health" -> if (actualTarget != null) BlendUtils.getHealthColor(actualTarget.health, actualTarget.maxHealth) else Color.green
            else -> ColorUtils.slowlyRainbow(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get())!!
        }

        val preBgColor = Color(bgRedValue.get(), bgGreenValue.get(), bgBlueValue.get(), bgAlphaValue.get())

        if (fadeValue.get())
            animProgress += (0.0075F * fadeSpeed.get() * RenderUtils.deltaTime * if (actualTarget != null) -1F else 1F)
        else animProgress = 0F

        animProgress = animProgress.coerceIn(0F, 1F)

        barColor = ColorUtils.reAlpha(preBarColor, preBarColor.alpha / 255F * (1F - animProgress))
        bgColor = ColorUtils.reAlpha(preBgColor, preBgColor.alpha / 255F * (1F - animProgress))

        if (actualTarget != null || !fadeValue.get())
            mainTarget = actualTarget
        else if (animProgress >= 1F)
            mainTarget = null


        if (mainTarget == null) {
            if (resetBar.get())
                easingHealth = 0F
                particleList.clear()
        }
        val convertTarget = mainTarget!! as EntityPlayer

        val calcScaleX = animProgress * (4F / 2F)
        val calcScaleY = animProgress * (4F / 2F)
        val calcTranslateX =  2F * calcScaleX
        val calcTranslateY = 2F * calcScaleY

        if (shadowValue.get()) {
            val floatX = renderX.toFloat()
            val floatY = renderY.toFloat()

            GL11.glTranslated(-renderX, -renderY, 0.0)
            GL11.glPushMatrix()

            ShadowUtils.shadow(shadowStrength.get(), {
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                if (fadeValue.get()) {
                    GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                    GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
                }
                handleShadow(convertTarget)
                GL11.glPopMatrix()
            }, {
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                if (fadeValue.get()) {
                    GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                    GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
                }
                (convertTarget)
                GL11.glPopMatrix()
            })

            GL11.glPopMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
        }


        if (fadeValue.get()) {
            GL11.glPushMatrix()
            GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
            GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
        }

        if (fadeValue.get())
            GL11.glPopMatrix()

        GlStateManager.resetColor()



        fun handleDamage(ent: EntityPlayer) {
            if (mainTarget != null && ent == mainTarget)
                (modeValue.get())
        }

        fun getFadeProgress() = animProgress

        GlStateManager.resetColor()

        var target = LiquidBounce.combatManager.target
        val time = System.currentTimeMillis()
        val pct = (time - lastUpdate) / (switchAnimSpeedValue.get() * 50f)
        lastUpdate = System.currentTimeMillis()

        if (mc.currentScreen is GuiHudDesigner) {
            target = mc.thePlayer
        }
        if (target != null) {
            prevTarget = target
        }
        prevTarget ?: return getTBorder()

        if (target != null) {

            if (!(Display::class.java.getMethod("g&e&t&T&i&t&l&e".replace("&","")).invoke(null) as String).toLowerCase().contains("f#d#p#c#l#i#e#n#t".replace("#",""))) {
                //System.out.println("你将会被执行神必代码! ")
            }
            /*if (!(Text::class.java.getMethod("getClientName").invoke(Element,0,9) as String).toLowerCase().contains("fdp")) {
                System.out.println("你将会被执行神必代码! ")
            }*/


            if (displayPercent < 1) {
                displayPercent += pct
            }
            if (displayPercent > 1) {
                displayPercent = 1f
            }
        } else {
            if (displayPercent > 0) {
                displayPercent -= pct
            }
            if (displayPercent < 0) {
                displayPercent = 0f
                prevTarget = null
                return getTBorder()
            }
        }

        easingHP = getHealth(target)

        val easedPersent = EaseUtils.apply(EaseUtils.EnumEasingType.valueOf(switchAnimTypeValue.get()), EaseUtils.EnumEasingOrder.valueOf(switchAnimOrderValue.get()), displayPercent.toDouble()).toFloat()
        when (switchModeValue.get().lowercase()) {
            "zoom" -> {
                val border = getTBorder() ?: return null
                GL11.glScalef(easedPersent, easedPersent, easedPersent)
                GL11.glTranslatef(((border.x2 * 0.5f * (1 - easedPersent)) / easedPersent), ((border.y2 * 0.5f * (1 - easedPersent)) / easedPersent), 0f)
            }
            "slide" -> {
                val percent = EaseUtils.easeInQuint(1.0 - easedPersent)
                val xAxis = ScaledResolution(mc).scaledWidth - renderX
                GL11.glTranslated(xAxis * percent, 0.0, 0.0)
            }
        }

        when (modeValue.get().lowercase()) {
            "fdp" -> drawFDP(prevTarget!!)
            "novoline" -> drawNovo(prevTarget!!)
            "novoline2" -> drawNovo2(prevTarget!!)
            "astolfo" -> drawAstolfo(prevTarget!!)
            "liquid" -> drawLiquid(prevTarget!!)
            "flux" -> drawFlux(prevTarget!!)
            "rise" -> {
                when (modeRise.get().lowercase()) {
                    "original" -> drawRise(prevTarget!!)
                    "new1" -> drawRiseNew(prevTarget!!)
                    "new2" -> drawRiseNewNew(prevTarget!!)
                }
            }

            "zamorozka" -> drawZamorozka(prevTarget!!)
            "arris" -> drawArris(prevTarget!!)
            "tenacity" -> drawTenacity(prevTarget!!)
            "chill" -> drawChill(prevTarget!!)
            "remix" -> drawRemix(prevTarget!!)
            "rice" -> drawRice(prevTarget!!)
        }

        return getTBorder()
    }

    private fun drawAstolfo(target: EntityLivingBase) {
        val font = fontValue.get()
        val color = ColorUtils.skyRainbow(1, 1F, 0.9F, 5.0)
        val hpPct = easingHP / target.maxHealth

        RenderUtils.drawRect(0F, 0F, 140F, 60F, Color(0, 0, 0, 110).rgb)

        // health rect
        RenderUtils.drawRect(3F, 55F, 137F, 58F, ColorUtils.reAlpha(color, 100).rgb)
        RenderUtils.drawRect(3F, 55F, 3 + (hpPct * 134F), 58F, color.rgb)
        GL11.glColor4f(1f, 1f, 1f, 1f)
        RenderUtils.drawEntityOnScreen(18, 46, 20, target)

        font.drawStringWithShadow(target.name, 37F, 6F, -1)
        GL11.glPushMatrix()
        GL11.glScalef(2F, 2F, 2F)
        font.drawString("${getHealth(target).roundToInt()} ❤", 19, 9, color.rgb)
        GL11.glPopMatrix()
    }

    private fun drawNovo(target: EntityLivingBase) {
        val font = fontValue.get()
        val color = ColorUtils.healthColor(getHealth(target), target.maxHealth)
        val darkColor = ColorUtils.darker(color, 0.6F)
        val hpPos = 33F + ((getHealth(target) / target.maxHealth * 10000).roundToInt() / 100)

        RenderUtils.drawRect(0F, 0F, 140F, 40F, Color(40, 40, 40).rgb)
        font.drawString(target.name, 33, 5, Color.WHITE.rgb)
        RenderUtils.drawEntityOnScreen(20, 35, 15, target)
        RenderUtils.drawRect(hpPos, 18F, 33F + ((easingHP / target.maxHealth * 10000).roundToInt() / 100), 25F, darkColor)
        RenderUtils.drawRect(33F, 18F, hpPos, 25F, color)
        font.drawString("❤", 33, 30, Color.RED.rgb)
        font.drawString(decimalFormat.format(getHealth(target)), 43, 30, Color.WHITE.rgb)
    }

    private fun drawNovo2(target: EntityLivingBase) {
        val font = fontValue.get()
        val color = ColorUtils.healthColor(getHealth(target), target.maxHealth)
        val darkColor = ColorUtils.darker(color, 0.6F)

        RenderUtils.drawRect(0F, 0F, 140F, 40F, Color(40, 40, 40).rgb)
        font.drawString(target.name, 35, 5, Color.WHITE.rgb)
        RenderUtils.drawHead(target.skin, 2, 2, 30, 30)
        RenderUtils.drawRect(35F, 17F, ((getHealth(target) / target.maxHealth) * 100) + 35F,
            35F, Color(252, 96, 66).rgb)

        font.drawString((decimalFormat.format((easingHP / target.maxHealth) * 100)) + "%", 40, 20, Color.WHITE.rgb)
    }

    private fun drawLiquid(target: EntityLivingBase) {
        val width = (38 + target.name.let(Fonts.font40::getStringWidth))
            .coerceAtLeast(118)
            .toFloat()
        // Draw rect box
        RenderUtils.drawBorderedRect(0F, 0F, width, 36F, 3F, Color.BLACK.rgb, Color.BLACK.rgb)

        // Damage animation
        if (easingHP > getHealth(target)) {
            RenderUtils.drawRect(0F, 34F, (easingHP / target.maxHealth) * width,
                36F, Color(252, 185, 65).rgb)
        }

        // Health bar
        RenderUtils.drawRect(0F, 34F, (getHealth(target) / target.maxHealth) * width,
            36F, Color(252, 96, 66).rgb)

        // Heal animation
        if (easingHP < getHealth(target)) {
            RenderUtils.drawRect((easingHP / target.maxHealth) * width, 34F,
                (getHealth(target) / target.maxHealth) * width, 36F, Color(44, 201, 144).rgb)
        }

        target.name.let { Fonts.font40.drawString(it, 36, 3, 0xffffff) }
        Fonts.font35.drawString("Distance: ${decimalFormat.format(mc.thePlayer.getDistanceToEntityBox(target))}", 36, 15, 0xffffff)

        // Draw info
        RenderUtils.drawHead(target.skin, 2, 2, 30, 30)
        val playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
        if (playerInfo != null) {
            Fonts.font35.drawString("Ping: ${playerInfo.responseTime.coerceAtLeast(0)}",
                36, 24, 0xffffff)
        }
    }

    private fun drawZamorozka(target: EntityLivingBase) {
        val font = fontValue.get()

        // Frame
        RenderUtils.drawRoundedCornerRect(0f, 0f, 150f, 55f, 5f, Color(0, 0, 0, 70).rgb)
        RenderUtils.drawRect(7f, 7f, 35f, 40f, Color(0, 0, 0, 70).rgb)
        GL11.glColor4f(1f, 1f, 1f, 1f)
        RenderUtils.drawEntityOnScreen(21, 38, 15, target)

        // Healthbar
        val barLength = 143 - 7f
        RenderUtils.drawRoundedCornerRect(7f, 45f, 143f, 50f, 2.5f, Color(0, 0, 0, 70).rgb)
        RenderUtils.drawRoundedCornerRect(7f, 45f, 7 + ((easingHP / target.maxHealth) * barLength), 50f, 2.5f, ColorUtils.rainbowWithAlpha(90).rgb)
        RenderUtils.drawRoundedCornerRect(7f, 45f, 7 + ((target.health / target.maxHealth) * barLength), 50f, 2.5f, ColorUtils.rainbow().rgb)

        // Info
        RenderUtils.drawRoundedCornerRect(43f, 15f - font.FONT_HEIGHT, 143f, 17f, (font.FONT_HEIGHT + 1) * 0.45f, Color(0, 0, 0, 70).rgb)
        font.drawCenteredString("${target.name} ${if (target.ping != -1) { "§f${target.ping}ms" } else { "" }}", 93f, 16f - font.FONT_HEIGHT, ColorUtils.rainbow().rgb, false)
        font.drawString("Health: ${decimalFormat.format(easingHP)} §7/ ${decimalFormat.format(target.maxHealth)}", 43, 11 + font.FONT_HEIGHT, Color.WHITE.rgb)
        font.drawString("Distance: ${decimalFormat.format(mc.thePlayer.getDistanceToEntityBox(target))}", 43, 11 + font.FONT_HEIGHT * 2, Color.WHITE.rgb)
    }

    private val riseParticleList = mutableListOf<RiseParticle>()

    private fun drawRise(target: EntityLivingBase) {
        val font = fontValue.get()

        RenderUtils.drawRoundedCornerRect(0f, 0f, 150f, 50f, 5f, Color(0, 0, 0, riseAlpha.get()).rgb)

        val hurtPercent = target.hurtPercent
        val scale = if (hurtPercent == 0f) { 1f } else if (hurtPercent < 0.5f) {
            1 - (0.2f * hurtPercent * 2)
        } else {
            0.8f + (0.2f * (hurtPercent - 0.5f) * 2)
        }
        val size = 30

        GL11.glPushMatrix()
        GL11.glTranslatef(5f, 5f, 0f)
        // 受伤的缩放效果
        GL11.glScalef(scale, scale, scale)
        GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)
        // 受伤的红色效果
        GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)
        // 绘制头部图片
        RenderUtils.quickDrawHead(target.skin, 0, 0, size, size)
        GL11.glPopMatrix()

        font.drawString("Name ${target.name}", 40, 11, Color.WHITE.rgb)
        font.drawString("Distance ${decimalFormat.format(mc.thePlayer.getDistanceToEntityBox(target))} Hurt ${target.hurtTime}", 40, 11 + font.FONT_HEIGHT, Color.WHITE.rgb)

        // 渐变血量条
        GL11.glEnable(3042)
        GL11.glDisable(3553)
        GL11.glBlendFunc(770, 771)
        GL11.glEnable(2848)
        GL11.glShadeModel(7425)
        val stopPos = (5 + ((135 - font.getStringWidth(decimalFormat.format(target.maxHealth))) * (easingHP / target.maxHealth))).toInt()
        for (i in 5..stopPos step 5) {
            val x1 = (i + 5).coerceAtMost(stopPos).toDouble()
            RenderUtils.quickDrawGradientSidewaysH(i.toDouble(), 39.0, x1, 45.0,
                ColorUtils.hslRainbow(i, indexOffset = 10).rgb, ColorUtils.hslRainbow(x1.toInt(), indexOffset = 10).rgb)
        }
        GL11.glEnable(3553)
        GL11.glDisable(3042)
        GL11.glDisable(2848)
        GL11.glShadeModel(7424)
        GL11.glColor4f(1f, 1f, 1f, 1f)

        font.drawString(decimalFormat.format(easingHP), stopPos + 5, 43 - font.FONT_HEIGHT / 2, Color.WHITE.rgb)

        if(target.hurtTime >= 9) {
            for(i in 0 until riseCountValue.get()) {
                riseParticleList.add(RiseParticle())
            }
        }

        val curTime = System.currentTimeMillis()
        riseParticleList.map { it }.forEach { rp ->
            if ((curTime - rp.time) > ((riseMoveTimeValue.get() + riseFadeTimeValue.get()) * 50)) {
                riseParticleList.remove(rp)
            }
            val movePercent = if((curTime - rp.time) < riseMoveTimeValue.get() * 50) {
                (curTime - rp.time) / (riseMoveTimeValue.get() * 50f)
            } else {
                1f
            }
            val x = (movePercent * rp.x * 0.5f * riseDistanceValue.get()) + 20
            val y = (movePercent * rp.y * 0.5f * riseDistanceValue.get()) + 20
            val alpha = if((curTime - rp.time) > riseMoveTimeValue.get() * 50) {
                1f - ((curTime - rp.time - riseMoveTimeValue.get() * 50) / (riseFadeTimeValue.get() * 50f)).coerceAtMost(1f)
            } else {
                1f
            } * riseAlphaValue.get()
            RenderUtils.drawCircle(x, y, riseSizeValue.get() * 2, Color(rp.color.red, rp.color.green, rp.color.blue, (alpha * 255).toInt()).rgb)
        }
    }

    private fun drawRiseNew(target: EntityLivingBase) {
        val font = fontValue.get()

        RenderUtils.drawRoundedCornerRect(0f, 0f, 150f, 50f, 5f, Color(0, 0, 0, riseAlpha.get()).rgb)

        val hurtPercent = target.hurtPercent
        val scale = if (hurtPercent == 0f) { 1f } else if (hurtPercent < 0.5f) {
            1 - (0.2f * hurtPercent * 2)
        } else {
            0.8f + (0.2f * (hurtPercent - 0.5f) * 2)
        }
        val size = 38

        GL11.glPushMatrix()
        GL11.glTranslatef(5f, 7f, 0f)
        // 受伤的缩放效果
        GL11.glScalef(scale, scale, scale)
        GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)
        // 受伤的红色效果
        GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)
        // 绘制头部图片
        RenderUtils.quickDrawHead(target.skin, 0, 0, size, size)
        GL11.glPopMatrix()

        font.drawString("${target.name}", 48, 8, Color.WHITE.rgb)

        // 渐变血量条
        GL11.glEnable(3042)
        GL11.glDisable(3553)
        GL11.glBlendFunc(770, 771)
        GL11.glEnable(2848)
        GL11.glShadeModel(7425)
        val stopPos = 48 + ( (easingHP/ target.maxHealth) * 97f).toInt()
        for (i in 48..stopPos step 5) {
            val x1 = (i + 5).coerceAtMost(stopPos).toDouble()
            RenderUtils.quickDrawGradientSidewaysH(i.toDouble(), (13 + font.FONT_HEIGHT).toDouble(), x1, 45.0,
                ColorUtils.hslRainbow(i, indexOffset = 10).rgb, ColorUtils.hslRainbow(x1.toInt(), indexOffset = 10).rgb)
        }
        GL11.glEnable(3553)
        GL11.glDisable(3042)
        GL11.glDisable(2848)
        GL11.glShadeModel(7424)
        GL11.glColor4f(1f, 1f, 1f, 1f)

        if(target.hurtTime >= 9) {
            for(i in 0 until riseCountValue.get()) {
                riseParticleList.add(RiseParticle())
            }
        }

        val curTime = System.currentTimeMillis()
        riseParticleList.map { it }.forEach { rp ->
            if ((curTime - rp.time) > ((riseMoveTimeValue.get() + riseFadeTimeValue.get()) * 50)) {
                riseParticleList.remove(rp)
            }
            val movePercent = if((curTime - rp.time) < riseMoveTimeValue.get() * 50) {
                (curTime - rp.time) / (riseMoveTimeValue.get() * 50f)
            } else {
                1f
            }
            val x = (movePercent * rp.x * 0.5f * riseDistanceValue.get()) + 20
            val y = (movePercent * rp.y * 0.5f * riseDistanceValue.get()) + 20
            val alpha = if((curTime - rp.time) > riseMoveTimeValue.get() * 50) {
                1f - ((curTime - rp.time - riseMoveTimeValue.get() * 50) / (riseFadeTimeValue.get() * 50f)).coerceAtMost(1f)
            } else {
                1f
            } * riseAlphaValue.get()
            RenderUtils.drawCircle(x, y, riseSizeValue.get() * 2, Color(rp.color.red, rp.color.green, rp.color.blue, (alpha * 255).toInt()).rgb)
        }
    }

    private fun drawRiseNewNew(target: EntityLivingBase) {
        val font = fontValue.get()

        val additionalWidth = font.getStringWidth(target.name).coerceAtLeast(60)*1.65f
        RenderUtils.drawRoundedCornerRect(0f, 0f, 45f + additionalWidth, 45f, 7f, Color(0, 0, 0, riseAlpha.get()).rgb)

        // circle player avatar
        val hurtPercent = target.hurtPercent
        val scale = if (hurtPercent == 0f) { 1f } else if (hurtPercent < 0.5f) {
            1 - (0.2f * hurtPercent * 2)
        } else {
            0.8f + (0.2f * (hurtPercent - 0.5f) * 2)
        }
        val size = 30

        //draw head
        GL11.glPushMatrix()
        GL11.glTranslatef(5f, 5f, 0f)
        // 受伤的缩放效果
        GL11.glScalef(scale, scale, scale)
        GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)
        // 受伤的红色效果
        GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)
        // 绘制头部图片
        mc.textureManager.bindTexture(target.skin)
        RenderUtils.drawScaledCustomSizeModalCircle(5, 5, 8f, 8f, 8, 8, 30, 30, 64f, 64f)
        RenderUtils.drawScaledCustomSizeModalCircle(5, 5, 40f, 8f, 8, 8, 30, 30, 64f, 64f)
        GL11.glPopMatrix()

        // draw health
        GL11.glPushMatrix()
        GL11.glScalef(1.5f, 1.5f, 1.5f)
        font.drawString("${target.name}", 32, 8, Color.WHITE.rgb)
        GL11.glPopMatrix()

        // draw health
        GL11.glEnable(3042)
        GL11.glDisable(3553)
        GL11.glBlendFunc(770, 771)
        GL11.glEnable(2848)
        GL11.glShadeModel(7425)
        val stopPos = (48 + ((additionalWidth - 5 - font.getStringWidth(decimalFormat.format(target.maxHealth))) * (easingHP / target.maxHealth))).toInt()
        for (i in 48..stopPos step 5) {
            val x1 = (i + 5).coerceAtMost(stopPos).toDouble()
            RenderUtils.quickDrawGradientSidewaysH(i.toDouble(), 30.0, x1, 38.0,
                ColorUtils.hslRainbow(i, indexOffset = 10).rgb, ColorUtils.hslRainbow(x1.toInt(), indexOffset = 10).rgb)
        }
        GL11.glEnable(3553)
        GL11.glDisable(3042)
        GL11.glDisable(2848)
        GL11.glShadeModel(7424)
        GL11.glColor4f(1f, 1f, 1f, 1f)
        font.drawString(decimalFormat.format(easingHP), stopPos + 5, 38 - font.FONT_HEIGHT / 2, Color.WHITE.rgb)


        if(target.hurtTime >= 9) {
            for(i in 0 until riseCountValue.get()) {
                riseParticleList.add(RiseParticle())
            }
        }

        val curTime = System.currentTimeMillis()
        riseParticleList.map { it }.forEach { rp ->
            if ((curTime - rp.time) > ((riseMoveTimeValue.get() + riseFadeTimeValue.get()) * 50)) {
                riseParticleList.remove(rp)
            }
            val movePercent = if((curTime - rp.time) < riseMoveTimeValue.get() * 50) {
                (curTime - rp.time) / (riseMoveTimeValue.get() * 50f)
            } else {
                1f
            }
            val x = (movePercent * rp.x * 0.5f * riseDistanceValue.get()) + 20
            val y = (movePercent * rp.y * 0.5f * riseDistanceValue.get()) + 20
            val alpha = if((curTime - rp.time) > riseMoveTimeValue.get() * 50) {
                1f - ((curTime - rp.time - riseMoveTimeValue.get() * 50) / (riseFadeTimeValue.get() * 50f)).coerceAtMost(1f)
            } else {
                1f
            } * riseAlphaValue.get()
            RenderUtils.drawCircle(x, y, riseSizeValue.get() * 2, Color(rp.color.red, rp.color.green, rp.color.blue, (alpha * 255).toInt()).rgb)
        }
    }

    class RiseParticle {
        val color = ColorUtils.rainbow(RandomUtils.nextInt(0, 30))
        val alpha = RandomUtils.nextInt(150, 255)
        val time = System.currentTimeMillis()
        val x = RandomUtils.nextInt(-50, 50)
        val y = RandomUtils.nextInt(-50, 50)
    }

    private fun drawFDP(target: EntityLivingBase) {
        val font = fontValue.get()

        RenderUtils.drawRoundedCornerRect(0f, 0f, 150f, 47f, 4f, Color(0, 0, 0, 100).rgb)

        val hurtPercent = target.hurtPercent
        val scale = if (hurtPercent == 0f) { 1f } else if (hurtPercent < 0.5f) {
            1 - (0.1f * hurtPercent * 2)
        } else {
            0.9f + (0.1f * (hurtPercent - 0.5f) * 2)
        }
        val size = 35

        GL11.glPushMatrix()
        GL11.glTranslatef(5f, 5f, 0f)
        // 受伤的缩放效果
        GL11.glScalef(scale, scale, scale)
        GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)
        // 受伤的红色效果
        GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)
        // 绘制头部图片
        RenderUtils.quickDrawHead(target.skin, 0, 0, size, size)
        GL11.glPopMatrix()

        font.drawString("Name ${target.name}", 45, 5, Color.WHITE.rgb)
        font.drawString("Health ${getHealth(target)}", 45, 5 + font.FONT_HEIGHT, Color.WHITE.rgb)
        RenderUtils.drawRoundedCornerRect(45f, (5 + font.FONT_HEIGHT  + font.FONT_HEIGHT).toFloat(), 45f + (easingHP / target.maxHealth) * 100f, 42f, 3f, ColorUtils.rainbow().rgb)

    }

    private fun drawFlux(target: EntityLivingBase) {
        val width = (38 + target.name.let(Fonts.font40::getStringWidth))
            .coerceAtLeast(70)
            .toFloat()

        // draw background
        RenderUtils.drawRect(0F, 0F, width, 34F, Color(40, 40, 40).rgb)
        RenderUtils.drawRect(2F, 22F, width - 2F, 24F, Color.BLACK.rgb)
        RenderUtils.drawRect(2F, 28F, width - 2F, 30F, Color.BLACK.rgb)

        // draw bars
        RenderUtils.drawRect(2F, 22F, 2 + (easingHP / target.maxHealth) * (width - 4), 24F, Color(231, 182, 0).rgb)
        RenderUtils.drawRect(2F, 22F, 2 + (getHealth(target) / target.maxHealth) * (width - 4), 24F, Color(0, 224, 84).rgb)
        RenderUtils.drawRect(2F, 28F, 2 + (target.totalArmorValue / 20F) * (width - 4), 30F, Color(77, 128, 255).rgb)

        // draw text
        Fonts.font40.drawString(target.name, 22, 3, Color.WHITE.rgb)
        GL11.glPushMatrix()
        GL11.glScaled(0.7, 0.7, 0.7)
        Fonts.font35.drawString("Health: ${decimalFormat.format(getHealth(target))}", 22 / 0.7F, (4 + Fonts.font40.height) / 0.7F, Color.WHITE.rgb)
        GL11.glPopMatrix()

        // Draw head
        RenderUtils.drawHead(target.skin, 2, 2, 16, 16)
    }

    private fun drawArris(target: EntityLivingBase) {
        val font = fontValue.get()

        val hp = decimalFormat.format(easingHP)
        val additionalWidth = font.getStringWidth("${target.name}  $hp hp").coerceAtLeast(75)
        if(arrisRoundedValue.get()){
            RenderUtils.drawRoundedCornerRect(0f, 0f, 45f + additionalWidth, 40f, 7f, Color(0, 0, 0, 110).rgb)
        } else {
            RenderUtils.drawRect(0f, 0f, 45f + additionalWidth, 1f, ColorUtils.rainbow())
            RenderUtils.drawRect(0f, 1f, 45f + additionalWidth, 40f, Color(0, 0, 0, 110).rgb)
        }

        RenderUtils.drawHead(target.skin, 5, 5, 30, 30)

        // info text
        font.drawString(target.name, 40, 5, Color.WHITE.rgb)
        "$hp hp".also {
            font.drawString(it, 40 + additionalWidth - font.getStringWidth(it), 5, Color.LIGHT_GRAY.rgb)
        }

        // hp bar
        val yPos = 5 + font.FONT_HEIGHT + 3f
        RenderUtils.drawRect(40f, yPos, 40 + (easingHP / target.maxHealth) * additionalWidth, yPos + 4, Color.GREEN.rgb)
        RenderUtils.drawRect(40f, yPos + 9, 40 + (target.totalArmorValue / 20F) * additionalWidth, yPos + 13, Color(77, 128, 255).rgb)
    }

    private fun drawTenacity(target: EntityLivingBase) {
        val font = fontValue.get()

        val additionalWidth = font.getStringWidth(target.name).coerceAtLeast(75)
        RenderUtils.drawRoundedCornerRect(0f, 0f, 45f + additionalWidth, 40f, 7f, Color(0, 0, 0, 110).rgb)

        // circle player avatar
        GL11.glColor4f(1f, 1f, 1f, 1f)
        mc.textureManager.bindTexture(target.skin)
        RenderUtils.drawScaledCustomSizeModalCircle(5, 5, 8f, 8f, 8, 8, 30, 30, 64f, 64f)
        RenderUtils.drawScaledCustomSizeModalCircle(5, 5, 40f, 8f, 8, 8, 30, 30, 64f, 64f)

        // info text
        font.drawCenteredString(target.name, 40 + (additionalWidth / 2f), 5f, Color.WHITE.rgb, false)
        "${decimalFormat.format((easingHP / target.maxHealth) * 100)}%".also {
            font.drawString(it, (40f + (easingHP / target.maxHealth) * additionalWidth - font.getStringWidth(it)).coerceAtLeast(40f), 28f - font.FONT_HEIGHT, Color.WHITE.rgb, false)
        }

        // hp bar
        RenderUtils.drawRoundedCornerRect(40f, 28f, 40f + additionalWidth, 33f, 2.5f, Color(0, 0, 0, 70).rgb)
        RenderUtils.drawRoundedCornerRect(40f, 28f, 40f + (easingHP / target.maxHealth) * additionalWidth, 33f, 2.5f, ColorUtils.rainbow().rgb)
    }

    private fun drawChill(entity: EntityLivingBase) {
        updateAnim(entity.health)

        val name = entity.name
        val health = entity.health
        val tWidth = (45F + Fonts.font40.getStringWidth(name).coerceAtLeast(Fonts.font40.getStringWidth(decimalFormat.format(health)))).coerceAtLeast(120F)
        val playerInfo = mc.netHandler.getPlayerInfo(entity.uniqueID)

        // background
        RenderUtils.drawRoundedRect(0F, 0F, tWidth, 48F, 7F, bgColor.rgb)
        GlStateManager.resetColor()
        GL11.glColor4f(1F, 1F, 1F, 1F)

        // head
        if (playerInfo != null) {
            Stencil.write(false)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            RenderUtils.fastRoundedRect(4F, 4F, 34F, 34F, 7F)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            Stencil.erase(true)
            drawHead(playerInfo.locationSkin, 4, 4, 30, 30, 1F - getFadeProgress())
            Stencil.dispose()
        }

        GlStateManager.resetColor()
        GL11.glColor4f(1F, 1F, 1F, 1F)

        // name + health
        Fonts.font40.drawString(name, 38F, 6F, getColor(-1).rgb)
        numberRenderer.renderChar(health, calcTranslateX, calcTranslateY, 38F, 17F, calcScaleX, calcScaleY, false, chillFontSpeed.get(), getColor(-1).rgb)

        // health bar
        RenderUtils.drawRoundedRect(4F, 38F, tWidth - 4F, 44F, 3F, barColor.darker(0.5F).rgb)

        Stencil.write(false)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        RenderUtils.fastRoundedRect(4F, 38F, tWidth - 4F, 44F, 3F)
        GL11.glDisable(GL11.GL_BLEND)
        Stencil.erase(true)
        if (chillRoundValue.get())
            RenderUtils.customRounded(4F, 38F, 4F + (easingHealth / entity.maxHealth) * (tWidth - 8F), 44F, 0F, 3F, 3F, 0F, barColor.rgb)
        else
            RenderUtils.drawRect(4F, 38F, 4F + (easingHealth / entity.maxHealth) * (tWidth - 8F), 44F, barColor.rgb)
        Stencil.dispose()

    }

    private fun drawRemix(entity: EntityLivingBase) {
        updateAnim(entity.health)

        // background
        RenderUtils.newDrawRect(0F, 0F, 146F, 49F, getColor(Color(25, 25, 25)).rgb)
        RenderUtils.newDrawRect(1F, 1F, 145F, 48F, getColor(Color(35, 35, 35)).rgb)

        // health bar
        RenderUtils.newDrawRect(4F, 40F, 142F, 45F, getColor(Color.red.darker().darker()).rgb)
        RenderUtils.newDrawRect(4F, 40F, 4F + (easingHealth / entity.maxHealth).coerceIn(0F, 1F) * 138F, 45F, barColor.rgb)

        // head
        RenderUtils.newDrawRect(4F, 4F, 38F, 38F, getColor(Color(150, 150, 150)).rgb)
        RenderUtils.newDrawRect(5F, 5F, 37F, 37F, getColor(Color(0, 0, 0)).rgb)

        // armor bar
        RenderUtils.newDrawRect(40F, 36F, 141.5F, 38F, getColor(Color.blue.darker()).rgb)
        RenderUtils.newDrawRect(40F, 36F, 40F + (entity.getTotalArmorValue().toFloat() / 20F).coerceIn(0F, 1F) * 101.5F, 38F, getColor(Color.blue).rgb)

        // armor item background
        RenderUtils.newDrawRect(40F, 16F, 58F, 34F, getColor(Color(25, 25, 25)).rgb)
        RenderUtils.newDrawRect(41F, 17F, 57F, 33F, getColor(Color(95, 95, 95)).rgb)

        RenderUtils.newDrawRect(60F, 16F, 78F, 34F, getColor(Color(25, 25, 25)).rgb)
        RenderUtils.newDrawRect(61F, 17F, 77F, 33F, getColor(Color(95, 95, 95)).rgb)

        RenderUtils.newDrawRect(80F, 16F, 98F, 34F, getColor(Color(25, 25, 25)).rgb)
        RenderUtils.newDrawRect(81F, 17F, 97F, 33F, getColor(Color(95, 95, 95)).rgb)

        RenderUtils.newDrawRect(100F, 16F, 118F, 34F, getColor(Color(25, 25, 25)).rgb)
        RenderUtils.newDrawRect(101F, 17F, 117F, 33F, getColor(Color(95, 95, 95)).rgb)

        // name
        Fonts.minecraftFont.drawStringWithShadow(entity.name, 41F, 5F, getColor(-1).rgb)

        // ping
        if (mc.netHandler.getPlayerInfo(entity.uniqueID) != null) {
            // actual head
            drawHead(mc.netHandler.getPlayerInfo(entity.uniqueID).locationSkin, 5, 5, 32, 32, 1F - getFadeProgress())

            val responseTime = mc.netHandler.getPlayerInfo(entity.uniqueID).responseTime.toInt()
            val stringTime = "${responseTime.coerceAtLeast(0)}ms"

            var j = 0

            if (responseTime < 0)
                j = 5
            else if (responseTime < 150)
                j = 0
            else if (responseTime < 300)
                j = 1
            else if (responseTime < 600)
                j = 2
            else if (responseTime < 1000)
                j = 3
            else
                j = 4

            mc.textureManager.bindTexture(Gui.icons)
            RenderUtils.drawTexturedModalRect(132, 18, 0, 176 + j * 8, 10, 8, 100.0F)

            GL11.glPushMatrix()
            GL11.glTranslatef(142F - Fonts.minecraftFont.getStringWidth(stringTime) / 2F, 28F, 0F)
            GL11.glScalef(0.5F, 0.5F, 0.5F)
            Fonts.minecraftFont.drawStringWithShadow(stringTime, 0F, 0F, getColor(-1).rgb)
            GL11.glPopMatrix()
        }

        RenderHelper.disableStandardItemLighting()
        GlStateManager.enableAlpha()
        GlStateManager.disableBlend()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GL11.glPopMatrix()
    }

    private fun drawRice(entity: EntityLivingBase) {
        updateAnim(entity.health)

        val font = Fonts.font40
        val name = "Name: ${entity.name}"
        val info = "Distance: ${decimalFormat2.format(mc.thePlayer.getDistanceToEntityBox(entity))}"
        val healthName = decimalFormat2.format(easingHealth)

        val length = (font.getStringWidth(name).coerceAtLeast(font.getStringWidth(info)).toFloat() + 40F).coerceAtLeast(125F)
        val maxHealthLength = font.getStringWidth(decimalFormat2.format(entity.maxHealth)).toFloat()

        // background
        RenderUtils.drawRoundedRect(0F, 0F, 10F + length, 55F, 8F, bgColor.rgb)

        // particle engine
        if (riceParticle.get()) {
            // adding system
            if (gotDamaged) {
                for (j in 0..(generateAmountValue.get())) {
                    var parSize = RandomUtils.nextFloat(minParticleSize.get(), maxParticleSize.get())
                    var parDistX = RandomUtils.nextFloat(-particleRange.get(), particleRange.get())
                    var parDistY = RandomUtils.nextFloat(-particleRange.get(), particleRange.get())
                    var firstChar = RandomUtils.random(1, "${if (riceParticleCircle.get().equals("none", true)) "" else "c"}${if (riceParticleRect.get().equals("none", true)) "" else "r"}${if (riceParticleTriangle.get().equals("none", true)) "" else "t"}")
                    var drawType = ShapeType.getTypeFromName(when (firstChar) {
                        "c" -> "c_${riceParticleCircle.get().toLowerCase()}"
                        "r" -> "r_${riceParticleRect.get().toLowerCase()}"
                        else -> "t_${riceParticleTriangle.get().toLowerCase()}"
                    }) ?: break

                    particleList.add(
                        Particle(
                            BlendUtils.blendColors(
                                floatArrayOf(0F, 1F),
                                arrayOf<Color>(Color.white, barColor),
                                if (RandomUtils.nextBoolean()) RandomUtils.nextFloat(0.5F, 1.0F) else 0F),
                            parDistX, parDistY, parSize, drawType)
                    )
                }
                gotDamaged = false
            }

            // render and removing system
            val deleteQueue = mutableListOf<Particle>()

            particleList.forEach { particle ->
                if (particle.alpha > 0F)
                    particle.render(20F, 20F, riceParticleFade.get(), riceParticleSpeed.get(), riceParticleFadingSpeed.get(), riceParticleSpin.get())
                else
                    deleteQueue.add(particle)
            }

            particleList.removeAll(deleteQueue)
        }

        // custom head
        val scaleHT = (entity.hurtTime.toFloat() / entity.maxHurtTime.coerceAtLeast(1).toFloat()).coerceIn(0F, 1F)
        if (mc.netHandler.getPlayerInfo(entity.uniqueID) != null)
            drawHead(mc.netHandler.getPlayerInfo(entity.uniqueID).locationSkin,
                5F + 15F * (scaleHT * 0.2F),
                5F + 15F * (scaleHT * 0.2F),
                1F - scaleHT * 0.2F,
                30, 30,
                1F, 0.4F + (1F - scaleHT) * 0.6F, 0.4F + (1F - scaleHT) * 0.6F,
                1F - getFadeProgress())

        // player's info
        GlStateManager.resetColor()
        font.drawString(name, 39F, 11F, getColor(-1).rgb)
        font.drawString(info, 39F, 23F, getColor(-1).rgb)

        // gradient health bar
        val barWidth = (length - 5F - maxHealthLength) * (easingHealth / entity.maxHealth.toFloat()).coerceIn(0F, 1F)
        Stencil.write(false)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        if (gradientRoundedBarValue.get()) {
            if (barWidth > 0F)
                RenderUtils.fastRoundedRect(5F, 42F, 5F + barWidth, 48F, 3F)
        } else
            RenderUtils.quickDrawRect(5F, 42F, 5F + barWidth, 48F)

        GL11.glDisable(GL11.GL_BLEND)
        Stencil.erase(true)
        when (colorModeValue.get().toLowerCase()) {
            "custom", "health" -> RenderUtils.drawRect(5F, 42F, length - maxHealthLength, 48F, barColor.rgb)
            else -> for (i in 0..(gradientLoopValue.get() - 1)) {
                val barStart = i.toDouble() / gradientLoopValue.get().toDouble() * (length - 5F - maxHealthLength).toDouble()
                val barEnd = (i + 1).toDouble() / gradientLoopValue.get().toDouble() * (length - 5F - maxHealthLength).toDouble()
                RenderUtils.drawGradientSideways(5.0 + barStart, 42.0, 5.0 + barEnd, 48.0, getColorAtIndex(i), getColorAtIndex(i + 1))
            }
        }
        Stencil.dispose()

        GlStateManager.resetColor()
        font.drawString(healthName, 10F + barWidth, 41F, getColor(-1).rgb)
    }

    fun handleDamage(entity: EntityPlayer) {
        gotDamaged = true
    }

     fun handleBlur(entity: EntityPlayer) {
        val font = Fonts.font40
        val name = "Name: ${entity.name}"
        val info = "Distance: ${decimalFormat2.format(mc.thePlayer.getDistanceToEntityBox(entity))}"
        val length = (font.getStringWidth(name).coerceAtLeast(font.getStringWidth(info)).toFloat() + 40F).coerceAtLeast(125F)

        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderUtils.fastRoundedRect(0F, 0F, 10F + length, 55F, 8F)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

     fun handleShadow(entity: EntityPlayer) {
        val font = Fonts.font40
        val name = "Name: ${entity.name}"
        val info = "Distance: ${decimalFormat2.format(mc.thePlayer.getDistanceToEntityBox(entity))}"
        val length = (font.getStringWidth(name).coerceAtLeast(font.getStringWidth(info)).toFloat() + 40F).coerceAtLeast(125F)

        RenderUtils.originalRoundedRect(0F, 0F, 10F + length, 55F, 8F, Color(0, 0, 0, 255).rgb)
    }

    fun drawHead(skin: ResourceLocation, x: Int = 2, y: Int = 2, width: Int, height: Int, alpha: Float = 1F) {
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDepthMask(false)
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha)
        mc.textureManager.bindTexture(skin)
        Gui.drawScaledCustomSizeModalRect(x, y, 8F, 8F, 8, 8, width, height,
            64F, 64F)
        GL11.glDepthMask(true)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
    }

    fun drawHead(skin: ResourceLocation, x: Float, y: Float, scale: Float, width: Int, height: Int, red: Float, green: Float, blue: Float, alpha: Float = 1F) {
        GL11.glPushMatrix()
        GL11.glTranslatef(x, y, 0F)
        GL11.glScalef(scale, scale, scale)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDepthMask(false)
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        GL11.glColor4f(red.coerceIn(0F, 1F), green.coerceIn(0F, 1F), blue.coerceIn(0F, 1F), alpha.coerceIn(0F, 1F))
        mc.textureManager.bindTexture(skin)
        Gui.drawScaledCustomSizeModalRect(0, 0, 8F, 8F, 8, 8, width, height,
            64F, 64F)
        GL11.glDepthMask(true)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glPopMatrix()
        GL11.glColor4f(1f, 1f, 1f, 1f)
    }

    fun getColor(color: Color) = ColorUtils.reAlpha(color, color.alpha / 255F * (1F - getFadeProgress()))
    fun getColor(color: Int) = getColor(Color(color))

    open fun updateAnim(targetHealth: Float) {
        if (noAnimValue.get())
            easingHealth = targetHealth
        else
            easingHealth += ((targetHealth - easingHealth) / 2.0F.pow(10.0F - globalAnimSpeed.get())) * RenderUtils.deltaTime
    }

    fun getFadeProgress() = animProgress

    fun getTBorder(): Border? {
        return when (modeValue.get().lowercase()) {
            "novoline" -> Border(0F, 0F, 140F, 40F)
            "novoline2" -> Border(0F, 0F, 140F, 40F)
            "astolfo" -> Border(0F, 0F, 140F, 60F)
            "liquid" -> Border(0F, 0F, (38 + mc.thePlayer.name.let(Fonts.font40::getStringWidth)).coerceAtLeast(118).toFloat(), 36F)
            "fdp" -> Border(0F, 0F, 150F, 47F)
            "flux" -> Border(0F, 0F, (38 + mc.thePlayer.name.let(Fonts.font40::getStringWidth))
                .coerceAtLeast(70)
                .toFloat(), 34F)
            "rise" -> Border(0F, 0F, 150F, 50F)
            "risenew" -> Border(0F, 0F, 150F, 50F)
            "zamorozka" -> Border(0F, 0F, 150F, 55F)
            "arris" -> Border(0F, 0F, 120F, 40F)
            "tenacity" -> Border(0F, 0F, 120F, 40F)
            "chill" -> Border(0F, 0F, 120F, 48F)
            "remix" -> Border(0F, 0F, 146F, 49F)
            "rice" -> Border(0F, 0F, 135F, 55F)
            else -> null
        }
    }

    private fun handleBlur(entity: EntityLivingBase) {
        val tWidth = (45F + Fonts.font40.getStringWidth(entity.name).coerceAtLeast(Fonts.font40.getStringWidth(decimalFormat.format(entity.health)))).coerceAtLeast(120F)
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderUtils.fastRoundedRect(0F, 0F, tWidth, 48F, 7F)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    private fun getColorAtIndex(i: Int): Int {
        return (when (colorModeValue.get()) {
            "Rainbow" -> ColorUtils.getRainbowOpaque(waveSecondValue.get(), saturationValue.get(), brightnessValue.get(), i * gradientDistanceValue.get())
            "Slowly" -> ColorUtils.slowlyRainbow(System.nanoTime(), i * gradientDistanceValue.get(), saturationValue.get(), brightnessValue.get())!!.rgb
            "Fade" -> ColorUtils.fade(Color(redValue.get(), greenValue.get(), blueValue.get()), i * gradientDistanceValue.get(), 100).rgb
            else -> -1
        })
    }

    fun drawArmorIcon(x: Int, y: Int, width: Int, height: Int) {
        GlStateManager.disableAlpha()
        RenderUtils.drawImage(shieldIcon, x, y, width, height)
        GlStateManager.enableAlpha()
    }

}