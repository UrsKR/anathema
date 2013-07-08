package net.sf.anathema.hero.intimacies.display;

import net.sf.anathema.character.main.framework.RegisteredCharacterView;
import net.sf.anathema.character.main.library.intvalue.IntValueDisplayFactoryPrototype;
import net.sf.anathema.character.main.type.ICharacterType;
import net.sf.anathema.character.main.view.SubViewFactory;
import net.sf.anathema.framework.value.IntegerViewFactory;

@RegisteredCharacterView(IntimaciesView.class)
public class IntimaciesViewFactory implements SubViewFactory {
  //TODO (Swing->FX) Needs character type
  @SuppressWarnings("unchecked")
  @Override
  public <T> T create(ICharacterType type) {
    IntegerViewFactory factory = IntValueDisplayFactoryPrototype.createWithMarkerForCharacterType(type);
    return (T) new IntimaciesViewImpl(factory);
  }
}