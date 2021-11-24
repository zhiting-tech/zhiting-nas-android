package com.zhiting.clouddisk.request;

/**
 * 修改存储池分区
 */
public class ModifyPartitionRequest {

    private String new_name;  // 分区名称
    private Long capacity;  // 分区容量
    private String unit;  // 单位：MB，GB，T
    private String pool_name;  // 存储池名称

    public ModifyPartitionRequest(String new_name, Long capacity, String unit, String pool_name) {
        this.new_name = new_name;
        this.capacity = capacity;
        this.unit = unit;
        this.pool_name = pool_name;
    }

    public String getNew_name() {
        return new_name;
    }

    public void setNew_name(String new_name) {
        this.new_name = new_name;
    }

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPool_name() {
        return pool_name;
    }

    public void setPool_name(String pool_name) {
        this.pool_name = pool_name;
    }
}
