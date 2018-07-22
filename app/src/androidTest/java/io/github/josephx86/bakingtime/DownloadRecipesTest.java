package io.github.josephx86.bakingtime;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class DownloadRecipesTest {

    @Rule
    public ActivityTestRule<MainActivity> nMainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void checkRecipesDownloading() {
        onView((withId(R.id.message_tv))).check(matches(withText(R.string.getting_recipes)));
    }
}
