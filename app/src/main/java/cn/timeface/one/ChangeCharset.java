package cn.timeface.one;

import android.content.Context;

import com.epson.EpsonCom.EpsonCom;
import com.epson.EpsonCom.EpsonComDevice;
import com.epson.EpsonCom.EpsonComDeviceParameters;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

/**
 * author: rayboot  Created on 15/12/25.
 * email : sy0725work@gmail.com
 */
public class ChangeCharset {
    /**
     * 7位ASCII字符，也叫作ISO646-US、Unicode字符集的基本拉丁块
     */
    public static final String US_ASCII = "US-ASCII";
    /**
     * ISO拉丁字母表 No.1，也叫做ISO-LATIN-1
     */
    public static final String ISO_8859_1 = "ISO-8859-1";
    /**
     * 8 位 UCS 转换格式
     */
    public static final String UTF_8 = "UTF-8";
    /**
     * 16 位 UCS 转换格式，Big Endian(最低地址存放高位字节）字节顺序
     */
    public static final String UTF_16BE = "UTF-16BE";
    /**
     * 16 位 UCS 转换格式，Litter Endian（最高地址存放地位字节）字节顺序
     */
    public static final String UTF_16LE = "UTF-16LE";
    /**
     * 16 位 UCS 转换格式，字节顺序由可选的字节顺序标记来标识
     */
    public static final String UTF_16 = "UTF-16";
    /**
     * 中文超大字符集
     **/
    public static final String GBK = "GBK";

    public static final String GB2312 = "GB2312";

    public static final String GB18030 = "GB18030";

    /**
     * 将字符编码转换成US-ASCII码
     */
    public String toASCII(String str) throws UnsupportedEncodingException {
        return this.changeCharset(str, US_ASCII);
    }

    public String toGB18030(String str) throws UnsupportedEncodingException {
        return this.changeCharset(str, GB18030);
    }

    /**
     * 将字符编码转换成ISO-8859-1
     */
    public String toISO_8859_1(String str) throws UnsupportedEncodingException {
        return this.changeCharset(str, ISO_8859_1);
    }

    /**
     * 将字符编码转换成UTF-8
     */
    public String toUTF_8(String str) throws UnsupportedEncodingException {
        return this.changeCharset(str, UTF_8);
    }

    /**
     * 将字符编码转换成UTF-16BE
     */
    public String toUTF_16BE(String str) throws UnsupportedEncodingException {
        return this.changeCharset(str, UTF_16BE);
    }

    /**
     * 将字符编码转换成UTF-16LE
     */
    public String toUTF_16LE(String str) throws UnsupportedEncodingException {
        return this.changeCharset(str, UTF_16LE);
    }

    /**
     * 将字符编码转换成UTF-16
     */
    public String toUTF_16(String str) throws UnsupportedEncodingException {
        return this.changeCharset(str, UTF_16);
    }

    /**
     * 将字符编码转换成GBK
     */
    public String toGBK(String str) throws UnsupportedEncodingException {
        return this.changeCharset(str, GBK);
    }

    /**
     * 将字符编码转换成GB2312
     */
    public String toGB2312(String str) throws UnsupportedEncodingException {
        return this.changeCharset(str, GB2312);
    }

    /**
     * 字符串编码转换的实现方法
     *
     * @param str        待转换的字符串
     * @param newCharset 目标编码
     */
    public String changeCharset(String str, String newCharset) throws UnsupportedEncodingException {
        if (str != null) {
            //用默认字符编码解码字符串。与系统相关，中文windows默认为GB2312
            byte[] bs = str.getBytes();
            return new String(bs, newCharset);    //用新的字符编码生成字符串
        }
        return null;
    }

    /**
     * 字符串编码转换的实现方法
     *
     * @param str        待转换的字符串
     * @param oldCharset 源字符集
     * @param newCharset 目标字符集
     */
    public String changeCharset(String str, String oldCharset, String newCharset) throws UnsupportedEncodingException {
        if (str != null) {
            //用源字符编码解码字符串
            byte[] bs = str.getBytes(oldCharset);
            return new String(bs, newCharset);
        }
        return null;
    }

    public static void test(Context context) throws UnsupportedEncodingException {

        EpsonComDevice dev = new EpsonComDevice();

        EpsonComDeviceParameters devParams = new EpsonComDeviceParameters();
        devParams.PortType = EpsonCom.PORT_TYPE.ETHERNET;
        devParams.IPAddress = "192.168.10.188";
        devParams.PortNumber = 9100;

        dev.setDeviceParameters(devParams);
        EpsonCom.ERROR_CODE code = dev.openDevice();

        dev.selectAlignment(EpsonCom.ALIGNMENT.CENTER);
        dev.selectPrintDirection(EpsonCom.PRINTDIRECTION.LEFTTORIGHT);
        dev.printString("start", EpsonCom.FONT.FONT_A, true, false, false, false);
        Vector<Byte> test = new Vector<>(10);

        String res = "1、由于我们过于习惯在别人面前戴面具，因此最后导致在自己面前伪装自己。——徐志摩\n" +
                "\n" +
                "　　19、是时候了。好好地做个女人。穿裙子。扎辫子。不和别人吵架。不翘课。不说脏話。——徐志摩";
        byte[] resBytes = res.getBytes(GB18030);

        for (byte b : resBytes) {
            test.add(Byte.valueOf(b));
        }

        code = dev.sendData(test);
        dev.printString("end", EpsonCom.FONT.FONT_A, true, false, false, false);

        dev.cutPaper();
        dev.closeDevice();

    }

}
