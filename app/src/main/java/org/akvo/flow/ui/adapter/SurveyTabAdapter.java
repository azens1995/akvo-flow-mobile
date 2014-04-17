package org.akvo.flow.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;

import org.akvo.flow.dao.SurveyDbAdapter;
import org.akvo.flow.domain.Dependency;
import org.akvo.flow.domain.Question;
import org.akvo.flow.domain.QuestionGroup;
import org.akvo.flow.domain.QuestionResponse;
import org.akvo.flow.event.QuestionInteractionEvent;
import org.akvo.flow.event.QuestionInteractionListener;
import org.akvo.flow.event.SurveyListener;
import org.akvo.flow.ui.view.QuestionListView;
import org.akvo.flow.ui.view.QuestionView;
import org.akvo.flow.ui.view.SubmitTab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurveyTabAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener,
        ActionBar.TabListener {
    private Context mContext;
    private SurveyListener mSurveyListener;
    private QuestionInteractionListener mQuestionListener;
    private SurveyDbAdapter mDatabase;

    private ActionBar mActionBar;
    private ViewPager mPager;
    private List<QuestionGroup> mQuestionGroups;
    private List<QuestionListView> mQuestionListViews;
    private SubmitTab mSubmitTab;

    public SurveyTabAdapter(Context context, ActionBar actionBar, ViewPager pager,
            SurveyDbAdapter database, SurveyListener surveyListener,
            QuestionInteractionListener questionListener) {
        mContext = context;
        mDatabase = database;
        mSurveyListener = surveyListener;
        mQuestionListener = questionListener;
        mActionBar = actionBar;
        mPager = pager;

        mQuestionGroups = mSurveyListener.getQuestionGroups();
        mQuestionListViews = new ArrayList<QuestionListView>();
    }

    public void notifyOptionsChanged() {
        for (QuestionListView questionListView : mQuestionListViews) {
            questionListView.notifyOptionsChanged();// Spread the word
        }
    }

    public void load() {
        for (QuestionGroup group : mQuestionGroups) {
            QuestionListView questionListView = new QuestionListView(mContext, group, mDatabase,
                    mSurveyListener, mQuestionListener);
            questionListView.load();
            mQuestionListViews.add(questionListView);
        }

        // Now that all the tabs are populated, we setup the dependencies
        setupDependencies();

        mSubmitTab = new SubmitTab(mContext, mSurveyListener);

        // Setup the tabs in the action bar
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        for (QuestionGroup group : mQuestionGroups) {
            mActionBar.addTab(mActionBar.newTab()
                    .setText(group.getHeading())
                    .setTabListener(this));
        }

        mActionBar.addTab(mActionBar.newTab()
                .setText("Submit")// TODO: Externalize string
                .setTabListener(this));

        mPager.setOnPageChangeListener(this);
    }

    public void loadState(Map<String, QuestionResponse> responses, boolean prefill) {
        for (QuestionListView questionListView : mQuestionListViews) {
            questionListView.loadState(responses, prefill);
        }
    }

    public void saveState(long surveyInstanceId) {
        for (QuestionListView questionListView : mQuestionListViews) {
            questionListView.saveState(surveyInstanceId);
        }
    }

    public void onQuestionComplete(String questionId, Bundle data) {
        for (QuestionListView questionListView : mQuestionListViews) {
            questionListView.onQuestionComplete(questionId, data);
        }
    }

    private QuestionView getQuestionView(String questionId) {
        QuestionView questionView = null;
        for (QuestionListView questionListView : mQuestionListViews) {
            questionView = questionListView.getQuestionView(questionId);
            if (questionView != null) {
                break;
            }
        }
        return questionView;
    }

    /**
     * Sets up question dependencies across question groups and registers
     * questionInteractionListeners on the dependent views. This should be
     * called each time a new tab is hydrated. It will iterate over all
     * questions in the survey and install dependencies and the
     * questionInteractionListeners. After installation, it will check to see if
     * the parent question contains a response. If so, it will fire a
     * questionInteractionEvent to ensure dependent questions are put into the
     * correct state
     */
    private void setupDependencies() {
        for (QuestionGroup group : mQuestionGroups) {
            for (Question question : group.getQuestions()) {// TODO: Add getQuestions() to Survey
                setupDependencies(question);
            }
        }
    }

    private void setupDependencies(Question question) {
        final List<Dependency> dependencies = question.getDependencies();

        if (dependencies == null) {
            return;// No dependencies for this question
        }

        for (Dependency dependency : dependencies) {
            QuestionView parentQ = getQuestionView(dependency.getQuestion());
            QuestionView depQ = getQuestionView(question.getId());
            if (depQ != null && parentQ != null && depQ != parentQ) {
                parentQ.addQuestionInteractionListener(depQ);

                if (parentQ.getResponse(true) != null && parentQ.getResponse(true).hasValue()) {
                    // Trigger event, the parent already contains a response
                    QuestionInteractionEvent event = new QuestionInteractionEvent(
                            QuestionInteractionEvent.QUESTION_ANSWER_EVENT, parentQ);
                    depQ.onQuestionInteraction(event);
                }
            }
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;
        if (position < mQuestionListViews.size()) {
            view = mQuestionListViews.get(position);// Already instantiated
        } else {
            view = mSubmitTab;
        }
        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return mQuestionGroups.size() + 1;// Do not forget the submit tab
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == mQuestionListViews.size()) {
            mSubmitTab.refresh(checkInvalidQuestions());
        }

        // Select the corresponding tab
        mActionBar.setSelectedNavigationItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        final int position = tab.getPosition();
        if (position == mQuestionListViews.size()) {
            mSubmitTab.refresh(checkInvalidQuestions());
        }
        mPager.setCurrentItem(position);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * checks if all the mandatory questions (on all tabs) have responses
     *
     * @return
     */
    private List<Question> checkInvalidQuestions() {
        Map<String, QuestionResponse> responseMap = new HashMap<String, QuestionResponse>();
        ArrayList<Question> invalidQuestions = new ArrayList<Question>();
        List<Question> candidateInvalidQuestions = new ArrayList<Question>();
        for (QuestionListView questionListView : mQuestionListViews) {
            // Add this tab's responses to the map.
            Map<String, QuestionResponse> responses = questionListView.getResponses();
            responseMap.putAll(responses);
            candidateInvalidQuestions.addAll(questionListView.checkInvalidQuestions());
        }

        // now make sure that the candidate missing questions are really
        // missing by seeing if their dependencies are fulfilled
        for (Question q : candidateInvalidQuestions) {
            if (areDependenciesSatisfied(q, responseMap)) {
                invalidQuestions.add(q);
            }
        }

        return invalidQuestions;
    }

    /**
     * Checks if the dependencies for the question passed in are satisfied
     *
     * @param q Question to check dependencies for
     * @param responses All the responses for this survey
     * @return true if no dependency is broken, false otherwise
     */
    private boolean areDependenciesSatisfied(Question q, Map<String, QuestionResponse> responses) {
        List<Dependency> dependencies = q.getDependencies();
        if (dependencies != null) {
            for (Dependency dependency : dependencies) {
                QuestionResponse resp = responses.get(dependency.getQuestion());
                if (resp == null || !resp.hasValue()
                        || !dependency.isMatch(resp.getValue())
                        || !resp.getIncludeFlag()) {
                    return false;
                }
            }
        }
        return true;
    }

}
