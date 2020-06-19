import java.util.*;
public class Coordinator<T extends Comparable<T>> {
	public int threshold=Driver.threshold;
	public int nodes=Driver.nodes;
	public int tot_workingSet_size=0;
	public int max_help=Driver.max_help;
	public HashMap<T,Node<T>> map=new HashMap<>();
	public ArrayList<Splay_node<T>> ar1=new ArrayList<>();
	public ArrayList<Splay_node<T>> ar2=new ArrayList<>();
	public Splay_node<T> temp;
	public HashMap<T,T> spareSmall=new HashMap<>();
	public void initialize(T[] ar)
	{
		for(int i=0;i<ar.length;i++)
		{
			Node<T> n=new Node<>();
			n.key=ar[i];
			map.put(ar[i],n);
			spareSmall.put(ar[i],ar[i]);
		}
	}
	public void check(T x,T y)
	{
		Node<T> src=map.get(x);
	    Node<T> dst=map.get(y);
	    process_request(src,dst);
	    if(src.working_set.size()>threshold)
	    	src.ego_tree.printTree();
//	    if(map.get(2).working_set.size()>threshold)
//	    	map.get(2).ego_tree.printTree();
	}
	public void process_request(Node<T> src,Node<T> dst)
	{
		if(src.status==0)
		{
			if(dst.status==0)
			{
				int bool=0;
				for(int i=0;i<src.S.size();i++) //finding for the destination in list S.
				{
					if(src.S.get(i)==dst)
					{
						bool=1;
						break;
					}
				}
				if(bool==0)
				{
					System.out.println("Adding Route between "+src.key+"(small)"+" "+dst.key+"(small)");
					addRoute(src,dst);
				}
				else
					System.out.println("Direct connection from "+src.key+" to "+dst.key+" with 1 hop");
			}
			else
			{
				if(src.L.containsKey(dst.ego_tree))
				{
					int h=parent_traversal(src.L.get(dst.ego_tree).parent);
					System.out.println("Reached from "+src.key+" to root in "+h+" hops and then directly to "+dst.key+" in 1 hop");
				}
				else
				{
					System.out.println("Adding Route between "+src.key+"(small)"+" "+dst.key+"(large)");
					addRoute(src,dst);
				}
			}
		}
		else
		{
			if(dst.status==0)
			{
				src.ego_tree.root.host=null;
				src.ego_tree.search(dst.key);
				if(src.ego_tree.root.key==dst.key)
					System.out.println("Destination found in "+src.ego_tree.hops+" hops");
				else
				{
					System.out.println("Adding Route between "+src.key+"(large)"+" "+dst.key+"(small)");
					addRoute(src,dst);
				}
				src.ego_tree.root.host=src; //important: since the root will change after splaying, the host pointer must be given to this new root
			}
			else
			{
				//find helper node
				src.ego_tree.root.host=null;
//				dst.ego_tree.root.host=null;
				//currently I am doing inorder traversal for searching the helper node, but can we do a better search?
				temp=null;
				Splay_node<T> relay=inorder_helper(src.ego_tree.root,dst.key);
				if(relay==null)
				{
					System.out.println("Adding Route between "+src.key+"(large)"+" "+dst.key+"(large)");
					addRoute(src,dst);
				}
				else {
				int g=parent_traversal(relay.sec_keys.get(dst.key).parent);
				src.ego_tree.splay(relay);
				src.ego_tree.root.host=src; 
				dst.ego_tree.splay(relay.sec_keys.get(dst.key));
				dst.ego_tree.root.host=dst;
//				System.out.println(src.ego_tree.root.host.key+" "+dst.ego_tree.root.host.key);
				System.out.println("Found the helper node( "+relay.key+" ) and jumped to another tree in 1 hop and from there to root in "+g+" hops");}
			}
		}
	}
	public Splay_node<T> inorder_helper(Splay_node<T> node,T key)
	{
		if(node==null)
			return null;
		inorder_helper(node.left,key);
		if(node.sec_keys.containsKey(key))
		{
//			System.out.println(node.key);
			temp=node;
		}
		if(temp!=null)
			return temp;
		inorder_helper(node.right,key);
		return temp;
	}
	public int parent_traversal(Splay_node<T> node)
	{
		if(node==null)
			return 0;
		return 1+parent_traversal(node.parent);
	}
	public void addRoute(Node<T> src,Node<T> dst)
	{
		tot_workingSet_size+=2;
//		if(tot_workingSet_size>nodes*threshold/2)
//		{
//			reset();
//		}
		int bool1=0;
		int bool2=0;
		src.working_set.add(dst); 
		if(src.status==0 && src.working_set.size()>threshold)
		{
			bool1++;
			makeLarge(src);
		}
		dst.working_set.add(src);
		if(dst.status==0 && dst.working_set.size()>threshold)
		{
			bool2++;
			makeLarge(dst);
		}
		//making the connection between the nodes
		if(src.status==0 && dst.status==0)
		{
			src.S.add(dst);
			dst.S.add(src);
		}
		else if(bool2==0 && src.status==0 && dst.status==1)
		{
			Splay_node<T> n=dst.ego_tree.insert(src.key);
			dst.ego_tree.splay(n);
		}
		else if(bool1==0 && src.status==1 && dst.status==0)
		{
			Splay_node<T> n=src.ego_tree.insert(dst.key);
			dst.ego_tree.splay(n);
		}
		else if(bool1==0 && bool2==0 && src.status==1 && dst.status==1)
		{
			//find helper node
			find_helper_node(src,dst);
		}
	}
	public void makeLarge(Node<T> n)
	{
		n.status=1;
		spareSmall.remove(n.key);
		n.L.clear();
		n.S.clear();
		splayTree<T> ego_tree=new splayTree<>();
		int x=0;
		ArrayList<Node<T>> large=new ArrayList<>();
		for(int i=n.working_set.size()-1;i>=0;i--)     //starting to add the keys from backwards of working set as the last key should be at the root
		{
			//only adding small nodes to the tree
			if(n.working_set.get(i).status==0 && x==0)
			{
				ego_tree.set_root(n.working_set.get(i).key);
				if(map.get(ego_tree.root.key).num_helps<max_help)
				n.helperlist.push(ego_tree.root);
				spareSmall.remove(n.working_set.get(i).key);
				x++;
			}
			else if(n.working_set.get(i).status==0 && x!=0)
			{
				Splay_node<T> node=ego_tree.insert(n.working_set.get(i).key); 
				if(map.get(node.key).num_helps<max_help)
				n.helperlist.push(node);
				spareSmall.remove(n.working_set.get(i).key);
			}
			else if(n.working_set.get(i).status==1)
			{
				n.working_set.get(i).ego_tree.root.host=null;
				//just storing the large nodes, also remove this key from all the ego trees in which it was when it was small
				n.working_set.get(i).ego_tree.delete(n.key); //should splay be done during delete?
				n.working_set.get(i).ego_tree.root.host=n.working_set.get(i);
				large.add(n.working_set.get(i));
			}
		}
		n.ego_tree=ego_tree;
		n.ego_tree.root.host=n;
		for(int i=0;i<large.size();i++)
		{
			find_helper_node(n,large.get(i));
		}
	}
	public void find_helper_node(Node<T> node1,Node<T> node2)
	{
		if(!node1.helperlist.empty())
		{
			Splay_node<T> helper=node1.helperlist.peek();
			map.get(helper.key).num_helps++;
			if(map.get(helper.key).num_helps>=max_help)
				node1.helperlist.pop();
			Splay_node<T> n=node2.ego_tree.insert(helper.key);  //is splay required during formation of relay?
			helper.sec_keys.put(node2.key,n);
			n.sec_keys.put(node1.key,helper);
			System.out.println(helper.key+" "+node1.key+" "+node2.key);
		}
		else if(!node2.helperlist.empty())
		{
			Splay_node<T> helper=node2.helperlist.peek();
			map.get(helper.key).num_helps++;
			if(map.get(helper.key).num_helps>=max_help)
				node2.helperlist.pop();
			Splay_node<T> n=node1.ego_tree.insert(helper.key);
			helper.sec_keys.put(node1.key,n);
			n.sec_keys.put(node2.key,helper);
		}
		else
		{
			Map.Entry<T,T> entry = spareSmall.entrySet().iterator().next();
			T helper=entry.getKey();
			map.get(helper).num_helps++;
			if(map.get(helper).num_helps>=max_help)
				spareSmall.remove(helper);
			Splay_node<T> n1=node1.ego_tree.insert(helper);
			Splay_node<T> n2=node2.ego_tree.insert(helper);
			n1.sec_keys.put(node2.key,n2);
			n2.sec_keys.put(node1.key,n1);
		}
	}
	//	public void find_helper_node(Node<T> node1,Node<T> node2)
//	{
//		ar1=new ArrayList<>();
//		ar2=new ArrayList<>();
////		System.out.println(node1.key+" "+node2.key);
//		inorder(node1.ego_tree.root,ar1);
//		inorder(node2.ego_tree.root,ar2);
//		int i=0;
//		int j=0;
//		while(i<ar1.size() && j<ar2.size())
//		{
//			if(ar1.get(i).key.compareTo(ar2.get(j).key)>0)
//				j++;
//			else if(ar1.get(i).key.compareTo(ar2.get(j).key)<0)
//				i++;
//			else
//			{                                       
//				if(node1.num_helps>max_help)       
//				{
//					i++;
//					j++;
//					continue;
//				}
//				else
//				{
//					ar1.get(i).sec_keys.put(node2.key,ar2.get(j));
//					ar2.get(j).sec_keys.put(node1.key,ar1.get(i));
//					node1.num_helps++;
//					break;
//				}
//			}
//		}
//	}
	public void inorder(Splay_node<T> node,ArrayList<Splay_node<T>> ar)
	{
		if(node==null)
			return;
		inorder(node.left,ar);
		ar.add(node);
		inorder(node.right,ar);
	}
	public void reset()
	{
		for(int i=1;i<=nodes;i++)
		{
			Node<T> n=map.get(i);
			n.L.clear();
			n.S.clear();
			n.working_set.clear();
		}
	}
}
