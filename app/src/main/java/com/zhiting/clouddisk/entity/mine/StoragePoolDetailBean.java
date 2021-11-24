package com.zhiting.clouddisk.entity.mine;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 存储池详情
 */
public class StoragePoolDetailBean implements MultiItemEntity, Serializable {

    public static final int POOL = 1; // 自己定义字段添加存储池时是存储
    public static final int ADD = 2;  // 自己定义字段添加存储池时是最后的加号图片

    private int itemType; // 自己定义字段，用于区分添加存储池时是存储还是最后的加号图片

    private boolean selected; // 自己定义字段，用于表示存储添加存储池时是否选择

    private String id;
    private String name; // 名称
    private double capacity; // 容量
    private double use_capacity; //已用容量


    /**
     * // 异步任务状态, 为空则没有异步任务,
     * TaskDelPool_0删除存储池失败,TaskDelPool_1删除存储池中,
     * TaskAddPool_0添加存储池失败,TaskAddPool_1添加存储池中,
     * TaskUpdatePool_0修改存储池失败,TaskUpdatePool_1修改存储池中,
     */
    private String status;
    private String task_id; //异步任务ID

    private List<DiskBean> pv;  // 物理分区：就是所谓硬盘
    private List<DiskBean> lv;  //逻辑分区：实际存储池分区

    public StoragePoolDetailBean() {
    }

    public StoragePoolDetailBean(int itemType) {
        this.itemType = itemType;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
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

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
