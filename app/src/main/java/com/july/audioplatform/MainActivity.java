package com.july.audioplatform;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Button bt_start,bt_stop;
    private TextView xvalue,yvalue,zvalue;
    private Chronometer ch;
    private SensorManager sensorManager;
    private Float[] acceleration=new Float[3];
    BufferedWriter bwx,bwy,bwz;
    File path;
    private SensorEventListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listener=this;

        bt_start=(Button)findViewById(R.id.bt_start);
        bt_stop=(Button)findViewById(R.id.bt_stop);
        xvalue=(TextView)findViewById(R.id.tv_x_value);
        yvalue=(TextView)findViewById(R.id.tv_y_value);
        zvalue=(TextView)findViewById(R.id.tv_z_value);
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        ch=(Chronometer)findViewById(R.id.timer);

        //权限检测
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        //文件检测
        final File file=new File(FileUtil.absolutePath);
        if(!file.exists())
            file.mkdir();



        //监听器
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sensorManager.registerListener(listener,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_FASTEST);
                ch.setBase(SystemClock.elapsedRealtime());
                ch.start();

                String date=FileUtil.getTime();
                path=new File(FileUtil.absolutePath,date);
                if(!path.exists())
                    path.mkdir();
                try{
                    bwx=new BufferedWriter(new FileWriter(new File(path,"x.txt")));
                    bwy=new BufferedWriter(new FileWriter(new File(path,"y.txt")));
                    bwz=new BufferedWriter(new FileWriter(new File(path,"z.txt")));
                }catch (IOException e){
                    Toast.makeText(getApplicationContext(),"Acceleration IOException!",Toast.LENGTH_SHORT);
                }
                bt_start.setBackgroundColor(getResources().getColor(R.color.grey));
                bt_start.setEnabled(false);
            }
        });
        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensorManager.unregisterListener(listener);
                bt_start.setEnabled(true);
                bt_start.setBackgroundColor(getResources().getColor(R.color.green));
                ch.stop();
                if(bwx!=null){
                    try {
                        bwx.flush();
                        bwx.close();
                    }catch (IOException e){

                    }
                }
                if(bwy!=null){
                    try {
                        bwy.flush();
                        bwy.close();
                    }catch (IOException e){

                    }
                }
                if(bwz!=null){
                    try {
                        bwz.flush();
                        bwz.close();
                    }catch (IOException e){

                    }
                }
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri=Uri.fromFile(new File(path,"x.txt"));
                intent.setData(uri);
                getApplicationContext().sendBroadcast(intent);
                uri=Uri.fromFile(new File(path,"y.txt"));
                intent.setData(uri);
                getApplicationContext().sendBroadcast(intent);
                uri=Uri.fromFile(new File(path,"z.txt"));
                intent.setData(uri);
                getApplicationContext().sendBroadcast(intent);
            }

        });

        ch.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if(SystemClock.elapsedRealtime()-ch.getBase()>3600*1000){
                    ch.stop();
                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    int i = 0;
                }
                else{
                    Toast.makeText(this,"You denied the permission!",Toast.LENGTH_SHORT);
                    finish();
                }
            case 2:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                }
                else{
                    Toast.makeText(this,"You denied the permission!",Toast.LENGTH_SHORT);
                    finish();
                }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                final float alpha = (float) 0.8;
                acceleration[0]=sensorEvent.values[0];
                acceleration[1]=sensorEvent.values[1];
                acceleration[2]=sensorEvent.values[2];
                xvalue.setText(String.valueOf(acceleration[0]));
                yvalue.setText(String.valueOf(acceleration[1]));
                zvalue.setText(String.valueOf(acceleration[2]));
                try {
                    bwx.write(String.valueOf(acceleration[0]) + "\r\n");
                    bwy.write(String.valueOf(acceleration[1]) + "\r\n");
                    bwz.write(String.valueOf(acceleration[2]) + "\r\n");
                }catch (IOException e){

                }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
