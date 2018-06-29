// Generated code from Butter Knife. Do not modify!
package com.wisemen.demo.taskwalker;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class JsonRequestActivity$$ViewBinder<T extends com.wisemen.demo.taskwalker.JsonRequestActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492979, "method 'requestJson'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.requestJson(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492980, "method 'requestJsonArray'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.requestJsonArray(p0);
        }
      });
  }

  @Override public void unbind(T target) {
  }
}
