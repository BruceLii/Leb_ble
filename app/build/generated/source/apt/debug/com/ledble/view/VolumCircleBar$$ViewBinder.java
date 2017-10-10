// Generated code from Butter Knife. Do not modify!
package com.ledble.view;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class VolumCircleBar$$ViewBinder<T extends com.ledble.view.VolumCircleBar> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558629, "field 'volumBar'");
    target.volumBar = finder.castView(view, 2131558629, "field 'volumBar'");
  }

  @Override public void unbind(T target) {
    target.volumBar = null;
  }
}
