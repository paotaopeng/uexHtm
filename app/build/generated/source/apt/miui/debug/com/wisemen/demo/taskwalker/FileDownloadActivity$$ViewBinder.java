// Generated code from Butter Knife. Do not modify!
package com.wisemen.demo.taskwalker;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class FileDownloadActivity$$ViewBinder<T extends com.wisemen.demo.taskwalker.FileDownloadActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492995, "field 'btnFileDownload' and method 'fileDownload'");
    target.btnFileDownload = finder.castView(view, 2131492995, "field 'btnFileDownload'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.fileDownload(p0);
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
    target.btnFileDownload = null;
    target.tvDownloadSize = null;
    target.tvProgress = null;
    target.tvNetSpeed = null;
    target.pbProgress = null;
  }
}
