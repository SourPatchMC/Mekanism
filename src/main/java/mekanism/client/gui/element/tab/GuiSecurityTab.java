package mekanism.client.gui.element.tab;

import java.util.function.Supplier;
import mekanism.api.MekanismAPI;
import mekanism.api.text.EnumColor;
import mekanism.client.SpecialColors;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiInsetElement;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.lib.security.SecurityData;
import mekanism.common.network.to_server.PacketGuiInteract;
import mekanism.common.network.to_server.PacketGuiInteract.GuiInteraction;
import mekanism.common.network.to_server.PacketGuiInteract.GuiInteractionEntity;
import mekanism.common.network.to_server.PacketSecurityMode;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.common.util.SecurityUtils;
import mekanism.common.util.text.OwnerDisplay;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class GuiSecurityTab extends GuiInsetElement<Supplier<@Nullable ICapabilityProvider>> {

    private static final ResourceLocation PUBLIC = MekanismUtils.getResource(ResourceType.GUI, "public.png");
    private static final ResourceLocation PRIVATE = MekanismUtils.getResource(ResourceType.GUI, "private.png");
    private static final ResourceLocation PROTECTED = MekanismUtils.getResource(ResourceType.GUI, "protected.png");

    @Nullable
    private final InteractionHand currentHand;

    public GuiSecurityTab(IGuiWrapper gui, ICapabilityProvider provider) {
        this(gui, provider, 34);
    }

    public GuiSecurityTab(IGuiWrapper gui, ICapabilityProvider provider, int y) {
        this(gui, () -> provider, y, null);
    }

    public GuiSecurityTab(IGuiWrapper gui, @NotNull InteractionHand hand) {
        this(gui, () -> minecraft.player.getItemInHand(hand), 34, hand);
    }

    private GuiSecurityTab(IGuiWrapper gui, Supplier<ICapabilityProvider> provider, int y, @Nullable InteractionHand hand) {
        super(PUBLIC, gui, provider, gui.getWidth(), y, 26, 18, false);
        this.currentHand = hand;
    }

    @Override
    protected void colorTab() {
        MekanismRenderer.color(SpecialColors.TAB_SECURITY);
    }

    @Override
    protected ResourceLocation getOverlay() {
        return switch (MekanismAPI.getSecurityUtils().getSecurityMode(dataSource.get(), true)) {
            case PUBLIC -> super.getOverlay();
            case PRIVATE -> PRIVATE;
            case TRUSTED -> PROTECTED;
        };
    }

    @Override
    public void renderToolTip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderToolTip(guiGraphics, mouseX, mouseY);
        ICapabilityProvider provider = dataSource.get();
        if (provider != null) {
            provider.getCapability(Capabilities.SECURITY_OBJECT).ifPresent(security -> {
                SecurityData data = SecurityUtils.INSTANCE.getFinalData(security, true);
                Component securityComponent = MekanismLang.SECURITY.translateColored(EnumColor.GRAY, data.mode());
                Component ownerComponent = OwnerDisplay.of(minecraft.player, security.getOwnerUUID(), security.getOwnerName()).getTextComponent();
                if (data.override()) {
                    displayTooltips(guiGraphics, mouseX, mouseY, securityComponent, ownerComponent, MekanismLang.SECURITY_OVERRIDDEN.translateColored(EnumColor.RED));
                } else {
                    displayTooltips(guiGraphics, mouseX, mouseY, securityComponent, ownerComponent);
                }
            });
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        ICapabilityProvider provider = dataSource.get();
        if (provider != null) {
            provider.getCapability(Capabilities.SECURITY_OBJECT).ifPresent(security -> {
                if (security.ownerMatches(minecraft.player)) {
                    if (currentHand != null) {
                        Mekanism.packetHandler().sendToServer(new PacketSecurityMode(currentHand, button == GLFW.GLFW_MOUSE_BUTTON_1));
                    } else if (provider instanceof BlockEntity tile) {
                        Mekanism.packetHandler().sendToServer(new PacketGuiInteract(button == GLFW.GLFW_MOUSE_BUTTON_1 ? GuiInteraction.NEXT_SECURITY_MODE
                                                                                                                       : GuiInteraction.PREVIOUS_SECURITY_MODE, tile));
                    } else if (provider instanceof Entity entity) {
                        Mekanism.packetHandler().sendToServer(new PacketGuiInteract(button == GLFW.GLFW_MOUSE_BUTTON_1 ? GuiInteractionEntity.NEXT_SECURITY_MODE
                                                                                                                       : GuiInteractionEntity.PREVIOUS_SECURITY_MODE, entity));
                    }
                }
            });
        }
    }

    @Override
    public boolean isValidClickButton(int button) {
        return button == GLFW.GLFW_MOUSE_BUTTON_1 || button == GLFW.GLFW_MOUSE_BUTTON_2;
    }
}