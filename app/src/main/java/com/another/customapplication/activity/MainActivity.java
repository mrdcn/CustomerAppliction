package com.another.customapplication.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Element;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.another.customapplication.R;
import com.another.customapplication.view.ColorBorderTextView;

import java.io.IOException;

/**
 * Created by another on 17-2-18.
 */

public class MainActivity extends Activity  {
    private ColorBorderTextView tv;
    private ColorBorderTextView black;
    private ColorBorderTextView white;
    private ColorBorderTextView blue;
    private LinearLayout tvContainer;
    IntentFilter tagDetected;
    IntentFilter tagDetected1;
    IntentFilter tagDetected2;
    IntentFilter tagDetected3;
    PendingIntent pi;
    NfcAdapter nfcAdapter;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pi = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        tagDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        tagDetected.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        tagDetected.addAction(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);



    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.e("intent action",intent.getAction());

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NdefFormatable ndefFormatable = NdefFormatable.get(tag);

        readTagClassic(tag);
        if(tag != null && tag.getId() != null)
        Log.e("nfc tag",readTagClassic(tag));
//        Log.e("nfc tag",bytesToHexString(tag.getId()));
    }

    private String bytesToHexString(byte[] src) {
        return bytesToHexString(src, true);
    }

    public String readTagClassic(Tag tag) {
        boolean auth = false;
        MifareClassic mfc = MifareClassic.get(tag);
        // 读取TAG
        try {
            String metaInfo = "";
            int type = mfc.getType();// 获取TAG的类型
            int sectorCount = mfc.getSectorCount();// 获取TAG中包含的扇区数
            String typeS = "";
            switch (type) {
                case MifareClassic.TYPE_CLASSIC:
                    typeS = "TYPE_CLASSIC";
                    break;
                case MifareClassic.TYPE_PLUS:
                    typeS = "TYPE_PLUS";
                    break;
                case MifareClassic.TYPE_PRO:
                    typeS = "TYPE_PRO";
                    break;
                case MifareClassic.TYPE_UNKNOWN:
                    typeS = "TYPE_UNKNOWN";
                    break;
            }
            metaInfo += "卡片类型：" + typeS + "\n共" + sectorCount + "个扇区\n共"
                    + mfc.getBlockCount() + "个块\n存储空间: " + mfc.getSize()
                    + "B\n";
            for (int j = 0; j < sectorCount; j++) {
                // Authenticate a sector with key A.
                if(!mfc.isConnected())
                mfc.connect();
                auth = mfc.authenticateSectorWithKeyA(j,
                        MifareClassic.KEY_DEFAULT);
                int bCount;
                int bIndex;
                if (auth) {
                    metaInfo += "Sector " + j + ":验证成功\n";
                    // 读取扇区中的块
                    bCount = mfc.getBlockCountInSector(j);
                    bIndex = mfc.sectorToBlock(j);
                    synchronized (this){

                    for (int i = 0; i < bCount; i++) {
                        byte[] data = mfc.readBlock(bIndex);
                        metaInfo += "Block " + bIndex + " : "
                                + bytesToHexString(data) + "\n";
                        bIndex++;
                    }
                    }
                } else {
                    metaInfo += "Sector " + j + ":验证失败\n";
                }
            }
            return metaInfo;
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (mfc != null) {
                try {
                    mfc.close();
                } catch (IOException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                }
            }
        }
        return null;

    }

    private String bytesToHexString(byte[] src, boolean isPrefix) {
        StringBuilder stringBuilder = new StringBuilder();
        if (isPrefix == true) {
            stringBuilder.append("0x");
        }
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.toUpperCase(Character.forDigit(
                    (src[i] >>> 4) & 0x0F, 16));
            buffer[1] = Character.toUpperCase(Character.forDigit(src[i] & 0x0F,
                    16));
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pi,
                new IntentFilter[] { tagDetected }, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }
}
