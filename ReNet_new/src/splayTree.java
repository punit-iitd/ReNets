import java.util.LinkedList;
import java.util.Queue;

public class splayTree<T extends Comparable<T>>
{
	int hops=0;                                         //hops required for searching a node in its ego tree
	int links=0;                                        //link changes while splaying a node(this is equal to links added and links deleted as both are same)
	public Splay_node<T> root=new Splay_node<T>();
	public void printTree()
	{
		Queue<Splay_node<T>> q=new LinkedList<>();
		q.add(root);
		while(q.size()!=0)
		{
			int n=q.size();
			while(n!=0)
			{
				n--;
				Splay_node<T> node=q.remove();
				System.out.print(node.key+" ");
				Splay_node<T> left=node.left;
				Splay_node<T> right=node.right;
				if(left!=null)
				    q.add(left);
				if(right!=null)
					q.add(right);
			}
			System.out.println();
		}
	}
	public void set_root(T key)                           //initialization of the tree by setting a root
	{
		Splay_node<T> n=new Splay_node<T>();
		n.key=key;
		root=n;
		((Coordinator<T>)(Driver.coord)).map.get(key).L.put(this,root);
	}
	public int parent_traversal(Splay_node<T> node)                   //function for calculating hops during traversing from parent to parent
	{
		if(node==null)
			return 0;
		return 1+parent_traversal(node.parent);
	}
	public Splay_node<T> insert(T key)                                //inserting a node in a binary search tree fashion
	{
		Splay_node<T>n=insert_helper(root,key);
		((Coordinator<T>)(Driver.coord)).map.get(key).L.put(this,n);  //as mentioned L is a map that stores the location of a node in different trees mapped with the tree itself as a key
		return n;                                                     //so whenever a node is inserted into a ego tree, its map L is updated here.
	}
	public Splay_node<T> insert_helper(Splay_node<T> node,T key)
	{
		if(node.key.compareTo(key)>0)
		{
			if(node.left==null)
			{
				Splay_node<T> n=new Splay_node<T>();
				n.key=key;
				n.parent=node;
				node.left=n;
//				splay(n);
				return n;
			}
			else
				return insert_helper(node.left,key);
		}
		else if(node.key.compareTo(key)<0)
		{
			if(node.right==null)
			{
				Splay_node<T> n=new Splay_node<T>();
				n.key=key;
				n.parent=node;
				node.right=n;
				return n;
			}
			else
				return insert_helper(node.right,key);
		}
		else
			return node;                                              // if node already exists then just return the pointer to it
	}
	public Splay_node<T> search(T key)                                //search a node in binary tree fashion and returns the pointer to it. Also it counts the hops required for searching
	{
		hops=-1;
		search_helper(root,key);
		return root;
	}
	public void search_helper(Splay_node<T> node,T key)
	{
		if(node.key.compareTo(key)>0)
		{
			if(node.left==null)
			{
				splay(node);
				hops++;
				return;
			}
			hops++;
			search_helper(node.left,key);
		}
		else if(node.key.compareTo(key)<0)
		{
			if(node.right==null)
			{
				splay(node);
				hops++;
				return;
			}
			hops++;
			search_helper(node.right,key);
		}
		else
		{
			hops++;
			splay(node);
		}
	}
	public Splay_node<T> inorder_successor(Splay_node<T> node)    //finds the inorder successor of a node which is used when we delete a node and want to find a node to replace it(inorder predecessor also works) 
	{
		if(node.left==null)
			return node;
		else
			return inorder_successor(node.left);
	}
	public void delete(T key)                                     //deletes a node
	{ 
		delete_helper(root,key);
	}
	public void delete_helper(Splay_node<T> node,T key)
	{
		if(node.key.compareTo(key)==0)
		{
			if(node.left==null && node.right==null)               //case where both child are null, then just delete it from the tree by setting the child pointer of its parent to null
			{
				if(node==node.parent.left)
					node.parent.left=null;
				else
					node.parent.right=null;
			}
			else if(node.left==null && node.right!=null)         //the following two cases are where one of the child is not null, then the child will take place of the node.
			{
				if(node==node.parent.left)
				{
					node.parent.left=node.right;
					node.right.parent=node.parent;
				}                                               
				else
				{
					node.parent.right=node.right;
					node.right.parent=node.parent;
				}
			}
			else if(node.left!=null && node.right==null)
			{
				if(node==node.parent.left)
				{
					node.parent.left=node.left;
					node.left.parent=node.parent;
				}
				else
				{
					node.parent.right=node.left;
					node.left.parent=node.parent;
				}
			}
			else                                                 //where both the child are not null. Here we find a inorder successor of the node and it take place of the node, satisfying the binary search property
			{                                                    //inorder successor is the smallest key which is greater the the current key to be deleted.
				Splay_node<T> n=inorder_successor(node.right);
				Splay_node<T> parent=node.parent;
				Splay_node<T> r=n.right;
				if(n.parent!=node)
				    n.parent.left=r;
				else
					n.parent.right=r;
				if(r!=null)
					r.parent=n.parent;
				Splay_node<T> left=node.left;
				Splay_node<T> right=node.right;
				n.left=left;
				left.parent=n;
				n.right=right;
				if(right!=null)
				right.parent=n;
				n.parent=parent;
				if(parent!=null)
				{
					if(node==node.parent.left)
						parent.left=n;
					else
						parent.right=n;
				}
				if(root.key.compareTo(key)==0)
					root=n;
			}
//			splay(node.parent);
		}
		else if(node.key.compareTo(key)>0)
		{
			delete_helper(node.left,key);
		}
		else
			delete_helper(node.right,key);
	}
	public int splay(Splay_node<T> node)                      //this is the bottom up splay function which uses necessary left rotations and right rotations according to the cases.
	{                                                         //this function also returns the total link changes in a splay operation which is just the sum of the link changes in left rotation and right rotation.
		links=0;
		while(true)
		{
			if(node.parent==null)
				break;
			if(node==node.parent.left)
			{
				if(node.parent.parent==null)
					links+=rotate_right(node.parent);
				else
				{
					if(node.parent==node.parent.parent.left)   //zig-zig case (left-left)
					{
						links+=rotate_right(node.parent.parent);
						links+=rotate_right(node.parent);
					}
					else                                       //zig-zag case (left-right)
					{
						links+=rotate_right(node.parent);
						links+=rotate_left(node.parent);
					}
				}
			}
			else
			{
				if(node.parent.parent==null)
					links+=rotate_left(node.parent);
				else
				{
					if(node.parent==node.parent.parent.right)  //zig-zig case (right-right)
					{
						links+=rotate_left(node.parent.parent);
						links+=rotate_left(node.parent);
					}
					else
					{
						links+=rotate_left(node.parent);        //zig-zag case(right-left)
						links+=rotate_right(node.parent);
					}
				}
			}
		}
		return links;
	}
	public int rotate_right(Splay_node<T> node)
	{
		int cnt=0;
		Splay_node<T> parent=node.parent;
		Splay_node<T> left=node.left;
		Splay_node<T> right=node.left.right;
		node.left=right;
		if(right!=null)
		{
			cnt++;
			right.parent=node;
		}
		node.parent=left;
		left.right=node;
		left.parent=parent;
		if(parent!=null) 
		{
			cnt++;
			if(parent.left==node)
				parent.left=left;
			else
				parent.right=left;
		}
		else
			root=left;
		return cnt;
	}
	public int rotate_left(Splay_node<T> node)
	{
		int cnt=0;
		Splay_node<T> parent=node.parent;
		Splay_node<T> right=node.right;
		Splay_node<T> left=node.right.left;
		node.right=left;
		if(left!=null)
		{
			cnt++;
			left.parent=node;
		}
		node.parent=right;
		right.left=node;
		right.parent=parent;
		if(parent!=null) 
		{
			cnt++;
			if(parent.left==node)
				parent.left=right;
			else
				parent.right=right;
		}
		else
			root=right;
		return cnt;
	}
}
