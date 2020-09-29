package cn.nkym.pt.pojo;

import java.util.List;

public class MtPage {

    //查询到的种子总数
    private Integer totalNum;

    //该页一级置顶的种子总数
    private Integer topNum;

    //该页二级置顶的种子总数
    private Integer middleNum;

    //该页的普通种子总数
    private Integer emptyNum;

    //该页的种子数据封装成的对象列表
    private List<MtTorr> mtTorrs;

    public MtPage(Integer totalNum, Integer topNum, Integer middleNum, Integer emptyNum, List<MtTorr> mtTorrs) {
        this.totalNum = totalNum;
        this.topNum = topNum;
        this.middleNum = middleNum;
        this.emptyNum = emptyNum;
        this.mtTorrs = mtTorrs;
    }

    public MtPage() {
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getTopNum() {
        return topNum;
    }

    public void setTopNum(Integer topNum) {
        this.topNum = topNum;
    }

    public Integer getMiddleNum() {
        return middleNum;
    }

    public void setMiddleNum(Integer middleNum) {
        this.middleNum = middleNum;
    }

    public Integer getEmptyNum() {
        return emptyNum;
    }

    public void setEmptyNum(Integer emptyNum) {
        this.emptyNum = emptyNum;
    }

    public List<MtTorr> getMtTorrs() {
        return mtTorrs;
    }

    public void setMtTorrs(List<MtTorr> mtTorrs) {
        this.mtTorrs = mtTorrs;
    }
}
