import java.util.LinkedList;
import java.util.Queue;

public class splayTree<T extends Comparable<T>>
{
	int hops=0;
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
	public void set_root(T key)
	{
		Splay_node<T> n=new Splay_node<T>();
		n.key=key;
		root=n;
		((Coordinator<T>)(Driver.coord)).map.get(key).L.put(this,root);
	}
	public int parent_traversal(Splay_node<T> node)
	{
		if(node==null)
			return 0;
		return 1+parent_traversal(node.parent);
	}
	public Splay_node<T> insert(T key)
	{
		Splay_node<T>n=insert_helper(root,key);
		((Coordinator<T>)(Driver.coord)).map.get(key).L.put(this,n);
		return n;
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
			return node;                     // if node already exists then just return the pointer to it.
	}
	public Splay_node<T> search(T key)
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
	public Splay_node<T> inorder_successor(Splay_node<T> node)
	{
		if(node.left==null)
			return node;
		else
			return inorder_successor(node.left);
	}
	public void delete(T key)
	{
		delete_helper(root,key);
	}
	public void delete_helper(Splay_node<T> node,T key)
	{
		if(node.key.compareTo(key)==0)
		{
			if(node.left==null && node.right==null)
			{
				if(node==node.parent.left)
					node.parent.left=null;
				else
					node.parent.right=null;
			}
			else if(node.left==null && node.right!=null)
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
			else
			{
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
	public void splay(Splay_node<T> node)
	{
		while(true)
		{
			if(node.parent==null)
				break;
			if(node==node.parent.left)
			{
				if(node.parent.parent==null)
					rotate_right(node.parent);
				else
				{
					if(node.parent==node.parent.parent.left)
					{
						rotate_right(node.parent.parent);
						rotate_right(node.parent);
					}
					else
					{
						rotate_right(node.parent);
						rotate_left(node.parent);
					}
				}
			}
			else
			{
				if(node.parent.parent==null)
					rotate_left(node.parent);
				else
				{
					if(node.parent==node.parent.parent.right)
					{
						rotate_left(node.parent.parent);
						rotate_left(node.parent);
					}
					else
					{
						rotate_left(node.parent);
						rotate_right(node.parent);
					}
				}
			}
		}
	}
	public void rotate_right(Splay_node<T> node)
	{
		Splay_node<T> parent=node.parent;
		Splay_node<T> left=node.left;
		Splay_node<T> right=node.left.right;
		node.left=right;
		if(right!=null)
		    right.parent=node;
		node.parent=left;
		left.right=node;
		left.parent=parent;
		if(parent!=null) {
		if(parent.left==node)
			parent.left=left;
		else
			parent.right=left;}
		else
			root=left;
	}
	public void rotate_left(Splay_node<T> node)
	{
		Splay_node<T> parent=node.parent;
		Splay_node<T> right=node.right;
		Splay_node<T> left=node.right.left;
		node.right=left;
		if(left!=null)
		    left.parent=node;
		node.parent=right;
		right.left=node;
		right.parent=parent;
		if(parent!=null) {
		if(parent.left==node)
			parent.left=right;
		else
			parent.right=right;}
		else
			root=right;
	}
}
