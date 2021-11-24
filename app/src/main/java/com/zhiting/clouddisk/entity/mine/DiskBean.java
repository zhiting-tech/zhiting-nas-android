package com.zhiting.clouddisk.entity.mine;

import java.io.Serializable;

/**
 *  硬盘和存储池分区
 */
public class DiskBean implements Serializable {


    /**
     * id : 4255
     * name : 安但风张义
     * capacity : 91.2
     */

    private String id;
    private String name;
    private long capacity;
    private long use_capacity; // 已用容量
    private boolean selected;

    /**
     * 为空则没有任务状态,
     * TaskAddPartition_1添加存储池分区中,TaskAddPartition_0添加存储池分区失败,
     * TaskUpdatePartition_1修改存储池分区中,TaskUpdatePartition_0修改存储池分区失败,
     * TaskDelPartition_1删除存储池分区中,TaskDelPartition_0删除存储池分区失败
     */
    private String status;
    private String task_id;  // 异步任务id

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

    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public long getUse_capacity() {
        return use_capacity;
    }

    public void setUse_capacity(long use_capacity) {
        this.use_capacity = use_capacity;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
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
}
