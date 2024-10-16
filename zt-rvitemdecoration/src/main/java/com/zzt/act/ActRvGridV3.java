package com.zzt.act;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinuscxj.itemdecoration.GridDividerItemDecoration;
import com.dinuscxj.recycleritemdecoration.decor.DividerDrawable;
import com.dinuscxj.recycleritemdecoration.decor.DividerItemDecoration;
import com.dinuscxj.recycleritemdecoration.foundation.RecyclerListAdapter;
import com.dinuscxj.recycleritemdecoration.foundation.RecyclerListFragment;
import com.dinuscxj.recycleritemdecoration.foundation.SingleFragmentActivity;
import com.dinuscxj.recycleritemdecoration.model.ItemAnimal;
import com.dinuscxj.recycleritemdecoration.model.ItemCartoon;
import com.dinuscxj.recycleritemdecoration.model.ItemDrawable;
import com.dinuscxj.recycleritemdecoration.model.ItemScenic;
import com.dinuscxj.recycleritemdecoration.viewholder.AnimalViewHolder;
import com.dinuscxj.recycleritemdecoration.viewholder.CartoonViewHolder;
import com.dinuscxj.recycleritemdecoration.viewholder.ScenicViewHolder;
import com.yanyusong.y_divideritemdecoration.Y_Divider;
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder;
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration;
import com.zzt.zt_rvitemdecoration.R;

import java.util.ArrayList;
import java.util.List;

public class ActRvGridV3 extends SingleFragmentActivity {

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, ActRvGridV3.class);
        activity.startActivity(intent);
    }

    @Override
    protected Fragment createFragment() {
        return GridDividerFragment.newInstance();
    }

    public static class GridDividerFragment extends RecyclerListFragment {

        public static GridDividerFragment newInstance() {
            return new GridDividerFragment();
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getAdapter().addAll(createItemDrawableList());
        }

        private List<ItemDrawable> createItemDrawableList() {
            List<ItemDrawable> itemDrawableList = new ArrayList<>();
            //ItemAnimal
            itemDrawableList.add(new ItemAnimal(R.drawable.ic_animal0, getString(R.string.animal0)));
            itemDrawableList.add(new ItemAnimal(R.drawable.ic_animal1, getString(R.string.animal1)));
            itemDrawableList.add(new ItemAnimal(R.drawable.ic_animal2, getString(R.string.animal2)));
            //ItemCartoon
            itemDrawableList.add(new ItemCartoon(R.drawable.ic_cartoon0, getString(R.string.cartoon0)));
            itemDrawableList.add(new ItemCartoon(R.drawable.ic_cartoon1, getString(R.string.cartoon1)));
            itemDrawableList.add(new ItemCartoon(R.drawable.ic_cartoon2, getString(R.string.cartoon2)));
            //ItemScenic
            itemDrawableList.add(new ItemScenic(R.drawable.ic_scenic0, getString(R.string.scenic0)));
            itemDrawableList.add(new ItemScenic(R.drawable.ic_scenic1, getString(R.string.scenic1)));
            itemDrawableList.add(new ItemScenic(R.drawable.ic_scenic2, getString(R.string.scenic2)));

            return itemDrawableList;
        }

        @Override
        public RecyclerListAdapter onCreateAdapter() {
            return new RecyclerListAdapter() {
                {
                    addViewType(ItemAnimal.class, new ViewHolderFactory<ViewHolder>() {
                        @Override
                        public ViewHolder onCreateViewHolder(ViewGroup parent) {
                            return new AnimalViewHolder(parent);
                        }
                    });

                    addViewType(ItemCartoon.class, new ViewHolderFactory<ViewHolder>() {
                        @Override
                        public ViewHolder onCreateViewHolder(ViewGroup parent) {
                            return new CartoonViewHolder(parent);
                        }
                    });

                    addViewType(ItemScenic.class, new ViewHolderFactory<ViewHolder>() {
                        @Override
                        public ViewHolder onCreateViewHolder(ViewGroup parent) {
                            return new ScenicViewHolder(parent);
                        }
                    });
                }
            };
        }

        @Override
        public RecyclerView.LayoutManager onCreateLayoutManager() {
            return new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        }

        @Override
        public RecyclerView.ItemDecoration onCreateItemDecoration() {
            return createOffsetsItemDecoration();
        }

        private RecyclerView.ItemDecoration createOffsetsItemDecoration() {
//            Y_DividerItemDecoration itemDecoration = new Y_DividerItemDecoration(getContext()) {
//                @Nullable
//                @Override
//                public Y_Divider getDivider(int itemPosition) {
//                    Y_Divider divider = null;
//                    switch (itemPosition % 2) {
//                        case 0:
//                            //每一行第一个显示rignt和bottom
//                            divider = new Y_DividerBuilder()
//                                    .setRightSideLine(true, ContextCompat.getColor(getContext(), R.color.color_FF0000), 22, 0, 0)
//                                    .setBottomSideLine(true, ContextCompat.getColor(getContext(), R.color.color_11FF00), 22, 0, 0)
//                                    .create();
//                            break;
//                        case 1:
//                            //第二个显示Left和bottom
//                            divider = new Y_DividerBuilder()
//                                    .setLeftSideLine(true, ContextCompat.getColor(getContext(), R.color.color_0066FF), 22, 0, 0)
//                                    .setBottomSideLine(true, ContextCompat.getColor(getContext(), R.color.color_FFC800), 22, 0, 0)
//                                    .create();
//                            break;
//                        default:
//                            break;
//                    }
//                    return divider;
//                }
//            };


            Y_DividerItemDecoration itemDecoration = new Y_DividerItemDecoration(getContext()) {
                @Nullable
                @Override
                public Y_Divider getDivider(int itemPosition, int childCount, int mSpanCount) {
                    Y_Divider divider = null;
                    //每一行第一个显示rignt和bottom
                    divider = new Y_DividerBuilder()
                            .setRightSideLine(true, ContextCompat.getColor(getContext(), R.color.color_FF0000), 22, 0, 0)
                            .setBottomSideLine(true, ContextCompat.getColor(getContext(), R.color.color_11FF00), 22, 0, 0)
                            .setLeftSideLine(true, ContextCompat.getColor(getContext(), R.color.color_0066FF), 22, 0, 0)
                            .setTopSideLine(true, ContextCompat.getColor(getContext(), R.color.color_FFC800), 22, 0, 0)
                            .create();
                    return divider;
                }
            };

            return itemDecoration;
        }
    }
}
