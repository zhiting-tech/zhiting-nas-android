package com.zhiting.clouddisk.entity;

import java.util.List;

public class MemberBean {

    /**
     * is_creator : true
     * user_count : 6
     * users : [{"user_id":1,"role_infos":[{"id":1,"name":"管理员"}],"account_name":"","nickname":"User_B7011C","token":"","phone":"","is_set_password":true},{"user_id":2,"role_infos":[{"id":1,"name":"管理员"}],"account_name":"","nickname":"User_0520","token":"","phone":"","is_set_password":false},{"user_id":4,"role_infos":[{"id":1,"name":"管理员"}],"account_name":"","nickname":"User_808080yy","token":"","phone":"","is_set_password":false},{"user_id":5,"role_infos":[{"id":1,"name":"管理员"}],"account_name":"","nickname":"User_940964","token":"","phone":"","is_set_password":false},{"user_id":6,"role_infos":[{"id":1,"name":"管理员"}],"account_name":"","nickname":"User_D5890F","token":"","phone":"","is_set_password":false},{"user_id":7,"role_infos":[{"id":1,"name":"管理员"}],"account_name":"","nickname":"kUuFYRv3LBNoRji7","token":"","phone":"","is_set_password":false}]
     */

    private boolean is_creator;
    private int user_count;
    private boolean is_owner;
    private List<UsersBean> users;

    public boolean isIs_creator() {
        return is_creator;
    }

    public void setIs_creator(boolean is_creator) {
        this.is_creator = is_creator;
    }

    public int getUser_count() {
        return user_count;
    }

    public void setUser_count(int user_count) {
        this.user_count = user_count;
    }

    public boolean isIs_owner() {
        return is_owner;
    }

    public void setIs_owner(boolean is_owner) {
        this.is_owner = is_owner;
    }

    public List<UsersBean> getUsers() {
        return users;
    }

    public void setUsers(List<UsersBean> users) {
        this.users = users;
    }

    public static class UsersBean {
        /**
         * user_id : 1
         * role_infos : [{"id":1,"name":"管理员"}]
         * account_name :
         * nickname : User_B7011C
         * token :
         * phone :
         * is_set_password : true
         */

        private int user_id;
        private String account_name;
        private String nickname;
        private String token;
        private String phone;
        private boolean is_set_password;
        private boolean selected;
        private boolean enabled = true;
        private List<RoleInfosBean> role_infos;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getAccount_name() {
            return account_name;
        }

        public void setAccount_name(String account_name) {
            this.account_name = account_name;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public boolean isIs_set_password() {
            return is_set_password;
        }

        public void setIs_set_password(boolean is_set_password) {
            this.is_set_password = is_set_password;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<RoleInfosBean> getRole_infos() {
            return role_infos;
        }

        public void setRole_infos(List<RoleInfosBean> role_infos) {
            this.role_infos = role_infos;
        }

        public static class RoleInfosBean {
            /**
             * id : 1
             * name : 管理员
             */

            private int id;
            private String name;

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
        }
    }
}
