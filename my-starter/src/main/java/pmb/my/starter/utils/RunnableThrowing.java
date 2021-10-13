package pmb.my.starter.utils;

import pmb.my.starter.exception.MajorException;

@FunctionalInterface
public interface RunnableThrowing {

	void run() throws MajorException;

}
