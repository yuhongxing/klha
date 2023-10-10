
package mylib.utils;

import java.io.IOException;

public class BadHttpStatusException extends IOException {

	private static final long serialVersionUID = -8455513138673143528L;
	public int mRetCode;

    public BadHttpStatusException(int code) {
        mRetCode = code;
    }
    
    public String toString(){
        return ""+mRetCode;
    }
}
