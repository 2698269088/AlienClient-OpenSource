package dev.luminous.mod.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.luminous.Alien;
import dev.luminous.api.utils.Wrapper;
import dev.luminous.mod.Mod;
import dev.luminous.mod.gui.items.Component;
import dev.luminous.mod.gui.items.Item;
import dev.luminous.mod.gui.items.buttons.ModuleButton;
import dev.luminous.mod.modules.Module;
import dev.luminous.mod.modules.impl.client.ClickGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;

public class ClickGuiScreen extends Screen {
    private static ClickGuiScreen INSTANCE;

    static {
        INSTANCE = new ClickGuiScreen();
    }

    private final ArrayList<Component> components = new ArrayList<>();

    public ClickGuiScreen() {
        super(Text.literal("Alien"));
        setInstance();
        load();
    }

    public static ClickGuiScreen getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGuiScreen();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private void load() {
        int x = -84;
        for (final Module.Category category : Module.Category.values()) {
            this.components.add(new Component(category.toString(), category, x += 94, 4, true) {

                @Override
                public void setupItems() {
                    for (Module module : Alien.MODULE.getModules()) {
                        if (module.getCategory().equals(category)) {
                            this.addButton(new ModuleButton(module));
                        }
                    }
                }
            });
        }
        this.components.forEach(components -> components.getItems().sort(Comparator.comparing(Mod::getName)));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderColor(1, 1, 1, (float) ClickGui.getInstance().alphaValue);
        Item.context = context;
        renderBackground(context, mouseX, mouseY, delta);
        //context.fill(0, 0, context.getScaledWindowWidth(), context.getScaledWindowHeight(), new Color(0, 0, 0, 120).hashCode());
        this.components.forEach(components -> components.drawScreen(context, mouseX, mouseY, delta));
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int clickedButton) {
        this.components.forEach(components -> components.mouseClicked((int) mouseX, (int) mouseY, clickedButton));
        return super.mouseClicked(mouseX, mouseY, clickedButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int releaseButton) {
        this.components.forEach(components -> components.mouseReleased((int) mouseX, (int) mouseY, releaseButton));
        return super.mouseReleased(mouseX, mouseY, releaseButton);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (InputUtil.isKeyPressed(Wrapper.mc.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
            if (verticalAmount < 0) {
                this.components.forEach(component -> component.setX(component.getX() - 15));
            } else if (verticalAmount > 0) {
                this.components.forEach(component -> component.setX(component.getX() + 15));
            }
        } else {
            if (verticalAmount < 0) {
                this.components.forEach(component -> component.setY(component.getY() - 15));
            } else if (verticalAmount > 0) {
                this.components.forEach(component -> component.setY(component.getY() + 15));
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.components.forEach(component -> component.onKeyPressed(keyCode));
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        this.components.forEach(component -> component.onKeyTyped(chr, modifiers));
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public final ArrayList<Component> getComponents() {
        return this.components;
    }

    public int getTextOffset() {
        return -ClickGui.getInstance().textOffset.getValueInt() - 6;
    }
}
