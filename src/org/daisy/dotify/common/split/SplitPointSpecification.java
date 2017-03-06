package org.daisy.dotify.common.split;

public class SplitPointSpecification {
	enum Type {
		ALL,
		FORWARD_ALL,
		NONE,
		EMPTY,
		INDEX;
	}
	private static final SplitPointSpecification INSTANCE_NONE = new SplitPointSpecification(Type.NONE);
	private static final SplitPointSpecification INSTANCE_EMPTY = new SplitPointSpecification(Type.EMPTY);
	private static final SplitPointSpecification INSTANCE_ALL = new SplitPointSpecification(Type.ALL);
	private static final SplitPointSpecification INSTANCE_FORWARD_ALL = new SplitPointSpecification(Type.FORWARD_ALL);
	private final int index;
	private final boolean hard;
	private final Type type;

	private SplitPointSpecification(Type type) {
		if (type==Type.INDEX) {
			throw new IllegalArgumentException("This constructor doesn't support type:  " + Type.INDEX);
		}
		this.type = type;
		this.hard = false;
		this.index = -1;
	}

	public SplitPointSpecification(int index, boolean hard) {
		if (index<0) {
			throw new IllegalArgumentException("Index out of bounds: " + index);
		}
		this.type = Type.INDEX;
		this.hard = hard;
		this.index = index;
	}
	
	public static SplitPointSpecification none() {
		return INSTANCE_NONE;
	}
	
	public static SplitPointSpecification empty() {
		return INSTANCE_EMPTY;
	}
	
	public static SplitPointSpecification all() {
		return INSTANCE_ALL;
	}
	
	public static SplitPointSpecification forwardAll() {
		return INSTANCE_FORWARD_ALL;
	}
	
	public Type getType() {
		return type;
	}
		
	public int getIndex() {
		if (type!=Type.INDEX) {
			throw new IllegalStateException("This type doesn't support this method.");
		}
		return index;
	}
	
	public boolean isHard() {
		if (type!=Type.INDEX) {
			throw new IllegalStateException("This type doesn't support this method.");
		}
		return hard;
	}

}
