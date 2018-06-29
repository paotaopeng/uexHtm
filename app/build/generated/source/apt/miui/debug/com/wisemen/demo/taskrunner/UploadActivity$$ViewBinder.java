// Generated code from Butter Knife. Do not modify!
package com.wisemen.demo.taskrunner;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class UploadActivity$$ViewBinder<T extends com.wisemen.demo.taskrunner.UploadActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492982, "field 'toolbar'");
    target.toolbar = finder.castView(view, 2131492982, "field 'toolbar'");
    view = finder.findRequiredView(source, 2131492984, "field 'tvCorePoolSize'");
    target.tvCorePoolSize = finder.castView(view, 2131492984, "field 'tvCorePoolSize'");
    view = finder.findRequiredView(source, 2131492985, "field 'sbCorePoolSize'");
    target.sbCorePoolSize = finder.castView(view, 2131492985, "field 'sbCorePoolSize'");
    view = finder.findRequiredView(source, 2131493008, "method 'upload'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.upload(p0);
        }
      });
    view = finder.findRequiredView(source, 2131493009, "method 'local'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.local(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492996, "method 'cancel'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.cancel(p0);
        }
      });
  }

  @Override public void unbind(T target) {
    target.toolbar = null;
    target.tvCorePoolSize = null;
    target.sbCorePoolSize = null;
  }
}
