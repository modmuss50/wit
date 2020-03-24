package me.modmuss50.wit.mixin;

import me.modmuss50.wit.WhatIsThis;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Gui;
import net.minecraft.src.GuiIngame;
import net.minecraft.src.RenderItem;
import net.minecraft.src.ScaledResolution;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class GuiIngameMixin extends Gui {

	@Shadow @Final private Minecraft mc;

	@Shadow @Final private static RenderItem itemRenderer;

	@Inject(method = "renderGameOverlay", at = @At("RETURN"))
	public void renderGameOverlay(float deltaTicks, boolean aBoolean, int int1, int int2, CallbackInfo callbackInfo) {
		ScaledResolution scaledResolution = new ScaledResolution(this.mc.y, this.mc.c, this.mc.d);
		WhatIsThis.render(scaledResolution, (GuiIngame)(Object)this, this.mc.p, this.mc, itemRenderer);
	}
}
