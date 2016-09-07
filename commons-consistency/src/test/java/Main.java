
public class Main {
	
	public static int cj(int digit) {
		int k = 1;
		int j = digit;
		while(j > 0) {
			k = k*(j%10);
			j = j / 10;
		}
		return k;
	}
	
	
	public static boolean isQHS(int a, int b) {
		int sa = sumYS(a);
		if(sa != b) {
			return false;
		}
		int sb = sumYS(b);
		if(sb != a) {
			return false;
		}
		return true;
	}
	
	private static int sumYS(int s) {
		int k = 0;
		for(int i = 1; i < s; i++) {
			if(s % i == 0) {
				k += i;
			}
		}
		return k;
	}
	
	public static void main(String[] args) {
		System.out.println(isQHS(220, 285));
	}
}
