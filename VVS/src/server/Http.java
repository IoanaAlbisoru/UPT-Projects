package server;

import java.util.HashMap;
import java.util.Map;

abstract class Http {
  /** 2XX: generally "OK" */
  public static final int HTTP_OK = 200;
  public static final int HTTP_CREATED = 201;
  public static final int HTTP_ACCEPTED = 202;
  public static final int HTTP_NOT_AUTHORITATIVE = 203;
  public static final int HTTP_NO_CONTENT = 204;
  public static final int HTTP_RESET = 205;
  public static final int HTTP_PARTIAL = 206;

  /** 3XX: relocation/redirect */
  public static final int HTTP_MULT_CHOICE = 300;
  public static final int HTTP_MOVED_PERM = 301;
  public static final int HTTP_MOVED_TEMP = 302;
  public static final int HTTP_SEE_OTHER = 303;
  public static final int HTTP_NOT_MODIFIED = 304;
  public static final int HTTP_USE_PROXY = 305;

  /** 4XX: client error */
  public static final int HTTP_BAD_REQUEST = 400;
  public static final int HTTP_UNAUTHORIZED = 401;
  public static final int HTTP_PAYMENT_REQUIRED = 402;
  public static final int HTTP_FORBIDDEN = 403;
  public static final int HTTP_NOT_FOUND = 404;
  public static final int HTTP_BAD_METHOD = 405;
  public static final int HTTP_NOT_ACCEPTABLE = 406;
  public static final int HTTP_PROXY_AUTH = 407;
  public static final int HTTP_CLIENT_TIMEOUT = 408;
  public static final int HTTP_CONFLICT = 409;
  public static final int HTTP_GONE = 410;
  public static final int HTTP_LENGTH_REQUIRED = 411;
  public static final int HTTP_PRECON_FAILED = 412;
  public static final int HTTP_ENTITY_TOO_LARGE = 413;
  public static final int HTTP_REQ_TOO_LONG = 414;
  public static final int HTTP_UNSUPPORTED_TYPE = 415;

  /** 5XX: server error */
  public static final int HTTP_SERVER_ERROR = 500;
  public static final int HTTP_INTERNAL_ERROR = 501;
  public static final int HTTP_BAD_GATEWAY = 502;
  public static final int HTTP_UNAVAILABLE = 503;
  public static final int HTTP_GATEWAY_TIMEOUT = 504;
  public static final int HTTP_VERSION = 505;

  public final static Map<Integer, String> codeMessageMap = new HashMap<Integer, String>(30);
  static {
    Http.codeMessageMap.put(Http.HTTP_OK, "200 OK");
    Http.codeMessageMap.put(Http.HTTP_CREATED, "");
    Http.codeMessageMap.put(Http.HTTP_ACCEPTED, "");
    Http.codeMessageMap.put(Http.HTTP_NOT_AUTHORITATIVE, "");
    Http.codeMessageMap.put(Http.HTTP_NO_CONTENT, "");
    Http.codeMessageMap.put(Http.HTTP_RESET, "");
    Http.codeMessageMap.put(Http.HTTP_PARTIAL, "");

    Http.codeMessageMap.put(Http.HTTP_MULT_CHOICE, "");
    Http.codeMessageMap.put(Http.HTTP_MOVED_PERM, "");
    Http.codeMessageMap.put(Http.HTTP_MOVED_TEMP, "");
    Http.codeMessageMap.put(Http.HTTP_SEE_OTHER, "");
    Http.codeMessageMap.put(Http.HTTP_NOT_MODIFIED, "");
    Http.codeMessageMap.put(Http.HTTP_USE_PROXY, "");

    Http.codeMessageMap.put(Http.HTTP_BAD_REQUEST, "400 Bad Request");
    Http.codeMessageMap.put(Http.HTTP_UNAUTHORIZED, "");
    Http.codeMessageMap.put(Http.HTTP_PAYMENT_REQUIRED, "");
    Http.codeMessageMap.put(Http.HTTP_FORBIDDEN, "403 Forbidden");
    Http.codeMessageMap.put(Http.HTTP_NOT_FOUND, "404 Not Found");
    Http.codeMessageMap.put(Http.HTTP_BAD_METHOD, "");
    Http.codeMessageMap.put(Http.HTTP_NOT_ACCEPTABLE, "");
    Http.codeMessageMap.put(Http.HTTP_PROXY_AUTH, "");
    Http.codeMessageMap.put(Http.HTTP_CLIENT_TIMEOUT, "408 Request Timeout");
    Http.codeMessageMap.put(Http.HTTP_CONFLICT, "");
    Http.codeMessageMap.put(Http.HTTP_GONE, "");
    Http.codeMessageMap.put(Http.HTTP_LENGTH_REQUIRED, "");
    Http.codeMessageMap.put(Http.HTTP_PRECON_FAILED, "");
    Http.codeMessageMap.put(Http.HTTP_ENTITY_TOO_LARGE, "");
    Http.codeMessageMap.put(Http.HTTP_REQ_TOO_LONG, "");
    Http.codeMessageMap.put(Http.HTTP_UNSUPPORTED_TYPE, "");

    Http.codeMessageMap.put(Http.HTTP_SERVER_ERROR, "500 Server Error");
    Http.codeMessageMap.put(Http.HTTP_INTERNAL_ERROR, "");
    Http.codeMessageMap.put(Http.HTTP_BAD_GATEWAY, "");
    Http.codeMessageMap.put(Http.HTTP_UNAVAILABLE, "503 Maintenance");
    Http.codeMessageMap.put(Http.HTTP_GATEWAY_TIMEOUT, "");
    Http.codeMessageMap.put(Http.HTTP_VERSION, "");
  }
}
