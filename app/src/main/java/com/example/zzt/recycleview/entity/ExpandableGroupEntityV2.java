package com.example.zzt.recycleview.entity;

import java.util.ArrayList;
import java.util.Objects;

/**
 * 可展开收起的组数据的实体类 它比GroupEntity只是多了一个boolean类型的isExpand，用来表示展开和收起的状态。
 */
public class ExpandableGroupEntityV2 extends BaseDiffEntity {

    private String header;
    private ArrayList<ChildEntity> children;

    public ExpandableGroupEntityV2(String header, ArrayList<ChildEntity> children) {
        super(-1, -1);
        this.header = header;
        this.children = children;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
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
        if (!(o instanceof ExpandableGroupEntityV2)) return false;
        if (!super.equals(o)) return false;
        ExpandableGroupEntityV2 that = (ExpandableGroupEntityV2) o;
        return Objects.equals(header, that.header) && Objects.equals(children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), header, children);
    }

    @Override
    public String toString() {
        return "ExpandableGroupEntityV2{" +
                "header='" + header + '\'' +
                ", children=" + children +
                ", isExpand=" + isExpand +
                "} " + super.toString();
    }
}
