package pkreutzer.modelfilter.example;

import static pkreutzer.modelfilter.ModelFilter.*;

public final class ModelFilterExample {

  public static final void main(final String[] args) {
    final User user = new User("my_username", "my_password", "my_email");

    // simple interface
    final User userPublic = filter(user, UserView.Public.class);

    // fluent interface
    final User userCredentials = buildFilter().
                                    forClass(User.class).
                                    useView(UserView.Credentials.class).
                                    applyTo(user);

    System.out.println("original:    " + user);
    System.out.println("public:      " + userPublic);
    System.out.println("credentials: " + userCredentials);
  }

}
