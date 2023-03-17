import java.util.Timer;

public class Clock {
	public static volatile int Time=0;//运行时间
	public static volatile int TimeSlice=0;//时间片
	public static volatile Timer clock=new Timer();//计时器
}
