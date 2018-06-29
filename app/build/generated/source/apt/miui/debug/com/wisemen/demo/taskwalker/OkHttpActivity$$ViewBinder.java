// Generated code from Butter Knife. Do not modify!
package com.wisemen.demo.taskwalker;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class OkHttpActivity$$ViewBinder<T extends com.wisemen.demo.taskwalker.OkHttpActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492982, "field 'toolbar'");
    target.toolbar = finder.castView(view, 2131492982, "field 'toolbar'");
    view = finder.findRequiredView(source, 2131492986, "field 'recyclerView'");
    target.recyclerView = finder.castView(view, 2131492986, "field 'recyclerView'");
  }

  @Override public void unbind(T target) {
    target.toolbar = null;
    target.recyclerView = null;
  }
}
