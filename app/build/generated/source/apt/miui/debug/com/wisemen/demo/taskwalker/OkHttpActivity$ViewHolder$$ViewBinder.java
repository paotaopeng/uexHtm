// Generated code from Butter Knife. Do not modify!
package com.wisemen.demo.taskwalker;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class OkHttpActivity$ViewHolder$$ViewBinder<T extends com.wisemen.demo.taskwalker.OkHttpActivity.ViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492937, "field 'title'");
    target.title = finder.castView(view, 2131492937, "field 'title'");
    view = finder.findRequiredView(source, 2131493027, "field 'des'");
    target.des = finder.castView(view, 2131493027, "field 'des'");
  }

  @Override public void unbind(T target) {
    target.title = null;
    target.des = null;
  }
}
