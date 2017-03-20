package bredesh.medico.Fragments.PictureItem;

public class PictureItem{
    private int id;
    private String exerciseName;
    private String numAlerts;
    private String crateBy;
    private String txtDate;

//    private String price;

    private byte[] image;

    public PictureItem(String exerciseName, String numAlerts,String crateBy, String txtDate, byte[] image, int id) {
        this.exerciseName = exerciseName;
        this.numAlerts = numAlerts;
        this.crateBy = crateBy;
        this.txtDate = txtDate;
        this.image = image;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getNumAlerts() {
        return numAlerts;
    }

    public void setPrice(String numAlerts) {
        this.numAlerts = numAlerts;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
