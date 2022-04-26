package com.zhiting.clouddisk.entity;

import java.util.List;

public class ExtensionTokenListBean {

    private List<ExtensionTokenBean> extension_token_list;

    public List<ExtensionTokenBean> getExtension_token_list() {
        return extension_token_list;
    }

    public void setExtension_token_list(List<ExtensionTokenBean> extension_token_list) {
        this.extension_token_list = extension_token_list;
    }

    public static class ExtensionTokenBean {
        private String area_id;
        private String token;
        private String area_name;
        private int sa_user_id;
        private String said;
        private boolean selected;

        public String getArea_id() {
            return area_id;
        }

        public void setArea_id(String area_id) {
            this.area_id = area_id;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getArea_name() {
            return area_name;
        }

        public void setArea_name(String area_name) {
            this.area_name = area_name;
        }

        public int getSa_user_id() {
            return sa_user_id;
        }

        public void setSa_user_id(int sa_user_id) {
            this.sa_user_id = sa_user_id;
        }

        public String getSaid() {
            return said;
        }

        public void setSaid(String said) {
            this.said = said;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }
}
