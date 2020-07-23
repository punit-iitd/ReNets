import java.util.*;
public class Circ_list<T> {                 //circular list is made for round robin selection of helper nodes
	Circ_list_Node<T> head;                 //head of the list
	Circ_list_Node<T> tail;                 //tail of the list which is connected with the head for circular behavior
	Circ_list_Node<T> current;              //this is the node which is returned to us when we need a helper key and then it is updated to next element in the list
	int size=0;
	HashMap<T,Circ_list_Node<T>> map;       //map stores the links of the nodes which comes in handy when we want to delete a particular node in approx. O(1) time
	public Circ_list()
	{
		head=new Circ_list_Node<T>();
		tail=head;
		current=head;
		size=0;
		map=new HashMap<>();
	}
	public void insert(T val)              //here I am inserting a new node next to tail and then updating the tail to this node and also maintaining the next and prev pointers
	{
		if(size==0)
		{
			head.val=val;
			tail=head;
			tail.next=head;
		}
		else
		{
			Circ_list_Node<T> node=new Circ_list_Node<>();
			node.val=val;
			tail.next=node;
			node.prev=tail;
			tail=node;
			tail.next=head;
			head.prev=tail;
		}
		size++;
		map.put(val,tail);
	}
	public T get()                       //this function just returns the key of the "current" node and updates the current node to next
	{
		T val=current.val;
		current=current.next;
		return val;
	} 
	public void remove(T val)           //this function deletes a node. Deletion is required when a node can no longer provide as helper node.
	{
		Circ_list_Node<T> n=map.get(val);
		Circ_list_Node<T> nex=n.next;
		n.prev.next=nex;
		nex.prev=n.prev;
		if(n==tail)
		{
			tail=n.prev;
		}
		else if(n==head)
		{
			head=nex;
		}
		if(n==current)
			current=n.next;
		size--;
	}
}
