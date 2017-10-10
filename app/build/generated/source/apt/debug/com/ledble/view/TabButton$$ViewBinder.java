// Generated code from Butter Knife. Do not modify!
package com.ledble.view;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class TabButton$$ViewBinder<T extends com.ledble.view.TabButton> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558708, "field 'ivTabImage'");
    target.ivTabImage = finder.castView(view, 2131558708, "field 'ivTabImage'");
    view = finder.findRequiredView(source, 2131558710, "field 'tvTabName'");
    target.tvTabName = finder.castView(view, 2131558710, "field 'tvTabName'");
    view = finder.findRequiredView(source, 2131558712, "field 'ivTabImage_above'");
    target.ivTabImage_above = finder.castView(view, 2131558712, "field 'ivTabImage_above'");
    view = finder.findRequiredView(source, 2131558713, "field 'tvTabName_above'");
    target.tvTabName_above = finder.castView(view, 2131558713, "field 'tvTabName_above'");
    view = finder.findRequiredView(source, 2131558707, "field 'llBelow'");
    target.llBelow = finder.castView(view, 2131558707, "field 'llBelow'");
    view = finder.findRequiredView(source, 2131558711, "field 'llAbove'");
    target.llAbove = finder.castView(view, 2131558711, "field 'llAbove'");
    view = finder.findRequiredView(source, 2131558709, "field 'tvCount'");
    target.tvCount = finder.castView(view, 2131558709, "field 'tvCount'");
  }

  @Override public void unbind(T target) {
    target.ivTabImage = null;
    target.tvTabName = null;
    target.ivTabImage_above = null;
    target.tvTabName_above = null;
    target.llBelow = null;
    target.llAbove = null;
    target.tvCount = null;
  }
}
