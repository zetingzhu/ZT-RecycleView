package com.example.zzt.recycleview.entity;

import java.util.ArrayList;
import java.util.Objects;

/**
 * 可展开收起的组数据的实体类 它比GroupEntity只是多了一个boolean类型的isExpand，用来表示展开和收起的状态。
 */
public class ExpandableGroupEntity {

    private String header;
    private String footer;
    private ArrayList<ChildEntity> children;
    private boolean isExpand;

    public ExpandableGroupEntity(String header, String footer, boolean isExpand,
                                 ArrayList<ChildEntity> children) {
        this.header = header;
        this.footer = footer;
        this.isExpand = isExpand;
        this.children = children;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public ArrayList<ChildEntity> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<ChildEntity> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpandableGroupEntity)) return false;
        ExpandableGroupEntity that = (ExpandableGroupEntity) o;
        return isExpand() == that.isExpand() && getHeader().equals(that.getHeader()) && getFooter().equals(that.getFooter()) && getChildren().equals(that.getChildren());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHeader(), getFooter(), getChildren(), isExpand());
    }
}
