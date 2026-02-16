package com.example.myhealthlife.ecg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myhealthlife.R;
import com.example.myhealthlife.ecg.adapter.EcgHisListAdapter;
import com.example.myhealthlife.ecg.bean.EcgSyncListResponse;
import com.example.myhealthlife.ecg.util.SharedPreferencesUtil;
import com.example.myhealthlife.ecg.view.Cardiograph2View;
import com.example.myhealthlife.domain.util.ToastUtil;
import com.example.myhealthlife.views.NavigationBar;
import com.google.gson.Gson;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.yucheng.ycbtsdk.YCBTClient;
import com.yucheng.ycbtsdk.bean.AIDataBean;
import com.yucheng.ycbtsdk.AITools;
import com.yucheng.ycbtsdk.response.BleAIDiagnosisResponse;
import com.yucheng.ycbtsdk.response.BleDataResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

public class EcgActicvity extends Activity {
    private SmartRefreshLayout refreshLayout;
    private TextView tvStartEcg;
    private Cardiograph2View cardiographView;
    private ListView listView;
    private List<String> lists = new ArrayList<>();
    private NavigationBar bar;
    private ProgressDialog progressDialog;
    private EcgHisListAdapter adapter;
    private static final String ECG_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    dismisDialog(1);
                    break;
                case 2:
                    dismisDialog(2);
                    break;
                case 3:
                    dismisDialog(2);
                    //upDateSyncData();
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg);
        Log.d("ECG_LOG", "inicia");
        init();
        initListener();
    }

    private void init() {
        bar = findViewById(R.id.navigationbar);
        tvStartEcg = findViewById(R.id.tv_start_button);
        cardiographView = findViewById(R.id.cardiograph2View);
        listView = findViewById(R.id.ls_view);
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setEnableRefresh(true); //是否启用下拉刷新功能
        refreshLayout.setEnableLoadMore(false); //是否启用上拉加载功能
        adapter = new EcgHisListAdapter(EcgActicvity.this, lists);
        listView.setAdapter(adapter);
    }

    private void initListener() {
        bar.showLeftbtn(0);
        bar.setLeftOnClickListener(new NavigationBar.MyOnClickListener() {
            @Override
            public void onClick(View btn) {
                finish();
            }
        });
        bar.setTitle("ECG");
        tvStartEcg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EcgActicvity.this, EcgMeasureActivity.class));
            }
        });
        adapter.setOnCall(new EcgHisListAdapter.OnCall() {
            @Override
            public void setInfo(View v, int position) {
                int id = v.getId();

                if (id == R.id.tv_jilu) {
                    updateEcgGraph(lists.get(position));
                } else if (id == R.id.tv_jiance) {

                    Bitmap bitmap = getBitmapFromView(cardiographView);
                    Uri uri = saveBitmapAndGetUri(bitmap);

                    if (uri != null) {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("image/png");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Resultado del ECG");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        startActivity(Intent.createChooser(shareIntent, "Compartir ECG"));
                    }

                } else if (id == R.id.btn_sycn) {
                    //AQUI HARIA FALTA ACTUALIZAR
                }
            }
        });
    }

    private void initData() {
        Log.e("ECG_DEBUG", "initData() - reading local ECG list");

        List<String> localList = SharedPreferencesUtil.readEcgList(this);

        if (localList == null) {
            localList = new ArrayList<>();
        }

        lists.clear();
        lists.addAll(localList);

        Log.e("ECG_DEBUG", "ECG local count = " + lists.size());

        adapter.notifyDataSetChanged();

        List<Integer> blist = new ArrayList<>();
        List<Integer> blist_change = new ArrayList<>();
        List<String> strs = SharedPreferencesUtil.readEcgList(this);

        // Mostrar preview del último ECG
        if (strs != null && strs.size() > 0) {
            blist.addAll(SharedPreferencesUtil.readEcgListMsg(strs.get(0), this));
            if (blist.size() > 280) {
                for (int i = 280; i < blist.size(); i++) {
                    blist_change.add(blist.get(i));
                }
            } else {
                blist_change.addAll(blist);
            }
            cardiographView.setDatas(blist_change, true);
            cardiographView.invalidate();
        }
    }

