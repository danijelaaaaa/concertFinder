package rs.reviewer;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

import model.NavItem;

/**
 * Created by milossimic on 3/10/16.
 */
public class ModelUnitTest extends TestCase {

    NavItem item;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        item = new NavItem("Home", "Here is home of this app", R.drawable.ic_action_map);
    }

    @SmallTest
    public void testIsItemCreated(){
        assertNotNull("NavItem bi trebalo da je kreiran", item);
    }

    @SmallTest
    public void testIfIconIDNotNegative(){
        assertNotSame("ID ikone ne bi trebao biti -1", item.getmIcon(), -1);
    }

}
