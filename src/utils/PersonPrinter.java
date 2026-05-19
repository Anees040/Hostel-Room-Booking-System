package utils;

import java.util.ArrayList;
import models.AbstractPerson;

/**
 * Demonstrates polymorphism (Lab 08) on AbstractPerson.
 * Works with both Student and Admin objects.
 */
public class PersonPrinter {

    private PersonPrinter() {
        // Utility class — no instances needed
    }

    /**
     * Polymorphic — works with Student or Admin via AbstractPerson reference.
     * Demonstrates runtime dispatch of getRole() and toString().
     *
     * @param person any AbstractPerson subtype
     */
    public static void printPersonInfo(AbstractPerson person) {
        System.out.println("Role: " + person.getRole());   // runtime dispatch
        System.out.println(person.toString());              // overridden toString
    }

    /**
     * Prints all persons in a heterogeneous list.
     * Uses wildcard generic parameter.
     *
     * @param persons list of AbstractPerson or any subtype
     */
    public static void printAllPersons(ArrayList<? extends AbstractPerson> persons) {
        persons.forEach(p -> printPersonInfo(p));
    }
}
