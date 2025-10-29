package lyf.reg.server.util;

public class CounterUtil {

  private int count;

  public static CounterUtil of() {
    return new CounterUtil(0);
  }

  public static CounterUtil of(int count) {
    return new CounterUtil(count);
  }

  private CounterUtil(int count) {
    this.count = count;
  }

  public void count() {
    count++;
  }

  public void reset() {
    count = 0;
  }

  public int get() {
    return count;
  }
}
