/*
 * Copyright (C) 2018 Stichting Akvo (Akvo Foundation)
 *
 * This file is part of Akvo Flow.
 *
 * Akvo Flow is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Akvo Flow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Akvo Flow.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.akvo.flow.presentation.form;

import org.akvo.flow.domain.interactor.DefaultObserver;
import org.akvo.flow.domain.interactor.ExportSurveyInstances;
import org.akvo.flow.domain.interactor.UseCase;
import org.akvo.flow.presentation.Presenter;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import timber.log.Timber;

public class FormPresenter implements Presenter {

    private final UseCase exportSurveyInstances;
    private FormView view;

    @Inject
    public FormPresenter(@Named("exportSurveyInstances")
            UseCase exportSurveyInstances) {
        this.exportSurveyInstances = exportSurveyInstances;
    }

    @Override
    public void destroy() {
        exportSurveyInstances.dispose();
    }

    public void setView(FormView view) {
        this.view = view;
    }

    public void onSubmitPressed(long surveyInstanceId) {
        view.showLoading();
        Map<String, Object> params = new HashMap<>(2);
        params.put(ExportSurveyInstances.SURVEY_INSTANCE_ID_PARAM, surveyInstanceId);
        exportSurveyInstances.execute(new DefaultObserver<Boolean>() {
            @Override
            public void onError(Throwable e) {
                Timber.e(e);
                view.hideLoading();
                view.showErrorExport();
            }

            @Override
            public void onNext(Boolean aBoolean) {
                view.hideLoading();
                view.dismiss();
            }
        }, params);
    }
}
