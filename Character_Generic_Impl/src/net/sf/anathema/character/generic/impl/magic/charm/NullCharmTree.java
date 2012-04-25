package net.sf.anathema.character.generic.impl.magic.charm;

import java.util.ArrayList;
import java.util.List;

import net.sf.anathema.character.generic.magic.ICharm;
import net.sf.anathema.character.generic.magic.charms.ICharmGroup;
import net.sf.anathema.character.generic.magic.charms.ICharmTree;

public class NullCharmTree implements ICharmTree {

  @Override
  public ICharm getCharmById(String id) {
    return null;
  }

  @Override
  public ICharm[] getAllCharms() {
    return new ICharm[0];
  }

  @Override
  public List<ICharm> getAllCharmsForGroup(String id) {
    return new ArrayList<ICharm>();
  }

  @Override
  public ICharmGroup[] getAllCharmGroups() {
    return new ICharmGroup[0];
  }

  @Override
  public boolean isLearnable(ICharm charm) {
    return false;
  }
}