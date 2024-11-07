package uk.co.stikman.eventbus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * this is thread-safe in immediate mode, but not otherwise
 */
public class StringEventBus {
	private static final class Subscriber {
		String	key;
		Method	method;
		Object	object;

		private Subscriber(String key, Method method, Object object) {
			super();
			this.key = key;
			this.method = method;
			this.object = object;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((method == null) ? 0 : method.hashCode());
			result = prime * result + ((object == null) ? 0 : object.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Subscriber other = (Subscriber) obj;
			if (method == null) {
				if (other.method != null)
					return false;
			} else if (!method.equals(other.method))
				return false;
			if (object == null) {
				if (other.object != null)
					return false;
			} else if (!object.equals(other.object))
				return false;
			return true;
		}
	}

	private static final class Event {
		String				name;
		Subscriber			subscriber;
		Object				data;
		StackTraceElement[]	callstack;

		private Event(String name, Subscriber subscriber, Object data, boolean enableStackTraces) {
			super();
			if (enableStackTraces) {
				StackTraceElement[] cs = Thread.currentThread().getStackTrace();
				// 
				// remove anything in this package. Note we start from 1 since the first
				// element will be Thread.getStackTrace
				//
				int j = -1;
				for (int i = 1; i < cs.length; ++i) {
					if (!cs[i].getClassName().startsWith(StringEventBus.class.getPackage().getName())) {
						j = i;
						break;
					}
				}
				if (j == -1) // this has gone badly - might happen if someone subclasses StringEventBus?
					callstack = cs;
				callstack = new StackTraceElement[cs.length - j];
				for (int i = j; i < cs.length; ++i)
					callstack[i - j] = cs[i];
			} else
				callstack = null;
			this.name = name;
			this.subscriber = subscriber;
			this.data = data;
		}

	}

	private Set<Subscriber>			subscribers			= new HashSet<>();
	private Set<Object>				subscriberObjects	= new HashSet<>();
	private List<Event>				pending				= new ArrayList<>();
	private static StringEventBus	INSTANCE;
	private boolean					immediateMode		= false;
	private boolean					enableStackTraces	= true;
	private ReadWriteLock			lock				= new ReentrantReadWriteLock();

	/**
	 * Calling this twice for the same {@link Object} and {@link Method} will have no effect
	 * 
	 * @param who
	 */
	public void register(Object who) {
		lock.writeLock().lock();
		try {
			if (subscriberObjects.contains(who))
				return;
			subscriberObjects.add(who);
			Class<?> cls = who.getClass();
			while (cls != null) {
				registerMethods(who, cls);
				cls = cls.getSuperclass();
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	private void registerMethods(Object who, Class<?> cls) {
		for (Method mthd : cls.getDeclaredMethods()) {
			Subscribe an = mthd.getAnnotation(Subscribe.class);
			if (an != null) {
				lock.writeLock().lock();
				try {
					subscribers.add(new Subscriber(an.value(), mthd, who));
				} finally {
					lock.writeLock().unlock();
				}
			}
			// TODO: check number of parameters
		}
	}

	public void unregister(Object who) {
		lock.readLock().lock();
		try {
			Iterator<Subscriber> i = subscribers.iterator();
			while (i.hasNext()) {
				Subscriber x = i.next();
				if (x.object.equals(who))
					i.remove();
			}

			if (!subscriberObjects.remove(who))
				System.err.println("WARNING: StringEventBus.unregister(Object) called for object that was never registered");
		} finally {
			lock.readLock().unlock();
		}
	}

	public void fire(String event) {
		fire(event, null);
	}

	public void fire(String event, Object data) {
		if (isImmediateMode()) {
			lock.readLock().lock();
			try {
				for (Subscriber s : subscribers)
					if (s.key.equals(event))
						call(s, data);
			} finally {
				lock.readLock().unlock();
			}
		} else {
			for (Subscriber s : subscribers)
				if (s.key.equals(event))
					pending.add(new Event(event, s, data, enableStackTraces));
		}
	}

	private static void call(Subscriber s, Object data) {
		try {
			if (s.method.getParameterCount() == 0)
				s.method.invoke(s.object);
			else if (s.method.getParameterCount() == 1)
				s.method.invoke(s.object, data);
			else
				throw new RuntimeException("Too many parameters to event subscriber");
		} catch (Throwable th) {
			throw new RuntimeException("Event dispatch failed", th);
		}
	}

	public static StringEventBus get() {
		if (INSTANCE == null)
			INSTANCE = new StringEventBus();
		return INSTANCE;
	}

	public boolean hasPendingEvents() {
		lock.readLock().lock();
		try {
			return !pending.isEmpty();
		} finally {
			lock.readLock().unlock();
		}
	}

	public void dispatch() {
		lock.readLock().lock();
		try {
			List<Event> tmp = new ArrayList<>(pending);
			pending.clear();
			for (Event e : tmp) {
				try {
					call(e.subscriber, e.data);
				} catch (Throwable th) {
					if (e.callstack == null) {
						System.err.println("Event dispatch failed. \"" + e.name + "\" Event's callstack is not available.");
					} else {
						System.err.println("Event dispatch failed. \"" + e.name + "\" Event fired at (exception follows):");
						for (StackTraceElement x : e.callstack)
							System.err.println("    " + x);
					}
					throw th;
				}
			}
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * In immediate mode calling {@link #fire(String, Object)} (or {@link #fire(String)}) causes any
	 * subscribers to be called immediately. When it's off (the default behaviour) it queues them up
	 * until you call the {@link #dispatch()} method
	 * 
	 * @return
	 */
	public boolean isImmediateMode() {
		return immediateMode;
	}

	/**
	 * In immediate mode calling {@link #fire(String, Object)} (or {@link #fire(String)}) causes any
	 * subscribers to be called immediately. When it's off (the default behaviour) it queues them up
	 * until you call the {@link #dispatch()} method
	 * 
	 * @return
	 */
	public void setImmediateMode(boolean immediateMode) {
		this.immediateMode = immediateMode;
	}

	/**
	 * When <code>true</code> each invocation of {@link #fire(String)} or {@link #fire(String, Object)}
	 * records the stack trace at that point, so that in the event of an exception being thrown it'll
	 * get logged for ease of debugging
	 * 
	 */
	public boolean isEnableStackTraces() {
		return enableStackTraces;
	}

	/**
	 * When <code>true</code> each invocation of {@link #fire(String)} or {@link #fire(String, Object)}
	 * records the stack trace at that point, so that in the event of an exception being thrown it'll
	 * get logged for ease of debugging
	 * 
	 * @param enableStackTraces
	 */
	public void setEnableStackTraces(boolean enableStackTraces) {
		this.enableStackTraces = enableStackTraces;
	}

}
