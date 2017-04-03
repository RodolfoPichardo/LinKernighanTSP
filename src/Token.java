class Token {
    private int id;
    private Point point;
    public Token(int id, double x, double y) {
        this.id = id;
        this.point = new Point(x, y);
    }

    public int getId() {
        return this.id;
    }

    public Point getPoint() {
        return this.point;
    }

}
