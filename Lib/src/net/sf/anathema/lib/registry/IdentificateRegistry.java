package net.sf.anathema.lib.registry;

import net.sf.anathema.lib.util.IIdentificate;

public class IdentificateRegistry<E extends IIdentificate> extends CollectionRegistry<E> implements
    IIdentificateRegistry<E> {

  @Override
  public boolean idRegistered(String id) {
    for (E identificate : getAll()) {
      if (identificate.getId().equals(id)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public E getById(String id) {
    for (E identificate : getAll()) {
      if (identificate.getId().equals(id)) {
        return identificate;
      }
    }
    return null;
  }
}