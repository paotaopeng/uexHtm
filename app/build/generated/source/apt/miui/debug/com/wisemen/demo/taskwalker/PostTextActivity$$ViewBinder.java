// Generated code from Butter Knife. Do not modify!
package com.wisemen.demo.taskwalker;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class PostTextActivity$$ViewBinder<T extends com.wisemen.demo.taskwalker.PostTextActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493004, "method 'postJson'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.postJson(p0);
        }
      });
    view = finder.findRequiredView(source, 2131493005, "method 'postString'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.postString(p0);
        }
      });
    view = finder.findRequiredView(source, 2131493006, "method 'postBytes'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.postBytes(p0);
        }
      });
  }

  @Override public void unbind(T target) {
  }
}
