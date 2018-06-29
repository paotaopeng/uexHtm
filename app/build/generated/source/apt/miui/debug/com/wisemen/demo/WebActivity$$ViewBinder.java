// Generated code from Butter Knife. Do not modify!
package com.wisemen.demo;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class WebActivity$$ViewBinder<T extends com.wisemen.demo.WebActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492982, "field 'toolbar'");
    target.toolbar = finder.castView(view, 2131492982, "field 'toolbar'");
    view = finder.findRequiredView(source, 2131493010, "field 'pb'");
    target.pb = finder.castView(view, 2131493010, "field 'pb'");
    view = finder.findRequiredView(source, 2131493011, "field 'webView'");
    target.webView = finder.castView(view, 2131493011, "field 'webView'");
  }

  @Override public void unbind(T target) {
    target.toolbar = null;
    target.pb = null;
    target.webView = null;
  }
}
