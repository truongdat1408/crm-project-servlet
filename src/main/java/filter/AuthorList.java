package filter;

public enum AuthorList {
    ADMIN(1),
    LEADER(2),
    STAFF(3);

    private int value;
    AuthorList(int value) {
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
