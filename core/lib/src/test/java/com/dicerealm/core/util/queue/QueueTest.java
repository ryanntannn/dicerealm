package com.dicerealm.core.util.queue;

import org.junit.jupiter.api.Test;

public class QueueTest {
	@Test
	public void testEnqueue() {
		Queue<Integer> queue = new Queue<Integer>();

		queue.enqueue(1);
		queue.enqueue(2);
		queue.enqueue(3);

		assert(queue.peek() == 1);
	}

	@Test
	public void testDequeue() {
		Queue<Integer> queue = new Queue<Integer>();

		queue.enqueue(1);
		queue.enqueue(2);
		queue.enqueue(3);

		assert(queue.dequeue() == 1);
		assert(queue.dequeue() == 2);
		assert(queue.dequeue() == 3);

		assert(queue.isEmpty());
	}
}
