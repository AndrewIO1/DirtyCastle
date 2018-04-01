package util;

public class PriorityQueue<T> {


	Node head;
	int size = 0;

	public PriorityQueue(){
		head = new Node();
	}

	public void add(T v, float f){
		Node cur = head;
		while(cur.right != null){
			if(cur.right.priority >= f){
				break;
			}
			cur = cur.right;
		}
		cur.right = new Node(v,f,cur,cur.right);
		size++;
	}

	public int size(){
		return size;
	}

	public T poll(){
		if(size == 0){
			return null;
		}
		T r = head.right.data;

		if(size == 1){
			head.right = null;
		}else{
			head.right.right.left = head;
			head.right = head.right.right;
		}

		size--;
		return r;
	}

	public void remove(int i) {
		Node cur = head;
		int iter = 0;
		while(cur.right != null && iter != i){
			cur = cur.right;
			iter++;
		}
		if(cur.right == null) {
			System.out.println("Queue error: index outside boundaries");
			return;
		}

		if(cur.right.right != null) {
			cur.right.right.left = cur;
		}
		cur.right = cur.right.right;
		size--;

	}
	
	public void remove(T object) {
		Node cur = head;
		while(cur.right != null && !cur.right.data.equals(object)){
			cur = cur.right;
		}
		if(cur.right == null) {
			//System.out.println("Queue error: object for deletion not found");
			return;
		}

		if(cur.right.right != null) {
			cur.right.right.left = cur;
		}
		cur.right = cur.right.right;
		size--;
	}

	public T peek(int i){
		Node cur = head;
		int iter = 0;
		while(cur.right != null && iter != i){
			cur = cur.right;
			iter++;
		}
		return cur.right.data;
	}

	public boolean contains(T v){
		Node cur = head;
		while(cur.right != null){
			if(cur.right.data == v){
				return true;
			}
			cur = cur.right;
		}
		return false;
	}
	
	public void clear() {
		if(head.right == null) {
			size = 0;
			return;
		}
		head.right.left = null;
		head.right = null;
		size = 0;
	}
	
	
	private class Node {
		private T data;
		@SuppressWarnings("unused")
		//TODO возможно стоит убрать указатель на элемент слева
		private Node left;
		private Node right;
		private float priority;

		public Node(T data, float f, Node left, Node right){
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
}
