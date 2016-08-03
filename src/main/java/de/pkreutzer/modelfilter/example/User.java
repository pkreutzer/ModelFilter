package de.pkreutzer.modelfilter.example;

import de.pkreutzer.modelfilter.InView;

public final class User {

  @InView(UserView.Username.class)
  public String username;

  @InView(UserView.Password.class)
  public String password;

  @InView(UserView.EMail.class)
  public String email;

  public User() {
    /* intentionally left blank */
  }

  public User(final String username, final String password, final String email) {
    this.username = username;
    this.password = password;
    this.email = email;
  }

  @Override
  public final String toString() {
    return String.format("{username: '%s', password: '%s', email: '%s'}",
                         this.username, this.password, this.email);
  }

}
