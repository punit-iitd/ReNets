import java.util.*;
public class Circ_list<T> {
	Circ_list_Node<T> head;
	Circ_list_Node<T> tail;
	Circ_list_Node<T> current;
	int size=0;
	HashMap<T,Circ_list_Node<T>> map;
	public Circ_list()
	{
		head=new Circ_list_Node<T>();
		tail=head;
		current=head;
		size=0;
		map=new HashMap<>();
	}
	public void insert(T val)
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
	public T get()
	{
		T val=current.val;
		current=current.next;
		return val;
	}
	public void remove(T val)
	{
		Circ_list_Node<T> n=map.get(val);
		Circ_list_Node<T> nex=n.next;
		n.prev.next=nex;
		nex.prev=n.prev;
		if(n==tail)
		{
//			if(current==tail)
//				current=n.prev;
			tail=n.prev;
		}
		else if(n==head)
		{
//			if(current==head)
//				current=nex;
			head=nex;
		}
		if(n==current)
			current=n.next;
		size--;
	}
}
