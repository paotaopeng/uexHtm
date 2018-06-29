// Generated code from Butter Knife. Do not modify!
package com.wisemen.demo.taskrunner;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DownloadActivity$$ViewBinder<T extends com.wisemen.demo.taskrunner.DownloadActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492982, "field 'toolbar'");
    target.toolbar = finder.castView(view, 2131492982, "field 'toolbar'");
    view = finder.findRequiredView(source, 2131492983, "field 'targetFolder'");
    target.targetFolder = finder.castView(view, 2131492983, "field 'targetFolder'");
    view = finder.findRequiredView(source, 2131492984, "field 'tvCorePoolSize'");
    target.tvCorePoolSize = finder.castView(view, 2131492984, "field 'tvCorePoolSize'");
    view = finder.findRequiredView(source, 2131492985, "field 'sbCorePoolSize'");
    target.sbCorePoolSize = finder.castView(view, 2131492985, "field 'sbCorePoolSize'");
    view = finder.findRequiredView(source, 2131492986, "field 'recyclerView'");
    target.recyclerView = finder.castView(view, 2131492986, "field 'recyclerView'");
  }

  @Override public void unbind(T target) {
    target.toolbar = null;
    target.targetFolder = null;
    target.tvCorePoolSize = null;
    target.sbCorePoolSize = null;
    target.recyclerView = null;
  }
}
