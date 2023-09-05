package com.example.zzt.recycleview.entity;

import java.util.Objects;

/**
 * 子项数据的实体类
 */
public class ExpandableChildEntityV2 extends BaseDiffEntity{

    private String child;

    public ExpandableChildEntityV2(String child) {
        super(-1,-1);
        this.child = child;
    }

    public String getChild() {
        return child;
    }

    public void setChild(String child) {
        this.child = child;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpandableChildEntityV2)) return false;
        if (!super.equals(o)) return false;
        ExpandableChildEntityV2 that = (ExpandableChildEntityV2) o;
        return Objects.equals(getChild(), that.getChild());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getChild());
    }
}
