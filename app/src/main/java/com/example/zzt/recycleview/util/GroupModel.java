package com.example.zzt.recycleview.util;

import android.util.Log;

import com.example.zzt.recycleview.entity.ChildEntity;
import com.example.zzt.recycleview.entity.ExpandableGroupEntity;
import com.example.zzt.recycleview.entity.ExpandableGroupEntityV2;
import com.example.zzt.recycleview.entity.GroupEntity;

import java.util.ArrayList;
import java.util.Random;

/**
 * Depiction:
 * Author: teach
 * Date: 2017/3/20 15:51
 */
public class GroupModel {
    private static final String TAG = GroupModel.class.getSimpleName();

    /**
     * 获取组列表数据
     *
     * @param groupCount    组数量
     * @param childrenCount 每个组里的子项数量
     * @return
     */
    public static ArrayList<GroupEntity> getGroups(int groupCount, int childrenCount) {
        ArrayList<GroupEntity> groups = new ArrayList<>();
        for (int i = 0; i < groupCount; i++) {
            ArrayList<ChildEntity> children = new ArrayList<>();
            for (int j = 0; j < childrenCount; j++) {
                children.add(new ChildEntity("第" + (i + 1) + "组第" + (j + 1) + "项"));
            }
            groups.add(new GroupEntity("第" + (i + 1) + "组头部",
                    "第" + (i + 1) + "组尾部", children));
        }
        return groups;
    }

    /**
     * 获取可展开收起的组列表数据(默认展开)
     *
     * @param groupCount    组数量
     * @param childrenCount 每个组里的子项数量
     * @return
     */
    public static ArrayList<ExpandableGroupEntity> getExpandableGroups(int groupCount, int childrenCount) {
        ArrayList<ExpandableGroupEntity> groups = new ArrayList<>();
        for (int i = 0; i < groupCount; i++) {
            ArrayList<ChildEntity> children = new ArrayList<>();
            for (int j = 0; j < childrenCount; j++) {
                children.add(new ChildEntity("第" + (i + 1) + "组第" + (j + 1) + "项"));
            }
            groups.add(new ExpandableGroupEntity("第" + (i + 1) + "组头部",
                    "第" + (i + 1) + "组尾部", true, children));
        }
        return groups;
    }


    public static ArrayList<ExpandableGroupEntity> getExpandableGroupsRandom(int groupCount, int childrenCount, int changeIndex) {
        Log.d(TAG, "必选随机数是：" + changeIndex);
        ArrayList<ExpandableGroupEntity> groups = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < groupCount; i++) {
            ArrayList<ChildEntity> children = new ArrayList<>();

            for (int j = 0; j < childrenCount; j++) {
                if (changeIndex == j) {
                    children.add(new ChildEntity(">  必选随机数 随机数据 " + changeIndex));
                } else {
                    int rIndex = random.nextInt(5);
                    if (j == rIndex) {
                        children.add(new ChildEntity(">>  随机数据 " + rIndex));
                    } else {
                        children.add(new ChildEntity("第" + (i + 1) + "组第" + (j + 1) + "项"));
                    }
                }
            }
            groups.add(new ExpandableGroupEntity("第" + (i + 1) + "组头部",
                    "第" + (i + 1) + "组尾部", true, children));
        }
        return groups;
    }

    /**
     * 获取可展开收起的组列表数据(默认展开)
     *
     * @param groupCount    组数量
     * @param childrenCount 每个组里的子项数量
     * @return
     */
    public static ArrayList<ExpandableGroupEntityV2> getExpandableGroupsV2(int groupCount, int childrenCount) {
        ArrayList<ExpandableGroupEntityV2> groups = new ArrayList<>();
        for (int i = 0; i < groupCount; i++) {
            ArrayList<ChildEntity> children = new ArrayList<>();
            for (int j = 0; j < childrenCount; j++) {
                children.add(new ChildEntity("第" + (i + 1) + "组第" + (j + 1) + "项"));
            }
            groups.add(new ExpandableGroupEntityV2("第" + (i + 1) + "组头部", children));
        }
        return groups;
    }


    public static ArrayList<ExpandableGroupEntityV2> getExpandableGroupsRandomV2(int groupCount, int childrenCount, int changeIndex) {
        Log.d(TAG, "必选随机数是：" + changeIndex);
        ArrayList<ExpandableGroupEntityV2> groups = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < groupCount; i++) {
            ArrayList<ChildEntity> children = new ArrayList<>();

            for (int j = 0; j < childrenCount; j++) {
                if (changeIndex == j) {
                    children.add(new ChildEntity(">  必选随机数 随机数据 " + changeIndex));
                } else {
                    int rIndex = random.nextInt(5);
                    if (j == rIndex) {
                        children.add(new ChildEntity(">>  随机数据 " + rIndex));
                    } else {
                        children.add(new ChildEntity("第" + (i + 1) + "组第" + (j + 1) + "项"));
                    }
                }
            }
            groups.add(new ExpandableGroupEntityV2("第" + (i + 1) + "组头部", children));
        }
        return groups;
    }


}
