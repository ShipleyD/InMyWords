package com.example.inmywords.View;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.inmywords.R;
import com.example.inmywords.View.HomeActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class HomeScreenTest {

    @Rule
    public ActivityTestRule<HomeActivity> mHomeActivityActivityTestRule =
            new ActivityTestRule<HomeActivity>(HomeActivity.class);

    @Test
    public void clickSearchButton_OpenSearchUi() throws Exception {
        onView(withId(R.id.btnHSearch)).perform(click());
        onView(withId(R.id.searchWord)).check(matches(isDisplayed()));
    }
}
