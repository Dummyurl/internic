package com.uca.devceargo.internic.api;

import android.content.Context;

import com.uca.devceargo.internic.R;

public class ApiMessage {
    private static final int BAD_REQUEST = 400;
    private static final int LOGIN_FAILED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int EXPECTATION_FAILED = 417;
    public static final int CODE_OK = 200;
    public static final int DEFAULT_ERROR_CODE = 0;
    private static final int INTERNAL_SERVER_ERROR = 500;

    public String sendMessageOfResponseAPI(int code, Context context){
        String message;

        switch (code){
            case BAD_REQUEST:
                message = String.format(context.getString(R.string.message_api_format), code,
                        context.getString(R.string.message_bad_request));
                break;

            case FORBIDDEN:
                message = String.format(context.getString(R.string.message_api_format), code,
                        context.getString(R.string.message_forbidden));
                break;

            case NOT_FOUND:
                message = String.format(context.getString(R.string.message_api_format), code,
                        context.getString(R.string.message_not_found));
                break;
            case REQUEST_TIMEOUT:
                message = String.format(context.getString(R.string.message_api_format), code,
                        context.getString(R.string.message_request_timeout));
                break;
            case EXPECTATION_FAILED:
                message = String.format(context.getString(R.string.message_api_format), code,
                        context.getString(R.string.message_expectation_failed));
                break;
            case CODE_OK:
                message = String.format(context.getString(R.string.message_api_format), code,
                        context.getString(R.string.message_code_ok));
                break;
            case INTERNAL_SERVER_ERROR:
                message = String.format(context.getString(R.string.message_api_format), code,
                        context.getString(R.string.message_internal_server_error));
                break;
            case LOGIN_FAILED:
                message = context.getString(R.string.message_login_failed);
                break;
            default:
                message = context.getString(R.string.message_network_connection_error);
                break;
        }
        return message;
    }
}
