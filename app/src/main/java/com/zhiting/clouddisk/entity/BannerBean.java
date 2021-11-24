package com.zhiting.clouddisk.entity;

/**
 * @author : WZH
 * @date : 2021/1/20 23:01
 * @description :
 */
public class BannerBean {


    /**
     * desc : 扔物线
     * id : 29
     * imagePath : https://wanandroid.com/blogimgs/8690f5f9-733a-476a-8ad2-2468d043c2d4.png
     * isVisible : 1
     * order : 0
     * title : Kotlin 的 Lambda，大部分人学得连皮毛都不算
     * type : 0
     * url : http://i0k.cn/5jhSp
     */

    private String desc;
    private int id;
    private String imagePath;
    private int isVisible;
    private int order;
    private String title;
    private int type;
    private String url;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(int isVisible) {
        this.isVisible = isVisible;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
