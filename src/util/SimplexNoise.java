package util;

public class SimplexNoise { 
	
	private int seed;

	private Gradient grad3[] = { new Gradient(1,1), new Gradient(-1,1), new Gradient(1,-1), new Gradient(-1,-1), 
			new Gradient(1,0), new Gradient(-1,0), new Gradient(1,0), new Gradient(-1,0), 
			new Gradient(0,1), new Gradient(0,-1), new Gradient(0,1), new Gradient(0,-1) }; 

	private short p[] = { 151,160,137,91,90,15, 
			131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23, 
			190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33, 
			88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166, 
			77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244, 
			102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196, 
			135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123, 
			5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42, 
			223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9, 
			129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228, 
			251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107, 
			49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254, 
			138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180 }; 

	private short perm[] = new short[512]; 
	private short permMod12[] = new short[512]; 

	private final float F2 = (float) (0.5 * (Math.sqrt(3) - 1)); 
	private final float G2 = (float) ((3 - Math.sqrt(3)) / 6); 

	public SimplexNoise(int seed) { 
		this.seed = seed;
		for(int i = 0; i < 512; i++) { 
			perm[i] = p[i + seed & 255]; 
			permMod12[i] = (short) (perm[i] % 12); 
		} 
	}
	
	public final int seed() {
		return seed;
	}

	private static int fastFloor(double x) { 
		int xi = (int) x; 
		return x < xi ? xi - 1 : xi; 
	} 

	private double dot(Gradient gradient, double x, double y) { 
		return gradient.X * x + gradient.Y * y; 
	} 

	public double generateSimplexNoise(double x, double y) { 
		double n0;
		double n1;
		double n2; 
		double s = (x + y) * F2; 

		int i = fastFloor(x + s); 
		int j = fastFloor(y + s); 

		double t = (i + j) * G2; 

		double X0 = i - t; 
		double Y0 = j - t; 

		double x0 = x - X0; 
		double y0 = y - Y0; 

		int i1, j1; 

		if(x0 > y0) { 
			i1 = 1; j1 = 0; 
		} else { 
			i1 = 0; j1 = 1; 
		} 

		double x1 = x0 - i1 + G2; 
		double y1 = y0 - j1 + G2; 

		double x2 = x0 - 1.0F + 2.0F * G2; 
		double y2 = y0 - 1.0F + 2.0F * G2; 

		int ii = i & 255; 
		int jj = j & 255;   

		int gi0 = permMod12[ ii + perm[jj] ]; 
		int gi1 = permMod12[ ii + i1 + perm[jj + j1] ]; 
		int gi2 = permMod12[ ii + 1 + perm[jj+1] ]; 

		double t0 = 0.5F - x0 * x0 - y0 * y0; 

		if(t0 < 0.0F) { 
			n0 = 0.0F; 
		} else { 
			t0 *= t0; 
			n0 = t0 * t0 * dot(grad3[gi0], x0, y0); 
		} 

		double t1 = 0.5F - x1 * x1 - y1 * y1; 

		if(t1 < 0.0F) { 
			n1 = 0.0F; 
		} else { 
			t1 *= t1; 
			n1 = t1 * t1 * dot(grad3[gi1], x1, y1); 
		} 

		double t2 = 0.5F - x2 * x2 - y2 * y2; 

		if(t2 < 0.0F) { 
			n2 = 0.0F; 
		} else { 
			t2 *= t2; 
			n2 = t2 * t2 * dot(grad3[gi2], x2, y2); 
		} 
		return 40.0F * (n0 + n1 + n2); 
	} 

	private class Gradient { 

		double X, Y; 

		Gradient(double x, double y) { 
			this.X = x; 
			this.Y = y; 
		}   

	} 

}