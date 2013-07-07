package net.sf.anathema.hero.magic.display.tree;

import net.sf.anathema.character.main.presenter.magic.detail.ShowMagicDetailListener;
import net.sf.anathema.platform.tree.presenter.view.NodeInteractionListener;

public class ShowDetailInteractionListener implements NodeInteractionListener {
  private final ShowMagicDetailListener listener;

  public ShowDetailInteractionListener(ShowMagicDetailListener listener) {
    this.listener = listener;
  }

  @Override
  public void nodeSelected(String nodeId) {
    // nothing to do
  }

  @Override
  public void nodeDetailsDemanded(String nodeId) {
    listener.showDetail(nodeId);
  }
}