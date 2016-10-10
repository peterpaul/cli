package tk.kleinhaneveld.cli.fn;

public interface Registerable {
    void register(Runner listener);
    void unregister(Runner listener);
}
