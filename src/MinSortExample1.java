
class MinSortExample1 {
    private native void segmented_sort(float machines[], int task_index[], int segments[], int t, int m);

    public static void main(String[] args) {
    	int m = 2;
    	int t = 4;
  	
    	float machines[] = new float[t*m];
    	int task_index[] = new int[t*m];
    	int segments[] = new int[m];
    	
        for(int i = 0;i < m; i++) {
    		for(int j = 0;j < t; j++) {
    			machines[i*t + j] = (i-j+5)*(float)Math.random();
    		}
    		segments[i] = i*t;
        }
    	
        for(int i = 0;i < m; i++) {
    		for(int j = 0;j < t; j++) {
    			task_index[i*t + j] = j;
    		}
        }
        
        new MinSortExample1().segmented_sort(machines, task_index, segments, t, m);
    }

    static {
        System.loadLibrary("MinSortExample1");
    }
}