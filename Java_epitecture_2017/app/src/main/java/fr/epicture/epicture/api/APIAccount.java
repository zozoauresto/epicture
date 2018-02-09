package fr.epicture.epicture.api;

public abstract class APIAccount {

    // ========================================================================
    // FIELDS
    // ========================================================================

    public String id;
    protected String realname;
    protected String username;

    // ========================================================================
    // CONSTRUCTOR
    // ========================================================================

    protected APIAccount() {
        id = "undefined";
        username = "undefined";
    }

    public APIAccount(String accountId, String username) throws InstantiationException {
        if (accountId == null || username == null)
            throw new InstantiationException();
        this.id = accountId;
        this.username = username;
    }

    // ========================================================================
    // METHODS
    // ========================================================================


    public abstract String getID();
    public abstract String getNSID();
    public abstract String getIconServer();
    public abstract String getFarm();

    public String getRealname() {
        return realname;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof APIAccount
                && ((APIAccount)o).id.equals(id)
                && ((APIAccount)o).username.equals(username);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

