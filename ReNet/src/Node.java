import java.util.*;

public class Node<T extends Comparable<T>>
{
	T key;
	int status=0; //small node
	ArrayList<Node<T>> working_set=new ArrayList<>(); //will contain all the recent communicating nodes
	ArrayList<Node<T>> S=new ArrayList<>();//set of small nodes
	HashMap<splayTree<T>,Splay_node<T>> L=new HashMap<>();
	int num_helps=0; 
	splayTree<T> ego_tree;
	Stack<Splay_node<T>> helperlist=new Stack<>();
}
