package pl.kasprowski.etcal.optimizer;

import java.util.ArrayList;
import java.util.List;

public class TestMask {
	public static void main(String[] args) {
		int bits = 30;
		List<boolean[]> masks = generateAllMasks(bits);
		System.out.println(masks.size());
		//for(boolean[] mask:masks) System.out.println(maskAsString(mask));
	}
	
	
	public static String maskAsString(boolean[] mask) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<mask.length;i++) {
			sb.append((mask[i])?"1":"0");
		}
		return sb.toString();
	}

	static List<boolean[]> generateAllMasks(int bits) {
		long size = 1 << bits;
		ArrayList<boolean[]> masks = new ArrayList<boolean[]>();
		for (long val = 1; val < size; val++) {
			//BitSet bs = new BitSet(bits);
			boolean[] m = new boolean[bits];
			for (int i = bits-1; i >= 0; i--) {
				m[i] = (val & (1 << i)) != 0;
			}
			masks.add(m);
	//		System.out.println(val+" > "+maskAsString(m));
		}
		return masks;
	}

}
