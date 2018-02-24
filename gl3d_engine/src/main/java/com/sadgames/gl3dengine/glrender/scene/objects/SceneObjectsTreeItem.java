package com.sadgames.gl3dengine.glrender.scene.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public abstract class SceneObjectsTreeItem {

    protected long itemNumber;
    protected String itemName;
    protected SceneObjectsTreeItem parent;
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
        }
    }

    public SceneObjectsTreeItem getChild(String name) {
        return childs.get(name);
    }

    public void putChild(SceneObjectsTreeItem item, String name, long number) {
        if (item != null) {
            if (item.parent != null)
                item.parent.deleteChild(item.itemName);

            item.itemName = name;
            item.itemNumber = number;
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
        ArrayList<SceneObjectsTreeItem> sortedItems = new ArrayList<>(childs.values());

        Collections.sort(sortedItems, new Comparator<SceneObjectsTreeItem>() {
            @Override
            public int compare(SceneObjectsTreeItem i1, SceneObjectsTreeItem i2) {
                return (int)(i1.itemNumber - i2.itemNumber);
            }
        });

        for (SceneObjectsTreeItem item : sortedItems) {
            itemHandler.onProcessItem(item);
            item.proceesTreeItems(itemHandler);
        }
    }
}
