package util;

/**
 * @Author sir
 * @Date 2020/1/9 10:40
 * @Description TODO
 **/
public class TimeCounter {

    private static final ThreadLocal<Long> timeLocal = new ThreadLocal<>();

    /**
     * 启动计时器
     */
    public static void start() {
        timeLocal.set(System.nanoTime());
    }

    /**
     * 计时
     *
     * @return
     */
    public static double counts() {
        if (timeLocal.get() == null) {
            throw new RuntimeException("请先启动计时器:TimeCounter.start()");
        }
        return (System.nanoTime() - timeLocal.get()) / 1e6d;
    }
}
