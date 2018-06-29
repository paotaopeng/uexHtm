// Generated code from Butter Knife. Do not modify!
package com.wisemen.demo.taskwalker;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class HttpsActivity$$ViewBinder<T extends com.wisemen.demo.taskwalker.HttpsActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492998, "method 'btn_none_https_request'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.btn_none_https_request(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492999, "method 'btn_https_request'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.btn_https_request(p0);
        }
      });
  }

  @Override public void unbind(T target) {
  }
}
