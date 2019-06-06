package com.johnhao.wechatapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import com.johnhao.wechatapp.MainActivity;
import com.johnhao.wechatapp.R;
import java.util.ArrayList;
import java.util.List;


public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private List<TextContent> mDatas;
    private PopupWindowList mPopupWindowList;
    private MainActivity context;


    public DataAdapter(MainActivity context,List<TextContent> list) {
        this.context = context;
        this.mDatas = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.dataView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int pos = holder.getAdapterPosition();
                showPopWindows(v, pos);
                Log.d("Adapter", "onLongClick: ");
                return false;
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder holder, int position) {
        TextContent textContent = mDatas.get(position);
        holder.target.setText(textContent.getTargetText());
        holder.replace.setText(textContent.getReplaceText());
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View dataView;
        public TextView target;
        public TextView replace;

        public ViewHolder(View itemView) {
            super(itemView);
            dataView = itemView;
            target = itemView.findViewById(R.id.data_target);
            replace = itemView.findViewById(R.id.data_replace);
        }
    }

    // 在对应位置增加一个item
    public void addData(int position, TextContent textContent) {
        mDatas.add(position, textContent);
        notifyItemInserted(position);
    }

    // 删除对应item
    public void removeData(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    public void changeData(int position, TextContent textContent) {
        mDatas.set(position, textContent);
        notifyItemChanged(position);
    }

    private void showPopWindows(View view, final int pos) {
        List<String> dataList = new ArrayList<>();
        dataList.add("删除");
        dataList.add("修改");

        if (mPopupWindowList == null) {
            mPopupWindowList = new PopupWindowList(view.getContext());
        }
        mPopupWindowList.setAnchorView(view);
        mPopupWindowList.setItemData(dataList);
        mPopupWindowList.setModal(true);
        mPopupWindowList.show();
        mPopupWindowList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Adapter", "onItemClick: " + position);
                switch (position) {
                    case 0:
                        TextContent textContent = mDatas.get(pos);

                        // 删掉db中的数据
                        context.deleteData(textContent.getTargetText());

                        // 删掉adapter中数据
                        removeData(pos);
                        break;
                    case 1:
                        textContent = mDatas.get(pos);
                        String target = textContent.getTargetText();
                        String replace = textContent.getReplaceText();

                        // 删掉db中的数据
                        context.deleteData(target);

                        // 设置EditTextView中的内容
                        context.setInputText(target, replace, pos);

                        // 删掉adapter中数据
                        removeData(pos);
                        break;
                }
                mPopupWindowList.hide();
            }
        });
    }


}
