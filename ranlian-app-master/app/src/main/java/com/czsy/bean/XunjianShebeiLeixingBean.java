package com.czsy.bean;

import com.czsy.ui.xunjian.IPickerViewData;

import java.util.List;

public class XunjianShebeiLeixingBean {

    private int code;
    private String message;
    private List<ResultDTO> result;
    private Object ext;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ResultDTO> getResult() {
        return result;
    }

    public void setResult(List<ResultDTO> result) {
        this.result = result;
    }

    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }

    public static class ResultDTO {
        private String id;
        private String mingcheng;
        private String parentId;
        private String parentName;
        private String chuangjianriqi;
        private String lastupdatedate;
        private String isDelete;
        private List<ErJiListDTO> erJiList;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMingcheng() {
            return mingcheng;
        }

        public void setMingcheng(String mingcheng) {
            this.mingcheng = mingcheng;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getParentName() {
            return parentName;
        }

        public void setParentName(String parentName) {
            this.parentName = parentName;
        }

        public String getChuangjianriqi() {
            return chuangjianriqi;
        }

        public void setChuangjianriqi(String chuangjianriqi) {
            this.chuangjianriqi = chuangjianriqi;
        }

        public String getLastupdatedate() {
            return lastupdatedate;
        }

        public void setLastupdatedate(String lastupdatedate) {
            this.lastupdatedate = lastupdatedate;
        }

        public String getIsDelete() {
            return isDelete;
        }

        public void setIsDelete(String isDelete) {
            this.isDelete = isDelete;
        }

        public List<ErJiListDTO> getErJiList() {
            return erJiList;
        }

        public void setErJiList(List<ErJiListDTO> erJiList) {
            this.erJiList = erJiList;
        }

        public static class ErJiListDTO implements IPickerViewData {
            private String id;
            private String mingcheng;
            private String parentId;
            private String parentName;
            private String chuangjianriqi;
            private String lastupdatedate;
            private String isDelete;
            private Object erJiList;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getMingcheng() {
                return mingcheng;
            }

            public void setMingcheng(String mingcheng) {
                this.mingcheng = mingcheng;
            }

            public String getParentId() {
                return parentId;
            }

            public void setParentId(String parentId) {
                this.parentId = parentId;
            }

            public String getParentName() {
                return parentName;
            }

            public void setParentName(String parentName) {
                this.parentName = parentName;
            }

            public String getChuangjianriqi() {
                return chuangjianriqi;
            }

            public void setChuangjianriqi(String chuangjianriqi) {
                this.chuangjianriqi = chuangjianriqi;
            }

            public String getLastupdatedate() {
                return lastupdatedate;
            }

            public void setLastupdatedate(String lastupdatedate) {
                this.lastupdatedate = lastupdatedate;
            }

            public String getIsDelete() {
                return isDelete;
            }

            public void setIsDelete(String isDelete) {
                this.isDelete = isDelete;
            }

            public Object getErJiList() {
                return erJiList;
            }

            public void setErJiList(Object erJiList) {
                this.erJiList = erJiList;
            }

            @Override
            public String getPickerViewText() {
                return mingcheng;
            }

            @Override
            public String getItemId() {
                return id;
            }
        }
    }
}
