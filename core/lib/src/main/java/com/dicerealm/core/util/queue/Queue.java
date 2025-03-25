package com.dicerealm.core.util.queue;

public class Queue<T> {
	private Node<T> head;
	private Node<T> tail;
	private int size;

	public Queue() {
		head = null;
		tail = null;
		size = 0;
	}

	public void enqueue(T data) {
		Node<T> node = new Node<T>(data);
		if (head == null) {
			head = node;
			tail = node;
		} else {
			tail.setNext(node);
			tail = node;
		}
		size++;
	}

	public T dequeue() {
		if (head == null) {
			return null;
		}
		T data = head.getData();
		head = head.getNext();
		size--;
		return data;
	}

	public T peek() {
		if (head == null) {
			return null;
		}
		return head.getData();
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

}
