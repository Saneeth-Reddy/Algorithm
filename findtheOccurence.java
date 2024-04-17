/******************************************************************************

Welcome to GDB Online.
GDB online is an online compiler and debugger tool for C, C++, Python, Java, PHP, Ruby, Perl,
C#, OCaml, VB, Swift, Pascal, Fortran, Haskell, Objective-C, Assembly, HTML, CSS, JS, SQLite, Prolog.
Code, Compile, Run and Debug online from anywhere in world.

*******************************************************************************/
public class findtheOccurence
{
    
    
     public static int rec(int arr[], int low, int high, int target) {
        int count = 0;
        if (low <= high) {
            int mid = (low + high) / 2;
            if (arr[mid] == target) {
                count++;
                int left = rec(arr, low, mid - 1, target);
                int right = rec(arr, mid + 1, high, target);
                count = count + left + right;
            } else if (arr[mid] < target) {
                count = rec(arr, mid + 1, high, target);
            } else {
                count = rec(arr, low, mid - 1, target);
            }
        }
        return count;
    }
 	public static void main(String[] args) {
 	    int arr[] = {1,2,2,2,2,3,3,3,4,4};
 	    int target = 1;
 	   int result = rec(arr,0,arr.length-1,target);
 	   if(result > 1) {
 	       System.out.println("true");
 	   }
 	   else {
 	       System.out.println("false");
 	   }
		
	}
}
