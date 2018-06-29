// Generated code from Butter Knife. Do not modify!
package com.wisemen.demo.taskwalker;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class BitmapRequestActivity$$ViewBinder<T extends com.wisemen.demo.taskwalker.BitmapRequestActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492978, "field 'imageView'");
    target.imageView = finder.castView(view, 2131492978, "field 'imageView'");
    view = finder.findRequiredView(source, 2131492977, "method 'requestJson'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.requestJson(p0);
        }
      });
  }

  @Override public void unbind(T target) {
    target.imageView = null;
  }
}
