package rs.reviewer;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.TextView;

/**
 * Created by milossimic on 3/10/16.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity activity;

    public MainActivityTest(){
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
    }
}
