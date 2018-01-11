package cn.a1949science.www.bookrecord.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.willy.ratingbar.ScaleRatingBar;

import java.util.LinkedList;

import cn.a1949science.www.bookrecord.R;
import cn.a1949science.www.bookrecord.bean.BookInfoComment;
import cn.a1949science.www.bookrecord.widget.CircleImageView;

//自定义BaseAdapter
public class CommentAdapter extends BaseAdapter {
    private LinkedList<BookInfoComment> mData;//保存多个读书类
    private Context mContext;

    public CommentAdapter()
    {}
    public CommentAdapter(LinkedList<BookInfoComment> mData, Context context)
    {
        this.mData=mData;
        this.mContext=context;
    }
    public LinkedList<BookInfoComment> getmData() {
        return mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //listview每包含一行就会执行一次getview
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.book_info_list,parent,false);
        //加载对应的各种资源
        CircleImageView userIcon = convertView.findViewById(R.id.headicon);
        TextView userNick = convertView.findViewById(R.id.book_info_usernick);
        ScaleRatingBar userRating= convertView.findViewById(R.id.book_info_rating);
        TextView userCommnent= convertView.findViewById(R.id.book_info_usercomment);
        TextView commentdate= convertView.findViewById(R.id.book_info_commentdate);
        userIcon.setImageBitmap(mData.get(position).getIcon());
        userNick.setText(mData.get(position).getUsernick());
        userRating.setRating(mData.get(position).getRate());
        userCommnent.setText(mData.get(position).getComment());
        commentdate.setText(mData.get(position).getDate());
        return convertView;
    }
}
