package com.yisuho.caumenu;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xon23 on 2016-08-23.
 */
public class CustomBodyStringRequest extends StringRequest {
    String mRequestBody;
    public CustomBodyStringRequest(int method, String url, String buildingCode, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String today = simpleDateFormat.format(date);
        this.mRequestBody = "<map><calvalue value='0'/><today value='" + today +"'/><store value='" + buildingCode +"'/></map>";
//        this.mRequestBody = "<map><calvalue value='0'/><today value='" + 20170113 +"'/><store value='" + buildingCode +"'/></map>";
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        byte[] body = null;
        if (!TextUtils.isEmpty(this.mRequestBody)) {
            try {
                body = mRequestBody.getBytes(getParamsEncoding());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Encoding not supported: " + getParamsEncoding(), e);
            }
        }

        return body;

    }

    @Override
    public String getBodyContentType() {
        return "application/xml";
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String utf8String = null;
        try {
            utf8String = new String(response.data, "UTF-8");
            return Response.success(utf8String, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
