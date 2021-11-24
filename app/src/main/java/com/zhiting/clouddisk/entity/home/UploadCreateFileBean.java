package com.zhiting.clouddisk.entity.home;

import java.util.List;

public class UploadCreateFileBean {

    private ResourceBean resource;
    private List<ChunkBean> chunks;

    public ResourceBean getResource() {
        return resource;
    }

    public void setResource(ResourceBean resource) {
        this.resource = resource;
    }

    public List<ChunkBean> getChunks() {
        return chunks;
    }

    public void setChunks(List<ChunkBean> chunks) {
        this.chunks = chunks;
    }
}
