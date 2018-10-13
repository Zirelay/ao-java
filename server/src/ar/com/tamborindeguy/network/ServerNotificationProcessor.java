package ar.com.tamborindeguy.network;

import ar.com.tamborindeguy.manager.WorldManager;
import ar.com.tamborindeguy.network.interfaces.INotification;
import ar.com.tamborindeguy.network.interfaces.INotificationProcessor;
import ar.com.tamborindeguy.network.inventory.InventoryUpdate;
import ar.com.tamborindeguy.network.notifications.EntityUpdate;
import ar.com.tamborindeguy.network.notifications.RemoveEntity;
import com.artemis.E;
import entity.character.info.Inventory;

import static com.artemis.E.E;

public class ServerNotificationProcessor implements INotificationProcessor {
    @Override
    public void defaultProcess(INotification notification) {
    }

    @Override
    public void processNotification(EntityUpdate entityUpdate) {
        WorldManager.notifyUpdateToNearEntities(entityUpdate);
    }

    @Override
    public void processNotification(RemoveEntity removeEntity) {
        defaultProcess(removeEntity);
    }

    @Override
    public void processNotification(InventoryUpdate inventoryUpdate) {
        E player = E(inventoryUpdate.getId());
        Inventory inventory = player.getInventory();
        inventoryUpdate.getUpdates().forEach(inventory::set);
    }
}
