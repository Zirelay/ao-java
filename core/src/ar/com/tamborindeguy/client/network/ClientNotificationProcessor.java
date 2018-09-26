package ar.com.tamborindeguy.client.network;

import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.network.interfaces.INotification;
import ar.com.tamborindeguy.network.interfaces.INotificationProcessor;
import ar.com.tamborindeguy.network.notifications.EntityUpdate;
import ar.com.tamborindeguy.network.notifications.RemoveEntity;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.EntityEdit;
import com.esotericsoftware.minlog.Log;
import position.WorldPos;

import static com.artemis.E.E;

public class ClientNotificationProcessor implements INotificationProcessor {

    @Override
    public void defaultProcess(INotification notification) {
    }

    @Override
    public void processNotification(EntityUpdate entityUpdate) {
        if (!GameScreen.entityExsists(entityUpdate.entityId)) {
            Log.info("Network entity doesn't exists: " + entityUpdate.entityId + ". So we create it");
            Entity newEntity = GameScreen.getWorld().createEntity();
            GameScreen.registerEntity(entityUpdate.entityId, newEntity.getId());
            addComponentsToEntity(newEntity, entityUpdate);
        } else {
            Log.info("Network entity exists: " + entityUpdate.entityId + ". Updating");
            updateEntity(entityUpdate);
        }
    }

    @Override
    public void processNotification(RemoveEntity removeEntity) {
        Log.debug("Unregistering entity: " + removeEntity.entityId);
        GameScreen.unregisterEntity(removeEntity.entityId);
    }

    private void addComponentsToEntity(Entity newEntity, EntityUpdate entityUpdate) {
        EntityEdit edit = newEntity.edit();
        for (Component component : entityUpdate.components) {
            Log.info("Adding component: " + component);
            edit.add(component);
        }
        if (E(newEntity.getId()).hasWorldPos()) {
            WorldPos worldPos = E(newEntity.getId()).getWorldPos();
            E(newEntity.getId()).pos2DX(worldPos.x);
            E(newEntity.getId()).pos2DY(worldPos.y);
            E(newEntity.getId()).character();
            E(newEntity.getId()).aOPhysics();
        }
    }

    private void updateEntity(EntityUpdate entityUpdate) {
        int entityId = GameScreen.getNetworkedEntity(entityUpdate.entityId);
        Entity entity = GameScreen.getWorld().getEntity(entityId);
        EntityEdit edit = entity.edit();
        for (Component component : entityUpdate.components) {
            edit.add(component);
        }
    }
}