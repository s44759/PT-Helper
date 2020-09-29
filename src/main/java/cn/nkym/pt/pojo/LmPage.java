package cn.nkym.pt.pojo;

import java.util.List;

public class LmPage {

    //查询到的种子总数
    private Integer totalNum;

    //该页一级置顶的种子总数
    private Integer topNum;

    //该页二级置顶的种子总数
    private Integer middleNum;

    //该页的普通种子总数
    private Integer emptyNum;

    //该页的种子数据封装成的对象列表
    private List<LmTorr> lmTorrs;

    public LmPage(Integer totalNum, Integer topNum, Integer middleNum, Integer emptyNum, List<LmTorr> lmTorrs) {
        this.totalNum = totalNum;
        this.topNum = topNum;
        this.middleNum = middleNum;
        this.emptyNum = emptyNum;
        this.lmTorrs = lmTorrs;
    }

    public LmPage() {
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

    public List<LmTorr> getLmTorrs() {
        return lmTorrs;
    }

    public void setLmTorrs(List<LmTorr> lmTorrs) {
        this.lmTorrs = lmTorrs;
    }
}
