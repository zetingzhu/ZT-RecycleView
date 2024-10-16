package com.zzt.act;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinuscxj.recyclDecoration.RvLinearItemDecoration;
import com.dinuscxj.recycleritemdecoration.decor.DividerDrawable;
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

public class ActRvVerV3 extends SingleFragmentActivity {

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, ActRvVerV3.class);
        activity.startActivity(intent);
    }

    @Override
    protected Fragment createFragment() {
        return LinearDividerFragment.newInstance();
    }

    public static class LinearDividerFragment extends RecyclerListFragment {

        public static LinearDividerFragment newInstance() {
            return new LinearDividerFragment();
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getAdapter().addAll(createItemDrawableList());
        }

        private List<ItemDrawable> createItemDrawableList() {
            List<ItemDrawable> itemDrawableList = new ArrayList<>();
            // ItemAnimal
            itemDrawableList.add(new ItemAnimal(R.drawable.ic_animal0, getString(R.string.animal0)));
            itemDrawableList.add(new ItemAnimal(R.drawable.ic_animal1, getString(R.string.animal1)));
            itemDrawableList.add(new ItemAnimal(R.drawable.ic_animal2, getString(R.string.animal2)));
            // ItemCartoon
            itemDrawableList.add(new ItemCartoon(R.drawable.ic_cartoon0, getString(R.string.cartoon0)));
            itemDrawableList.add(new ItemCartoon(R.drawable.ic_cartoon1, getString(R.string.cartoon1)));
            itemDrawableList.add(new ItemCartoon(R.drawable.ic_cartoon2, getString(R.string.cartoon2)));
            // ItemScenic
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
            return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        }

        @Override
        public RecyclerView.ItemDecoration onCreateItemDecoration() {
            return createDividerItemDecoration();
        }

        @NonNull
        private RecyclerView.ItemDecoration createDividerItemDecoration() {
            RvLinearItemDecoration itemDecoration = new RvLinearItemDecoration(getContext(), RvLinearItemDecoration.VERTICAL_TOP_BOTTOM);
            itemDecoration.setDividerColorAndHeight(ContextCompat.getColor(getContext(), R.color.color_11FF00), 10);

//            Y_DividerItemDecoration itemDecoration = new Y_DividerItemDecoration(getContext()) {
//                @Nullable
//                @Override
//                public Y_Divider getDivider(int itemPosition, int childCount) {
//                    Y_Divider divider = null;
//                    divider = new Y_DividerBuilder()
//                            .setBottomSideLine(true, ContextCompat.getColor(getContext(), R.color.color_11FF00), 22, 0, 0)
//                            .create();
//                    return divider;
//                }
//            };

            return itemDecoration;
        }
    }
}
