public class Horse {
    private String name = null;
    private boolean isWinner = false;
    private int positionX, positionY;

    Horse(String name) {
        this.name = name;
        this.positionX = 0;
        this.positionY = 0;
    }

    public String getName() {
        return this.name;
    }

    public void setPositionX(int pos) {
        this.positionX = pos;
    }

    public int getPositionX() {
        return this.positionX;
    }

    public int getPositionY() {
        return this.positionY;
    }

    public void setPositionY(int pos) {
        this.positionY = pos;
    }

    public void awardWin(boolean winner) {
        this.isWinner = winner;
    }

    public boolean isWinner() {
        return isWinner;
    }

}
