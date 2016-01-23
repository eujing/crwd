public class DataTime {

	public static final int epoch = 24220000;
	public static final int speed = 720;

	public static int getTime(long time) {
		int mins = (int)((time - (long)epoch * 1000 * 60) / 1000d / 60d * speed);
		return epoch + mins;
	}
	
}