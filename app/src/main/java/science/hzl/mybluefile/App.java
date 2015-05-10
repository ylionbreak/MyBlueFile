package science.hzl.mybluefile;

import android.app.Application;
import android.content.Context;

/**
 * Created by YLion on 2015/5/7.
 */
public class App extends Application {
	private static Context mContext;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
	}	//haha
	
	public static Context getContext() {
		return mContext;
	}

}
