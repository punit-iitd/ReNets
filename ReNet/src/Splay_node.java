import java.util.*;
public class Splay_node<T extends Comparable<T>>
{
	T key;
	Splay_node<T> left;
	Splay_node<T> right;
	Splay_node<T> parent;
	Node<T> host;
	HashMap<T,Splay_node<T>> sec_keys=new HashMap<>();
}
class T implements Comparable<T>
{
	public int compareTo(T a)
	{
		if(this.toString().compareTo(a.toString())>0)
		{
			return 1;
		}
		else if(this.toString().compareTo(a.toString())==0)
		{
			return 0;
		}
		else
		{
		    return -1;
		}
	}
}
