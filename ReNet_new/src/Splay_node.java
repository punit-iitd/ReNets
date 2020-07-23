import java.util.*;
public class Splay_node<T extends Comparable<T>>
{
	T key;
	Splay_node<T> left;                         //left, right and parent pointers of a node
	Splay_node<T> right;
	Splay_node<T> parent;
	Node<T> host;                               //host of the tree
	T represent;                                //since we are using duplicate nodes as helper nodes(duplicate in the sense that it a node with different key but we are adding is at as another key), so I am kind of storing its originality
	Splay_node<T> relay;                         
}
class T implements Comparable<T>                //since this is generic implementation, I have overriden the compareTo function according to the generic key.
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
