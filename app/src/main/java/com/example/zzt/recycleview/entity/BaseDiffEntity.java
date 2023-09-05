package com.example.zzt.recycleview.entity;


import java.io.Serializable;
import java.util.Objects;

/**
 * 可收缩展开列表数据基类，用于 diffUtils 数据比较
 */
public class BaseDiffEntity implements Serializable {

    protected Integer groupId; // 组id
    protected Integer childId; // 子类id
    protected int childrenCount;// 子类数量
    protected boolean isExpand;

    public BaseDiffEntity(Integer groupId, Integer childId, int childrenCount, boolean isExpand) {
        this.groupId = groupId;
        this.childId = childId;
        this.childrenCount = childrenCount;
        this.isExpand = isExpand;
    }

    public BaseDiffEntity(Integer groupId, Integer childId) {
        this.groupId = groupId;
        this.childId = childId;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getChildId() {
        return childId;
    }

    public void setChildId(Integer childId) {
        this.childId = childId;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(int childrenCount) {
        this.childrenCount = childrenCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseDiffEntity)) return false;
        BaseDiffEntity that = (BaseDiffEntity) o;
        return getChildrenCount() == that.getChildrenCount() && isExpand() == that.isExpand() && Objects.equals(getGroupId(), that.getGroupId()) && Objects.equals(getChildId(), that.getChildId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGroupId(), getChildId(), getChildrenCount(), isExpand());
    }

    @Override
    public String toString() {
        return "BaseDiffEntity{" +
                "groupId=" + groupId +
                ", childId=" + childId +
                ", childrenCount=" + childrenCount +
                ", isExpand=" + isExpand +
                '}';
    }

}
