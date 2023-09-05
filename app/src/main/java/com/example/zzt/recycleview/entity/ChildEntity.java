package com.example.zzt.recycleview.entity;

import java.util.Objects;

/**
 * 子项数据的实体类
 */
public class ChildEntity {

    private String child;

    public ChildEntity(String child) {
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
        if (!(o instanceof ChildEntity)) return false;
        ChildEntity that = (ChildEntity) o;
        return Objects.equals(getChild(), that.getChild());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChild());
    }
}
