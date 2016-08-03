package de.pkreutzer.modelfilter.example;

import de.pkreutzer.modelfilter.View;

public final class UserView {

  public static interface Username extends View {}
  public static interface Password extends View {}
  public static interface EMail extends View {}

  public static interface Public extends Username, EMail {}
  public static interface Credentials extends Username, Password {}

}
