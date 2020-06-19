import java.io.*;
import java.util.*;
public class Driver {
	public static Coordinator<Integer> coord;
	public static int threshold=4;
	public static int max_help=1;
	public static int nodes;
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		BufferedReader br=new BufferedReader(new FileReader("/ReNet/src/demand.txt"));
		nodes=Integer.valueOf(br.readLine());
		//Initialization of nodes
		Integer[] arr=new Integer[nodes+1];
		for(int i=1;i<=nodes;i++)
		{
			arr[i]=i;
		}
		String s;
		coord=new Coordinator<>();
		coord.initialize(arr);
		while((s=br.readLine())!=null)
		{
			String[] pair=s.split(" ");
			int x=Integer.valueOf(pair[0]);
			int y=Integer.valueOf(pair[1]);
		    coord.check(x,y);
		}
		br.close();
	}

}
