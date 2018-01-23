package com.ftd.services.listener.search.bl.dm;


import java.util.Map;

public class EventEntity {
    private String id;
    private String pid;
    private String siteId;
    private String source;
    private Long tranxTime;
    private Map<String, String> metaData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getTranxTime() {
        return tranxTime;
    }

    public void setTranxTime(Long tranxTime) {
        this.tranxTime = tranxTime;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, String> metaData) {
        this.metaData = metaData;
    }


    @Override
    public String toString() {
        return "EventEntity{" +
                "id='" + id + '\'' +
                ", pid='" + pid + '\'' +
                ", siteId='" + siteId + '\'' +
                ", source='" + source + '\'' +
                ", tranxTime=" + tranxTime +
                ", metaData=" + metaData +
                '}';
    }
}
