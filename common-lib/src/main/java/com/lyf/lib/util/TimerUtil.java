package com.lyf.lib.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimerUtil {

  private TimerUtil() {
  }

  public static TimerUtil of() {
    return new TimerUtil();
  }

  public Date getDate(Date date, String str) {
    if (StringUtils.isNotBlank(str)) {
      boolean match = DateType.RELATIVE_TIME.match(str);
      return match ? DateType.RELATIVE_TIME.date(date, str) : date;
    }
    return date;
  }


  public enum DateType {

    RELATIVE_TIME {
      @Override
      boolean match(String str) {
        Pattern pattern = Pattern.compile(RELATIVE_TIME_PATTERN);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
      }

      @Override
      Date date(Date date, String str) {
        Pattern pattern = Pattern.compile(RELATIVE_TIME_PATTERN);
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
          String sign = matcher.group(1);
          int number = Integer.parseInt(matcher.group(2));
          String dayMonthOrYear = matcher.group(3);
          switch (sign + dayMonthOrYear) {
            case "+D" -> date = DateUtils.addDays(date, number);
            case "+M" -> date = DateUtils.addMonths(date, number);
            case "+Y" -> date = DateUtils.addYears(date, number);
            case "-D" -> date = DateUtils.addDays(date, -number);
            case "-M" -> date = DateUtils.addMonths(date, -number);
            case "-Y" -> date = DateUtils.addYears(date, -number);
          }
        }
        return date;
      }
    };

    private static final String RELATIVE_TIME_PATTERN = "([\\+|\\-])([0-9]{1,3})([DMY])";

    boolean match(String str) {
      return false;
    }

    Date date(Date date, String str) {
      return date;
    }
  }

}
