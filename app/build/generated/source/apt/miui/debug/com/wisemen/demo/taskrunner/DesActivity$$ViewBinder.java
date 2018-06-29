// Generated code from Butter Knife. Do not modify!
package com.wisemen.demo.taskrunner;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DesActivity$$ViewBinder<T extends com.wisemen.demo.taskrunner.DesActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492982, "field 'toolbar'");
    target.toolbar = finder.castView(view, 2131492982, "field 'toolbar'");
    view = finder.findRequiredView(source, 2131492993, "field 'name'");
    target.name = finder.castView(view, 2131492993, "field 'name'");
    view = finder.findRequiredView(source, 2131492994, "field 'url'");
    target.url = finder.castView(view, 2131492994, "field 'url'");
    view = finder.findRequiredView(source, 2131492989, "field 'downloadSize'");
    target.downloadSize = finder.castView(view, 2131492989, "field 'downloadSize'");
    view = finder.findRequiredView(source, 2131492990, "field 'tvProgress'");
    target.tvProgress = finder.castView(view, 2131492990, "field 'tvProgress'");
    view = finder.findRequiredView(source, 2131492991, "field 'netSpeed'");
    target.netSpeed = finder.castView(view, 2131492991, "field 'netSpeed'");
    view = finder.findRequiredView(source, 2131492992, "field 'pbProgress'");
    target.pbProgress = finder.castView(view, 2131492992, "field 'pbProgress'");
    view = finder.findRequiredView(source, 2131492906, "field 'download'");
    target.download = finder.castView(view, 2131492906, "field 'download'");
    view = finder.findRequiredView(source, 2131492987, "field 'remove'");
    target.remove = finder.castView(view, 2131492987, "field 'remove'");
    view = finder.findRequiredView(source, 2131492988, "field 'restart'");
    target.restart = finder.castView(view, 2131492988, "field 'restart'");
  }

  @Override public void unbind(T target) {
    target.toolbar = null;
    target.name = null;
    target.url = null;
    target.downloadSize = null;
    target.tvProgress = null;
    target.netSpeed = null;
    target.pbProgress = null;
    target.download = null;
    target.remove = null;
    target.restart = null;
  }
}
