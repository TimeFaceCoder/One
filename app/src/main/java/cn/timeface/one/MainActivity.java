package cn.timeface.one;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.epson.EpsonCom.EpsonCom;
import com.epson.EpsonCom.EpsonComDevice;
import com.epson.EpsonCom.EpsonComDeviceParameters;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    EpsonComDevice dev = new EpsonComDevice();
    ImageView imageView;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        imageView = (ImageView) findViewById(R.id.imageView);
        setSupportActionBar(toolbar);
        TextView tvContent = (TextView) findViewById(R.id.tvContent);
        tvContent.setText(NetWorkUtils.getLocalIpAddress(this));


        EpsonComDeviceParameters devParams = new EpsonComDeviceParameters();
        devParams.PortType = EpsonCom.PORT_TYPE.ETHERNET;
        devParams.IPAddress = "192.168.10.188";
        devParams.PortNumber = 9100;
        dev.setDeviceParameters(devParams);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printImage();
            }
        });
    }

    public void printText(String text) throws UnsupportedEncodingException {
        Vector<Byte> result = new Vector<>();
        byte[] resBytes = text.getBytes("GB18030");

        for (byte b : resBytes) {
            result.add(b);
        }
        EpsonCom.ERROR_CODE code = dev.openDevice();
        code = dev.selectAlignment(EpsonCom.ALIGNMENT.LEFT);
        code = dev.sendData(result);
        cutPaper();
    }

    public void printImage() {
        RxPermissions.getInstance(getApplication())
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(
                        new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {

                                Log.e("111111", "printImage: 0 start");
                                EpsonCom.ERROR_CODE code = dev.openDevice();
                                Log.e("111111", "printImage: 1" + code);
                                dev.selectAlignment(EpsonCom.ALIGNMENT.CENTER);
                                code = dev.sendData(PicUtil.getInfo3(getApplicationContext()));
                                Log.e("111111", "printImage: 2" + code);
                                cutPaper();
                            }
                        }
                );

    }

    private void cutPaper() {
        EpsonCom.ERROR_CODE code;
        if (!dev.isDeviceOpen()) {
            code = dev.openDevice();
        }
        code = dev.cutPaper();
        if (code != EpsonCom.ERROR_CODE.SUCCESS) {
            code = dev.resetDevice();
            dev.cutPaper();
        }
    }
}
