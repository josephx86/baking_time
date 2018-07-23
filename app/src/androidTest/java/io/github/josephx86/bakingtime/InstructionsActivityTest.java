package io.github.josephx86.bakingtime;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class InstructionsActivityTest {
    @Rule
    public ActivityTestRule<InstructionsActivity> instructionsActivityActivityTestRule = new ActivityTestRule<>(InstructionsActivity.class);

    @Before
    public void initializeFragmentManagre() {
        instructionsActivityActivityTestRule.getActivity().getSupportFragmentManager().beginTransaction();
    }

    @Test
    public void nextStepbuttonClickShownInPortraitMode() {
        // Check button has been displayed
        onView((withId(R.id.next_b))).check(matches(withText(R.string.next_step)));

    }
}
