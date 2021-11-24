package com.zhiting.clouddisk.request;

public class AddPartitionRequest {

    private String name;  // 分区名称
    private Long capacity;  // 分区容量
    private String unit;  // 单位：MB，GB，T
    private String pool_name;  // 存储池名称

    public AddPartitionRequest(String name, Long capacity, String unit, String pool_name) {
        this.name = name;
        this.capacity = capacity;
        this.unit = unit;
        this.pool_name = pool_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
