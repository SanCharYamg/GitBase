import java.util.Timer;

public class Clock {
	public static volatile int Time=0;//����ʱ��
	public static volatile int TimeSlice=0;//ʱ��Ƭ
	public static volatile Timer clock=new Timer();//��ʱ��
}
