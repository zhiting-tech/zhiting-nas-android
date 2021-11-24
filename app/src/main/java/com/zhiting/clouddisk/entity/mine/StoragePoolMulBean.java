package com.zhiting.clouddisk.entity.mine;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

public class StoragePoolMulBean implements MultiItemEntity {

    public static final int POOL = 1;
    public static final int ADD = 2;

    private int itemType;

    /**
     * id : 4255
     * name : 安但风张义
     * capacity : 91.2
     */

    private int id;
    private String name;
    private double capacity;
    private double use_capacity; // 已用容量
    private boolean selected;

    private List<DiskBean> pv;  // 物理分区：就是所谓硬盘
    private List<DiskBean> lv;  //逻辑分区：实际存储池分区

    public StoragePoolMulBean(int itemType) {
        this.itemType = itemType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public double getUse_capacity() {
        return use_capacity;
    }

    public void setUse_capacity(double use_capacity) {
        this.use_capacity = use_capacity;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public List<DiskBean> getPv() {
        return pv;
    }

    public void setPv(List<DiskBean> pv) {
        this.pv = pv;
    }

    public List<DiskBean> getLv() {
        return lv;
    }

    public void setLv(List<DiskBean> lv) {
        this.lv = lv;
    }
}
