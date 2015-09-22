package com.hyh.www.adapter;
import java.util.ArrayList;

import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.entity.PageList;
import com.hyh.www.R;
import com.hyh.www.entity.Emotion;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
/**
 * 
 * @author xiaobai
 * 2014-10-21
 * @todo( 表情适配器 )
 */
public class FaceAdapter extends BasicAdapter {

    private ArrayList<GezitechEntity_I> data = null;

    private LayoutInflater inflater;
    public FaceAdapter(Context context,ArrayList<GezitechEntity_I> list) {
        this.inflater=LayoutInflater.from(context);
        this.data = list;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	Emotion emoji= (Emotion)data.get(position);
        ViewHolder viewHolder=null;
        if(convertView == null) {
            viewHolder=new ViewHolder();
            convertView=inflater.inflate(R.layout.item_face, null);
            viewHolder.iv_face=(ImageView)convertView.findViewById(R.id.item_iv_face);
            convertView.setTag(viewHolder);
        } else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        if(emoji.drawable == R.drawable.face_del_icon_select) {
            convertView.setBackgroundResource(0);
            viewHolder.iv_face.setImageResource(emoji.drawable);
        } else if(TextUtils.isEmpty(emoji.title)) {
            convertView.setBackgroundResource(0);
            viewHolder.iv_face.setImageDrawable(null);
        } else {
            viewHolder.iv_face.setTag(emoji);
            viewHolder.iv_face.setImageResource( emoji.drawable );
        }

        return convertView;
    }

    class ViewHolder {

        public ImageView iv_face;
    }
}