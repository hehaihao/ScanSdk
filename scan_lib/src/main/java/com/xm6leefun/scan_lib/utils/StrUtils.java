package com.xm6leefun.scan_lib.utils;


import android.content.ClipboardManager;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串校验工具
 *
 * @author zll
 */
public class StrUtils {
    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

    /**
     *
     * <b>@Description:<b>检验密码是否合格<br/>
     * <b>@param str <b>@return<b>boolean<br/>
     * <b>@Author:<b>ccy<br/>
     * <b>@Since:<b>2014-8-8-上午10:06:31<br/>
     */
    public static boolean validatePassword(String password) {
        if (StrUtils.isEmpty(password)) {
            return false;
        }
        Pattern p = Pattern.compile("^[a-zA-Z0-9!@#$%^&*()_+-=]{6,20}$");
        Matcher m = p.matcher(password);
        return m.find();
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取随机String  (默认取5位)
     *
     * @param len
     * @return
     */
    public static String getRandomString(int len) {
        len = len < 5 ? 5 : len;
        String returnStr = "";
        char[] ch = new char[len];
        Random rd = new Random();
        for (int i = 0; i < len; i++) {
            ch[i] = (char) (rd.nextInt(9) + 65);
            ch[i] = encodeTable[rd.nextInt(36)];
        }
        returnStr = new String(ch);
        return returnStr;
    }

    private static final char[] encodeTable = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9'};

    public static void main(String[] args) {
        /*int sdafafagd231 = toHash("sdafafagd2312");
        System.out.print(sdafafagd231);*/
    }

    /**
     * 将字符串转化为hash值
     *
     * @param key
     * @return
     */
    public static String StringtoGBK(String key) {
        String t = "这是一个字符串aaa111";

        String utf8 = null;
        try {
            utf8 = new String(t.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//        System.out.println(utf8);

        String unicode = null;
        try {
            unicode = new String(utf8.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//        System.out.println(unicode);

        String gbk = null;
        try {
            gbk = new String(unicode.getBytes("GBK"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return gbk;
    }

    public static int toHash(String key) {
        int arraySize = 11113; // 数组大小一般取质数
        int hashCode = 0;
        for (int i = 0; i < key.length(); i++) { // 从字符串的左边开始计算
            int letterValue = key.charAt(i) - 96;// 将获取到的字符串转换成数字，比如a的码值是97，则97-96=1
            // 就代表a的值，同理b=2；
            hashCode = ((hashCode << 5) + letterValue) % arraySize;// 防止编码溢出，对每步结果都进行取模运算
        }
        return hashCode;
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailer.matcher(email).matches();
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     * @author ccy
     */
    public static int toInt(String obj) {
        try {
            return Integer.parseInt(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     * @author ccy
     */
    public static float toFloat(String obj) {
        try {
            return Float.parseFloat(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 字符串转布尔值
     *
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 将一个InputStream流转换成字符串
     *
     * @param is
     * @return
     */
    public static String toConvertString(InputStream is) {
        StringBuffer res = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader read = new BufferedReader(isr);
        try {
            String line;
            line = read.readLine();
            while (line != null) {
                res.append(line);
                line = read.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != isr) {
                    isr.close();
                    isr.close();
                }
                if (null != read) {
                    read.close();
                    read = null;
                }
                if (null != is) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
            }
        }
        return res.toString();
    }

    /**
     * @param str
     * @return boolean <br>
     * @Title: isNullOrEmpty <br>
     * @description: 判断是否为null或空值 <br>
     * @author limingliang <br>
     * @date 2014年6月11日-上午10:53:03 <br>
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * @param str 待判断字符串<br>
     * @return String 判断后的字符串<br>
     * @Title: getString <br>
     * @description: 判断字符串是否为空，为空则返回一个空值，不为空则返回原字符串 <br>
     * @author limingliang <br>
     * @date 2014年6月11日-上午10:54:21 <br>
     */
    public static String getString(String str) {
        return str == null ? "" : str;
    }

    /**
     * <b>@Description:<b>判断是不是合法手机 手机号码<br/>
     * <b>@param handset <b>@return<b>boolean<br/>
     * <b>@Author:<b>ccy<br/>
     * <b>@Since:<b>2014-7-30-上午11:44:56<br/>
     */
    public static boolean isHandset(String handset) {
        try {
            if (!handset.substring(0, 1).equals("1")) {
                return false;
            }
            if (handset == null || handset.length() != 11) {
                return false;
            }
            String check = "^[0123456789]+$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(handset);
            boolean isMatched = matcher.matches();
            if (isMatched) {
                return true;
            } else {
                return false;
            }
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static boolean isCorrectPhone(String mobileNums) {
        if (mobileNums == null || mobileNums.length() != 11) {
            return false;
        }
        // "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String telRegex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$";
//		if (StrUtils.isEmpty(mobileNums))
//			return false;
//		else
        return mobileNums.matches(telRegex);
    }

    /**
     * <b>@Description:<b>判断输入的字符串是否为纯汉字<br/>
     * <b>@param str <b>@return<b>boolean<br/>
     * <b>@Author:<b>ccy<br/>
     * <b>@Since:<b>2014-7-30-上午11:45:37<br/>
     */
    public static boolean isChinese(String str) {
        Pattern pattern = Pattern.compile("[\u0391-\uFFE5]+$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断输入的字符串是否为英文小写字母
     */
    public static boolean isLowerChar(String str) {
//        return str.matches("[a-zA-Z]+");
        return str.matches("[a-z]+");
    }

    /**
     * 判断输入的字符串是否为英文字母
     */
    public static boolean isAllChar(String str) {
        return str.matches("[a-zA-Z]+");
    }

    /**
     * <b>@Description:<b>检验密码是否合格<br/>
     * <b>@param str <b>@return<b>boolean<br/>
     * <b>@Author:<b>ccy<br/>
     * <b>@Since:<b>2014-8-8-上午10:06:31<br/>
     */
    public static boolean isValidatePassword(String password) {
        if (StrUtils.isEmpty(password)) {
            return false;
        }
//		Pattern p = Pattern.compile("^[a-zA-Z0-9!@#$%^&*()_+-=]{8,16}$");
//		Pattern p = Pattern.compile("^(?![0-9])(?![0-9]+$)(?![a-zA-Z]+$)[a-zA-Z0-9!@#$%^&*()_+-=]{6,20}$");
        Pattern p = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[a-zA-Z0-9?!@#$%^&*()_+-=.“”，。、？！`~￥/<>\"|,]{6,20}$");
        Matcher m = p.matcher(password);
        boolean b = m.find();
        return b;
    }

    /**
     * 设置支付密码
     * 必须含有数字和字母，可以包含其他符号，6-20位
     * ^ 匹配一行的开头位置
     * (?![0-9]+$) 预测该位置后面不全是数字
     * (?![a-zA-Z]+$) 预测该位置后面不全是字母
     * [0-9A-Za-z] {6,20} 由6-20位数字或着字母组成
     * $ 匹配行结尾位置
     * @param payPwd
     * @return
     */
    public static boolean isValidatePayPwd(String payPwd) {
        if (StrUtils.isEmpty(payPwd)) {
            return false;
        }
//		Pattern p = Pattern.compile("^[a-zA-Z0-9!@#$%^&*()_+-=]{8,16}$");
//		Pattern p = Pattern.compile("^(?![0-9])(?![0-9]+$)(?![a-zA-Z]+$)[a-zA-Z0-9!@#$%^&*()_+-=]{6,20}$");
//		Pattern p = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[a-zA-Z0-9][@#￥$%^&*()_+-=.“”，。、？！`~￥/<>\"|,]{6,20}$");
//        Pattern p = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$");
        Pattern p = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)(?![@#￥$%^?!&*()_+\\-=.“”，。、？！`~/<>\"|,\\\\]+$)(?![a-zA-Z@#￥$%^?!&*()_+\\-=.“”，。、？！`~/<>\"|,\\\\]+$)(?![0-9@#￥$%^?!&*()_+\\-=.“”，。、？！`~￥/<>\"|,\\\\]+$)[a-zA-Z0-9@#￥$%^?!&*()_+\\-=.“”，。、？！`~￥/<>\"|,\\\\]{6,20}$");
        Matcher m = p.matcher(payPwd);
        boolean b = m.find();
        return b;
    }

    /**
     * <b>@Description:<b>检验姓名是否合格<br/>
     * <b>@param name <b>@return<b>boolean<br/>
     * <b>@Author:<b>ccy<br/>
     * <b>@Since:<b>2014-8-8-上午10:16:36<br/>
     */
    public static boolean isValidatenName(String name) {
        if (StrUtils.isEmpty(name)) {
            return false;
        }
        Pattern p = Pattern.compile("^[a-zA-Z0-9]{8,16}$");
        Matcher m = p.matcher(name);
        return m.find();
    }

    /**
     * 检验是否是正确的账号格式
     * 5-32位，由大小写字母和数字组成
     *
     * @param account
     * @return
     */
    public static boolean isValidatenAccount(String account) {
        if (StrUtils.isEmpty(account)) {
            return false;
        }
        Pattern p = Pattern.compile("^[a-zA-Z0-9_]{5,32}$");
        Matcher m = p.matcher(account);
        return m.find();
    }

    /**
     * 判断字符串是否是数字
     */
    public static boolean isNumber(String value) {
        return /*isInteger(value) ||*/ isDouble(value) || isLong(value);
    }

    /**
     * 判断字符串是否是整数
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否是长整型
     */
    public static boolean isLong(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否是浮点数
     */
    private static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            if (value.contains("."))
                return true;
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 去除后部多余的0
     */
    public static String removeZero(String bd) {
        BigDecimal bigDecimal = new BigDecimal(bd);
        String inputStr = bigDecimal.stripTrailingZeros().toPlainString();  // 去除小数点后末尾的0
        return inputStr;
    }

    /**
     * 复制字符串
     */
    public static void copyStrings(Context context, String string) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            clipboardManager.setText(string);
        }
    }

    /**
     * 判断传入的地址字符串是否为主网钱包的地址（以“oc”开头并且长度为42 [ 原地址长度40，前缀“oc”2 ]），是则返回该地址，否则加上“oc”再返回
     */
    public static String getOCWalletAddress(String address) {
        if (!isEmpty(address)) {
            if (address.startsWith("oc") && address.length() == 42) {
                return address;
            } else {
                address = "oc" + address;
                return address;
            }
        } else {
            return "无效的钱包地址(Invalid Wallet Address)";
        }
    }
    public static String get0XWalletAddress(String address) {
        if (!isEmpty(address)) {
            if (address.startsWith("0x") && address.length() == 42) {
                return address;
            } else {
                address = "0x" + address;
                return address;
            }
        } else {
            return "无效的钱包地址(Invalid Wallet Address)";
        }
    }

    /**
     * 判断传入的地址字符串是否为主网钱包的地址（以“oc”开头并且长度为42 [ 原地址长度40，前缀“oc”2 ]）
     */
    public static boolean isOCWalletAddress(String address) {
        try {
            if (isEmpty(address)) {
                return false;
            } else if (!checkAddress(address)) {
                return false;
            } else
                return address.startsWith("oc") && address.length() == 42;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String[] list2StringArray(List<String> souceData) {
        return souceData.toArray(new String[souceData.size()]);
    }

    // 获取不带groupId的群成员id
    public static String getMemberId(String groupId, String memberId) {
        if (!isEmpty(memberId) && !isEmpty(groupId)) {
            if (memberId.startsWith(groupId.substring(0, 13))) {
                return memberId.replace(groupId, "");
            } else {
                return memberId;
            }
        } else {
            return memberId;
        }
    }

    // 获取带groupId的群成员id
    public static String getGroupMemberId(String groupId, String memberId) {
        if (!isEmpty(memberId) && !isEmpty(groupId)) {
            if (memberId.startsWith(groupId.substring(0, 13))) {
                return memberId;
            } else {
                return groupId + memberId;
            }
        } else {
            return "";
        }
    }

    public static String getPwdMD5(String pwd) {
        if (!isEmpty(pwd))
            pwd = MD5Util.string2MD5(MD5Util.string2MD5(pwd) + "mding");
        return pwd;
    }

    /**
     * 检查是否属于OC合法地址
     * @param input
     * @return
     */
    public static boolean checkAddress(String input) {
        if (input.length() == 42) {
            String head = input.substring(0, 2).toLowerCase();
            String body = input.substring(2, input.length()).toLowerCase();
            String regex = "^[A-Fa-f0-9]+$";
            if (head.equals("oc") && body.matches(regex))
                return true;
        }
        return false;
    }
    /**
     * 检查是否属于0x合法地址
     * @param input
     * @return
     */
    public static boolean check0XAddress(String input) {
        if (input.length() == 42) {
            String head = input.substring(0, 2).toLowerCase();
            String body = input.substring(2, input.length()).toLowerCase();
            String regex = "^[A-Fa-f0-9]+$";
            if (head.equals("0x") && body.matches(regex))
                return true;
        }
        return false;
    }

    /**
     * 检查是否属于ETH合法地址
     * @param input
     * @return
     */
    public static boolean checkAddressEth(String input) {
        if (input.length() == 42) {
            String head = input.substring(0, 2).toLowerCase();
            String body = input.substring(2, input.length()).toLowerCase();
            String regex = "^[A-Fa-f0-9]+$";
            if (head.equals("0x") && body.matches(regex))
                return true;
        }
        return false;
    }

    /**
     * 对字符串进行Base64编码
     */
    public static String str2Base64Encode(String str) {
        String encode = Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
        Log.d("",str + "加密后为：" + encode);
        return encode;
    }

    /**
     * 对字符串进行Base64解码
     */
    public static String base642StrDecode(String str) {
        String decode = new String(Base64.decode(str, Base64.DEFAULT));
        Log.d("",str + "解密后为：" + decode);
        return decode;
    }

    /**
     * 获取上传的Base64私钥
     */
    public static String getBase64PrivateKey(String plainPriveteKey) {
        String pk1 = plainPriveteKey.substring(0, 16);
        String pk2 = plainPriveteKey.substring(16, 32);
        String pk3 = plainPriveteKey.substring(32, 48);
        String pk4 = plainPriveteKey.substring(48, 64);

        String beforeBase64 = pk1 + pk3 + pk2 + pk4;
        String encode = StrUtils.str2Base64Encode(beforeBase64);
       /* String decode = StrUtils.base642StrDecode(encode);
        String pk01 = decode.substring(0, 16);
        String pk02 = decode.substring(32, 48);
        String pk03 = decode.substring(16, 32);
        String pk04 = decode.substring(48, 64);
        String afterBase64 = pk01 + pk02 + pk03 + pk04;*/
/*        String simpleAfterBase64 = privateKey.replace(pk2 + pk3, pk3 + pk2);
        AppConfig.logs(simpleAfterBase64);
        AppConfig.logs(encode + "===encode=================decode===" + decode + "===simpleAfterBase64===" + simpleAfterBase64);*/
        Log.d("","===encode===" + encode);
        return encode;
    }

    /**
     * 不显示科学计数法
     * @param d
     * @return
     */
    public static String formatDouble(double d) {
        NumberFormat nf = NumberFormat.getInstance();
        //设置保留多少位小数
        nf.setMaximumFractionDigits(8);
        // 取消科学计数法
        nf.setGroupingUsed(false);
        //返回结果
        String result = nf.format(d);
//        System.out.println("不显示科学计数法---结果===" + result);
        return result;
    }

    public static String formatLong(long l, int scale) {
        if(l == 0L){
            return "0";
        }
        BigDecimal result = new BigDecimal(l).divide(new BigDecimal("100000000"));
//        System.out.println(result);
        BigDecimal bigDecimal = result.setScale(scale, BigDecimal.ROUND_DOWN);
        String plainString = bigDecimal.stripTrailingZeros().toPlainString();  // 去除小数点后末尾的0
//        System.out.println(plainString);
        return plainString;
    }

    public static String formatEthLong(long l, int scale) {
        if(l == 0L) {
            return "0";
        }
        BigDecimal result = new BigDecimal(l).divide(new BigDecimal("1000000000"));
//        System.out.println(result);
        BigDecimal bigDecimal = result.setScale(scale, BigDecimal.ROUND_DOWN);
        String plainString = bigDecimal.stripTrailingZeros().toPlainString();  // 去除小数点后末尾的0
//        System.out.println(plainString);
        return plainString;
    }

    /**
     * 判断是否是链接
     * @param url
     * @return
     */
    public static boolean isNode(String url){
        String regex = "^((https|http|ftp|rtsp|mms)?://)"  //https、http、ftp、rtsp、mms
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 例如：199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // first level domain- .com or .museum
                + "(:[0-9]{1,5})?" // 端口号最大为65535,5位数
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        Pattern pattern = Pattern.compile(regex);
        if (pattern.matcher(url).matches()) {
//            System.out.println("是正确的网址");
            return true;
        } else {
//            System.out.println("非法网址");
            return false;
        }
    }

}
