package cn.a1949science.www.bookrecord.database;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;

import cn.a1949science.www.bookrecord.bean.BookInfo;
import cn.a1949science.www.bookrecord.bean.ReadInfo;
import cn.a1949science.www.bookrecord.bean._User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 对WantRead列表，reading列表，seen列表进行操作
 * 数据库操作类
 * Created by 高子忠 on 2018/6/19.
 */

public class OperationReadInfo {
    /*需要返回值时调用的Handler*/
    public static Handler bmobHandler;
    /**
     * 为read_info添加一行数据
     * 数据库操作类
     * 返回值 Boolean
     */
    public static Boolean addReadInfo(ReadInfo readInfo) {
        final Boolean[] add = {false};
        readInfo.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if(e==null){
                    //toast();
                    Log.i("bmob","创建数据成功：" + objectId);
                    add[0]=true;
                }else{
                    Log.i("bmob","创建数据失败："+e.getMessage()+","+e.getErrorCode());
                    add[0]=false;
                }
            }
        });
        return add[0];
    }



    /**
     * 查询ReadInfo表中是否存在这条信息
     * 数据库操作类
     * 传入值：_User user, String book_isbn
     * 返回值：ReadInfo对象
     */
    public static void queryReadInfo(_User user, String book_isbn, final Handler handler) {
        final ReadInfo[] readInfo = {new ReadInfo()};


        //--and条件1
        BmobQuery eq1 =new BmobQuery<ReadInfo>();
        eq1.addWhereEqualTo("book_isbn", book_isbn);//ISBN比较
        //--and条件2
        BmobQuery eq2 =new BmobQuery<ReadInfo>();
        eq2.addWhereEqualTo("user_id", user);//用户ID

        //最后组装完整的and条件
        List<BmobQuery<ReadInfo>> andQuerys = new ArrayList<>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);
        //查询符合整个and条件的信息
        BmobQuery<ReadInfo> query = new BmobQuery<>();
        query.and(andQuerys);
        //query.include("user_id");//查询结果包含user_id
        query.findObjects(new FindListener<ReadInfo>() {
            @Override
            public void done(List<ReadInfo> list, BmobException e) {
                if(e==null){
                    /*//解决方法1，用序列化来进行深拷贝
                    try {
                        //序列化
                        ByteArrayOutputStream bots=new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(bots);
                        oos.writeObject(list.get(0));
                        //反序列化
                        ObjectInputStream ois=new ObjectInputStream(new ByteArrayInputStream(bots.toByteArray()));
                        readInfo[0] = (ReadInfo) ois.readObject();
                    } catch (IOException | ClassNotFoundException e1) {
                        e1.printStackTrace();
                    }*/

                    /*//解决方法2，用JSON来进行深拷贝
                    String gao = JSON.toJSONString(list.get(0));
                    readInfo[0] = JSON.parseObject(gao,ReadInfo.class);
                    readInfo[0] = JSON.parseObject(JSON.toJSONString(list.get(0)),ReadInfo.class);
                    readInfoString[0] = JSON.toJSONString(list.get(0));*/

                    //解决方法3，handler+message
                    Message message = handler.obtainMessage();
                    message.what = 0;
                    //以消息为载体
                    message.obj = list;//这里的list就是查询出list
                    //向handler发送消息
                    handler.sendMessage(message);

                    Log.i("bmob","查询成功:"+list.get(0).getObjectId());
                }else{
                    Log.i("bmob","查询失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });


        //return readInfoString[0];
    }




    /**
     * 对ReadInfo表中存在某条信息进行更改
     * 数据库操作类
     * 传入值：带有User_id和Book_isbn以及更新信息的ReadInfo
     * 返回值：Boolean
     */
    public static Boolean updateReadInfo(final ReadInfo readInfo) {
        final Boolean[] update = {false};
        final ReadInfo[] queryResult = {new ReadInfo()};
        @SuppressLint("HandlerLeak")
        //这个应该通过方法里的参数传进来
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        List<ReadInfo> list = (List<ReadInfo>) msg.obj;
                        if (list != null) {
                            queryResult[0] = list.get(0);
                            readInfo.update(queryResult[0].getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        update[0] = true;
                                        Log.i("bmob","更新成功");
                                    }else{
                                        update[0] = false;
                                        Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                                    }
                                }

                            });
                        }
                        break;
                }
            }
        };
        OperationReadInfo.queryReadInfo(readInfo.getUser_id(),readInfo.getBook_isbn(),handler);

        return update[0];
    }

    /**
     * 对ReadInfo表中存在某条信息进行删除
     * 数据库操作类
     * 返回值：Boolean 是否更新
     */
    public static Boolean deleteBookInfo(_User user, String book_isbn) {
        final Boolean[] delete = {false};
        final ReadInfo[] queryResult = {new ReadInfo()};
        @SuppressLint("HandlerLeak")
        //这个应该通过方法里的参数传进来
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        List<ReadInfo> list = (List<ReadInfo>) msg.obj;
                        if (list != null) {
                            queryResult[0] = list.get(0);
                        }
                        break;
                }
            }
        };
        OperationReadInfo.queryReadInfo(user,book_isbn,handler);
        //ReadInfo queryResult = JSON.parseObject(OperationReadInfo.queryReadInfo(user, book_isbn),ReadInfo.class);
        ReadInfo r1=new ReadInfo();
        r1.setObjectId(queryResult[0].getObjectId());
        r1.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    delete[0] = true;
                    Log.i("bmob","成功");

                }else{
                    delete[0] = true;
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
        return delete[0];
    }


    /**
     * 根据阅读状态和user_id查询ReadInfo
     * 输入：read_state和user_id
     * 返回值：List<BookInfo>
     * 数据库操作类
     */
    public static List<BookInfo> queryBookInfoByState(_User user, String read_state) {
        final List<BookInfo> bookInfo = new ArrayList<BookInfo>();
        //--and条件1
        BmobQuery eq1 =new BmobQuery("read_info");
        eq1.addWhereEqualTo("read_state", read_state);//ISBN比较
        //--and条件2
        BmobQuery eq2 =new BmobQuery("read_info");
        eq2.addWhereGreaterThanOrEqualTo("user_id", user);//用户ID

        //最后组装完整的and条件
        List<BmobQuery> andQuerys = new ArrayList<BmobQuery>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);
        //查询符合整个and条件的信息
        BmobQuery query =new BmobQuery("read_info");
        query.and(andQuerys);
        //query.include("user_id");//查询结果包含user_id
        query.findObjectsByTable(new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray jsonArray, BmobException e) {
                if(e==null){
                    List<ReadInfo> list = JSON.parseArray(jsonArray.toString(), ReadInfo.class);
                    for(int i=0;i<list.size();i++){
                        //bookInfo.set(i, OperationBookInfo.queryBookInfo(list.get(i).getBook_isbn()));
                    }

                    Log.i("bmob","查询成功");
                }else{
                    Log.i("bmob","查询失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
        return bookInfo;
    }


}
