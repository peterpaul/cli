package net.kleinhaneveld.cli.instantiator;

public interface Instantiator {
    <T> T instantiate(Class<T> aClass);
}
