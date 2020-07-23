import java.util.*;

public class Node<T extends Comparable<T>>
{
	T key;
	int status=0;                                            //small node
	ArrayList<Node<T>> working_set=new ArrayList<>();        //will contain all the recent communicating nodes
	HashMap<Node<T>,Integer> S=new HashMap<>();              //set of small nodes
	HashMap<splayTree<T>,Splay_node<T>> L=new HashMap<>();   //L is a map that stores the location of a node in different trees mapped with the tree itself as a key
	int num_helps=0;                                         //counter for number of tree pairs a node is serving as a relay(helper) node 
	splayTree<T> ego_tree;                                   
}
