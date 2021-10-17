package window;

import android.app.Activity;
import android.view.WindowManager;

public class fullscreen {
    Activity context;
    public fullscreen(Activity context)
    {
        this.context = context;
        this.context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
