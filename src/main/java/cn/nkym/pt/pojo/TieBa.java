package cn.nkym.pt.pojo;

public class TieBa {

    //贴吧的名称
    private String title;

    //贴吧的链接地址
    private String href;

    //签到需要发送的参数
    private String kw;

    //签到需要发送的参数
    private String tbs;

    public TieBa() {
    }

    public TieBa(String title, String href, String kw, String tbs) {
        this.title = title;
        this.href = href;
        this.kw = kw;
        this.tbs = tbs;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getKw() {
        return kw;
    }

    public void setKw(String kw) {
        this.kw = kw;
    }

    public String getTbs() {
        return tbs;
    }

    public void setTbs(String tbs) {
        this.tbs = tbs;
    }
}
