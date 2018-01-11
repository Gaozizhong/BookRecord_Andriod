package cn.a1949science.www.bookrecord.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.ycl.tabview.library.TabView;
import com.ycl.tabview.library.TabViewChild;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.a1949science.www.bookrecord.R;
import cn.a1949science.www.bookrecord.bean.BookInfo;
import cn.a1949science.www.bookrecord.fragment.ReadingFragment;
import cn.a1949science.www.bookrecord.fragment.SeenFragment;
import cn.a1949science.www.bookrecord.fragment.WantFragment;
import cn.a1949science.www.bookrecord.utils.BookInfoGetFromDouban;
import cn.a1949science.www.bookrecord.utils.HttpUtils;
import cn.a1949science.www.bookrecord.widget.CircleImageView;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, WantFragment.OnFragmentInteractionListener
        ,ReadingFragment.OnFragmentInteractionListener,SeenFragment.OnFragmentInteractionListener {

    Context mContext = MainActivity.this;
    private long exitTime = 0;
    Toolbar toolbar;
    DrawerLayout drawer;
    View headerLayout;
    TabView tabView;
    MaterialSearchView searchView;
    CircleImageView favicon;
    TextView nickname;
    private int REQUEST_CODE = 5,REQUEST_CAMERA_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*沉浸式标题栏*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        initView();
        onClick();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerLayout = navigationView.getHeaderView(0);
        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(mContext,UserInfoActivity.class);
                startActivity(it);
                overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
            }

        });
        nickname = headerLayout.findViewById(R.id.nickname);
        favicon = headerLayout.findViewById(R.id.favicon);
    }

    //点击事件
    private void onClick() {
        //对搜索的文字监听
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Snackbar.make(findViewById(R.id.container), "Query: " + query, Snackbar.LENGTH_LONG)
                        .show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        //
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
            //Do some magic
            }
        });

        //点击事件
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(mContext,"第"+i+"行",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initView() {
        //执行二维码扫描的初始化操作
        ZXingLibrary.initDisplayOpinion(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        //为底部导航栏添加数据源
        List<TabViewChild> tabViewChildList=new ArrayList<>();
        TabViewChild tabViewChild01=new TabViewChild(R.drawable.wanting,R.drawable.wanting,"想读", WantFragment.newInstance("想读","1"));
        TabViewChild tabViewChild02=new TabViewChild(R.drawable.reading,R.drawable.reading,"在读",  ReadingFragment.newInstance("在读","1"));
        TabViewChild tabViewChild03=new TabViewChild(R.drawable.seen,R.drawable.seen,"读过",  SeenFragment.newInstance("读过","1"));
        tabViewChildList.add(tabViewChild01);
        tabViewChildList.add(tabViewChild02);
        tabViewChildList.add(tabViewChild03);
        tabView = findViewById(R.id.tabView);
        tabView.setTabViewChild(tabViewChildList,getSupportFragmentManager());
        searchView = findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        //Add suggestions
        searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));

    }

    //返回键监听事件
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(searchView.isSearchOpen()){
            searchView.closeSearch();
        }//点击两次返回键退出程序
        else if(System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    //toolbar右面按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    //toolbar右面按钮的事件监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        int menuItemId = item.getItemId();
        if (menuItemId == R.id.action_scan) {
            //如果有权限
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent it = new Intent(mContext, CaptureActivity.class);
                startActivityForResult(it, REQUEST_CODE);
            } else {//没有权限
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA_PERMISSION);
            }

        }
        return true;
    }

    //侧滑菜单中的点击事件
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_statistic) {
            // Handle the camera action
        } else if (id == R.id.nav_advice)
        {
            Intent intent = new Intent(mContext,AdviceActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(mContext,AboutUsActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_update) {

        }else if (id == R.id.nav_quit) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    final String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(MainActivity.this, HttpUtils.doGetAsy("https://api.douban.com/v2/book/isbn/" + result), Toast.LENGTH_LONG).show();
                    //开启线程来发起网络请求
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                BookInfo bookInfo = BookInfoGetFromDouban.BookInfoGet(result);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }



}
