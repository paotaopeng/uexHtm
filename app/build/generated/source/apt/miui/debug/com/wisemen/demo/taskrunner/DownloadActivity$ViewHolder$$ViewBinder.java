// Generated code from Butter Knife. Do not modify!
package com.wisemen.demo.taskrunner;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DownloadActivity$ViewHolder$$ViewBinder<T extends com.wisemen.demo.taskrunner.DownloadActivity.ViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492993, "field 'name'");
    target.name = finder.castView(view, 2131492993, "field 'name'");
    view = finder.findRequiredView(source, 2131493026, "field 'download'");
    target.download = finder.castView(view, 2131493026, "field 'download'");
  }

  @Override public void unbind(T target) {
    target.name = null;
    target.download = null;
  }
}
