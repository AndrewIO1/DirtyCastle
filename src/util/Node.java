package util;

public class Node<T> {
	T data;
	Node<T> left;
	Node<T> right;
	float priority;

	public Node(T data, float f, Node<T> left, Node<T> right){
		this.data = data;
		this.right = right;
		this.left = left;
		this.priority = f;
	}

	public Node(){
		data = null;
		left = null;
		right = null;
	}
}