/*    private void upDateSyncData() {
        lists.clear();
        Log.d("ECG_DEBUG", "datas size: " + (datas == null ? "null" : datas.size()));

        for (EcgSyncListResponse.DataBean d : datas) {
            lists.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new Date(d.collectStartTime)));
        }

        adapter.setDataChanged(lists);
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
        }
    }*/

    private void syncEcgListData(int index, long startTime, long sendTime) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, getString(R.string.prompt), getString(R.string.ecg_sync_data), true, false);
        } else {
            progressDialog.show();
        }
        YCBTClient.collectEcgDataWithIndex(index, new BleDataResponse() {
            @Override
            public void onDataResponse(int code, float ratio, HashMap resultMap) {
                if (code == 0 && resultMap.get("data") != null) {
                    byte[] ints = (byte[]) resultMap.get("data");
                    List<Integer> mEcgMeasureList = AITools.getInstance().ecgRealWaveFiltering(ints);
                    SharedPreferencesUtil.saveEcgList(person(mEcgMeasureList),
                            new SimpleDateFormat(ECG_TIME_FORMAT).format(new Date(startTime)),
                                    EcgActicvity.this);
                    getAIDiagnosisResult(startTime, sendTime);
                } else {
                    handler.sendEmptyMessage(1);
                }
            }
        });
    }

    private List<Integer> person(List<Integer> datas) {
        List<Integer> lists = new ArrayList<>();
        int index = 0;
        int value = 0;
        for (Integer data : datas) {
            value += data;
            index++;
            if (index % 3 == 0) {
                value = value / 40 / 3;
                value = (value > 500 ? 500 : value);
                value = (value < -500 ? -500 : value);
                lists.add(value);
            }
        }
        return lists;
    }

    /*
    * 诊断结果  诊断界面用来展示
    * */
    private void getAIDiagnosisResult(long startTime, long sendTime) {
        AITools.getInstance().getAIDiagnosisResult(new BleAIDiagnosisResponse() {
            @Override
            public void onAIDiagnosisResponse(AIDataBean aiDataBean) {
                if (aiDataBean != null) {
                    short heart = aiDataBean.heart;//心率
                    int mDiagnoseType = aiDataBean.qrstype;//类型 1正常心拍 5室早心拍 9房早心拍  14噪声
                    boolean isAfib = aiDataBean.is_atrial_fibrillation;//是否心房颤动
                    System.out.println("chong------heart==" + heart + "--qrstype==" + mDiagnoseType + "--is_atrial_fibrillation==" + isAfib);
                    deleteEcgInfo(sendTime);
                } else {
                    handler.sendEmptyMessage(1);
                }
            }
        });
    }

    private void deleteEcgInfo(long sendTime) {
        // 数据库存储成功后 删除手环数据
        YCBTClient.deleteHistoryListData(0, sendTime, new BleDataResponse() {
            @Override
            public void onDataResponse(int code, float ratio, HashMap resultMap) {
                if (code == 0) {
                    handler.sendEmptyMessage(2);
                } else {
                    handler.sendEmptyMessage(1);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("ECG_DEBUG", "onResume()");
        initData();
    }

    private void dismisDialog(int type) {
        Log.d("ECG_DEBUG", "dismisDialog");
        if (!EcgActicvity.this.isFinishing() && progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            if (type == 1) {
                ToastUtil.getInstance(EcgActicvity.this).toast(getString(R.string.ecg_sync_data_failed));
            } else if (type == 2) {
                ToastUtil.getInstance(EcgActicvity.this).toast(getString(R.string.ecg_sync_data_success));
            }
        }
    }

    private void updateEcgGraph(String timeKey) {
        Log.e("ECG_DEBUG", "Updating ECG graph for: " + timeKey);

        List<Integer> blist =
                SharedPreferencesUtil.readEcgListMsg(timeKey, this);

        if (blist == null || blist.isEmpty()) {
            Log.e("ECG_DEBUG", "No ECG data found for " + timeKey);
            return;
        }

        List<Integer> blist_change = new ArrayList<>();

        if (blist.size() > 280) {
            blist_change.addAll(blist.subList(blist.size() - 280, blist.size()));
        } else {
            blist_change.addAll(blist);
        }

        cardiographView.setDatas(blist_change, true);
        cardiographView.invalidate();
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(
                view.getWidth(),
                view.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private Uri saveBitmapAndGetUri(Bitmap bitmap) {
        File cachePath = new File(getCacheDir(), "images");
        cachePath.mkdirs();

        File file = new File(cachePath, "ecg_graph.png");
        FileOutputStream stream;

        try {
            stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                file
        );
    }



}
