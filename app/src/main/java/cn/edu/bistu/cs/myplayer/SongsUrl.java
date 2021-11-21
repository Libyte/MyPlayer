package cn.edu.bistu.cs.myplayer;

import java.util.List;

public class SongsUrl {

    private List<Data> data;
    private Integer code;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public static class Data {
        private Integer id;
        private Object url;
        private Integer br;
        private Integer size;
        private Object md5;
        private Integer code;
        private Integer expi;
        private Object type;
        private Integer gain;
        private Integer fee;
        private Object uf;
        private Integer payed;
        private Integer flag;
        private Boolean canExtend;
        private Object freeTrialInfo;
        private Object level;
        private Object encodeType;
        private FreeTrialPrivilege freeTrialPrivilege;
        private FreeTimeTrialPrivilege freeTimeTrialPrivilege;
        private Integer urlSource;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Object getUrl() {
            return url;
        }

        public void setUrl(Object url) {
            this.url = url;
        }

        public Integer getBr() {
            return br;
        }

        public void setBr(Integer br) {
            this.br = br;
        }

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }

        public Object getMd5() {
            return md5;
        }

        public void setMd5(Object md5) {
            this.md5 = md5;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public Integer getExpi() {
            return expi;
        }

        public void setExpi(Integer expi) {
            this.expi = expi;
        }

        public Object getType() {
            return type;
        }

        public void setType(Object type) {
            this.type = type;
        }

        public Integer getGain() {
            return gain;
        }

        public void setGain(Integer gain) {
            this.gain = gain;
        }

        public Integer getFee() {
            return fee;
        }

        public void setFee(Integer fee) {
            this.fee = fee;
        }

        public Object getUf() {
            return uf;
        }

        public void setUf(Object uf) {
            this.uf = uf;
        }

        public Integer getPayed() {
            return payed;
        }

        public void setPayed(Integer payed) {
            this.payed = payed;
        }

        public Integer getFlag() {
            return flag;
        }

        public void setFlag(Integer flag) {
            this.flag = flag;
        }

        public Boolean getCanExtend() {
            return canExtend;
        }

        public void setCanExtend(Boolean canExtend) {
            this.canExtend = canExtend;
        }

        public Object getFreeTrialInfo() {
            return freeTrialInfo;
        }

        public void setFreeTrialInfo(Object freeTrialInfo) {
            this.freeTrialInfo = freeTrialInfo;
        }

        public Object getLevel() {
            return level;
        }

        public void setLevel(Object level) {
            this.level = level;
        }

        public Object getEncodeType() {
            return encodeType;
        }

        public void setEncodeType(Object encodeType) {
            this.encodeType = encodeType;
        }

        public FreeTrialPrivilege getFreeTrialPrivilege() {
            return freeTrialPrivilege;
        }

        public void setFreeTrialPrivilege(FreeTrialPrivilege freeTrialPrivilege) {
            this.freeTrialPrivilege = freeTrialPrivilege;
        }

        public FreeTimeTrialPrivilege getFreeTimeTrialPrivilege() {
            return freeTimeTrialPrivilege;
        }

        public void setFreeTimeTrialPrivilege(FreeTimeTrialPrivilege freeTimeTrialPrivilege) {
            this.freeTimeTrialPrivilege = freeTimeTrialPrivilege;
        }

        public Integer getUrlSource() {
            return urlSource;
        }

        public void setUrlSource(Integer urlSource) {
            this.urlSource = urlSource;
        }

        public static class FreeTrialPrivilege {
            private Boolean resConsumable;
            private Boolean userConsumable;

            public Boolean getResConsumable() {
                return resConsumable;
            }

            public void setResConsumable(Boolean resConsumable) {
                this.resConsumable = resConsumable;
            }

            public Boolean getUserConsumable() {
                return userConsumable;
            }

            public void setUserConsumable(Boolean userConsumable) {
                this.userConsumable = userConsumable;
            }
        }

        public static class FreeTimeTrialPrivilege {
            private Boolean resConsumable;
            private Boolean userConsumable;
            private Integer type;
            private Integer remainTime;

            public Boolean getResConsumable() {
                return resConsumable;
            }

            public void setResConsumable(Boolean resConsumable) {
                this.resConsumable = resConsumable;
            }

            public Boolean getUserConsumable() {
                return userConsumable;
            }

            public void setUserConsumable(Boolean userConsumable) {
                this.userConsumable = userConsumable;
            }

            public Integer getType() {
                return type;
            }

            public void setType(Integer type) {
                this.type = type;
            }

            public Integer getRemainTime() {
                return remainTime;
            }

            public void setRemainTime(Integer remainTime) {
                this.remainTime = remainTime;
            }
        }
    }
}
