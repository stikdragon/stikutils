package uk.co.stikman.utils;

import java.io.IOException;

/***
 * Streamable objects must have a default constructor too!
 * 
 * @author Stik
 *
 */
public interface Streamable {
	void toStream(ObjectOutputStream oos) throws IOException;

	void fromStream(ObjectInputStream iis, int version) throws IOException;
}
