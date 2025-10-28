package com.lyf.lib.exception;

import com.lyf.lib.dto.ErrorResponseDto;
import com.lyf.lib.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ErrorHelper {

  private static final Logger logger = LoggerFactory.getLogger(ErrorHelper.class);

  public static <T> HttpUtil.ResponseHolder<T> checkAndThrow(HttpUtil.ResponseHolder<T> resp) {

    if(resp == null) {
      throw new HttpIOError("Failed to call Service, missing response.");
    }

    int httpStatus = resp.httpStatus();
    String msg = Optional.ofNullable(resp.error()).map(ErrorResponseDto::message).orElse(String.format("Failed, status=%s", httpStatus));

    checkAndThrow(httpStatus, msg);
    return resp;
  }

  public static void checkAndThrow(Integer httpStatus) {

    if(httpStatus == null) {
      throw new HttpIOError("Failed to call Service, missing httpStatus.");
    }

    String msg = String.format("Failed, status=%s", httpStatus);
    checkAndThrow(httpStatus, msg);
  }

  private static void checkAndThrow(Integer httpStatus, String msg) {

    if (httpStatus == 401) throw new AuthenticationError(msg);

    else if (httpStatus == 403) throw new ForbiddenError(msg);

    else if (httpStatus == 404) throw new DataNotFoundError(msg);

    else if (httpStatus == 428) logger.warn("MFA is required.");

    else if (httpStatus / 100 == 4) throw new InvalidClientRequestException(msg);

    else if (httpStatus / 100 == 5) throw new HttpIOError(msg);
  }

}
