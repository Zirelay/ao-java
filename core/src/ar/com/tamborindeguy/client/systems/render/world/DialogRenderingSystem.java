package ar.com.tamborindeguy.client.systems.render.world;

import ar.com.tamborindeguy.client.handlers.DescriptorHandler;
import ar.com.tamborindeguy.client.systems.OrderedEntityProcessingSystem;
import ar.com.tamborindeguy.client.systems.camera.CameraSystem;
import ar.com.tamborindeguy.client.utils.Fonts;
import ar.com.tamborindeguy.model.map.Tile;
import ar.com.tamborindeguy.util.Util;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import entity.Body;
import entity.Dialog;
import position.Pos2D;
import position.WorldPos;

import java.util.Comparator;

import static ar.com.tamborindeguy.client.utils.Fonts.DIALOG_FONT;
import static ar.com.tamborindeguy.client.utils.Fonts.MAGIC_FONT;
import static ar.com.tamborindeguy.client.utils.Fonts.dialogLayout;

@Wire
public class DialogRenderingSystem extends OrderedEntityProcessingSystem {

    public static final int ALPHA_TIME = 2;
    public static final int MAX_LENGTH = 120;
    public static final int DISTANCE_TO_TOP = 5;
    public static final float TIME = 0.3f;
    public static final float VELOCITY = DISTANCE_TO_TOP / TIME;
    private SpriteBatch batch;
    private CameraSystem cameraSystem;

    public DialogRenderingSystem(SpriteBatch batch) {
        super(Aspect.all(Dialog.class, Body.class, WorldPos.class));
        this.batch = batch;
    }

    @Override
    protected void begin() {
        cameraSystem.guiCamera.update();
        batch.setProjectionMatrix(cameraSystem.guiCamera.combined);
        batch.begin();
    }

    @Override
    protected void end() {
        batch.end();
    }

    @Override
    protected void process(Entity e) {
        E player = E.E(e);
        Pos2D playerPos = Util.toScreen(player.worldPosPos2D());
        Pos2D cameraPos = new Pos2D(cameraSystem.camera.position.x, cameraSystem.camera.position.y);
        Pos2D screenPos = new Pos2D(cameraPos.x - playerPos.x, cameraPos.y - playerPos.y);
        Dialog dialog = player.getDialog();
        dialog.time -= world.getDelta();
        if (dialog.time > 0) {
            BitmapFont font = dialog.kind.equals(Dialog.Kind.MAGIC_WORDS) ? MAGIC_FONT : DIALOG_FONT;
            Color copy = font.getColor().cpy();
            if (dialog.time < ALPHA_TIME) {
                dialog.alpha = dialog.time / ALPHA_TIME;
                font.getColor().a = dialog.alpha;
            }

            dialogLayout.setText(font, dialog.text);
            float width = Math.min(dialogLayout.width, MAX_LENGTH);
            dialogLayout.setText(font, dialog.text, font.getColor(), width, Align.center, true);
            final float fontX = (cameraSystem.guiCamera.viewportWidth / 2) - screenPos.x - (width + Tile.TILE_PIXEL_WIDTH) / 2;
            float  up = Dialog.DEFAULT_TIME - dialog.time <= TIME ? (Dialog.DEFAULT_TIME - dialog.time) * VELOCITY : DISTANCE_TO_TOP;
            float offsetY = DescriptorHandler.getBody(player.getBody().index).getHeadOffsetY() - up;
            final float fontY = (cameraSystem.guiCamera.viewportHeight / 2) + screenPos.y + 50 - offsetY + dialogLayout.height;
            font.draw(batch, dialogLayout, fontX, fontY);
            font.setColor(copy);
        } else {
            player.removeDialog();
        }
    }

    @Override
    protected Comparator<? super Entity> getComparator() {
        return Comparator.comparingInt(entity -> E.E(entity).getWorldPos().y);
    }
}
