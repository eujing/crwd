public class TimeUtils {

	public static final int epoch = 24220000;
	public static final int speed = 1440;

	public static long getTime(long time) {
		return epoch + (time - epoch) * speed;
	}
	
}