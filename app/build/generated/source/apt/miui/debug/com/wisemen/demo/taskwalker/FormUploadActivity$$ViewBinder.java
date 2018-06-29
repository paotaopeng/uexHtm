// Generated code from Butter Knife. Do not modify!
package com.wisemen.demo.taskwalker;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class FormUploadActivity$$ViewBinder<T extends com.wisemen.demo.taskwalker.FormUploadActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492997, "field 'btnFormUpload' and method 'formUpload'");
    target.btnFormUpload = finder.castView(view, 2131492997, "field 'btnFormUpload'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.formUpload(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492989, "field 'tvDownloadSize'");
    target.tvDownloadSize = finder.castView(view, 2131492989, "field 'tvDownloadSize'");
    view = finder.findRequiredView(source, 2131492990, "field 'tvProgress'");
    target.tvProgress = finder.castView(view, 2131492990, "field 'tvProgress'");
    view = finder.findRequiredView(source, 2131492991, "field 'tvNetSpeed'");
    target.tvNetSpeed = finder.castView(view, 2131492991, "field 'tvNetSpeed'");
    view = finder.findRequiredView(source, 2131492992, "field 'pbProgress'");
    target.pbProgress = finder.castView(view, 2131492992, "field 'pbProgress'");
  }

  @Override public void unbind(T target) {
    target.btnFormUpload = null;
    target.tvDownloadSize = null;
    target.tvProgress = null;
    target.tvNetSpeed = null;
    target.pbProgress = null;
  }
}
