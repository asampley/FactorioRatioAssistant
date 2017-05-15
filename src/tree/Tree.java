package tree;

import java.util.ArrayList;
import java.util.List;

public class Tree<T> {
	protected T item;
	protected List<Tree<T>> children;
	
	public Tree(T root) {
		item = root;
		children = new ArrayList<Tree<T>>();
	}
	
	public T getRootValue() {
		return item;
	}
	
	public boolean isLeaf() {
		return children.isEmpty();
	}
	
	public boolean addChild(Tree<T> child) {
		return children.add(child);
	}
	
	public boolean removeChild(Tree<T> child) {
		return children.remove(child);
	}
	
	public Tree<T> getChild(int i) {
		return children.get(i);
	}
	
	public Iterable<Tree<T>> getChildren() {
		return children;
	}
	
	@Override
	public String toString() {
		return toString(0);
	}
	
	protected String toString(int tabs) {
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < tabs; ++i) {
			sb.append('\t');
		}
		
		sb.append(this.getRootValue().toString() + '\n');
		
		for (Tree<T> child : getChildren()) {
			if (child == null) {
				sb.append("null\n");
			} else {
				sb.append(child.toString(tabs + 1));
			}
		}
		
		return sb.toString();
	}
}
