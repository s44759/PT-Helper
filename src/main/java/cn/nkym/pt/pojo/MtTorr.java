package cn.nkym.pt.pojo;

import java.util.Date;

public class MtTorr {

    //种子标题
    private String title;

    //种子副标题
    private String titleContext;

    //文件大小
    private String size;

    //种子id
    private Long id;

    //优惠、官方、置顶等标识
    private String activity;

    //发布时间
    private Date time;

    public MtTorr(String title, String titleContext, String size, Long id, String activity, Date time) {
        this.title = title;
        this.titleContext = titleContext;
        this.size = size;
        this.id = id;
        this.activity = activity;
        this.time = time;
    }

    public MtTorr() {
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getTitleContext() {
        return titleContext;
    }

    public void setTitleContext(String titleContext) {
        this.titleContext = titleContext;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
