// Generated code from Butter Knife. Do not modify!
package com.ledble.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class TimerFragment$$ViewBinder<T extends com.ledble.fragment.TimerFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558658, "field 'linearLayoutTimerOn'");
    target.linearLayoutTimerOn = finder.castView(view, 2131558658, "field 'linearLayoutTimerOn'");
    view = finder.findRequiredView(source, 2131558662, "field 'linearLayoutTimerOff'");
    target.linearLayoutTimerOff = finder.castView(view, 2131558662, "field 'linearLayoutTimerOff'");
    view = finder.findRequiredView(source, 2131558659, "field 'textViewOnTime'");
    target.textViewOnTime = finder.castView(view, 2131558659, "field 'textViewOnTime'");
    view = finder.findRequiredView(source, 2131558663, "field 'textViewOffTime'");
    target.textViewOffTime = finder.castView(view, 2131558663, "field 'textViewOffTime'");
    view = finder.findRequiredView(source, 2131558660, "field 'textViewModelText'");
    target.textViewModelText = finder.castView(view, 2131558660, "field 'textViewModelText'");
    view = finder.findRequiredView(source, 2131558661, "field 'toggleButtonOn'");
    target.toggleButtonOn = finder.castView(view, 2131558661, "field 'toggleButtonOn'");
    view = finder.findRequiredView(source, 2131558664, "field 'toggleButtonOff'");
    target.toggleButtonOff = finder.castView(view, 2131558664, "field 'toggleButtonOff'");
  }

  @Override public void unbind(T target) {
    target.linearLayoutTimerOn = null;
    target.linearLayoutTimerOff = null;
    target.textViewOnTime = null;
    target.textViewOffTime = null;
    target.textViewModelText = null;
    target.toggleButtonOn = null;
    target.toggleButtonOff = null;
  }
}
