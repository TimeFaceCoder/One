package cn.timeface.one;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * author: rayboot  Created on 15/12/29.
 * email : sy0725work@gmail.com
 */
public class PicUtil {

    /**
     * print photo in assets 打印assets里的图片
     *
     * @param pic.bmp
     */
    public static Vector<Byte> printPhotoInAssets(Context context, String fileName) {

        AssetManager asm = context.getResources().getAssets();
        InputStream is;
        try {
            is = asm.open(fileName);
            Bitmap bmp = BitmapFactory.decodeStream(is);
            is.close();
            if (bmp != null) {
                byte[] command = decodeBitmap(bmp);
                Vector<Byte> result = new Vector<>(10);
                for (byte b : command) {
                    result.add(b);
                }
                return result;
            } else {
                Log.e("PrintTools", "the file isn't exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
        return null;
    }

    public static Vector<Byte> getInfo3(Context context) {
        Vector<Byte> result = new Vector<>(10);
        Bitmap bitmap = BitmapFactory.decodeFile("/mnt/sdcard/test487.jpg");
        byte[] image = decodeBitmap(bitmap);
        for (byte b : image) {
            result.add(b);
        }
        return result;
    }

    /**
     * decode bitmap to bytes 解码Bitmap为位图字节流
     */
    public static byte[] decodeBitmap(Bitmap bmp) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<String>(); //binaryString list
        StringBuffer sb;

        // 每行字节数(除以8，不足补0)
        int bitLen = bmpWidth / 8;
        int zeroCount = bmpWidth % 8;
        // 每行需要补充的0
        String zeroStr = "";
        if (zeroCount > 0) {
            bitLen = bmpWidth / 8 + 1;
            for (int i = 0; i < (8 - zeroCount); i++) {
                zeroStr = zeroStr + "0";
            }
        }
        // 逐个读取像素颜色，将非白色改为黑色
        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i); // 获得Bitmap 图片中每一个点的color颜色值
                //颜色值的R G B
                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;

                // if color close to white，bit='0', else bit='1'
                if (r > 160 && g > 160 && b > 160)
                    sb.append("0");
                else
                    sb.append("1");
            }
            // 每一行结束时，补充剩余的0
            if (zeroCount > 0) {
                sb.append(zeroStr);
            }
            list.add(sb.toString());
        }
        // binaryStr每8位调用一次转换方法，再拼合
        List<String> bmpHexList = ConvertUtil.binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        // 宽度指令
        int width = bmpWidth % 8 == 0 ? bmpWidth / 8 : (bmpWidth / 8 + 1);
        String widthHexString = String.format("%02x%02x", width % 256, width / 256);
        Log.e("decodeBitmap ", "宽度 width is " + widthHexString);

        // 高度指令
        String heightHexString = String.format("%02x%02x", bmpHeight % 256, bmpHeight / 256);
        Log.e("decodeBitmap ", "高度 height is " + heightHexString);

        List<String> commandList = new ArrayList<String>();
        commandList.add(commandHexString + widthHexString + heightHexString);
        commandList.addAll(bmpHexList);

        return ConvertUtil.hexList2Byte(commandList);
    }
}
