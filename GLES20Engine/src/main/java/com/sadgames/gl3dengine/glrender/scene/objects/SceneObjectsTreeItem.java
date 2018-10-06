package com.sadgames.gl3dengine.glrender.scene.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class SceneObjectsTreeItem {

    protected long itemNumber;
    protected String itemName;
    protected SceneObjectsTreeItem parent;
    protected boolean visible = true;
    protected Map<String, SceneObjectsTreeItem> childs = new HashMap<>();

    public interface ISceneObjectsTreeHandler {
        void onProcessItem(SceneObjectsTreeItem item);
    }

    @SuppressWarnings("all")
    protected SceneObjectsTreeItem(long itemNumber, String itemName, SceneObjectsTreeItem parent) {
        this.itemNumber = itemNumber < 0 ? System.currentTimeMillis() : itemNumber;
        this.parent = parent;
        this.itemName = itemName != null ? itemName : String.format("ITEM_#_%d", itemNumber);
    }

    protected SceneObjectsTreeItem() {
        this(-1, null, null);
    }

    public boolean isVisible() {
        return visible;
    }

    @SuppressWarnings("unused") public void hideObject() {
        visible = false;
    }
    @SuppressWarnings("unused") public void showObject() {
        visible = true;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public long getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(long itemNumber) {
        for (SceneObjectsTreeItem item : childs.values())
            if (item.getItemNumber() >= itemNumber)
                item.setItemNumber(item.getItemNumber() + 1);

        this.itemNumber = itemNumber;
    }

    public SceneObjectsTreeItem getParent() {
        return parent;
    }
    public void setParent(SceneObjectsTreeItem parent) {
        this.parent = parent;
    }

    public Map<String, SceneObjectsTreeItem> getChilds() {
        return childs;
    }

    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void deleteChild(String name) {
        SceneObjectsTreeItem item = getChild(name);

        if (item != null) {
            childs.remove(name);
            item.setParent(null);

            long inum = item.getItemNumber();
            for (SceneObjectsTreeItem titem : childs.values())
                if (titem.getItemNumber() > inum)
                    titem.setItemNumber(titem.getItemNumber() - 1);
        }
    }

    public SceneObjectsTreeItem getChild(String name) {
        Iterator<SceneObjectsTreeItem> items = childs.values().iterator();
        SceneObjectsTreeItem result = childs.get(name);

        while (result == null && items.hasNext())
            result = items.next().getChild(name);

        return result;
    }

    public void putChild(SceneObjectsTreeItem item, String name, long number) {
        if (item != null) {
            if (item.parent != null)
                item.parent.deleteChild(item.itemName);

            item.itemName = name;
            item.setItemNumber(number);
            item.parent = this;

            childs.put(name, item);
        }
    }

    public void putChild(SceneObjectsTreeItem item, String name) {
        putChild(item, name, childs.size());
    }

    public void putChild(SceneObjectsTreeItem item) {
        putChild(item, item.itemName);
    }

    public void proceesTreeItems(ISceneObjectsTreeHandler itemHandler) {
        if (!isVisible()) return;

        ArrayList<SceneObjectsTreeItem> sortedItems = new ArrayList<>(childs.values());
        Collections.sort(sortedItems, (i1, i2) -> (int)(i1.itemNumber - i2.itemNumber));

        for (SceneObjectsTreeItem item : sortedItems)
            if (item.isVisible()) {
                itemHandler.onProcessItem(item);
                item.proceesTreeItems(itemHandler);
            }
    }
}
