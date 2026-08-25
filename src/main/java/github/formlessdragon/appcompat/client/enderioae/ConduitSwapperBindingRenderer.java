package github.formlessdragon.appcompat.client.enderioae;

import github.formlessdragon.appcompat.bridge.enderioae.ConduitSwapperNetworkBridge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class ConduitSwapperBindingRenderer {

    public static final ConduitSwapperBindingRenderer INSTANCE = new ConduitSwapperBindingRenderer();

    private ConduitSwapperBindingRenderer() {
    }

    @SubscribeEvent
    public void render(final RenderWorldLastEvent event) {
        final Minecraft minecraft = Minecraft.getMinecraft();
        final EntityPlayer player = minecraft.player;
        if (player == null || minecraft.world == null) {
            return;
        }
        final ItemStack stack = player.getHeldItemMainhand();
        final BlockPos position = ConduitSwapperNetworkBridge.getBoundPosition(stack);
        if (position == null || !ConduitSwapperNetworkBridge.isBoundInDimension(stack,
            minecraft.world.provider.getDimension()) || !minecraft.world.isBlockLoaded(position)) {
            return;
        }
        final RenderManager renderManager = minecraft.getRenderManager();
        final AxisAlignedBB bounds = minecraft.world.getBlockState(position)
            .getSelectedBoundingBox(minecraft.world, position)
            .grow(0.002D)
            .offset(-renderManager.viewerPosX, -renderManager.viewerPosY, -renderManager.viewerPosZ);
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE,
            GL11.GL_ZERO);
        GlStateManager.glLineWidth(2.0F);
        RenderGlobal.drawSelectionBoundingBox(bounds, 0.0F, 1.0F, 0.0F, 0.9F);
        GlStateManager.glLineWidth(1.0F);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
}
