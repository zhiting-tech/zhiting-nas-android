package com.zhiting.networklib.utils;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

public class StringUtil {

    /**
     * 占位符显示
     * @param str
     * @param object
     * @return
     */
    public static String getStringFormat(String str, Object... object){
        return String.format(str, object);
    }

    /**
     * long 转 double
     * @param num
     * @return
     */
    public static double long2Double(long num){
        String str = String.valueOf(num);
        return Double.parseDouble(str);
    }

    /**
     * 通过长度截取字符串
     * @param str
     * @param len
     * @return
     */
    public static String subStringByLen(String str, int len, String suffix){
        String result = str.length()<len+1 ? str : str.substring(0,len)+suffix;
        return result;
    }

    /**
     * 用户协议和隐私政策样式
     * @param content
     * @param color
     * @param agreementPolicyListener
     * @return
     */
    public static SpannableStringBuilder setAgreementAndPolicyStyle(String content, @ColorInt int color, AgreementPolicyListener agreementPolicyListener){
        SpannableStringBuilder spannableString = new SpannableStringBuilder();
        spannableString.append(content);

        ClickableSpan agreementClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if (agreementPolicyListener!=null){
                    agreementPolicyListener.onAgreement();
                }
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
                ds.setColor(color);
            }
        };

        ClickableSpan policyClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if (agreementPolicyListener!=null){
                    agreementPolicyListener.onPolicy();
                }
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
                ds.setColor(color);
            }
        };

        int agreementBeginIndex = content.indexOf("《");
        int agreementEndIndex = content.indexOf("》") + 1;
        int policyBeginIndex = content.lastIndexOf("《");
        int policyEndIndex = content.lastIndexOf("》") + 1;
        spannableString.setSpan(agreementClickableSpan, agreementBeginIndex, agreementEndIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(policyClickableSpan, policyBeginIndex, policyEndIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannableString;
    }
}
