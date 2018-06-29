// Generated code from Butter Knife. Do not modify!
package com.wisemen.demo;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$$ViewBinder<T extends com.wisemen.demo.MainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492982, "field 'toolbar'");
    target.toolbar = finder.castView(view, 2131492982, "field 'toolbar'");
    view = finder.findRequiredView(source, 2131492986, "field 'recyclerView'");
    target.recyclerView = finder.castView(view, 2131492986, "field 'recyclerView'");
    view = finder.findRequiredView(source, 2131493001, "method 'fab'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.fab(p0);
        }
      });
  }

  @Override public void unbind(T target) {
    target.toolbar = null;
    target.recyclerView = null;
  }
}
